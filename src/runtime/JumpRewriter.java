package runtime;

import instruction.Instruction;
import instruction.JumpIfFalseInstruction;
import instruction.JumpIfTrueInstruction;
import instruction.JumpInstruction;
import instruction.LabelInstruction;
import instruction.LabeledJumpIfFalseInstruction;
import instruction.LabeledJumpIfTrueInstruction;
import instruction.LabeledJumpInstruction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Allows converting of instruction lists between offset jumps and labeled jumps.
 */
public class JumpRewriter {
	/**
	 * Converts a list with offset jump instructions into labeled jump instructions.
	 */
	public static List<Instruction> convertToLabelledJumps(List<Instruction> instructions) {
		Map<Integer, List<String>> jumpLabels = getJumpLabels(instructions);
		return createLabeledList(instructions, jumpLabels);
	}
	
	/**
	 * Returns a list of instructions containing labeled jumps based on the jump labels. 
	 */
	private static List<Instruction> createLabeledList(List<Instruction> instructions, Map<Integer, List<String>> jumpLabels) {
		List<Instruction> newList = new ArrayList<>();
		
		int count = 1;
		for(int i = 0; i <= instructions.size(); i++) {
			if(jumpLabels.containsKey(i)) {
				for(String label:jumpLabels.get(i)) {
					newList.add(new LabelInstruction(label));
				}
			}
			
			if(i < instructions.size()) {
				Instruction labeledInstruction = getLabeledInstruction(instructions.get(i), String.valueOf(count));
				if(labeledInstruction == null) {
					newList.add(instructions.get(i));
				} else {
					newList.add(labeledInstruction);
					count++;
				}
			}
		}
		
		return newList;
	}

	/**
	 * Creates a list of labels for each position targeted by offset jump instructions in instructions.
	 */
	private static Map<Integer, List<String>> getJumpLabels(List<Instruction> instructions) {
		Map<Integer, List<String>> labels = new HashMap<>();

		int count = 1;
		for(int i = 0; i < instructions.size(); i++) {
			int offset = getOffset(instructions.get(i));
			if(offset != 0) {
				if(!labels.containsKey(i + offset)) {
					labels.put(i + offset, new ArrayList<String>());
				}
				labels.get(i + offset).add(String.valueOf(count));
				count++;
			}
		}
		
		return labels;
	}
	
	/**
	 * Converts a list with labeled jump instructions into offset jump instructions.
	 */
	public static List<Instruction> convertToOffsetJumps(List<Instruction> instructions) {
		Map<String, Integer> labelPositions = getLabelPositions(instructions);
		return createOffsetList(instructions, labelPositions);
	}
	
	/**
	 * Returns a list of instructions containing offset jumps based on the label positions.
	 */
	private static List<Instruction> createOffsetList(List<Instruction> instructions, Map<String, Integer> labelPositions) {
		List<Instruction> newList = new ArrayList<>();
		
		int offset = 0;
		for(int i = 0; i < instructions.size(); i++) {
			if(instructions.get(i) instanceof LabelInstruction) {
				offset++;
			} else {
				String label = getLabel(instructions.get(i));
				if(label == null) {
					newList.add(instructions.get(i));
				} else {
					int jumpOffset = labelPositions.get(label) - i + offset;
					newList.add(getOffsetInstruction(instructions.get(i), jumpOffset));
				}
			}
		}
		
		return newList;
	}

	/**
	 * Finds the position that each label would represent if all labels were removed from instructions.
	 */
	private static Map<String, Integer> getLabelPositions(List<Instruction> instructions) {
		Map<String, Integer> labels = new HashMap<>();
		
		int offset = 0;
		for(int i = 0; i < instructions.size(); i++) {
			if(instructions.get(i) instanceof LabelInstruction) {
				String label = ((LabelInstruction)instructions.get(i)).getLabel();
				labels.put(label, i - offset);
				offset++;
			}
		}
		
		return labels;
	}
	
	/**
	 * Returns the target label of instruction if it has one, otherwise returns null.
	 */
	private static String getLabel(Instruction instruction) {
		if(instruction instanceof LabeledJumpInstruction) {
			return ((LabeledJumpInstruction)instruction).getLabel();
		} else if(instruction instanceof LabeledJumpIfTrueInstruction) {
			return ((LabeledJumpIfTrueInstruction)instruction).getLabel();
		} else if(instruction instanceof LabeledJumpIfFalseInstruction) {
			return ((LabeledJumpIfFalseInstruction)instruction).getLabel();
		}
		return null;
	}
	
	/**
	 * Returns the jump offset of instruction if it has one, otherwise returns 0.
	 */
	private static int getOffset(Instruction instruction) {
		if(instruction instanceof JumpInstruction) {
			return ((JumpInstruction)instruction).getOffset();
		} else if(instruction instanceof JumpIfTrueInstruction) {
			return ((JumpIfTrueInstruction)instruction).getOffset();
		} else if(instruction instanceof JumpIfFalseInstruction) {
			return ((JumpIfFalseInstruction)instruction).getOffset();
		}
		return 0;
	}
	
	/**
	 * Returns the labeled jump equivalent of instruction it it has one, otherwise returns null.
	 */
	private static Instruction getLabeledInstruction(Instruction instruction, String label) {
		if(instruction instanceof JumpInstruction) {
			return new LabeledJumpInstruction(label);
		} else if(instruction instanceof JumpIfTrueInstruction) {
			return new LabeledJumpIfTrueInstruction(label);
		} else if(instruction instanceof JumpIfFalseInstruction) {
			return new LabeledJumpIfFalseInstruction(label);
		}
		return null;
	}
	
	/**
	 * Returns the offset jump equivalent of instruction if it has one, otherwise returns null.
	 */
	private static Instruction getOffsetInstruction(Instruction instruction, int offset) {
		if(instruction instanceof LabeledJumpInstruction) {
			return new JumpInstruction(offset);
		} else if(instruction instanceof LabeledJumpIfTrueInstruction) {
			return new JumpIfTrueInstruction(offset);
		} else if(instruction instanceof LabeledJumpIfFalseInstruction) {
			return new JumpIfFalseInstruction(offset);
		}
		return null;
	}
}