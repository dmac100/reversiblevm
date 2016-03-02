package instruction;

import runtime.ExecutionException;
import runtime.Runtime;

public interface Instruction {
	public void execute(Runtime runtime) throws ExecutionException;
	public void undo(Runtime runtime);
}