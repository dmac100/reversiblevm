package backend.runtime;

import static backend.runtime.EngineAsserts.assertError;
import static backend.runtime.EngineAsserts.assertOutput;

import java.util.Arrays;

import org.junit.Test;

public class ParseAndRunTest {
	@Test
	public void Literal() {
	}
	
	@Test
	public void NullLiteral() {
		assertOutput("null", "print(null);");
	}
	
	@Test
	public void BooleanLiteral() {
		assertOutput("true", "print(true);");
		assertOutput("false", "print(false);");
	}
	
	@Test
	public void NumericLiteral() {
		assertOutput("-10", "print(-10);");
		assertOutput("-10.5", "print(-10.5);");
		assertOutput("10", "print(10);");
		assertOutput("10.5", "print(10.5);");
	}
	
	@Test
	public void StringLiteral() {
		assertOutput("", "print('');");
		assertOutput("", "print(\"\");");
		assertOutput("a", "print('a');");
		assertOutput("a", "print(\"a\");");
	}
	
	@Test
	public void Identifier() {
	}

	@Test
	public void PrimaryExpression() {
	}
	
	@Test
	public void ArrayLiteral() {
		assertOutput("[]", "print([]);");
		assertOutput("[1, 2, 3]", "print([1, 2, 3]);");
		
		assertOutput("5", "a = [3, 4, 5]; print(a[2]);");
		assertOutput("3", "a = [[1, 2], [3, 4]]; print(a[1][0]);");
		
		assertOutput("null", "print([][0]);");
		
		assertOutput("[[CYCLIC]]", "var a = []; a[0] = a; print(a);");
		assertOutput("[[0], [0]]", "a = [[0]]; a[1] = a[0]; print(a);");
	}
	
	@Test
	public void Elision() {
	}
	
	@Test
	public void ObjectLiteral() {
		assertOutput("{}", "var x = ({}); print(x);");
		assertOutput("{a:1}", "var x = ({ a: 1 }); print(x);");
		assertOutput("{a:1, b:2}", "var x = ({ a: 1, b: 2 }); print(x);");
		assertOutput("{a:{b:2}}", "var x = ({ a: { b: 2 } }); print(x);");
		
		assertOutput("1", "var x = ({ a: 1 }); print(x.a);");
		assertOutput("1", "var x = ({ a: { b: 1 } }); print(x.a.b);");
		assertOutput("1", "var x = ({ f: function() { print(1); } }); x.f();");
		
		assertOutput("null", "print({}.a);");
		
		assertOutput("{a:[CYCLIC]}", "var a = {}; a.a = a; print(a);");
		assertOutput("{a:[1], b:[1]}", "a = {a: [1]}; a.b = a.a; print(a);");
	}
	
	@Test
	public void PropertyNameAndValueList() {
	}
	
	@Test
	public void MemberExpression() {
	}
	
	@Test
	public void NewExpression() {
	}

	@Test
	public void Arguments() {
	}
	
	@Test
	public void ArgumentList() {
	}
	
	@Test
	public void LeftHandSideExpression() {
	}
	
	@Test
	public void PostfixExpression() {
		assertOutput("2", "x = 1; x++; print(x);");
		assertOutput("1", "x = 1; print(x++);");
		assertOutput("1", "x = 2; x--; print(x);");
		assertOutput("2", "x = 2; print(x--);");
		
		assertOutput("2", "x = { y: 1 }; x.y++; print(x.y);");
		assertOutput("1", "x = { y: 1 }; print(x.y++);");
		assertOutput("1", "x = { y: 2 }; x.y--; print(x.y);");
		assertOutput("2", "x = { y: 2 }; print(x.y--);");
		
		assertOutput("2", "x = [1]; x[0]++; print(x[0]);");
		assertOutput("1", "x = [1]; print(x[0]++);");
		assertOutput("1", "x = [2]; x[0]--; print(x[0]);");
		assertOutput("2", "x = [2]; print(x[0]--);");
	}
	
