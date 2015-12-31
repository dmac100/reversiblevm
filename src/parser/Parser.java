package parser;

import static instruction.AddInstruction.Add;
import static instruction.AndInstruction.And;
import static instruction.BitwiseAndInstruction.BitwiseAnd;
import static instruction.BitwiseNotInstruction.BitwiseNot;
import static instruction.BitwiseOrInstruction.BitwiseOr;
import static instruction.BitwiseXorInstruction.BitwiseXor;
import static instruction.CallInstruction.Call;
import static instruction.DivideInstruction.Divide;
import static instruction.DupInstruction.Dup;
import static instruction.EqualInstruction.Equal;
import static instruction.GreaterThanEqualInstruction.GreaterThanEqual;
import static instruction.GreaterThanInstruction.GreaterThan;
import static instruction.JumpIfFalseInstruction.JumpIfFalse;
import static instruction.JumpIfTrueInstruction.JumpIfTrue;
import static instruction.JumpInstruction.Jump;
import static instruction.LessThanEqualInstruction.LessThanEqual;
import static instruction.LessThanInstruction.LessThan;
import static instruction.LoadInstruction.Load;
import static instruction.LocalInstruction.Local;
import static instruction.MinusInstruction.Minus;
import static instruction.ModuloInstruction.Modulo;
import static instruction.MultiplyInstruction.Multiply;
import static instruction.NopInstruction.Nop;
import static instruction.NotInstruction.Not;
import static instruction.OrInstruction.Or;
import static instruction.PopInstruction.Pop;
import static instruction.PushInstruction.Push;
import static instruction.ShiftLeftInstruction.ShiftLeft;
import static instruction.ShiftRightInstruction.ShiftRight;
import static instruction.StoreInstruction.Store;
import static instruction.UnaryMinusInstruction.UnaryMinus;
import static instruction.UnaryPlusInstruction.UnaryPlus;
import static instruction.UnsignedShiftRightInstruction.UnsignedShiftRight;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static value.BooleanValue.Value;
import static value.DoubleValue.Value;
import static value.NullValue.NullValue;
import static value.StringValue.Value;
import instruction.Instruction;
import instruction.JumpIfFalseInstruction;
import instruction.JumpInstruction;
import instruction.LoadInstruction;
import instruction.LocalInstruction;
import instruction.NopInstruction;
import instruction.PopInstruction;
import instruction.StoreInstruction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.parboiled.BaseParser;
import org.parboiled.Rule;
import org.parboiled.annotations.BuildParseTree;
import org.parboiled.annotations.SuppressSubnodes;
import org.parboiled.support.Var;

