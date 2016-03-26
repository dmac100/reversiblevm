package backend.runtime.library;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import backend.instruction.operator.EqualInstruction;
import backend.runtime.ExecutionException;
import backend.runtime.Runtime;
import backend.runtime.Stack;
import backend.value.ArrayValue;
import backend.value.DoubleValue;
import backend.value.NullValue;
import backend.value.StringValue;
import backend.value.Value;

public class ArrayProto {
	public static Value length(Runtime runtime, Stack stack, List<Value> params) throws ExecutionException {
		while(params.size() <= 0) params.add(new NullValue());
		ArrayValue array = runtime.checkArrayValue(params.get(0));
		return array.length(runtime);
	}

	public static Value concat(Runtime runtime, Stack stack, List<Value> params) throws ExecutionException {
		while(params.size() <= 0) params.add(new NullValue());
		List<Value> list = new ArrayList<>();
		for(Value value:params) {
			list.addAll(runtime.checkArrayValue(value).values(runtime));
		}
		return new ArrayValue(list, runtime.getUndoStack());
	}

	public static Value indexOf(Runtime runtime, Stack stack, List<Value> params) throws ExecutionException {
		while(params.size() <= 0) params.add(new NullValue());
		ArrayValue array = runtime.checkArrayValue(params.get(0));
		Value value = params.get(1);
		List<Value> values = array.values(runtime);
		for(int i = 0; i < values.size(); i++) {
			if(EqualInstruction.equals(values.get(i), value)) {
				return new DoubleValue(i);
			}
		}
		return new DoubleValue(-1);
	}

	public static Value join(Runtime runtime, Stack stack, List<Value> params) throws ExecutionException {
		while(params.size() <= 1) params.add(new NullValue());
		List<Value> array = runtime.checkArrayValue(params.get(0)).values(runtime);
		String separator = runtime.checkStringValue(params.get(1)).getValue();
		StringBuilder s = new StringBuilder();
		for(int i = 0; i < array.size(); i++) {
			if(i > 0) {
				s.append(separator);
			}
			s.append(array.get(i));
		}
		return new StringValue(s.toString());
	}

	public static Value lastIndexOf(Runtime runtime, Stack stack, List<Value> params) throws ExecutionException {
		while(params.size() <= 0) params.add(new NullValue());
		ArrayValue array = runtime.checkArrayValue(params.get(0));
		Value value = params.get(1);
		List<Value> values = array.values(runtime);
		for(int i = values.size() - 1; i >= 0; i--) {
			if(EqualInstruction.equals(values.get(i), value)) {
				return new DoubleValue(i);
			}
		}
		return new DoubleValue(-1);
	}

	public static Value slice(Runtime runtime, Stack stack, List<Value> params) throws ExecutionException {
		while(params.size() <= 2) params.add(new NullValue());
		List<Value> array = runtime.checkArrayValue(params.get(0)).values(runtime);
		int start = (int)runtime.checkDoubleValue(params.get(1)).getValue();
		int end = (int)runtime.checkDoubleValue(params.get(2)).getValue();
		start = Math.max(start, 0);
		start = Math.min(start, array.size());
		end = Math.max(end, 0);
		end = Math.min(end, array.size());
		List<Value> list = new ArrayList<>();
		for(int i = start; i < end; i++) {
			list.add(array.get(i));
		}
		return new ArrayValue(list, runtime.getUndoStack());
	}

	public static Value pop(Runtime runtime, Stack stack, List<Value> params) throws ExecutionException {
		while(params.size() <= 0) params.add(new NullValue());
		ArrayValue array = runtime.checkArrayValue(params.get(0));
		List<Value> values = array.values(runtime);
		if(values.isEmpty()) {
			return new NullValue();
		} else {
			Value value = values.remove(values.size() - 1);
			array.setValues(values);
			return value;
		}
	}

	public static Value push(Runtime runtime, Stack stack, List<Value> params) throws ExecutionException {
		while(params.size() <= 0) params.add(new NullValue());
		ArrayValue array = runtime.checkArrayValue(params.get(0));
		Value value = params.get(1);
		List<Value> values = array.values(runtime);
		values.add(value);
		array.setValues(values);
		return value;
	}

	public static Value reverse(Runtime runtime, Stack stack, List<Value> params) throws ExecutionException {
		while(params.size() <= 0) params.add(new NullValue());
		ArrayValue array = runtime.checkArrayValue(params.get(0));
		List<Value> values = array.values(runtime);
		Collections.reverse(values);
		array.setValues(values);
		return array;
	}

	public static Value shift(Runtime runtime, Stack stack, List<Value> params) throws ExecutionException {
		while(params.size() <= 0) params.add(new NullValue());
		ArrayValue array = runtime.checkArrayValue(params.get(0));
		List<Value> values = array.values(runtime);
		if(values.isEmpty()) {
			return new NullValue();
		} else {
			Value value = values.remove(0);
			array.setValues(values);
			return value;
		}
	}

	public static Value unshift(Runtime runtime, Stack stack, List<Value> params) throws ExecutionException {
		while(params.size() <= 1) params.add(new NullValue());
		ArrayValue array = runtime.checkArrayValue(params.get(0));
		Value value = params.get(1);
		List<Value> values = array.values(runtime);
		values.add(0, value);
		array.setValues(values);
		return value;
	}

	public static Value splice(Runtime runtime, Stack stack, List<Value> params) throws ExecutionException {
		while(params.size() <= 2) params.add(new NullValue());
		ArrayValue array = runtime.checkArrayValue(params.get(0));
		int index = (int)runtime.checkDoubleValue(params.get(1)).getValue();
		int count = (int)runtime.checkDoubleValue(params.get(2)).getValue();
		List<Value> removed = new ArrayList<>();
		List<Value> values = array.values(runtime);
		for(int i = 0; i < count; i++) {
			if(index < values.size()) {
				removed.add(values.remove(index));
			}
		}
		array.setValues(values);
		return new ArrayValue(removed, runtime.getUndoStack());
	}

	public static Value keys(Runtime runtime, Stack stack, List<Value> params) throws ExecutionException {
		while(params.size() <= 0) params.add(new NullValue());
		ArrayValue array = runtime.checkArrayValue(params.get(0));
		List<Value> list = new ArrayList<>();
		for(int i = 0; i < array.length(runtime).getValue(); i++) {
			list.add(new DoubleValue(i));
		}
		return new ArrayValue(list, runtime.getUndoStack());
	}

	public static Value values(Runtime runtime, Stack stack, List<Value> params) throws ExecutionException {
		while(params.size() <= 0) params.add(new NullValue());
		ArrayValue array = runtime.checkArrayValue(params.get(0));
		List<Value> list = new ArrayList<>();
		for(int i = 0; i < array.length(runtime).getValue(); i++) {
			list.add(array.get(new DoubleValue(i), runtime));
		}
		return new ArrayValue(list, runtime.getUndoStack());
	}
}
