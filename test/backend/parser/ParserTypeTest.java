package backend.parser;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.parboiled.BaseParser;
import org.parboiled.Parboiled;
import org.parboiled.Rule;
import org.parboiled.parserunners.ReportingParseRunner;
import org.parboiled.support.ParsingResult;

public class ParserTypeTest {
	private Parser parser = Parboiled.createParser(Parser.class);
	
	@Test
	public void Literal() {
		assertParseType("null", parser.Literal());
		assertParseType("true", parser.Literal());
		assertParseType("-10.5", parser.Literal());
		assertParseType("'a'", parser.Literal());
	}

	@Test
	public void NullLiteral() {
		assertParseType("null", parser.NullLiteral());
	}

	@Test
	public void BooleanLiteral() {
		assertParseType("true", parser.BooleanLiteral());
		assertParseType("false", parser.BooleanLiteral());
	}

	@Test
	public void NumericLiteral() {
		assertParseType("10", parser.NumericLiteral());
	}

	@Test
	public void StringLiteral() {
		assertParseType("'a'", parser.StringLiteral());
		assertParseType("\"a\"", parser.StringLiteral());
	}

	@Test
	public void Identifier() {
		assertParseType("a", parser.Identifier());
	}

	@Test
	public void PrimaryExpression() {
		assertParseType("this", parser.PrimaryExpression());
		assertParseType("a", parser.PrimaryExpression());
		assertParseType("1", parser.PrimaryExpression());
		assertParseType("[1,2]", parser.PrimaryExpression());
		assertParseType("{a: 2}", parser.PrimaryExpression());
		assertParseType("(x)", parser.PrimaryExpression());
	}

	@Test
	public void ArrayLiteral() {
		assertParseType("[]", parser.ArrayLiteral());
		assertParseType("[1]", parser.ArrayLiteral());
		assertParseType("[1,2]", parser.ArrayLiteral());
		assertParseType("[1,2,3]", parser.ArrayLiteral());
	}

	@Test
	public void ObjectLiteral() {
		assertParseType("{}", parser.ObjectLiteral());
		assertParseType("{a: 1, b: 2}", parser.ObjectLiteral());
	}

	@Test
	public void PropertyNameAndValueList() {
		assertParseType("a: 1", parser.PropertyNameAndValueList());
		assertParseType("a: 1, b: 2", parser.PropertyNameAndValueList());
	}

	@Test
	public void MemberExpression() {
		assertParseType("a", parser.MemberExpression());
		assertParseType("function() {}", parser.MemberExpression());
		assertParseType("a.b", parser.MemberExpression());
		assertParseType("a[10]", parser.MemberExpression());
	}

	@Test
	public void CallExpression() {
		assertParseType("a()", parser.CallExpression());
		assertParseType("a()()", parser.CallExpression());
		assertParseType("a()[10]", parser.CallExpression());
		assertParseType("a().a", parser.CallExpression());
	}

	@Test
	public void Arguments() {
		assertParseType("()", parser.Arguments());
		assertParseType("(1)", parser.Arguments());
	}

	@Test
	public void ArgumentList() {
		assertParseType("1", parser.ArgumentList());
		assertParseType("1, 2", parser.ArgumentList());
		assertParseType("1, 2, 3", parser.ArgumentList());
	}

	@Test
	public void LeftHandSideExpression() {
		assertParseType("a()", parser.LeftHandSideExpression());
	}

	@Test
	public void PostfixExpression() {
		assertParseType("a++", parser.PostfixExpression());
		assertParseType("a--", parser.PostfixExpression());
		assertParseType("a", parser.PostfixExpression());
	}

	@Test
	public void UnaryExpression() {
		assertParseType("a++", parser.UnaryExpression());
		assertParseType("void a", parser.UnaryExpression());
		assertParseType("++a", parser.UnaryExpression());
		assertParseType("--a", parser.UnaryExpression());
		assertParseType("+a", parser.UnaryExpression());
		assertParseType("-a", parser.UnaryExpression());
		assertParseType("~a", parser.UnaryExpression());
		assertParseType("!a", parser.UnaryExpression());
	}

