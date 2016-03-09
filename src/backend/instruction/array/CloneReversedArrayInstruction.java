package backend.instruction.array;

import java.util.Collections;
import java.util.List;

import backend.instruction.Instruction;
import backend.instruction.stack.DupInstruction;
import backend.runtime.Runtime;
import backend.value.ArrayValue;
import backend.value.Value;

public class CloneReversedArrayInstruction extends Instruction {
	public CloneReversedArrayInstruction() {
	}
	
	public static Instruction CloneReversedArrayInstruction() {
		return new CloneReversedArrayInstruction();
	}
	
	public Instruction copy() {
		return new CloneReversedArrayInstruction();
	}
	
	public void execute(Runtime runtime) {
		runtime.checkArrayValue(runtime.getStack().peekValue(0));
		ArrayValue array = runtime.checkArrayValue(runtime.getStack().popValue(true, false));
		
		List<Value> values = array.values(runtime);
		Collections.reverse(values);
		ArrayValue arrayClone = new ArrayValue(values, runtime.getUndoStack());
		
		runtime.getStack().push(arrayClone, false);
	}
	
	public void undo(Runtime runtime) {
		runtime.getStack().popValue(false, false);
	}
	
	public String toString() {
		return "CLONEREVERSEDARRAY";
	}
}
