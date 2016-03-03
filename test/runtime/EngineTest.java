package runtime;

import static instruction.function.CallInstruction.Call;
import static instruction.function.EndFunctionInstruction.EndFunction;
import static instruction.function.StartFunctionInstruction.StartFunction;
import static instruction.jump.JumpIfTrueInstruction.JumpIfTrue;
import static instruction.jump.JumpInstruction.Jump;
import static instruction.operator.AddInstruction.Add;
import static instruction.operator.DivideInstruction.Divide;
import static instruction.operator.MinusInstruction.Minus;
import static instruction.operator.MultiplyInstruction.Multiply;
import static instruction.operator.UnaryMinusInstruction.UnaryMinus;
import static instruction.stack.PopInstruction.Pop;
import static instruction.stack.PushInstruction.Push;
import static instruction.variable.LoadInstruction.Load;
import static instruction.variable.LocalInstruction.Local;
import static instruction.variable.StoreInstruction.Store;
import static runtime.EngineAsserts.assertError;
import static runtime.EngineAsserts.assertOutput;
import static runtime.EngineAsserts.assertStackValue;
import static value.BooleanValue.Value;
import static value.DoubleValue.Value;
import static value.NullValue.NullValue;
import static value.StringValue.Value;

import java.util.Arrays;

import org.junit.Test;

public class EngineTest {
	@Test
	public void printHello() {
		assertOutput("Hello World!", Arrays.asList(
			Push(NullValue()),
			Push(Value("Hello World!")),
			Push(Value(2)),
			Load("print"),
			Call(),
			Pop()
		));
	}
	
	@Test
	public void add() {
		assertStackValue("3", Arrays.asList(
			Push(Value(1)),
			Push(Value(2)),
			Add()
		));
	}
	
	@Test
	public void subtract() {
		assertStackValue("2", Arrays.asList(
			Push(Value(5)),
			Push(Value(3)),
			Minus()
		));
	}
	
	@Test
	public void multiply() {
		assertStackValue("10", Arrays.asList(
			Push(Value(5)),
			Push(Value(2)),
			Multiply()
		));
	}
	
	@Test
	public void divide() {
		assertStackValue("2.5", Arrays.asList(
			Push(Value(5)),
			Push(Value(2)),
			Divide()
		));
	}
	
	@Test
	public void unaryMinus() {
		assertStackValue("3", Arrays.asList(
			Push(Value(-3)),
			UnaryMinus()
		));
	}
	
	@Test
	public void typeError() {
		assertError("TypeError: Not a double: 3", Arrays.asList(
			Push(Value("3")),
			UnaryMinus()
		));
	}
	
	@Test
	public void variables() {
		assertStackValue("3", Arrays.asList(
			Push(Value(1)),
			Store("x"),
			Push(Value(2)),
			Store("y"),
			Load("x"),
			Load("y"),
			Add()
		));
	}
	
	@Test
	public void function() {
		assertStackValue("3", Arrays.asList(
			StartFunction(1),
			Pop(),
			Local("x"),
			Store("x"),
			Load("x"),
			Push(Value(1)),
			Add(),
			EndFunction(),
			Store("f"),
			Push(Value(2)),
			Push(Value(1)),
			Load("f"),
			Call()
		));
	}
	
	@Test
	public void functionScope() {
		assertStackValue("5", Arrays.asList(
			Push(Value(5)),
			Store("x"),
			StartFunction(0),
			Pop(),
			Push(Value(10)),
			Local("x"),
			Store("x"),
			Push(NullValue()),
			EndFunction(),
			Store("f"),
			Push(Value(0)),
			Load("f"),
			Call(),
			Pop(),
			Load("x")
		));
	}
	
	@Test
	public void jump() {
		assertOutput("6", Arrays.asList(
			Jump(7),
			Push(NullValue()),
			Push(Value(5)),
			Push(Value(2)),
			Load("print"),
			Call(),
			Pop(),
			Push(NullValue()),
			Push(Value(6)),
			Push(Value(2)),
			Load("print"),
			Call(),
			Pop()
		));
	}
	
	@Test
	public void jumpiftrue() {
		assertOutput("6", Arrays.asList(
			Push(Value(true)),
			JumpIfTrue(7),
			Push(NullValue()),
			Push(Value(5)),
			Push(Value(2)),
			Load("print"),
			Call(),
			Pop(),
			Push(Value(false)),
			JumpIfTrue(7),
			Push(NullValue()),
			Push(Value(6)),
			Push(Value(2)),
			Load("print"),
			Call(),
			Pop()
		));
	}
}
