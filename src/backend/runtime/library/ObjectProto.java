package backend.runtime.library;

import java.util.ArrayList;
import java.util.List;

import backend.runtime.ExecutionException;
import backend.runtime.Runtime;
import backend.runtime.Stack;
import backend.value.ArrayValue;
import backend.value.NullValue;
import backend.value.ObjectValue;
import backend.value.StringValue;
import backend.value.Value;

public class ObjectProto {
	public static Value keys(Runtime runtime, Stack stack, List<Value> params) throws ExecutionException {
		while(params.size() <= 0) params.add(new NullValue());
		List<String> keys = runtime.checkObjectValue(params.get(0)).keys(runtime);
		List<Value> list = new ArrayList<>();
		for(String key:keys) {
			if(!key.equals("prototype")) {
				list.add(new StringValue(key));
			}
		}
		return new ArrayValue(list, runtime.getUndoStack());
	}
	
	public static Value values(Runtime runtime, Stack stack, List<Value> params) throws ExecutionException {
		while(params.size() <= 0) params.add(new NullValue());
		ObjectValue object = runtime.checkObjectValue(params.get(0));
		List<String> keys = object.keys(runtime);
		List<Value> list = new ArrayList<>();
		for(String key:keys) {
			if(!key.equals("prototype")) {
				list.add(object.get(key, runtime));
			}
		}
		return new ArrayValue(list, runtime.getUndoStack());
	}
}
