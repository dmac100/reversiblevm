package runtime;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import value.Value;

public class Stack implements HasState {
	private final UndoStack undoStack;
	private final List<Value> stack = new ArrayList<>();
	
	public Stack(UndoStack undoStack) {
		this.undoStack = undoStack;
	}

	public boolean isEmpty() {
		return stack.isEmpty();
	}
	
	public void push(Value value) {
		stack.add(value);
		undoStack.addCommandUndo(new Runnable() {
			public void run() {
				stack.remove(stack.size() - 1);
			}
		});
	}
	
	public Value popValue() {
		final Value value = stack.remove(stack.size() - 1);
		undoStack.addCommandUndo(new Runnable() {
			public void run() {
				stack.add(value);
			}
		});
		return (Value) value;
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