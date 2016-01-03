package runtime;

import static instruction.AddInstruction.Add;
import static instruction.BitwiseAndInstruction.BitwiseAnd;
import static instruction.BitwiseOrInstruction.BitwiseOr;
import static instruction.BitwiseXorInstruction.BitwiseXor;
import static instruction.DivideInstruction.Divide;
import static instruction.MinusInstruction.Minus;
import static instruction.ModuloInstruction.Modulo;
import static instruction.MultiplyInstruction.Multiply;
import static instruction.ShiftLeftInstruction.ShiftLeft;
import static instruction.ShiftRightInstruction.ShiftRight;
import static instruction.UnsignedShiftRightInstruction.UnsignedShiftRight;
import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import instruction.Instruction;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.parboiled.BaseParser;
import org.parboiled.Parboiled;
import org.parboiled.parserunners.ReportingParseRunner;
import org.parboiled.support.ParseTreeUtils;
import org.parboiled.support.ParsingResult;

import parser.Parser;

public class ParseAndRunTest {
	private Parser parser = Parboiled.createParser(Parser.class);
	
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
	}
	
	@Test
	public void Elision() {
	}
	
	@Test
	public void ObjectLiteral() {
		assertOutput("1", "var x = ({ a: 1 }); print(x.a);");
		assertOutput("1", "var x = ({ a: { b: 1 } }); print(x.a.b);");
		assertOutput("1", "var x = ({ f: function() { print(1); } }); x.f();");
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
	}
	
	@Test
	public void UnaryExpression() {
		assertOutput("2", "x = 1; ++x; print(x);");
		assertOutput("2", "x = 1; print(++x);");
		assertOutput("1", "x = 2; --x; print(x);");
		assertOutput("1", "x = 2; print(--x);");
		assertOutput("5", "print(+5);");
		assertOutput("-5", "print(-5);");
		assertOutput("-6", "print(~5);");
		assertOutput("false", "print(!true);");
		assertOutput("null", "print(void 5);");
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
		assertOutput("1", "if(true) { print(1); }");
		assertOutput("", "if(false) { print(1); }");
		
		assertOutput("1", "if(true) { print(1); } else { print(2); }");
		assertOutput("2", "if(false) { print(1); } else { print(2); }");
		
		assertOutput("1", "if(true) { print(1); } else if(true) { print(2); } else { print(3); }");
		assertOutput("1", "if(true) { print(1); } else if(false) { print(2); } else { print(3); }");
		assertOutput("2", "if(false) { print(1); } else if(true) { print(2); } else { print(3); }");
		assertOutput("3", "if(false) { print(1); } else if(false) { print(2); } else { print(3); }");
	}
	
	@Test
	public void IterationStatement() {
		assertOutput("0\n1\n2\n3", "x = 0; do { print(x); x = x + 1; } while(x < 4);");
		assertOutput("0\n1\n2\n3", "x = 0; while(x < 4) { print(x); x = x + 1; }");
		assertOutput("0\n1\n2\n3", "for(x = 0; x < 4; x = x + 1) { print(x); }");
		assertOutput("0\n1\n2\n3", "x = 0; for(; x < 4; x = x + 1) { print(x); }");
		assertOutput("0\n1\n2\n3", "for(x = 0; x < 4; ) { print(x); x = x + 1; }");
		assertOutput("0\n1\n2\n3", "x = 0; for(; x < 4; ) { print(x); x = x + 1; }");
		assertOutput("0\n1\n2\n3", "for(var x = 0; x < 4; x = x + 1) { print(x); }");
		assertOutput("0\n1\n2\n3", "for(var x = 0; x < 4; ) { print(x); x = x + 1; }");
		assertOutput("0\n1\n2\n3", "for(var x = 0, y = 1; x < 4; x = x + y) { print(x); }");
		assertOutput("0\n1\n2\n3", "for(var x = 0, y = 1; x < 4; ) { print(x); x = x + y; }");
	}
	
	@Test
	public void ReturnStatement() {
		assertOutput("null", "print((function() { return; })());");
		assertOutput("1", "print((function() { return 1; })());");
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