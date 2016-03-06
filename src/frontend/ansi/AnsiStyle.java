package frontend.ansi;

import org.eclipse.swt.graphics.RGB;

public class AnsiStyle {
	public int start;
	public int length;
	public RGB foreground;
	public RGB background;
	public boolean underline;
	public boolean doubleUnderline;
	public boolean bold;
	public boolean italic;
	
	public AnsiStyle() {
	}
	
	public AnsiStyle(AnsiStyle style) {
		this.start = style.start;
		this.length = style.length;
		this.foreground = style.foreground;
		this.background = style.background;
		this.underline = style.underline;
		this.doubleUnderline = style.doubleUnderline;
		this.bold = style.bold;
		this.italic = style.italic;
	}
}
