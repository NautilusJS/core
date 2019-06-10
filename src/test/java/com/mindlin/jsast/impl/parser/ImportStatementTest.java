package com.mindlin.jsast.impl.parser;

import static com.mindlin.jsast.impl.parser.JSParserTest.assertExceptionalStatement;
import static com.mindlin.jsast.impl.parser.JSParserTest.assertIdentifier;
import static com.mindlin.jsast.impl.parser.JSParserTest.assertLiteral;
import static com.mindlin.jsast.impl.parser.JSParserTest.parseStatement;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import com.mindlin.jsast.tree.ImportDeclarationTree;
import com.mindlin.jsast.tree.ImportSpecifierTree;
import com.mindlin.jsast.tree.Tree.Kind;
public class ImportStatementTest {

	/**
	 * Pretty exhaustive tests for {@code import} statement parsing.
	 * I honestly can't think of any (non-trivial) test cases that could be added.
	 */
	@Test
	public void testThingsThatShouldntWork() {
		final String msg = "Failed to throw error on illegal import statement";
		//Things that *shouldn't* work
		assertExceptionalStatement("import ;", msg);
		assertExceptionalStatement("import foo;", msg);
		assertExceptionalStatement("import from 'foo.js';", msg);
		assertExceptionalStatement("import def 'foo.js';", msg);
		assertExceptionalStatement("import * 'foo.js';", msg);
		assertExceptionalStatement("import * as def 'foo.js';", msg);
		assertExceptionalStatement("import * from 'foo.js';", msg);
		assertExceptionalStatement("import {} from 'foo.js';", msg);
		assertExceptionalStatement("import {*} from 'foo.js';", msg);
		assertExceptionalStatement("import {* as def} from 'foo.js';", msg);
		assertExceptionalStatement("import {def} 'foo.js';", msg);
		assertExceptionalStatement("import {def, *} from 'foo.js';", msg);
		assertExceptionalStatement("import {def ghi} from 'foo.js';", msg);
		assertExceptionalStatement("import * as bar;", msg);
		assertExceptionalStatement("import * as bar, def;", msg);
		assertExceptionalStatement("import * as a, {foo as bar} from 'foo.js';", msg);
		assertExceptionalStatement("import {foo as bar from 'foo.js';", msg);
		assertExceptionalStatement("import {'hello' as world} from 'foo.js';", msg);
		assertExceptionalStatement("import {hello as 'world'} from 'foo.js';", msg);
		assertExceptionalStatement("import default from 'foo.js';", msg);
	}
	
	//Things that *should* work
	//Examples taken from developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Statements/import

	@Test
	public void testImportRaw() {
		ImportDeclarationTree impt = parseStatement("import 'module-name';", Kind.IMPORT);
		assertLiteral("module-name", impt.getSource());
		List<? extends ImportSpecifierTree> specifiers = impt.getSpecifiers();
		assertEquals(0, specifiers.size());
	}
	
	@Test
	public void testImportDefaultMember() {
		ImportDeclarationTree impt = parseStatement("import defaultMember from 'module-name';", Kind.IMPORT);
		assertLiteral("module-name", impt.getSource());
		List<? extends ImportSpecifierTree> specifiers = impt.getSpecifiers();
		assertEquals(1, specifiers.size());
		
		ImportSpecifierTree specifier = specifiers.get(0);
		assertIdentifier("defaultMember", specifier.getImported());
		assertIdentifier("defaultMember", specifier.getAlias());
		assertEquals(specifier.getImported(), specifier.getAlias());
		assertTrue(specifier.isDefault());
	}
	
	@Test
	public void testImportWildcard() {
		ImportDeclarationTree impt = parseStatement("import * as name from 'module-name';", Kind.IMPORT);
		assertLiteral("module-name", impt.getSource());
		List<? extends ImportSpecifierTree> specifiers = impt.getSpecifiers();
		assertEquals(1, specifiers.size());
		
		ImportSpecifierTree specifier = specifiers.get(0);
		assertIdentifier("*", specifier.getImported());
		assertIdentifier("name", specifier.getAlias());
		assertFalse(specifier.isDefault());
	}
	
	@Test
	public void testImportSingleNamed() {
		ImportDeclarationTree impt = parseStatement("import { member } from 'module-name';", Kind.IMPORT);
		assertLiteral("module-name", impt.getSource());
		List<? extends ImportSpecifierTree> specifiers = impt.getSpecifiers();
		assertEquals(1, specifiers.size());
		
		ImportSpecifierTree specifier = specifiers.get(0);
		assertIdentifier("member", specifier.getImported());
		assertIdentifier("member", specifier.getAlias());
		assertEquals(specifier.getImported(), specifier.getAlias());
		assertFalse(specifier.isDefault());
	}
	
