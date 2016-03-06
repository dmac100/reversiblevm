package frontend.compiler;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.google.common.base.Optional;

public class BracketMatcherTest {
	private BracketMatcher matcher = new BracketMatcher();
	
	@Test
	public void forwardParen() {
		assertEquals(Optional.of(10), matcher.getMatchingParen(".(.({[]}).)", 1));
	}
	
	@Test
	public void forwardBrace() {
		assertEquals(Optional.of(10), matcher.getMatchingParen(".{.({[]}).}", 1));
	}
	
	@Test
	public void forwardBracket() {
		assertEquals(Optional.of(10), matcher.getMatchingParen(".{.({[]}).}", 1));
	}
	
	@Test
	public void forwardAngle() {
		assertEquals(Optional.of(10), matcher.getMatchingParen(".<.({[]}).>", 1));
	}
	
	@Test
	public void backwardParen() {
		assertEquals(Optional.of(1), matcher.getMatchingParen(".(.({[]}).)", 10));
	}
	
	@Test
	public void backwardBrace() {
		assertEquals(Optional.of(1), matcher.getMatchingParen(".{.({[]}).}", 10));
	}
	
	@Test
	public void backwardBracket() {
		assertEquals(Optional.of(1), matcher.getMatchingParen(".{.({[]}).}", 10));
	}
	
	@Test
	public void backwardAngle() {
		assertEquals(Optional.of(1), matcher.getMatchingParen(".<.({[]}).>", 10));
	}
	
	@Test
	public void noStarting() {
		assertEquals(Optional.absent(), matcher.getMatchingParen(".{.({[]}).}", 0));
	}
	
	@Test
	public void noEnding() {
		assertEquals(Optional.absent(), matcher.getMatchingParen(".{.({[]}).)", 1));
	}
}
