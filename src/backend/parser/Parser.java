package backend.parser;

import static backend.instruction.NopInstruction.Nop;
import static backend.instruction.array.GetElementInstruction.GetElement;
import static backend.instruction.array.NewArrayInstruction.NewArray;
import static backend.instruction.array.PushElementInstruction.PushElement;
import static backend.instruction.function.CallInstruction.Call;
import static backend.instruction.function.EndFunctionInstruction.EndFunction;
import static backend.instruction.function.StartFunctionInstruction.StartFunction;
import static backend.instruction.jump.JumpIfFalseInstruction.JumpIfFalse;
import static backend.instruction.jump.JumpIfTrueInstruction.JumpIfTrue;
import static backend.instruction.jump.JumpInstruction.Jump;
import static backend.instruction.object.GetPropertyInstruction.GetProperty;
import static backend.instruction.object.NewObjectInstruction.NewObject;
import static backend.instruction.object.SetPropertyInstruction.SetProperty;
import static backend.instruction.operator.AddInstruction.Add;
import static backend.instruction.operator.AndInstruction.And;
import static backend.instruction.operator.BitwiseAndInstruction.BitwiseAnd;
import static backend.instruction.operator.BitwiseNotInstruction.BitwiseNot;
import static backend.instruction.operator.BitwiseOrInstruction.BitwiseOr;
import static backend.instruction.operator.BitwiseXorInstruction.BitwiseXor;
import static backend.instruction.operator.DivideInstruction.Divide;
import static backend.instruction.operator.EqualInstruction.Equal;
import static backend.instruction.operator.GreaterThanEqualInstruction.GreaterThanEqual;
import static backend.instruction.operator.GreaterThanInstruction.GreaterThan;
import static backend.instruction.operator.LessThanEqualInstruction.LessThanEqual;
import static backend.instruction.operator.LessThanInstruction.LessThan;
import static backend.instruction.operator.MinusInstruction.Minus;
import static backend.instruction.operator.ModuloInstruction.Modulo;
import static backend.instruction.operator.MultiplyInstruction.Multiply;
import static backend.instruction.operator.NotInstruction.Not;
import static backend.instruction.operator.OrInstruction.Or;
import static backend.instruction.operator.ShiftLeftInstruction.ShiftLeft;
import static backend.instruction.operator.ShiftRightInstruction.ShiftRight;
import static backend.instruction.operator.UnaryMinusInstruction.UnaryMinus;
import static backend.instruction.operator.UnaryPlusInstruction.UnaryPlus;
import static backend.instruction.operator.UnsignedShiftRightInstruction.UnsignedShiftRight;
import static backend.instruction.stack.DupInstruction.Dup;
import static backend.instruction.stack.PopInstruction.Pop;
import static backend.instruction.stack.PushInstruction.Push;
import static backend.instruction.stack.SwapInstruction.Swap;
import static backend.instruction.variable.LoadInstruction.Load;
import static backend.instruction.variable.LocalInstruction.Local;
import static backend.instruction.variable.StoreInstruction.Store;
import static backend.instruction.viz.EnableVizFilterInstruction.EnableVizFilterInstruction;
import static backend.instruction.viz.EnableVizUpdatesInstruction.EnableVizUpdatesInstruction;
import static backend.instruction.viz.EndVizInstruction.EndVizInstruction;
import static backend.instruction.viz.NewVizObjectInstruction.NewVizObjectInstruction;
import static backend.instruction.viz.SetVizFilterPropertyInstruction.SetVizFilterPropertyInstruction;
import static backend.instruction.viz.SetVizPropertyInstruction.SetVizPropertyInstruction;
import static backend.instruction.viz.StartVizInstruction.StartVizInstruction;
import static backend.instruction.viz.VizFilterInstruction.VizFilterInstruction;
import static backend.instruction.viz.VizIterateInstruction.VizIterateInstruction;
import static backend.parser.Instructions.Instructions;
import static backend.value.BooleanValue.Value;
import static backend.value.DoubleValue.Value;
import static backend.value.NullValue.NullValue;
import static backend.value.StringValue.Value;

import java.util.List;

import org.parboiled.BaseParser;
import org.parboiled.Rule;
import org.parboiled.annotations.DontLabel;
import org.parboiled.annotations.SuppressNode;
import org.parboiled.annotations.SuppressSubnodes;
import org.parboiled.support.Var;

