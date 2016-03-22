package frontend.ui;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;

/**
 * A text widget containing console output.
 */
public class ConsoleText {
	private final StyledText text;
	private final Text commandText;
	private final ColorCache colorCache;
	
	private ConsoleCompletion completion = new ConsoleCompletion();
	
	private List<String> currentText = new ArrayList<>();
	
	private List<String> history = new ArrayList<String>();
	private int historyPosition = 0;
	private String currentCommand = "";
	
	private Callback<String> runCommandCallback;
	
	public ConsoleText(Composite parent) {
		GridLayout gridLayout = new GridLayout(1, false);
		gridLayout.verticalSpacing = 0;
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(gridLayout);
		
		text = new StyledText(composite, SWT.WRAP | SWT.V_SCROLL | SWT.BORDER);
		text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.FILL_VERTICAL));
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
		
		commandText = new Text(composite, SWT.BORDER);
		commandText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		commandText.addTraverseListener(new TraverseListener() {
			public void keyTraversed(TraverseEvent event) {
				event.doit = false;
			}
		});
		
		commandText.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				if(e.stateMask == 0) {
					if(e.keyCode == SWT.ARROW_UP) {
						historyUp();
						e.doit = false;
					}
					
					if(e.keyCode == SWT.ARROW_DOWN) {
						historyDown();
						e.doit = false;
					}
					
					if(e.keyCode == SWT.TAB) {
						insertCompletion();
						e.doit = false;
					} else {
						dismissCompletion();
					}
					
					if(e.character == '\r' || e.character == '\n') {
						runCommand();
						e.doit = false;
					}					
				}
			}
		});

		colorCache = new ColorCache(Display.getCurrent());
		text.addDisposeListener(colorCache);
	}
	
	private void historyUp() {
		if(historyPosition == history.size()) {
			currentCommand = commandText.getText();
		}
			
		if(historyPosition > 0) {
			historyPosition -= 1;
			commandText.setText(history.get(historyPosition));
		}
		
		commandText.setSelection(commandText.getText().length());
	}

	private void historyDown() {
		if(historyPosition >= history.size() - 1) {
			historyPosition = history.size();
			commandText.setText(currentCommand);
		} else {
			historyPosition += 1;
			commandText.setText(history.get(historyPosition));
		}
		
		commandText.setSelection(commandText.getText().length());
	}
	
	private void insertCompletion() {
		if(commandText.getCaretPosition() == commandText.getText().length()) {
			completion.setHistory(history);
			commandText.setText(completion.getCompletion(commandText.getText()));
			commandText.setSelection(commandText.getText().length());
		}
	}
	
	private void dismissCompletion() {
		completion.dismiss();
	}
	
	private void runCommand() {
		String command = commandText.getText().trim();
		if(runCommandCallback != null) {
			runCommandCallback.onCallback(command);
		}
		commandText.setText("");
		
		history.add(command);
		historyPosition = history.size();
	}
	
	public void setRunCommandCallback(Callback<String> callback) {
		this.runCommandCallback = callback;
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
			
			highlightErrors(errors);
		}
	}

	private void highlightErrors(List<String> errors) {
		int errorLength = 0;
		for(String line:errors) {
			errorLength += line.length() + 1;
		}
		
		StyleRange range = new StyleRange();
		range.start = text.getText().length() - errorLength;
		range.length = errorLength;
		range.fontStyle = SWT.NONE;
		range.foreground = text.getDisplay().getSystemColor(SWT.COLOR_RED);
		
		text.setStyleRange(range);
	}
}