package runtime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import instruction.Instruction;

import java.util.Arrays;
import java.util.List;

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
	}
	
	@Test
	public void PropertyNameAndValueList() {
	}
	
	@Test
	public void PropertyName() {
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
	}
	
	@Test
	public void UnaryExpression() {
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
	}
	
	@Test
	public void AssignmentExpression() {
	}
	
	@Test
	public void AssignmentOperator() {
	}
	
	@Test
	public void Expression() {
	}
	
	@Test
	public void Statement() {
	}
	
	@Test
	public void Block() {
	}
	
	@Test
	public void StatementList() {
	}
	
	@Test
	public void VariableStatement() {
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
	}
	
	@Test
	public void ExpressionStatement() {
	}
	
	@Test
	public void IfStatement() {
	}
	
	@Test
	public void IterationStatement() {
	}
	
	@Test
	public void ReturnStatement() {
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
	}
	
	@Test
	public void FunctionExpression() {
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
	}
	
	private void assertOutput(String expected, String program) {
		ParsingResult<List<Instruction>> result = new ReportingParseRunner<List<Instruction>>(parser.Sequence(parser.Program(), BaseParser.EOI)).run(program);
		System.out.println(ParseTreeUtils.printNodeTree(result));
		
		List<Instruction> instructions = result.valueStack.pop();
		
		System.out.println("Instructions: " + instructions);
		
		Runtime runtime = new Runtime();
		
		new Engine().run(runtime, instructions);
		
		System.out.println("Output: " + runtime.getOutput());
		
		assertEquals(Arrays.asList(expected), runtime.getOutput());
		assertTrue(runtime.getErrors().isEmpty());
		assertTrue(runtime.getStack().isEmpty());
	}
}
