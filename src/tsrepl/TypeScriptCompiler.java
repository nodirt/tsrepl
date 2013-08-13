package tsrepl;

import java.io.*;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

public class TypeScriptCompiler {

	final Scriptable mScope;
	final String initialScript = "var compiler = new TypeScript.TypeScriptCompiler()";

	public TypeScriptCompiler() throws IOException {
		Context cx = Context.enter();
		try {
			mScope = cx.initStandardObjects();
			readCompilerCode(cx);
			cx.evaluateString(mScope, initialScript, "initialScript", 1, null);
		} finally {
			Context.exit();
		}
	}

	void readCompilerCode(Context cx) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader("lib/typescript.js"));
		try {
			cx.evaluateReader(mScope, br, "TypeScript", 1, null);
		} finally {
			br.close();
		}
	}

	public String compile(String typeScript) {
		Context cx = Context.enter();
		try {
		} finally {
			Context.exit();
		}
	}
}
