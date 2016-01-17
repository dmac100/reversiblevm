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
	public void run(List<Instruction> instructions) {
		run(new Runtime(), instructions);
	}
	
	public void run(Runtime runtime, List<Instruction> instructions) {
		try {
			GlobalScope globalScope = new GlobalScope();
			
			FunctionValue includeFunction = new FunctionValue(globalScope, 0, parseFile("/runtime/include.js"));
			runtime.addStackFrame(includeFunction);
			run(runtime);
			
			FunctionValue mainFunction = new FunctionValue(globalScope, 0, instructions);
			runtime.addStackFrame(mainFunction);
			run(runtime);
		} catch(ExecutionException e) {
			System.err.println("Error: " + e.getMessage());
			runtime.getErrors().add(e.getMessage());
		}
	}
	
	private void run(Runtime runtime) throws ExecutionException {
		while(true) {
			StackFrame frame = runtime.getCurrentStackFrame();
			if(frame == null) {
				return;
			}
			FunctionValue function = frame.getFunction();
			
			if(frame.getInstructionCounter() >= function.getInstructions().size()) {
				runtime.popStackFrame();
				continue;
			}
			
			Instruction instruction = function.getInstructions().get(frame.getInstructionCounter());
			
			//System.out.println(frame + ":" + frame.getInstructionCounter() + " - INSTRUCTION: " + instruction);
			
			if(runtime.getNestedFunctionDefinitionCount() == 0) {
				execute(runtime, instruction);
			} else {
				if(instruction instanceof StartFunctionInstruction || instruction instanceof EndFunctionInstruction) {
					execute(runtime, instruction);
				} else {
					runtime.getCurrentFunctionDefinition().addInstruction(instruction);
				}
			}
			
			frame.setInstructionCounter(frame.getInstructionCounter() + 1);
		}
	}

	private void execute(Runtime runtime, Instruction instruction) throws ExecutionException {
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
		Parser parser = Parboiled.createParser(Parser.class);
		ReportingParseRunner<Instructions> parseRunner = new ReportingParseRunner<Instructions>(parser.Sequence(parser.Program(), BaseParser.EOI));
		ParsingResult<Instructions> result = parseRunner.run(program);
		if(result.valueStack.size() != 1) throw new CompileException("Invalid value stack size: " + result.valueStack.size());
		return result.valueStack.pop().getInstructions();
	}
}