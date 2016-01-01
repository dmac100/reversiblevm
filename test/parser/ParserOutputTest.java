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
		assertParseOutput("null;", Arrays.asList("PUSH: null", "POP"));
	}
	
	@Test
	public void BooleanLiteral() {
		assertParseOutput("true;", Arrays.asList("PUSH: true", "POP"));
		assertParseOutput("false;", Arrays.asList("PUSH: false", "POP"));
	}
	
	@Test
	public void NumericLiteral() {
		assertParseOutput("-10;", Arrays.asList("PUSH: -10", "POP"));
		assertParseOutput("-10.5;", Arrays.asList("PUSH: -10.5", "POP"));
		assertParseOutput("10;", Arrays.asList("PUSH: 10", "POP"));
		assertParseOutput("10.5;", Arrays.asList("PUSH: 10.5", "POP"));
	}
	
	@Test
	public void StringLiteral() {
		assertParseOutput("'';", Arrays.asList("PUSH: ", "POP"));
		assertParseOutput("'a';", Arrays.asList("PUSH: a", "POP"));
		assertParseOutput("\"\";", Arrays.asList("PUSH: ", "POP"));
		assertParseOutput("\"a\";", Arrays.asList("PUSH: a", "POP"));
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
		assertParseOutput("x++;", Arrays.asList("LOAD: x", "DUP", "PUSH: 1", "ADD", "STORE: x", "POP"));
		assertParseOutput("x--;", Arrays.asList("LOAD: x", "DUP", "PUSH: 1", "MINUS", "STORE: x", "POP"));
	}
	
	@Test
	public void UnaryExpression() {
		assertParseOutput("++x;", Arrays.asList("LOAD: x", "PUSH: 1", "ADD", "DUP", "STORE: x", "POP"));
		assertParseOutput("--x;", Arrays.asList("LOAD: x", "PUSH: 1", "MINUS", "DUP", "STORE: x", "POP"));
		assertParseOutput("+x;", Arrays.asList("LOAD: x", "UNARYPLUS", "POP"));
		assertParseOutput("-x;", Arrays.asList("LOAD: x", "UNARYMINUS", "POP"));
		assertParseOutput("~x;", Arrays.asList("LOAD: x", "BITWISENOT", "POP"));
		assertParseOutput("!x;", Arrays.asList("LOAD: x", "NOT", "POP"));
		assertParseOutput("void x;", Arrays.asList("LOAD: x", "POP", "PUSH: null", "POP"));
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
		assertParseOutput("true && false;", Arrays.asList("PUSH: true", "DUP", "JUMPIFFALSE: 2", "PUSH: false", "AND", "POP"));
	}
	
	@Test
	public void LogicalORExpression() {
		assertParseOutput("true || false;", Arrays.asList("PUSH: true", "DUP", "JUMPIFTRUE: 2", "PUSH: false", "OR", "POP"));
	}
	
	@Test
	public void ConditionalExpression() {
		assertParseOutput("x ? y : z;", Arrays.asList("LOAD: x", "JUMPIFFALSE: 2", "LOAD: y", "JUMP: 1", "LOAD: z", "POP"));
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
		assertParseOutput("{ }", Arrays.<String>asList());
		assertParseOutput("{ 1; }", Arrays.asList("PUSH: 1", "POP"));
		assertParseOutput("{ 1; 2; }", Arrays.asList("PUSH: 1", "POP", "PUSH: 2", "POP"));
		assertParseOutput("{ 1; 2; 3; }", Arrays.asList("PUSH: 1", "POP", "PUSH: 2", "POP", "PUSH: 3", "POP"));
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
		assertParseOutput(";", Arrays.<String>asList());
	}
	
	@Test
	public void ExpressionStatement() {
		assertParseOutput("1;", Arrays.asList("PUSH: 1", "POP"));
	}
	
	@Test
	public void IfStatement() {
		assertParseOutput("if(x) { a; }", Arrays.asList("LOAD: x", "JUMPIFFALSE: 2", "LOAD: a", "POP"));
		assertParseOutput("if(x) { a; } else { b; }", Arrays.asList("LOAD: x", "JUMPIFFALSE: 3", "LOAD: a", "POP", "JUMP: 2", "LOAD: b", "POP"));
		assertParseOutput("if(x) { a; } else if(y) { b; } else { c; }", Arrays.asList("LOAD: x", "JUMPIFFALSE: 3", "LOAD: a", "POP", "JUMP: 7", "LOAD: y", "JUMPIFFALSE: 3", "LOAD: b", "POP", "JUMP: 2", "LOAD: c", "POP"));
	}
	
	@Test
	public void IterationStatement() {
		assertParseOutput("do { a; } while(x);", Arrays.asList("LOAD: a", "POP", "LOAD: x", "JUMPIFTRUE: -4"));
		assertParseOutput("while(x) { b; }", Arrays.asList("LOAD: x", "JUMPIFFALSE: 3", "LOAD: b", "POP", "JUMP: -5"));
		assertParseOutput("for(x; y; z) { b; }", Arrays.asList("LOAD: x", "POP", "LOAD: y", "JUMPIFFALSE: 7", "LOAD: b", "POP", "LOAD: z", "POP", "JUMP: -7"));
		assertParseOutput("for(var x; y; z) { b; }", Arrays.asList("LOCAL: x", "LOAD: y", "JUMPIFFALSE: 7", "LOAD: b", "POP", "LOAD: z", "POP", "JUMP: -7"));
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
		assertParseOutput("print(1, 2, 3);", Arrays.asList(
			"PUSH: 3",
			"PUSH: 2",
			"PUSH: 1",
			"PUSH: 3",
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
