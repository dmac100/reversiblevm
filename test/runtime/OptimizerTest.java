package runtime;

import static instruction.function.CallInstruction.Call;
import static instruction.stack.PopInstruction.Pop;
import static instruction.stack.PushInstruction.Push;
import static instruction.stack.SwapInstruction.Swap;
import static instruction.variable.LoadInstruction.Load;
import static org.junit.Assert.*;
import static value.DoubleValue.Value;
import static value.NullValue.NullValue;

import instruction.Instruction;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

public class OptimizerTest {
	@Test
	public void test() {
		List<Instruction> instructions = new Optimizer().optimize(Arrays.asList(
			Push(NullValue()),
			Load("f"),
			Push(Value(3)),
			Swap(),
			Push(Value(4)),
			Swap(),
			Push(Value(3)),
			Swap(),
			Call(),
			Pop()
		));
		
		assertEquals(Arrays.asList(
			"PUSH: null",
			"PUSH: 3",
			"PUSH: 4",
			"PUSH: 3",
			"LOAD: f",
			"CALL",
			"POP"
		).toString(), instructions.toString());
	}
}
