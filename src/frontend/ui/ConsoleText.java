package frontend.ui;

import java.util.ArrayList;
import java.util.List;

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
	
	private List<String> currentText = new ArrayList<>();
	
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
	}
	
	public void selectAll() {
		text.setSelection(0, text.getText().length());
	}

	/**
	 * Sets the whole output of the console.
	 */
	public void setOutput(List<String> output, List<String> errors) {
		List<String> newText = new ArrayList<>();
		newText.addAll(output);
		newText.addAll(errors);
		
		if(!newText.equals(currentText)) {
			StringBuilder s = new StringBuilder();
			for(String line:newText) {
				s.append(line).append("\n");
			}
			text.setText(s.toString());
			currentText = newText;
			text.setTopIndex(text.getLineCount() - 1);
		}
	}
}

