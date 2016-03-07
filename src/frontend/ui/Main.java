package frontend.ui;

import java.io.File;
import java.io.IOException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import frontend.controller.MainController;
import frontend.event.EnabledChangedEvent;
import frontend.event.ModifiedEvent;

public class Main {
	private final EventBus eventBus = new EventBus();
	private final Shell shell;
	private final MainController mainController;
	
	private EditorText editorText;
	private SashForm horizontalSash;
	
	private boolean inputVisible = false;
	
	public Main(final Shell shell) {
		this.shell = shell;

		shell.setLayout(new GridLayout(1, false));
		Composite top = new Composite(shell, SWT.NONE);
		Composite bottom = new Composite(shell, SWT.BORDER);
		bottom.setLayout(new FillLayout());
		bottom.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.FILL_VERTICAL));
		
		SashForm verticalSash = new SashForm(bottom, SWT.VERTICAL);
		horizontalSash = new SashForm(verticalSash, SWT.HORIZONTAL);
		editorText = new EditorText(eventBus, shell, horizontalSash);
		InputText inputText = new InputText(eventBus, horizontalSash);
		ConsoleText consoleText = new ConsoleText(verticalSash);
		
		horizontalSash.setWeights(new int[] { 70, 30 });
		verticalSash.setWeights(new int[] { 75, 25 });
		
		mainController = new MainController(shell, eventBus, editorText, inputText, consoleText);
		
		createMenuBar(shell);
		createToolBar(top);
		refreshTitle();
		setInputPaneVisible(inputVisible);
		
		eventBus.register(new Object() {
			@Subscribe @SuppressWarnings("unused")
			public void onEnabledChanged(EnabledChangedEvent event) {
				createMenuBar(shell);
			}
			
			@Subscribe @SuppressWarnings("unused")
			public void onModified(ModifiedEvent event) {
				refreshTitle();
			}
		});
	}
	
	private void createMenuBar(final Shell shell) {
		MenuBuilder menuBuilder = new MenuBuilder(shell);
		
		menuBuilder.addMenu("&File")
			.addItem("&Open...\tCtrl+O").addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent event) {
					try {
						open();
					} catch(Exception e) {
						displayException(e);
					}
				}
			})
			.setAccelerator(SWT.CONTROL | 'o')
			.addSeparator()
			.addItem("&Save\tCtrl+S").addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent event) {
					try {
						if(mainController.getSaveEnabled()) {
							mainController.save();
						} else {
							saveAs();
						}
					} catch(Exception e) {
						displayException(e);
					}
				}
			})
			.setAccelerator(SWT.CONTROL | 's')
			.addItem("Save &As...\tShift+Ctrl+S").addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent event) {
					try {
						saveAs();
					} catch(Exception e) {
						displayException(e);
					}
				}
			})
			.setAccelerator(SWT.CONTROL | SWT.SHIFT | 's')
			.addSeparator()
			.addItem("E&xit\tCtrl+Q").addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent event) {
					shell.dispose();
				}
			})
			.setAccelerator(SWT.CTRL | 'q');
		
		menuBuilder.addMenu("&Edit")
			.addItem("&Undo\tCtrl+Z").addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent event) {
					try {
						mainController.undo();
					} catch(Exception e) {
						displayException(e);
					}
				}
			})
			.setAccelerator(SWT.CONTROL | 'z')
			.setEnabled(mainController.undoEnabled())
			.addItem("&Redo\tCtrl+Y").addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent event) {
					try {
						mainController.redo();
					} catch(Exception e) {
						displayException(e);
					}
				}
			}).setEnabled(mainController.redoEnabled())
			.setAccelerator(SWT.CONTROL | 'y')
			.addSeparator()
			.addItem("Cu&t\tCtrl+X").addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent event) {
					try {
						mainController.cut();
					} catch(Exception e) {
						displayException(e);
					}
				}
			})
			.setAccelerator(SWT.CONTROL | 'x')
			.setEnabled(mainController.cutEnabled())
			.addItem("&Copy\tCtrl+C").addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent event) {
					try {
						mainController.copy();
					} catch(Exception e) {
						displayException(e);
					}
				}
			})
			.setAccelerator(SWT.CONTROL | 'c')
			.setEnabled(mainController.copyEnabled())
			.addItem("&Paste\tCtrl+V").addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent event) {
					try {
						mainController.paste();
					} catch(Exception e) {
						displayException(e);
					}
				}
			})
			.setAccelerator(SWT.CONTROL | 'v')
			.setEnabled(mainController.pasteEnabled())
			.addSeparator()
			.addItem("&Find/Replace...\tCtrl+F").addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent event) {
					try {
						mainController.find();
					} catch(Exception e) {
						displayException(e);
					}
				}
			})
			.setAccelerator(SWT.CONTROL | 'f')
			.addSeparator()
			.addItem("Convert Spaces to Tabs").addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent event) {
					try {
						mainController.convertSpacesToTabs();
					} catch(Exception e) {
						displayException(e);
					}
				}
			})
			.addItem("Convert Tabs to Spaces").addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent event) {
					try {
						mainController.convertTabsToSpaces();
					} catch(Exception e) {
						displayException(e);
					}
				}
			});
		
		menuBuilder.addMenu("&View")
			.addItem("&Show Input Pane\tCtrl+I").addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent event) {
					setInputPaneVisible(true);
				}
			})
			.setAccelerator(SWT.CTRL | 'i')
			.setEnabled(!inputVisible)
			.addItem("&Hide Input Pane\tCtrl+I").addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent event) {
					setInputPaneVisible(false);
				}
			})
			.setAccelerator(SWT.CTRL | 'i')
			.setEnabled(inputVisible);
		
		menuBuilder.build();
	}
	
	private void open() throws IOException {
		FileDialog dialog = new FileDialog(shell, SWT.OPEN);
		dialog.setText("Open");
		
		dialog.setFilterExtensions(new String[] { "*.js", "*.*" });

		String selected = dialog.open();
		
		if(selected != null) {
			mainController.open(selected);
		}
	}
	
	private void saveAs() throws IOException {
		FileDialog dialog = new FileDialog(shell, SWT.SAVE);
		dialog.setText("Save");
		
		String selected = dialog.open();
		
		if(selected != null) {
			mainController.saveAs(selected);
		}
	}

	private void setInputPaneVisible(boolean visible) {
		inputVisible = visible;
		
		if(visible) {
			horizontalSash.setMaximizedControl(null);
		} else {
			horizontalSash.setMaximizedControl(editorText.getControl());
		}
		
		eventBus.post(new EnabledChangedEvent());
	}
	
	private void createToolBar(Composite parent) {
		parent.setLayout(new FillLayout());
		parent.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL));
		
		Button compileButton = new Button(parent, SWT.NONE);
		compileButton.setText("Compile/Run");
		compileButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				try {
					mainController.compile();
				} catch(Exception e) {
					displayException(e);
				}
			}
		});
		
		final Button stopButton = new Button(parent, SWT.NONE);
		stopButton.setText("Stop");
		stopButton.setEnabled(false);
		stopButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				try {
					mainController.stop();
				} catch(Exception e) {
					displayException(e);
				}
			}
		});
		
		mainController.setRunningChangedCallback(new Callback<Boolean>() {
			public void onCallback(Boolean running) {
				stopButton.setEnabled(running);
			}
		});
	}
	
	private void displayMessage(String message) {
		MessageBox alert = new MessageBox(shell, SWT.NONE);
		alert.setText("Message");
		alert.setMessage(message);
		alert.open();
	}
	
	private void displayException(Exception e) {
		MessageBox alert = new MessageBox(shell);
		alert.setText("Error");
		alert.setMessage((e.getMessage() == null) ? "Unknown error" : e.getMessage());
		e.printStackTrace();
		alert.open();
	}

	public void refreshTitle() {
		shell.setText("ScratchPad");

		String filename = "Untitled";
		String modified = "";
		
		File file = mainController.getFile();
		if(file != null) {
			if(file.getParentFile() == null) {
				filename = file.getName();
			} else {
				filename = file.getName() + " (" + file.getParentFile() + ")";
			}
		}
		
		if(mainController.getModified()) {
			modified = "*";
		}
		
		shell.setText(modified + filename + " - ScratchPad");
	}
	
	private void addFileDropTarget(Composite parent) {
		final FileTransfer fileTransfer = FileTransfer.getInstance();
		
		DropTarget target = new DropTarget(parent, DND.DROP_COPY | DND.DROP_DEFAULT);
		target.setTransfer(new Transfer[] { fileTransfer });
		target.addDropListener(new DropTargetAdapter() {
			public void dragEnter(DropTargetEvent event) {
				if(event.detail == DND.DROP_DEFAULT) {
					if((event.operations & DND.DROP_COPY) > 0) {
						event.detail = DND.DROP_COPY;
					} else {
						event.detail = DND.DROP_NONE;
					}
				}
			}
			
			public void drop(DropTargetEvent event) {
				if(fileTransfer.isSupportedType(event.currentDataType)) {
					String[] files = (String[])event.data;
					for(String file:files) {
						try {
							mainController.open(file);
						} catch(Exception e) {
							displayException(e);
						}
					}
				}
			}
		});
	}
	
	public static void main(String[] args) {
		Display display = new Display();

		Shell shell = new Shell(display);

		Main main = new Main(shell);
		main.parseArgs(args);
		
		shell.setSize(700, 600);
		shell.open();
		
		main.addFileDropTarget(shell);

		while(!shell.isDisposed()) {
			if(!display.readAndDispatch()) {
				display.sleep();
			}
		}

		display.dispose();
	}

	private void parseArgs(String[] args) {
		CommandLineParser parser = new GnuParser();
		
		Options options = new Options();
		options.addOption(new Option("l", "language", true, "set the language by name"));
		options.addOption(new Option("f", "file", true, "load a file"));
		options.addOption(new Option("i", "input", false, "show input pane"));
		options.addOption(new Option("j", "jardir", true, "adds the jars in a directory to the classpath"));
		options.addOption(new Option("cp", "classpath", true, "sets the classpath to use when compiling/running java programs"));
		options.addOption(new Option("h", "help", false, "show help"));
		
		try {
			CommandLine command = parser.parse(options, args);
		
			if(command.hasOption("h") || command.getArgs().length > 1) {
				new HelpFormatter().printHelp("java -jar scratchpad.jar [options] [filename]", options);
				System.exit(0);
			}
			
			if(command.hasOption("i")) {
				setInputPaneVisible(true);
			}
			
			if(command.hasOption("f")) {
				mainController.open(command.getOptionValue("f"));
			}
			
			for(String s:command.getArgs()) {
				mainController.open(s);
			}
		} catch(Throwable e) {
			// Print usage and exit on any error.
			System.err.println(e.getMessage());
			new HelpFormatter().printHelp("java -jar scratchpad.jar", options);
			System.exit(0);
		}
	}
}
