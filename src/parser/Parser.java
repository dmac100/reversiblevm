package parser;

import static org.parboiled.support.ParseTreeUtils.printNodeTree;

import org.parboiled.BaseParser;
import org.parboiled.Parboiled;
import org.parboiled.Rule;
import org.parboiled.annotations.BuildParseTree;
import org.parboiled.parserunners.BasicParseRunner;
import org.parboiled.support.ParsingResult;

@BuildParseTree
public class Parser extends BaseParser<Object> {
	public Rule Literal() {
		return FirstOf(
			NullLiteral(),
			BooleanLiteral(),
			NumericLiteral(),
			StringLiteral()
		);
	}
	
	public Rule NullLiteral() {
		return Terminal("null");
	}
	
	public Rule BooleanLiteral() {
		return FirstOf(
			Terminal("true"),
			Terminal("false")
		);
	}
	
	public Rule NumericLiteral() {
		return Terminal(OneOrMore(FirstOf(CharRange('0', '9'), '-', '.')));
	}
	
	public Rule StringLiteral() {
		return FirstOf(
			Terminal(Sequence("'", ZeroOrMore(TestNot("'"), ANY), "'")),
			Terminal(Sequence("\"", ZeroOrMore(TestNot("\""), ANY), "\""))
		);
	}
	
	public Rule Identifier() {
		return Terminal(Sequence(
			FirstOf(CharRange('a', 'z'), CharRange('A', 'Z'), "$", "_"),
			ZeroOrMore(FirstOf(CharRange('a', 'z'), CharRange('A', 'Z'), CharRange('0', '9'), "$", "_"))
		));
	}

	public Rule PrimaryExpression() {
		return FirstOf(
			Terminal("this"),
			Identifier(),
			Literal(),
			ArrayLiteral(),
			ObjectLiteral(),
			Sequence(Terminal("("), Expression(), Terminal(")"))
		);
	}
	
	public Rule ArrayLiteral() {
		return Sequence(Terminal("["), Optional(ZeroOrMore(AssignmentExpression(), Terminal(",")), AssignmentExpression()), Terminal("]"));
	}
	
	public Rule Elision() {
		return OneOrMore(Terminal(","));
	}
	
	public Rule ObjectLiteral() {
		return Sequence(Terminal("{"), Optional(PropertyNameAndValueList()), Terminal("}"));
	}
	
	public Rule PropertyNameAndValueList() {
		return Sequence(PropertyName(), Terminal(":"), AssignmentExpression(), Optional(Terminal(","), PropertyNameAndValueList()));
	}
	
	public Rule PropertyName() {
		return FirstOf(
			Identifier(),
			StringLiteral(),
			NumericLiteral()
		);
	}
	
	public Rule MemberExpression() {
		return Sequence(FirstOf(	
			PrimaryExpression(),
			FunctionExpression(),
			Sequence(Terminal("new"), MemberExpression(), Arguments())
		), ZeroOrMore(FirstOf(
			Sequence(Terminal("["), Expression(), Terminal("]")),
			Sequence(Terminal("."), Identifier())
		)));
	}
	
	public Rule NewExpression() {
		return Sequence(Optional(Terminal("new")), OneOrMore(MemberExpression()));
	}
	
	public Rule CallExpression() {
		return Sequence(	
			Sequence(MemberExpression(), Arguments()),
			ZeroOrMore(FirstOf(
				Arguments(),
				Sequence(Terminal("["), Expression(), Terminal("]")),
				Sequence(Terminal("."), Identifier())
			))
		);
	}
	
	public Rule Arguments() {
		return FirstOf(	
			Sequence(Terminal("("), Terminal(")")),
			Sequence(Terminal("("), ArgumentList(), Terminal(")"))
		);
	}
	
	public Rule ArgumentList() {
		return FirstOf(	
			AssignmentExpression(),
			Sequence(ArgumentList(), Terminal(","), AssignmentExpression())
		);
	}
	
	public Rule LeftHandSideExpression() {
		return FirstOf(	
			CallExpression(),
			NewExpression()
		);
	}
	
	public Rule PostfixExpression() {
		return FirstOf(	
			Sequence(LeftHandSideExpression(), Terminal("++")),
			Sequence(LeftHandSideExpression(), Terminal("--")),
			LeftHandSideExpression()
		);
	}
	
	public Rule UnaryExpression() {
		return FirstOf(
			PostfixExpression(),
			Sequence(Terminal("delete"), UnaryExpression()),
			Sequence(Terminal("void"), UnaryExpression()),
			Sequence(Terminal("typeof"), UnaryExpression()),
			Sequence(Terminal("++"), UnaryExpression()),
			Sequence(Terminal("--"), UnaryExpression()),
			Sequence(Terminal("+"), UnaryExpression()),
			Sequence(Terminal("-"), UnaryExpression()),
			Sequence(Terminal("~"), UnaryExpression()),
			Sequence(Terminal("!"), UnaryExpression())
		);
	}
	
