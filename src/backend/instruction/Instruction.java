package backend.instruction;

import java.util.ArrayList;
import java.util.List;

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
	
	public static List<Instruction> copyInstructions(List<Instruction> instructions) {
		List<Instruction> newInstructions = new ArrayList<>();
		for(Instruction instruction:instructions) {
			Instruction newInstruction = instruction.copy();
			newInstruction.setLineNumber(instruction.getLineNumber());
			newInstruction.setInstructionNumber(instruction.getInstructionNumber());
			newInstruction.setColumnNumber(instruction.getColumnNumber());
			newInstructions.add(newInstruction);
		}
		return newInstructions;
	}

	public abstract Instruction copy();
	public abstract void execute(Runtime runtime) throws ExecutionException;
	public abstract void undo(Runtime runtime);
}