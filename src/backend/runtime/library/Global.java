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
}
