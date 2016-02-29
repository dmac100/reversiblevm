import instruction.Instruction;

import java.util.List;

import runtime.Engine;

public class Main {
	public static void main(String[] args) {
		List<Instruction> instructions = Engine.parseFile("/runtime/main.js");
		new Engine(instructions).run();
	}
}
