package parser;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.parboiled.BaseParser;
import org.parboiled.Parboiled;
import org.parboiled.Rule;
import org.parboiled.parserunners.ReportingParseRunner;
import org.parboiled.support.ParseTreeUtils;
import org.parboiled.support.ParsingResult;

public class ParserTest {
	private Parser parser = Parboiled.createParser(Parser.class);
	
	@Test(timeout=15000)
	public void Literal() {
		assertParse("null", parser.Literal());
		assertParse("true", parser.Literal());
		assertParse("-10.5", parser.Literal());
		assertParse("'a'", parser.Literal());
	}

	@Test(timeout=15000)
	public void NullLiteral() {
		assertParse("null", parser.NullLiteral());
	}

	@Test(timeout=15000)
	public void BooleanLiteral() {
		assertParse("true", parser.BooleanLiteral());
		assertParse("false", parser.BooleanLiteral());
	}

	@Test(timeout=15000)
	public void NumericLiteral() {
		assertParse("10", parser.NumericLiteral());
	}

	@Test(timeout=15000)
	public void StringLiteral() {
		assertParse("'a'", parser.StringLiteral());
		assertParse("\"a\"", parser.StringLiteral());
	}

	@Test(timeout=15000)
	public void Identifier() {
		assertParse("a", parser.Identifier());
	}

	@Test(timeout=15000)
	public void PrimaryExpression() {
		assertParse("this", parser.PrimaryExpression());
		assertParse("a", parser.PrimaryExpression());
		assertParse("1", parser.PrimaryExpression());
		assertParse("[1,2]", parser.PrimaryExpression());
		assertParse("{a: 2}", parser.PrimaryExpression());
		assertParse("(x)", parser.PrimaryExpression());
	}

	@Test(timeout=15000)
	public void ArrayLiteral() {
		assertParse("[]", parser.ArrayLiteral());
		assertParse("[1]", parser.ArrayLiteral());
		assertParse("[1,2]", parser.ArrayLiteral());
		assertParse("[1,2,3]", parser.ArrayLiteral());
	}

	@Test(timeout=15000)
	public void ObjectLiteral() {
		assertParse("{}", parser.ObjectLiteral());
		assertParse("{a: 1, b: 2}", parser.ObjectLiteral());
	}

	@Test(timeout=15000)
	public void PropertyNameAndValueList() {
		assertParse("a: 1", parser.PropertyNameAndValueList());
		assertParse("a: 1, b: 2", parser.PropertyNameAndValueList());
	}

	@Test(timeout=15000)
	public void PropertyName() {
		assertParse("a", parser.PropertyName());
		assertParse("'a'", parser.PropertyName());
		assertParse("1", parser.PropertyName());
	}

	@Test(timeout=15000)
	public void MemberExpression() {
		assertParse("a", parser.MemberExpression());
		assertParse("function() {}", parser.MemberExpression());
		assertParse("a.b", parser.MemberExpression());
		assertParse("a[10]", parser.MemberExpression());
	}

	@Test(timeout=15000)
	public void NewExpression() {
		assertParse("a", parser.NewExpression());
	}

	@Test(timeout=15000)
	public void CallExpression() {
		assertParse("a()", parser.CallExpression());
		assertParse("a()()", parser.CallExpression());
		assertParse("a()[10]", parser.CallExpression());
		assertParse("a().a", parser.CallExpression());
	}

	@Test(timeout=15000)
	public void Arguments() {
		assertParse("()", parser.Arguments());
		assertParse("(1)", parser.Arguments());
	}

	@Test(timeout=15000)
	public void ArgumentList() {
		assertParse("1", parser.ArgumentList());
		assertParse("1, 2", parser.ArgumentList());
		assertParse("1, 2, 3", parser.ArgumentList());
	}

	@Test(timeout=15000)
	public void LeftHandSideExpression() {
		assertParse("a()", parser.LeftHandSideExpression());
	}

	@Test(timeout=15000)
	public void PostfixExpression() {
		assertParse("a++", parser.PostfixExpression());
		assertParse("a--", parser.PostfixExpression());
		assertParse("a", parser.PostfixExpression());
	}

