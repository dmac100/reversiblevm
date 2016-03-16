package backend.runtime;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import backend.instruction.Instruction;

public class StepBackwardTest {
	@Test
	public void Literal() {
	}
	
	@Test
	public void NullLiteral() {
		assertStepBackward("print(null);");
	}
	
	@Test
	public void BooleanLiteral() {
		assertStepBackward("print(true);");
		assertStepBackward("print(false);");
	}
	
	@Test
	public void NumericLiteral() {
		assertStepBackward("print(-10);");
		assertStepBackward("print(-10.5);");
		assertStepBackward("print(10);");
		assertStepBackward("print(10.5);");
	}
	
	@Test
	public void StringLiteral() {
		assertStepBackward("print('');");
		assertStepBackward("print(\"\");");
		assertStepBackward("print('a');");
		assertStepBackward("print(\"a\");");
	}
	
	@Test
	public void Identifier() {
	}

	@Test
	public void PrimaryExpression() {
	}
	
	@Test
	public void ArrayLiteral() {
		assertStepBackward("print([]);");
		assertStepBackward("print([1, 2, 3]);");
		
		assertStepBackward("a = [3, 4, 5]; print(a[2]);");
		assertStepBackward("a = [[1, 2], [3, 4]]; print(a[1][0]);");
		
		assertStepBackward("print([][0]);");
		
		assertStepBackward("var a = []; a[0] = a; print(a);");
		assertStepBackward("a = [[0]]; a[1] = a[0]; print(a);");
	}
	
	@Test
	public void Elision() {
	}
	
	@Test
	public void ObjectLiteral() {
		assertStepBackward("var x = ({}); print(x);");
		assertStepBackward("var x = ({ a: 1 }); print(x);");
		assertStepBackward("var x = ({ a: 1, b: 2 }); print(x);");
		assertStepBackward("var x = ({ a: { b: 2 } }); print(x);");
		
		assertStepBackward("var x = ({ a: 1 }); print(x.a);");
		assertStepBackward("var x = ({ a: { b: 1 } }); print(x.a.b);");
		assertStepBackward("var x = ({ f: function() { print(1); } }); x.f();");
		
		assertStepBackward("print({}.a);");
		
		assertStepBackward("var a = {}; a.a = a; print(a);");
		assertStepBackward("a = {a: [1]}; a.b = a.a; print(a);");
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
		assertStepBackward("x = 1; x++; print(x);");
		assertStepBackward("x = 1; print(x++);");
		assertStepBackward("x = 2; x--; print(x);");
		assertStepBackward("x = 2; print(x--);");
		
		assertStepBackward("x = { y: 1 }; x.y++; print(x.y);");
		assertStepBackward("x = { y: 1 }; print(x.y++);");
		assertStepBackward("x = { y: 2 }; x.y--; print(x.y);");
		assertStepBackward("x = { y: 2 }; print(x.y--);");
		
		assertStepBackward("x = [1]; x[0]++; print(x[0]);");
		assertStepBackward("x = [1]; print(x[0]++);");
		assertStepBackward("x = [2]; x[0]--; print(x[0]);");
		assertStepBackward("x = [2]; print(x[0]--);");
	}
	
	@Test
	public void UnaryExpression() {
		assertStepBackward("print(+5);");
		assertStepBackward("print(-5);");
		assertStepBackward("print(~5);");
		assertStepBackward("print(!true);");
		assertStepBackward("print(void 5);");
		
		assertStepBackward("x = 1; ++x; print(x);");
		assertStepBackward("x = 1; print(++x);");
		assertStepBackward("x = 2; --x; print(x);");
		assertStepBackward("x = 2; print(--x);");
		
		assertStepBackward("x = { y: 1 }; ++x.y; print(x.y);");
		assertStepBackward("x = { y: 1 }; print(++x.y);");
		assertStepBackward("x = { y: 2 }; --x.y; print(x.y);");
		assertStepBackward("x = { y: 2 }; print(--x.y);");
		
		assertStepBackward("x = [1]; ++x[0]; print(x[0]);");
		assertStepBackward("x = [1]; print(++x[0]);");
		assertStepBackward("x = [2]; --x[0]; print(x[0]);");
		assertStepBackward("x = [2]; print(--x[0]);");
	}
	