	public Rule MultiplicativeExpression() {
		return FirstOf(	
			Sequence(UnaryExpression(), Terminal("*"), MultiplicativeExpression()),
			Sequence(UnaryExpression(), Terminal("/"), MultiplicativeExpression()),
			Sequence(UnaryExpression(), Terminal("%"), MultiplicativeExpression()),
			UnaryExpression()
		);
	}
	
	public Rule AdditiveExpression() {
		return FirstOf(	
			Sequence(MultiplicativeExpression(), Terminal("+"), AdditiveExpression()),
			Sequence(MultiplicativeExpression(), Terminal("-"), AdditiveExpression()),
			MultiplicativeExpression()
		);
	}
	
	public Rule ShiftExpression() {
		return FirstOf(	
			Sequence(AdditiveExpression(), Terminal("<<"), ShiftExpression()),
			Sequence(AdditiveExpression(), Terminal(">>"), ShiftExpression()),
			Sequence(AdditiveExpression(), Terminal(">>>"), ShiftExpression()),
			AdditiveExpression()
		);
	}
	
	public Rule RelationalExpression() {
		return FirstOf(	
			Sequence(ShiftExpression(), Terminal("<"), RelationalExpression()),
			Sequence(ShiftExpression(), Terminal(">"), RelationalExpression()),
			Sequence(ShiftExpression(), Terminal("<="), RelationalExpression()),
			Sequence(ShiftExpression(), Terminal(">="), RelationalExpression()),
			Sequence(ShiftExpression(), Terminal("instanceof"), RelationalExpression()),
			Sequence(ShiftExpression(), Terminal("in"), RelationalExpression()),
			ShiftExpression()
		);
	}
	
	public Rule EqualityExpression() {
		return FirstOf(
			Sequence(RelationalExpression(), Terminal("=="), EqualityExpression()),
			Sequence(RelationalExpression(), Terminal("!="), EqualityExpression()),
			Sequence(RelationalExpression(), Terminal("==="), EqualityExpression()),
			Sequence(RelationalExpression(), Terminal("!=="), EqualityExpression()),
			RelationalExpression()
		);
	}
	
	public Rule BitwiseANDExpression() {
		return FirstOf(	
			Sequence(EqualityExpression(), Terminal("&"), BitwiseANDExpression()),
			EqualityExpression()
		);
	}
	
	public Rule BitwiseXORExpression() {
		return FirstOf(	
			Sequence(BitwiseANDExpression(), Terminal("^"), BitwiseXORExpression()),
			BitwiseANDExpression()
		);
	}
	
	public Rule BitwiseORExpression() {
		return FirstOf(	
			Sequence(BitwiseXORExpression(), Terminal("|"), BitwiseORExpression()),
			BitwiseXORExpression()
		);
	}
	
	public Rule LogicalANDExpression() {
		return FirstOf(	
			Sequence(BitwiseORExpression(), Terminal("&&"), LogicalANDExpression()),
			BitwiseORExpression()
		);
	}
	
	public Rule LogicalORExpression() {
		return FirstOf(
			Sequence(LogicalANDExpression(), Terminal("||"), LogicalORExpression()),
			LogicalANDExpression()
		);
	}
	
	public Rule ConditionalExpression() {
		return FirstOf(	
			Sequence(LogicalORExpression(), Terminal("?"), AssignmentExpression(), Terminal(":"), AssignmentExpression()),
			LogicalORExpression()
		);
	}
	
	public Rule AssignmentExpression() {
		return FirstOf(
			Sequence(LeftHandSideExpression(), AssignmentOperator(), AssignmentExpression()),
			ConditionalExpression()
		);
	}
	
	public Rule AssignmentOperator() {
		return FirstOf(	
			Terminal("="),
			Terminal("*="),
			Terminal("/="),
			Terminal("%="),
			Terminal("+="),
			Terminal("-="),
			Terminal("<<="),
			Terminal(">>="),
			Terminal(">>>="),
			Terminal("&="),
			Terminal("^="),
			Terminal("|=")
		);
	}
	
	public Rule Expression() {
		return Sequence(AssignmentExpression(), ZeroOrMore(Terminal(","), Expression()));
	}
	
	public Rule Statement() {
		return FirstOf(
			Block(),
			VariableStatement(),
			EmptyStatement(),
			ExpressionStatement(),
			IfStatement(),
			IterationStatement(),
			ReturnStatement(),
			SwitchStatement()
		);
	}
	
