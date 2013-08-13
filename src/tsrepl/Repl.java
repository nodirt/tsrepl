package tsrepl;

import java.io.*;
import java.util.*;

import org.joda.time.*;
import org.mozilla.javascript.*;

public class Repl {
	
	class Entry {
		final Scriptable mScope;
		DateTime mLastAccessTime;
		
		public Entry(Scriptable scope) {
			if (scope == null) {
				throw new IllegalArgumentException();
			}
			mScope = scope;
			mLastAccessTime = DateTime.now();
		}

		public DateTime getLastAccessTime() {
			return mLastAccessTime;
		}
		
		public Scriptable getScope() {
			mLastAccessTime = DateTime.now();
			return mScope;
		}
	}
	
	static final String scopeInitScriptResourceName = "/js/scopeInit.js";
	final String mInitScript;
	final Map<String, Entry> mScopes = new HashMap<String, Entry>();

	public Repl() {
		InputStream resource = getClass().getResourceAsStream(scopeInitScriptResourceName);
		mInitScript = new Scanner(resource).useDelimiter("\\A").next();
	}
	
	void cleanup(int cacheLifetimeInHours) {
		List<String> toRemove = new ArrayList<String>();
		DateTime minDate = DateTime.now().minusHours(cacheLifetimeInHours);
		for (Map.Entry<String, Entry> e : mScopes.entrySet()) {
			if (e.getValue().getLastAccessTime().compareTo(minDate) < 0) {
				toRemove.add(e.getKey());
			}
		}
		
		for (String userName : toRemove) {
			mScopes.remove(userName);
		}
	}
	
	Scriptable createScope() {
		Context ctx = Context.getCurrentContext();
		Scriptable scope = ctx.initStandardObjects();
		ctx.evaluateString(scope, mInitScript, "init", 1, null);
		return scope;
	}
	
	Scriptable getScope(String userName) {
		Entry entry = mScopes.get(userName);
		if (entry == null) {
			entry = new Entry(createScope());
			mScopes.put(userName, entry);
		}
		
		return entry.getScope();
	}
	
	public String eval(String userName, String text) {
		Context cx = Context.enter();
        try {
    		Scriptable scope = getScope(userName);

    		try {
	    		Object result = cx.evaluateString(scope, text, userName, 1, null);
	            if (result == Undefined.instance) {
	            	return null;
	            } else {
	            	return Context.toString(result);
	            }
    		} catch (Exception ex) {
    			return ex.getMessage();
    		}
        } finally {
            // Exit from the context.
            Context.exit();
        }
	}
}
