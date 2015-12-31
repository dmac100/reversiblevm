package parser;

import static instruction.AddInstruction.Add;
import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
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
	public void Literal() {
	}
	
	@Test
	public void NullLiteral() {
	}
	
	@Test
	public void BooleanLiteral() {
	}
	
	@Test
	public void NumericLiteral() {
	}
	
	@Test
	public void StringLiteral() {
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
		assertParseOutput("+5;", Arrays.asList("PUSH: 5", "UNARYPLUS", "POP"));
		assertParseOutput("-5;", Arrays.asList("PUSH: 5", "UNARYMINUS", "POP"));
		assertParseOutput("~5;", Arrays.asList("PUSH: 5", "BITWISENOT", "POP"));
		assertParseOutput("!5;", Arrays.asList("PUSH: 5", "NOT", "POP"));
		assertParseOutput("void 5;", Arrays.asList("PUSH: 5", "POP", "PUSH: null", "POP"));
	}
	
	@Test
	public void MultiplicativeExpression() {
		assertParseOutput("1 * 2;", Arrays.asList("PUSH: 1", "PUSH: 2", "MULTIPLY", "POP"));
		assertParseOutput("1 / 2;", Arrays.asList("PUSH: 1", "PUSH: 2", "DIVIDE", "POP"));
		assertParseOutput("1 % 2;", Arrays.asList("PUSH: 1", "PUSH: 2", "MODULO", "POP"));
	}
	
	@Test
	public void AdditiveExpression() {
		assertParseOutput("1 + 2;", Arrays.asList("PUSH: 1", "PUSH: 2", "ADD", "POP"));
		assertParseOutput("1 - 2;", Arrays.asList("PUSH: 1", "PUSH: 2", "MINUS", "POP"));
	}
	
	@Test
	public void ShiftExpression() {
		assertParseOutput("1 << 2;", Arrays.asList("PUSH: 1", "PUSH: 2", "SHIFTLEFT", "POP"));
		assertParseOutput("1 >>> 2;", Arrays.asList("PUSH: 1", "PUSH: 2", "UNSIGNEDSHIFTRIGHT", "POP"));
		assertParseOutput("1 >> 2;", Arrays.asList("PUSH: 1", "PUSH: 2", "SHIFTRIGHT", "POP"));
	}
	
	@Test
	public void RelationalExpression() {
		assertParseOutput("1 < 2;", Arrays.asList("PUSH: 1", "PUSH: 2", "LESSTHAN", "POP"));
		assertParseOutput("1 > 2;", Arrays.asList("PUSH: 1", "PUSH: 2", "GREATERTHAN", "POP"));
		assertParseOutput("1 <= 2;", Arrays.asList("PUSH: 1", "PUSH: 2", "LESSTHANEQUAL", "POP"));
		assertParseOutput("1 >= 2;", Arrays.asList("PUSH: 1", "PUSH: 2", "GREATERTHANEQUAL", "POP"));
	}
	
	@Test
	public void EqualityExpression() {
		assertParseOutput("1 == 2;", Arrays.asList("PUSH: 1", "PUSH: 2", "EQUAL", "POP"));
		assertParseOutput("1 != 2;", Arrays.asList("PUSH: 1", "PUSH: 2", "EQUAL", "NOT", "POP"));
	}
	
	@Test
	public void BitwiseANDExpression() {
		assertParseOutput("1 & 2;", Arrays.asList("PUSH: 1", "PUSH: 2", "BITWISEAND", "POP"));
	}
	
	@Test
	public void BitwiseXORExpression() {
		assertParseOutput("1 ^ 2;", Arrays.asList("PUSH: 1", "PUSH: 2", "BITWISEXOR", "POP"));
	}
	
	@Test
	public void BitwiseORExpression() {
		assertParseOutput("1 | 2;", Arrays.asList("PUSH: 1", "PUSH: 2", "BITWISEOR", "POP"));
	}
	
	@Test
	public void LogicalANDExpression() {
		assertParseOutput("1 && 2;", Arrays.asList("PUSH: 1", "PUSH: 2", "AND", "POP"));
	}
	
	@Test
	public void LogicalORExpression() {
		assertParseOutput("1 || 2;", Arrays.asList("PUSH: 1", "PUSH: 2", "OR", "POP"));
	}
	
	@Test
	public void ConditionalExpression() {
	}
	
	@Test
	public void AssignmentExpression() {
		assertParseOutput("x = 2;", Arrays.asList("PUSH: 2", "DUP", "STORE: x", "POP"));
		assertParseOutput("x = y = 2;", Arrays.asList("PUSH: 2", "DUP", "STORE: y", "DUP", "STORE: x", "POP"));
		assertParseOutput("x = x + 2;", Arrays.asList("LOAD: x", "PUSH: 2", "ADD", "DUP", "STORE: x", "POP"));
		assertParseOutput("x += 2;", Arrays.asList("LOAD: x", "PUSH: 2", "ADD", "DUP", "STORE: x", "POP"));
		
		assertParseOutput("x *= 2;", Arrays.asList("LOAD: x", "PUSH: 2", "MULTIPLY", "DUP", "STORE: x", "POP"));
		assertParseOutput("x /= 2;", Arrays.asList("LOAD: x", "PUSH: 2", "DIVIDE", "DUP", "STORE: x", "POP"));
		assertParseOutput("x %= 2;", Arrays.asList("LOAD: x", "PUSH: 2", "MODULO", "DUP", "STORE: x", "POP"));
		assertParseOutput("x += 2;", Arrays.asList("LOAD: x", "PUSH: 2", "ADD", "DUP", "STORE: x", "POP"));
		assertParseOutput("x -= 2;", Arrays.asList("LOAD: x", "PUSH: 2", "MINUS", "DUP", "STORE: x", "POP"));
		assertParseOutput("x <<= 2;", Arrays.asList("LOAD: x", "PUSH: 2", "SHIFTLEFT", "DUP", "STORE: x", "POP"));
		assertParseOutput("x >>= 2;", Arrays.asList("LOAD: x", "PUSH: 2", "SHIFTRIGHT", "DUP", "STORE: x", "POP"));
		assertParseOutput("x >>>= 2;", Arrays.asList("LOAD: x", "PUSH: 2", "UNSIGNEDSHIFTRIGHT", "DUP", "STORE: x", "POP"));
		assertParseOutput("x &= 2;", Arrays.asList("LOAD: x", "PUSH: 2", "BITWISEAND", "DUP", "STORE: x", "POP"));
		assertParseOutput("x ^= 2;", Arrays.asList("LOAD: x", "PUSH: 2", "BITWISEXOR", "DUP", "STORE: x", "POP"));
		assertParseOutput("x |= 2;", Arrays.asList("LOAD: x", "PUSH: 2", "BITWISEOR", "DUP", "STORE: x", "POP"));
	}
	
	@Test
	public void CompoundAssignmentOperator() {
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
		assertParseOutput("var x;", Arrays.<String>asList("LOCAL: x"));
		assertParseOutput("var x = 1;", Arrays.<String>asList("LOCAL: x", "PUSH: 1", "STORE: x"));
		assertParseOutput("var x = 1, y = 2;", Arrays.<String>asList("LOCAL: x", "PUSH: 1", "STORE: x", "LOCAL: y", "PUSH: 2", "STORE: y"));
		assertParseOutput("var x, y, z;", Arrays.<String>asList("LOCAL: x", "LOCAL: y", "LOCAL: z"));
	}
	
	@Test
	public void VariableDeclarationList() {
	}
	
	@Test
	public void VariableDeclaration() {
		assertParseOutput("var x;", Arrays.<String>asList("LOCAL: x"));
		assertParseOutput("var x = 1;", Arrays.<String>asList("LOCAL: x", "PUSH: 1", "STORE: x"));
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
