package backend.runtime.library;

import java.util.ArrayList;
import java.util.List;

import backend.runtime.ExecutionException;
import backend.runtime.Runtime;
import backend.runtime.Stack;
import backend.value.ArrayValue;
import backend.value.BooleanValue;
import backend.value.DoubleValue;
import backend.value.NullValue;
import backend.value.StringValue;
import backend.value.Value;

public class StringProto {
	public static Value length(Runtime runtime, Stack stack, List<Value> params) throws ExecutionException {
		while(params.size() <= 0) params.add(new NullValue());
		String string = runtime.checkStringValue(params.get(0)).getValue();
		return new DoubleValue(string.length());
	}
	
	public static Value charAt(Runtime runtime, Stack stack, List<Value> params) throws ExecutionException {
		while(params.size() <= 1) params.add(new NullValue());
		String string = runtime.checkStringValue(params.get(0)).getValue();
		int index = (int)runtime.checkDoubleValue(params.get(1)).getValue();
		return new StringValue(String.valueOf(string.charAt(index)));
	}
	
	public static Value concat(Runtime runtime, Stack stack, List<Value> params) throws ExecutionException {
		while(params.size() <= 1) params.add(new NullValue());
		String string = runtime.checkStringValue(params.get(0)).getValue();
		String other = runtime.checkStringValue(params.get(1)).getValue();
		return new StringValue(string + other);
	}
	
	public static Value endsWith(Runtime runtime, Stack stack, List<Value> params) throws ExecutionException {
		while(params.size() <= 1) params.add(new NullValue());
		String string = runtime.checkStringValue(params.get(0)).getValue();
		String other = runtime.checkStringValue(params.get(1)).getValue();
		return new BooleanValue(string.endsWith(other));
	}
	
	public static Value indexOf(Runtime runtime, Stack stack, List<Value> params) throws ExecutionException {
		while(params.size() <= 1) params.add(new NullValue());
		String string = runtime.checkStringValue(params.get(0)).getValue();
		String other = runtime.checkStringValue(params.get(1)).getValue();
		return new DoubleValue(string.indexOf(other));
	}
	
	public static Value lastIndexOf(Runtime runtime, Stack stack, List<Value> params) throws ExecutionException {
		while(params.size() <= 1) params.add(new NullValue());
		String string = runtime.checkStringValue(params.get(0)).getValue();
		String other = runtime.checkStringValue(params.get(1)).getValue();
		return new DoubleValue(string.lastIndexOf(other));
	}
	
	public static Value repeat(Runtime runtime, Stack stack, List<Value> params) throws ExecutionException {
		while(params.size() <= 1) params.add(new NullValue());
		String string = runtime.checkStringValue(params.get(0)).getValue();
		int count = (int)runtime.checkDoubleValue(params.get(1)).getValue();
		StringBuilder s = new StringBuilder();
		for(int i = 0; i < count; i++) {
			s.append(string);
		}
		return new StringValue(s.toString());
	}
	
	public static Value substring(Runtime runtime, Stack stack, List<Value> params) throws ExecutionException {
		while(params.size() <= 2) params.add(new NullValue());
		String string = runtime.checkStringValue(params.get(0)).getValue();
		int start = (int)runtime.checkDoubleValue(params.get(1)).getValue();
		int end = (int)runtime.checkDoubleValue(params.get(2)).getValue();
		start = Math.max(start, 0);
		start = Math.min(start, string.length());
		end = Math.max(end, 0);
		end = Math.min(end, string.length());
		return new StringValue(string.substring(start, end));
	}
	
	public static Value split(Runtime runtime, Stack stack, List<Value> params) throws ExecutionException {
		while(params.size() <= 1) params.add(new NullValue());
		String string = runtime.checkStringValue(params.get(0)).getValue();
		String separator = runtime.checkStringValue(params.get(1)).getValue();
		List<Value> list = new ArrayList<>();
		for(String value:string.split(separator)) {
			list.add(new StringValue(value));
		}
		if(string.endsWith(separator)) {
			list.add(new StringValue(""));
		}
		return new ArrayValue(list, runtime.getUndoStack());
	}
	
	public static Value startsWith(Runtime runtime, Stack stack, List<Value> params) throws ExecutionException {
		while(params.size() <= 1) params.add(new NullValue());
		String string = runtime.checkStringValue(params.get(0)).getValue();
		String other = runtime.checkStringValue(params.get(1)).getValue();
		return new BooleanValue(string.startsWith(other));
	}
	
	public static Value toLowerCase(Runtime runtime, Stack stack, List<Value> params) throws ExecutionException {
		while(params.size() <= 0) params.add(new NullValue());
		String string = runtime.checkStringValue(params.get(0)).getValue();
		return new StringValue(string.toLowerCase());
	}
	
	public static Value toUpperCase(Runtime runtime, Stack stack, List<Value> params) throws ExecutionException {
		while(params.size() <= 0) params.add(new NullValue());
		String string = runtime.checkStringValue(params.get(0)).getValue();
		return new StringValue(string.toUpperCase());
	}
	
	public static Value trim(Runtime runtime, Stack stack, List<Value> params) throws ExecutionException {
		while(params.size() <= 0) params.add(new NullValue());
		String string = runtime.checkStringValue(params.get(0)).getValue();
		return new StringValue(string.trim());
	}
}