	@Test
	public void MultiplicativeExpression() {
		assertStepBackward("print(2 * 3);");
		assertStepBackward("print(10 / 5);");
		assertStepBackward("print(8 % 5);");
	}
	
	@Test
	public void AdditiveExpression() {
		assertStepBackward("print(2 + 3);");
		assertStepBackward("print(5 - 3);");
		
		assertStepBackward("print('2' + 3);");
		assertStepBackward("print(2 + '3');");
		assertStepBackward("print('2' + '3');");
	}
	
	@Test
	public void ShiftExpression() {
		assertStepBackward("print(1 << 3);");
		assertStepBackward("print(8 >> 1);");
		assertStepBackward("print(-14 >>> 30);");
		assertStepBackward("print(-14 >> 30);");
	}
	
	@Test
	public void RelationalExpression() {
		assertStepBackward("print(1 < 3);");
		assertStepBackward("print(3 > 1);");
		assertStepBackward("print(1 <= 3);");
		assertStepBackward("print(3 >= 1);");
		
		assertStepBackward("print(1 > 3);");
		assertStepBackward("print(3 < 1);");
		assertStepBackward("print(1 >= 3);");
		assertStepBackward("print(3 <= 1);");
		
		assertStepBackward("print(1 > 1);");
		assertStepBackward("print(1 < 1);");
		assertStepBackward("print(1 >= 1);");
		assertStepBackward("print(1 <= 1);");
	}
	
	@Test
	public void EqualityExpression() {
		assertStepBackward("print(true == true);");
		assertStepBackward("print(false == false);");
		assertStepBackward("print(true == false);");
		assertStepBackward("print(true == true);");
		assertStepBackward("print(true == false);");
		assertStepBackward("print(1 == 1);");
		assertStepBackward("print(1 == 2);");
		assertStepBackward("print(print == print);");
		assertStepBackward("print(null == null);");
		assertStepBackward("print('a' == 'a');");
		assertStepBackward("print('a' == 'b');");
		
		assertStepBackward("print(true != true);");
		assertStepBackward("print(false != false);");
		assertStepBackward("print(true != false);");
		assertStepBackward("print(true != true);");
		assertStepBackward("print(true != false);");
		assertStepBackward("print(1 != 1);");
		assertStepBackward("print(1 != 2);");
		assertStepBackward("print(print != print);");
		assertStepBackward("print(null != null);");
		assertStepBackward("print('a' != 'a');");
		assertStepBackward("print('a' != 'b');");
	}
	
	@Test
	public void BitwiseANDExpression() {
		assertStepBackward("print(5 & 7);");
	}
	
	@Test
	public void BitwiseXORExpression() {
		assertStepBackward("print(5 ^ 3);");
	}
	
	@Test
	public void BitwiseORExpression() {
		assertStepBackward("print(5 | 3);");
	}
	
	@Test
	public void LogicalANDExpression() {
		assertStepBackward("print(true && true);");
		assertStepBackward("print(true && false);");
		assertStepBackward("print(false && true);");
		assertStepBackward("print(false && false);");
	}
	
	@Test
	public void LogicalORExpression() {
		assertStepBackward("print(true || true);");
		assertStepBackward("print(true || false);");
		assertStepBackward("print(false || true);");
		assertStepBackward("print(false || false);");
	}
	
	@Test
	public void ConditionalExpression() {
		assertStepBackward("print(true ? 1 : 2);");
		assertStepBackward("print(false ? 1 : 2);");
	}
	
