package frontend.ui;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

class FindDialog extends Dialog {
	private final StyledText styledText;
	
	private boolean foundMatch;
	private int matchStart;
	private int matchLength;;
	private String matchText;

	private Text findText;
	private Text replaceText;
	private Button regexCheck;
	private Button caseInsensitiveCheck;
	
	private Button findButton;
	private Button replaceButton;
	private Button replaceAllButton;
	
	public FindDialog(Shell parent, StyledText styledText) {
		super(parent, 0);
		this.styledText = styledText;
		setText("Find/Replace");
	}
	
	public String open() {
		Shell parent = getParent();
		final Shell shell = new Shell(parent, SWT.DIALOG_TRIM | SWT.RESIZE);
		shell.setText(getText());
		shell.setLayout(new GridLayout());
		
		// Form Controls

		Composite formComposite = new Composite(shell, SWT.NONE);
		formComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		GridLayout formLayout = new GridLayout(2, false);
		formComposite.setLayout(formLayout);
		
		findText = createTextWithLabel(formComposite, "Find:");
		replaceText = createTextWithLabel(formComposite, "Replace:");
		
		findText.setText(styledText.getSelectionText());
		
		// Options

		regexCheck = new Button(shell, SWT.CHECK);
		regexCheck.setText("Regular Expression");
		regexCheck.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
		
		caseInsensitiveCheck = new Button(shell, SWT.CHECK);
		caseInsensitiveCheck.setText("Case Sensitive");
		caseInsensitiveCheck.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
		
		// Button Composite
		
		Composite buttonComposite = new Composite(shell, SWT.NONE);
		buttonComposite.setLayoutData(new GridData(SWT.RIGHT, SWT.NONE, false, false));
		
		GridLayout buttonLayout = new GridLayout(2, true);
		buttonLayout.marginHeight = 0;
		buttonComposite.setLayout(buttonLayout);
		
		findButton = new Button(buttonComposite, SWT.NONE);
		findButton.setText("Find");
		findButton.setLayoutData(new GridData(SWT.FILL, SWT.NONE, false, false));
		
		replaceButton = new Button(buttonComposite, SWT.NONE);
		replaceButton.setText("Replace");
		replaceButton.setLayoutData(new GridData(SWT.FILL, SWT.NONE, false, false));
		
		replaceAllButton = new Button(buttonComposite, SWT.NONE);
		replaceAllButton.setText("Replace All");
		replaceAllButton.setLayoutData(new GridData(SWT.FILL, SWT.NONE, false, false));
		
		replaceButton.setEnabled(false);
		
		Button closeButton = new Button(buttonComposite, SWT.NONE);
		closeButton.setText("Close");
		closeButton.setLayoutData(new GridData(SWT.FILL, SWT.NONE, false, false));

		shell.setDefaultButton(findButton);
		
		findButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				find();
			}
		});
		
		replaceButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				replace();
			}
		});
		
		replaceAllButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				replaceAll();
			}
		});
		
		closeButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				shell.dispose();
			}
		});
		
		// Open and wait for result.
		shell.pack();
		
		center(shell);
		
		shell.open();
		Display display = parent.getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		
		return null;
	}
	
	private static Text createTextWithLabel(Composite parent, String labelText) {
		Label label = new Label(parent, SWT.NONE);
		label.setText(labelText);
		
		final Text text = new Text(parent, SWT.BORDER);
		GridData gridData = new GridData(SWT.FILL, SWT.NONE, true, false);
		gridData.minimumWidth = 200;
		text.setLayoutData(gridData);
		
		return text;
	}

	private void center(Shell dialog) {
        Rectangle bounds = getParent().getBounds();
        Point size = dialog.getSize();

        dialog.setLocation(
        	bounds.x + (bounds.width - size.x) / 2,
        	bounds.y + (bounds.height - size.y) / 2
        );
	}

	/**
	 * Finds and selects the next result, saving the match if any is found.
	 */
	private void find() {
		foundMatch = false;
		
		String searchText = findText.getText();
		
		// Find results after the current selection.
		int start = styledText.getSelectionRange().x + 1;
		if(start >= styledText.getText().length()) {
			start = 0;
		}
		
		// Try from start position first, then wrap round to the beginning.
		int index = find(start);
		if(index == -1) {
			index = find(0);
		}
		
		// Select and save any match.
		if(index >= 0) {
			foundMatch = true;
			matchStart = index;
			matchLength = searchText.length();
			matchText = styledText.getTextRange(matchStart, matchLength);
			
			styledText.setSelectionRange(matchStart, matchLength);
			styledText.showSelection();
		}
		
		replaceButton.setEnabled(foundMatch);
	}
	
	/**
	 * Replaces the last found result with the replacement text unless it's been changed.
	 */
	private void replace() {
		if(foundMatch) {
			if(matchStart + matchLength <= styledText.getCharCount()) {
				String match = styledText.getTextRange(matchStart, matchLength);
				if(match.equals(matchText)) {
					styledText.replaceTextRange(matchStart, matchLength, replaceText.getText());
				}
			}
		}
	}
	
	/**
	 * Replaces all matches for the search with the replacement text.
	 */
	private void replaceAll() {
		Matcher matcher = getPattern().matcher(styledText.getText());
		String newText = matcher.replaceAll(replaceText.getText());
		styledText.setText(newText);
	}
	
	/**
	 * Returns the start of the next result starting at the given index, or
	 * -1 if nothing is found.
	 */
	private int find(int start) {
		Matcher matcher = getPattern().matcher(styledText.getText());
		if(matcher.find(start)) {
			return matcher.start();
		}
		return -1;
	}
	
	/**
	 * Returns the pattern to use for seaching using the text in the find textbox,
	 * taking into account the case sensitive and regex checkboxes.
	 */
	private Pattern getPattern() {
		String patternText = (regexCheck.getSelection()) ? findText.getText() : Pattern.quote(findText.getText());
		int flags = (caseInsensitiveCheck.getSelection()) ? 0 : Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE;
		return Pattern.compile(patternText, flags);
	}
}
