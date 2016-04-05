package backend.instruction.object;

import com.google.common.base.Supplier;

import backend.instruction.Instruction;
import backend.observer.ValueChangeObservable;
import backend.observer.ValueReadObserver;
import backend.runtime.ExecutionException;
import backend.runtime.Runtime;
import backend.runtime.Stack;
import backend.value.ArrayValue;
import backend.value.FunctionValue;
import backend.value.HasPropertiesObject;
import backend.value.Identifier;
import backend.value.NativeFunctionValue;
import backend.value.ObjectValue;
import backend.value.StringValue;
import backend.value.Value;

public class GetPropertyInstruction extends Instruction {
	private final Identifier identifier;
	
	public GetPropertyInstruction(Identifier identifier) {
		this.identifier = identifier;
	}
	
	public static Instruction GetProperty(Identifier identifier) {
		return new GetPropertyInstruction(identifier);
	}
	
	public Instruction copy() {
		return new GetPropertyInstruction(identifier);
	}
	
	public String getName() {
		return identifier.getName();
	}
	
	public Identifier getIdentifier() {
		return identifier;
	}
	
	/**
	 * Changes stack from [object] to [object.name].
	 */
	public void execute(final Runtime runtime) throws ExecutionException {
		final Stack stack = runtime.getStack();
		
		final Value value = stack.peekValue(0);
		final ObjectValue objectValue;
		if(value instanceof ObjectValue) {
			objectValue = getObjectValue(runtime, stack, null);
		} else if(value instanceof ArrayValue) {
			objectValue = getObjectValue(runtime, stack, "ArrayProto");
		} else if(value instanceof StringValue) {
			objectValue = getObjectValue(runtime, stack, "StringProto");
		} else if(value instanceof FunctionValue || value instanceof NativeFunctionValue) {
			objectValue = getObjectValue(runtime, stack, "FunctionProto");
		} else {
			throw new ExecutionException("TypeError: Not an object: " + value);
		}
		
		runtime.getStack().push(objectValue.get(identifier.getName(), runtime), false);
		
		runtime.getCurrentStackFrame().setIdentifierValue(identifier, new Supplier<String>() {
			public String get() {
				Value value = objectValue.get(identifier.getName(), new ValueReadObserver() {
					public void onValueRead(ValueChangeObservable valueChangeObservable) {
					}
				});
				return value.inspect();
			}
		});
	}
	
	/**
	 * Returns an object value from the top of the stack, and sets its prototype to the
	 * value of prototypeName.
	 */
	private static ObjectValue getObjectValue(Runtime runtime, Stack stack, String prototypeName) {
		Value value = stack.popValue(false, true);
		if(!(value instanceof HasPropertiesObject)) {
			throw new ExecutionException("TypeError: Not an object: " + value);
		}
		ObjectValue objectValue = ((HasPropertiesObject)value).getPropertiesObject();
		if(prototypeName != null) {
			ObjectValue prototype = runtime.checkObjectValue(runtime.getScope().get(prototypeName, runtime));
			objectValue.set("prototype", prototype);
		}
		return objectValue;
	}

	public void undo(Runtime runtime) {
		runtime.getStack().popValue(false, false);
		runtime.getUndoStack().undoPopValue(runtime);
	}
	
	public String toString() {
		return super.toString() + "GETPROPERTY: " + identifier.getName();
	}
}