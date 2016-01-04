package parser;

import static instruction.Dup2Instruction.Dup2;
import static instruction.DupInstruction.Dup;
import static instruction.Swap2Instruction.Swap2;
import static instruction.SwapInstruction.Swap;
import static java.util.Collections.singletonList;
import static value.StringValue.Value;
import instruction.Dup2Instruction;
import instruction.GetElementInstruction;
import instruction.GetPropertyInstruction;
import instruction.Instruction;
import instruction.LoadInstruction;
import instruction.SetElementInstruction;
import instruction.SetPropertyInstruction;
import instruction.StoreInstruction;
import instruction.Swap2Instruction;
import instruction.SwapInstruction;

import java.util.ArrayList;
import java.util.List;

/**
 * Splits instructions for reading a variable into different parts required by an assignment operation.
 * Prefix: Loads any context object onto the stack.
 * Dup: Duplicates the context object if it exists.
 * Swap: Swaps the context object, if it exists, with the previous stack value.
 * Read: Reads the value.
 * Write: Writes the value.
 */
public class AssignmentInstructions {
	private final List<Instruction> prefix;
	private final List<Instruction> dup;
	private final List<Instruction> swap;
	private final List<Instruction> read;
	private final List<Instruction> write;
	
	public AssignmentInstructions(List<Instruction> instructions) {
		this.prefix = instructions.subList(0, instructions.size() - 1);
		this.dup = new ArrayList<>();
		this.swap = new ArrayList<>();
		this.read = singletonList(instructions.get(instructions.size() - 1));
		this.write = singletonList(convertToWrite(instructions.get(instructions.size() - 1)));
		if(!prefix.isEmpty()) {
			if(write.get(0) instanceof SetElementInstruction) {
				dup.add(Dup2());
				swap.add(Swap2());
			} else {
				dup.add(Dup());
				swap.add(Swap());
			}
		}
	}
	
	public List<Instruction> getPrefix() {
		return prefix;
	}
	
	public List<Instruction> getDup() {
		return dup;
	}
	
	public List<Instruction> getSwap() {
		return swap;
	}
	
	public List<Instruction> getRead() {
		return read;
	}
	
	public List<Instruction> getWrite() {
		return write;
	}
	
	/**
	 * Returns a read instruction converted to the corresponding write instruction.
	 */
	private static Instruction convertToWrite(Instruction instruction) {
		if(instruction instanceof LoadInstruction) {
			return new StoreInstruction(Value(((LoadInstruction)instruction).getName()));
		} else if(instruction instanceof GetPropertyInstruction) {
			return new SetPropertyInstruction(Value(((GetPropertyInstruction)instruction).getName()));
		} else if(instruction instanceof GetElementInstruction) {
			return new SetElementInstruction();
		}
		return instruction;
	}
}
