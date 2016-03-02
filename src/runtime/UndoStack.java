package runtime;

import gnu.trove.list.array.TIntArrayList;
import instruction.Instruction;

import java.util.ArrayList;
import java.util.List;

import value.Value;

public class UndoStack {
	public final static int POPSTACKFRAME = -1;
	public final static Runnable UNDOPOINT = null;
	
	private final List<Runnable> commandUndos = new ArrayList<>();
	private final TIntArrayList instructionCounterUndos = new TIntArrayList();
	private final List<StackFrame> popStackFrameUndos = new ArrayList<>();
	private final List<Value> popValueUndos = new ArrayList<>();
	
	public void addCommandUndo(Runnable command) {
		commandUndos.add(command);
	}
	
	public void addInstructionCounterUndo(int instructionCounter) {
		instructionCounterUndos.add(instructionCounter);
	}
	
	public void addPopStackFrameUndo(StackFrame stackFrame) {
		popStackFrameUndos.add(stackFrame);
	}
	
	public void addPopValueUndo(Value value) {
		popValueUndos.add(value);
	}
	
	public void undoPopValue(Runtime runtime) {
		Value value = popValueUndos.remove(popValueUndos.size() - 1);
		runtime.getStack().push(value, false);
	}

	public void undo(Runtime runtime) {
		if(!instructionCounterUndos.isEmpty()) {
			// Undo any pop stack frame.
			if(instructionCounterUndos.get(instructionCounterUndos.size() - 1) == POPSTACKFRAME) {
				instructionCounterUndos.removeAt(instructionCounterUndos.size() - 1);
				StackFrame stackFrame = popStackFrameUndos.remove(popStackFrameUndos.size() - 1);
				runtime.addStackFrame(stackFrame, false);
			}
			
			int instructionCounter = instructionCounterUndos.get(instructionCounterUndos.size() - 1);
			
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
		while(!commandUndos.isEmpty()) {
			Runnable command = commandUndos.remove(commandUndos.size() - 1);
			if(command == UNDOPOINT) {
				break;
			}
			command.run();
		}
		
		// Undo instruction counter changes.
		if(!instructionCounterUndos.isEmpty()) {
			int instructionCounter = instructionCounterUndos.removeAt(instructionCounterUndos.size() - 1);
			if(runtime.getCurrentStackFrame() != null) {
				runtime.getCurrentStackFrame().setInstructionCounter(instructionCounter);
			}
		}
	}

	public void saveUndoPoint() {
		commandUndos.add(UNDOPOINT);
	}
}
