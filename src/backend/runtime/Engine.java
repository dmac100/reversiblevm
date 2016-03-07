package backend.runtime;

import static org.parboiled.errors.ErrorUtils.printParseErrors;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.parboiled.BaseParser;
import org.parboiled.Parboiled;
import org.parboiled.parserunners.ReportingParseRunner;
import org.parboiled.support.ParsingResult;

import backend.instruction.Instruction;
import backend.instruction.function.EndFunctionInstruction;
import backend.instruction.function.StartFunctionInstruction;
import backend.instruction.viz.EndVizInstruction;
import backend.instruction.viz.StartVizInstruction;
import backend.parser.Instructions;
import backend.parser.Parser;
import backend.value.FunctionValue;

public class Engine {
	private static final Parser parser = Parboiled.createParser(Parser.class);
	private static final List<Instruction> includeInstructions = parseFile("/backend/runtime/include.js");
	
	private final Runtime runtime;
	private final List<Instruction> instructions;
	
	public Engine(List<Instruction> instructions) {
		this(new Runtime(), instructions);
	}
	
	public Engine(Runtime runtime, List<Instruction> instructions) throws ExecutionException {
		this.runtime = runtime;
		this.instructions = instructions;
		
		GlobalScope globalScope = new GlobalScope(runtime.getUndoStack());
		
		runtime.addStackFrame(new FunctionValue(globalScope, 0, includeInstructions));
		run();
		
		runtime.getUndoStack().clear();
		
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
	
	public void stepForward() {
		if(runtime.getCurrentStackFrame() == null) return;
		
		runtime.getUndoStack().saveUndoPoint();
		runtime.getUndoStack().addInstructionCounterUndo(runtime.getCurrentStackFrame().getInstructionCounter());
		
		StackFrame frame = runtime.getCurrentStackFrame();
		FunctionValue function = frame.getFunction();
		
		if(frame.getInstructionCounter() >= function.getInstructions().size()) {
			runtime.popStackFrame();
			return;
		}

		do {
			Instruction instruction = function.getInstructions().get(frame.getInstructionCounter());
			
			if(runtime.isInVizInstruction()) {
				if(instruction instanceof StartVizInstruction || instruction instanceof EndVizInstruction) {
					execute(instruction);
				} else {
					runtime.getCurrentVizInstructions().add(instruction);
				}
			} else {
				execute(instruction);
			}
			
			frame.setInstructionCounter(frame.getInstructionCounter() + 1);
		} while(runtime.isInVizInstruction());
	}
	
	public void stepBackward() {
		runtime.getUndoStack().undo(runtime);
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
		if(result.valueStack.size() != 1) {
			throw new CompileException("Invalid value stack size: " + printParseErrors(parseRunner.getParseErrors()));
		}
		List<Instruction> instructions = result.valueStack.pop().getInstructions();
		instructions = new Optimizer().optimize(instructions);
		return instructions;
	}
}