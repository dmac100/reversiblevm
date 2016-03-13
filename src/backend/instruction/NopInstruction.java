package backend.instruction;

import backend.instruction.stack.PopInstruction;
import backend.runtime.ExecutionException;
import backend.runtime.Runtime;

public class NopInstruction extends Instruction {
	public NopInstruction() {
	}
	
	public static Instruction Nop() {
		return new NopInstruction();
	}
	
	public Instruction copy() {
		return new NopInstruction();
	}
	
	public void execute(Runtime runtime) throws ExecutionException {
	}
	
	public void undo(Runtime runtime) {
	}
	
	public String toString() {
		return super.toString() + "NOP";
	}
}