package backend.runtime;

import static backend.runtime.EngineAsserts.assertError;
import static backend.runtime.EngineAsserts.assertOutput;

import org.junit.Test;

public class LibraryTest {
	@Test
	public void range() {
		assertError("TypeError: Not a double: null", "print(range());");
		assertOutput("[0, 1, 2]", "print(range(3));");
	}
	
	@Test
	public void stringLength() {
		assertOutput("0", "print(''.length());");
		assertOutput("1", "print('1'.length());");
		assertOutput("2", "print('12'.length());");
	}

	@Test
	public void stringCharAt() {
		assertOutput("1", "print('12'.charAt(0));");
		assertOutput("2", "print('12'.charAt(1));");
	}

	@Test
	public void stringConcat() {
		assertOutput("12", "print('12'.concat());");
		assertOutput("1234", "print('12'.concat('34'));");
		assertOutput("12345", "print('12'.concat('34').concat('5'));");
	}

	@Test
	public void stringEndsWith() {
		assertOutput("true", "print('aaa'.endsWith('a'));");
		assertOutput("false", "print('aaa'.endsWith('b'));");
	}

	@Test
	public void stringIndexOf() {
		assertOutput("0", "print('aaab'.indexOf('a'));");
		assertOutput("3", "print('aaab'.indexOf('b'));");
		assertOutput("-1", "print('aaab'.indexOf('c'));");
	}

	@Test
	public void stringLastIndexOf() {
		assertOutput("2", "print('aaab'.lastIndexOf('a'));");
		assertOutput("3", "print('aaab'.lastIndexOf('b'));");
		assertOutput("-1", "print('aaab'.lastIndexOf('c'));");
	}

	@Test
	public void stringRepeat() {
		assertOutput("ababab", "print('ab'.repeat(3));");
	}

	@Test
	public void stringSubstring() {
		assertOutput("", "print(''.substring(0, 0));");
		assertOutput("ab", "print('abcd'.substring(0, 2));");
		assertOutput("bc", "print('abcd'.substring(1, 3));");
		assertOutput("bcd", "print('abcd'.substring(1, 6));");
	}

	@Test
	public void stringSplit() {
		assertOutput("[a, b]", "print('a,b'.split(','));");
		assertOutput("[ab]", "print('ab'.split(','));");
		assertOutput("[ab, ]", "print('ab,'.split(','));");
		assertOutput("[, ab]", "print(',ab'.split(','));");
		assertOutput("[, ab, ]", "print(',ab,'.split(','));");
		assertOutput("[, a, b, ]", "print(',a,b,'.split(','));");
	}

	@Test
	public void stringStartsWith() {
		assertOutput("true", "print('abcd'.startsWith(''));");
		assertOutput("true", "print('abcd'.startsWith('ab'));");
		assertOutput("false", "print('abcd'.startsWith('cd'));");
	}
	
	@Test
	public void stringToLowerCase() {
		assertOutput("abcd", "print('abcd'.toLowerCase());");
		assertOutput("abcd", "print('ABCD'.toLowerCase());");
	}

	@Test
	public void stringToUpperCase() {
		assertOutput("ABCD", "print('abcd'.toUpperCase());");
		assertOutput("ABCD", "print('ABCD'.toUpperCase());");
	}

	@Test
	public void stringTrim() {
		assertOutput("ab cd", "print(' ab cd '.trim());");
	}

	@Test
	public void objectKeys() {
		assertOutput("[]", "print(({}).keys());");
		assertOutput("[a]", "print(({a:1}).keys());");
		assertOutput("[a, b]", "print(({a:1, b:2}).keys());");
	}
	
	@Test
	public void objectValues() {
		assertOutput("[]", "print(({}).values());");
		assertOutput("[1]", "print(({a:1}).values());");
		assertOutput("[1, 2]", "print(({a:1, b:2}).values());");
	}

	@Test
	public void arrayLength() {
		assertOutput("0", "print([].length());");
		assertOutput("3", "print([1, 2, 3].length());");
	}

	@Test
	public void arrayConcat() {
		assertOutput("[1, 2]", "print([1, 2].concat([]));");
		assertOutput("[1, 2, 3]", "print([1, 2].concat([3]));");
		assertOutput("[1, 2, 3, 4]", "print([1, 2].concat([3], [4]));");
	}

	@Test
	public void arrayEvery() {
		assertOutput("true", "print([].every(function(x) { return x >= 1; }));");
		assertOutput("true", "print([1, 2, 3].every(function(x) { return x >= 1; }));");
		assertOutput("false", "print([1, 2, 3].every(function(x) { return x > 1; }));");
	}

