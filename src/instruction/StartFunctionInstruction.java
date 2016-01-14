package instruction;

import runtime.Runtime;
import value.FunctionValue;

public class StartFunctionInstruction implements Instruction {
	private int paramCount;

	public StartFunctionInstruction(int paramCount) {
		this.paramCount = paramCount;
	}
	
	public static Instruction StartFunction(int paramCount) {
		return new StartFunctionInstruction(paramCount);
	}
	
	public void execute(Runtime runtime) {
		if(runtime.getNestedFunctionDefinitionCount() == 0) {
			runtime.setCurrentFunctionDefinition(new FunctionValue(runtime.getScope(), paramCount));
		} else {
			runtime.getCurrentFunctionDefinition().addInstruction(this);
		}
		runtime.setNestedFunctionDefinitionCount(runtime.getNestedFunctionDefinitionCount() + 1);
	}
	
	public String toString() {
		return "STARTFUNCTION: " + paramCount;
	}
}