	@Test
	public void MultiplicativeExpression() {
		assertParseType("a * a", parser.MultiplicativeExpression());
		assertParseType("a / a", parser.MultiplicativeExpression());
		assertParseType("a % a", parser.MultiplicativeExpression());
		assertParseType("a", parser.MultiplicativeExpression());
	}

	@Test
	public void AdditiveExpression() {
		assertParseType("a + a", parser.AdditiveExpression());
		assertParseType("a - a", parser.AdditiveExpression());
		assertParseType("a * a", parser.AdditiveExpression());
	}

	@Test
	public void ShiftExpression() {
		assertParseType("a << a", parser.ShiftExpression());
		assertParseType("a >> a", parser.ShiftExpression());
		assertParseType("a >>> a", parser.ShiftExpression());
		assertParseType("a + a", parser.ShiftExpression());
	}

	@Test
	public void RelationalExpression() {
		assertParseType("a < a", parser.RelationalExpression());
		assertParseType("a > a", parser.RelationalExpression());
		assertParseType("a <= a", parser.RelationalExpression());
		assertParseType("a >= a", parser.RelationalExpression());
		assertParseType("a << a", parser.RelationalExpression());
	}

	@Test
	public void EqualityExpression() {
		assertParseType("a == a", parser.EqualityExpression());
		assertParseType("a != a", parser.EqualityExpression());
		assertParseType("a < a", parser.EqualityExpression());
	}

	@Test
	public void BitwiseANDExpression() {
		assertParseType("a & a", parser.BitwiseANDExpression());
		assertParseType("a == a", parser.BitwiseANDExpression());
	}

	@Test
	public void BitwiseXORExpression() {
		assertParseType("a ^ a", parser.BitwiseXORExpression());
		assertParseType("a & a", parser.BitwiseXORExpression());
		assertParseType("a ^ a", parser.BitwiseXORExpression());
	}

	@Test
	public void BitwiseORExpression() {
		assertParseType("a | a", parser.BitwiseORExpression());
	}

	@Test
	public void LogicalANDExpression() {
		assertParseType("a && a", parser.LogicalANDExpression());
		assertParseType("a | a", parser.LogicalANDExpression());
	}

	@Test
	public void LogicalORExpression() {
		assertParseType("a || a", parser.LogicalORExpression());
		assertParseType("a && a", parser.LogicalORExpression());
	}

	@Test
	public void ConditionalExpression() {
		assertParseType("a ? 1 : 2", parser.ConditionalExpression());
		assertParseType("a || b", parser.ConditionalExpression());
	}

	@Test
	public void AssignmentExpression() {
		assertParseType("a = 2", parser.AssignmentExpression());
		assertParseType("a += 2", parser.AssignmentExpression());
		assertParseType("a ? 1 : 2", parser.AssignmentExpression());
	}
	
	@Test
	public void ArrowFunction() {
		assertParseType("x => x + 1", parser.ArrowFunction());
		assertParseType("x => { return x + 1; }", parser.ArrowFunction());
		assertParseType("x => { x = x + 1; return x; }", parser.ArrowFunction());
	}

	@Test
	public void CompoundAssignmentOperator() {
		assertParseType("*=", parser.CompoundAssignmentOperator());
		assertParseType("/=", parser.CompoundAssignmentOperator());
		assertParseType("%=", parser.CompoundAssignmentOperator());
		assertParseType("+=", parser.CompoundAssignmentOperator());
		assertParseType("-=", parser.CompoundAssignmentOperator());
		assertParseType("<<=", parser.CompoundAssignmentOperator());
		assertParseType(">>=", parser.CompoundAssignmentOperator());
		assertParseType(">>>=", parser.CompoundAssignmentOperator());
		assertParseType("&=", parser.CompoundAssignmentOperator());
		assertParseType("^=", parser.CompoundAssignmentOperator());
		assertParseType("|=", parser.CompoundAssignmentOperator());
	}

	@Test
	public void Expression() {
		assertParseType("a", parser.Expression());
	}

	@Test
	public void Statement() {
		assertParseType("{}", parser.Statement());
		assertParseType("var a = 2;", parser.Statement());
		assertParseType(";", parser.Statement());
		assertParseType("1;", parser.Statement());
		assertParseType("if(a) a();", parser.Statement());
		assertParseType("while(a) { a(); }", parser.Statement());
		assertParseType("return a;", parser.Statement());
	}

