package com.mindlin.jsast.impl.parser;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;

import org.junit.Ignore;
import org.junit.Test;

import com.mindlin.jsast.impl.lexer.JSLexer;
import com.mindlin.jsast.impl.parser.JSParser.Context;
import com.mindlin.jsast.impl.util.CharacterArrayStream;
import com.mindlin.jsast.impl.util.CharacterStream;
import com.mindlin.jsast.tree.CompilationUnitTree;
import com.mindlin.jsast.tree.Tree;

public class JQueryTest {
	@Test
	public void test() throws IOException {
		String jQuery;
		try (BufferedReader br = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/jquery.js")))) {
			StringBuffer sb = new StringBuffer();
			char[] tmp = new char[4096];
			int len;
			while ((len = br.read(tmp)) > -1)
				sb.append(tmp, 0, len);
			jQuery = sb.toString();
		}
		JSLexer lexer = new JSLexer(jQuery);
		JSParser parser = new JSParser();
		CompilationUnitTree result = parser.apply("jQuery", lexer);
		System.out.println(result);
	}
	
}