	@Test
	public void testImportSingleAliased() {
		ImportDeclarationTree impt = parseStatement("import { member as alias } from 'module-name';", Kind.IMPORT);
		assertLiteral("module-name", impt.getSource());
		List<? extends ImportSpecifierTree> specifiers = impt.getSpecifiers();
		assertEquals(1, specifiers.size());
		
		ImportSpecifierTree specifier0 = specifiers.get(0);
		assertIdentifier("member", specifier0.getImported());
		assertIdentifier("alias", specifier0.getAlias());
		assertFalse(specifier0.isDefault());
	}
	
	@Test
	public void testImportMultipleNamed() {
		ImportDeclarationTree impt = parseStatement("import { member1 , member2 } from 'module-name';", Kind.IMPORT);
		assertLiteral("module-name", impt.getSource());
		List<? extends ImportSpecifierTree> specifiers = impt.getSpecifiers();
		assertEquals(2, specifiers.size());
		
		ImportSpecifierTree specifier0 = specifiers.get(0);
		assertIdentifier("member1", specifier0.getImported());
		assertIdentifier("member1", specifier0.getAlias());
		assertEquals(specifier0.getImported(), specifier0.getAlias());
		assertFalse(specifier0.isDefault());
		
		ImportSpecifierTree specifier1 = specifiers.get(1);
		assertIdentifier("member2", specifier1.getImported());
		assertIdentifier("member2", specifier1.getAlias());
		assertEquals(specifier1.getImported(), specifier1.getAlias());
		assertFalse(specifier1.isDefault());
	}
	
	@Test
	public void testImportMultipleMixed() {
		ImportDeclarationTree impt = parseStatement("import { member1 , member2 as alias2 } from 'module-name';", Kind.IMPORT);
		assertLiteral("module-name", impt.getSource());
		List<? extends ImportSpecifierTree> specifiers = impt.getSpecifiers();
		assertEquals(2, specifiers.size());
		
		ImportSpecifierTree specifier0 = specifiers.get(0);
		assertIdentifier("member1", specifier0.getImported());
		assertIdentifier("member1", specifier0.getAlias());
		assertEquals(specifier0.getImported(), specifier0.getAlias());
		assertFalse(specifier0.isDefault());
		
		ImportSpecifierTree specifier1 = specifiers.get(1);
		assertIdentifier("member2", specifier1.getImported());
		assertIdentifier("alias2", specifier1.getAlias());
		assertFalse(specifier1.isDefault());
	}
	
	@Test
	public void testImportDefaultAndSingleNamed() {
		ImportDeclarationTree impt = parseStatement("import defaultMember, { member } from 'module-name';", Kind.IMPORT);
		assertLiteral("module-name", impt.getSource());
		List<? extends ImportSpecifierTree> specifiers = impt.getSpecifiers();
		assertEquals(2, specifiers.size());
		
		ImportSpecifierTree specifier0 = specifiers.get(0);
		assertIdentifier("defaultMember", specifier0.getImported());
		assertIdentifier("defaultMember", specifier0.getAlias());
		assertEquals(specifier0.getImported(), specifier0.getAlias());
		assertTrue(specifier0.isDefault());
		
		ImportSpecifierTree specifier1 = specifiers.get(1);
		assertIdentifier("member", specifier1.getImported());
		assertIdentifier("member", specifier1.getAlias());
		assertEquals(specifier1.getImported(), specifier1.getAlias());
		assertFalse(specifier1.isDefault());
	}
	
	@Test
	public void testImportDefaultAndWildcardAliased() {
		ImportDeclarationTree impt = parseStatement("import defaultMember, * as name from 'module-name';", Kind.IMPORT);
		assertLiteral("module-name", impt.getSource());
		List<? extends ImportSpecifierTree> specifiers = impt.getSpecifiers();
		assertEquals(2, specifiers.size());
		
		ImportSpecifierTree specifier0 = specifiers.get(0);
		assertIdentifier("defaultMember", specifier0.getImported());
		assertIdentifier("defaultMember", specifier0.getAlias());
		assertEquals(specifier0.getImported(), specifier0.getAlias());
		assertTrue(specifier0.isDefault());
		
		ImportSpecifierTree specifier1 = specifiers.get(1);
		assertIdentifier("*", specifier1.getImported());
		assertIdentifier("name", specifier1.getAlias());
		assertFalse(specifier1.isDefault());
	}
}
