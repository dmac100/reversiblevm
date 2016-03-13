package backend.instruction.function;

import backend.instruction.Instruction;
import backend.instruction.viz.EndVizInstruction;
import backend.runtime.Runtime;

public class EndFunctionInstruction extends Instruction {
	public EndFunctionInstruction() {
	}
	
	public static Instruction EndFunction() {
		return new EndFunctionInstruction();
	}
	
	public Instruction copy() {
		return new EndFunctionInstruction();
	}
	
	public void execute(Runtime runtime) {
	}
	
	public void undo(Runtime runtime) {
	}
	
	public String toString() {
		return super.toString() + "ENDFUNCTION";
	}
}