package instruction;

import runtime.Runtime;
import value.FunctionValue;

public class StartFunctionInstruction implements Instruction {
	public StartFunctionInstruction() {
	}
	
	public static Instruction StartFunction() {
		return new StartFunctionInstruction();
	}
	
	public void execute(Runtime runtime) {
		if(runtime.getNestedFunctionDefinitionCount() == 0) {
			runtime.setCurrentFunctionDefinition(new FunctionValue(runtime.getScope()));
		}
		runtime.setNestedFunctionDefinitionCount(runtime.getNestedFunctionDefinitionCount() + 1);
	}
	
	public String toString() {
		return "STARTFUNCTION";
	}
}