import value.DoubleValue;
import value.NullValue;

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
		return Sequence(Terminal("null"), push(List(Push(NullValue()))));
	}
	
	public Rule BooleanLiteral() {
		return FirstOf(
			Sequence(Terminal("true"), push(List(Push(Value(true))))),
			Sequence(Terminal("false"), push(List(Push(Value(false)))))
		);
	}
	
	@SuppressSubnodes
	public Rule NumericLiteral() {
		return Terminal(Sequence(
			Sequence(Optional('-'), CharRange('0', '9'), ZeroOrMore(FirstOf(CharRange('0', '9'), '.'))),
			push(List(Push(Value(Double.parseDouble(match())))))
		));
	}
	
	@SuppressSubnodes
	public Rule StringLiteral() {
		return FirstOf(
			Terminal(Sequence("'", ZeroOrMore(TestNot("'"), ANY), push(List(Push(Value(match())))), "'")),
			Terminal(Sequence("\"", ZeroOrMore(TestNot("\""), ANY), push(List(Push(Value(match())))), "\""))
		);
	}
	
	@SuppressSubnodes
	public Rule Identifier() {
		return Terminal(Sequence(
			FirstOf(CharRange('a', 'z'), CharRange('A', 'Z'), "$", "_"),
			ZeroOrMore(FirstOf(CharRange('a', 'z'), CharRange('A', 'Z'), CharRange('0', '9'), "$", "_"))
		));
	}

	public Rule PrimaryExpression() {
		return FirstOf(
			Terminal("this"),
			Literal(),
			Sequence(Identifier(), push(List(Load(Value(match().trim()))))),
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
			Sequence(MemberExpression(), Arguments(), push(
				concat(
					pop(),
					pop(),
					pop(),
					List(Call())
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
			Sequence(Terminal("("), Terminal(")"), push(List(Push(Value(0)))), push(new ArrayList<Instruction>())),
			Sequence(Terminal("("), ArgumentList(), Terminal(")"))
		);
	}
	
	public Rule ArgumentList() {
		Var<Integer> argCount = new Var<Integer>();
		return Sequence(
			argCount.set(1),
			AssignmentExpression(),
			ZeroOrMore(
				Terminal(","),
				AssignmentExpression(),
				argCount.set(argCount.get() + 1),
				push(concat(pop(), pop()))
			),
			push(List(Push(new DoubleValue(argCount.get())))),
			swap()
		);
	}
	
	public Rule LeftHandSideExpression() {
		return FirstOf(	
			CallExpression(),
			NewExpression()
		);
	}
	
	public Rule PostfixExpression() {
		return Sequence(
			LeftHandSideExpression(),
			Optional(FirstOf(
				Sequence(Terminal("++"), push(concat(peek(), List(Dup()), List(Push(Value(1))), List(Add()), convertToWrite(pop())))),
				Sequence(Terminal("--"), push(concat(peek(), List(Dup()), List(Push(Value(1))), List(Minus()), convertToWrite(pop()))))
			))
		);
	}
	
	public Rule UnaryExpression() {
		return FirstOf(
			Sequence(Terminal("delete"), UnaryExpression()),
			Sequence(Terminal("void"), UnaryExpression(), push(concat(pop(), List(Pop()), List(Push(NullValue()))))),
			Sequence(Terminal("++"), UnaryExpression(), push(concat(peek(), List(Push(Value(1))), List(Add()), List(Dup()), convertToWrite(pop())))),
			Sequence(Terminal("--"), UnaryExpression(), push(concat(peek(), List(Push(Value(1))), List(Minus()), List(Dup()), convertToWrite(pop())))),
			Sequence(Terminal("+"), UnaryExpression(), push(concat(pop(), List(UnaryPlus())))),
			Sequence(Terminal("-"), UnaryExpression(), push(concat(pop(), List(UnaryMinus())))),
			Sequence(Terminal("~"), UnaryExpression(), push(concat(pop(), List(BitwiseNot())))),
			Sequence(Terminal("!"), UnaryExpression(), push(concat(pop(), List(Not())))),
			PostfixExpression()
		);
	}
	
	public Rule MultiplicativeExpression() {
		return Sequence(UnaryExpression(),
			Optional(FirstOf(
				Sequence(Terminal("*"), push(List(Multiply()))),
				Sequence(Terminal("/"), push(List(Divide()))),
				Sequence(Terminal("%"), push(List(Modulo())))
			), MultiplicativeExpression(), push(concat(pop(2), pop(), pop())))
		);
	}
	
	public Rule AdditiveExpression() {
		return Sequence(MultiplicativeExpression(),
			Optional(FirstOf(
				Sequence(Terminal("+"), push(List(Add()))),
				Sequence(Terminal("-"), push(List(Minus())))
			), AdditiveExpression(), push(concat(pop(2), pop(), pop())))
		);
	}
	
	public Rule ShiftExpression() {
		return Sequence(AdditiveExpression(),
			Optional(FirstOf(
				Sequence(Terminal("<<"), push(List(ShiftLeft()))),
				Sequence(Terminal(">>>"), push(List(UnsignedShiftRight()))),
				Sequence(Terminal(">>"), push(List(ShiftRight())))
			), ShiftExpression(), push(concat(pop(2), pop(), pop())))
		);
	}
	
	public Rule RelationalExpression() {
		return Sequence(ShiftExpression(),
			Optional(FirstOf(
				Sequence(Terminal("<="), push(List(LessThanEqual()))),
				Sequence(Terminal(">="), push(List(GreaterThanEqual()))),
				Sequence(Terminal("<"), push(List(LessThan()))),
				Sequence(Terminal(">"), push(List(GreaterThan())))
			), RelationalExpression(), push(concat(pop(2), pop(), pop())))
		);
	}
	
	public Rule EqualityExpression() {
		return Sequence(RelationalExpression(),
			Optional(FirstOf(
				Sequence(Terminal("=="), push(List(Equal()))),
				Sequence(Terminal("!="), push(asList(Equal(), Not())))
			), EqualityExpression(), push(concat(pop(2), pop(), pop())))
		);
	}
	
	public Rule BitwiseANDExpression() {
		return Sequence(EqualityExpression(),
			Optional(
				Terminal("&"),
				BitwiseANDExpression(),
				push(concat(pop(1), pop(), List(BitwiseAnd())))
			)
		);
	}
	
	public Rule BitwiseXORExpression() {
		return Sequence(BitwiseANDExpression(),
			Optional(
				Terminal("^"),
				BitwiseXORExpression(),
				push(concat(pop(1), pop(), List(BitwiseXor())))
			)
		);
	}
	
	public Rule BitwiseORExpression() {
		return Sequence(BitwiseXORExpression(),
			Optional(
				Terminal("|"),
				BitwiseORExpression(),
				push(concat(pop(1), pop(), List(BitwiseOr())))
			)
		);
	}
	
	public Rule LogicalANDExpression() {
		return Sequence(BitwiseORExpression(),
			Optional(
				Terminal("&&"),
				LogicalANDExpression(),
				push(concat(pop(1), List(Dup()), List(JumpIfFalse(Value(peek().size() + 1))), pop(), List(And())))
			)
		);
	}
	
	public Rule LogicalORExpression() {
		return Sequence(LogicalANDExpression(),
			Optional(
				Terminal("||"),
				LogicalORExpression(),
				push(concat(pop(1), List(Dup()), List(JumpIfTrue(Value(peek().size() + 1))), pop(), List(Or())))
			)
		);
	}
	
	public Rule ConditionalExpression() {
		return Sequence(LogicalORExpression(),
			Optional(
				Terminal("?"),
				AssignmentExpression(),
				Terminal(":"),
				AssignmentExpression(),
				push(concat(
					pop(2),
					List(JumpIfFalse(Value(peek(1).size() + 1))),
					pop(1),
					List(Jump(Value(peek().size()))),
					pop()
				))
			)
		);
	}
	
	public Rule AssignmentExpression() {
		return FirstOf(
			Sequence(
				LeftHandSideExpression(),
				Terminal("="),
				AssignmentExpression(),
				push(concat(pop(), List(Dup()), convertToWrite(pop())))
			),
			Sequence(
				LeftHandSideExpression(),
				CompoundAssignmentOperator(),
				AssignmentExpression(),
				push(concat(peek(2), pop(), pop(), List(Dup()), convertToWrite(pop())))
			),
			ConditionalExpression()
		);
	}
	
	public Rule CompoundAssignmentOperator() {
		return FirstOf(	
			Sequence(Terminal("*="), push(List(Multiply()))),
			Sequence(Terminal("/="), push(List(Divide()))),
			Sequence(Terminal("%="), push(List(Modulo()))),
			Sequence(Terminal("+="), push(List(Add()))),
			Sequence(Terminal("-="), push(List(Minus()))),
			Sequence(Terminal("<<="), push(List(ShiftLeft()))),
			Sequence(Terminal(">>="), push(List(ShiftRight()))),
			Sequence(Terminal(">>>="), push(List(UnsignedShiftRight()))),
			Sequence(Terminal("&="), push(List(BitwiseAnd()))),
			Sequence(Terminal("^="), push(List(BitwiseXor()))),
			Sequence(Terminal("|="), push(List(BitwiseOr())))
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
		return Sequence(Terminal("{"), StatementList(), Terminal("}"));
	}
	
	public Rule StatementList() {
		return Sequence(push(new ArrayList<Instruction>()), ZeroOrMore(Statement(), push(concat(pop(1), pop()))));
	}
	
	public Rule VariableStatement() {
		return Sequence(Terminal("var"), VariableDeclarationList(), Terminal(";"));
	}
	
	public Rule VariableDeclarationList() {
		return Sequence(VariableDeclaration(), ZeroOrMore(Terminal(","), VariableDeclaration(), push(concat(pop(1), pop()))));
	}
	
	public Rule VariableDeclaration() {
		Var<String> name = new Var<>();
		return Sequence(
			Identifier(),
			name.set(match().trim()),
			push(List(Local(Value(name.get())))),
			Optional(Initialiser(), push(concat(pop(1), pop(), List(Store(Value(name.get()))))))
		);
	}
	
	public Rule Initialiser() {
		return Sequence(Terminal("="), AssignmentExpression());
	}
	
	public Rule EmptyStatement() {
		return Sequence(push(new ArrayList<Instruction>()), Terminal(";"));
	}
	
	public Rule ExpressionStatement() {
		return Sequence(TestNot(FirstOf("{", "function")), Expression(), push(concat(pop(), List(PopInstruction.Pop()))), Terminal(";"));
	}
	
	public Rule IfStatement() {
		return FirstOf(
			Sequence(
				Terminal("if"), Terminal("("), Expression(), Terminal(")"), Statement(), Terminal("else"), Statement(),
				push(concat(
					pop(2),
					List(JumpIfFalse(Value(peek(1).size() + 1))),
					pop(1),
					List(Jump(Value(peek().size()))),
					pop()
				))
			),
			Sequence(
				Terminal("if"), Terminal("("), Expression(), Terminal(")"), Statement(),
				push(concat(
					pop(1),
					List(JumpIfFalse(Value(peek().size()))),
					pop()
				))
			)
		);
	}
	
	public Rule IterationStatement() {
		return FirstOf(
			Sequence(
				Terminal("do"), Statement(), Terminal("while"), Terminal("("), Expression(), Terminal(")"), Terminal(";"),
				push(concat(
					peek(1),
					peek(),
					List(JumpIfTrue(Value(-pop().size() - pop().size() - 1)))
				))
			),
			Sequence(
				Terminal("while"), Terminal("("), Expression(), Terminal(")"), Statement(),
				push(concat(
					peek(1),
					List(JumpIfFalse(Value(peek().size() + 1))),
					peek(),
					List(Jump(Value(-pop().size() - pop().size() - 2)))
				))
			),
			Sequence(
				Terminal("for"),
				Terminal("("),
				OptionalOr(Expression(), Push(NullValue())),
				Terminal(";"),
				OptionalOr(Expression(), Push(Value(true))),
				Terminal(";"),
				OptionalOr(Expression(), Push(NullValue())),
				Terminal(")"),
				Statement(),
				push(concat(
					pop(3),
					List(Pop()),
					peek(2),
					List(JumpIfFalse(Value(peek(0).size() + peek(1).size() + 4))),
					peek(0),
					peek(1),
					List(Pop()),
					List(Jump(Value(-pop().size() - pop().size() - pop().size() - 3)))
				))
			),
			Sequence(
				Terminal("for"),
				Terminal("("),
				Terminal("var"),
				VariableDeclarationList(),
				Terminal(";"),
				OptionalOr(Expression(), Push(Value(true))),
				Terminal(";"),
				OptionalOr(Expression(), Push(NullValue())),
				Terminal(")"),
				Statement(),
				push(concat(
					pop(3),
					peek(2),
					List(JumpIfFalse(Value(peek(0).size() + peek(1).size() + 4))),
					peek(0),
					peek(1),
					List(Pop()),
					List(Jump(Value(-pop().size() - pop().size() - pop().size() - 3)))
				))
			)
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
		return Sequence(Terminal("case"), Expression(), Terminal(":"), StatementList());
	}
	
	public Rule DefaultClause() {
		return Sequence(Terminal("default"), Terminal(":"), StatementList());
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
		return Sequence(SourceElement(), ZeroOrMore(SourceElement(), push(concat(pop(1), pop()))));
	}

	public Rule SourceElement() {
		return FirstOf(
			Statement(),
			FunctionDeclaration()
		);
	}
	
	@SuppressSubnodes
	public Rule Terminal(Object value) {
		return Sequence(value, Optional(OneOrMore(FirstOf(" ", "\r", "\n", "\t"))));
	}
	
	/**
	 * Matches optional or pushes instruction onto the stack.
	 */
	public Rule OptionalOr(Rule optional, Instruction instruction) {
		return FirstOf(optional, push(List(instruction)));
	}

	/**
	 * Converts a list of instructions so that the read last instruction becomes the corresponding write instruction.
	 */
	protected static List<Instruction> convertToWrite(List<Instruction> instructions) {
		List<Instruction> newInstructions = new ArrayList<>();
		for(int i = 0; i < instructions.size() - 1; i++) {
			newInstructions.add(instructions.get(i));
		}
		Instruction last = instructions.get(instructions.size() - 1);
		if(last instanceof LoadInstruction) {
			newInstructions.add(new StoreInstruction(Value(((LoadInstruction)last).getName())));
		}
		return newInstructions;
	}

	protected static <T> List<T> List(T t) {
		return Collections.singletonList(t);
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
