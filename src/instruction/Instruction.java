package instruction;

import runtime.Runtime;

public interface Instruction {
	public void execute(Runtime runtime);
}