package parser;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.parboiled.BaseParser;
import org.parboiled.Parboiled;
import org.parboiled.parserunners.ReportingParseRunner;
import org.parboiled.support.ParseTreeUtils;
import org.parboiled.support.ParsingResult;

public class ParserOutputTest {
	private Parser parser = Parboiled.createParser(Parser.class);
	
	@Test
	public void CallExpression() {
		assertParseOutput("print();", Arrays.asList(
			"PUSH: 0",
			"LOAD: print",
			"CALL",
			"POP"
		));
		assertParseOutput("print('Hello World!');", Arrays.asList(
			"PUSH: Hello World!",
			"PUSH: 1",
			"LOAD: print",
			"CALL",
			"POP"
		));
		assertParseOutput("print(1, 2);", Arrays.asList(
			"PUSH: 2",
			"PUSH: 1",
			"PUSH: 2",
			"LOAD: print",
			"CALL",
			"POP"
		));
	}
	
	private void assertParseOutput(String input, List<String> instructions) {
		ParsingResult<Object> result = new ReportingParseRunner<>(parser.Sequence(parser.Program(), BaseParser.EOI)).run(input);
		System.out.println(ParseTreeUtils.printNodeTree(result));
		
		String expected = instructions.toString();
		String actual = result.valueStack.pop().toString();
		
		System.out.println("EXPECTED: " + expected);
		System.out.println("  ACTUAL: " + actual);
		
		assertEquals(expected, actual);
	}
}
