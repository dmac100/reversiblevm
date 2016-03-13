package backend.value;

import java.util.ArrayList;

import java.util.List;
import java.util.Set;

import backend.observer.ValueObserverList;
import backend.observer.ValueReadObserver;
import backend.runtime.ExecutionException;
import backend.runtime.HasState;
import backend.runtime.UndoStack;

public class ArrayValue extends Value implements HasState {
	private final UndoStack undoStack;
	private List<Value> values = new ArrayList<>();
	private final ValueObserverList<String> valueObserverList = new ValueObserverList<>();
	
	public ArrayValue(UndoStack undoStack) {
		this.undoStack = undoStack;
	}
	
	public ArrayValue(List<Value> values, UndoStack undoStack) {
		this.values = values;
		this.undoStack = undoStack;
	}
	
	public Value get(DoubleValue indexValue, ValueReadObserver valueReadObserver) throws ExecutionException {
		valueObserverList.onReadValue(valueReadObserver, null);
		
		int index = (int)indexValue.getValue();
		if(index < 0) throw new ExecutionException("Invalid index: " + index);
		if(index >= values.size()) {
			return new NullValue();
		} else {
			return values.get(index);
		}
	}
	
	public void set(DoubleValue indexValue, Value value) throws ExecutionException {
		final int index = (int)indexValue.getValue();
		if(index < 0) throw new ExecutionException("Invalid index: " + index);
		if(index < values.size()) {
			final Value oldValue = values.get(index);
			undoStack.addCommandUndo(new Runnable() {
				public void run() {
					values.set(index, oldValue);
					valueObserverList.onChangeValue(null);
				}
				
				public String toString() {
					return "[ARRAY SET]";
				}
			});
		} else {
			final int oldSize = values.size();
			undoStack.addCommandUndo(new Runnable() {
				public void run() {
					while(values.size() > oldSize) {
						values.remove(values.size() - 1);
					}
					valueObserverList.onChangeValue(null);
				}
				
				public String toString() {
					return "[ARRAY SET]";
				}
			});
		}
		while(values.size() <= index) {
			values.add(new NullValue());
		}
		values.set(index, value);
		valueObserverList.onChangeValue(null);
	}
	
	public void push(Value value) {
		undoStack.addCommandUndo(new Runnable() {
			public void run() {
				values.remove(values.size() - 1);
				valueObserverList.onChangeValue(null);
			}
			
			public String toString() {
				return "[ARRAY PUSH]";
			}
		});
		values.add(value);
		valueObserverList.onChangeValue(null);
	}
	
	public Value pop() {
		if(values.isEmpty()) {
			return null;
		} else {
			final Value value = values.remove(values.size() - 1);
			undoStack.addCommandUndo(new Runnable() {
				public void run() {
					values.add(value);
					valueObserverList.onChangeValue(null);
				}
				
				public String toString() {
					return "[ARRAY POP]";
				}
			});
			valueObserverList.onChangeValue(null);
			return value;
		}
	}
	
	public DoubleValue length(ValueReadObserver valueReadObserver) {
		valueObserverList.onReadValue(valueReadObserver, null);
		return new DoubleValue(values.size());
	}
	
	public void setValues(List<Value> values) {
		final List<Value> oldValues = new ArrayList<>(this.values);
		undoStack.addCommandUndo(new Runnable() {
			public void run() {
				ArrayValue.this.values = oldValues;
				valueObserverList.onChangeValue(null);
			}
			
			public String toString() {
				return "[ARRAY SET VALUES]";
			}
		});
		this.values = values;
		valueObserverList.onChangeValue(null);
	}
	
	public List<Value> values(ValueReadObserver valueReadObserver) {
		valueObserverList.onReadValue(valueReadObserver, null);
		return new ArrayList<>(values);
	}

	public String toString(Set<Value> used) {
		if(used.contains(this)) return "[CYCLIC]";
		used.add(this);
		
		StringBuilder s = new StringBuilder();
		for(int i = 0; i < values.size(); i++) {
			if(i > 0) {
				s.append(", ");
			}
			s.append(values.get(i).toString(used));
		}
		
		used.remove(this);
		
		return "[" + s.toString() + "]";
	}
	
	public String getState(String prefix, Set<Object> used) {
		if(used.contains(this)) return prefix + "[CYCLIC]";
		used.add(this);
		
		StringBuilder s = new StringBuilder();
		s.append(prefix + "Array:").append("\n");
		for(int i = 0; i < values.size(); i++) {
			s.append(values.get(i).getState(prefix + "  ", used)).append("\n");
		}
		
		used.remove(this);
		
		return s.toString().replaceAll("\\s+$", "");
	}
}
