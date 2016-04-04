package frontend.controller;

import integration.GraphicsCanvas;
import integration.RuntimeController;
import integration.RuntimeModel;
import integration.VizObjectControlledSettings;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolTip;

import backend.runtime.Engine;

import com.google.common.eventbus.EventBus;

import frontend.event.ModifiedEvent;
import frontend.event.RuntimeModelChangedEvent;
import frontend.ui.Callback;
import frontend.ui.ConsoleText;
import frontend.ui.EditorText;
import frontend.ui.HoverListener;

public class MainController {
	private final Shell shell;
	private final EditorText editorText;
	private final GraphicsCanvas graphicsCanvas;
	private final ConsoleText consoleText;
	
	private EventBus eventBus;
	private Callback<Boolean> runningChangedCallback;
	
	private File file = null;
	private boolean modified = false;
	
	private final RuntimeController runtime = new RuntimeController(this);
	
	public MainController(Shell shell, final EventBus eventBus, final EditorText editorText, GraphicsCanvas graphicsCanvas, ConsoleText consoleText) {
		this.shell = shell;
		this.eventBus = eventBus;
		
		this.editorText = editorText;
		this.graphicsCanvas = graphicsCanvas;
		this.consoleText = consoleText;

		runtime.startQueueThread();
		
		loadDefaultText();
		
		editorText.setCompileCallback(new Callback<Void>() {
			public void onCallback(Void param) {
				String selectedText = editorText.getSelectedText();
				if(selectedText.trim().isEmpty()) {
					compile();
					runForward();
				} else {
					runtime.runCommand(selectedText);
				}
			}
		});
		
		editorText.setModifiedCallback(new Callback<Void>() {
			public void onCallback(Void param) {
				modified = true;
				eventBus.post(new ModifiedEvent(modified));
				runtime.setUserBreakpoints(editorText.getBreakpoints());
			}
		});
		
		editorText.setHoverListener(new HoverListener() {
			public void onHover(int lineNumber, int columnNumber) {
				runtime.hover(lineNumber, columnNumber);
			}
		});
		
		consoleText.setRunCommandCallback(new Callback<String>() {
			public void onCallback(String command) {
				runtime.runCommand(command);
			}
		});
	}

	private void loadDefaultText() {
		try(InputStream inputStream = Engine.class.getResourceAsStream("/backend/runtime/main.js")) {
			editorText.setText(IOUtils.toString(inputStream));
			compile();
		} catch(IOException e) {
			throw new RuntimeException("Error reading main.js file", e);
		}
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
	
	public void setVizObjectControlledSettings(VizObjectControlledSettings vizObjectControlledSettings) {
		graphicsCanvas.setVizObjectControlledSettings(vizObjectControlledSettings);
	}
	
	public void setRuntimeModel(RuntimeModel runtimeModel) {
		editorText.setDebugLineNumber(runtimeModel.getLineNumber());
		consoleText.setOutput(runtimeModel.getOutput());
		graphicsCanvas.setVizObjects(runtimeModel.getVizObjects());
		eventBus.post(new RuntimeModelChangedEvent(runtimeModel));
	}
	
	public void setEditorHover(String value) {
		editorText.setHoverValue(value);
	}
	
	public void compile() {
		runtime.compile(editorText.getText());
	}
	
	public void runForward() {
		runtime.runForward();
	}
	
	public void runBackward() {
		runtime.runBackward();
	}
	
	public void stepBackward() {
		runtime.stepBackward();
	}
	
	public void stepForward() {
		runtime.stepForward();
	}
	
	public void prevVisual() {
		runtime.prevVisual();
	}
	
	public void nextVisual() {
		runtime.nextVisual();
	}
	
	public void stop() {
		runtime.pause();
	}
	
	public void setExecutionPoint(int selection) {
		runtime.setExecutionPoint(selection);
	}

	public void undo() {
		editorText.getEditFunctions().undo();
	}

	public void redo() {
		editorText.getEditFunctions().redo();
	}
	
	public void cut() {
		if(consoleText.hasFocus()) {
			consoleText.cut();
		} else if(editorText.hasFocus()) {
			editorText.getEditFunctions().cut();
		}
	}
	
	public void copy() {
		if(consoleText.hasFocus()) {
			consoleText.copy();
		} else {
			editorText.getEditFunctions().copy();
		}
	}
	
	public void paste() {
		if(consoleText.hasFocus()) {
			consoleText.paste();
		} else {
			editorText.getEditFunctions().paste();
		}
	}

	public void find() {
		editorText.find();
	}

	public boolean undoEnabled() {
		return editorText.getEditFunctions().isUndoEnabled();
	}

	public boolean redoEnabled() {
		return editorText.getEditFunctions().isRedoEnabled();
	}
	
	public boolean cutEnabled() {
		return editorText.getEditFunctions().isCutEnabled();
	}
	
	public boolean copyEnabled() {
		return editorText.getEditFunctions().isCopyEnabled();
	}
	
	public boolean pasteEnabled() {
		return editorText.getEditFunctions().isPasteEnabled();
	}

	public void convertSpacesToTabs() {
		editorText.convertSpacesToTabs();
	}

	public void convertTabsToSpaces() {
		editorText.convertTabsToSpaces();
	}
}
