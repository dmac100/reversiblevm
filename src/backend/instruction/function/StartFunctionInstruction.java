package backend.instruction.function;

import java.util.List;

import backend.instruction.Instruction;
import backend.runtime.ExecutionException;
import backend.runtime.Runtime;
import backend.value.FunctionValue;

public class StartFunctionInstruction extends Instruction {
	private int paramCount;

	public StartFunctionInstruction(int paramCount) {
		this.paramCount = paramCount;
	}
	
	public static Instruction StartFunction(int paramCount) {
		return new StartFunctionInstruction(paramCount);
	}
	
	public int getParamCount() {
		return paramCount;
	}
	
	public void execute(Runtime runtime) throws ExecutionException {
		List<Instruction> instructions = runtime.getInstructionsUpTo(StartFunctionInstruction.class, EndFunctionInstruction.class);
		FunctionValue newFunction = new FunctionValue(runtime.getScope(), paramCount, instructions);
		runtime.getStack().push(newFunction, true);
	}
	
	public void undo(Runtime runtime) {
	}
	
	public String toString() {
		return "STARTFUNCTION: " + paramCount;
	}
}