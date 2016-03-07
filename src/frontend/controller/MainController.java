package frontend.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

import org.apache.commons.io.FileUtils;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.google.common.eventbus.EventBus;

import frontend.compiler.Compiler;
import frontend.event.EnabledChangedEvent;
import frontend.event.ModifiedEvent;
import frontend.ui.Callback;
import frontend.ui.ConsoleText;
import frontend.ui.EditFunctions;
import frontend.ui.EditorText;
import frontend.ui.InputText;
import frontend.util.StringUtil;

public class MainController {
	private final Shell shell;
	private final EditorText editorText;
	private final InputText inputText;
	private final ConsoleText consoleText;
	
	private EventBus eventBus;
	private Callback<Boolean> runningChangedCallback;
	
	private File file = null;
	private boolean modified = false;
	private String jarDir = null;
	private String classpath = null;
	
	private Future<?> runningProgram;
	private ConsoleAppender out;
	private ConsoleAppender err;
	private ConsoleAppender info;
	
	public MainController(Shell shell, final EventBus eventBus, EditorText editorText, InputText inputText, ConsoleText consoleText) {
		this.shell = shell;
		this.eventBus = eventBus;
		
		this.editorText = editorText;
		this.inputText = inputText;
		this.consoleText = consoleText;
		
		editorText.setCompileCallback(new Callback<Void>() {
			public void onCallback(Void param) {
				compile();
			}
		});
		
		editorText.setModifiedCallback(new Callback<Void>() {
			public void onCallback(Void param) {
				modified = true;
				eventBus.post(new ModifiedEvent(modified));
			}
		});
	}

	public void open(String selected) throws IOException {
		String text = FileUtils.readFileToString(new File(selected));
		file = new File(selected);
		editorText.setText(text);
		modified = false;
		eventBus.post(new ModifiedEvent(modified));
		compile();
	}

	public boolean getSaveEnabled() {
		return file != null;
	}

	public void save() throws IOException {
		FileUtils.writeStringToFile(file, editorText.getText());
		modified = false;
		eventBus.post(new ModifiedEvent(modified));
	}
	
	public void saveAs(String selected) throws IOException {
		file = new File(selected);
		FileUtils.writeStringToFile(file, editorText.getText());
		modified = false;
		eventBus.post(new ModifiedEvent(modified));
	}
	
	public boolean getModified() {
		return modified;
	}
	
	public File getFile() {
		return file;
	}
	
	public void compile() {
		final String source = editorText.getText();
		final String input = inputText.getText();

		stop();
		consoleText.clear();
		
		out = new ConsoleAppender(consoleText);
		err = new ConsoleAppender(consoleText);
		info = new ConsoleAppender(consoleText);
		
		try {
			Compiler compiler = new Compiler();
			runningProgram = compiler.runFile(source, input, out, err, info, new Callback<Void>() {
				public void onCallback(Void param) {
					fireRunningChanged(false);
				}
			});
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		fireRunningChanged(true);
	}
	
	public void stop() {
		if(runningProgram != null) {
			runningProgram.cancel(true);
		}
		
		if(out != null) out.close();
		if(err != null) err.close();
		if(info != null) info.close();
	}

	public void setRunningChangedCallback(Callback<Boolean> callback) {
		this.runningChangedCallback = callback;
	}
	
	private void fireRunningChanged(final boolean running) {
		if(runningChangedCallback != null) {
			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					runningChangedCallback.onCallback(running);
				}
			});
		}
	}
	
	private EditFunctions getFocusedEditFunctions() {
		if(editorText.hasFocus()) {
			return editorText.getEditFunctions();
		}
		
		if(inputText.hasFocus()) {
			return inputText.getEditFunctions();
		}
		
		return null;
	}
	
	public void undo() {
		EditFunctions control = getFocusedEditFunctions();
		if(control != null) {
			control.undo();
		}
	}

	public void redo() {
		EditFunctions control = getFocusedEditFunctions();
		if(control != null) {
			control.redo();
		}
	}
	
	public void cut() {
		EditFunctions control = getFocusedEditFunctions();
		if(control != null) {
			control.cut();
		}
	}
	
	public void copy() {
		EditFunctions control = getFocusedEditFunctions();
		if(control != null) {
			control.copy();
		}
	}
	
	public void paste() {
		EditFunctions control = getFocusedEditFunctions();
		if(control != null) {
			control.paste();
		}
	}

	public void find() {
		editorText.find();
	}

	public boolean undoEnabled() {
		EditFunctions control = getFocusedEditFunctions();
		if(control != null) {
			return control.isUndoEnabled();
		} else {
			return false;
		}
	}

	public boolean redoEnabled() {
		EditFunctions control = getFocusedEditFunctions();
		if(control != null) {
			return control.isRedoEnabled();
		} else {
			return false;
		}
	}
	
	public boolean cutEnabled() {
		EditFunctions control = getFocusedEditFunctions();
		if(control != null) {
			return control.isCutEnabled();
		} else {
			return false;
		}
	}
	
	public boolean copyEnabled() {
		EditFunctions control = getFocusedEditFunctions();
		if(control != null) {
			return control.isCopyEnabled();
		} else {
			return false;
		}
	}
	
	public boolean pasteEnabled() {
		EditFunctions control = getFocusedEditFunctions();
		if(control != null) {
			return control.isPasteEnabled();
		} else {
			return false;
		}
	}

	public void convertSpacesToTabs() {
		editorText.convertSpacesToTabs();
	}

	public void convertTabsToSpaces() {
		editorText.convertTabsToSpaces();
	}
}
