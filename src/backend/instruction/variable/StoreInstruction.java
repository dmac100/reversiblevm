package backend.instruction.variable;

import backend.instruction.Instruction;
import backend.observer.ValueChangeObservable;
import backend.observer.ValueReadObserver;
import backend.runtime.Runtime;
import backend.runtime.Scope;
import backend.runtime.Stack;
import backend.value.Identifier;
import backend.value.Value;

import com.google.common.base.Supplier;

public class StoreInstruction extends Instruction {
	private final Identifier identifier;

	public StoreInstruction(Identifier identifier) {
		this.identifier = identifier;
	}
	
	public Instruction copy() {
		return new StoreInstruction(identifier);
	}
	
	public String getName() {
		return identifier.getName();
	}
	
	public Identifier getIdentifier() {
		return identifier;
	}
	
	public static Instruction Store(Identifier identifier) {
		return new StoreInstruction(identifier);
	}
	
	public void execute(final Runtime runtime) {
		final Stack stack = runtime.getStack();
		final Scope scope = runtime.getScope();
		scope.set(identifier.getName(), stack.popValue(false, true));
		
		runtime.getCurrentStackFrame().setIdentifierValue(identifier, new Supplier<String>() {
			public String get() {
				Value value = scope.get(identifier.getName(), new ValueReadObserver() {
					public void onValueRead(ValueChangeObservable valueChangeObservable) {
					}
				});
				return value.inspect();
			}
		});
	}
	
	public void undo(Runtime runtime) {
		runtime.getUndoStack().undoPopValue(runtime);
	}
	
	public String toString() {
		return super.toString() + "STORE: " + identifier.getName();
	}
}