package backend.runtime;

import static backend.runtime.EngineAsserts.assertDoubleOutput;
import static backend.runtime.EngineAsserts.assertError;
import static backend.runtime.EngineAsserts.assertOutput;

import org.junit.Test;

public class LibraryTest {
	@Test
	public void range() {
		assertError("TypeError: Not a double: null (at line 1)", "print(range());");
		assertOutput("[0, 1, 2]", "print(range(3));");
	}
	
	@Test
	public void newArray() {
		assertOutput("[]", "print(newArray());");
		assertOutput("[null, null, null]", "print(newArray(3));");
		assertOutput("[[null, null], [null, null], [null, null]]", "print(newArray(3, 2));");
	}
	
	@Test
	public void parseInt() {
		assertOutput("5", "print(parseInt('5'))");
		assertOutput("-5", "print(parseInt('-5'))");
		assertOutput("NaN", "print(parseInt('5.5'))");
		assertOutput("NaN", "print(parseInt('-5.5'))");
		assertOutput("10", "print(parseInt('a', 16))");
		assertOutput("NaN", "print(parseInt('a'))");
	}
	
	@Test
	public void functionApply() {
		assertError("TypeError: Not an array: null (at line 1)", "print.apply()");
		assertError("TypeError: Not an array: null (at line 1)", "print.apply('a')");
		assertOutput("1", "print.apply(null, [1])");
		assertOutput("1 2", "print.apply(null, [1, 2])");
	}
	
	@Test
	public void functionCall() {
		assertOutput("", "print.call()");
		assertOutput("", "print.call(null)");
		assertOutput("1", "print.call(null, 1)");
		assertOutput("1 2", "print.call(null, 1, 2)");
		assertOutput("a", "(function() { print(this); }).call('a')");
	}
	
