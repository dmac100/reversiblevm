package frontend.ui;

import org.eclipse.swt.custom.StyledText;

/**
 * Performs basic edit functions on a StyledText and checks
 * whether they're enabled.
 */
public class EditFunctions {
	private final StyledText text;
	private final UndoRedoImpl undoRedo;
	
	public EditFunctions(StyledText text) {
		this.text = text;
		this.undoRedo = new UndoRedoImpl(text);
	}

	public void selectAll() {
		text.setSelection(0, text.getText().length());
	}

	public void clear() {
		text.setText("");
	}
	
	public String getText() {
		return text.getText();
	}

	public void setText(String s) {
		text.setText(s);
	}

	public void clearUndoHistory() {
		undoRedo.clear();
	}
	
	public void undo() {
		undoRedo.undo();
	}
	
	public void redo() {
		undoRedo.redo();
	}
	
	public void cut() {
		text.cut();
	}
	
	public void copy() {
		text.copy();
	}
	
	public void paste() {
		text.paste();
	}

	public boolean isUndoEnabled() {
		return undoRedo.hasUndo();
	}

	public boolean isRedoEnabled() {
		return undoRedo.hasRedo();
	}

	public boolean isCutEnabled() {
		return text.getSelectionText().length() > 0;
	}

	public boolean isCopyEnabled() {
		return text.getSelectionText().length() > 0;
	}

	public boolean isPasteEnabled() {
		return true;
	}
}
