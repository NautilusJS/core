package com.mindlin.jsast.impl.parser;

import static com.mindlin.jsast.impl.parser.JSParserTest.assertIdentifier;
import static com.mindlin.jsast.impl.parser.JSParserTest.assertKind;
import static com.mindlin.jsast.impl.parser.JSParserTest.assertModifiers;
import static com.mindlin.jsast.impl.parser.JSParserTest.assertSingleElementKind;
import static com.mindlin.jsast.impl.parser.JSParserTest.assertSpecialType;
import static com.mindlin.jsast.impl.parser.JSParserTest.parseStatement;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.List;

import org.junit.Test;

import com.mindlin.jsast.tree.MethodSignatureTree;
import com.mindlin.jsast.tree.Modifiers;
import com.mindlin.jsast.tree.PropertySignatureTree;
import com.mindlin.jsast.tree.SignatureDeclarationTree.CallSignatureTree;
import com.mindlin.jsast.tree.SignatureDeclarationTree.ConstructSignatureTree;
import com.mindlin.jsast.tree.Tree.Kind;
import com.mindlin.jsast.tree.type.InterfaceDeclarationTree;
import com.mindlin.jsast.tree.type.SpecialTypeTree.SpecialType;
import com.mindlin.jsast.tree.type.TypeElementTree;

public class InterfaceDeclarationTest {
	
	@Test
	public void testEmptyInterfaceDeclaration() {
		InterfaceDeclarationTree iface = parseStatement("interface Foo{}", Kind.INTERFACE_DECLARATION);
		assertIdentifier("Foo", iface.getName());
	}
	
	@Test
	public void testSingleMember() {
		InterfaceDeclarationTree iface = parseStatement("interface HasName {name: string;}", Kind.INTERFACE_DECLARATION);
		assertIdentifier("HasName", iface.getName());
		assertEquals(0, iface.getHeritage().size());
		
		PropertySignatureTree prop0 = assertSingleElementKind(Kind.PROPERTY_SIGNATURE, iface.getDeclaredMembers());
		assertModifiers(prop0.getModifiers(), Modifiers.NONE);
		assertIdentifier("name", prop0.getName());
		assertSpecialType(SpecialType.STRING, prop0.getType());
	}
	
	@Test
	public void testMultipleMembers() {
		InterfaceDeclarationTree iface = parseStatement("interface TwoPartName {fName: string; lName: string;}", Kind.INTERFACE_DECLARATION);
		assertIdentifier("TwoPartName", iface.getName());
		assertEquals(0, iface.getHeritage().size());
		List<? extends TypeElementTree> members = iface.getDeclaredMembers();
		assertEquals(2, members.size());
		
		
		PropertySignatureTree prop0 = assertKind(Kind.PROPERTY_SIGNATURE, members.get(0));
		assertModifiers(prop0.getModifiers(), Modifiers.NONE);
		assertIdentifier("fName", prop0.getName());
		assertSpecialType(SpecialType.STRING, prop0.getType());
		
		PropertySignatureTree prop1 = assertKind(Kind.PROPERTY_SIGNATURE, members.get(1));
		assertModifiers(prop1.getModifiers(), Modifiers.NONE);
		assertIdentifier("lName", prop1.getName());
		assertSpecialType(SpecialType.STRING, prop1.getType());
	}
	
	@Test
	public void testSimpleMethodSignature() {
		InterfaceDeclarationTree iface = parseStatement("interface HasMethod { toString(): string; }", Kind.INTERFACE_DECLARATION);
		assertIdentifier("HasMethod", iface.getName());
		assertEquals(0, iface.getHeritage().size());
		List<? extends TypeElementTree> members = iface.getDeclaredMembers();
		assertEquals(1, members.size());
		
		//TODO: we need more method signature tests
		
		MethodSignatureTree prop0 = assertKind(Kind.METHOD_SIGNATURE, members.get(0));
		assertModifiers(prop0.getModifiers(), Modifiers.NONE);
		assertEquals(0, prop0.getTypeParameters().size());
		assertIdentifier("toString", prop0.getName());
		assertEquals(0, prop0.getParameters().size());
		assertSpecialType(SpecialType.STRING, prop0.getReturnType());
	}
	
	@Test
	public void testConstructorSignature() {
		InterfaceDeclarationTree iface = parseStatement("interface HasCtor { new (): string; }", Kind.INTERFACE_DECLARATION);
		assertIdentifier("HasCtor", iface.getName());
		assertEquals(0, iface.getHeritage().size());
		List<? extends TypeElementTree> members = iface.getDeclaredMembers();
		assertEquals(1, members.size());
		
		ConstructSignatureTree prop0 = assertKind(Kind.CONSTRUCT_SIGNATURE, members.get(0));
		assertEquals(0, prop0.getTypeParameters().size());
		assertNull(prop0.getName());
		assertEquals(0, prop0.getParameters().size());
		assertSpecialType(SpecialType.STRING, prop0.getReturnType());
	}
	
	@Test
	public void testCallSignature() {
		InterfaceDeclarationTree iface = parseStatement("interface HasCallSignature { (): void; }", Kind.INTERFACE_DECLARATION);
		assertIdentifier("HasCallSignature", iface.getName());
		assertEquals(0, iface.getHeritage().size());
		List<? extends TypeElementTree> members = iface.getDeclaredMembers();
		assertEquals(1, members.size());
		
		
		CallSignatureTree prop0 = assertKind(Kind.CALL_SIGNATURE, members.get(0));
		assertEquals(0, prop0.getTypeParameters().size());
		assertNull(prop0.getName());
		assertEquals(0, prop0.getParameters().size());
		assertSpecialType(SpecialType.VOID, prop0.getReturnType());
	}
	
	@Test
	public void testReadonlyProperty() {
		InterfaceDeclarationTree iface = parseStatement("interface Foo { readonly bar: string; }", Kind.INTERFACE_DECLARATION);
		assertIdentifier("Foo", iface.getName());
		assertEquals(0, iface.getHeritage().size());
		List<? extends TypeElementTree> members = iface.getDeclaredMembers();
		assertEquals(1, members.size());
		

		PropertySignatureTree prop0 = assertKind(Kind.PROPERTY_SIGNATURE, members.get(0));
		assertModifiers(prop0.getModifiers(), Modifiers.READONLY);
		assertIdentifier("bar", prop0.getName());
		assertSpecialType(SpecialType.STRING, prop0.getType());
	}
	
	// Some extra punctuation checks
	@Test
	public void testCommaSeparated() {
		//TODO: finish
	}
	
	@Test
	public void testNoEndPunctuation() {
		//TODO: finish
	}
	
	//TODO: Heritage clauses
}
