package backend.runtime;

import static backend.instruction.function.CallInstruction.Call;
import static backend.instruction.function.EndFunctionInstruction.EndFunction;
import static backend.instruction.function.StartFunctionInstruction.StartFunction;
import static backend.instruction.jump.JumpIfTrueInstruction.JumpIfTrue;
import static backend.instruction.jump.JumpInstruction.Jump;
import static backend.instruction.operator.AddInstruction.Add;
import static backend.instruction.operator.DivideInstruction.Divide;
import static backend.instruction.operator.MinusInstruction.Minus;
import static backend.instruction.operator.MultiplyInstruction.Multiply;
import static backend.instruction.operator.UnaryMinusInstruction.UnaryMinus;
import static backend.instruction.stack.PopInstruction.Pop;
import static backend.instruction.stack.PushInstruction.Push;
import static backend.instruction.variable.LoadInstruction.Load;
import static backend.instruction.variable.LocalInstruction.Local;
import static backend.instruction.variable.StoreInstruction.Store;
import static backend.runtime.EngineAsserts.assertError;
import static backend.runtime.EngineAsserts.assertOutput;
import static backend.runtime.EngineAsserts.assertStackValue;
import static backend.value.BooleanValue.Value;
import static backend.value.DoubleValue.Value;
import static backend.value.NullValue.NullValue;
import static backend.value.StringValue.Value;

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
		assertError("TypeError: Not a double: 3 (at line 0)", Arrays.asList(
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