	@Test
	public void AssignmentExpression() {
		assertStepBackward("x = 2; print(x);");
		assertStepBackward("x = y = 2; print(x, y);");
		assertStepBackward("x = 1; y = 2; print(x + y);");
		assertStepBackward("x = 1; x = x + 1; y = 2; print(x + y);");
		
		assertStepBackward("x = { y: 2 }; x.y = 3; print(x.y);");
		assertStepBackward("x = { y: { z: 2 } }; x.y.z = 3; print(x.y.z);");
		
		assertStepBackward("x = [2]; x[0] = 3; print(x[0]);");
	}
	
	@Test
	public void CompoundAssignmentOperator() {
		assertStepBackward("x = 2; x *= 3; print(x);");
		assertStepBackward("x = 4; x /= 2; print(x);");
		assertStepBackward("x = 5; x %= 3; print(x);");
		assertStepBackward("x = 2; x += 3; print(x);");
		assertStepBackward("x = 5; x -= 3; print(x);");
		assertStepBackward("x = 2; x <<= 1; print(x);");
		assertStepBackward("x = 2; x >>= 1; print(x);");
		assertStepBackward("x = 2; x >>>= 1; print(x);");
		assertStepBackward("x = 2; x &= 3; print(x);");
		assertStepBackward("x = 2; x ^= 3; print(x);");
		assertStepBackward("x = 2; x |= 3; print(x);");
		
		assertStepBackward("x = { y: 2 }; x.y += 3; print(x.y);");
		assertStepBackward("x = { y: { z: 2 } }; x.y.z += 3; print(x.y.z);");
		
		assertStepBackward("x = [2]; x[0] += 3; print(x[0]);");
		assertStepBackward("x = [[2]]; x[0][0] += 3; print(x[0][0]);");
	}
	
	@Test
	public void Expression() {
	}
	
	@Test
	public void Statement() {
	}
	
	@Test
	public void Block() {
		assertStepBackward("{ }");
		assertStepBackward("{ print(1); }");
		assertStepBackward("{ print(1); print(2); }");
	}
	
	@Test
	public void StatementList() {
	}
	
	@Test
	public void VariableStatement() {
		assertStepBackward("var x = 1, y = 2; print(x); print(y);");
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
		assertStepBackward(";");
	}
	
	@Test
	public void ExpressionStatement() {
	}
	
	@Test
	public void IfStatement() {
		assertStepBackward("if(true) { print(1); }; print();");
		assertStepBackward("if(false) { print(1); }; print();");
		
		assertStepBackward("if(true) { print(1); } else { print(2); }; print();");
		assertStepBackward("if(false) { print(1); } else { print(2); }; print();");
		
		assertStepBackward("if(true) { print(1); } else if(true) { print(2); } else { print(3); }; print();");
		assertStepBackward("if(true) { print(1); } else if(false) { print(2); } else { print(3); }; print();");
		assertStepBackward("if(false) { print(1); } else if(true) { print(2); } else { print(3); }; print();");
		assertStepBackward("if(false) { print(1); } else if(false) { print(2); } else { print(3); }; print();");
	}
	
	@Test
	public void IterationStatement() {
		assertStepBackward("x = 0; do { print(x); x = x + 1; } while(x < 4); print();");
		assertStepBackward("x = 0; while(x < 4) { print(x); x = x + 1; }; print();");
		assertStepBackward("for(x = 0; x < 4; x = x + 1) { print(x); }; print();");
		assertStepBackward("x = 0; for(; x < 4; x = x + 1) { print(x); }; print();");
		assertStepBackward("for(x = 0; x < 4; ) { print(x); x = x + 1; }; print();");
		assertStepBackward("x = 0; for(; x < 4; ) { print(x); x = x + 1; }; print();");
		assertStepBackward("for(var x = 0; x < 4; x = x + 1) { print(x); }; print();");
		assertStepBackward("for(var x = 0; x < 4; ) { print(x); x = x + 1; }; print();");
		assertStepBackward("for(var x = 0, y = 1; x < 4; x = x + y) { print(x); }; print();");
		assertStepBackward("for(var x = 0, y = 1; x < 4; ) { print(x); x = x + y; }; print();");
		assertStepBackward("for(x = 0, y = 1; x < 8; x = x + y, y = y + 1) { print(x); }; print();");
	}
	
