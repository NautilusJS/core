package js_ast;

import static org.junit.Assert.*;

import org.junit.Assert;
import org.junit.Test;

import com.mindlin.jsast.impl.util.CharacterArrayStream;
import com.mindlin.jsast.impl.util.CharacterStream;
import com.mindlin.jsast.impl.util.Characters;

public class CharacterStreamTest {
	@Test
	public void testNextPrev() {
		CharacterStream chars = new CharacterArrayStream("123456789");
		assertEquals('1', chars.next());
		assertEquals(0, chars.position());
		
		assertEquals('2', chars.next());
		assertEquals(1, chars.position());
		
		assertEquals('1', chars.prev());
		assertEquals(0, chars.position());
		
		assertEquals('2', chars.next(), '2');
		assertEquals(1, chars.position());
	}
	
	@Test
	public void testNextCurrent() {
		CharacterStream chars = new CharacterArrayStream("123456789");
		assertEquals('1', chars.next());
		assertEquals(0, chars.position());
		assertEquals('1', chars.current());
	}
	
	@Test
	public void testSkipAndGet() {
		CharacterArrayStream chars = new CharacterArrayStream("abcde");
		assertEquals('a', chars.next());
		Assert.assertNotNull(chars.skip(1));
		assertEquals('b', chars.current());
		assertEquals('c', chars.next());
	}
	
	@Test
	public void testEOF() {
		CharacterArrayStream chars = new CharacterArrayStream("x");
		assertTrue(chars.hasNext());
		chars.next();
		assertFalse(chars.hasNext());
	}
	
	@Test
	public void testCopy() {
		CharacterArrayStream chars = new CharacterArrayStream("123ABC456");
		assertEquals("123", chars.copyNext(3));
		assertEquals("ABC456", chars.copyNext(6));
		assertFalse(chars.hasNext());
		assertEquals("123ABC", chars.copy(0, 6));
		assertFalse(chars.hasNext());
	}
	@Test
	public void testWhitespace() {
		assertTrue(Characters.isJsWhitespace(' '));
	}
	@Test
	public void testSkipComments() {
		CharacterArrayStream chars = new CharacterArrayStream("0//slc\n1");
		assertEquals('0', chars.skipComments().next());
		assertEquals('1', chars.skipComments().next());
		//assertEquals('2', chars.skipComments().next());
		//assertEquals('3', chars.skipComments().next());
	}
	@Test
	public void testSkipWhitespace() {
		CharacterArrayStream chars = new CharacterArrayStream("1  23  4\r5");
		assertEquals('1', chars.skipWhitespace().next());
		assertEquals('2', chars.skipWhitespace().next());
		assertEquals('3', chars.skipWhitespace().next());
		assertEquals('4', chars.skipWhitespace().next());
		assertEquals('5', chars.skipWhitespace().next());
	}
}
