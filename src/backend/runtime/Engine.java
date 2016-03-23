package backend.runtime;

import static org.parboiled.errors.ErrorUtils.printParseErrors;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.parboiled.BaseParser;
import org.parboiled.Parboiled;
import org.parboiled.parserunners.ReportingParseRunner;
import org.parboiled.support.ParsingResult;

import backend.instruction.Instruction;
import backend.parser.Instructions;
import backend.parser.Parser;
import backend.runtime.OutputLine.OutputType;
import backend.value.FunctionValue;

public class Engine {
	private static final Parser parser = Parboiled.createParser(Parser.class);
	private static final List<Instruction> includeInstructions = parseFile("/backend/runtime/library/include.js");
	
	private final Runtime runtime;
	private final List<Instruction> instructions;
	
	public Engine(List<Instruction> instructions) {
		this(new Runtime(), instructions);
	}
	
	public Engine(Runtime runtime, List<Instruction> instructions) throws ExecutionException {
		this.runtime = runtime;
		this.instructions = instructions;
		
		GlobalScope globalScope = new GlobalScope(runtime.getUndoStack());
		
		addIncludeFiles(globalScope);
		
		runtime.addStackFrame(new FunctionValue(globalScope, 0, instructions));
		
		runtime.getUndoStack().clear();
	}
	
	private void addIncludeFiles(GlobalScope globalScope) {
		for(Instruction instruction:includeInstructions) {
			instruction.setLineNumber((short) 0);
		}
		
		runtime.addStackFrame(new FunctionValue(globalScope, 0, includeInstructions));
		run();
	}

	public void run() {
		try {
			while(runtime.getCurrentStackFrame() != null) {
				stepForward();
			}
		} catch(ExecutionException e) {
			System.err.println("Error: " + e.getMessage());
			runtime.getOutput().add(new OutputLine(e.getMessage(), OutputType.ERROR));
		}
	}
	
	public void stepForward() {
		runtime.runNextInstruction();
	}
	
	public void stepBackward() {
		runtime.getUndoStack().undo(runtime);
	}

	public static List<Instruction> parseFile(String name) {
		try(InputStream inputStream = Engine.class.getResourceAsStream(name)) {
			return compile(IOUtils.toString(inputStream));
		} catch(IOException e) {
			throw new RuntimeException("Error reading file: " + name, e);
		}
	}
	
	public static List<Instruction> compile(String program) {
		ReportingParseRunner<Instructions> parseRunner = new ReportingParseRunner<Instructions>(parser.Sequence(parser.Program(), BaseParser.EOI));
		ParsingResult<Instructions> result = parseRunner.run(program);
		if(result.valueStack.size() != 1) {
			throw new CompileException("Invalid value stack size: " + printParseErrors(parseRunner.getParseErrors()));
		}
		List<Instruction> instructions = result.valueStack.pop().getInstructions();
		instructions = new Optimizer().optimize(instructions);
		instructions = Instruction.copyInstructions(instructions);
		for(int i = 0; i < instructions.size(); i++) {
			instructions.get(i).setInstructionNumber((short) i);
		}
		return instructions;
	}
}