import backend.instruction.Instruction;
import backend.instruction.function.ReturnInstruction;
import backend.value.Identifier;

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
			Sequence(Identifier(), push(Instructions(Load(createIdentifier())))),
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
	
	public Rule ObjectLiteral() {
		return Sequence(
			push(Instructions(NewObject())),
			Terminal("{"),
			Optional(PropertyNameAndValueList(), mergeAfter()),
			Terminal("}")
		);
	}
	
	public Rule PropertyNameAndValueList() {
		return Sequence(
			Identifier(), push(Instructions(SetProperty(createIdentifier()))),
			Terminal(":"),
			AssignmentExpression(),
			push(Instructions(Instructions(Dup()), pop(), pop())),
			Optional(Terminal(","), PropertyNameAndValueList(), mergeAfter())
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
				push(Instructions(pop(1), pop(), Instructions(GetElement()))),
				Terminal("]")
			),
			Sequence(
				Terminal("."),
				Identifier(),
				push(Instructions(pop(), Instructions(GetProperty(createIdentifier()))))
			)
		)));
	}
	
	public Rule CallExpression() {
		Var<CallableInstructions> callable = new Var<>();
		
		return Sequence(
			Sequence(
				MemberExpression(),
				Arguments(),
				callable.set(new CallableInstructions(pop(1))),
				push(Instructions(
					Instructions(callable.get().getPrefix()),
					Instructions(callable.get().getRead()),
					pop(),
					Instructions(Call())
				))
			),
			ZeroOrMore(FirstOf(
				Sequence(
					Arguments(),
					callable.set(new CallableInstructions(pop(1))),
					push(Instructions(
						Instructions(callable.get().getPrefix()),
						Instructions(callable.get().getRead()),
						pop(),
						Instructions(Call())
					))
				),
				Sequence(
					Terminal("["),
					Expression(),
					push(Instructions(pop(1), pop(), Instructions(GetElement()))),
					Terminal("]")
				),
				Sequence(
					Terminal("."),
					Identifier(),
					push(Instructions(pop(), Instructions(GetProperty(createIdentifier()))))
				)
			))
		);
	}
	
	public Rule Arguments() {
		return FirstOf(	
			Sequence(
				Terminal("("),
				Terminal(")"),
				push(Instructions(Push(Value(1)), Swap()))
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
			mergeAfter()
		);
	}
	
	public Rule LeftHandSideExpression() {
		return FirstOf(	
			CallExpression(),
			MemberExpression()
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
			ZeroOrMore(FirstOf(
				Sequence(Terminal("*"), push(Instructions(Multiply()))),
				Sequence(Terminal("/"), push(Instructions(Divide()))),
				Sequence(Terminal("%"), push(Instructions(Modulo())))
			), UnaryExpression(), push(Instructions(pop(2), pop(), pop())))
		);
	}
	
	public Rule AdditiveExpression() {
		return Sequence(MultiplicativeExpression(),
			ZeroOrMore(FirstOf(
				Sequence(Terminal("+"), push(Instructions(Add()))),
				Sequence(Terminal("-"), push(Instructions(Minus())))
			), MultiplicativeExpression(), push(Instructions(pop(2), pop(), pop())))
		);
	}
	
	public Rule ShiftExpression() {
		return Sequence(AdditiveExpression(),
			ZeroOrMore(FirstOf(
				Sequence(Terminal("<<"), push(Instructions(ShiftLeft()))),
				Sequence(Terminal(">>>"), push(Instructions(UnsignedShiftRight()))),
				Sequence(Terminal(">>"), push(Instructions(ShiftRight())))
			), AdditiveExpression(), push(Instructions(pop(2), pop(), pop())))
		);
	}
	
	public Rule RelationalExpression() {
		return Sequence(ShiftExpression(),
			ZeroOrMore(FirstOf(
				Sequence(Terminal("<="), push(Instructions(LessThanEqual()))),
				Sequence(Terminal(">="), push(Instructions(GreaterThanEqual()))),
				Sequence(Terminal("<"), push(Instructions(LessThan()))),
				Sequence(Terminal(">"), push(Instructions(GreaterThan())))
			), ShiftExpression(), push(Instructions(pop(2), pop(), pop())))
		);
	}
	
	public Rule EqualityExpression() {
		return Sequence(RelationalExpression(),
			ZeroOrMore(FirstOf(
				Sequence(Terminal("=="), push(Instructions(Equal()))),
				Sequence(Terminal("!="), push(Instructions(Equal(), Not())))
			), RelationalExpression(), push(Instructions(pop(2), pop(), pop())))
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
				FirstOf(
					Sequence(
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
					)
				)
			),
			ArrowFunction(),
			ConditionalExpression()
		);
	}
	
	public Rule ArrowFunction() {
		return Sequence(
			ArrowParameters(),
			Terminal("=>"),
			ConciseBody(),
			push(Instructions(
				Instructions(StartFunction(peek(1).getInstructions().size() / 2 + 1)),
				Instructions(Pop()),
				pop(1),
				pop(),
				Instructions(EndFunction())
			))
		);
	}
	
	public Rule ArrowParameters() {
		return FirstOf(
			Sequence(
				push(Instructions(Pop())),
				FormalParameter(),
				mergeBefore()
			),
			Sequence(
				push(Instructions(Pop())),
				Terminal("("),
				Optional(
					FormalParameter(), mergeBefore(),
					ZeroOrMore(
						Terminal(","), FormalParameter(), mergeBefore()
					)
				),
				Terminal(")")
			)
		);
	}
	
	public Rule ConciseBody() {
		return FirstOf(
			Sequence(
				Terminal("{"),
				FunctionBody(),
				push(Instructions(Push(NullValue()))),
				mergeBefore(),
				Terminal("}")
			),
			AssignmentExpression()
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
				mergeAfter()
			)
		);
	}
	
	public Rule Statement() {
		Var<Short> lineNumber = new Var<Short>();
		Var<Short> columnNumber = new Var<Short>();
		
		return Sequence(
			lineNumber.set((short) position().line),
			columnNumber.set((short) position().column),
			FirstOf(
				Block(),
				VariableStatement(),
				EmptyStatement(),
				ReturnStatement(),
				IfStatement(),
				IterationStatement(),
				ExpressionStatement(),
				VizStatement()
			),
			push(addLineNumbers(pop(), lineNumber.get(), columnNumber.get()))
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
				mergeAfter()
			)
		);
	}
	
	public Rule VariableStatement() {
		return Sequence(Terminal("var"), VariableDeclarationList(), Optional(Terminal(";")));
	}
	
	public Rule VariableDeclarationList() {
		return Sequence(
			VariableDeclaration(),
			ZeroOrMore(
				Terminal(","),
				VariableDeclaration(),
				mergeAfter()
			)
		);
	}
	
	public Rule VariableDeclaration() {
		Var<Identifier> identifier = new Var<>();
		return Sequence(
			Identifier(),
			identifier.set(createIdentifier()),
			push(Instructions(Local(identifier.get()))),
			Optional(
				Initialiser(),
				push(Instructions(
					pop(),
					pop(),
					Instructions(Store(identifier.get()))
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
			Optional(Terminal(";"))
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
				Terminal("do"), Statement(), Terminal("while"), Terminal("("), Expression(), Terminal(")"), Optional(Terminal(";")),
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
				OptionalOr(Expression(), Push(NullValue())).label("Expression"),
				Terminal(";"),
				OptionalOr(Expression(), Push(Value(true))).label("Expression"),
				Terminal(";"),
				OptionalOr(Expression(), Push(NullValue())).label("Expression"),
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
				OptionalOr(Expression(), Push(Value(true))).label("Expression"),
				Terminal(";"),
				OptionalOr(Expression(), Push(NullValue())).label("Expression"),
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
			Optional(Expression(), push(Instructions(Instructions(Pop()), pop(), pop()))), Optional(Terminal(";"))
		);
	}
	
	public Rule VizStatement() {
		Var<Identifier> identifier = new Var<>();
		return FirstOf(
			Sequence(Terminal("@vizUpdatesOn"), Optional(Terminal(";")), push(Instructions(EnableVizUpdatesInstruction(true)))),
			Sequence(Terminal("@vizUpdatesOff"), Optional(Terminal(";")), push(Instructions(EnableVizUpdatesInstruction(false)))),
			Sequence(
				Test(Sequence(
					ZeroOrMore(FirstOf(CharRange('a', 'z'), CharRange('A', 'Z'), CharRange('0', '9'), "$", "_", "@")),
					identifier.set(createIdentifier())
				)),
				push(Instructions()),
				Optional(
					Terminal("@for"),
					Terminal("("),
					VizForExpression(),
					ZeroOrMore(
						Terminal(","),
						VizForExpression(),
						mergeAfter()
					),
					mergeBefore(),
					Terminal(")")
				),
				FirstOf(
					Sequence(
						VizObject(),
						mergeAfter(),
						push(Instructions(Instructions(StartVizInstruction(identifier.get())), pop(), Instructions(EndVizInstruction())))
					),
					Sequence(
						Terminal("{"),
						ZeroOrMore(VizObject(), mergeAfter()),
						push(Instructions(Instructions(StartVizInstruction(identifier.get())), pop(), Instructions(EndVizInstruction()))),
						Terminal("}")
					)
				)
			)
		);
	}
	
	public Rule VizForExpression() {
		return FirstOf(
			Sequence(
				push(Instructions()),
				VizDestructuredExpression(),
				push(Instructions(VizIterateInstruction("_element"))),
				mergeBefore(),
				Terminal("<-"),
				AssignmentExpression(),
				mergeBefore()
			),
			Sequence(
				Optional(Terminal("var")),
				Identifier(),
				push(Instructions(VizIterateInstruction(match().trim()))),
				Terminal("<-"),
				AssignmentExpression(),
				mergeBefore()
			),
			Sequence(
				AssignmentExpression(),
				push(Instructions(pop(), Instructions(VizFilterInstruction())))
			)
		);
	}
	
	public Rule VizDestructuredExpression() {
		return FirstOf(
			VizDestructuredArrayExpression(),
			VizDestructuredObjectExpression()
		);
	}
	
	public Rule VizDestructuredArrayExpression() {
		Var<Integer> index = new Var<>(0);
		Var<Instructions> loadContext = new Var<>();
		return Sequence(
			loadContext.set(pop()),
			Terminal("["),
			push(Instructions()),
			SeparatedSequence(Terminal(","), Sequence(
				FirstOf(
					Sequence(
						push(Instructions(loadContext.get(), Instructions(Push(Value(index.get())), GetElement()))),
						VizDestructuredExpression()
					),
					VizDestructuredArrayExpressionIdentifier(loadContext, index)
				),
				mergeAfter(),
				index.set(index.get() + 1)
			)),
			Terminal("]")
		);
	}
	
	public Rule VizDestructuredArrayExpressionIdentifier(Var<Instructions> loadContext, Var<Integer> index) {
		Var<Identifier> identifier = new Var<>();
		return Sequence(
			Identifier(),
			identifier.set(createIdentifier()),
			push(Instructions(Local(identifier.get()), Load(new Identifier("_element")))),
			push(Instructions(loadContext.get(), Instructions(Push(Value(index.get())), GetElement()))),
			mergeAfter(),
			push(Instructions(Store(identifier.get()))),
			mergeAfter()
		);
	}
	
	public Rule VizDestructuredObjectExpression() {
		Var<Identifier> property = new Var<>();
		Var<Instructions> loadContext = new Var<>();
		return Sequence(
			loadContext.set(pop()),
			Terminal("{"),
			push(Instructions()),
			SeparatedSequence(Terminal(","), Sequence(
				Identifier(),
				property.set(createIdentifier()),
				Terminal(":"),
				FirstOf(
					Sequence(
						push(Instructions(loadContext.get(), Instructions(GetProperty(property.get())))),
						VizDestructuredExpression()
					),
					VizDestructuredObjectExpressionIdentifier(loadContext, property)
				),
				mergeAfter()
			)),
			Terminal("}")
		);
	}
	
	public Rule VizDestructuredObjectExpressionIdentifier(Var<Instructions> loadContext, Var<Identifier> property) {
		Var<Identifier> identifier = new Var<>();
		return Sequence(
			Identifier(),
			identifier.set(createIdentifier()),
			push(Instructions(Local(identifier.get()), Load(new Identifier("_element")))),
			push(Instructions(loadContext.get(), Instructions(GetProperty(property.get())))),
			mergeAfter(),
			push(Instructions(Store(identifier.get()))),
			mergeAfter()
		);
	}

	public Rule VizObject() {
		return Sequence(
			Terminal("@"),
			Sequence(TestNot(Terminal("for"), Terminal("(")), Identifier()),
			push(Instructions(NewVizObjectInstruction(match().trim()))),
			Optional(
				Terminal("["),
				push(Instructions(EnableVizFilterInstruction())),
				mergeAfter(),
				Optional(
					VizFilterProperty(),
					ZeroOrMore(
						Terminal(","),
						VizFilterProperty(),
						mergeAfter()
					),
					mergeAfter()
				),
				Terminal("]")
			),
			Terminal("("),
			Optional(
				VizProperty(),
				ZeroOrMore(
					Terminal(","),
					VizProperty(),
					mergeAfter()
				),
				mergeAfter()
			),
			Terminal(")"),
			Optional(Terminal(";"))
		);
	}
	
	public Rule VizProperty() {
		return Sequence(
			Identifier(),
			push(Instructions(SetVizPropertyInstruction(match().trim()))),
			Terminal(":"),
			AssignmentExpression(),
			mergeBefore()
		);
	}
	
	public Rule VizFilterProperty() {
		return Sequence(
			Identifier(),
			push(Instructions(SetVizFilterPropertyInstruction(match().trim()))),
			Terminal(":"),
			AssignmentExpression(),
			mergeBefore()
		);
	}
	
	public Rule FunctionDeclaration() {
		Var<Identifier> identifier = new Var<>();
		return Sequence(
			Terminal("function"),
			Sequence(
				Identifier(),
				identifier.set(createIdentifier()),
				push(Instructions(
					Instructions(Local(identifier.get())),
					Instructions(Store(identifier.get()))
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
				Instructions(Local(new Identifier("this"))),
				Instructions(Store(new Identifier("this")))
			)),
			Optional(
				FormalParameter(), mergeBefore(),
				ZeroOrMore(
					Terminal(","), FormalParameter(), mergeBefore()
				)
			)
		);
	}
	
	public Rule FormalParameter() {
		Var<Identifier> identifier = new Var<>();
		return Sequence(
			Identifier(),
			identifier.set(createIdentifier()),
			push(
				Instructions(
					Instructions(Local(identifier.get())),
					Instructions(Store(identifier.get()))
				)
			)
		);
	}
	
	public Rule FunctionBody() {
		return Sequence(
			push(Instructions()),
			Optional(SourceElements(), mergeBefore())
		);
	}
	
	public Rule Program() {
		return Sequence(Spacing(), OptionalOr(SourceElements(), Nop()).label("SourceElements"), EOI);
	}
	
	public Rule SourceElements() {
		return Sequence(SourceElement(), ZeroOrMore(SourceElement(), mergeAfter()));
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
	
	@SuppressNode
	@DontLabel
	public Rule Terminal(Object value) {
		return Sequence(value, Spacing());
	}
	
	public Instructions addLineNumbers(Instructions instructions, short lineNumber, short columnNumber) {
		List<Instruction> newInstructions = Instruction.copyInstructions(instructions.getInstructions());
		
		for(Instruction instruction:newInstructions) {
			if(instruction.getLineNumber() <= 0) {
				instruction.setLineNumber(lineNumber);
				instruction.setColumnNumber(columnNumber);
			}
		}
		
		return new Instructions(newInstructions);
	}
	
	public Identifier createIdentifier() {
		String name = match().trim();
		int lineNumber = position().line;
		int columnNumber = position().column - matchLength();
		return new Identifier(name, lineNumber, columnNumber);
	}
	
	public boolean mergeBefore() {
		return push(Instructions(pop(), pop()));
	}
	
	public boolean mergeAfter() {
		return push(Instructions(pop(1), pop()));
	}
	
	/**
	 * Matches optional or pushes instruction onto the stack.
	 */
	public Rule OptionalOr(Rule optional, Instruction instruction) {
		return FirstOf(optional, push(Instructions(instruction)));
	}
	
	/**
	 * Matches one or more rules separated by separator.
	 */
	public Rule SeparatedSequence(Rule separator, Rule rule) {
		return Sequence(rule, ZeroOrMore(separator, rule));
	}
}
