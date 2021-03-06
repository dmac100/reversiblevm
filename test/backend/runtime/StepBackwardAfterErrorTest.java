package backend.runtime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import backend.instruction.Instruction;

public class StepBackwardAfterErrorTest {
	@Test
	public void PostfixExpression() {
		assertStepBackward("x = null; x++;");
		assertStepBackward("x = null; x--;");
	}
	
	@Test
	public void UnaryExpression() {
		assertStepBackward("+null;");
		assertStepBackward("-null;");
		assertStepBackward("!null;");
		
		assertStepBackward("x = null; ++x;");
		assertStepBackward("x = null; --x;");
	}
	
	@Test
	public void MultiplicativeExpression() {
		assertStepBackward("null * 5;");
		assertStepBackward("5 * null;");
		assertStepBackward("null * null;");
		
		assertStepBackward("null / 5;");
		assertStepBackward("5 / null;");
		assertStepBackward("null / null;");
		
		assertStepBackward("null % 5;");
		assertStepBackward("5 % null;");
		assertStepBackward("null % null;");
	}
	
	@Test
	public void AdditiveExpression() {
		assertStepBackward("null + 5;");
		assertStepBackward("5 + null;");
		assertStepBackward("null + null;");
		
		assertStepBackward("null - 5;");
		assertStepBackward("5 - null;");
		assertStepBackward("null - null;");
	}
	
	@Test
	public void ShiftExpression() {
		assertStepBackward("null << 5;");
		assertStepBackward("5 << null;");
		assertStepBackward("null << null;");
		
		assertStepBackward("null >> 5;");
		assertStepBackward("5 >> null;");
		assertStepBackward("null >> null;");
		
		assertStepBackward("null >>> 5;");
		assertStepBackward("5 >>> null;");
		assertStepBackward("null >>> null;");
	}
	
	@Test
	public void RelationalExpression() {
		assertStepBackward("5 < null;");
		assertStepBackward("null < null;");
		
		assertStepBackward("5 > null;");
		assertStepBackward("null > null;");
		
		assertStepBackward("5 <= null;");
		assertStepBackward("null <= null;");
		
		assertStepBackward("5 >= null;");
		assertStepBackward("null >= null;");
		
		assertStepBackward("'a' >= 5;");
		assertStepBackward("5 >= 'a';");
		assertStepBackward("'a' > 5;");
		assertStepBackward("5 > 'a';");
		assertStepBackward("'a' <= 5;");
		assertStepBackward("5 <= 'a';");
		assertStepBackward("'a' < 5;");
		assertStepBackward("5 < 'a';");
	}
	
	@Test
	public void BitwiseANDExpression() {
		assertStepBackward("5 & null;");
		assertStepBackward("null & null;");
	}
	
	@Test
	public void BitwiseXORExpression() {
		assertStepBackward("5 ^ null;");
		assertStepBackward("null ^ null;");
	}
	
	@Test
	public void BitwiseORExpression() {
		assertStepBackward("5 | null;");
		assertStepBackward("null | null;");
	}
	
	@Test
	public void LogicalANDExpression() {
		assertStepBackward("true && null;");
		assertStepBackward("null && true;");
		assertStepBackward("null && null;");
	}
	
	@Test
	public void LogicalORExpression() {
		assertStepBackward("null || true;");
		assertStepBackward("null || null;");
	}
	
	@Test
	public void ConditionalExpression() {
		assertStepBackward("null ? 1 : 2;");
	}
	
	@Test
	public void CompoundAssignmentOperator() {
		assertStepBackward("x *= null;");
		assertStepBackward("x /= null;");
		assertStepBackward("x %= null;");
		assertStepBackward("x += null;");
		assertStepBackward("x -= null;");
		assertStepBackward("x <<= null;");
		assertStepBackward("x >>>= null;");
		assertStepBackward("x &= null;");
		assertStepBackward("x ^= null;");
		assertStepBackward("x |= null;");
	}
	
	@Test
	public void IfStatement() {
		assertStepBackward("if(null) { print(1); };");
		assertStepBackward("if(false) { print(1); } else if(null) { print(2); };");
	}
	
	@Test
	public void IterationStatement() {
		assertStepBackward("x = 0; do { print(x); x = x + 1; } while(null);");
		assertStepBackward("x = 0; while(null) { print(x); x = x + 1; };");
		assertStepBackward("for(x = 0; x < null; x = x + 1) { print(x); };");
		assertStepBackward("for(var x = 0; x < null; x = x + 1) { print(x); };");
	}
	
	@Test
	public void MemberExpression() {
		assertStepBackward("x = null; x[0];");
		assertStepBackward("x[null] = 5;");
		assertStepBackward("x = null; y = x.prop;");
		assertStepBackward("x = null; x.prop = 5;");
		assertStepBackward("x = null; x['prop'];");
		assertStepBackward("x = null; x['prop'] = 5;");
	}
	
	@Test
	public void CallExpression() {
		assertStepBackward("x = null; x();");
	}

	private static void assertStepBackward(String program) {
		Runtime runtime = new Runtime();
		List<Instruction> instructions = Engine.compile(program);
		
		Engine engine = new Engine(runtime, instructions);
		
		ArrayList<String> states = new ArrayList<>();
		
		boolean error = false;
		
		states.add(runtime.getState());
		while(!runtime.atEnd()) {
			try {
				engine.stepForward();
				states.add(runtime.getState());
			} catch(ExecutionException e) {
				error = true;
				break;
			}
		}
		
		assertTrue(error);
		
		for(int i = states.size() - 1; i >= 0; i--) {
			assertEquals(states.get(i), runtime.getState());
			engine.stepBackward();
		}
	}
}