package runtime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import instruction.Instruction;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.parboiled.BaseParser;
import org.parboiled.Parboiled;
import org.parboiled.parserunners.ReportingParseRunner;
import org.parboiled.support.ParseTreeUtils;
import org.parboiled.support.ParsingResult;

import parser.Parser;

public class LibraryTest {
	private Parser parser = Parboiled.createParser(Parser.class);
	
	@Test
	public void test() {
		assertOutput("0", "print(StringProto.length(''));");
		assertOutput("1", "print(StringProto.length('1'));");
		assertOutput("2", "print(StringProto.length('12'));");

		assertOutput("1", "print(StringProto.charAt('12', 0));");
		assertOutput("2", "print(StringProto.charAt('12', 1));");

		assertOutput("1234", "print(StringProto.concat('12', '34'));");

		assertOutput("true", "print(StringProto.endsWith('aaa', 'a'));");
		assertOutput("false", "print(StringProto.endsWith('aaa', 'b'));");

		assertOutput("0", "print(StringProto.indexOf('aaab', 'a'));");
		assertOutput("3", "print(StringProto.indexOf('aaab', 'b'));");
		assertOutput("-1", "print(StringProto.indexOf('aaab', 'c'));");

		assertOutput("2", "print(StringProto.lastIndexOf('aaab', 'a'));");
		assertOutput("3", "print(StringProto.lastIndexOf('aaab', 'b'));");
		assertOutput("-1", "print(StringProto.lastIndexOf('aaab', 'c'));");

		assertOutput("ababab", "print(StringProto.repeat('ab', 3));");

		assertOutput("", "print(StringProto.substring('', 0, 0));");
		assertOutput("ab", "print(StringProto.substring('abcd', 0, 2));");
		assertOutput("bc", "print(StringProto.substring('abcd', 1, 3));");
		assertOutput("bcd", "print(StringProto.substring('abcd', 1, 6));");

		assertOutput("[a, b]", "print(StringProto.split('a,b', ','));");
		assertOutput("[ab]", "print(StringProto.split('ab', ','));");
		assertOutput("[ab, ]", "print(StringProto.split('ab,', ','));");
		assertOutput("[, ab]", "print(StringProto.split(',ab', ','));");
		assertOutput("[, ab, ]", "print(StringProto.split(',ab,', ','));");
		assertOutput("[, a, b, ]", "print(StringProto.split(',a,b,', ','));");

		assertOutput("true", "print(StringProto.startsWith('abcd', ''));");
		assertOutput("true", "print(StringProto.startsWith('abcd', 'ab'));");
		assertOutput("false", "print(StringProto.startsWith('abcd', 'cd'));");

		assertOutput("abcd", "print(StringProto.toLowerCase('abcd'));");
		assertOutput("abcd", "print(StringProto.toLowerCase('ABCD'));");

		assertOutput("ABCD", "print(StringProto.toUpperCase('abcd'));");
		assertOutput("ABCD", "print(StringProto.toUpperCase('ABCD'));");

		assertOutput("ab cd", "print(StringProto.trim(' ab cd '));");

		assertOutput("[]", "print(ObjectProto.keys({}));");
		assertOutput("[a]", "print(ObjectProto.keys({a:1}));");
		assertOutput("[a, b]", "print(ObjectProto.keys({a:1, b:2}));");

		assertOutput("0", "print(ArrayProto.length([]));");
		assertOutput("3", "print(ArrayProto.length([1, 2, 3]));");

		assertOutput("[1, 2, 3]", "print(ArrayProto.concat([1, 2], [3]));");

		assertOutput("true", "print(ArrayProto.every([], function(x) { return x >= 1 }));");
		assertOutput("true", "print(ArrayProto.every([1, 2, 3], function(x) { return x >= 1 }));");
		assertOutput("false", "print(ArrayProto.every([1, 2, 3], function(x) { return x > 1 }));");

		assertOutput("[3, 4, 5]", "print(ArrayProto.filter([1, 2, 3, 4, 5], function(x) { return x >= 3 }));");

		assertOutput("null", "print(ArrayProto.find([], function(x) { return x == 3 }));");
		assertOutput("3", "print(ArrayProto.find([1, 2, 3, 4, 5], function(x) { return x == 3 }));");

		assertOutput("-1", "print(ArrayProto.findIndex([], function(x) { return x == 3 }));");
		assertOutput("2", "print(ArrayProto.findIndex([1, 2, 3, 4, 5], function(x) { return x == 3 }));");

		assertOutput("-1", "print(ArrayProto.indexOf([1, 2, 3, 4, 5], 6));");
		assertOutput("2", "print(ArrayProto.indexOf([1, 2, 3, 4, 5], 3));");

		assertOutput("", "print(ArrayProto.join([], ','));");
		assertOutput("1,2,3,4,5", "print(ArrayProto.join([1, 2, 3, 4, 5], ','));");

		assertOutput("-1", "print(ArrayProto.lastIndexOf([3, 3, 3, 3, 3], 4));");
		assertOutput("4", "print(ArrayProto.lastIndexOf([3, 3, 3, 3, 3], 3));");

		assertOutput("[2, 4, 6, 8]", "print(ArrayProto.map([1, 2, 3, 4], function(x) { return x * 2; }));");

		assertOutput("10", "print(ArrayProto.reduce([1, 2, 3, 4], function(prev, x) { return prev + x; }, 0));");
		assertOutput("1234", "print(ArrayProto.reduce([1, 2, 3, 4], function(prev, x) { return prev.concat(x); }, ''));");

		assertOutput("10", "print(ArrayProto.reduceRight([1, 2, 3, 4], function(prev, x) { return prev + x; }, 0));");
		assertOutput("4321", "print(ArrayProto.reduceRight([1, 2, 3, 4], function(prev, x) { return prev.concat(x); }, ''));");

		assertOutput("false", "print(ArrayProto.some([], function(x) { return x == 1; }));");
		assertOutput("true", "print(ArrayProto.some([1, 2, 3, 4], function(x) { return x == 1; }));");
		assertOutput("false", "print(ArrayProto.some([1, 2, 3, 4], function(x) { return x == 5; }));");

		assertOutput("[]", "print(ArrayProto.slice([1, 2, 3, 4], 1, 1));");
		assertOutput("[2]", "print(ArrayProto.slice([1, 2, 3, 4], 1, 2));");
		assertOutput("[2, 3]", "print(ArrayProto.slice([1, 2, 3, 4], 1, 3));");
		
		assertOutput("[1, 2, 3]", "var a = [3, 1, 2]; ArrayProto.sort(a); print(a);");

		assertOutput("[]", "var a = []; ArrayProto.pop(a); print(a);");
		assertOutput("[1, 2]", "var a = [1, 2, 3]; ArrayProto.pop(a); print(a);");

		assertOutput("[1, 2, 3, 4]", "var a = [1, 2, 3]; ArrayProto.push(a, 4); print(a);");

		assertOutput("[3, 2, 1]", "var a = [1, 2, 3]; ArrayProto.reverse(a); print(a);");

		assertOutput("[2, 3]", "var a = [1, 2, 3]; ArrayProto.shift(a); print(a);");

		assertOutput("[4, 1, 2, 3]", "var a = [1, 2, 3]; ArrayProto.unshift(a, 4); print(a);");

		assertOutput("[1, 4]", "var a = [1, 2, 3, 4]; ArrayProto.splice(a, 1, 2); print(a);");
	}
	
	private void assertOutput(String expected, String program) {
		ParsingResult<List<Instruction>> result = new ReportingParseRunner<List<Instruction>>(parser.Sequence(parser.Program(), BaseParser.EOI)).run(program);
		System.out.println(ParseTreeUtils.printNodeTree(result));
		
		System.out.println("Parse Stack Size: " + result.valueStack.size());

		for(List<Instruction> in:result.valueStack) {
			System.out.println("Parse Stack: " + in);
		}
		
		List<Instruction> instructions = result.valueStack.pop();
		
		System.out.println("Instructions: " + instructions);
		
		Runtime runtime = new Runtime();
		
		new Engine().run(runtime, instructions);
		
		System.out.println("Output: " + runtime.getOutput());
		
		assertEquals(expected, StringUtils.join(runtime.getOutput(), "\n"));
		assertTrue(runtime.getErrors().isEmpty());
		assertTrue(runtime.getStack().isEmpty());
		assertTrue(result.valueStack.isEmpty());
	}
}