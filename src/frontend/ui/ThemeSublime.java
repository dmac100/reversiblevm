package frontend.ui;

import java.awt.Color;
import java.awt.Font;
import syntaxhighlight.Style;
import syntaxhighlight.Theme;

class ThemeSublime extends Theme {
	public ThemeSublime() {
		setFont(new Font("Consolas", Font.PLAIN, 12));
		setBackground(Color.decode("0x272822"));

		setHighlightedBackground(Color.decode("0x253e5a"));

		setGutterText(Color.decode("0x38566f"));
		setGutterBorderColor(Color.decode("0x435a5f"));
		setGutterBorderWidth(3);
		setGutterTextFont(new Font("Verdana", Font.PLAIN, 11));
		setGutterTextPaddingLeft(7);
		setGutterTextPaddingRight(7);

		Style style = new Style();
		style.setBold(true);
		addStyle("bold", style);

		style = new Style();
		style.setColor(Color.decode("0xffffff"));
		addStyle("plain", style);
		setPlain(style);

		style = new Style();
		style.setColor(Color.decode("0x75715E"));
		addStyle("comments", style);

		style = new Style();
		style.setColor(Color.decode("0xE6DB74"));
		addStyle("string", style);

		style = new Style();
		style.setColor(Color.decode("0xF92672"));
		addStyle("keyword", style);

		style = new Style();
		style.setColor(Color.decode("0x8aa6c1"));
		addStyle("preprocessor", style);

		style = new Style();
		style.setColor(Color.decode("0xFD971F"));
		addStyle("variable", style);

		style = new Style();
		style.setColor(Color.decode("0x00ff00"));
		addStyle("value", style);

		style = new Style();
		style.setColor(Color.decode("0x66d9ef"));
		addStyle("functions", style);

		style = new Style();
		style.setColor(Color.decode("0xae81ff"));
		addStyle("constants", style);

		style = new Style();
		style.setBold(true);
		style.setColor(Color.decode("0xaaaaff"));
		addStyle("script", style);

		style = new Style();
		addStyle("scriptBackground", style);

		style = new Style();
		style.setColor(Color.red);
		addStyle("color3", style);

		style = new Style();
		style.setColor(Color.yellow);
		addStyle("color2", style);

		style = new Style();
		style.setColor(Color.decode("0xffaa3e"));
		addStyle("color3", style);
	}
}