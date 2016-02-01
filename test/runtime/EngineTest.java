package runtime;

import static instruction.AddInstruction.Add;
import static instruction.CallInstruction.Call;
import static instruction.DivideInstruction.Divide;
import static instruction.EndFunctionInstruction.EndFunction;
import static instruction.JumpIfTrueInstruction.JumpIfTrue;
import static instruction.JumpInstruction.Jump;
import static instruction.LoadInstruction.Load;
import static instruction.LocalInstruction.Local;
import static instruction.MinusInstruction.Minus;
import static instruction.MultiplyInstruction.Multiply;
import static instruction.PopInstruction.Pop;
import static instruction.PushInstruction.Push;
import static instruction.StartFunctionInstruction.StartFunction;
import static instruction.StoreInstruction.Store;
import static instruction.UnaryMinusInstruction.UnaryMinus;
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
			Load(Value("print")),
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
			Store(Value("x")),
			Push(Value(2)),
			Store(Value("y")),
			Load(Value("x")),
			Load(Value("y")),
			Add()
		));
	}
	
	@Test
	public void function() {
		assertStackValue("3", Arrays.asList(
			StartFunction(1),
			Pop(),
			Local(Value("x")),
			Store(Value("x")),
			Load(Value("x")),
			Push(Value(1)),
			Add(),
			EndFunction(),
			Store(Value("f")),
			Push(Value(2)),
			Push(Value(1)),
			Load(Value("f")),
			Call()
		));
	}
	
	@Test
	public void functionScope() {
		assertStackValue("5", Arrays.asList(
			Push(Value(5)),
			Store(Value("x")),
			StartFunction(0),
			Pop(),
			Push(Value(10)),
			Local(Value("x")),
			Store(Value("x")),
			Push(NullValue()),
			EndFunction(),
			Store(Value("f")),
			Push(Value(0)),
			Load(Value("f")),
			Call(),
			Pop(),
			Load(Value("x"))
		));
	}
	
	@Test
	public void jump() {
		assertOutput("6", Arrays.asList(
			Jump(Value(7)),
			Push(NullValue()),
			Push(Value(5)),
			Push(Value(2)),
			Load(Value("print")),
			Call(),
			Pop(),
			Push(NullValue()),
			Push(Value(6)),
			Push(Value(2)),
			Load(Value("print")),
			Call(),
			Pop()
		));
	}
	
	@Test
	public void jumpiftrue() {
		assertOutput("6", Arrays.asList(
			Push(Value(true)),
			JumpIfTrue(Value(7)),
			Push(NullValue()),
			Push(Value(5)),
			Push(Value(2)),
			Load(Value("print")),
			Call(),
			Pop(),
			Push(Value(false)),
			JumpIfTrue(Value(7)),
			Push(NullValue()),
			Push(Value(6)),
			Push(Value(2)),
			Load(Value("print")),
			Call(),
			Pop()
		));
	}
}
