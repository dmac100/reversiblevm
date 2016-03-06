package frontend.ansi;

import java.util.List;

import org.eclipse.swt.custom.StyleRange;

public class ParseResult {
	private final String newText;
	private final List<AnsiStyle> styles;
	
	public ParseResult(String newText, List<AnsiStyle> styles) {
		this.newText = newText;
		this.styles = styles;
	}
	
	public String getNewText() {
		return newText;
	}
	
	public List<AnsiStyle> getStyleRanges() {
		return styles;
	}
}
