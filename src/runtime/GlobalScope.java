package runtime;

import value.NativeFunctionValue;
import value.NullValue;
import value.Value;

public class GlobalScope implements Scope {
	public Value get(String name) {
		if(name.equals("print")) {
			return new NativeFunctionValue("print");
		} else {
			return new NullValue();
		}
	}
}