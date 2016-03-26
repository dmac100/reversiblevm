package backend.runtime.library;

import java.util.List;

import backend.runtime.ExecutionException;
import backend.runtime.Runtime;
import backend.runtime.Stack;
import backend.value.DoubleValue;
import backend.value.NullValue;
import backend.value.Value;

public class MathStatic {
	public static Value abs(Runtime runtime, Stack stack, List<Value> params) throws ExecutionException {
		while(params.size() <= 1) params.add(new NullValue());
		DoubleValue value = runtime.checkDoubleValue(params.get(1));
		return new DoubleValue(Math.abs(value.getValue()));
	}
	
	public static Value acos(Runtime runtime, Stack stack, List<Value> params) throws ExecutionException {
		while(params.size() <= 1) params.add(new NullValue());
		DoubleValue value = runtime.checkDoubleValue(params.get(1));
		return new DoubleValue(Math.acos(value.getValue()));
	}
	
	public static Value acosh(Runtime runtime, Stack stack, List<Value> params) throws ExecutionException {
		while(params.size() <= 1) params.add(new NullValue());
		DoubleValue value = runtime.checkDoubleValue(params.get(1));
		return new DoubleValue(Math.log(value.getValue() + Math.sqrt(value.getValue() * value.getValue() - 1)));
	}
	
	public static Value asin(Runtime runtime, Stack stack, List<Value> params) throws ExecutionException {
		while(params.size() <= 1) params.add(new NullValue());
		DoubleValue value = runtime.checkDoubleValue(params.get(1));
		return new DoubleValue(Math.asin(value.getValue()));
	}
	
	public static Value asinh(Runtime runtime, Stack stack, List<Value> params) throws ExecutionException {
		while(params.size() <= 1) params.add(new NullValue());
		DoubleValue value = runtime.checkDoubleValue(params.get(1));
		if(value.getValue() == Double.NEGATIVE_INFINITY) {
			return new DoubleValue(value.getValue());
		} else {
			return new DoubleValue(Math.log(value.getValue() + Math.sqrt(value.getValue() * value.getValue() + 1)));
		}
	}
	
	public static Value atan(Runtime runtime, Stack stack, List<Value> params) throws ExecutionException {
		while(params.size() <= 1) params.add(new NullValue());
		DoubleValue value = runtime.checkDoubleValue(params.get(1));
		return new DoubleValue(Math.atan(value.getValue()));
	}
	
	public static Value atanh(Runtime runtime, Stack stack, List<Value> params) throws ExecutionException {
		while(params.size() <= 1) params.add(new NullValue());
		DoubleValue value = runtime.checkDoubleValue(params.get(1));
		return new DoubleValue(Math.log((1 + value.getValue()) / (1 - value.getValue())) / 2);
	}
	
	public static Value atan2(Runtime runtime, Stack stack, List<Value> params) throws ExecutionException {
		while(params.size() <= 2) params.add(new NullValue());
		DoubleValue value1 = runtime.checkDoubleValue(params.get(1));
		DoubleValue value2 = runtime.checkDoubleValue(params.get(2));
		return new DoubleValue(Math.atan2(value1.getValue(), value2.getValue()));
	}
	
	public static Value cbrt(Runtime runtime, Stack stack, List<Value> params) throws ExecutionException {
		while(params.size() <= 1) params.add(new NullValue());
		DoubleValue value = runtime.checkDoubleValue(params.get(1));
		return new DoubleValue(Math.cbrt(value.getValue()));
	}
	
	public static Value ceil(Runtime runtime, Stack stack, List<Value> params) throws ExecutionException {
		while(params.size() <= 1) params.add(new NullValue());
		DoubleValue value = runtime.checkDoubleValue(params.get(1));
		return new DoubleValue(Math.ceil(value.getValue()));
	}
	
	public static Value cos(Runtime runtime, Stack stack, List<Value> params) throws ExecutionException {
		while(params.size() <= 1) params.add(new NullValue());
		DoubleValue value = runtime.checkDoubleValue(params.get(1));
		return new DoubleValue(Math.cos(value.getValue()));
	}
	
	public static Value cosh(Runtime runtime, Stack stack, List<Value> params) throws ExecutionException {
		while(params.size() <= 1) params.add(new NullValue());
		DoubleValue value = runtime.checkDoubleValue(params.get(1));
		return new DoubleValue(Math.cosh(value.getValue()));
	}
	
	public static Value exp(Runtime runtime, Stack stack, List<Value> params) throws ExecutionException {
		while(params.size() <= 1) params.add(new NullValue());
		DoubleValue value = runtime.checkDoubleValue(params.get(1));
		return new DoubleValue(Math.exp(value.getValue()));
	}
	
	public static Value expm1(Runtime runtime, Stack stack, List<Value> params) throws ExecutionException {
		while(params.size() <= 1) params.add(new NullValue());
		DoubleValue value = runtime.checkDoubleValue(params.get(1));
		return new DoubleValue(Math.expm1(value.getValue()));
	}
	
	public static Value floor(Runtime runtime, Stack stack, List<Value> params) throws ExecutionException {
		while(params.size() <= 1) params.add(new NullValue());
		DoubleValue value = runtime.checkDoubleValue(params.get(1));
		return new DoubleValue(Math.floor(value.getValue()));
	}
	
