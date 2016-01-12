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
import static java.util.Arrays.asList;
import static value.BooleanValue.Value;
import static value.DoubleValue.Value;
import static value.NullValue.NullValue;
import static value.StringValue.Value;
import instruction.GetElementInstruction;
import instruction.GetPropertyInstruction;
import instruction.Instruction;
import instruction.LoadInstruction;
import instruction.NewArrayInstruction;
import instruction.NewObjectInstruction;
import instruction.NopInstruction;
import instruction.PushElementInstruction;
import instruction.ReturnInstruction;
import instruction.SetPropertyInstruction;
import instruction.StoreInstruction;
import instruction.SwapInstruction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.parboiled.BaseParser;
import org.parboiled.Rule;
import org.parboiled.annotations.BuildParseTree;
import org.parboiled.annotations.SuppressSubnodes;
import org.parboiled.support.Var;

import value.DoubleValue;

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
		return Sequence(
			Terminal("["),
			push(concat(List(NewArray()))),
			Optional(
				ZeroOrMore(AssignmentExpression(), Terminal(","), push(concat(pop(1), List(Dup()), pop(), List(PushElement())))),
				AssignmentExpression(), push(concat(pop(1), List(Dup()), pop(), List(PushElement())))
			),
			Terminal("]")
		);
	}
	
	public Rule Elision() {
		return OneOrMore(Terminal(","));
	}
	
	public Rule ObjectLiteral() {
		return Sequence(
			push(List(NewObject())),
			Terminal("{"),
			Optional(PropertyNameAndValueList(), push(concat(pop(1), pop()))),
			Terminal("}")
		);
	}
	
	public Rule PropertyNameAndValueList() {
		return Sequence(
			Identifier(), push(List(SetProperty(Value(match().trim())))),
			Terminal(":"),
			AssignmentExpression(),
			push(concat(List(Dup()), pop(), pop())),
			Optional(Terminal(","), PropertyNameAndValueList(), push(concat(pop(1), pop())))
		);
	}
	
	public Rule MemberExpression() {
		return Sequence(FirstOf(	
			FunctionExpression(),
			PrimaryExpression()
		), ZeroOrMore(FirstOf(
			Sequence(Terminal("["), Expression(), push(concat(pop(1), pop(), List(GetElementInstruction()))), Terminal("]")),
			Sequence(Terminal("."), Identifier(), push(concat(pop(), List(GetProperty(Value(match().trim()))))))
		)));
	}
	
	public Rule NewExpression() {
		return MemberExpression();
	}
	
	public Rule CallExpression() {
		return Sequence(
			Sequence(MemberExpression(), Arguments(), push(
				concat(pop(2), pop(), pop(), List(Call()))
			)),
			ZeroOrMore(FirstOf(
				Sequence(Arguments(), push(concat(pop(2), pop(), pop(), List(Call())))),
				Sequence(Terminal("["), Expression(), Terminal("]")),
				Sequence(Terminal("."), Identifier())
			))
		);
	}
	
	public Rule Arguments() {
		return FirstOf(	
			Sequence(Terminal("("), Terminal(")"), push(concat(List(Push(Value(0))), List(Swap()))), push(new ArrayList<Instruction>())),
			Sequence(Terminal("("), ArgumentList(), Terminal(")"))
		);
	}
	
	public Rule ArgumentList() {
		Var<Integer> argCount = new Var<Integer>();
		return Sequence(
			argCount.set(1),
			AssignmentExpression(), push(concat(pop(), List(Swap()))),
			ZeroOrMore(
				Terminal(","),
				AssignmentExpression(),
				argCount.set(argCount.get() + 1),
				push(concat(pop(1), pop(), List(Swap())))
			),
			push(concat(List(Push(Value(argCount.get()))), List(Swap()))),
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
				Sequence(Terminal("++"), assignment.set(new AssignmentInstructions(pop())), push(concat(
					assignment.get().getPrefix(),
					assignment.get().getDup(),
					assignment.get().getRead(),
					assignment.get().getSwap(),
					assignment.get().getDup(),
					assignment.get().getRead(),
					List(Push(Value(1))),
					List(Add()),
					assignment.get().getWrite()
				))),
				Sequence(Terminal("--"), assignment.set(new AssignmentInstructions(pop())), push(concat(
					assignment.get().getPrefix(),
					assignment.get().getDup(),
					assignment.get().getRead(),
					assignment.get().getSwap(),
					assignment.get().getDup(),
					assignment.get().getRead(),
					List(Push(Value(1))),
					List(Minus()),
					assignment.get().getWrite()
				)))
			))
		);
	}
	
	public Rule UnaryExpression() {
		Var<AssignmentInstructions> assignment = new Var<>();
		
		return FirstOf(
			Sequence(Terminal("delete"), UnaryExpression()),
			Sequence(Terminal("void"), UnaryExpression(), push(concat(pop(), List(Pop()), List(Push(NullValue()))))),
			Sequence(Terminal("++"), UnaryExpression(), assignment.set(new AssignmentInstructions(pop())), push(concat(
				assignment.get().getPrefix(),
				assignment.get().getDup(),
				assignment.get().getDup(),
				assignment.get().getRead(),
				List(Push(Value(1))),
				List(Add()),
				assignment.get().getWrite(),
				assignment.get().getRead()
			))),
			Sequence(Terminal("--"), UnaryExpression(), assignment.set(new AssignmentInstructions(pop())), push(concat(
				assignment.get().getPrefix(),
				assignment.get().getDup(),
				assignment.get().getDup(),
				assignment.get().getRead(),
				List(Push(Value(1))),
				List(Minus()),
				assignment.get().getWrite(),
				assignment.get().getRead()
			))),
			Sequence(Terminal("+"), UnaryExpression(), push(concat(pop(), List(UnaryPlus())))),
			Sequence(Terminal("~"), UnaryExpression(), push(concat(pop(), List(BitwiseNot())))),
			Sequence(Terminal("!"), UnaryExpression(), push(concat(pop(), List(Not())))),
			PostfixExpression(),
			Sequence(Terminal("-"), UnaryExpression(), push(concat(pop(), List(UnaryMinus()))))
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
		Var<AssignmentInstructions> assignment = new Var<>();
		
		return FirstOf(
			Sequence(
				LeftHandSideExpression(),
				Terminal("="),
				assignment.set(new AssignmentInstructions(pop())),
				AssignmentExpression(),
				push(concat(
					assignment.get().getPrefix(),
					assignment.get().getDup(),
					pop(),
					assignment.get().getWrite(),
					assignment.get().getRead()
				))
			),
			Sequence(
				LeftHandSideExpression(),
				CompoundAssignmentOperator(),
				assignment.set(new AssignmentInstructions(pop(1))),
				AssignmentExpression(),
				push(concat(
					assignment.get().getPrefix(),
					assignment.get().getDup(),
					assignment.get().getDup(),
					assignment.get().getRead(),
					pop(),
					pop(),
					assignment.get().getWrite(),
					assignment.get().getRead()
				))
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
			Optional(Initialiser(), push(concat(pop(), pop(), List(Store(Value(name.get()))))))
		);
	}
	
	public Rule Initialiser() {
		return Sequence(Terminal("="), AssignmentExpression());
	}
	
	public Rule EmptyStatement() {
		return Sequence(push(new ArrayList<Instruction>()), Terminal(";"));
	}
	
	public Rule ExpressionStatement() {
		return Sequence(TestNot(FirstOf("{", "function")), Expression(), push(concat(pop(), List(Pop()))), Terminal(";"));
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
					List(JumpIfFalse(Value(peek(0).size() + peek(1).size() + 2))),
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
					List(JumpIfFalse(Value(peek(0).size() + peek(1).size() + 2))),
					peek(0),
					peek(1),
					List(Pop()),
					List(Jump(Value(-pop().size() - pop().size() - pop().size() - 3)))
				))
			)
		);
	}
	
	public Rule ReturnStatement() {
		return Sequence(
			Terminal("return"), push(List(ReturnInstruction.Return())),
			Optional(Expression(), push(concat(List(Pop()), pop(), pop()))), Terminal(";")
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
			Sequence(Identifier(), push(concat(List(Local(Value(match().trim()))), List(Store(Value(match().trim())))))),
			Terminal("("),
			FormalParameterList(),
			Terminal(")"),
			Terminal("{"),
			FunctionBody(),
			Terminal("}"),
			push(concat(List(StartFunction()), List(Pop()), pop(1), List(Push(NullValue())), pop(), List(EndFunction()), pop()))
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
			push(concat(List(StartFunction()), List(Pop()), pop(1), List(Push(NullValue())), pop(), List(EndFunction())))
		);
	}
	
	public Rule FormalParameterList() {
		return Sequence(
			push(new ArrayList<Instruction>()),
			Optional(
				Sequence(Identifier(), push(concat(pop(), List(Local(Value(match().trim()))), List(Store(Value(match().trim())))))),
				Optional(
					Terminal(","), FormalParameterList(), push(concat(pop(), pop()))
				)
			)
		);
	}
	
	public Rule FunctionBody() {
		return Sequence(
			push(new ArrayList<Instruction>()),
			Optional(SourceElements(), push(concat(pop(), pop())))
		);
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
