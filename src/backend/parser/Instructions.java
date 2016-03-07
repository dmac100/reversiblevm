package backend.parser;

import java.util.ArrayList;
import java.util.List;

import backend.instruction.Instruction;

public class Instructions {
	private final Instructions[] children;
	private final Instruction instruction;

	public Instructions() {
		this.instruction = null;
		this.children = new Instructions[0];
	}
	
	public Instructions(Instruction instruction) {
		this.instruction = instruction;
		this.children = null;
	}
	
	public Instructions(List<Instruction> children) {
		this.instruction = null;
		this.children = new Instructions[children.size()];
		for(int i = 0; i < children.size(); i++) {
			this.children[i] = new Instructions(children.get(i));
		}
	}
	
	public Instructions(Instructions... children) {
		this.instruction = null;
		this.children = children;
	}
	
	public Instructions(Instruction... children) {
		this.instruction = null;
		this.children = new Instructions[children.length];
		for(int i = 0; i < children.length; i++) {
			this.children[i] = new Instructions(children[i]);
		}
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
