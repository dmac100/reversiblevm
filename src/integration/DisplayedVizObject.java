package integration;

import java.util.HashMap;
import java.util.Map;

import backend.runtime.VizObject;
import backend.value.DoubleValue;
import backend.value.ImmutableValue;
import backend.value.NullValue;

public class DisplayedVizObject {
	private final String name;
	
	private long updateTime;
	private Map<String, ImmutableValue> initialValues = new HashMap<>();
	private Map<String, ImmutableValue> currentValues = new HashMap<>();
	private Map<String, ImmutableValue> targetValues = new HashMap<>();
	private boolean deletePending = false;
	private boolean deleted;
	
	public DisplayedVizObject(VizObject vizObject) {
		this.name = vizObject.getName();
		this.currentValues = new HashMap<>(vizObject.getValues());
	}

	public String getName() {
		return name;
	}
	
	public void update(VizObject newVizObject) {
		initialValues = new HashMap<>(currentValues);
		targetValues.putAll(newVizObject.getValues());
		updateTime = System.currentTimeMillis();
		deletePending = false;
		
		for(String property:currentValues.keySet()) {
			ImmutableValue value = targetValues.get(property);
			if(!(value instanceof DoubleValue)) {
				currentValues.put(property, value);
			}
		}
	}

	public void create() {
		initialValues = new HashMap<>(currentValues);
		targetValues = new HashMap<>(currentValues);
		updateTime = System.currentTimeMillis();
		deletePending = false;
		
		initialValues.put("opacity", new DoubleValue(0));
		if(!targetValues.containsKey("opacity")) {
			targetValues.put("opacity", new DoubleValue(1));
		}
	}

	public void delete() {
		initialValues = new HashMap<>(currentValues);
		targetValues = new HashMap<>(currentValues);
		updateTime = System.currentTimeMillis();
		deletePending = true;
		
		initialValues.put("opacity", currentValues.get("opacity"));
		targetValues.put("opacity", new DoubleValue(0));
	}
	
	public boolean isDeleted() {
		return deleted;
	}
	
	public void redraw() {
		double t = Math.min(1, (System.currentTimeMillis() - updateTime) / 200.0);
		
		if(t >= 1 && deletePending) {
			deleted = true;
			return;
		}
		
		for(String property:targetValues.keySet()) {
			ImmutableValue value1 = initialValues.get(property);
			ImmutableValue value2 = targetValues.get(property);
			
			if(value1 instanceof DoubleValue && value2 instanceof DoubleValue) {
				double a = ((DoubleValue)value1).getValue();
				double b = ((DoubleValue)value2).getValue();
				
				double c = a + (b - a) * t;
				
				currentValues.put(property, new DoubleValue(c));
			}
		}
	}
	
	public ImmutableValue getProperty(String name) {
		if(currentValues.containsKey(name)) {
			return currentValues.get(name);
		} else {
			return new NullValue();
		}
	}
	
	public void setProperty(String name, ImmutableValue value) {
		currentValues.put(name, value);
	}
	
	public String toString() {
		StringBuilder s = new StringBuilder();
		for(String key:currentValues.keySet()) {
			if(s.length() > 0) {
				s.append(", ");
			}
			s.append(key).append(": ").append(currentValues.get(key));
		}
		
		return name + "(" + s.toString() + ")";
	}
}
