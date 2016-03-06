package backend.parser;

import static backend.instruction.stack.Dup2Instruction.Dup2;
import static backend.instruction.stack.DupInstruction.Dup;
import static backend.instruction.stack.PopInstruction.Pop;
import static backend.instruction.stack.PushInstruction.Push;
import static backend.instruction.stack.Swap2Instruction.Swap2;
import static backend.value.NullValue.NullValue;
import static java.util.Collections.singletonList;

import java.util.Arrays;
import java.util.List;

import backend.instruction.Instruction;
import backend.instruction.array.GetElementInstruction;
import backend.instruction.object.GetPropertyInstruction;

/**
 * Splits instructions for loading a function into different parts required by a call operation.
 * Prefix: Loads any context object onto the stack, leaving the value of 'this' below it.
 * Read: Reads the function value.
 */
public class CallableInstructions {
	private final List<Instruction> prefix;
	private final List<Instruction> read;
	
	public CallableInstructions(Instructions instructions) {
		this(instructions.getInstructions());
	}
	
	public CallableInstructions(List<Instruction> instructions) {
		this.prefix = instructions.subList(0, instructions.size() - 1);
		this.read = singletonList(instructions.get(instructions.size() - 1));
		if(!prefix.isEmpty()) {
			if(read.get(0) instanceof GetElementInstruction) {
				prefix.addAll(Arrays.asList(Dup2(), Pop(), Swap2()));
				return;
			} else if(read.get(0) instanceof GetPropertyInstruction) {
				prefix.add(Dup());
				return;
			}
		}
		
		prefix.addAll(0, Arrays.asList(Push(NullValue())));
	}
	
	public List<Instruction> getPrefix() {
		return prefix;
	}
	
	public List<Instruction> getRead() {
		return read;
	}
}
