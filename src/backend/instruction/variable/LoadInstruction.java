package backend.instruction.variable;

import com.google.common.base.Supplier;

import backend.instruction.Instruction;
import backend.observer.ValueChangeObservable;
import backend.observer.ValueReadObserver;
import backend.runtime.Runtime;
import backend.runtime.Scope;
import backend.runtime.Stack;
import backend.value.Identifier;
import backend.value.Value;

public class LoadInstruction extends Instruction {
	private final Identifier identifier;

	public LoadInstruction(Identifier identifier) {
		this.identifier = identifier;
	}
	
	public Instruction copy() {
		return new LoadInstruction(identifier);
	}
	
	public String getName() {
		return identifier.getName();
	}
	
	public Identifier getIdentifier() {
		return identifier;
	}
	
	public static Instruction Load(Identifier identifier) {
		return new LoadInstruction(identifier);
	}
	
	public void execute(final Runtime runtime) {
		final Stack stack = runtime.getStack();
		final Scope scope = runtime.getScope();
		stack.push(scope.get(identifier.getName(), runtime), false);
		
		runtime.getCurrentStackFrame().setIdentifierValue(identifier, new Supplier<Value>() {
			public Value get() {
				return scope.get(identifier.getName(), new ValueReadObserver() {
					public void onValueRead(ValueChangeObservable valueChangeObservable) {
					}
				});
			}
		});
	}
	
	public void undo(Runtime runtime) {
		runtime.getStack().popValue(false, false);
	}
	
	public String toString() {
		return super.toString() + "LOAD: " + identifier.getName();
	}
}