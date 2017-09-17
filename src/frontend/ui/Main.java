package frontend.ui;

import integration.GraphicsCanvas;
import integration.RuntimeModel;

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
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
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
import org.eclipse.swt.widgets.Slider;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import frontend.controller.MainController;
import frontend.event.EnabledChangedEvent;
import frontend.event.ModifiedEvent;
import frontend.event.RuntimeModelChangedEvent;

public class Main {
	private final EventBus eventBus = new EventBus();
	private final Shell shell;
	private final MainController mainController;
	
	private SashForm horizontalSash;
	private SashForm verticalSash;
	
	private Button compileButton;
	private Button runForwardButton;
	private Button runBackwardButton;
	private Button pauseButton;
	private Button stepBackwardButton;
	private Button stepForwardButton;
	private Button nextVisualButton;
	private Button prevVisualButton;
	
	private Slider executionPointSlider;
	private boolean executionPointSliderDragInProgress;
	
	private boolean graphicsPane = true;
	private boolean consolePane = true;
	
	public static void main(String[] args) {
		System.setProperty("line.separator", "\n");
		
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
	
	public Main(final Shell shell) {
		this.shell = shell;

		shell.setLayout(new GridLayout(1, false));
		final Composite top = new Composite(shell, SWT.NONE);
		final Composite bottom = new Composite(shell, SWT.BORDER);
		bottom.setLayout(new FillLayout());
		bottom.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.FILL_VERTICAL));
		
		verticalSash = new SashForm(bottom, SWT.VERTICAL);
		horizontalSash = new SashForm(verticalSash, SWT.HORIZONTAL);
		EditorText editorText = new EditorText(eventBus, shell, horizontalSash);
		GraphicsCanvas graphicsCanvas = new GraphicsCanvas(eventBus, horizontalSash);
		ConsoleText consoleText = new ConsoleText(verticalSash);
		
		horizontalSash.setWeights(new int[] { 60, 40 });
		verticalSash.setWeights(new int[] { 75, 25 });
		
		mainController = new MainController(shell, eventBus, editorText, graphicsCanvas, consoleText);
		
		createMenuBar(shell);
		createToolBar(top);
		refreshTitle();
		setGraphicsPaneVisible(graphicsPane);
		
		refreshToolbar(new RuntimeModel());
		
