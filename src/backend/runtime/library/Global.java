package backend.runtime.library;

import java.util.ArrayList;
import java.util.List;

import backend.runtime.Runtime;
import backend.runtime.Stack;
import backend.value.ArrayValue;
import backend.value.DoubleValue;
import backend.value.NullValue;
import backend.value.Value;

public class Global {
	public static Value print(Runtime runtime, Stack stack, List<Value> params) {
		while(params.size() <= 0) params.add(new NullValue());
		StringBuilder value = new StringBuilder();
		for(int i = 1; i < params.size(); i++) {
			if(i != 1) {
				value.append(" ");
			}
			value.append(params.get(i).toString());
		}
		runtime.print(value.toString());
		return new NullValue();
	}

	public static Value range(Runtime runtime, Stack stack, List<Value> params) {
		while(params.size() <= 1) params.add(new NullValue());
		int max = (int) runtime.checkDoubleValue(params.get(1)).getValue();
		List<Value> list = new ArrayList<>();
		for(int i = 0; i < max; i++) {
			list.add(new DoubleValue(i));
		}
		return new ArrayValue(list, runtime.getUndoStack());
	}
	
	public static Value newArray(Runtime runtime, Stack stack, List<Value> params) {
		while(params.size() <= 0) params.add(new NullValue());
		for(int i = 1; i < params.size(); i++) {
			runtime.checkDoubleValue(params.get(i));
		}
		if(params.size() == 1) {
			return new ArrayValue(runtime.getUndoStack());
		}
		return newArray(runtime, params.subList(1, params.size()));
	}
	
	private static Value newArray(Runtime runtime, List<Value> params) {
		if(params.isEmpty()) {
			return new NullValue();
		}
		ArrayValue array = new ArrayValue(runtime.getUndoStack());
		int size = (int) runtime.checkDoubleValue(params.get(0)).getValue();
		List<Value> values = new ArrayList<>();
		for(int i = 0; i < size; i++) {
			values.add(newArray(runtime, params.subList(1, params.size())));
		}
		array.setValues(values);
		return array;
	}
	
	public static Value parseInt(Runtime runtime, Stack stack, List<Value> params) {
		while(params.size() <= 1) params.add(new NullValue());
		try {
			double radix = 10;
			if(params.size() > 2) {
				radix = runtime.checkDoubleValue(params.get(2)).getValue();
			}
			int value = Integer.parseInt(params.get(1).toString(), (int) radix);
			return new DoubleValue(value);
		} catch(NumberFormatException e) {
			return new DoubleValue(Double.NaN);
		}
	}
	
	public static Value parseDouble(Runtime runtime, Stack stack, List<Value> params) {
		while(params.size() <= 1) params.add(new NullValue());
		try {
			double value = Double.parseDouble(params.get(1).toString());
			if(Double.isInfinite(value)) value = Double.NaN;
			return new DoubleValue(value);
		} catch(NumberFormatException e) {
			return new DoubleValue(Double.NaN);
		}
	}
}