	@Test(timeout=15000)
	public void UnaryExpression() {
		assertParse("a++", parser.UnaryExpression());
		assertParse("delete a", parser.UnaryExpression());
		assertParse("void a", parser.UnaryExpression());
		assertParse("++a", parser.UnaryExpression());
		assertParse("--a", parser.UnaryExpression());
		assertParse("+a", parser.UnaryExpression());
		assertParse("-a", parser.UnaryExpression());
		assertParse("~a", parser.UnaryExpression());
		assertParse("!a", parser.UnaryExpression());
	}

	@Test(timeout=15000)
	public void MultiplicativeExpression() {
		assertParse("a * a", parser.MultiplicativeExpression());
		assertParse("a / a", parser.MultiplicativeExpression());
		assertParse("a % a", parser.MultiplicativeExpression());
		assertParse("a", parser.MultiplicativeExpression());
	}

	@Test(timeout=15000)
	public void AdditiveExpression() {
		assertParse("a + a", parser.AdditiveExpression());
		assertParse("a - a", parser.AdditiveExpression());
		assertParse("a * a", parser.AdditiveExpression());
	}

	@Test(timeout=15000)
	public void ShiftExpression() {
		assertParse("a << a", parser.ShiftExpression());
		assertParse("a >> a", parser.ShiftExpression());
		assertParse("a >>> a", parser.ShiftExpression());
		assertParse("a + a", parser.ShiftExpression());
	}

	@Test(timeout=15000)
	public void RelationalExpression() {
		assertParse("a < a", parser.RelationalExpression());
		assertParse("a > a", parser.RelationalExpression());
		assertParse("a <= a", parser.RelationalExpression());
		assertParse("a >= a", parser.RelationalExpression());
		assertParse("a in a", parser.RelationalExpression());
		assertParse("a << a", parser.RelationalExpression());
	}

	@Test(timeout=15000)
	public void EqualityExpression() {
		assertParse("a == a", parser.EqualityExpression());
		assertParse("a != a", parser.EqualityExpression());
		assertParse("a === a", parser.EqualityExpression());
		assertParse("a !== a", parser.EqualityExpression());
		assertParse("a < a", parser.EqualityExpression());
	}

	@Test(timeout=15000)
	public void BitwiseANDExpression() {
		assertParse("a & a", parser.BitwiseANDExpression());
		assertParse("a == a", parser.BitwiseANDExpression());
	}

	@Test(timeout=15000)
	public void BitwiseXORExpression() {
		assertParse("a ^ a", parser.BitwiseXORExpression());
		assertParse("a & a", parser.BitwiseXORExpression());
		assertParse("a ^ a", parser.BitwiseXORExpression());
	}

	@Test(timeout=15000)
	public void BitwiseORExpression() {
		assertParse("a | a", parser.BitwiseORExpression());
	}

	@Test(timeout=15000)
	public void LogicalANDExpression() {
		assertParse("a && a", parser.LogicalANDExpression());
		assertParse("a | a", parser.LogicalANDExpression());
	}

	@Test(timeout=15000)
	public void LogicalORExpression() {
		assertParse("a || a", parser.LogicalORExpression());
		assertParse("a && a", parser.LogicalORExpression());
	}

	@Test(timeout=15000)
	public void ConditionalExpression() {
		assertParse("a ? 1 : 2", parser.ConditionalExpression());
		assertParse("a || b", parser.ConditionalExpression());
	}

	@Test(timeout=15000)
	public void AssignmentExpression() {
		assertParse("a = 2", parser.AssignmentExpression());
		assertParse("a ? 1 : 2", parser.AssignmentExpression());
	}

	@Test(timeout=15000)
	public void AssignmentOperator() {
		assertParse("=", parser.AssignmentOperator());
		assertParse("*=", parser.AssignmentOperator());
		assertParse("/=", parser.AssignmentOperator());
		assertParse("%=", parser.AssignmentOperator());
		assertParse("+=", parser.AssignmentOperator());
		assertParse("-=", parser.AssignmentOperator());
		assertParse("<<=", parser.AssignmentOperator());
		assertParse(">>=", parser.AssignmentOperator());
		assertParse(">>>=", parser.AssignmentOperator());
		assertParse("&=", parser.AssignmentOperator());
		assertParse("^=", parser.AssignmentOperator());
		assertParse("|=", parser.AssignmentOperator());
	}

	@Test(timeout=15000)
	public void Expression() {
		assertParse("a", parser.Expression());
	}

