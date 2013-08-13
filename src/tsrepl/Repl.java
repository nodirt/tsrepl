package tsrepl;

import java.util.*;

import org.mozilla.javascript.*;

public class Repl {
	
	final Map<String, Scriptable> mScopes = new HashMap<String, Scriptable>();
	
	Scriptable getScope(Context context, String userName) {
		Scriptable scope = mScopes.get(userName);
		if (scope == null) {
			scope = context.initStandardObjects();
			mScopes.put(userName, scope);
		}
		
		return scope;
	}
	
	public String eval(String userName, String text) {
        // Creates and enters a Context. The Context stores information
        // about the execution environment of a script.
		Context cx = Context.enter();
        try {
    		Scriptable scope = getScope(cx, userName);

            // Initialize the standard objects (Object, Function, etc.)
            // This must be done before scripts can be executed. Returns
            // a scope object that we use in later calls.
            Object result = cx.evaluateString(scope, text, userName, 1, null);

            return Context.toString(result);
        } finally {
            // Exit from the context.
            Context.exit();
        }
	}
}
