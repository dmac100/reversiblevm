package runtime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import instruction.Instruction;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

public class LibraryTest {
	@Test
	public void stringLength() {
		assertOutput("0", "print(StringProto.length(''));");
		assertOutput("1", "print(StringProto.length('1'));");
		assertOutput("2", "print(StringProto.length('12'));");
	}

	@Test
	public void stringCharAt() {
		assertOutput("1", "print(StringProto.charAt('12', 0));");
		assertOutput("2", "print(StringProto.charAt('12', 1));");
	}

	@Test
	public void stringConcat() {
		assertOutput("1234", "print(StringProto.concat('12', '34'));");
	}

	@Test
	public void stringEndsWith() {
		assertOutput("true", "print(StringProto.endsWith('aaa', 'a'));");
		assertOutput("false", "print(StringProto.endsWith('aaa', 'b'));");
	}

	@Test
	public void stringIndexOf() {
		assertOutput("0", "print(StringProto.indexOf('aaab', 'a'));");
		assertOutput("3", "print(StringProto.indexOf('aaab', 'b'));");
		assertOutput("-1", "print(StringProto.indexOf('aaab', 'c'));");
	}

	@Test
	public void stringLastIndexOf() {
		assertOutput("2", "print(StringProto.lastIndexOf('aaab', 'a'));");
		assertOutput("3", "print(StringProto.lastIndexOf('aaab', 'b'));");
		assertOutput("-1", "print(StringProto.lastIndexOf('aaab', 'c'));");
	}

	@Test
	public void stringRepeat() {
		assertOutput("ababab", "print(StringProto.repeat('ab', 3));");
	}

	@Test
	public void stringSubstring() {
		assertOutput("", "print(StringProto.substring('', 0, 0));");
		assertOutput("ab", "print(StringProto.substring('abcd', 0, 2));");
		assertOutput("bc", "print(StringProto.substring('abcd', 1, 3));");
		assertOutput("bcd", "print(StringProto.substring('abcd', 1, 6));");
	}

	@Test
	public void stringSplit() {
		assertOutput("[a, b]", "print(StringProto.split('a,b', ','));");
		assertOutput("[ab]", "print(StringProto.split('ab', ','));");
		assertOutput("[ab, ]", "print(StringProto.split('ab,', ','));");
		assertOutput("[, ab]", "print(StringProto.split(',ab', ','));");
		assertOutput("[, ab, ]", "print(StringProto.split(',ab,', ','));");
		assertOutput("[, a, b, ]", "print(StringProto.split(',a,b,', ','));");
	}

	@Test
	public void stringStartsWith() {
		assertOutput("true", "print(StringProto.startsWith('abcd', ''));");
		assertOutput("true", "print(StringProto.startsWith('abcd', 'ab'));");
		assertOutput("false", "print(StringProto.startsWith('abcd', 'cd'));");
	}
	
	@Test
	public void stringToLowerCase() {
		assertOutput("abcd", "print(StringProto.toLowerCase('abcd'));");
		assertOutput("abcd", "print(StringProto.toLowerCase('ABCD'));");
	}

	@Test
	public void stringToUpperCase() {
		assertOutput("ABCD", "print(StringProto.toUpperCase('abcd'));");
		assertOutput("ABCD", "print(StringProto.toUpperCase('ABCD'));");
	}

	@Test
	public void stringTrim() {
		assertOutput("ab cd", "print(StringProto.trim(' ab cd '));");
	}

	@Test
	public void objectKeys() {
		assertOutput("[]", "print(ObjectProto.keys({}));");
		assertOutput("[a]", "print(ObjectProto.keys({a:1}));");
		assertOutput("[a, b]", "print(ObjectProto.keys({a:1, b:2}));");
	}

	@Test
	public void arrayLength() {
		assertOutput("0", "print(ArrayProto.length([]));");
		assertOutput("3", "print(ArrayProto.length([1, 2, 3]));");
	}

	@Test
	public void arrayConcat() {
		assertOutput("[1, 2, 3]", "print(ArrayProto.concat([1, 2], [3]));");
	}

	@Test
	public void arrayEvery() {
		assertOutput("true", "print(ArrayProto.every([], function(x) { return x >= 1; }));");
		assertOutput("true", "print(ArrayProto.every([1, 2, 3], function(x) { return x >= 1; }));");
		assertOutput("false", "print(ArrayProto.every([1, 2, 3], function(x) { return x > 1; }));");
	}

	@Test
	public void arrayFilter() {
		assertOutput("[3, 4, 5]", "print(ArrayProto.filter([1, 2, 3, 4, 5], function(x) { return x >= 3; }));");
	}

