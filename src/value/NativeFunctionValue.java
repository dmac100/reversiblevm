package value;

import java.util.List;

import runtime.Runtime;

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

	public void execute(Runtime runtime, List<Value> params) {
		if(name.equals("print")) {
			StringBuilder value = new StringBuilder();
			for(int i = 0; i < params.size(); i++) {
				if(i != 0) {
					value.append(" ");
				}
				value.append(params.get(i).toString());
			}
			runtime.print(value.toString());
		} else {
			throw new RuntimeException("Unknown native function: " + name);
		}
	}
}