	public Rule Block() {
		return Sequence(Terminal("{"), Optional(StatementList()), Terminal("}"));
	}
	
	public Rule StatementList() {
		return OneOrMore(Statement());
	}
	
	public Rule VariableStatement() {
		return Sequence(Terminal("var"), VariableDeclarationList(), Terminal(";"));
	}
	
	public Rule VariableDeclarationList() {
		return FirstOf(
			VariableDeclaration(),
			VariableDeclarationList(), VariableDeclaration()
		);
	}
	
	public Rule VariableDeclaration() {
		return Sequence(Identifier(), Initialiser());
	}
	
	public Rule Initialiser() {
		return Sequence(Terminal("="), AssignmentExpression());
	}
	
	public Rule EmptyStatement() {
		return Terminal(";");
	}
	
	public Rule ExpressionStatement() {
		return Sequence(TestNot(FirstOf("{", "function")), Expression(), Terminal(";"));
	}
	
	public Rule IfStatement() {
		return FirstOf(
			Sequence(Terminal("if"), Terminal("("), Expression(), Terminal(")"), Statement(), Terminal("else"), Statement()),
			Sequence(Terminal("if"), Terminal("("), Expression(), Terminal(")"), Statement())
		);
	}
	
	public Rule IterationStatement() {
		return FirstOf(
			Sequence(Terminal("do"), Statement(), Terminal("while"), Terminal("("), Expression(), Terminal(")"), Terminal(";")),
			Sequence(Terminal("while"), Terminal("("), Expression(), Terminal(")"), Statement()),
			Sequence(Terminal("for"), Terminal("("), Optional(Expression()), Terminal(";"), Optional(Expression()), Terminal(";"), Optional(Expression()), Terminal(")"), Statement()),
			Sequence(Terminal("for"), Terminal("("), Terminal("var"), VariableDeclarationList(), Terminal(";"), Optional(Expression()), Terminal(";"), Optional(Expression()), Terminal(")"), Statement())
		);
	}
	
	public Rule ReturnStatement() {
		return Sequence(Terminal("return"), Optional(Expression()), Terminal(";"));
	}
	
	public Rule SwitchStatement() {
		return Sequence(Terminal("switch"), Terminal("("), Expression(), Terminal(")"), CaseBlock());
	}
	
	public Rule CaseBlock() {
		return FirstOf(
			Sequence(Terminal("{"), Optional(CaseClauses()), Terminal("}")),
			Sequence(Terminal("{"), Optional(CaseClauses()), DefaultClause(), Optional(CaseClauses()), Terminal("}"))
		);
	}
	
	public Rule CaseClauses() {
		return FirstOf(
			CaseClause(),
			Sequence(CaseClauses(), CaseClause())
		);
	}
	
	public Rule CaseClause() {
		return Sequence(Terminal("case"), Expression(), Terminal(":"), Optional(StatementList()));
	}
	
	public Rule DefaultClause() {
		return Sequence(Terminal("default"), Terminal(":"), Optional(StatementList()));
	}
	
	public Rule FunctionDeclaration() {
		return Sequence(Terminal("function"), Identifier(), Terminal("("), Optional(FormalParameterList()), Terminal(")"), Terminal("{"), FunctionBody(), Terminal("}"));
	}
	
	public Rule FunctionExpression() {
		return Sequence(Terminal("function"), Optional(Identifier()), Terminal("("), Optional(FormalParameterList()), Terminal(")"), Terminal("{"), FunctionBody(), Terminal("}"));
	}
	
	public Rule FormalParameterList() {
		return Sequence(Identifier(), Optional(Terminal(","), FormalParameterList()));
	}
	
	public Rule FunctionBody() {
		return Optional(SourceElements());
	}
	
	public Rule Program() {
		return Sequence(SourceElements(), EOI);
	}
	
	public Rule SourceElements() {
		return OneOrMore(SourceElement());
	}

	public Rule SourceElement() {
		return FirstOf(
			Statement(),
			FunctionDeclaration()
		);
	}
	
	public Rule Terminal(Object value) {
		return Sequence(value, Optional(OneOrMore(FirstOf(" ", "\r", "\n", "\t"))));
	}
	
	public static void main(String[] args) {
    	String input = "a + b";
    	
        Parser parser = Parboiled.createParser(Parser.class);
        ParsingResult<?> result = new BasicParseRunner<>(parser.Expression()).run(input);
        
        System.out.println(printNodeTree(result));
        
        System.out.println(result.parseTreeRoot.getEndIndex());
    }
}