	@Test
	public void arrayFind() {
		assertOutput("null", "print(ArrayProto.find([], function(x) { return x == 3; }));");
		assertOutput("3", "print(ArrayProto.find([1, 2, 3, 4, 5], function(x) { return x == 3; }));");
	}

	@Test
	public void arrayFindIndex() {
		assertOutput("-1", "print(ArrayProto.findIndex([], function(x) { return x == 3; }));");
		assertOutput("2", "print(ArrayProto.findIndex([1, 2, 3, 4, 5], function(x) { return x == 3; }));");
	}

	@Test
	public void arrayIndexOf() {
		assertOutput("-1", "print(ArrayProto.indexOf([1, 2, 3, 4, 5], 6));");
		assertOutput("2", "print(ArrayProto.indexOf([1, 2, 3, 4, 5], 3));");
	}

	@Test
	public void arrayJoin() {
		assertOutput("", "print(ArrayProto.join([], ','));");
		assertOutput("1,2,3,4,5", "print(ArrayProto.join([1, 2, 3, 4, 5], ','));");
	}

	@Test
	public void arrayLastIndexOf() {
		assertOutput("-1", "print(ArrayProto.lastIndexOf([3, 3, 3, 3, 3], 4));");
		assertOutput("4", "print(ArrayProto.lastIndexOf([3, 3, 3, 3, 3], 3));");
	}

	@Test
	public void arrayMap() {
		assertOutput("[2, 4, 6, 8]", "print(ArrayProto.map([1, 2, 3, 4], function(x) { return x * 2; }));");
	}

	@Test
	public void arrayReduce() {
		assertOutput("10", "print(ArrayProto.reduce([1, 2, 3, 4], function(prev, x) { return prev + x; }, 0));");
		assertOutput("1234", "print(ArrayProto.reduce(['1', '2', '3', '4'], function(prev, x) { return StringProto.concat(prev, x); }, ''));");
	}

	@Test
	public void arrayReduceRight() {
		assertOutput("10", "print(ArrayProto.reduceRight([1, 2, 3, 4], function(prev, x) { return prev + x; }, 0));");
		assertOutput("4321", "print(ArrayProto.reduceRight(['1', '2', '3', '4'], function(prev, x) { return StringProto.concat(prev, x); }, ''));");
	}

	@Test
	public void arraySome() {
		assertOutput("false", "print(ArrayProto.some([], function(x) { return x == 1; }));");
		assertOutput("true", "print(ArrayProto.some([1, 2, 3, 4], function(x) { return x == 1; }));");
		assertOutput("false", "print(ArrayProto.some([1, 2, 3, 4], function(x) { return x == 5; }));");
	}

	@Test
	public void arraySlice() {
		assertOutput("[]", "print(ArrayProto.slice([1, 2, 3, 4], 1, 1));");
		assertOutput("[2]", "print(ArrayProto.slice([1, 2, 3, 4], 1, 2));");
		assertOutput("[2, 3]", "print(ArrayProto.slice([1, 2, 3, 4], 1, 3));");
	}
	
	@Test
	public void arraySort() {
		assertOutput("[1, 2, 3]", "var a = [3, 1, 2]; ArrayProto.sort(a); print(a);");
	}

	@Test
	public void arrayPop() {
		assertOutput("[]", "var a = []; ArrayProto.pop(a); print(a);");
		assertOutput("[1, 2]", "var a = [1, 2, 3]; ArrayProto.pop(a); print(a);");
	}

	@Test
	public void arrayPush() {
		assertOutput("[1, 2, 3, 4]", "var a = [1, 2, 3]; ArrayProto.push(a, 4); print(a);");
	}

	@Test
	public void arrayReverse() {
		assertOutput("[3, 2, 1]", "var a = [1, 2, 3]; ArrayProto.reverse(a); print(a);");
	}

	@Test
	public void arrayShift() {
		assertOutput("[2, 3]", "var a = [1, 2, 3]; ArrayProto.shift(a); print(a);");
	}

	@Test
	public void arrayUnshift() {
		assertOutput("[4, 1, 2, 3]", "var a = [1, 2, 3]; ArrayProto.unshift(a, 4); print(a);");
	}

	@Test
	public void arraySplice() {
		assertOutput("[1, 4]", "var a = [1, 2, 3, 4]; ArrayProto.splice(a, 1, 2); print(a);");
	}
	
	private void assertOutput(String expected, String program) {
		Runtime runtime = new Runtime();
		List<Instruction> instructions = Engine.compile(program);
		new Engine().run(runtime, instructions);
		
		assertEquals(expected, StringUtils.join(runtime.getOutput(), "\n"));
		assertTrue(runtime.getErrors().isEmpty());
		assertTrue(runtime.getStack().isEmpty());
	}
}