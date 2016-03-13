package backend.instruction.function;

import backend.instruction.Instruction;
import backend.instruction.operator.ShiftRightInstruction;
import backend.runtime.Runtime;
import backend.runtime.ExecutionException;

public class ReturnInstruction extends Instruction {
	public ReturnInstruction() {
	}
	
	public static Instruction Return() {
		return new ReturnInstruction();
	}
	
	public Instruction copy() {
		return new ReturnInstruction();
	}
	
	public void execute(Runtime runtime) throws ExecutionException {
		runtime.popStackFrame();
	}
	
	public void undo(Runtime runtime) {
	}
	
	public String toString() {
		return super.toString() + "RETURN";
	}
}