package frontend.ui;

import java.util.Set;

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
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import frontend.compiler.Importer;

public class ImportDialog extends Dialog {
	private Shell dialogShell;
	private final StyledText styledText;
	private final Importer importer;
	private Text text;
	private Table table;

	public ImportDialog(Shell parent, Importer importer, StyledText styledText) {
		super(parent, 0);
		this.styledText = styledText;
		this.importer = importer;
		setText("Import");
	}
	
	public String open(String initialText) {
		Shell parent = getParent();
		dialogShell = new Shell(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL | SWT.RESIZE);
		dialogShell.setText(getText());
		dialogShell.setLayout(new GridLayout());
		
		// Form Controls

		Composite formComposite = new Composite(dialogShell, SWT.NONE);
		formComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		GridLayout formLayout = new GridLayout(2, false);
		formComposite.setLayout(formLayout);
		
		Label label = new Label(formComposite, SWT.NONE);
		label.setText("Class Name: ");
		
		text = new Text(formComposite, SWT.BORDER);
		text.setText(initialText);
		text.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
		
		table = new Table(formComposite, SWT.BORDER | SWT.V_SCROLL);
		TableColumn column1 = new TableColumn(table, SWT.NONE);
		TableColumn column2 = new TableColumn(table, SWT.NONE);
		column1.setText("Class");
		column2.setText("Package");
		column1.setWidth(200);
		column2.setWidth(200);
		table.setHeaderVisible(true);
		GridData listGridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		listGridData.horizontalSpan = 2;
		table.setLayoutData(listGridData);
		
		// Button Composite
		
		Composite buttonComposite = new Composite(dialogShell, SWT.NONE);
		buttonComposite.setLayoutData(new GridData(SWT.RIGHT, SWT.NONE, false, false));
		
		GridLayout buttonLayout = new GridLayout(2, true);
		buttonLayout.marginHeight = 0;
		buttonLayout.marginBottom = 5;
		buttonComposite.setLayout(buttonLayout);
		
		final Button importButton = new Button(buttonComposite, SWT.NONE);
		importButton.setText("Import");
		importButton.setEnabled(false);
		importButton.setLayoutData(new GridData(SWT.FILL, SWT.NONE, false, false));
		
		final Button closeButton = new Button(buttonComposite, SWT.NONE);
		closeButton.setText("Close");
		closeButton.setLayoutData(new GridData(SWT.FILL, SWT.NONE, false, false));

		text.addSelectionListener(new SelectionAdapter() {
			public void widgetDefaultSelected(SelectionEvent event) {
				addImports(text.getText());
			}
		});
		
		table.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				importButton.setEnabled(true);
			}
			
			public void widgetDefaultSelected(SelectionEvent event) {
				addSelectedImport();
			}
		});
		
		importButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				addSelectedImport();
			}
		});
		
		closeButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				dialogShell.dispose();
			}
		});
		
		// Open and wait for result.
		dialogShell.setSize(650, 450);
		
		center(dialogShell);
		
		addImports(initialText);
		
		dialogShell.open();
		Display display = parent.getDisplay();
		while (!dialogShell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		
		return null;
	}
	
	public void addImports(String className) {
		if(className.length() == 0) return;
		
		Set<String> imports = importer.findImports(className);
		
		table.removeAll();
		for(String item:imports) {
			String localName = item.replaceAll(".*\\.", "");
			String packageName = item.replaceAll("\\.[^\\.]*$", "");
			
			TableItem tableItem = new TableItem(table, SWT.NONE);
			tableItem.setText(0, localName);
			tableItem.setText(1, packageName);
			tableItem.setData(item);
		}
		
		if(imports.size() > 0) {
			table.setSelection(0);
			table.setFocus();
		}
	}
	
	public void addSelectedImport() {
		TableItem[] selection = table.getSelection();
		if(selection.length > 0) {
			String className = (String)selection[0].getData();
			String line = "import " + className + ";";
			styledText.setText(line + "\n" + styledText.getText());
			dialogShell.dispose();
		}
	}
	
	private void center(Shell dialog) {
        Rectangle bounds = getParent().getBounds();
        Point size = dialog.getSize();

        dialog.setLocation(
        	bounds.x + (bounds.width - size.x) / 2,
        	bounds.y + (bounds.height - size.y) / 2
        );
	}
}