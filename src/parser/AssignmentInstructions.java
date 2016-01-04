package parser;

import static instruction.DupInstruction.Dup;
import static java.util.Collections.singletonList;
import static value.StringValue.Value;
import instruction.GetPropertyInstruction;
import instruction.Instruction;
import instruction.LoadInstruction;
import instruction.SetPropertyInstruction;
import instruction.StoreInstruction;

import java.util.ArrayList;
import java.util.List;

/**
 * Splits instructions for reading a variable into different parts required by an assignment operation.
 * Prefix: Loads any context object onto the stack.
 * Dup: Duplicates the context object if it exists.
 * Read: Reads the value.
 * Write: Writes the value.
 */
public class AssignmentInstructions {
	private final List<Instruction> prefix;
	private final List<Instruction> dup;
	private final List<Instruction> read;
	private final List<Instruction> write;
	
	public AssignmentInstructions(List<Instruction> instructions) {
		this.prefix = instructions.subList(0, instructions.size() - 1);
		this.dup = new ArrayList<>();
		this.read = singletonList(instructions.get(instructions.size() - 1));
		this.write = singletonList(convertToWrite(instructions.get(instructions.size() - 1)));
		if(!prefix.isEmpty()) {
			dup.add(Dup());
		}
	}
	
	public List<Instruction> getPrefix() {
		return prefix;
	}
	
	public List<Instruction> getDup() {
		return dup;
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
		}
		return instruction;
	}
}
