package parser;

import static org.junit.Assert.assertEquals;

import instruction.Instruction;

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
		assertParseOutput("([]);", Arrays.asList("NEWARRAY", "POP"));
		assertParseOutput("([1]);", Arrays.asList("NEWARRAY", "DUP", "PUSH: 1", "PUSHELEMENT", "POP"));
		assertParseOutput("([1, 2]);", Arrays.asList("NEWARRAY", "DUP", "PUSH: 1", "PUSHELEMENT", "DUP", "PUSH: 2", "PUSHELEMENT", "POP"));
		assertParseOutput("([1, 2, 3]);", Arrays.asList("NEWARRAY", "DUP", "PUSH: 1", "PUSHELEMENT", "DUP", "PUSH: 2", "PUSHELEMENT", "DUP", "PUSH: 3", "PUSHELEMENT", "POP"));
	}
	
	@Test
	public void Elision() {
	}
	
	@Test
	public void ObjectLiteral() {
		assertParseOutput("({});", Arrays.asList("NEWOBJECT", "POP"));
		assertParseOutput("({ a: 1 });", Arrays.asList("NEWOBJECT", "DUP", "PUSH: 1", "SETPROPERTY: a", "POP"));
		assertParseOutput("({ a: 1, b: 2 });", Arrays.asList("NEWOBJECT", "DUP", "PUSH: 1", "SETPROPERTY: a", "DUP", "PUSH: 2", "SETPROPERTY: b", "POP"));
		assertParseOutput("({ a: 1, b: 2, c: 3 });", Arrays.asList("NEWOBJECT", "DUP", "PUSH: 1", "SETPROPERTY: a", "DUP", "PUSH: 2", "SETPROPERTY: b", "DUP", "PUSH: 3", "SETPROPERTY: c", "POP"));
	}
	
	@Test
	public void PropertyNameAndValueList() {
	}
	
	@Test
	public void MemberExpression() {
		assertParseOutput("a.b;", Arrays.asList("LOAD: a", "GETPROPERTY: b", "POP"));
		assertParseOutput("a.b.c;", Arrays.asList("LOAD: a", "GETPROPERTY: b", "GETPROPERTY: c", "POP"));
		
		assertParseOutput("a[1];", Arrays.asList("LOAD: a", "PUSH: 1", "GETELEMENT", "POP"));
		assertParseOutput("a[1][2];", Arrays.asList("LOAD: a", "PUSH: 1", "GETELEMENT", "PUSH: 2", "GETELEMENT", "POP"));
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
		assertParseOutput("x++;", Arrays.asList("LOAD: x", "LOAD: x", "PUSH: 1", "ADD", "STORE: x", "POP"));
		assertParseOutput("x--;", Arrays.asList("LOAD: x", "LOAD: x", "PUSH: 1", "MINUS", "STORE: x", "POP"));
		
		assertParseOutput("a.x++;", Arrays.asList("LOAD: a", "DUP", "GETPROPERTY: x", "SWAP", "DUP", "GETPROPERTY: x", "PUSH: 1", "ADD", "SETPROPERTY: x", "POP"));
		assertParseOutput("a.x--;", Arrays.asList("LOAD: a", "DUP", "GETPROPERTY: x", "SWAP", "DUP", "GETPROPERTY: x", "PUSH: 1", "MINUS", "SETPROPERTY: x", "POP"));
		
		assertParseOutput("a[x]++;", Arrays.asList("LOAD: a", "LOAD: x", "DUP2", "GETELEMENT", "SWAP2", "DUP2", "GETELEMENT", "PUSH: 1", "ADD", "SETELEMENT", "POP"));
		assertParseOutput("a[x]--;", Arrays.asList("LOAD: a", "LOAD: x", "DUP2", "GETELEMENT", "SWAP2", "DUP2", "GETELEMENT", "PUSH: 1", "MINUS", "SETELEMENT", "POP"));
	}
	
	@Test
	public void UnaryExpression() {
		assertParseOutput("+x;", Arrays.asList("LOAD: x", "UNARYPLUS", "POP"));
		assertParseOutput("-x;", Arrays.asList("LOAD: x", "UNARYMINUS", "POP"));
		assertParseOutput("~x;", Arrays.asList("LOAD: x", "BITWISENOT", "POP"));
		assertParseOutput("!x;", Arrays.asList("LOAD: x", "NOT", "POP"));
		assertParseOutput("void x;", Arrays.asList("LOAD: x", "POP", "PUSH: null", "POP"));
		
		assertParseOutput("++x;", Arrays.asList("LOAD: x", "PUSH: 1", "ADD", "STORE: x", "LOAD: x", "POP"));
		assertParseOutput("--x;", Arrays.asList("LOAD: x", "PUSH: 1", "MINUS", "STORE: x", "LOAD: x", "POP"));
		
		assertParseOutput("a.x++;", Arrays.asList("LOAD: a", "DUP", "GETPROPERTY: x", "SWAP", "DUP", "GETPROPERTY: x", "PUSH: 1", "ADD", "SETPROPERTY: x", "POP"));
		assertParseOutput("a.x--;", Arrays.asList("LOAD: a", "DUP", "GETPROPERTY: x", "SWAP", "DUP", "GETPROPERTY: x", "PUSH: 1", "MINUS", "SETPROPERTY: x", "POP"));
		
		assertParseOutput("a[x]++;", Arrays.asList("LOAD: a", "LOAD: x", "DUP2", "GETELEMENT", "SWAP2", "DUP2", "GETELEMENT", "PUSH: 1", "ADD", "SETELEMENT", "POP"));
		assertParseOutput("a[x]--;", Arrays.asList("LOAD: a", "LOAD: x", "DUP2", "GETELEMENT", "SWAP2", "DUP2", "GETELEMENT", "PUSH: 1", "MINUS", "SETELEMENT", "POP"));
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
		assertParseOutput("x = 2;", Arrays.asList("PUSH: 2", "STORE: x", "LOAD: x", "POP"));
		assertParseOutput("x = y = 2;", Arrays.asList("PUSH: 2", "STORE: y", "LOAD: y", "STORE: x", "LOAD: x", "POP"));
		assertParseOutput("x = x + 2;", Arrays.asList("LOAD: x", "PUSH: 2", "ADD", "STORE: x", "LOAD: x", "POP"));
		assertParseOutput("x += 2;", Arrays.asList("LOAD: x", "PUSH: 2", "ADD", "STORE: x", "LOAD: x", "POP"));
		
		assertParseOutput("x *= 2;", Arrays.asList("LOAD: x", "PUSH: 2", "MULTIPLY", "STORE: x", "LOAD: x", "POP"));
		assertParseOutput("x /= 2;", Arrays.asList("LOAD: x", "PUSH: 2", "DIVIDE", "STORE: x", "LOAD: x", "POP"));
		assertParseOutput("x %= 2;", Arrays.asList("LOAD: x", "PUSH: 2", "MODULO", "STORE: x", "LOAD: x", "POP"));
		assertParseOutput("x += 2;", Arrays.asList("LOAD: x", "PUSH: 2", "ADD", "STORE: x", "LOAD: x", "POP"));
		assertParseOutput("x -= 2;", Arrays.asList("LOAD: x", "PUSH: 2", "MINUS", "STORE: x", "LOAD: x", "POP"));
		assertParseOutput("x <<= 2;", Arrays.asList("LOAD: x", "PUSH: 2", "SHIFTLEFT", "STORE: x", "LOAD: x", "POP"));
		assertParseOutput("x >>= 2;", Arrays.asList("LOAD: x", "PUSH: 2", "SHIFTRIGHT", "STORE: x", "LOAD: x", "POP"));
		assertParseOutput("x >>>= 2;", Arrays.asList("LOAD: x", "PUSH: 2", "UNSIGNEDSHIFTRIGHT", "STORE: x", "LOAD: x", "POP"));
		assertParseOutput("x &= 2;", Arrays.asList("LOAD: x", "PUSH: 2", "BITWISEAND", "STORE: x", "LOAD: x", "POP"));
		assertParseOutput("x ^= 2;", Arrays.asList("LOAD: x", "PUSH: 2", "BITWISEXOR", "STORE: x", "LOAD: x", "POP"));
		assertParseOutput("x |= 2;", Arrays.asList("LOAD: x", "PUSH: 2", "BITWISEOR", "STORE: x", "LOAD: x", "POP"));
		
		assertParseOutput("x.y = 2;", Arrays.asList("LOAD: x", "DUP", "PUSH: 2", "SETPROPERTY: y", "GETPROPERTY: y", "POP"));
		assertParseOutput("x.y.z = 2;", Arrays.asList("LOAD: x", "GETPROPERTY: y", "DUP", "PUSH: 2", "SETPROPERTY: z", "GETPROPERTY: z", "POP"));
		assertParseOutput("x.y += 2;", Arrays.asList("LOAD: x", "DUP", "DUP", "GETPROPERTY: y", "PUSH: 2", "ADD", "SETPROPERTY: y", "GETPROPERTY: y", "POP"));
		
		assertParseOutput("x[y] = 2;", Arrays.asList("LOAD: x", "LOAD: y", "DUP2", "PUSH: 2", "SETELEMENT", "GETELEMENT", "POP"));
		assertParseOutput("x[y][z] += 2;", Arrays.asList("LOAD: x", "LOAD: y", "GETELEMENT", "LOAD: z", "DUP2", "DUP2", "GETELEMENT", "PUSH: 2", "ADD", "SETELEMENT", "GETELEMENT", "POP"));
		assertParseOutput("x[y] += 2;", Arrays.asList("LOAD: x", "LOAD: y", "DUP2", "DUP2", "GETELEMENT", "PUSH: 2", "ADD", "SETELEMENT", "GETELEMENT", "POP"));
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
		assertParseOutput("var x = 1;", Arrays.<String>asList("PUSH: 1", "LOCAL: x", "STORE: x"));
		assertParseOutput("var x = 1, y = 2;", Arrays.<String>asList("PUSH: 1", "LOCAL: x", "STORE: x", "PUSH: 2", "LOCAL: y", "STORE: y"));
		assertParseOutput("var x, y, z;", Arrays.<String>asList("LOCAL: x", "LOCAL: y", "LOCAL: z"));
	}
	
	@Test
	public void VariableDeclarationList() {
	}
	
	@Test
	public void VariableDeclaration() {
		assertParseOutput("var x;", Arrays.<String>asList("LOCAL: x"));
		assertParseOutput("var x = 1;", Arrays.<String>asList("PUSH: 1", "LOCAL: x", "STORE: x"));
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
		assertParseOutput("for(x; y; z) { b; }", Arrays.asList("LOAD: x", "POP", "LOAD: y", "JUMPIFFALSE: 5", "LOAD: b", "POP", "LOAD: z", "POP", "JUMP: -7"));
		assertParseOutput("for(var x; y; z) { b; }", Arrays.asList("LOCAL: x", "LOAD: y", "JUMPIFFALSE: 5", "LOAD: b", "POP", "LOAD: z", "POP", "JUMP: -7"));
	}
	
	@Test
	public void ReturnStatement() {
		assertParseOutput("function f() { return; }", Arrays.asList("STARTFUNCTION", "POP", "PUSH: null", "RETURN", "ENDFUNCTION", "LOCAL: f", "STORE: f"));
		assertParseOutput("function f() { return 1; }", Arrays.asList("STARTFUNCTION", "POP", "PUSH: null", "POP", "PUSH: 1", "RETURN", "ENDFUNCTION", "LOCAL: f", "STORE: f"));
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
		assertParseOutput("function f() { }", Arrays.asList("STARTFUNCTION", "POP", "PUSH: null", "ENDFUNCTION", "LOCAL: f", "STORE: f"));
		assertParseOutput("function f(x) { }", Arrays.asList("STARTFUNCTION", "POP", "LOCAL: x", "STORE: x", "PUSH: null", "ENDFUNCTION", "LOCAL: f", "STORE: f"));
		assertParseOutput("function f(x, y) { }", Arrays.asList("STARTFUNCTION", "POP", "LOCAL: y", "STORE: y", "LOCAL: x", "STORE: x", "PUSH: null", "ENDFUNCTION", "LOCAL: f", "STORE: f"));
		assertParseOutput("function f(x, y, z) { }", Arrays.asList("STARTFUNCTION", "POP", "LOCAL: z", "STORE: z", "LOCAL: y", "STORE: y", "LOCAL: x", "STORE: x", "PUSH: null", "ENDFUNCTION", "LOCAL: f", "STORE: f"));
		
		assertParseOutput("function f() { print(); }", Arrays.asList("STARTFUNCTION", "POP", "PUSH: null", "LOAD: print", "PUSH: 0", "SWAP", "CALL", "POP", "ENDFUNCTION", "LOCAL: f", "STORE: f"));
		assertParseOutput("function f(x) { print(x); }", Arrays.asList("STARTFUNCTION", "POP", "LOCAL: x", "STORE: x", "PUSH: null", "LOAD: print", "LOAD: x", "SWAP", "PUSH: 1", "SWAP", "CALL", "POP", "ENDFUNCTION", "LOCAL: f", "STORE: f"));
	}
	
	@Test
	public void FunctionExpression() {
		assertParseOutput("var f = function() { };", Arrays.asList("STARTFUNCTION", "POP", "PUSH: null", "ENDFUNCTION", "LOCAL: f", "STORE: f"));
		assertParseOutput("var f = function(x) { print(x); };", Arrays.asList("STARTFUNCTION", "POP", "LOCAL: x", "STORE: x", "PUSH: null", "LOAD: print", "LOAD: x", "SWAP", "PUSH: 1", "SWAP", "CALL", "POP", "ENDFUNCTION", "LOCAL: f", "STORE: f"));
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
	public void Comment() {
		assertParseOutput("// Comment", Arrays.asList("NOP"));
		assertParseOutput("/* Comment */", Arrays.asList("NOP"));
		assertParseOutput("/* Comment */ x;", Arrays.asList("LOAD: x", "POP"));
		assertParseOutput("x; /* Comment */", Arrays.asList("LOAD: x", "POP"));
		assertParseOutput("x; // Comment", Arrays.asList("LOAD: x", "POP"));
	}
	
	@Test
	public void CallExpression() {
		assertParseOutput("f();", Arrays.asList("LOAD: f", "PUSH: 0", "SWAP", "CALL", "POP"));
		assertParseOutput("f(3);", Arrays.asList("LOAD: f", "PUSH: 3", "SWAP", "PUSH: 1", "SWAP", "CALL", "POP"));
		assertParseOutput("f(3)(4);", Arrays.asList("LOAD: f", "PUSH: 3", "SWAP", "PUSH: 1", "SWAP", "CALL", "PUSH: 4", "SWAP", "PUSH: 1", "SWAP", "CALL", "POP"));
		assertParseOutput("f(3)(4, 5);", Arrays.asList("LOAD: f", "PUSH: 3", "SWAP", "PUSH: 1", "SWAP", "CALL", "PUSH: 4", "SWAP", "PUSH: 5", "SWAP", "PUSH: 2", "SWAP", "CALL", "POP"));
		
		assertParseOutput("f(3, 4);", Arrays.asList("LOAD: f", "PUSH: 3", "SWAP", "PUSH: 4", "SWAP", "PUSH: 2", "SWAP", "CALL", "POP"));
		assertParseOutput("f(3, 4, 5);", Arrays.asList("LOAD: f", "PUSH: 3", "SWAP", "PUSH: 4", "SWAP", "PUSH: 5", "SWAP", "PUSH: 3", "SWAP", "CALL", "POP"));
		
		assertParseOutput("f()()();", Arrays.asList("LOAD: f", "PUSH: 0", "SWAP", "CALL", "PUSH: 0", "SWAP", "CALL", "PUSH: 0", "SWAP", "CALL", "POP"));
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