	@Test
	public void Block() {
		assertParseType("{}", parser.Block());
	}

	@Test
	public void StatementList() {
		assertParseType("a(); b();", parser.StatementList());
	}

	@Test
	public void VariableStatement() {
		assertParseType("var a = 1;", parser.VariableStatement());
	}

	@Test
	public void VariableDeclarationList() {
		assertParseType("a = 1", parser.VariableDeclarationList());
		assertParseType("a = 1, b = 2", parser.VariableDeclarationList());
	}

	@Test
	public void VariableDeclaration() {
		assertParseType("a = 2", parser.VariableDeclaration());
	}

	@Test
	public void Initialiser() {
		assertParseType("= 2", parser.Initialiser());
	}

	@Test
	public void EmptyStatement() {
		assertParseType(";", parser.EmptyStatement());
	}

	@Test
	public void ExpressionStatement() {
		assertParseType("1;", parser.ExpressionStatement());
	}

	@Test
	public void IfStatement() {
		assertParseType("if(a) { a(); } else { b(); }", parser.IfStatement());
		assertParseType("if(a) { a(); }", parser.IfStatement());
	}

	@Test
	public void IterationStatement() {
		assertParseType("do { a(); } while(a);", parser.IterationStatement());
		assertParseType("while(a) { a(); }", parser.IterationStatement());
		assertParseType("for(a = 1; a < 10; a++) a();", parser.IterationStatement());
		assertParseType("for(var a = 1; a < 10; a++) a();", parser.IterationStatement());
	}

	@Test
	public void ReturnStatement() {
		assertParseType("return 1;", parser.ReturnStatement());
	}
	
	@Test
	public void VizStatement() {
		assertParseType("@rect();", parser.VizStatement());
		assertParseType("@rect(x: 1);", parser.VizStatement());
		assertParseType("@rect(x: 1, y: 2);", parser.VizStatement());
		assertParseType("@rect(x: 1, y: 2, z: 3);", parser.VizStatement());
		assertParseType("@rect(x: 1 + 2);", parser.VizStatement());
		assertParseType("@for(x <- a) @rect();", parser.VizStatement());
		assertParseType("@for(x <- a, y <- b) @rect();", parser.VizStatement());
		assertParseType("@for(x <- a, x < 2) @rect();", parser.VizStatement());
		assertParseType("@rect[]();", parser.VizStatement());
		assertParseType("@rect[x: 2]();", parser.VizStatement());
		assertParseType("@rect[x: 2, y: 3]();", parser.VizStatement());
		assertParseType("@for([x] <- a) @rect();", parser.VizStatement());
		assertParseType("@for([x, y] <- a) @rect();", parser.VizStatement());
		assertParseType("@for([x, [y, z]] <- a) @rect();", parser.VizStatement());
		assertParseType("@for([x] <- a, [y] <- b) @rect();", parser.VizStatement());
	}

	@Test
	public void FunctionDeclaration() {
		assertParseType("function a() { a(); }", parser.FunctionDeclaration());
	}

	@Test
	public void FunctionExpression() {
		assertParseType("function() { a(); }", parser.FunctionExpression());
	}

	@Test
	public void FormalParameterList() {
		assertParseType("a", parser.FormalParameterList());
		assertParseType("a, b", parser.FormalParameterList());
	}

	@Test
	public void FunctionBody() {
		assertParseType("a();", parser.FunctionBody());
	}

	@Test
	public void Program() {
		assertParseType("a(); b();", parser.Program());
	}

	@Test
	public void SourceElements() {
		assertParseType("a(); b();", parser.SourceElements());
	}

	@Test
	public void SourceElement() {
		assertParseType("a();", parser.SourceElement());
		assertParseType("function a() { a(); }", parser.SourceElement());
	}
	
	@Test
	public void Comment() {
		assertParseType("//123", parser.Comment());
		assertParseType("/*123*/", parser.Comment());
	}
	
	private void assertParseType(String input, Rule rule) {
		ParsingResult<Object> result = new ReportingParseRunner<>(parser.Sequence(rule, BaseParser.EOI)).run(input);
		//System.out.println(ParseTreeUtils.printNodeTree(result));
		assertTrue(result.matched);
		//assertEquals(1, result.valueStack.size());
	}
}