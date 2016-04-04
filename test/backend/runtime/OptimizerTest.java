package backend.runtime;

import static backend.instruction.function.CallInstruction.Call;
import static backend.instruction.stack.PopInstruction.Pop;
import static backend.instruction.stack.PushInstruction.Push;
import static backend.instruction.stack.SwapInstruction.Swap;
import static backend.instruction.variable.LoadInstruction.Load;
import static backend.value.DoubleValue.Value;
import static backend.value.NullValue.NullValue;
import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import backend.instruction.Instruction;
import backend.value.Identifier;

public class OptimizerTest {
	@Test
	public void test() {
		List<Instruction> instructions = new Optimizer().optimize(Arrays.asList(
			Push(NullValue()),
			Load(new Identifier("f")),
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
