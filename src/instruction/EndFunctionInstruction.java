package instruction;

import runtime.Runtime;

public class EndFunctionInstruction implements Instruction {
	public EndFunctionInstruction() {
	}
	
	public static Instruction EndFunction() {
		return new EndFunctionInstruction();
	}
	
	public void execute(Runtime runtime) {
		runtime.setNestedFunctionDefinitionCount(runtime.getNestedFunctionDefinitionCount() - 1);
		if(runtime.getNestedFunctionDefinitionCount() == 0) {
			runtime.getStack().push(runtime.getCurrentFunctionDefinition(), true);
			runtime.setCurrentFunctionDefinition(null);
		} else {
			runtime.getCurrentFunctionDefinition().addInstruction(this);
		}
	}
	
	public void undo(Runtime runtime) {
	}
	
	public String toString() {
		return "ENDFUNCTION";
	}
}