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
import static instruction.EndFunctionInstruction.EndFunction;
import static instruction.EqualInstruction.Equal;
import static instruction.GetElementInstruction.GetElementInstruction;
import static instruction.GetPropertyInstruction.GetProperty;
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
import static instruction.NewArrayInstruction.NewArray;
import static instruction.NewObjectInstruction.NewObject;
import static instruction.NopInstruction.Nop;
import static instruction.NotInstruction.Not;
import static instruction.OrInstruction.Or;
import static instruction.PopInstruction.Pop;
import static instruction.PushElementInstruction.PushElement;
import static instruction.PushInstruction.Push;
import static instruction.SetPropertyInstruction.SetProperty;
import static instruction.ShiftLeftInstruction.ShiftLeft;
import static instruction.ShiftRightInstruction.ShiftRight;
import static instruction.StartFunctionInstruction.StartFunction;
import static instruction.StoreInstruction.Store;
import static instruction.SwapInstruction.Swap;
import static instruction.UnaryMinusInstruction.UnaryMinus;
import static instruction.UnaryPlusInstruction.UnaryPlus;
import static instruction.UnsignedShiftRightInstruction.UnsignedShiftRight;
import static parser.Instructions.Instructions;
import static value.BooleanValue.Value;
import static value.DoubleValue.Value;
import static value.NullValue.NullValue;
import static value.StringValue.Value;
import instruction.Instruction;
import instruction.ReturnInstruction;

import org.parboiled.BaseParser;
import org.parboiled.Rule;
import org.parboiled.annotations.BuildParseTree;
import org.parboiled.annotations.SuppressSubnodes;
import org.parboiled.support.Var;

@BuildParseTree
public class Parser extends BaseParser<Instructions> {
	public Rule Literal() {
		return FirstOf(
			NullLiteral(),
			BooleanLiteral(),
			NumericLiteral(),
			StringLiteral()
		);
	}
	
	public Rule NullLiteral() {
		return Sequence(Terminal("null"), push(Instructions(Push(NullValue()))));
	}
	
	public Rule BooleanLiteral() {
		return FirstOf(
			Sequence(Terminal("true"), push(Instructions(Push(Value(true))))),
			Sequence(Terminal("false"), push(Instructions(Push(Value(false)))))
		);
	}
	
	@SuppressSubnodes
	public Rule NumericLiteral() {
		return Terminal(Sequence(
			Sequence(Optional('-'), CharRange('0', '9'), ZeroOrMore(FirstOf(CharRange('0', '9'), '.'))),
			push(Instructions(Push(Value(Double.parseDouble(match())))))
		));
	}
	
	@SuppressSubnodes
	public Rule StringLiteral() {
		return FirstOf(
			Terminal(Sequence("'", ZeroOrMore(TestNot("'"), ANY), push(Instructions(Push(Value(match())))), "'")),
			Terminal(Sequence("\"", ZeroOrMore(TestNot("\""), ANY), push(Instructions(Push(Value(match())))), "\""))
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
			Literal(),
			Sequence(Identifier(), push(Instructions(Load(match().trim())))),
			ArrayLiteral(),
			ObjectLiteral(),
			Sequence(Terminal("("), Expression(), Terminal(")"))
		);
	}
	
	public Rule ArrayLiteral() {
		return Sequence(
			Terminal("["),
			push(Instructions(NewArray())),
			Optional(
				ZeroOrMore(
					AssignmentExpression(),
					Terminal(","),
					push(Instructions(
						pop(1),
						Instructions(Dup()),
						pop(),
						Instructions(PushElement())
					))
				),
				AssignmentExpression(),
				push(Instructions(
					pop(1),
					Instructions(Dup()),
					pop(),
					Instructions(PushElement())
				))
			),
			Terminal("]")
		);
	}
	
	public Rule Elision() {
		return OneOrMore(Terminal(","));
	}
	
