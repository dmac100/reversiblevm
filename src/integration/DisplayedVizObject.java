package integration;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import backend.runtime.HasImmutableValueProperties;
import backend.runtime.VizObject;
import backend.util.VizObjectUtil;
import backend.value.DoubleValue;
import backend.value.ImmutableValue;
import backend.value.NullValue;
import backend.value.StringValue;

public class DisplayedVizObject implements HasImmutableValueProperties {
	private final String name;
	
	private long updateTime;
	private Map<String, ImmutableValue> initialValues = new LinkedHashMap<>();
	private Map<String, ImmutableValue> currentValues = new LinkedHashMap<>();
	private Map<String, ImmutableValue> targetValues = new LinkedHashMap<>();
	private boolean deletePending = false;
	private boolean updatePending = false;
	private boolean deleted;
	
	public DisplayedVizObject(VizObject vizObject) {
		this.name = vizObject.getName();
		this.currentValues = new LinkedHashMap<>(vizObject.getValues());
		
		setDefaultValues(currentValues);
	}
	
	private static void setDefaultValues(Map<String, ImmutableValue> values) {
		setDefaultColorValues(values, "fill", "red");
		setDefaultColorValues(values, "stroke", "darkgrey");
	}
	
	private static void setDefaultColorValues(Map<String, ImmutableValue> values, String prefix, String defaultValue) {
		ImmutableValue color = values.get(prefix);
		if(color instanceof StringValue) {
			setComponentColorValues(values, prefix, ((StringValue)color).getValue());
		} else {
			setComponentColorValues(values, prefix, defaultValue);
		}
	}

	/**
	 * Splits the color value into separate red, green, and blue properties with the given prefix.
	 */
	private static void setComponentColorValues(Map<String, ImmutableValue> values, String prefix, String value) {
		int[] rgb = new int[] { 200, 100, 100 };
		if(value.equals("red")) rgb = new int[] { 200, 100, 100 };
		if(value.equals("green")) rgb = new int[] { 100, 200, 100 };
		if(value.equals("blue")) rgb = new int[] { 100, 100, 200 };
		if(value.equals("yellow")) rgb = new int[] { 200, 200, 100 };
		if(value.equals("magenta")) rgb = new int[] { 200, 100, 200 };
		if(value.equals("cyan")) rgb = new int[] { 100, 200, 200 };
		if(value.equals("white")) rgb = new int[] { 255, 255, 255 };
		if(value.equals("lightgrey")) rgb = new int[] { 200, 200, 200 };
		if(value.equals("grey")) rgb = new int[] { 150, 150, 150 };
		if(value.equals("darkgrey")) rgb = new int[] { 50, 50, 50 };
		if(value.equals("black")) rgb = new int[] { 0, 0, 0 };
		if(value.matches("#[0-9a-fA-F]{3}")) {
			rgb = new int[] {
				Integer.parseInt(value.substring(1, 2) + value.substring(1, 2), 16),
				Integer.parseInt(value.substring(2, 3) + value.substring(2, 3), 16),
				Integer.parseInt(value.substring(3, 4) + value.substring(3, 4), 16),
			};
			System.out.println(value.substring(1, 2) + value.substring(1, 2));
			System.out.println(value.substring(2, 3) + value.substring(2, 3));
			System.out.println(value.substring(3, 4) + value.substring(3, 4));
			System.out.println(Arrays.toString(rgb));
		} else if(value.toLowerCase().matches("#[0-9a-fA-F]{6}")) {
			rgb = new int[] {
				Integer.parseInt(value.substring(1, 2) + value.substring(2, 3), 16),
				Integer.parseInt(value.substring(3, 4) + value.substring(4, 5), 16),
				Integer.parseInt(value.substring(5, 6) + value.substring(6, 7), 16),
			};
		}
		values.put(prefix + "-red", new DoubleValue(rgb[0]));
		values.put(prefix + "-green", new DoubleValue(rgb[1]));
		values.put(prefix + "-blue", new DoubleValue(rgb[2]));
	}

	public String getName() {
		return name;
	}
	
	public void update(VizObject newVizObject) {
		// Check that newVizObjects contains changes from the current target values.
		if(VizObjectUtil.hasSubValues(targetValues, newVizObject.getValues())) {
			return;
		}
		
		initialValues = new HashMap<>(currentValues);
		targetValues.putAll(newVizObject.getValues());
		updateTime = System.currentTimeMillis();
		
		// Undo delete if it's in progress.
		if(deletePending) {
			deleted = false;
			targetValues.put("opacity", new DoubleValue(1));
		}
		
		deletePending = false;
		updatePending = true;
		
		setDefaultValues(targetValues);
		
		// Add non-numeric values to current values immediately.
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
		updatePending = true;
		
		initialValues.put("opacity", new DoubleValue(0));
		targetValues.put("opacity", new DoubleValue(1));
	}

	public void delete() {
		if(deletePending) {
			return;
		}
		
		initialValues = new HashMap<>(currentValues);
		targetValues = new HashMap<>(currentValues);
		updateTime = System.currentTimeMillis();
		deletePending = true;
		updatePending = true;
		
		initialValues.put("opacity", currentValues.get("opacity"));
		targetValues.put("opacity", new DoubleValue(0));
	}
	
	public boolean isDeleted() {
		return deleted;
	}
	
	public boolean isUpdatePending() {
		return updatePending;
	}
	
	public void redraw(int transitionDelay) {
		double t = Math.min(1, (System.currentTimeMillis() - updateTime) / (double) transitionDelay);
		
		if(t >= 1) {
			if(deletePending) {
				deleted = true;
				return;
			}
			
			updatePending = false;
			currentValues = new HashMap<>(targetValues);
			return;
		}
		
		for(String property:targetValues.keySet()) {
			ImmutableValue value1 = initialValues.get(property);
			ImmutableValue value2 = targetValues.get(property);
			
			if(value1 instanceof DoubleValue && value2 instanceof DoubleValue) {
				double a = ((DoubleValue)value1).getValue();
				double b = ((DoubleValue)value2).getValue();
				
				double c = new CubicBezier(0.8, 0.2, 0.2, 0.8).getBezierYValue(t) * (b - a) + a;
				
				currentValues.put(property, new DoubleValue(c));
			}
		}
	}
	
	public boolean hasProperty(String name) {
		return currentValues.containsKey(name);
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