	public static Value hypot(Runtime runtime, Stack stack, List<Value> params) throws ExecutionException {
		while(params.size() <= 1) params.add(new NullValue());
		double sum = 0;
		for(Value value:params.subList(1, params.size())) {
			double x = runtime.checkDoubleValue(value).getValue();
			sum += x * x;
		}
		return new DoubleValue(Math.sqrt(sum));
	}
	
	public static Value log(Runtime runtime, Stack stack, List<Value> params) throws ExecutionException {
		while(params.size() <= 1) params.add(new NullValue());
		DoubleValue value = runtime.checkDoubleValue(params.get(1));
		return new DoubleValue(Math.log(value.getValue()));
	}
	
	public static Value log1p(Runtime runtime, Stack stack, List<Value> params) throws ExecutionException {
		while(params.size() <= 1) params.add(new NullValue());
		DoubleValue value = runtime.checkDoubleValue(params.get(1));
		return new DoubleValue(Math.log1p(value.getValue()));
	}
	
	public static Value log10(Runtime runtime, Stack stack, List<Value> params) throws ExecutionException {
		while(params.size() <= 1) params.add(new NullValue());
		DoubleValue value = runtime.checkDoubleValue(params.get(1));
		return new DoubleValue(Math.log10(value.getValue()));
	}
	
	public static Value log2(Runtime runtime, Stack stack, List<Value> params) throws ExecutionException {
		while(params.size() <= 1) params.add(new NullValue());
		DoubleValue value = runtime.checkDoubleValue(params.get(1));
		return new DoubleValue(Math.log(value.getValue()) / Math.log(2));
	}
	
	public static Value max(Runtime runtime, Stack stack, List<Value> params) throws ExecutionException {
		while(params.size() <= 0) params.add(new NullValue());
		double max = Double.NEGATIVE_INFINITY;
		for(Value value:params.subList(1, params.size())) {
			max = Math.max(max, runtime.checkDoubleValue(value).getValue());
		}
		return new DoubleValue(max);
	}
	
	public static Value min(Runtime runtime, Stack stack, List<Value> params) throws ExecutionException {
		while(params.size() <= 0) params.add(new NullValue());
		double min = Double.POSITIVE_INFINITY;
		for(Value value:params.subList(1, params.size())) {
			min = Math.min(min, runtime.checkDoubleValue(value).getValue());
		}
		return new DoubleValue(min);
	}
	
	public static Value pow(Runtime runtime, Stack stack, List<Value> params) throws ExecutionException {
		while(params.size() <= 1) params.add(new NullValue());
		DoubleValue value1 = runtime.checkDoubleValue(params.get(1));
		DoubleValue value2 = runtime.checkDoubleValue(params.get(2));
		return new DoubleValue(Math.pow(value1.getValue(), value2.getValue()));
	}
	
	public static Value random(Runtime runtime, Stack stack, List<Value> params) throws ExecutionException {
		while(params.size() <= 1) params.add(new NullValue());
		return new DoubleValue(Math.random());
	}
	
	public static Value round(Runtime runtime, Stack stack, List<Value> params) throws ExecutionException {
		while(params.size() <= 1) params.add(new NullValue());
		DoubleValue value = runtime.checkDoubleValue(params.get(1));
		return new DoubleValue(Math.round(value.getValue()));
	}
	
	public static Value sign(Runtime runtime, Stack stack, List<Value> params) throws ExecutionException {
		while(params.size() <= 1) params.add(new NullValue());
		DoubleValue value = runtime.checkDoubleValue(params.get(1));
		return new DoubleValue(Math.signum(value.getValue()));
	}
	
	public static Value sin(Runtime runtime, Stack stack, List<Value> params) throws ExecutionException {
		while(params.size() <= 1) params.add(new NullValue());
		DoubleValue value = runtime.checkDoubleValue(params.get(1));
		return new DoubleValue(Math.sin(value.getValue()));
	}
	
	public static Value sinh(Runtime runtime, Stack stack, List<Value> params) throws ExecutionException {
		while(params.size() <= 1) params.add(new NullValue());
		DoubleValue value = runtime.checkDoubleValue(params.get(1));
		return new DoubleValue(Math.sinh(value.getValue()));
	}
	
	public static Value sqrt(Runtime runtime, Stack stack, List<Value> params) throws ExecutionException {
		while(params.size() <= 1) params.add(new NullValue());
		DoubleValue value = runtime.checkDoubleValue(params.get(1));
		return new DoubleValue(Math.sqrt(value.getValue()));
	}
	
	public static Value tan(Runtime runtime, Stack stack, List<Value> params) throws ExecutionException {
		while(params.size() <= 1) params.add(new NullValue());
		DoubleValue value = runtime.checkDoubleValue(params.get(1));
		return new DoubleValue(Math.tan(value.getValue()));
	}
	
	public static Value tanh(Runtime runtime, Stack stack, List<Value> params) throws ExecutionException {
		while(params.size() <= 1) params.add(new NullValue());
		DoubleValue value = runtime.checkDoubleValue(params.get(1));
		return new DoubleValue(Math.tanh(value.getValue()));
	}
	
	public static Value trunc(Runtime runtime, Stack stack, List<Value> params) throws ExecutionException {
		while(params.size() <= 1) params.add(new NullValue());
		DoubleValue value = runtime.checkDoubleValue(params.get(1));
		return new DoubleValue(value.getValue() < 0 ? Math.ceil(value.getValue()) : Math.floor(value.getValue()));
	}
}
