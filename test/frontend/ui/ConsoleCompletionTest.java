package frontend.ui;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.junit.Test;

public class ConsoleCompletionTest {
	@Test
	public void getCompletion_noConsoleCompletion() {
		ConsoleCompletion completion = new ConsoleCompletion();
		completion.setHistory("", Arrays.asList(
			"dc",
			"da",
			"db"
		));
		assertEquals("a", completion.getCompletion("a"));
		assertEquals("a", completion.getCompletion("a"));
	}
	
	@Test
	public void getCompletion() {
		ConsoleCompletion completion = new ConsoleCompletion();
		completion.setHistory("", Arrays.asList(
			"ac",
			"aa",
			"bb"
		));
		assertEquals("aa", completion.getCompletion("a"));
		assertEquals("ac", completion.getCompletion("aa"));
		assertEquals("a", completion.getCompletion("ac"));
		assertEquals("aa", completion.getCompletion("a"));
	}
	
	@Test
	public void getCompletion_ignoreCase() {
		ConsoleCompletion completion = new ConsoleCompletion();
		completion.setHistory("", Arrays.asList(
			"ac",
			"Aa",
			"bb"
		));
		assertEquals("Aa", completion.getCompletion("a"));
		assertEquals("ac", completion.getCompletion("Aa"));
	}
	
	@Test
	public void getCompletion_historyWords() {
		ConsoleCompletion completion = new ConsoleCompletion();
		completion.setHistory("", Arrays.asList(
			"ab ac",
			"bd be",
			"aab bbb"
		));
		assertEquals("aab", completion.getCompletion("a"));
		assertEquals("ac", completion.getCompletion("aab"));
		assertEquals("ab", completion.getCompletion("ac"));
		assertEquals("a", completion.getCompletion("ab"));
	}
	
	@Test
	public void getCompletion_completionWords() {
		ConsoleCompletion completion = new ConsoleCompletion();
		completion.setHistory("", Arrays.asList(
			"ab ac",
			"bd be",
			"aab bbb"
		));
		assertEquals("dd aab", completion.getCompletion("dd a"));
		assertEquals("dd ac", completion.getCompletion("dd aab"));
		assertEquals("dd ab", completion.getCompletion("dd ac"));
		assertEquals("dd a", completion.getCompletion("dd ab"));
	}
	
	@Test
	public void getCompletion_prefix() {
		ConsoleCompletion completion = new ConsoleCompletion();
		completion.setHistory("ad aa", Arrays.asList(
			"ac",
			"aa",
			"bb"
		));
		assertEquals("aa", completion.getCompletion("a"));
		assertEquals("ac", completion.getCompletion("aa"));
		assertEquals("ad", completion.getCompletion("ac"));
		assertEquals("a", completion.getCompletion("ad"));
		assertEquals("aa", completion.getCompletion("a"));
	}
}