package backend.runtime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import backend.instruction.Instruction;
import backend.runtime.OutputLine.OutputType;

public class EngineAsserts {
	public static void assertStackValue(String expected, String program) {
		assertStackValue(expected, Engine.compile(program));
	}
	
	public static void assertOutput(String expected, String program) {
		assertOutput(expected, Engine.compile(program));
	}
	
	public static void assertError(String expected, String program) {
		assertError(expected, Engine.compile(program));
	}
	
	public static void assertStackValue(String expected, List<Instruction> instructions) {
		Runtime runtime = new Runtime();
		new Engine(runtime, instructions).run();
		
		assertTrue(runtime.getOutput().isEmpty());
		assertEquals(expected, runtime.getStack().popValue(false, false).toString());
		assertTrue(runtime.getStack().isEmpty());
	}
	
	public static void assertOutput(String expected, List<Instruction> instructions) {
		Runtime runtime = new Runtime();
		new Engine(runtime, instructions).run();
		
		assertEquals(expected, StringUtils.join(filterStandard(runtime.getOutput()), "\n"));
		assertTrue(filterError(runtime.getOutput()).isEmpty());
		assertTrue(runtime.getStack().isEmpty());
	}
	
	public static void assertError(String expected, List<Instruction> instructions) {
		Runtime runtime = new Runtime();
		new Engine(runtime, instructions).run();
		
		assertEquals(expected, filterError(runtime.getOutput()).get(0).getText());
		assertTrue(filterStandard(runtime.getOutput()).isEmpty());
	}
	
	private static List<OutputLine> filterStandard(List<OutputLine> output) {
		List<OutputLine> filtered = new ArrayList<>();
		for(OutputLine line:output) {
			if(line.getType() == OutputType.STANDARD) {
				filtered.add(line);
			}
		}
		return filtered;
	}
	
	private static List<OutputLine> filterError(List<OutputLine> output) {
		List<OutputLine> filtered = new ArrayList<>();
		for(OutputLine line:output) {
			if(line.getType() == OutputType.ERROR) {
				filtered.add(line);
			}
		}
		return filtered;
	}
}
