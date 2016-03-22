package backend.parser;

import static org.junit.Assert.assertEquals;

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
		assertParseOutput("true && false;", Arrays.asList("PUSH: true", "DUP", "JUMPIFFALSE: 3", "PUSH: false", "AND", "POP"));
	}
	
	@Test
	public void LogicalORExpression() {
		assertParseOutput("true || false;", Arrays.asList("PUSH: true", "DUP", "JUMPIFTRUE: 3", "PUSH: false", "OR", "POP"));
	}
	
	@Test
	public void ConditionalExpression() {
		assertParseOutput("x ? y : z;", Arrays.asList("LOAD: x", "JUMPIFFALSE: 3", "LOAD: y", "JUMP: 2", "LOAD: z", "POP"));
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
	public void ArrowFunction() {
		assertParseOutput("x => x + 1;", Arrays.asList("STARTFUNCTION: 2", "POP", "LOCAL: x", "STORE: x", "POP", "LOAD: x" , "PUSH: 1", "ADD", "ENDFUNCTION", "POP"));
		assertParseOutput("(x, y) => x + y;", Arrays.asList("STARTFUNCTION: 3", "POP", "LOCAL: y", "STORE: y", "LOCAL: x", "STORE: x", "POP", "LOAD: x" , "LOAD: y", "ADD", "ENDFUNCTION", "POP"));
		assertParseOutput("x => { return x + 1; };", Arrays.asList("STARTFUNCTION: 2", "POP", "LOCAL: x", "STORE: x", "POP", "PUSH: null", "POP", "LOAD: x", "PUSH: 1", "ADD", "RETURN", "ENDFUNCTION", "POP"));
		assertParseOutput("x => { x = x + 1; return x; };", Arrays.asList("STARTFUNCTION: 2", "POP", "LOCAL: x", "STORE: x", "POP", "PUSH: null", "LOAD: x", "PUSH: 1", "ADD", "STORE: x", "LOAD: x", "POP", "POP", "LOAD: x", "RETURN", "ENDFUNCTION", "POP"));
	}
	
	@Test
	public void CompoundAssignmentOperator() {
	}
	
	@Test
	public void Expression() {
		assertParseOutput("a;", Arrays.asList("LOAD: a", "POP"));
		assertParseOutput("a, b;", Arrays.asList("LOAD: a", "POP", "LOAD: b", "POP"));
		assertParseOutput("a, b, c;", Arrays.asList("LOAD: a", "POP", "LOAD: b", "POP", "LOAD: c", "POP"));
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
		assertParseOutput("if(x) { a; }", Arrays.asList("LOAD: x", "JUMPIFFALSE: 3", "LOAD: a", "POP"));
		assertParseOutput("if(x) { a; } else { b; }", Arrays.asList("LOAD: x", "JUMPIFFALSE: 4", "LOAD: a", "POP", "JUMP: 3", "LOAD: b", "POP"));
		assertParseOutput("if(x) { a; } else if(y) { b; } else { c; }", Arrays.asList("LOAD: x", "JUMPIFFALSE: 4", "LOAD: a", "POP", "JUMP: 8", "LOAD: y", "JUMPIFFALSE: 4", "LOAD: b", "POP", "JUMP: 3", "LOAD: c", "POP"));
	}
	
	@Test
	public void IterationStatement() {
		assertParseOutput("do { a; } while(x);", Arrays.asList("LOAD: a", "POP", "LOAD: x", "JUMPIFTRUE: -3"));
		assertParseOutput("while(x) { b; }", Arrays.asList("LOAD: x", "JUMPIFFALSE: 4", "LOAD: b", "POP", "JUMP: -4"));
		assertParseOutput("for(x; y; z) { b; }", Arrays.asList("LOAD: x", "POP", "LOAD: y", "JUMPIFFALSE: 6", "LOAD: b", "POP", "LOAD: z", "POP", "JUMP: -6"));
		assertParseOutput("for(var x; y; z) { b; }", Arrays.asList("LOCAL: x", "LOAD: y", "JUMPIFFALSE: 6", "LOAD: b", "POP", "LOAD: z", "POP", "JUMP: -6"));
	}
	
	@Test
	public void ReturnStatement() {
		assertParseOutput("function f() { return; }", Arrays.asList("STARTFUNCTION: 1", "POP", "LOCAL: this", "STORE: this", "PUSH: null", "RETURN", "ENDFUNCTION", "LOCAL: f", "STORE: f"));
		assertParseOutput("function f() { return 1; }", Arrays.asList("STARTFUNCTION: 1", "POP", "LOCAL: this", "STORE: this", "PUSH: null", "POP", "PUSH: 1", "RETURN", "ENDFUNCTION", "LOCAL: f", "STORE: f"));
	}
	
	@Test
	public void VizStatement() {
		assertParseOutput("@rect();", Arrays.asList("STARTVIZ", "NEWVIZOBJECT: rect", "ENDVIZ"));
		assertParseOutput("@rect(x: 1);", Arrays.asList("STARTVIZ", "NEWVIZOBJECT: rect", "PUSH: 1", "SETVIZPROPERTY: x", "ENDVIZ"));
		assertParseOutput("@rect(x: 1, y: 2);", Arrays.asList("STARTVIZ", "NEWVIZOBJECT: rect", "PUSH: 1", "SETVIZPROPERTY: x", "PUSH: 2", "SETVIZPROPERTY: y", "ENDVIZ"));
		assertParseOutput("@rect(x: 1, y: 2, z: 3);", Arrays.asList("STARTVIZ", "NEWVIZOBJECT: rect", "PUSH: 1", "SETVIZPROPERTY: x", "PUSH: 2", "SETVIZPROPERTY: y", "PUSH: 3", "SETVIZPROPERTY: z", "ENDVIZ"));
		assertParseOutput("@rect(x: 1 + 2);", Arrays.asList("STARTVIZ", "NEWVIZOBJECT: rect", "PUSH: 1", "PUSH: 2", "ADD", "SETVIZPROPERTY: x", "ENDVIZ"));
		
		assertParseOutput("@for(x <- a) @rect();", Arrays.asList("STARTVIZ", "LOAD: a", "VIZITERATE: x", "NEWVIZOBJECT: rect", "ENDVIZ"));
		assertParseOutput("@for(x <- a) @rect(x: 1);", Arrays.asList("STARTVIZ", "LOAD: a", "VIZITERATE: x", "NEWVIZOBJECT: rect", "PUSH: 1", "SETVIZPROPERTY: x", "ENDVIZ"));
		assertParseOutput("@for(x <- a, y <- b) @rect();", Arrays.asList("STARTVIZ", "LOAD: a", "VIZITERATE: x", "LOAD: b", "VIZITERATE: y", "NEWVIZOBJECT: rect", "ENDVIZ"));
		assertParseOutput("@for(x <- a, x < 2) @rect();", Arrays.asList("STARTVIZ", "LOAD: a", "VIZITERATE: x", "LOAD: x", "PUSH: 2", "LESSTHAN", "VIZFILTER", "NEWVIZOBJECT: rect", "ENDVIZ"));
		assertParseOutput("@for(var x <- a) @rect();", Arrays.asList("STARTVIZ", "LOAD: a", "VIZITERATE: x", "NEWVIZOBJECT: rect", "ENDVIZ"));
		assertParseOutput("@for(var x <- a, y <- b) @rect();", Arrays.asList("STARTVIZ", "LOAD: a", "VIZITERATE: x", "LOAD: b", "VIZITERATE: y", "NEWVIZOBJECT: rect", "ENDVIZ"));
		
		assertParseOutput("@rect[]();", Arrays.asList("STARTVIZ", "NEWVIZOBJECT: rect", "ENABLEVIZFILTER", "ENDVIZ"));
		assertParseOutput("@rect[x: 2]();", Arrays.asList("STARTVIZ", "NEWVIZOBJECT: rect", "ENABLEVIZFILTER", "PUSH: 2", "SETVIZFILTERPROPERTY: x", "ENDVIZ"));
		assertParseOutput("@rect[x: 2, y: 3]();", Arrays.asList("STARTVIZ", "NEWVIZOBJECT: rect", "ENABLEVIZFILTER", "PUSH: 2", "SETVIZFILTERPROPERTY: x", "PUSH: 3", "SETVIZFILTERPROPERTY: y", "ENDVIZ"));
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
		assertParseOutput("function f() { }", Arrays.asList("STARTFUNCTION: 1", "POP", "LOCAL: this", "STORE: this", "PUSH: null", "ENDFUNCTION", "LOCAL: f", "STORE: f"));
		assertParseOutput("function f(x) { }", Arrays.asList("STARTFUNCTION: 2", "POP", "LOCAL: x", "STORE: x", "LOCAL: this", "STORE: this", "PUSH: null", "ENDFUNCTION", "LOCAL: f", "STORE: f"));
		assertParseOutput("function f(x, y) { }", Arrays.asList("STARTFUNCTION: 3", "POP", "LOCAL: y", "STORE: y", "LOCAL: x", "STORE: x", "LOCAL: this", "STORE: this", "PUSH: null", "ENDFUNCTION", "LOCAL: f", "STORE: f"));
		assertParseOutput("function f(x, y, z) { }", Arrays.asList("STARTFUNCTION: 4", "POP", "LOCAL: z", "STORE: z", "LOCAL: y", "STORE: y", "LOCAL: x", "STORE: x", "LOCAL: this", "STORE: this", "PUSH: null", "ENDFUNCTION", "LOCAL: f", "STORE: f"));
		
		assertParseOutput("function f() { print(); }", Arrays.asList("STARTFUNCTION: 1", "POP", "LOCAL: this", "STORE: this", "PUSH: null", "PUSH: null", "LOAD: print", "PUSH: 1", "SWAP", "CALL", "POP", "ENDFUNCTION", "LOCAL: f", "STORE: f"));
		assertParseOutput("function f(x) { print(x); }", Arrays.asList("STARTFUNCTION: 2", "POP", "LOCAL: x", "STORE: x", "LOCAL: this", "STORE: this", "PUSH: null", "PUSH: null", "LOAD: print", "LOAD: x", "SWAP", "PUSH: 2", "SWAP", "CALL", "POP", "ENDFUNCTION", "LOCAL: f", "STORE: f"));
	}
	
	@Test
	public void FunctionExpression() {
		assertParseOutput("var f = function() { };", Arrays.asList("STARTFUNCTION: 1", "POP", "LOCAL: this", "STORE: this", "PUSH: null", "ENDFUNCTION", "LOCAL: f", "STORE: f"));
		assertParseOutput("var f = function(x) { print(x); };", Arrays.asList("STARTFUNCTION: 2", "POP", "LOCAL: x", "STORE: x", "LOCAL: this", "STORE: this", "PUSH: null", "PUSH: null", "LOAD: print", "LOAD: x", "SWAP", "PUSH: 2", "SWAP", "CALL", "POP", "ENDFUNCTION", "LOCAL: f", "STORE: f"));
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
		assertParseOutput("f();", Arrays.asList("PUSH: null", "LOAD: f", "PUSH: 1", "SWAP", "CALL", "POP"));
		assertParseOutput("f(3);", Arrays.asList("PUSH: null", "LOAD: f", "PUSH: 3", "SWAP", "PUSH: 2", "SWAP", "CALL", "POP"));
		assertParseOutput("f(3)(4);", Arrays.asList("PUSH: null", "PUSH: null", "LOAD: f", "PUSH: 3", "SWAP", "PUSH: 2", "SWAP", "CALL", "PUSH: 4", "SWAP", "PUSH: 2", "SWAP", "CALL", "POP"));
		assertParseOutput("f(3)(4, 5);", Arrays.asList("PUSH: null", "PUSH: null", "LOAD: f", "PUSH: 3", "SWAP", "PUSH: 2", "SWAP", "CALL", "PUSH: 4", "SWAP", "PUSH: 5", "SWAP", "PUSH: 3", "SWAP", "CALL", "POP"));
		
		assertParseOutput("f(3, 4);", Arrays.asList("PUSH: null", "LOAD: f", "PUSH: 3", "SWAP", "PUSH: 4", "SWAP", "PUSH: 3", "SWAP", "CALL", "POP"));
		assertParseOutput("f(3, 4, 5);", Arrays.asList("PUSH: null", "LOAD: f", "PUSH: 3", "SWAP", "PUSH: 4", "SWAP", "PUSH: 5", "SWAP", "PUSH: 4", "SWAP", "CALL", "POP"));
		
		assertParseOutput("f()()();", Arrays.asList("PUSH: null", "PUSH: null", "PUSH: null", "LOAD: f", "PUSH: 1", "SWAP", "CALL", "PUSH: 1", "SWAP", "CALL", "PUSH: 1", "SWAP", "CALL", "POP"));
		
		assertParseOutput("a.f();", Arrays.asList("LOAD: a", "DUP", "GETPROPERTY: f", "PUSH: 1", "SWAP", "CALL", "POP"));
		assertParseOutput("a.f()();", Arrays.asList("PUSH: null", "LOAD: a", "DUP", "GETPROPERTY: f", "PUSH: 1", "SWAP", "CALL", "PUSH: 1", "SWAP", "CALL", "POP"));
		assertParseOutput("a.b.f();", Arrays.asList("LOAD: a", "GETPROPERTY: b", "DUP", "GETPROPERTY: f", "PUSH: 1", "SWAP", "CALL", "POP"));
		assertParseOutput("a.b[0].f();", Arrays.asList("LOAD: a", "GETPROPERTY: b", "PUSH: 0", "GETELEMENT", "DUP", "GETPROPERTY: f", "PUSH: 1", "SWAP", "CALL", "POP"));
		assertParseOutput("a.b[0]();", Arrays.asList("LOAD: a", "GETPROPERTY: b", "PUSH: 0", "DUP2", "POP", "SWAP2", "GETELEMENT", "PUSH: 1", "SWAP", "CALL", "POP"));
		assertParseOutput("a[0]();", Arrays.asList("LOAD: a", "PUSH: 0", "DUP2", "POP", "SWAP2", "GETELEMENT", "PUSH: 1", "SWAP", "CALL", "POP"));
		assertParseOutput("a[0][0]();", Arrays.asList("LOAD: a", "PUSH: 0", "GETELEMENT", "PUSH: 0", "DUP2", "POP", "SWAP2", "GETELEMENT", "PUSH: 1", "SWAP", "CALL", "POP"));
		assertParseOutput("a.b[0]()();", Arrays.asList("PUSH: null", "LOAD: a", "GETPROPERTY: b", "PUSH: 0", "DUP2", "POP", "SWAP2", "GETELEMENT", "PUSH: 1", "SWAP", "CALL", "PUSH: 1", "SWAP", "CALL", "POP"));
		
		assertParseOutput("f()[0];", Arrays.asList("PUSH: null", "LOAD: f", "PUSH: 1", "SWAP", "CALL", "PUSH: 0", "GETELEMENT", "POP"));
		assertParseOutput("f().a;", Arrays.asList("PUSH: null", "LOAD: f", "PUSH: 1", "SWAP", "CALL", "GETPROPERTY: a", "POP"));
		assertParseOutput("f()[0].a;", Arrays.asList("PUSH: null", "LOAD: f", "PUSH: 1", "SWAP", "CALL", "PUSH: 0", "GETELEMENT", "GETPROPERTY: a", "POP"));
		assertParseOutput("f().a[0];", Arrays.asList("PUSH: null", "LOAD: f", "PUSH: 1", "SWAP", "CALL", "GETPROPERTY: a", "PUSH: 0", "GETELEMENT", "POP"));
		assertParseOutput("f().a[0]();", Arrays.asList("PUSH: null", "LOAD: f", "PUSH: 1", "SWAP", "CALL", "GETPROPERTY: a", "PUSH: 0", "DUP2", "POP", "SWAP2", "GETELEMENT", "PUSH: 1", "SWAP", "CALL", "POP"));
		
		assertParseOutput("a.b.c();", Arrays.asList("LOAD: a", "GETPROPERTY: b", "DUP", "GETPROPERTY: c", "PUSH: 1", "SWAP", "CALL", "POP"));
		
		assertParseOutput("a.b[c.d]();", Arrays.asList("LOAD: a", "GETPROPERTY: b", "LOAD: c", "GETPROPERTY: d", "DUP2", "POP", "SWAP2", "GETELEMENT", "PUSH: 1", "SWAP", "CALL", "POP"));
		assertParseOutput("a.b[1 + 1]();", Arrays.asList("LOAD: a", "GETPROPERTY: b", "PUSH: 1", "PUSH: 1", "ADD", "DUP2", "POP", "SWAP2", "GETELEMENT", "PUSH: 1", "SWAP", "CALL", "POP"));
	}
	
	private void assertParseOutput(String input, List<String> instructions) {
		ParsingResult<Instructions> result = new ReportingParseRunner<Instructions>(parser.Sequence(parser.Program(), BaseParser.EOI)).run(input);
		System.out.println(ParseTreeUtils.printNodeTree(result));
		
		Instructions actualInstructions = result.valueStack.pop();
		
		String expected = instructions.toString();
		String actual = actualInstructions.toString();
		
		System.out.println("EXPECTED: " + expected);
		System.out.println("  ACTUAL: " + actual);
		
		for(Object value:result.valueStack) {
			System.out.println("VALUE STACK ITEM: " + value);
		}
		
		assertEquals(expected, actual);
	}
}
