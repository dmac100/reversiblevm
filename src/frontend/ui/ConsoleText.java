package frontend.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

/**
 * A text widget containing console output.
 */
public class ConsoleText {
	private final StyledText text;
	private final ColorCache colorCache;
	
	public ConsoleText(Composite parent) {
		text = new StyledText(parent, SWT.WRAP | SWT.V_SCROLL);
		text.setEditable(false);
		text.setTabs(8);
		text.setMargins(3, 0, 3, 0);
		
		text.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent event) {
				if((event.stateMask & SWT.CTRL) > 0) {
					if(event.keyCode == 'a') {
						selectAll();
					}
				}
			}
		});

		text.setFont(FontList.consolas8);

		colorCache = new ColorCache(Display.getCurrent());
		text.addDisposeListener(colorCache);
		
		clear();
	}
	
	public void selectAll() {
		text.setSelection(0, text.getText().length());
	}

	/**
	 * Clears the console.
	 */
	public void clear() {
		text.setText("");
	}
	
	/**
	 * Appends some text to the end of the console.
	 */
	public void append(String newText) {
		text.append(newText);
		text.setTopIndex(text.getLineCount() - 1);
	}
}
