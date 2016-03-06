package backend.instruction;

import backend.runtime.ExecutionException;
import backend.runtime.Runtime;

public interface Instruction {
	public void execute(Runtime runtime) throws ExecutionException;
	public void undo(Runtime runtime);
}