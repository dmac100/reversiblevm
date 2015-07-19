package value;

import java.util.ArrayList;
import java.util.List;

import runtime.Runtime;
import runtime.Stack;

public class NativeFunctionValue implements Value {
	private String name;
	
	public NativeFunctionValue(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public String toString() {
		return "[Native: " + name + "]";
	}

	public void execute(Runtime runtime) {
		Stack stack = runtime.getStack();
		List<Value> params = getParams(stack);
		
		if(name.equals("print")) {
			StringBuilder value = new StringBuilder();
			for(int i = 0; i < params.size(); i++) {
				if(i != 0) {
					value.append(" ");
				}
				value.append(params.get(i).toString());
			}
			runtime.print(value.toString());
			
			stack.push(new NullValue());
		} else {
			throw new RuntimeException("Unknown native function: " + name);
		}
	}
	
	private static List<Value> getParams(Stack stack) {
		List<Value> params = new ArrayList<>();
		int numParams = (int) stack.popDoubleValue().getValue();
		for(int x = 0; x < numParams; x++) {
			params.add(stack.popValue());
		}
		return params;
	}
}