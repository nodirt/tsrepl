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
    final Evil mEvil;
    final Thread mWorkerThread;
    final Object mEvilResponse = new Object();

    class Evil implements Runnable {
        String mResult;
        String mExpression;
        String mUserName;

        public String getResult() {
            return mResult;
        }

        public void setExpression(String expression) {
            mExpression = expression;
        }

        public void setUserName(String userName) {
            mUserName = userName;
        }

        String eval() {
            Context cx = Context.enter();
            try {
                Scriptable scope = getScope(mUserName);

                try {
                    Object result = cx.evaluateString(scope, mExpression,
                            mUserName, 1, null);
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

        @Override
        public void run() {
            while (true) {
                try {
                    this.wait();
                } catch (InterruptedException ex) {
                    break;
                }
                
                mResult = eval();
                mEvilResponse.notifyAll();
            }
        }
    }

    public Repl() {
        InputStream resource = getClass().getResourceAsStream(
                scopeInitScriptResourceName);
        mEvil = new Evil();
        mInitScript = readInitScript(resource);
        mWorkerThread = new Thread(mEvil);
        mWorkerThread.start();
    }

    private String readInitScript(InputStream resource) {
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

    public String eval(final String userName, final String text) {
        synchronized (mEvil) {
            mEvil.setUserName(userName);
            mEvil.setExpression(text);
            mEvil.notifyAll();
            
            try {
                DateTime deadline = DateTime.now().plusSeconds(1);
                while (true) {
                    this.mEvilResponse.wait(10);
                    if ()
                }
                while ( workerThread.is()
                        && DateTime.now().compareTo(deadline) <= 0) {
                    Thread.sleep(10);
                }
    
                if (workerThread.isAlive()) {
                    workerThread.stop();
                    workerThread.destroy();
                    return "I wish your algorithm was faster";
                }
    
                return eval.getResult();
            } catch (Exception ex) {
                return "You are being mean";
            }
        }
    }
}
