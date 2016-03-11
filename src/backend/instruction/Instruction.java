package backend.instruction;

import backend.runtime.ExecutionException;
import backend.runtime.Runtime;

public abstract class Instruction {
	private short lineNumber;
	private short instructionNumber;
	private short columnNumber;
	
	public short getLineNumber() {
		return lineNumber;
	}
	
	public void setLineNumber(short lineNumber) {
		this.lineNumber = lineNumber;
	}
	
	public short getInstructionNumber() {
		return instructionNumber;
	}

	public void setInstructionNumber(short instructionNumber) {
		this.instructionNumber = instructionNumber;
	}

	public short getColumnNumber() {
		return columnNumber;
	}
	
	public void setColumnNumber(short columnNumber) {
		this.columnNumber = columnNumber;
	}
	
	public abstract void execute(Runtime runtime) throws ExecutionException;
	public abstract void undo(Runtime runtime);
}