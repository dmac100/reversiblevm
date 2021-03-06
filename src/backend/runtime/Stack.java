package backend.runtime;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import backend.value.NullValue;
import backend.value.Value;

/**
 * The stack where values are pushed and popped as instructions are being evaluated.
 */
public class Stack implements HasState {
	private final UndoStack undoStack;
	private final List<Value> stack = new ArrayList<>();
	
	private Value lastPoppedValue = new NullValue();
	
	public Stack(UndoStack undoStack) {
		this.undoStack = undoStack;
	}

	public boolean isEmpty() {
		return stack.isEmpty();
	}
	
	public void push(final Value value, boolean addToUndoStack) {
		stack.add(value);
		if(addToUndoStack) {
			undoStack.addCommandUndo(new Runnable() {
				public void run() {
					stack.remove(stack.size() - 1);
				}
				
				public String toString() {
					return "[STACK PUSH]";
				}
			});
		}
	}
	
	public Value popValue(boolean addUndoCommand, boolean addToPopUndoStack) {
		final Value value = stack.remove(stack.size() - 1);
		
		if(addUndoCommand) {
			undoStack.addCommandUndo(new Runnable() {
				public void run() {
					stack.add(value);
				}
				
				public String toString() {
					return "[STACK POP]";
				}
			});
		}
		
		if(addToPopUndoStack) {
			undoStack.addPopValueUndo(value);
		}
		
		lastPoppedValue = value;
		
		return (Value) value;
	}
	
	public Value peekValue(int n) {
		return stack.get(stack.size() - 1 - n);
	}
	
	public Value getLastPoppedValue() {
		return lastPoppedValue;
	}
	
	public void resetLastPoppedValue() {
		lastPoppedValue = new NullValue();
	}
	
	public String toString() {
		return stack.toString();
	}

	public String getState(String prefix, Set<Object> used) {
		StringBuilder s = new StringBuilder();
		for(Value value:stack) {
			s.append(value.getState(prefix, used)).append("\n");
		}
		return s.toString();
	}
}