	@Test
	public void UnaryExpression() {
		assertOutput("5", "print(+5);");
		assertOutput("-5", "print(-5);");
		assertOutput("-6", "print(~5);");
		assertOutput("false", "print(!true);");
		assertOutput("null", "print(void 5);");
		
		assertOutput("2", "x = 1; ++x; print(x);");
		assertOutput("2", "x = 1; print(++x);");
		assertOutput("1", "x = 2; --x; print(x);");
		assertOutput("1", "x = 2; print(--x);");
		
		assertOutput("2", "x = { y: 1 }; ++x.y; print(x.y);");
		assertOutput("2", "x = { y: 1 }; print(++x.y);");
		assertOutput("1", "x = { y: 2 }; --x.y; print(x.y);");
		assertOutput("1", "x = { y: 2 }; print(--x.y);");
		
		assertOutput("2", "x = [1]; ++x[0]; print(x[0]);");
		assertOutput("2", "x = [1]; print(++x[0]);");
		assertOutput("1", "x = [2]; --x[0]; print(x[0]);");
		assertOutput("1", "x = [2]; print(--x[0]);");
	}
	
	@Test
	public void MultiplicativeExpression() {
		assertOutput("6", "print(2 * 3);");
		assertOutput("2", "print(10 / 5);");
		assertOutput("3", "print(8 % 5);");
	}
	
	@Test
	public void AdditiveExpression() {
		assertOutput("5", "print(2 + 3);");
		assertOutput("2", "print(5 - 3);");
		
		assertOutput("23", "print('2' + 3);");
		assertOutput("23", "print(2 + '3');");
		assertOutput("23", "print('2' + '3');");
		assertError("TypeError: Not a double: false", "print(2 + false);");
	}
	
	@Test
	public void ShiftExpression() {
		assertOutput("8", "print(1 << 3);");
		assertOutput("4", "print(8 >> 1);");
		assertOutput("3", "print(-14 >>> 30);");
		assertOutput("-1", "print(-14 >> 30);");
	}
	
	@Test
	public void RelationalExpression() {
		assertOutput("true", "print(1 < 3);");
		assertOutput("true", "print(3 > 1);");
		assertOutput("true", "print(1 <= 3);");
		assertOutput("true", "print(3 >= 1);");
		
		assertOutput("false", "print(1 > 3);");
		assertOutput("false", "print(3 < 1);");
		assertOutput("false", "print(1 >= 3);");
		assertOutput("false", "print(3 <= 1);");
		
		assertOutput("false", "print(1 > 1);");
		assertOutput("false", "print(1 < 1);");
		assertOutput("true", "print(1 >= 1);");
		assertOutput("true", "print(1 <= 1);");
	}
	
	@Test
	public void EqualityExpression() {
		assertOutput("true", "print(true == true);");
		assertOutput("true", "print(false == false);");
		assertOutput("false", "print(true == false);");
		assertOutput("true", "print(true == true);");
		assertOutput("false", "print(true == false);");
		assertOutput("true", "print(1 == 1);");
		assertOutput("false", "print(1 == 2);");
		assertOutput("true", "print(print == print);");
		assertOutput("true", "print(null == null);");
		assertOutput("true", "print('a' == 'a');");
		assertOutput("false", "print('a' == 'b');");
		
		assertOutput("false", "print(true != true);");
		assertOutput("false", "print(false != false);");
		assertOutput("true", "print(true != false);");
		assertOutput("false", "print(true != true);");
		assertOutput("true", "print(true != false);");
		assertOutput("false", "print(1 != 1);");
		assertOutput("true", "print(1 != 2);");
		assertOutput("false", "print(print != print);");
		assertOutput("false", "print(null != null);");
		assertOutput("false", "print('a' != 'a');");
		assertOutput("true", "print('a' != 'b');");
	}
	
	@Test
	public void BitwiseANDExpression() {
		assertOutput("5", "print(5 & 7);");
	}
	
	@Test
	public void BitwiseXORExpression() {
		assertOutput("6", "print(5 ^ 3);");
	}
	
	@Test
	public void BitwiseORExpression() {
		assertOutput("7", "print(5 | 3);");
	}
	
	@Test
	public void LogicalANDExpression() {
		assertOutput("true", "print(true && true);");
		assertOutput("false", "print(true && false);");
		assertOutput("false", "print(false && true);");
		assertOutput("false", "print(false && false);");
	}
	
	@Test
	public void LogicalORExpression() {
		assertOutput("true", "print(true || true);");
		assertOutput("true", "print(true || false);");
		assertOutput("true", "print(false || true);");
		assertOutput("false", "print(false || false);");
	}
	
	@Test
	public void ConditionalExpression() {
		assertOutput("1", "print(true ? 1 : 2);");
		assertOutput("2", "print(false ? 1 : 2);");
	}
	
