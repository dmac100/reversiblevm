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

import backend.runtime.OutputLine;
import backend.runtime.OutputLine.OutputType;

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
	private String historyPrefix = "";
	
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

		text.setFont(FontList.consolas9);
		
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
	
	public void setHistoryPrefix(String prefix) {
		this.historyPrefix = prefix;
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
			completion.setHistory(historyPrefix, history);
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
	public void setOutput(List<OutputLine> output) {
		List<StyleRange> styleRanges = new ArrayList<>();
		
		List<String> newText = new ArrayList<>();
		for(OutputLine line:output) {
			newText.add(line.getText());
		}
		
		if(!newText.equals(currentText)) {
			StringBuilder s = new StringBuilder();
			for(OutputLine line:output) {
				s.append(line.getText()).append("\n");
				
				StyleRange styleRange = new StyleRange();
				styleRange.start = s.length() - line.getText().length() - 1;
				styleRange.length = line.getText().length() + 1;
				styleRange.fontStyle = SWT.NONE;
				if(line.getType() == OutputType.ERROR) {
					styleRange.foreground = text.getDisplay().getSystemColor(SWT.COLOR_RED);
				} else if(line.getType() == OutputType.INFO) {
					styleRange.foreground = text.getDisplay().getSystemColor(SWT.COLOR_BLUE);
				} else {
					styleRange.foreground = text.getDisplay().getSystemColor(SWT.COLOR_BLACK);
				}
				styleRanges.add(styleRange);
			}
			
			text.setText(s.toString());
			currentText = newText;
			text.setTopIndex(text.getLineCount() - 1);
			text.setStyleRanges(styleRanges.toArray(new StyleRange[styleRanges.size()]));
		}
	}
	
	public boolean hasFocus() {
		return commandText.isFocusControl();
	}
	
	public void cut() {
		commandText.cut();
	}
	
	public void copy() {
		commandText.copy();
	}
	
	public void paste() {
		commandText.paste();
	}
}