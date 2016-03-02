package runtime;

import gnu.trove.list.array.TIntArrayList;

import instruction.Instruction;

import java.util.ArrayList;
import java.util.List;

import value.FunctionValue;

public class UndoStack {
	public final static int POPSTACKFRAME = -1;
	public final static Runnable UNDOPOINT = null;
	
	private final List<Runnable> commands = new ArrayList<>();
	private final TIntArrayList instructionCounters = new TIntArrayList();
	private final List<StackFrame> popStackFrames = new ArrayList<>();
	
	public void addCommandUndo(Runnable command) {
		commands.add(command);
	}
	
	public void addInstructionCounterUndo(int instructionCounter) {
		instructionCounters.add(instructionCounter);
	}
	
	public void addPopStackFrameUndo(StackFrame stackFrame) {
		popStackFrames.add(stackFrame);
	}

	public void undo(Runtime runtime) {
		if(!instructionCounters.isEmpty()) {
			// Undo any pop stack frame.
			if(instructionCounters.get(instructionCounters.size() - 1) == POPSTACKFRAME) {
				instructionCounters.removeAt(instructionCounters.size() - 1);
				StackFrame stackFrame = popStackFrames.remove(popStackFrames.size() - 1);
				runtime.addStackFrame(stackFrame, false);
			}
			
			int instructionCounter = instructionCounters.get(instructionCounters.size() - 1);
			
			// Undo instruction.
			StackFrame stackFrame = runtime.getCurrentStackFrame();
			if(stackFrame != null) {
				if(instructionCounter < stackFrame.getFunction().getInstructions().size()) {
					Instruction instruction = stackFrame.getFunction().getInstructions().get(instructionCounter);
					instruction.undo(runtime);
				}
			}
		}
		
		// Undo commands.
		while(!commands.isEmpty()) {
			Runnable command = commands.remove(commands.size() - 1);
			if(command == UNDOPOINT) {
				break;
			}
			command.run();
		}
		
		// Undo instruction counter changes.
		if(!instructionCounters.isEmpty()) {
			int instructionCounter = instructionCounters.removeAt(instructionCounters.size() - 1);
			if(runtime.getCurrentStackFrame() != null) {
				runtime.getCurrentStackFrame().setInstructionCounter(instructionCounter);
			}
		}
	}

	public void saveUndoPoint() {
		commands.add(UNDOPOINT);
	}
}
