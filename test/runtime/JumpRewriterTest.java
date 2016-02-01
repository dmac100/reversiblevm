package runtime;

import static instruction.JumpInstruction.Jump;
import static instruction.LabelInstruction.LabelInstruction;
import static instruction.LabeledJumpInstruction.LabeledJump;
import static instruction.PushInstruction.Push;
import static org.junit.Assert.assertEquals;
import static value.DoubleValue.Value;
import instruction.Instruction;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

public class JumpRewriterTest {
	@Test
	public void convertToLabelledJumps() {
		List<Instruction> converted = JumpRewriter.convertToLabelledJumps(Arrays.asList(
			Jump(2),
			Push(Value(1)),
			Push(Value(2)),
			Push(Value(3)),
			Jump(-2)
		));
		
		assertEquals(Arrays.asList(
			"LABELEDJUMP: 1",
			"PUSH: 1",
			"LABEL: 1",
			"LABEL: 2",
			"PUSH: 2",
			"PUSH: 3",
			"LABELEDJUMP: 2"
		).toString(), converted.toString());
	}
	
	@Test
	public void convertToOffsetJumps() {
		List<Instruction> converted = JumpRewriter.convertToOffsetJumps(Arrays.asList(
			LabeledJump("1"),
			Push(Value(1)),
			LabelInstruction("1"),
			LabelInstruction("2"),
			Push(Value(2)),
			Push(Value(3)),
			LabeledJump("2")
		));
		
		assertEquals(Arrays.asList(
			"JUMP: 2",
			"PUSH: 1",
			"PUSH: 2",
			"PUSH: 3",
			"JUMP: -2"
		).toString(), converted.toString());
	}
}