	@Test
	public void AssignmentExpression() {
		assertOutput("2", "x = 2; print(x);");
		assertOutput("2 2", "x = y = 2; print(x, y);");
		assertOutput("3", "x = 1; y = 2; print(x + y);");
		assertOutput("4", "x = 1; x = x + 1; y = 2; print(x + y);");
		
		assertOutput("3", "x = { y: 2 }; x.y = 3; print(x.y);");
		assertOutput("3", "x = { y: { z: 2 } }; x.y.z = 3; print(x.y.z);");
		
		assertOutput("3", "x = [2]; x[0] = 3; print(x[0]);");
	}
	
	@Test
	public void ArrowFunction() {
		assertOutput("1", "print((() => 1)(1));");
		assertOutput("2", "print((x => x + 1)(1));");
		assertOutput("3", "print(((x, y) => x + y)(1, 2));");
		assertOutput("2", "print((x => { return x + 1; })(1));");
		assertOutput("1", "print((x => { return x; })(1));");
		assertOutput("null", "print((x => { })(1));");
	}
	
	@Test
	public void CompoundAssignmentOperator() {
		assertOutput("6", "x = 2; x *= 3; print(x);");
		assertOutput("2", "x = 4; x /= 2; print(x);");
		assertOutput("2", "x = 5; x %= 3; print(x);");
		assertOutput("5", "x = 2; x += 3; print(x);");
		assertOutput("2", "x = 5; x -= 3; print(x);");
		assertOutput("4", "x = 2; x <<= 1; print(x);");
		assertOutput("1", "x = 2; x >>= 1; print(x);");
		assertOutput("1", "x = 2; x >>>= 1; print(x);");
		assertOutput("2", "x = 2; x &= 3; print(x);");
		assertOutput("1", "x = 2; x ^= 3; print(x);");
		assertOutput("3", "x = 2; x |= 3; print(x);");
		
		assertOutput("5", "x = { y: 2 }; x.y += 3; print(x.y);");
		assertOutput("5", "x = { y: { z: 2 } }; x.y.z += 3; print(x.y.z);");
		
		assertOutput("5", "x = [2]; x[0] += 3; print(x[0]);");
		assertOutput("5", "x = [[2]]; x[0][0] += 3; print(x[0][0]);");
	}
	
	@Test
	public void Expression() {
	}
	
	@Test
	public void Statement() {
	}
	
	@Test
	public void Block() {
		assertOutput("", "{ }");
		assertOutput("1", "{ print(1); }");
		assertOutput("1\n2", "{ print(1); print(2); }");
	}
	
	@Test
	public void StatementList() {
	}
	
	@Test
	public void VariableStatement() {
		assertOutput("1\n2", "var x = 1, y = 2; print(x); print(y);");
	}
	
	@Test
	public void VariableDeclarationList() {
	}
	
	@Test
	public void VariableDeclaration() {
	}
	
	@Test
	public void Initialiser() {
	}
	
	@Test
	public void EmptyStatement() {
		assertOutput("", ";");
	}
	
	@Test
	public void ExpressionStatement() {
	}
	
	@Test
	public void IfStatement() {
		assertOutput("1\n", "if(true) { print(1); }; print();");
		assertOutput("", "if(false) { print(1); }; print();");
		
		assertOutput("1\n", "if(true) { print(1); } else { print(2); }; print();");
		assertOutput("2\n", "if(false) { print(1); } else { print(2); }; print();");
		
		assertOutput("1\n", "if(true) { print(1); } else if(true) { print(2); } else { print(3); }; print();");
		assertOutput("1\n", "if(true) { print(1); } else if(false) { print(2); } else { print(3); }; print();");
		assertOutput("2\n", "if(false) { print(1); } else if(true) { print(2); } else { print(3); }; print();");
		assertOutput("3\n", "if(false) { print(1); } else if(false) { print(2); } else { print(3); }; print();");
	}
	
