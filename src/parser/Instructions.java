package parser;

import instruction.Instruction;

import java.util.ArrayList;
import java.util.List;

public class Instructions {
	private final Instructions[] children;
	private final Instruction instruction;

	private Instructions() {
		this.instruction = null;
		this.children = new Instructions[0];
	}
	
	private Instructions(Instruction instruction) {
		this.instruction = instruction;
		this.children = null;
	}
	
	private Instructions(List<Instruction> children) {
		this.instruction = null;
		this.children = new Instructions[children.size()];
		for(int i = 0; i < children.size(); i++) {
			this.children[i] = new Instructions(children.get(i));
		}
		
	}
	
	private Instructions(Instructions... children) {
		this.instruction = null;
		this.children = children;
	}
	
	private Instructions(Instruction... children) {
		this.instruction = null;
		this.children = new Instructions[children.length];
		for(int i = 0; i < children.length; i++) {
			this.children[i] = new Instructions(children[i]);
		}
	}
	
	public static Instructions Instructions() {
		return new Instructions();
	}
	
	public static Instructions Instructions(List<Instruction> instruction) {
		return new Instructions(instruction);
	}
	
	public static Instructions Instructions(Instruction... instruction) {
		return new Instructions(instruction);
	}
	
	public static Instructions Instructions(Instruction instruction) {
		return new Instructions(instruction);
	}
	
	public static Instructions Instructions(Instructions... children) {
		return new Instructions(children);
	}
	
	public List<Instruction> getInstructions() {
		List<Instruction> instructions = new ArrayList<>();
		getInstructions(instructions);
		return instructions;
	}

	private void getInstructions(List<Instruction> instructions) {
		if(instruction == null) {
			for(Instructions child:children) {
				child.getInstructions(instructions);
			}
		} else {
			instructions.add(instruction);
		}
	}

	public int size() {
		int size = 0;
		if(instruction == null) {
			for(Instructions child:children) {
				size += child.size();
			}
		} else {
			size += 1;
		}
		return size;
	}
	
	public String toString() {
		return getInstructions().toString();
	}
}
