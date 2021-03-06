package backend.instruction.object;

import com.google.common.base.Supplier;

import backend.instruction.Instruction;
import backend.observer.ValueChangeObservable;
import backend.observer.ValueReadObserver;
import backend.runtime.ExecutionException;
import backend.runtime.Runtime;
import backend.runtime.Stack;
import backend.value.HasPropertiesObject;
import backend.value.Identifier;
import backend.value.ObjectValue;
import backend.value.Value;

public class SetPropertyInstruction extends Instruction {
	private final Identifier identifier;
	
	public SetPropertyInstruction(Identifier identifier) {
		this.identifier = identifier;
	}
	
	public static Instruction SetProperty(Identifier identifier) {
		return new SetPropertyInstruction(identifier);
	}
	
	public Identifier getIdentifier() {
		return identifier;
	}
	
	public Instruction copy() {
		return new SetPropertyInstruction(identifier);
	}
	
	/**
	 * Changes stack from [value, object] to [] and sets object.name to value.
	 */
	public void execute(final Runtime runtime) throws ExecutionException {
		Stack stack = runtime.getStack();
		if(!(stack.peekValue(1) instanceof HasPropertiesObject)) {
			throw new ExecutionException("TypeError: Not an object: " + stack.peekValue(1));
		}
		Value value = runtime.getStack().popValue(false, true);
		final ObjectValue object = ((HasPropertiesObject)stack.popValue(false, true)).getPropertiesObject();
		object.set(identifier.getName(), value);
		
		runtime.getCurrentStackFrame().setIdentifierValue(identifier, new Supplier<String>() {
			public String get() {
				Value value = object.get(identifier.getName(), new ValueReadObserver() {
					public void onValueRead(ValueChangeObservable valueChangeObservable) {
					}
				});
				return value.inspect();
			}
		});
	}
	
	public void undo(Runtime runtime) {
		runtime.getUndoStack().undoPopValue(runtime);
		runtime.getUndoStack().undoPopValue(runtime);
	}
	
	public String toString() {
		return super.toString() + "SETPROPERTY: " + identifier.getName();
	}
}