package tsrepl;

import java.io.*;
import java.util.*;

public class ReplConfiguration {
	static final String filename = "repl.properties";
	static final String cacheLifetimeKey = "cacheLifetime";  
	
	int mCacheLifetime = 24;
	
	public int getCacheLifetime() {
		return mCacheLifetime;
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
		
		String strCacheLifetime = props.getProperty(cacheLifetimeKey);
		if (strCacheLifetime != null) {
			mCacheLifetime = Integer.parseInt(strCacheLifetime);
		}
	}
}