	@Test
	public void ReturnStatement() {
		assertStepBackward("print((function() { return; })());");
		assertStepBackward("print((function() { return 1; })());");
	}
	
	@Test
	public void VizStatement() {
		assertStepBackward("print(1); @rect(); print(2);");
		assertStepBackward("print(1); @rect(x: 1); print(2);");
		assertStepBackward("print(1); @for(true) rect(x: 1); print(2);");
		assertStepBackward("print(1); @for(x <- [1]) rect(x: 1); print(2);");
		assertStepBackward("print(1); @for(3 > 2) rect(x: 1); print(2);");
		assertStepBackward("print(1); function f() { return 1; }; @for(x <- [1]) rect(x: f()); print(2);");
		assertStepBackward("print(1); a = [1, 2]; @for(i <- a) rect(x: i); print(2);");
		assertStepBackward("print(1); for(var i = 0; i < 3; i++) @rect(x: i); print(2);");
		
		assertStepBackward("print(1); @for(x <- null) rect(x: 1); print(2);");
		assertStepBackward("print(1); @for(x <- null) rect(x: 1); print(2);");
		assertStepBackward("print(1); @for(null) rect(x: 1); print(2);");
		assertStepBackward("print(1); @for(x <- [1], x <- null) rect(x: 1); print(2);");
		assertStepBackward("print(1); @for(x <- [1], null) rect(x: 1); print(2);");
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
		assertStepBackward("function f(x) { print(x); }; f(1);");
		assertStepBackward("function f(x, y) { print(x + y); }; f(1, 2);");
		assertStepBackward("function f(n) { return function() { print(n); }; }; var g = f(2); g();");
		assertStepBackward("function f(n) { return function() { print(n); }; }; f(2)();");
		
		assertStepBackward("function f(x, y) { print(x, y); }; f(1);");
		assertStepBackward("function f(x, y) { print(x, y); }; f(1, 2, 3);");
		
		assertStepBackward("function f() { var x = 5; } function g() { return f(); } g();");
	}
	
	@Test
	public void FunctionExpression() {
		assertStepBackward("var f = function(x) { print(x); }; f(1);");
		assertStepBackward("var f = function(x, y) { print(x + y); }; f(1, 2);");
		assertStepBackward("(function(x) { print(x); })(1);");
		assertStepBackward("(function(x, y) { print(x + y); })(1, 2);");
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
		assertStepBackward("print();");
		assertStepBackward("print(1);");
		assertStepBackward("print(1, 2);");
		assertStepBackward("print(1, 2, 3);");
		
		assertStepBackward("print({a: function() { return 1; }}.a());");
		assertStepBackward("print([function() { return 1; }, function() { return 2; }][1+0]());");
	}

	private static void assertStepBackward(String program) {
		assertStepBackward(program, getProgramLength(program));
	}
	
	private static void assertStepBackwardAllLengths(String program) {
		int maxLength = getProgramLength(program);
		for(int length = 0; length <= maxLength; length++) {
			assertStepBackward(program, length);
		}
	}
	
	private static int getProgramLength(String program) {
		Runtime runtime = new Runtime();
		Engine engine = new Engine(runtime, Engine.compile(program));
		
		int length = 0;
		
		while(!runtime.atEnd()) {
			engine.stepForward();
			length++;
		}
		
		return length;
	}

	private static void assertStepBackward(String program, int length) {
		Runtime runtime = new Runtime();
		List<Instruction> instructions = Engine.compile(program);
		
		Engine engine = new Engine(runtime, instructions);
		
		ArrayList<String> states = new ArrayList<>();
		
		states.add(runtime.getState());
		for(int s = 0; s < length; s++) {
			engine.stepForward();
			states.add(runtime.getState());
		}
		
		for(int i = states.size() - 1; i >= 0; i--) {
			assertEquals(states.get(i), runtime.getState());
			engine.stepBackward();
		}
	}
}