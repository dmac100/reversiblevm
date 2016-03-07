package backend.instruction.function;

import java.util.List;

import backend.instruction.Instruction;
import backend.runtime.ExecutionException;
import backend.runtime.Runtime;
import backend.value.FunctionValue;

public class StartFunctionInstruction extends Instruction {
	private final int paramCount;
	private List<Instruction> cachedInstructions = null;
	private int cachedInstructionCounter = 0;

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
		// Each function created from the same instruction should define the same instructions.
		if(cachedInstructions == null) {
			List<Instruction> instructions = runtime.getInstructionsUpTo(StartFunctionInstruction.class, EndFunctionInstruction.class);
			cachedInstructions = instructions;
			cachedInstructionCounter = runtime.getCurrentStackFrame().getInstructionCounter();
		} else {
			runtime.getCurrentStackFrame().setInstructionCounter(cachedInstructionCounter);
		}
		
		FunctionValue newFunction = new FunctionValue(runtime.getScope(), paramCount, cachedInstructions);
		runtime.getStack().push(newFunction, true);
	}
	
	public void undo(Runtime runtime) {
	}
	
	public String toString() {
		return "STARTFUNCTION: " + paramCount;
	}
}