package runtime;

import java.util.ArrayList;
import java.util.List;

public class Runtime {
	private Scope scope = new GlobalScope();
	private Stack stack = new Stack();
	
	private List<String> errors = new ArrayList<>();
	private List<String> output = new ArrayList<>();
	
	public Scope getScope() {
		return scope;
	}
	
	public void throwError(String error) {
		System.err.println("Error: " + error);
		errors.add(error);
	}
	
	public void print(String value) {
		System.out.println(value);
		output.add(value);
	}

	public Stack getStack() {
		return stack;
	}

	public List<String> getOutput() {
		return output;
	}
	
	public List<String> getErrors() {
		return errors;
	}
}