		eventBus.register(new Object() {
			@Subscribe @SuppressWarnings("unused")
			public void onEnabledChanged(EnabledChangedEvent event) {
				createMenuBar(shell);
			}
			
			@Subscribe @SuppressWarnings("unused")
			public void onCompilerModelChanged(RuntimeModelChangedEvent event) {
				refreshToolbar(event.getRuntimeModel());
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
			.addItem("Export HtML...").addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent event) {
					try {
						exportHtml();
					} catch(Exception e) {
						displayException(e);
					}
				}
			})
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
			.addItem("Show &Graphics Pane\tCtrl+G").addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent event) {
					setGraphicsPaneVisible(true);
				}
			})
			.setAccelerator(SWT.CTRL | 'g')
			.setEnabled(!graphicsPane)
			.addItem("Hide &Graphics Pane\tCtrl+G").addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent event) {
					setGraphicsPaneVisible(false);
				}
			})
			.setAccelerator(SWT.CTRL | 'g')
			.setEnabled(graphicsPane)
			.addItem("Show &Console\tCtrl+L").addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent event) {
					setConsolePaneVisible(true);
				}
			})
			.setAccelerator(SWT.CTRL | 'l')
			.setEnabled(!consolePane)
			.addItem("Hide &Console\tCtrl+L").addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent event) {
					setConsolePaneVisible(false);
				}
			})
			.setAccelerator(SWT.CTRL | 'l')
			.setEnabled(consolePane);
		
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
	
	private void exportHtml() throws IOException {
		FileDialog dialog = new FileDialog(shell, SWT.SAVE);
		dialog.setText("Export HTML");
		
		String selected = dialog.open();
		
		if(selected != null) {
			mainController.exportHtml(selected);
		}
	}

	private void setGraphicsPaneVisible(boolean visible) {
		graphicsPane = visible;
		
		if(visible) {
			horizontalSash.setMaximizedControl(null);
		} else {
			horizontalSash.setMaximizedControl(horizontalSash.getChildren()[0]);
		}
		
		eventBus.post(new EnabledChangedEvent());
	}
	
	private void setConsolePaneVisible(boolean visible) {
		consolePane = visible;
		
		if(visible) {
			verticalSash.setMaximizedControl(null);
		} else {
			verticalSash.setMaximizedControl(verticalSash.getChildren()[0]);
		}
		
		eventBus.post(new EnabledChangedEvent());
	}

	private void createToolBar(Composite parent) {
		GridLayout gridLayout = new GridLayout(1, false);
		parent.setLayout(gridLayout);
		parent.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL));
		gridLayout.verticalSpacing = 2;
		gridLayout.marginWidth = 1;
		gridLayout.marginHeight = 1;
		
		Composite topPanel = new Composite(parent, SWT.NONE);
		topPanel.setLayout(new FillLayout());
		topPanel.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL));
		
		compileButton = new Button(topPanel, SWT.NONE);
		compileButton.setText("Compile");
		compileButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				try {
					mainController.compile();
				} catch(Exception e) {
					displayException(e);
				}
			}
		});
		
		runBackwardButton = new Button(topPanel, SWT.NONE);
		runBackwardButton.setText("Run Backward");
		runBackwardButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				try {
					mainController.runBackward();
				} catch(Exception e) {
					displayException(e);
				}
			}
		});
		
		prevVisualButton = new Button(topPanel, SWT.NONE);
		prevVisualButton.setText("Prev Visual");
		prevVisualButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				try {
					mainController.prevVisual();
				} catch(Exception e) {
					displayException(e);
				}
			}
		});
		
		stepBackwardButton = new Button(topPanel, SWT.NONE);
		stepBackwardButton.setText("Step Backward");
		stepBackwardButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				try {
					mainController.stepBackward();
				} catch(Exception e) {
					displayException(e);
				}
			}
		});
		
		pauseButton = new Button(topPanel, SWT.NONE);
		pauseButton.setText("Pause");
		pauseButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				try {
					mainController.stop();
				} catch(Exception e) {
					displayException(e);
				}
			}
		});
		
		stepForwardButton = new Button(topPanel, SWT.NONE);
		stepForwardButton.setText("Step Forward");
		stepForwardButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				try {
					mainController.stepForward();
				} catch(Exception e) {
					displayException(e);
				}
			}
		});
		
		nextVisualButton = new Button(topPanel, SWT.NONE);
		nextVisualButton.setText("Next Visual");
		nextVisualButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				try {
					mainController.nextVisual();
				} catch(Exception e) {
					displayException(e);
				}
			}
		});
		
		runForwardButton = new Button(topPanel, SWT.NONE);
		runForwardButton.setText("Run Forward");
		runForwardButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				try {
					mainController.runForward();
				} catch(Exception e) {
					displayException(e);
				}
			}
		});
		
		Composite bottomPanel = new Composite(parent, SWT.NONE);
	    bottomPanel.setLayoutData(new GridData(SWT.CENTER, SWT.TOP, true, true));
	    
		executionPointSlider = new Slider(bottomPanel, SWT.HORIZONTAL);
		executionPointSlider.setBounds(1, 0, 400, 15);
		executionPointSlider.setEnabled(false);
		executionPointSlider.setMaximum(1);
		executionPointSlider.setThumb(1);
		executionPointSlider.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				mainController.setExecutionPoint(executionPointSlider.getSelection());
			}
		});
		
		executionPointSlider.addMouseListener(new MouseAdapter() {
			public void mouseDown(MouseEvent event) {
				executionPointSliderDragInProgress = true;
			}

			public void mouseUp(MouseEvent event) {
				executionPointSliderDragInProgress = false;
			}
		});
	}

	private void refreshToolbar(RuntimeModel runtimeModel) {
		compileButton.setEnabled(runtimeModel.isCompileEnabled());
		runForwardButton.setEnabled(runtimeModel.isForwardEnabled());
		runBackwardButton.setEnabled(runtimeModel.isBackwardEnabled());
		pauseButton.setEnabled(runtimeModel.isPauseEnabled());
		stepBackwardButton.setEnabled(runtimeModel.isBackwardEnabled());
		stepForwardButton.setEnabled(runtimeModel.isForwardEnabled());
		nextVisualButton.setEnabled(runtimeModel.isForwardEnabled());
		prevVisualButton.setEnabled(runtimeModel.isBackwardEnabled());
		
		if(runtimeModel.getMaxInstructionsExecutedCount() > 0) {
			executionPointSlider.setEnabled(true);
			if(runtimeModel.getMaxInstructionsExecutedCount() + 1 != executionPointSlider.getMaximum()) {
				executionPointSlider.setMaximum(runtimeModel.getMaxInstructionsExecutedCount() + 1);
			}
			if(executionPointSlider.getSelection() != runtimeModel.getLinesExecutedCount() && !executionPointSliderDragInProgress) {
				executionPointSlider.setSelection(runtimeModel.getLinesExecutedCount());
			}
		} else {
			executionPointSlider.setEnabled(false);
		}
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
		shell.setText("ReversibleVm");

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
		
		shell.setText(modified + filename + " - ReversibleVm");
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
	
	private void parseArgs(String[] args) {
		CommandLineParser parser = new GnuParser();
		
		Options options = new Options();
		options.addOption(new Option("f", "file", true, "load a file"));
		options.addOption(new Option("g", "graphics", false, "show graphics pane"));
		options.addOption(new Option("h", "help", false, "show help"));
		
		try {
			CommandLine command = parser.parse(options, args);
		
			if(command.hasOption("h") || command.getArgs().length > 1) {
				new HelpFormatter().printHelp("java -jar reversiblevm.jar [options] [filename]", options);
				System.exit(0);
			}
			
			if(command.hasOption("g")) {
				setGraphicsPaneVisible(true);
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
			new HelpFormatter().printHelp("java -jar reversiblevm.jar", options);
			System.exit(0);
		}
	}
}
