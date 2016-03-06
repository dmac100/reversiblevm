package backend.runtime;

import static backend.instruction.jump.JumpInstruction.Jump;
import static backend.instruction.jump.LabelInstruction.LabelInstruction;
import static backend.instruction.jump.LabeledJumpInstruction.LabeledJump;
import static backend.instruction.stack.PushInstruction.Push;
import static backend.value.DoubleValue.Value;
import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import backend.instruction.Instruction;
import backend.instruction.jump.LabelInstruction;
import backend.instruction.jump.LabeledJumpInstruction;

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