	public Rule ObjectLiteral() {
		return Sequence(
			push(Instructions(NewObject())),
			Terminal("{"),
			Optional(PropertyNameAndValueList(), push(Instructions(pop(1), pop()))),
			Terminal("}")
		);
	}
	
	public Rule PropertyNameAndValueList() {
		return Sequence(
			Identifier(), push(Instructions(SetProperty(match().trim()))),
			Terminal(":"),
			AssignmentExpression(),
			push(Instructions(Instructions(Dup()), pop(), pop())),
			Optional(Terminal(","), PropertyNameAndValueList(), push(Instructions(pop(1), pop())))
		);
	}
	
	public Rule MemberExpression() {
		return Sequence(FirstOf(	
			FunctionExpression(),
			PrimaryExpression()
		), ZeroOrMore(FirstOf(
			Sequence(
				Terminal("["),
				Expression(),
				push(Instructions(pop(1), pop(), Instructions(GetElementInstruction()))),
				Terminal("]")
			),
			Sequence(
				Terminal("."),
				Identifier(),
				push(Instructions(pop(), Instructions(GetProperty(match().trim()))))
			)
		)));
	}
	
	public Rule NewExpression() {
		return MemberExpression();
	}
	
	public Rule CallExpression() {
		Var<CallableInstructions> callable = new Var<>();
		
		return Sequence(
			Sequence(
				MemberExpression(),
				Arguments(),
				callable.set(new CallableInstructions(pop(2))),
				push(Instructions(
					Instructions(callable.get().getPrefix()),
					Instructions(callable.get().getRead()),
					pop(),
					pop(),
					Instructions(Call())
				))
			),
			ZeroOrMore(FirstOf(
				Sequence(
					Arguments(),
					push(Instructions(
						Instructions(Push(NullValue())),
						Instructions(pop(2)),
						pop(),
						pop(),
						Instructions(Call())
					))
				),
				Sequence(
					Terminal("["),
					Expression(),
					push(Instructions(pop(1), pop(), Instructions(GetElementInstruction()))),
					Terminal("]")
				),
				Sequence(
					Terminal("."),
					Identifier(),
					push(Instructions(pop(), Instructions(GetProperty(match().trim()))))
				)
			))
		);
	}
	
	public Rule Arguments() {
		return FirstOf(	
			Sequence(
				Terminal("("),
				Terminal(")"),
				push(Instructions(Push(Value(1)), Swap())),
				push(Instructions())
			),
			Sequence(Terminal("("), ArgumentList(), Terminal(")"))
		);
	}
	
