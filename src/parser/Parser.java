package parser;

import static instruction.CallInstruction.Call;
import static instruction.LoadInstruction.Load;
import static instruction.PopInstruction.Pop;
import static instruction.PushInstruction.Push;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static value.DoubleValue.Value;
import static value.StringValue.Value;

import instruction.CallInstruction;
import instruction.Instruction;
import instruction.PopInstruction;
import instruction.PushInstruction;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.parboiled.BaseParser;
import org.parboiled.Rule;
import org.parboiled.annotations.BuildParseTree;

import value.DoubleValue;
import value.StringValue;

@BuildParseTree
public class Parser extends BaseParser<List<Instruction>> {
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
		return Sequence(Terminal(OneOrMore(FirstOf(CharRange('0', '9'), '-', '.'))), push(singletonList(Push(Value(match())))));
	}
	
	public Rule StringLiteral() {
		return FirstOf(
			Terminal(Sequence("'", ZeroOrMore(TestNot("'"), ANY), push(singletonList(Push(Value(match())))), "'")),
			Terminal(Sequence("\"", ZeroOrMore(TestNot("\""), ANY), push(singletonList(Push(Value(match())))), "\""))
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
			Sequence(Identifier(), push(singletonList(Load(Value(match()))))),
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
			FunctionExpression(),
			PrimaryExpression()
		), ZeroOrMore(FirstOf(
			Sequence(Terminal("["), Expression(), Terminal("]")),
			Sequence(Terminal("."), Identifier())
		)));
	}
	
	public Rule NewExpression() {
		return MemberExpression();
	}
	
	public Rule CallExpression() {
		return Sequence(	
			Sequence(MemberExpression(), Arguments(), dup(), push(
				concat(
					pop(),
					singletonList(Push(Value(pop().size()))),
					pop(),
					singletonList(Call())
				)
			)),
			ZeroOrMore(FirstOf(
				Arguments(),
				Sequence(Terminal("["), Expression(), Terminal("]")),
				Sequence(Terminal("."), Identifier())
			))
		);
	}
	
	public Rule Arguments() {
		return FirstOf(	
			Sequence(Terminal("("), Terminal(")"), push(new ArrayList<Instruction>())),
			Sequence(Terminal("("), ArgumentList(), Terminal(")"))
		);
	}
	
	public Rule ArgumentList() {
		return Sequence(AssignmentExpression(), ZeroOrMore(Terminal(","), AssignmentExpression(), push(concat(pop(), pop()))));
	}
	
	public Rule LeftHandSideExpression() {
		return FirstOf(	
			CallExpression(),
			NewExpression()
		);
	}
	
	public Rule PostfixExpression() {
		return Sequence(LeftHandSideExpression(), Optional(FirstOf(Terminal("++"), Terminal("--"))));
	}
	
	public Rule UnaryExpression() {
		return FirstOf(
			Sequence(Terminal("delete"), UnaryExpression()),
			Sequence(Terminal("void"), UnaryExpression()),
			Sequence(Terminal("++"), UnaryExpression()),
			Sequence(Terminal("--"), UnaryExpression()),
			Sequence(Terminal("+"), UnaryExpression()),
			Sequence(Terminal("-"), UnaryExpression()),
			Sequence(Terminal("~"), UnaryExpression()),
			Sequence(Terminal("!"), UnaryExpression()),
			PostfixExpression()
		);
	}
	
	public Rule MultiplicativeExpression() {
		return Sequence(UnaryExpression(), Optional(FirstOf(Terminal("*"), Terminal("/"), Terminal("%")), MultiplicativeExpression()));
	}
	
	public Rule AdditiveExpression() {
		return Sequence(MultiplicativeExpression(), Optional(FirstOf(Terminal("+"), Terminal("-")), AdditiveExpression()));
	}
	
	public Rule ShiftExpression() {
		return Sequence(AdditiveExpression(), Optional(FirstOf(Terminal("<<"), Terminal(">>>"), Terminal(">>")), ShiftExpression()));
	}
	
	public Rule RelationalExpression() {
		return Sequence(ShiftExpression(), Optional(FirstOf(Terminal("<="), Terminal(">="), Terminal("<"), Terminal(">"), Terminal("in")), RelationalExpression()));
	}
	
	public Rule EqualityExpression() {
		return Sequence(RelationalExpression(), Optional(FirstOf(Terminal("==="), Terminal("!=="), Terminal("=="), Terminal("!=")), EqualityExpression()));
	}
	
	public Rule BitwiseANDExpression() {
		return Sequence(EqualityExpression(), Optional(Terminal("&"), BitwiseANDExpression()));
	}
	
	public Rule BitwiseXORExpression() {
		return Sequence(BitwiseANDExpression(), Optional(Terminal("^"), BitwiseXORExpression()));
	}
	
	public Rule BitwiseORExpression() {
		return Sequence(BitwiseXORExpression(), Optional(Terminal("|"), BitwiseORExpression()));
	}
	
	public Rule LogicalANDExpression() {
		return Sequence(BitwiseORExpression(), Optional(Terminal("&&"), LogicalANDExpression()));
	}
	
	public Rule LogicalORExpression() {
		return Sequence(LogicalANDExpression(), Optional(Terminal("||"), LogicalORExpression()));
	}
	
	public Rule ConditionalExpression() {
		return Sequence(LogicalORExpression(), Optional(Terminal("?"), AssignmentExpression(), Terminal(":"), AssignmentExpression()));
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
		return AssignmentExpression();
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
		return Sequence(VariableDeclaration(), ZeroOrMore(Terminal(","), VariableDeclaration()));
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
		return Sequence(TestNot(FirstOf("{", "function")), Expression(), push(concat(pop(), singletonList(PopInstruction.Pop()))), Terminal(";"));
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
		return OneOrMore(CaseClause());
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
	
	@SafeVarargs
	protected static <T> List<T> concat(List<T>... lists) {
		List<T> list = new ArrayList<>();
		for(List<T> a:lists) {
			list.addAll(a);
		}
		return list;
	}
}
