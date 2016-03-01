package runtime;

import gnu.trove.list.array.TIntArrayList;

import java.util.ArrayList;
import java.util.List;

public class UndoStack {
	private final List<Runnable> commands = new ArrayList<>();
	private final TIntArrayList instructionCounters = new TIntArrayList();

	public void addCommandUndo(Runnable command) {
		commands.add(command);
	}
	
	public void addInstructionCounterUndo(int instructionCounter) {
		instructionCounters.add(instructionCounter);
	}

	public void undo(Runtime runtime) {
		while(!commands.isEmpty()) {
			Runnable command = commands.remove(commands.size() - 1);
			if(command == null) {
				break;
			}
			command.run();
		}
		
		int instructionCounter = instructionCounters.removeAt(instructionCounters.size() - 1);
		if(runtime.getCurrentStackFrame() != null) {
			runtime.getCurrentStackFrame().setInstructionCounter(instructionCounter);
		}
	}

	public void saveUndoPoint() {
		commands.add(null);
	}
}
