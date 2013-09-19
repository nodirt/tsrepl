package tsrepl;

import java.io.*;
import java.util.*;

import org.joda.time.Duration;
import org.joda.time.ReadableDuration;

public class ReplConfiguration {
    static final String filename = "repl.properties";

    int mCacheLifetime = 24;
    ReadableDuration mTimeout = Duration.standardSeconds(1);
    String mBreakAttemptMessage = "Are you trying to break me?";
    String mTimeoutMessage = "Too slow!";

    public int cacheLifetime() {
        return mCacheLifetime;
    }
    public ReadableDuration timeout() {
        return mTimeout;
    }
    public String breakAttemptMessage() {
        return mBreakAttemptMessage;
    }
    public String timeoutMessage() {
        return mTimeoutMessage;
    }

    public void load() throws IOException { 
        File propFile = new File(filename);
        if (!propFile.exists()) {
            return;
        }

        Properties props = new Properties();
        FileReader reader = new FileReader(propFile);
        try {
            props.load(reader);
        } finally {
            reader.close();
        }

        String strCacheLifetime = props.getProperty("cacheLifetime");
        if (strCacheLifetime != null) {
            mCacheLifetime = Integer.parseInt(strCacheLifetime);
        }
        
        String timeout = props.getProperty("timeout");
        if (timeout != null) {
            mTimeout = Duration.millis(Integer.parseInt(timeout));
        }
        
        mBreakAttemptMessage = props.getProperty("breakAttemptMessage", mBreakAttemptMessage);
        mTimeoutMessage = props.getProperty("timeoutMessage", mTimeoutMessage);
    }
}
