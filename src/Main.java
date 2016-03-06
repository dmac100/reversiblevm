import java.util.List;

import backend.instruction.Instruction;
import backend.runtime.Engine;

public class Main {
	public static void main(String[] args) {
		long startTime = System.currentTimeMillis();
		List<Instruction> instructions = Engine.parseFile("/runtime/main.js");
		new Engine(instructions).run();
		System.out.println("Done in: " + (System.currentTimeMillis() - startTime));
	}
}