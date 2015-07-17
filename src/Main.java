import static instruction.CallInstruction.Call;
import static instruction.LoadInstruction.Load;
import static instruction.PushInstruction.Push;
import static value.StringValue.Value;

import java.util.Arrays;

import runtime.Engine;
import value.IntValue;

public class Main {
	public static void main(String[] args) {
		new Engine().run(Arrays.asList(
			Load(Value("print")),
			Push(Value("Hello World!")),
			Push(IntValue.Value(1)),
			Call()
		));
	}
}
