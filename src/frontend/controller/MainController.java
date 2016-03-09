package frontend.controller;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.eclipse.swt.widgets.Shell;

import backend.runtime.Engine;

import com.google.common.eventbus.EventBus;

import frontend.compiler.Compiler;
import frontend.compiler.CompilerModel;
import frontend.event.CompilerModelChangedEvent;
import frontend.event.ModifiedEvent;
import frontend.ui.Callback;
import frontend.ui.ConsoleText;
import frontend.ui.EditorText;
import frontend.ui.GraphicsCanvas;

public class MainController {
	private final Shell shell;
	private final EditorText editorText;
	private final GraphicsCanvas graphicsCanvas;
	private final ConsoleText consoleText;
	
	private EventBus eventBus;
	private Callback<Boolean> runningChangedCallback;
	
	private File file = null;
	private boolean modified = false;
	
	private final Compiler compiler = new Compiler(this);
	
	public MainController(Shell shell, final EventBus eventBus, EditorText editorText, GraphicsCanvas graphicsCanvas, ConsoleText consoleText) {
		this.shell = shell;
		this.eventBus = eventBus;
		
		this.editorText = editorText;
		this.graphicsCanvas = graphicsCanvas;
		this.consoleText = consoleText;

		compiler.startQueueThread();
		
		loadDefaultText();
		
		editorText.setCompileCallback(new Callback<Void>() {
			public void onCallback(Void param) {
				compile();
				runForward();
			}
		});
		
		editorText.setModifiedCallback(new Callback<Void>() {
			public void onCallback(Void param) {
				modified = true;
				eventBus.post(new ModifiedEvent(modified));
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
	
	public void setCompilerModel(CompilerModel compilerModel) {
		editorText.setDebugLineNumber(compilerModel.getLineNumber());
		consoleText.setOutput(compilerModel.getOutput(), compilerModel.getErrors());
		eventBus.post(new CompilerModelChangedEvent(compilerModel));
	}
	
	public void compile() {
		compiler.compile(editorText.getText());
	}
	
	public void runForward() {
		compiler.runForward();
	}
	
	public void runBackward() {
		compiler.runBackward();
	}
	
	public void stepBackward() {
		compiler.stepBackward();
	}
	
	public void stepForward() {
		compiler.stepForward();
	}
	
	public void stop() {
		compiler.pause();
	}

	public void undo() {
		editorText.getEditFunctions().undo();
	}

	public void redo() {
		editorText.getEditFunctions().redo();
	}
	
	public void cut() {
		editorText.getEditFunctions().cut();
	}
	
	public void copy() {
		editorText.getEditFunctions().copy();
	}
	
	public void paste() {
		editorText.getEditFunctions().paste();
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
