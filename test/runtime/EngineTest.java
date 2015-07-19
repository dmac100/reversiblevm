package runtime;

import static instruction.AddInstruction.Add;
import static instruction.CallInstruction.Call;
import static instruction.DivideInstruction.Divide;
import static instruction.EndFunctionInstruction.EndFunction;
import static instruction.LoadInstruction.Load;
import static instruction.LocalInstruction.Local;
import static instruction.MinusInstruction.Minus;
import static instruction.MultiplyInstruction.Multiply;
import static instruction.PopInstruction.Pop;
import static instruction.PushInstruction.Push;
import static instruction.StartFunctionInstruction.StartFunction;
import static instruction.StoreInstruction.Store;
import static instruction.UnaryMinusInstruction.UnaryMinus;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static value.DoubleValue.Value;
import static value.NullValue.NullValue;
import static value.StringValue.Value;
import instruction.Instruction;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

public class EngineTest {
	@Test
	public void printHello() {
		assertOutput("Hello World!", Arrays.asList(
			Push(Value("Hello World!")),
			Push(Value(1)),
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
		assertError("TypeError: Not a double", Arrays.asList(
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
			StartFunction(),
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
			StartFunction(),
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
	
	private static void assertStackValue(String expected, List<Instruction> instructions) {
		Runtime runtime = new Runtime();
		
		new Engine().run(runtime, instructions);
		
		assertTrue(runtime.getOutput().isEmpty());
		assertTrue(runtime.getErrors().isEmpty());
		assertEquals(expected, runtime.getStack().popValue().toString());
		assertTrue(runtime.getStack().isEmpty());
	}
	
	private static void assertOutput(String expected, List<Instruction> instructions) {
		Runtime runtime = new Runtime();
		
		new Engine().run(runtime, instructions);
		
		assertEquals(Arrays.asList(expected), runtime.getOutput());
		assertTrue(runtime.getErrors().isEmpty());
		assertTrue(runtime.getStack().isEmpty());
	}
	
	private static void assertError(String expected, List<Instruction> instructions) {
		Runtime runtime = new Runtime();
		
		new Engine().run(runtime, instructions);
		
		assertEquals(expected, runtime.getErrors().get(0));
		assertTrue(runtime.getOutput().isEmpty());
		assertTrue(runtime.getStack().isEmpty());
	}
}
