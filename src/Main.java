import instruction.Instruction;

import java.util.List;

import org.parboiled.BaseParser;
import org.parboiled.Parboiled;
import org.parboiled.parserunners.ReportingParseRunner;
import org.parboiled.support.ParsingResult;

import parser.Parser;
import runtime.Engine;

public class Main {
	public static void main(String[] args) {
		String program = "print('Hello World!');";
		
		Parser parser = Parboiled.createParser(Parser.class);
		
		ParsingResult<List<Instruction>> result = new ReportingParseRunner<List<Instruction>>(parser.Sequence(parser.Program(), BaseParser.EOI)).run(program);
		
		List<Instruction> instructions = result.valueStack.pop();
		
		new Engine().run(instructions);
	}
}
