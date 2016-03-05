package runtime;

import gnu.trove.list.array.TDoubleArrayList;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.list.array.TShortArrayList;
import instruction.Instruction;
import instruction.viz.EndVizInstruction;
import instruction.viz.StartVizInstruction;

import java.util.ArrayList;
import java.util.List;

import value.BooleanValue;
import value.DoubleValue;
import value.NullValue;
import value.Value;

public class UndoStack {
	public final static int POPSTACKFRAME = -1;
	public final static Runnable UNDOPOINT = null;
	
	private final short DOUBLETYPE = 0;
	private final short BOOLEANTRUETYPE = 1;
	private final short BOOLEANFALSETYPE = 2;
	private final short NULLTYPE = 3;
	private final short GENERALTYPE = 4;
	
	private final List<Runnable> commandUndos = new ArrayList<>();
	private final TIntArrayList instructionCounterUndos = new TIntArrayList();
	private final List<StackFrame> popStackFrameUndos = new ArrayList<>();
	
	private final List<Value> popValueUndos = new ArrayList<>();
	private final TDoubleArrayList popDoubleValueUndos = new TDoubleArrayList();
	private final TShortArrayList popValueUndoTypes = new TShortArrayList();
	
	private final boolean undoEnabled;
	
	public UndoStack(boolean undoEnabled) {
		this.undoEnabled = undoEnabled;
	}
	
	public void addCommandUndo(Runnable command) {
		if(!undoEnabled) return;
		
		commandUndos.add(command);
	}
	
	public void addInstructionCounterUndo(int instructionCounter) {
		if(!undoEnabled) return;
		
		instructionCounterUndos.add(instructionCounter);
	}
	
	public void addPopStackFrameUndo(StackFrame stackFrame) {
		if(!undoEnabled) return;
		
		popStackFrameUndos.add(stackFrame);
	}
	
	public void addPopValueUndo(Value value) {
		if(!undoEnabled) return;
		
		if(value instanceof DoubleValue) {
			popDoubleValueUndos.add(((DoubleValue)value).getValue());
			popValueUndoTypes.add(DOUBLETYPE);
		} else if(value instanceof BooleanValue) {
			popValueUndoTypes.add(((BooleanValue)value).getValue() ? BOOLEANTRUETYPE : BOOLEANFALSETYPE);
		} else if(value instanceof NullValue) {
			popValueUndoTypes.add(NULLTYPE);
		} else {
			popValueUndoTypes.add(GENERALTYPE);
			popValueUndos.add(value);
		}
	}
	
	public void undoPopValue(Runtime runtime) {
		if(!undoEnabled) return;
		
		short type = popValueUndoTypes.removeAt(popValueUndoTypes.size() - 1);
		
		final Value value;
		if(type == DOUBLETYPE) {
			value = new DoubleValue(popDoubleValueUndos.removeAt(popDoubleValueUndos.size() - 1));
		} else if(type == BOOLEANTRUETYPE) {
			value = new BooleanValue(true);
		} else if(type == BOOLEANFALSETYPE) {
			value = new BooleanValue(false);
		} else if(type == NULLTYPE) {
			value = new NullValue();
		} else {
			value = popValueUndos.remove(popValueUndos.size() - 1);
		}
		
		runtime.getStack().push(value, false);
	}

	public void undo(Runtime runtime) {
		if(!undoEnabled) return;
		
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
		undoCommands();
		
		// Undo instruction counter changes.
		if(!instructionCounterUndos.isEmpty()) {
			int instructionCounter = instructionCounterUndos.removeAt(instructionCounterUndos.size() - 1);
			if(runtime.getCurrentStackFrame() != null) {
				runtime.getCurrentStackFrame().setInstructionCounter(instructionCounter);
			}
		}
	}

	public void undoCommands() {
		while(!commandUndos.isEmpty()) {
			Runnable command = commandUndos.remove(commandUndos.size() - 1);
			if(command == UNDOPOINT) {
				break;
			}
			command.run();
		}
	}

	public void saveUndoPoint() {
		if(!undoEnabled) return;
		
		commandUndos.add(UNDOPOINT);
	}
	
	public int getSize() {
		int size = 0;
		size += commandUndos.size();
		size += instructionCounterUndos.size();
		size += popStackFrameUndos.size();
		size += popValueUndos.size();
		size += popDoubleValueUndos.size();
		size += popValueUndoTypes.size();
		return size;
	}
}