	public Rule ArgumentList() {
		Var<Integer> argCount = new Var<Integer>();
		return Sequence(
			argCount.set(2),
			AssignmentExpression(), push(Instructions(pop(), Instructions(Swap()))),
			ZeroOrMore(
				Terminal(","),
				AssignmentExpression(),
				argCount.set(argCount.get() + 1),
				push(Instructions(pop(1), pop(), Instructions(Swap())))
			),
			push(Instructions(Instructions(Push(Value(argCount.get()))), Instructions(Swap()))),
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
		Var<AssignmentInstructions> assignment = new Var<>();
		
		return Sequence(
			LeftHandSideExpression(),
			Optional(FirstOf(
				Sequence(Terminal("++"), assignment.set(new AssignmentInstructions(pop())), push(Instructions(
					Instructions(assignment.get().getPrefix()),
					Instructions(assignment.get().getDup()),
					Instructions(assignment.get().getRead()),
					Instructions(assignment.get().getSwap()),
					Instructions(assignment.get().getDup()),
					Instructions(assignment.get().getRead()),
					Instructions(Push(Value(1))),
					Instructions(Add()),
					Instructions(assignment.get().getWrite())
				))),
				Sequence(Terminal("--"), assignment.set(new AssignmentInstructions(pop())), push(Instructions(
					Instructions(assignment.get().getPrefix()),
					Instructions(assignment.get().getDup()),
					Instructions(assignment.get().getRead()),
					Instructions(assignment.get().getSwap()),
					Instructions(assignment.get().getDup()),
					Instructions(assignment.get().getRead()),
					Instructions(Push(Value(1))),
					Instructions(Minus()),
					Instructions(assignment.get().getWrite())
				)))
			))
		);
	}
	
	public Rule UnaryExpression() {
		Var<AssignmentInstructions> assignment = new Var<>();
		
		return FirstOf(
			Sequence(Terminal("delete"), UnaryExpression()),
			Sequence(Terminal("void"), UnaryExpression(), push(Instructions(pop(), Instructions(Pop()), Instructions(Push(NullValue()))))),
			Sequence(Terminal("++"), UnaryExpression(), assignment.set(new AssignmentInstructions(pop())), push(Instructions(
				Instructions(assignment.get().getPrefix()),
				Instructions(assignment.get().getDup()),
				Instructions(assignment.get().getDup()),
				Instructions(assignment.get().getRead()),
				Instructions(Push(Value(1))),
				Instructions(Add()),
				Instructions(assignment.get().getWrite()),
				Instructions(assignment.get().getRead())
			))),
			Sequence(Terminal("--"), UnaryExpression(), assignment.set(new AssignmentInstructions(pop())), push(Instructions(
				Instructions(assignment.get().getPrefix()),
				Instructions(assignment.get().getDup()),
				Instructions(assignment.get().getDup()),
				Instructions(assignment.get().getRead()),
				Instructions(Push(Value(1))),
				Instructions(Minus()),
				Instructions(assignment.get().getWrite()),
				Instructions(assignment.get().getRead())
			))),
			Sequence(Terminal("+"), UnaryExpression(), push(Instructions(pop(), Instructions(UnaryPlus())))),
			Sequence(Terminal("~"), UnaryExpression(), push(Instructions(pop(), Instructions(BitwiseNot())))),
			Sequence(Terminal("!"), UnaryExpression(), push(Instructions(pop(), Instructions(Not())))),
			PostfixExpression(),
			Sequence(Terminal("-"), UnaryExpression(), push(Instructions(pop(), Instructions(UnaryMinus()))))
		);
	}
	
	public Rule MultiplicativeExpression() {
		return Sequence(UnaryExpression(),
			Optional(FirstOf(
				Sequence(Terminal("*"), push(Instructions(Multiply()))),
				Sequence(Terminal("/"), push(Instructions(Divide()))),
				Sequence(Terminal("%"), push(Instructions(Modulo())))
			), MultiplicativeExpression(), push(Instructions(pop(2), pop(), pop())))
		);
	}
	
	public Rule AdditiveExpression() {
		return Sequence(MultiplicativeExpression(),
			Optional(FirstOf(
				Sequence(Terminal("+"), push(Instructions(Add()))),
				Sequence(Terminal("-"), push(Instructions(Minus())))
			), AdditiveExpression(), push(Instructions(pop(2), pop(), pop())))
		);
	}
	
	public Rule ShiftExpression() {
		return Sequence(AdditiveExpression(),
			Optional(FirstOf(
				Sequence(Terminal("<<"), push(Instructions(ShiftLeft()))),
				Sequence(Terminal(">>>"), push(Instructions(UnsignedShiftRight()))),
				Sequence(Terminal(">>"), push(Instructions(ShiftRight())))
			), ShiftExpression(), push(Instructions(pop(2), pop(), pop())))
		);
	}
	
	public Rule RelationalExpression() {
		return Sequence(ShiftExpression(),
			Optional(FirstOf(
				Sequence(Terminal("<="), push(Instructions(LessThanEqual()))),
				Sequence(Terminal(">="), push(Instructions(GreaterThanEqual()))),
				Sequence(Terminal("<"), push(Instructions(LessThan()))),
				Sequence(Terminal(">"), push(Instructions(GreaterThan())))
			), RelationalExpression(), push(Instructions(pop(2), pop(), pop())))
		);
	}
	
	public Rule EqualityExpression() {
		return Sequence(RelationalExpression(),
			Optional(FirstOf(
				Sequence(Terminal("=="), push(Instructions(Equal()))),
				Sequence(Terminal("!="), push(Instructions(Equal(), Not())))
			), EqualityExpression(), push(Instructions(pop(2), pop(), pop())))
		);
	}
	
	public Rule BitwiseANDExpression() {
		return Sequence(EqualityExpression(),
			Optional(
				Terminal("&"),
				BitwiseANDExpression(),
				push(Instructions(pop(1), pop(), Instructions(BitwiseAnd())))
			)
		);
	}
	
	public Rule BitwiseXORExpression() {
		return Sequence(BitwiseANDExpression(),
			Optional(
				Terminal("^"),
				BitwiseXORExpression(),
				push(Instructions(pop(1), pop(), Instructions(BitwiseXor())))
			)
		);
	}
	
	public Rule BitwiseORExpression() {
		return Sequence(BitwiseXORExpression(),
			Optional(
				Terminal("|"),
				BitwiseORExpression(),
				push(Instructions(pop(1), pop(), Instructions(BitwiseOr())))
			)
		);
	}
	
	public Rule LogicalANDExpression() {
		return Sequence(BitwiseORExpression(),
			Optional(
				Terminal("&&"),
				LogicalANDExpression(),
				push(Instructions(
					pop(1),
					Instructions(Dup()),
					Instructions(JumpIfFalse(peek().size() + 2)),
					pop(),
					Instructions(And())
				))
			)
		);
	}
	
	public Rule LogicalORExpression() {
		return Sequence(LogicalANDExpression(),
			Optional(
				Terminal("||"),
				LogicalORExpression(),
				push(Instructions(
					pop(1),
					Instructions(Dup()),
					Instructions(JumpIfTrue(peek().size() + 2)),
					pop(),
					Instructions(Or())
				))
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
				push(Instructions(
					pop(2),
					Instructions(JumpIfFalse(peek(1).size() + 2)),
					pop(1),
					Instructions(Jump(peek().size() + 1)),
					pop()
				))
			)
		);
	}
	
	public Rule AssignmentExpression() {
		Var<AssignmentInstructions> assignment = new Var<>();
		
		return FirstOf(
			Sequence(
				LeftHandSideExpression(),
				Terminal("="),
				assignment.set(new AssignmentInstructions(pop())),
				AssignmentExpression(),
				push(Instructions(
					Instructions(assignment.get().getPrefix()),
					Instructions(assignment.get().getDup()),
					pop(),
					Instructions(assignment.get().getWrite()),
					Instructions(assignment.get().getRead())
				))
			),
			Sequence(
				LeftHandSideExpression(),
				CompoundAssignmentOperator(),
				assignment.set(new AssignmentInstructions(pop(1))),
				AssignmentExpression(),
				push(Instructions(
					Instructions(assignment.get().getPrefix()),
					Instructions(assignment.get().getDup()),
					Instructions(assignment.get().getDup()),
					Instructions(assignment.get().getRead()),
					pop(),
					pop(),
					Instructions(assignment.get().getWrite()),
					Instructions(assignment.get().getRead())
				))
			),
			ConditionalExpression()
		);
	}
	
	public Rule CompoundAssignmentOperator() {
		return FirstOf(	
			Sequence(Terminal("*="), push(Instructions(Multiply()))),
			Sequence(Terminal("/="), push(Instructions(Divide()))),
			Sequence(Terminal("%="), push(Instructions(Modulo()))),
			Sequence(Terminal("+="), push(Instructions(Add()))),
			Sequence(Terminal("-="), push(Instructions(Minus()))),
			Sequence(Terminal("<<="), push(Instructions(ShiftLeft()))),
			Sequence(Terminal(">>="), push(Instructions(ShiftRight()))),
			Sequence(Terminal(">>>="), push(Instructions(UnsignedShiftRight()))),
			Sequence(Terminal("&="), push(Instructions(BitwiseAnd()))),
			Sequence(Terminal("^="), push(Instructions(BitwiseXor()))),
			Sequence(Terminal("|="), push(Instructions(BitwiseOr())))
		);
	}
	
	public Rule Expression() {
		return Sequence(
			AssignmentExpression(),
			ZeroOrMore(
				push(Instructions(pop(), Instructions(Pop()))),
				Terminal(","),
				AssignmentExpression(),
				push(Instructions(pop(1), pop()))
			)
		);
	}
	
	public Rule Statement() {
		return FirstOf(
			Block(),
			VariableStatement(),
			EmptyStatement(),
			ReturnStatement(),
			IfStatement(),
			IterationStatement(),
			SwitchStatement(),
			ExpressionStatement()
		);
	}
	
	public Rule Block() {
		return Sequence(Terminal("{"), StatementList(), Terminal("}"));
	}
	
	public Rule StatementList() {
		return Sequence(
			push(Instructions()),
			ZeroOrMore(
				Statement(),
				push(Instructions(pop(1), pop()))
			)
		);
	}
	
	public Rule VariableStatement() {
		return Sequence(Terminal("var"), VariableDeclarationList(), Terminal(";"));
	}
	
	public Rule VariableDeclarationList() {
		return Sequence(
			VariableDeclaration(),
			ZeroOrMore(
				Terminal(","),
				VariableDeclaration(),
				push(Instructions(pop(1), pop()))
			)
		);
	}
	
	public Rule VariableDeclaration() {
		Var<String> name = new Var<>();
		return Sequence(
			Identifier(),
			name.set(match().trim()),
			push(Instructions(Local(name.get()))),
			Optional(
				Initialiser(),
				push(Instructions(
					pop(),
					pop(),
					Instructions(Store(name.get()))
				))
			)
		);
	}
	
	public Rule Initialiser() {
		return Sequence(Terminal("="), AssignmentExpression());
	}
	
	public Rule EmptyStatement() {
		return Sequence(push(Instructions()), Terminal(";"));
	}
	
	public Rule ExpressionStatement() {
		return Sequence(
			TestNot(FirstOf("{", "function")),
			Expression(),
			push(Instructions(pop(), Instructions(Pop()))),
			Terminal(";")
		);
	}
	
	public Rule IfStatement() {
		return FirstOf(
			Sequence(
				Terminal("if"), Terminal("("), Expression(), Terminal(")"), Statement(), Terminal("else"), Statement(),
				push(Instructions(
					pop(2),
					Instructions(JumpIfFalse(peek(1).size() + 2)),
					pop(1),
					Instructions(Jump(peek().size() + 1)),
					pop()
				))
			),
			Sequence(
				Terminal("if"), Terminal("("), Expression(), Terminal(")"), Statement(),
				push(Instructions(
					pop(1),
					Instructions(JumpIfFalse(peek().size() + 1)),
					pop()
				))
			)
		);
	}
	
	public Rule IterationStatement() {
		return FirstOf(
			Sequence(
				Terminal("do"), Statement(), Terminal("while"), Terminal("("), Expression(), Terminal(")"), Terminal(";"),
				push(Instructions(
					peek(1),
					peek(),
					Instructions(JumpIfTrue(-pop().size() - pop().size()))
				))
			),
			Sequence(
				Terminal("while"), Terminal("("), Expression(), Terminal(")"), Statement(),
				push(Instructions(
					peek(1),
					Instructions(JumpIfFalse(peek().size() + 2)),
					peek(),
					Instructions(Jump(-pop().size() - pop().size() - 1))
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
				push(Instructions(
					pop(3),
					Instructions(Pop()),
					peek(2),
					Instructions(JumpIfFalse(peek(0).size() + peek(1).size() + 3)),
					peek(0),
					peek(1),
					Instructions(Pop()),
					Instructions(Jump(-pop().size() - pop().size() - pop().size() - 2))
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
				push(Instructions(
					pop(3),
					peek(2),
					Instructions(JumpIfFalse(peek(0).size() + peek(1).size() + 3)),
					peek(0),
					peek(1),
					Instructions(Pop()),
					Instructions(Jump(-pop().size() - pop().size() - pop().size() - 2))
				))
			)
		);
	}
	
	public Rule ReturnStatement() {
		return Sequence(
			Terminal("return"), push(Instructions(ReturnInstruction.Return())),
			Optional(Expression(), push(Instructions(Instructions(Pop()), pop(), pop()))), Terminal(";")
		);
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
		return Sequence(
			Terminal("function"),
			Sequence(
				Identifier(),
				push(Instructions(
					Instructions(Local(match().trim())),
					Instructions(Store(match().trim()))
				))
			),
			Terminal("("),
			FormalParameterList(),
			Terminal(")"),
			Terminal("{"),
			FunctionBody(),
			Terminal("}"),
			push(Instructions(
				Instructions(StartFunction(peek(1).getInstructions().size() / 2)),
				Instructions(Pop()),
				pop(1),
				Instructions(Push(NullValue())),
				pop(),
				Instructions(EndFunction()),
				pop()
			))
		);
	}
	
	public Rule FunctionExpression() {
		return Sequence(
			Terminal("function"),
			Terminal("("),
			FormalParameterList(),
			Terminal(")"),
			Terminal("{"),
			FunctionBody(),
			Terminal("}"),
			push(Instructions(
				Instructions(StartFunction(peek(1).getInstructions().size() / 2)),
				Instructions(Pop()),
				pop(1),
				Instructions(Push(NullValue())),
				pop(),
				Instructions(EndFunction())
			))
		);
	}
	
	public Rule FormalParameterList() {
		return Sequence(
			push(Instructions(
				Instructions(Local("this")),
				Instructions(Store("this"))
			)),
			Optional(
				FormalParameter(), push(Instructions(pop(), pop())),
				ZeroOrMore(
					Terminal(","), FormalParameter(), push(Instructions(pop(), pop()))
				)
			)
		);
	}
	
	public Rule FormalParameter() {
		return Sequence(
			Identifier(),
			push(
				Instructions(
					Instructions(Local(match().trim())),
					Instructions(Store(match().trim()))
				)
			)
		);
	}
	
	public Rule FunctionBody() {
		return Sequence(
			push(Instructions()),
			Optional(SourceElements(), push(Instructions(pop(), pop())))
		);
	}
	
	public Rule Program() {
		return Sequence(Spacing(), OptionalOr(SourceElements(), Nop()), EOI);
	}
	
	public Rule SourceElements() {
		return Sequence(SourceElement(), ZeroOrMore(SourceElement(), push(Instructions(pop(1), pop()))));
	}

	public Rule SourceElement() {
		return FirstOf(
			Statement(),
			FunctionDeclaration()
		);
	}
	
	public Rule Comment() {
		return FirstOf(
			Sequence(
				"//",
				ZeroOrMore(TestNot(AnyOf("\r\n")), ANY),
				Optional(OneOrMore(AnyOf(" \r\n\t")))
			),
			Sequence(
				"/*",
				ZeroOrMore(TestNot("*/"), ANY),
				"*/",
				Optional(OneOrMore(AnyOf(" \r\n\t")))
			)
		);
	}

	public Rule Spacing() {
		return ZeroOrMore(FirstOf(Comment(), AnyOf(" \r\n\t")));
	}
	
	@SuppressSubnodes
	public Rule Terminal(Object value) {
		return Sequence(value, Spacing());
	}
	
	/**
	 * Matches optional or pushes instruction onto the stack.
	 */
	public Rule OptionalOr(Rule optional, Instruction instruction) {
		return FirstOf(optional, push(Instructions(instruction)));
	}	
}
