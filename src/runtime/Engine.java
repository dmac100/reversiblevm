package runtime;

import instruction.EndFunctionInstruction;
import instruction.Instruction;
import instruction.StartFunctionInstruction;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.parboiled.BaseParser;
import org.parboiled.Parboiled;
import org.parboiled.parserunners.ReportingParseRunner;
import org.parboiled.support.ParsingResult;

import parser.Instructions;
import parser.Parser;
import value.FunctionValue;

public class Engine {
	private static final Parser parser = Parboiled.createParser(Parser.class);
	private static final List<Instruction> includeInstructions = parseFile("/runtime/include.js");
	
	private final Runtime runtime;
	private final List<Instruction> instructions;
	
	public Engine(List<Instruction> instructions) {
		this(new Runtime(), instructions);
	}
	
	public Engine(Runtime runtime, List<Instruction> instructions) throws ExecutionException {
		this.runtime = runtime;
		this.instructions = instructions;
		
		GlobalScope globalScope = new GlobalScope();
		
		runtime.addStackFrame(new FunctionValue(globalScope, 0, includeInstructions));
		run();
		
		runtime.addStackFrame(new FunctionValue(globalScope, 0, instructions));
	}
	
	public void run() {
		try {
			while(runtime.getCurrentStackFrame() != null) {
				stepForward();
			}
		} catch(ExecutionException e) {
			System.err.println("Error: " + e.getMessage());
			runtime.getErrors().add(e.getMessage());
		}
	}
	
	private void stepForward() {
		StackFrame frame = runtime.getCurrentStackFrame();
		FunctionValue function = frame.getFunction();
		
		if(frame.getInstructionCounter() >= function.getInstructions().size()) {
			runtime.popStackFrame();
			return;
		}
		
		Instruction instruction = function.getInstructions().get(frame.getInstructionCounter());
		
		if(runtime.getNestedFunctionDefinitionCount() == 0) {
			execute(instruction);
		} else {
			if(instruction instanceof StartFunctionInstruction || instruction instanceof EndFunctionInstruction) {
				execute(instruction);
			} else {
				runtime.getCurrentFunctionDefinition().addInstruction(instruction);
			}
		}
		
		frame.setInstructionCounter(frame.getInstructionCounter() + 1);
	}

	private void execute(Instruction instruction) throws ExecutionException {
		try {
			instruction.execute(runtime);
			//System.out.println(runtime.getNestedFunctionDefinitionCount() + ":" + runtime.getCurrentStackFrame().getInstructionCounter() + " - EXECUTING: " + instruction + " - " + runtime.getStack() + " - " + runtime.getScope());
		} catch (Exception e) {
			//System.out.println(runtime.getNestedFunctionDefinitionCount() + ":" + runtime.getCurrentStackFrame().getInstructionCounter() + " - EXECUTING: " + instruction + " - " + runtime.getStack() + " - " + runtime.getScope());
			throw e;
		}
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
		if(result.valueStack.size() != 1) throw new CompileException("Invalid value stack size: " + result.valueStack.size());
		List<Instruction> instructions = result.valueStack.pop().getInstructions();
		instructions = new Optimizer().optimize(instructions);
		return instructions;
	}
}