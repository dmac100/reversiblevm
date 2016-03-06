package frontend.compiler;

import com.google.common.base.Optional;

public class BracketMatcher {
	/**
	 * Returns the position of the matching bracket for the char at offset, if there is
	 * a match.
	 */
	public Optional<Integer> getMatchingParen(String text, int offset) {
		char starting = text.charAt(offset);
		
		char[] startChars = { '(', '{', '[', '<' };
		char[] endChars = { ')', '}', ']', '>' };
		
		for(int x = 0; x < startChars.length; x++) {
			if(starting == startChars[x]) {
				return getMatchingParen(text, starting, endChars[x], offset, 1);
			}
			if(starting == endChars[x]) {
				return getMatchingParen(text, starting, startChars[x], offset, -1);
			}
		}
		
		return Optional.absent();
	}
	
	/**
	 * Returns the position of the matching bracket for the char at offset, if there is
	 * a match. Count the pairs of starting and ending chars in the given direction (+1/-1),
	 * until a match is found.
	 */
	public Optional<Integer> getMatchingParen(String text, char starting, char ending, int offset, int direction) {
		int count = 0;
		
		for(int x = offset; x >= 0 && x < text.length(); x += direction) {
			char c = text.charAt(x);
			
			if(c == starting) count++;
			if(c == ending) count--;
			
			if(count == 0) return Optional.of(x);
		}
		
		return Optional.absent();
	}
}
