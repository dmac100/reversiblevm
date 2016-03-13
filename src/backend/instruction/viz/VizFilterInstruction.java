package backend.instruction.viz;

import backend.instruction.Instruction;
import backend.instruction.operator.UnsignedShiftRightInstruction;
import backend.runtime.Runtime;
import backend.runtime.StackFrame;
import backend.value.BooleanValue;

public class VizFilterInstruction extends Instruction {
	private final int offset;
	
	public VizFilterInstruction(int offset) {
		this.offset = offset;
	}
	
	public static Instruction VizFilterInstruction() {
		return new VizFilterInstruction(0);
	}
	
	public Instruction copy() {
		return new VizFilterInstruction(offset);
	}
	
	public void execute(Runtime runtime) {
		runtime.checkBooleanValue(runtime.getStack().peekValue(0));
		BooleanValue value = runtime.checkBooleanValue(runtime.getStack().popValue(false, true));
		if(!value.getValue()) {
			StackFrame stackFrame = runtime.getCurrentStackFrame();
			stackFrame.setInstructionCounter(stackFrame.getInstructionCounter() + offset - 1);
		}
	}
	
	public void undo(Runtime runtime) {
		runtime.getUndoStack().undoPopValue(runtime);
	}
	
	public String toString() {
		return super.toString() + "VIZFILTER: " + offset;
	}
}