	@Test
	public void arrayFilter() {
		assertOutput("[3, 4, 5]", "print([1, 2, 3, 4, 5].filter(function(x) { return x >= 3; }));");
	}

	@Test
	public void arrayFind() {
		assertOutput("null", "print([].find(function(x) { return x == 3; }));");
		assertOutput("3", "print([1, 2, 3, 4, 5].find(function(x) { return x == 3; }));");
	}

	@Test
	public void arrayFindIndex() {
		assertOutput("-1", "print([].findIndex(function(x) { return x == 3; }));");
		assertOutput("2", "print([1, 2, 3, 4, 5].findIndex(function(x) { return x == 3; }));");
	}
	
	@Test
	public void arrayForEach() {
		assertOutput("1\n2\n3", "[1, 2, 3].forEach(function(x) { print(x); });");
	}

	@Test
	public void arrayIndexOf() {
		assertOutput("-1", "print([1, 2, 3, 4, 5].indexOf(6));");
		assertOutput("2", "print([1, 2, 3, 4, 5].indexOf(3));");
	}

	@Test
	public void arrayJoin() {
		assertOutput("", "print([].join(','));");
		assertOutput("1,2,3,4,5", "print([1, 2, 3, 4, 5].join(','));");
	}

	@Test
	public void arrayLastIndexOf() {
		assertOutput("-1", "print([3, 3, 3, 3, 3].lastIndexOf(4));");
		assertOutput("4", "print([3, 3, 3, 3, 3].lastIndexOf(3));");
	}

	@Test
	public void arrayMap() {
		assertOutput("[2, 4, 6, 8]", "print([1, 2, 3, 4].map(function(x) { return x * 2; }));");
	}

	@Test
	public void arrayReduce() {
		assertOutput("10", "print([1, 2, 3, 4].reduce(function(prev, x) { return prev + x; }, 0));");
		assertOutput("1234", "print(['1', '2', '3', '4'].reduce(function(prev, x) { return prev.concat(x); }, ''));");
	}

	@Test
	public void arrayReduceRight() {
		assertOutput("10", "print([1, 2, 3, 4].reduceRight(function(prev, x) { return prev + x; }, 0));");
		assertOutput("4321", "print(['1', '2', '3', '4'].reduceRight(function(prev, x) { return prev.concat(x); }, ''));");
	}

	@Test
	public void arraySome() {
		assertOutput("false", "print([].some(function(x) { return x == 1; }));");
		assertOutput("true", "print([1, 2, 3, 4].some(function(x) { return x == 1; }));");
		assertOutput("false", "print([1, 2, 3, 4].some(function(x) { return x == 5; }));");
	}

	@Test
	public void arraySlice() {
		assertOutput("[]", "print([1, 2, 3, 4].slice(1, 1));");
		assertOutput("[2]", "print([1, 2, 3, 4].slice(1, 2));");
		assertOutput("[2, 3]", "print([1, 2, 3, 4].slice(1, 3));");
	}
	
	@Test
	public void arraySort() {
		assertOutput("[1, 2, 3]", "var a = [3, 1, 2]; a.sort(); print(a);");
	}

	@Test
	public void arrayPop() {
		assertOutput("[]", "var a = []; a.pop(); print(a);");
		assertOutput("[1, 2]", "var a = [1, 2, 3]; a.pop(); print(a);");
	}

	@Test
	public void arrayPush() {
		assertOutput("[1, 2, 3, 4]", "var a = [1, 2, 3]; a.push(4); print(a);");
	}

	@Test
	public void arrayReverse() {
		assertOutput("[3, 2, 1]", "var a = [1, 2, 3]; a.reverse(); print(a);");
	}

	@Test
	public void arrayShift() {
		assertOutput("[2, 3]", "var a = [1, 2, 3]; a.shift(); print(a);");
	}

	@Test
	public void arrayUnshift() {
		assertOutput("[4, 1, 2, 3]", "var a = [1, 2, 3]; a.unshift(4); print(a);");
	}

	@Test
	public void arrayKeys() {
		assertOutput("[0, 1, 2, 3]", "var a = [1, 2, 3, 4]; print(a.keys());");
	}
	
	@Test
	public void arrayValues() {
		assertOutput("[1, 2, 3, 4]", "var a = [1, 2, 3, 4]; print(a.values());");
	}
}