	@Test
	public void IterationStatement() {
		assertOutput("0\n1\n2\n3\n", "x = 0; do { print(x); x = x + 1; } while(x < 4); print();");
		assertOutput("0\n1\n2\n3\n", "x = 0; while(x < 4) { print(x); x = x + 1; }; print();");
		assertOutput("0\n1\n2\n3\n", "for(x = 0; x < 4; x = x + 1) { print(x); }; print();");
		assertOutput("0\n1\n2\n3\n", "x = 0; for(; x < 4; x = x + 1) { print(x); }; print();");
		assertOutput("0\n1\n2\n3\n", "for(x = 0; x < 4; ) { print(x); x = x + 1; }; print();");
		assertOutput("0\n1\n2\n3\n", "x = 0; for(; x < 4; ) { print(x); x = x + 1; }; print();");
		assertOutput("0\n1\n2\n3\n", "for(var x = 0; x < 4; x = x + 1) { print(x); }; print();");
		assertOutput("0\n1\n2\n3\n", "for(var x = 0; x < 4; ) { print(x); x = x + 1; }; print();");
		assertOutput("0\n1\n2\n3\n", "for(var x = 0, y = 1; x < 4; x = x + y) { print(x); }; print();");
		assertOutput("0\n1\n2\n3\n", "for(var x = 0, y = 1; x < 4; ) { print(x); x = x + y; }; print();");
		assertOutput("0\n1\n3\n6\n", "for(x = 0, y = 1; x < 8; x = x + y, y = y + 1) { print(x); }; print();");
	}
	
	@Test
	public void ReturnStatement() {
		assertOutput("null", "print((function() { return; })());");
		assertOutput("1", "print((function() { return 1; })());");
	}
	
	@Test
	public void VizStatement() {
		assertOutput("1\n2", "print(1); @rect(); print(2);");
		assertOutput("1\n2", "print(1); @rect(x: 1); print(2);");
		assertOutput("1\n2", "print(1); @for(true) rect(x: 1); print(2);");
		assertOutput("1\n2", "print(1); @for(x <- [1]) rect(x: 1); print(2);");
		assertOutput("2", "var x = 2; @for(x <- [1]) rect(); print(x);");
		assertOutput("null", "@for(x <- [1]) rect(); print(x);");
		assertOutput("0", "var y = 0; @for(x <- [y++]) rect(); print(y);");
	}
	
	@Test
	public void SwitchStatement() {
	}
	
	@Test
	public void CaseBlock() {
	}
	
	@Test
	public void CaseClauses() {
	}
	
	@Test
	public void CaseClause() {
	}
	
	@Test
	public void DefaultClause() {
	}
	
	@Test
	public void FunctionDeclaration() {
		assertOutput("1", "function f(x) { print(x); }; f(1);");
		assertOutput("3", "function f(x, y) { print(x + y); }; f(1, 2);");
		assertOutput("720", "function f(n) { return (n == 0) ? 1 : n * f(n - 1); }; print(f(6));");
		assertOutput("720", "function f(n) { return (n == 0) ? 1 : f(n - 1) * n; }; print(f(6));");
		assertOutput("1\n1\n2\n3\n5\n8\n13\n21\n34\n55", "function f(n) { return (n < 2) ? 1 : f(n - 1) + f(n - 2); }; for(var x = 0; x < 10; x++) print(f(x));");
		assertOutput("2", "function f(n) { return function() { print(n); }; }; var g = f(2); g();");
		assertOutput("2", "function f(n) { return function() { print(n); }; }; f(2)();");
		
		assertOutput("1 null", "function f(x, y) { print(x, y); }; f(1);");
		assertOutput("1 2", "function f(x, y) { print(x, y); }; f(1, 2, 3);");
	}
	
	@Test
	public void FunctionExpression() {
		assertOutput("1", "var f = function(x) { print(x); }; f(1);");
		assertOutput("3", "var f = function(x, y) { print(x + y); }; f(1, 2);");
		assertOutput("1", "(function(x) { print(x); })(1);");
		assertOutput("3", "(function(x, y) { print(x + y); })(1, 2);");
	}
	
	@Test
	public void FormalParameterList() {
	}
	
	@Test
	public void FunctionBody() {
	}
	
	@Test
	public void Program() {
	}
	
	@Test
	public void SourceElements() {
	}

	@Test
	public void SourceElement() {
	}
	
	@Test
	public void CallExpression() {
		assertOutput("", "print();");
		assertOutput("1", "print(1);");
		assertOutput("1 2", "print(1, 2);");
		assertOutput("1 2 3", "print(1, 2, 3);");
		
		assertOutput("1", "print({a: function() { return 1; }}.a());");
		assertOutput("2", "print([function() { return 1; }, function() { return 2; }][1+0]());");
	}
}