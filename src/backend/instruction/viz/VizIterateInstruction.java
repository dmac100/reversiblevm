package backend.instruction.viz;

import backend.instruction.Instruction;
import backend.instruction.operator.AndInstruction;
import backend.runtime.NonGlobalScope;
import backend.runtime.Runtime;
import backend.runtime.StackFrame;
import backend.runtime.UndoStack;
import backend.value.ArrayValue;
import backend.value.Value;

public class VizIterateInstruction extends Instruction {
	private final String name;
	private final int offset;
	
	public VizIterateInstruction(String name) {
		this(name, 0);
	}
	
	public VizIterateInstruction(String name, int offset) {
		this.name = name;
		this.offset = offset;
	}
	
	public static Instruction VizIterateInstruction(String name) {
		return new VizIterateInstruction(name);
	}
	
	public Instruction copy() {
		return new VizIterateInstruction(name, offset);
	}
	
	public String getName() {
		return name;
	}
	
	public void execute(Runtime runtime) {
		ArrayValue array = runtime.checkArrayValue(runtime.getStack().peekValue(0));
		Value value = array.pop();
		if(value == null) {
			runtime.getStack().popValue(true, false);
			StackFrame stackFrame = runtime.getCurrentStackFrame();
			stackFrame.setInstructionCounter(stackFrame.getInstructionCounter() + offset - 1);
		} else {
			runtime.getScope().create(name);
			runtime.getScope().set(name, value);
		}
	}
	
	public void undo(Runtime runtime) {
	}
	
	public String toString() {
		return super.toString() + "VIZITERATE: " + name + ", " + offset;
	}
}
