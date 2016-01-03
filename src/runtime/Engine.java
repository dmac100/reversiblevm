package runtime;

import instruction.EndFunctionInstruction;
import instruction.Instruction;
import instruction.StartFunctionInstruction;

import java.util.List;

import value.FunctionValue;

public class Engine {
	public void run(List<Instruction> instructions) {
		run(new Runtime(), instructions);
	}
	
	public void run(Runtime runtime, List<Instruction> instructions) {
		FunctionValue mainFunction = new FunctionValue(new GlobalScope());
		for(Instruction instruction:instructions) {
			mainFunction.addInstruction(instruction);
		}
		runtime.addStackFrame(mainFunction);
		run(runtime);
	}
	
	public void run(Runtime runtime) {
		while(true) {
			StackFrame frame = runtime.getCurrentStackFrame();
			if(frame == null) {
				return;
			}
			FunctionValue function = frame.getFunction();
			
			if(frame.getInstructionCounter() >= function.getInstructions().size()) {
				runtime.popStackFrame();
				continue;
			}
			
			Instruction instruction = function.getInstructions().get(frame.getInstructionCounter());
			
			//System.out.println(frame + ":" + frame.getInstructionCounter() + " - INSTRUCTION: " + instruction);
			
			if(runtime.getNestedFunctionDefinitionCount() == 0) {
				execute(runtime, instruction);
			} else {
				if(instruction instanceof StartFunctionInstruction || instruction instanceof EndFunctionInstruction) {
					execute(runtime, instruction);
				} else {
					runtime.getCurrentFunctionDefinition().addInstruction(instruction);
				}
			}
			
			frame.setInstructionCounter(frame.getInstructionCounter() + 1);
		}
	}

	private void execute(Runtime runtime, Instruction instruction) {
		try {
			instruction.execute(runtime);
			System.out.println(runtime.getNestedFunctionDefinitionCount() + ":" + runtime.getCurrentStackFrame().getInstructionCounter() + " - EXECUTING: " + instruction + " - " + runtime.getStack() + " - " + runtime.getScope());
		} catch (ExecutionException e) {
			System.out.println(runtime.getNestedFunctionDefinitionCount() + ":" + runtime.getCurrentStackFrame().getInstructionCounter() + " - EXECUTING: " + instruction + " - " + runtime.getStack() + " - " + runtime.getScope());
			System.err.println("Error: " + e.getMessage());
			runtime.getErrors().add(e.getMessage());
			return;
		} catch (Exception e) {
			System.out.println(runtime.getNestedFunctionDefinitionCount() + ":" + runtime.getCurrentStackFrame().getInstructionCounter() + " - EXECUTING: " + instruction + " - " + runtime.getStack() + " - " + runtime.getScope());
			throw e;
		}
	}
}