	@Test
	public void parseDouble() {
		assertOutput("5.5", "print(parseDouble('5.5'))");
		assertOutput("-5.5", "print(parseDouble('-5.5'))");
		assertOutput("NaN", "print(parseDouble('a'))");
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
		assertOutput("10", "print([1, 2, 3, 4].reduce(function(prev, x) { return prev + x; }));");
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
		assertOutput("[1, 10, 2]", "var a = [10, 1, 2]; a.sort(); print(a);");
		assertOutput("[1, 2, 10]", "var a = [10, 1, 2]; a.sort((x, y) => x - y); print(a);");
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

	@Test
	public void mathE() {
		assertDoubleOutput("2.718281828459045", "print(Math.E);");
	}
	
	@Test
	public void mathLn2() {
		assertDoubleOutput("0.6931471805599453", "print(Math.LN2);");
	}
	
	@Test
	public void mathLn10() {
		assertDoubleOutput("2.302585092994046", "print(Math.LN10);");
	}
	
	@Test
	public void mathLog2e() {
		assertDoubleOutput("1.4426950408889634", "print(Math.LOG2E);");
	}
	
	@Test
	public void mathLog10e() {
		assertDoubleOutput("0.4342944819032518", "print(Math.LOG10E);");
	}
	
	@Test
	public void mathPi() {
		assertDoubleOutput("3.141592653589793", "print(Math.PI);");
	}
	
	@Test
	public void mathSqrt1_2() {
		assertDoubleOutput("0.7071067811865476", "print(Math.SQRT1_2);");
	}
	
	@Test
	public void mathSqrt2() {
		assertDoubleOutput("1.4142135623730951", "print(Math.SQRT2);");
	}
	
	@Test
	public void mathAbs() {
		assertDoubleOutput("2", "print(Math.abs(-2));");
	}
	
	@Test
	public void mathAcos() {
		assertDoubleOutput("1.0471975511965979", "print(Math.acos(0.5));");
	}
	
	@Test
	public void mathAcosh() {
		assertDoubleOutput("1.3169578969248166", "print(Math.acosh(2));");
	}
	
	@Test
	public void mathAsin() {
		assertDoubleOutput("0.5235987755982989", "print(Math.asin(0.5));");
	}
	
	@Test
	public void mathAsinh() {
		assertDoubleOutput("0.48121182505960347", "print(Math.asinh(0.5));");
		assertDoubleOutput("Infinity", "print(Math.asinh(Infinity));");
		assertDoubleOutput("-Infinity", "print(Math.asinh(-Infinity));");
	}
	
	@Test
	public void mathAtan() {
		assertDoubleOutput("0.4636476090008061", "print(Math.atan(0.5));");
	}
	
	@Test
	public void mathAtanh() {
		assertDoubleOutput("0.5493061443340548", "print(Math.atanh(0.5));");
	}
	
	@Test
	public void mathAtan2() {
		assertDoubleOutput("0.4636476090008061", "print(Math.atan2(10, 20));");
	}
	
	@Test
	public void mathCbrt() {
		assertDoubleOutput("4.641588833612778", "print(Math.cbrt(100));");
	}
	
	@Test
	public void mathCeil() {
		assertDoubleOutput("11", "print(Math.ceil(10.5));");
	}
	
	@Test
	public void mathCos() {
		assertDoubleOutput("0.8775825618903728", "print(Math.cos(0.5));");
	}
	
	@Test
	public void mathCosh() {
		assertDoubleOutput("1.1276259652063807", "print(Math.cosh(0.5));");
	}
	
	@Test
	public void mathExp() {
		assertDoubleOutput("2.718281828459045", "print(Math.exp(1));");
	}
	
	@Test
	public void mathExpm1() {
		assertDoubleOutput("1.718281828459045", "print(Math.expm1(1));");
	}
	
	@Test
	public void mathFloor() {
		assertDoubleOutput("10", "print(Math.floor(10.5));");
	}
	
	@Test
	public void mathHypot() {
		assertDoubleOutput("5.385164807134504", "print(Math.hypot(2, 3, 4));");
	}
	
	@Test
	public void mathLog() {
		assertDoubleOutput("4.605170185988092", "print(Math.log(100));");
	}
	
	@Test
	public void mathLog1p() {
		assertDoubleOutput("4.61512051684126", "print(Math.log1p(100));");
	}
	
	@Test
	public void mathLog10() {
		assertDoubleOutput("2", "print(Math.log10(100));");
	}
	
	@Test
	public void mathLog2() {
		assertDoubleOutput("6.643856189774724", "print(Math.log2(100));");
	}
	
	@Test
	public void mathMax() {
		assertDoubleOutput("8", "print(Math.max(2, 4, 8));");
		assertDoubleOutput("-Infinity", "print(Math.max());");
	}
	
	@Test
	public void mathMin() {
		assertDoubleOutput("2", "print(Math.min(2, 4, 8));");
		assertDoubleOutput("Infinity", "print(Math.min());");
	}
	
	@Test
	public void mathPow() {
		assertDoubleOutput("8", "print(Math.pow(2, 3));");
	}
	
	@Test
	public void mathRandom() {
		assertOutput("true", "print(Math.random() < 1);");
	}
	
	@Test
	public void mathRound() {
		assertDoubleOutput("20", "print(Math.round(20.3));");
	}
	
	@Test
	public void mathSign() {
		assertDoubleOutput("-1", "print(Math.sign(-5));");
	}
	
	@Test
	public void mathSin() {
		assertDoubleOutput("0.479425538604203", "print(Math.sin(0.5));");
	}
	
	@Test
	public void mathSinh() {
		assertDoubleOutput("0.5210953054937474", "print(Math.sinh(0.5));");
	}
	
	@Test
	public void mathSqrt() {
		assertDoubleOutput("1.4142135623730951", "print(Math.sqrt(2));");
	}
	
	@Test
	public void mathTan() {
		assertDoubleOutput("0.5463024898437905", "print(Math.tan(0.5));");
	}
	
	@Test
	public void mathTanh() {
		assertDoubleOutput("0.46211715726000974", "print(Math.tanh(0.5));");
	}
	
	@Test
	public void mathTrunc() {
		assertDoubleOutput("3", "print(Math.trunc(3.5));");
	}
}