package runtime;

import instruction.Instruction;

import java.util.List;

public class Engine {
	public void run(List<Instruction> instructions) {
		run(new Runtime(), instructions);
	}
	
	public void run(Runtime runtime, List<Instruction> instructions) {
		for(Instruction instruction:instructions) {
			try {
				instruction.execute(runtime);
			} catch (ExecutionException e) {
				System.err.println("Error: " + e.getMessage());
				runtime.getErrors().add(e.getMessage());
				return;
			}
		}
	}
}