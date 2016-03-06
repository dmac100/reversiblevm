package frontend.ui;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.eclipse.swt.custom.StyledText;
import org.junit.Test;

import frontend.util.FakeUtil;

class FakeStyledText {
	StringBuilder text;
	int caretOffset;
	
	FakeStyledText(String text, int caretOffset) {
		this.text = new StringBuilder(text);
		setCaretOffset(caretOffset);
	}
	
	int getCharCount() {
		return text.length();
	}
	
	int getCaretOffset() {
		return caretOffset;
	}
	
	void setCaretOffset(int caretOffset) {
		if(caretOffset < 0 || caretOffset > text.length()) throw new IndexOutOfBoundsException();
		this.caretOffset = caretOffset;
	}
	
	String getText(int start, int end) {
		if(start < 0 || start >= text.length()) throw new IndexOutOfBoundsException();
		if(end < 0 || end >= text.length()) throw new IndexOutOfBoundsException();
		return text.substring(start, end + 1);
	}
	
	void setText(String text) {
		this.text = new StringBuilder(text);
		caretOffset = 0;
	}
	
	void replaceTextRange(int start, int length, String s) {
		if(start + length > text.length()) throw new IndexOutOfBoundsException();
		text.replace(start, start + length, s);
	}
	
	String getText() {
		return text.toString();
	}
}

public class CompletionTest {
	/**
	 * Creates a fake StyledText where textWithCaret contains the text along
	 * with the caret position represented as a '|' character.
	 */
	private static StyledText fakeStyledText(final String textWithCaret) {
		int caret = textWithCaret.indexOf('|');
		String text = textWithCaret.replaceAll("\\|", "");
		return FakeUtil.createDelegate(StyledText.class, new FakeStyledText(text, caret));
	}

	/**
	 * Asserts that the text and caret match textWithCaret. textWithCaret contains
	 * a string with a '|' character representing the expected caret position.
	 */
	private static void assertTextAndCaret(StyledText styledText, String textWithCaret) {
		int caret = textWithCaret.indexOf('|');
		String text = textWithCaret.replaceAll("\\|", "");
		assertEquals(text, styledText.getText());
		assertEquals(caret, styledText.getCaretOffset());
	}
	
	@Test
	public void completeEmpty() {
		StyledText styledText = fakeStyledText("|");
		
		new Completion(styledText).complete();
	}
	
	@Test
	public void completeAtHome() {
		StyledText styledText = fakeStyledText("|a");
		
		new Completion(styledText).complete();
		
		assertTextAndCaret(styledText, "|a");
	}
	
	@Test
	public void completeAtEnd() {
		StyledText styledText = fakeStyledText("a|");
		
		new Completion(styledText).complete();
		
		assertTextAndCaret(styledText, "a|");
	}
	
	@Test
	public void completePreviousWord() {
		StyledText styledText = fakeStyledText("aaa abb a|");
		
		new Completion(styledText).complete();
		
		assertTextAndCaret(styledText, "aaa abb abb|");
	}
	
	@Test
	public void noWordToComplete() {
		StyledText styledText = fakeStyledText("aaa |");
		
		new Completion(styledText).complete();
		
		assertTextAndCaret(styledText, "aaa |");
	}
	
	@Test
	public void noCompletionsFound() {
		StyledText styledText = fakeStyledText("bbb| aaa");
		
		new Completion(styledText).complete();
		
		assertTextAndCaret(styledText, "bbb| aaa");
	}
	
	@Test
	public void completionsWrap() {
		StyledText styledText = fakeStyledText("aa| aab aaa");
		
		Completion completion = new Completion(styledText);
		
		completion.complete();
		assertTextAndCaret(styledText, "aaa| aab aaa");
		
		completion.complete();
		assertTextAndCaret(styledText, "aab| aab aaa");
	}
	
	@Test
	public void wrapBackToOriginal() {
		StyledText styledText = fakeStyledText("a| ab");
		
		Completion completion = new Completion(styledText);
		
		completion.complete();
		assertTextAndCaret(styledText, "ab| ab");
		
		completion.complete();
		assertTextAndCaret(styledText, "a| ab");
	}
	
	@Test
	public void removeDuplicates() {
		StyledText styledText = fakeStyledText("aaa aab aaa aaa aa|");
		
		Completion completion = new Completion(styledText);
		
		completion.complete();
		assertTextAndCaret(styledText, "aaa aab aaa aaa aaa|");
		
		completion.complete();
		assertTextAndCaret(styledText, "aaa aab aaa aaa aab|");
	}
	
	@Test
	public void completeNextCompletion() {
		StyledText styledText = fakeStyledText("aaa abb a|");
		
		Completion completion = new Completion(styledText);
		
		completion.complete();
		assertTextAndCaret(styledText, "aaa abb abb|");
		
		completion.complete();
		assertTextAndCaret(styledText, "aaa abb aaa|");
	}
	
	@Test
	public void resetAfterCursorMove() {
		StyledText styledText = fakeStyledText("ac ab a|");
		
		Completion completion = new Completion(styledText);
		completion.complete();
		
		styledText.setCaretOffset(0);
		
		completion.complete();
		assertTextAndCaret(styledText, "|ac ab ab");
	}
	
	@Test
	public void resetAfterTextChange() {
		StyledText styledText = fakeStyledText("ac ab a|");
		
		Completion completion = new Completion(styledText);
		completion.complete();
		
		styledText.setText("aaa");
		
		completion.complete();
		assertTextAndCaret(styledText, "|aaa");
	}
	
	@Test
	public void resetAfterClear() {
		StyledText styledText = fakeStyledText("ac ab a|");
		
		Completion completion = new Completion(styledText);
		completion.complete();
		
		completion.dismissCompletions();
		
		completion.complete();
		assertTextAndCaret(styledText, "ac ab ab|");
	}
	
	@Test
	public void canCompleteEmpty() {
		Completion completion = new Completion(fakeStyledText("|"));
		assertFalse(completion.canComplete());
	}
	
	@Test
	public void canCompleteYes() {
		Completion completion = new Completion(fakeStyledText("aa|"));
		assertTrue(completion.canComplete());
	}
	
	@Test
	public void canCompleteNo() {
		Completion completion = new Completion(fakeStyledText("aa |"));
		assertFalse(completion.canComplete());
	}
	
	@Test
	public void completeToUppercase() {
		StyledText styledText = fakeStyledText("Aa a|");
		
		Completion completion = new Completion(styledText);
		completion.complete();
		
		assertTextAndCaret(styledText, "Aa Aa|");
	}
	
	@Test
	public void completeToLowercase() {
		StyledText styledText = fakeStyledText("aa A|");
		
		Completion completion = new Completion(styledText);
		completion.complete();
		
		assertTextAndCaret(styledText, "aa aa|");
	}
}
