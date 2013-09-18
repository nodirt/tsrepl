package tsrepl;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.Callable;

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
    final String mInitScript = readInitScript();
    final ConcurrentMap<String, Entry> mScopes = new ConcurrentHashMap<String, Entry>();
    final ExecutorService mEvilExecutor = Executors.newSingleThreadExecutor();

    private String readInitScript() {
        InputStream resource = getClass().getResourceAsStream(
                scopeInitScriptResourceName);
        Scanner scanner = new Scanner(resource);
        try {
            return scanner.useDelimiter("\\A").next();
        } finally {
            scanner.close();
        }
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

    String evalRaw(String userName, String expression) {
        Context cx = Context.enter();
        try {
            Scriptable scope = getScope(userName);

            try {
                Object result = cx.evaluateString(scope, expression,
                        userName, 1, null);
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
    
    public String eval(final String userName, final String expression) {
        try {
            Future<String> evaluation = mEvilExecutor.submit(new Callable<String>() {
                public String call() {
                    return evalRaw(userName, expression);
                }
            });
            
            DateTime deadline = DateTime.now().plusSeconds(1);
            while (!evaluation.isDone() && DateTime.now().compareTo(deadline) <= 0) {
                Thread.sleep(10);
            }

            if (evaluation.isDone()) {
                return evaluation.get();
            } else {
                evaluation.cancel(true);
                return "I wish your algorithm was faster";
            }
        } catch (Exception ex) {
            return "You are being mean: " + ex.getMessage();
        }
    }
}
