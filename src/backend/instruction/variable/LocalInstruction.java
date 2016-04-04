package backend.instruction.variable;

import com.google.common.base.Supplier;

import backend.instruction.Instruction;
import backend.observer.ValueChangeObservable;
import backend.observer.ValueReadObserver;
import backend.runtime.Runtime;
import backend.runtime.Scope;
import backend.value.Identifier;
import backend.value.Value;

public class LocalInstruction extends Instruction {
	private final Identifier identifier;

	public LocalInstruction(Identifier identifier) {
		this.identifier = identifier;
	}
	
	public Instruction copy() {
		return new LocalInstruction(identifier);
	}
	
	public static Instruction Local(Identifier identifier) {
		return new LocalInstruction(identifier);
	}
	
	public void execute(final Runtime runtime) {
		final Scope scope = runtime.getScope();
		scope.create(identifier.getName());
		
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
	}
	
	public String toString() {
		return super.toString() + "LOCAL: " + identifier.getName();
	}
}