	@Test(timeout=15000)
	public void Statement() {
		assertParse("{}", parser.Statement());
		assertParse("var a = 2;", parser.Statement());
		assertParse(";", parser.Statement());
		assertParse("1;", parser.Statement());
		assertParse("if(a) a();", parser.Statement());
		assertParse("while(a) { a(); }", parser.Statement());
		assertParse("return a;", parser.Statement());
		assertParse("switch(a) { default: a(); }", parser.Statement());
	}

	@Test(timeout=15000)
	public void Block() {
		assertParse("{}", parser.Block());
	}

	@Test(timeout=15000)
	public void StatementList() {
		assertParse("a(); b();", parser.StatementList());
	}

	@Test(timeout=15000)
	public void VariableStatement() {
		assertParse("var a = 1;", parser.VariableStatement());
	}

	@Test(timeout=15000)
	public void VariableDeclarationList() {
		assertParse("a = 1", parser.VariableDeclarationList());
		assertParse("a = 1, b = 2", parser.VariableDeclarationList());
	}

	@Test(timeout=15000)
	public void VariableDeclaration() {
		assertParse("a = 2", parser.VariableDeclaration());
	}

	@Test(timeout=15000)
	public void Initialiser() {
		assertParse("= 2", parser.Initialiser());
	}

	@Test(timeout=15000)
	public void EmptyStatement() {
		assertParse(";", parser.EmptyStatement());
	}

	@Test(timeout=15000)
	public void ExpressionStatement() {
		assertParse("1;", parser.ExpressionStatement());
	}

	@Test(timeout=15000)
	public void IfStatement() {
		assertParse("if(a) { a(); } else { b(); }", parser.IfStatement());
		assertParse("if(a) { a(); }", parser.IfStatement());
	}

	@Test(timeout=15000)
	public void IterationStatement() {
		assertParse("do { a(); } while(a);", parser.IterationStatement());
		assertParse("while(a) { a(); }", parser.IterationStatement());
		assertParse("for(a = 1; a < 10; a++) a();", parser.IterationStatement());
		assertParse("for(var a = 1; a < 10; a++) a();", parser.IterationStatement());
	}

	@Test(timeout=15000)
	public void ReturnStatement() {
		assertParse("return 1;", parser.ReturnStatement());
	}

	@Test(timeout=15000)
	public void SwitchStatement() {
		assertParse("switch(a) { default: a(); }", parser.SwitchStatement());
	}

	@Test(timeout=15000)
	public void CaseBlock() {
		assertParse("{ default: a(); }", parser.CaseBlock());
	}

	@Test(timeout=15000)
	public void CaseClauses() {
		assertParse("case 1: a(); case 2: b();", parser.CaseClauses());
	}

	@Test(timeout=15000)
	public void CaseClause() {
		assertParse("case 1: a();", parser.CaseClause());
	}

	@Test(timeout=15000)
	public void DefaultClause() {
		assertParse("default: a();", parser.DefaultClause());
	}

	@Test(timeout=15000)
	public void FunctionDeclaration() {
		assertParse("function a() { a(); }", parser.FunctionDeclaration());
	}

	@Test(timeout=15000)
	public void FunctionExpression() {
		assertParse("function a() { a(); }", parser.FunctionExpression());
	}

	@Test(timeout=15000)
	public void FormalParameterList() {
		assertParse("a", parser.FormalParameterList());
		assertParse("a, b", parser.FormalParameterList());
	}

	@Test(timeout=15000)
	public void FunctionBody() {
		assertParse("a();", parser.FunctionBody());
	}

	@Test(timeout=15000)
	public void Program() {
		assertParse("a(); b();", parser.Program());
	}

	@Test(timeout=15000)
	public void SourceElements() {
		assertParse("a(); b();", parser.SourceElements());
	}

	@Test(timeout=15000)
	public void SourceElement() {
		assertParse("a();", parser.SourceElement());
		assertParse("function a() { a(); }", parser.SourceElement());
	}

	private void assertParse(String input, Rule rule) {
		ParsingResult<Object> result = new ReportingParseRunner<>(parser.Sequence(rule, BaseParser.EOI)).run(input);
		//System.out.println(ParseTreeUtils.printNodeTree(result));
		assertTrue(result.matched);
	}
}