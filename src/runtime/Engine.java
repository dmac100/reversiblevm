package runtime;

import instruction.Instruction;

import java.util.List;

public class Engine {
	public void run(List<Instruction> instructions) {
		run(new Runtime(), instructions);
	}
	
	public void run(Runtime runtime, List<Instruction> instructions) {
		for(Instruction instruction:instructions) {
			instruction.execute(runtime);
		}
	}
}