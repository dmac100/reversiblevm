package backend.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import backend.runtime.HasImmutableValueProperties;
import backend.runtime.VizObject;
import backend.value.BooleanValue;
import backend.value.DoubleValue;
import backend.value.ImmutableValue;
import backend.value.StringValue;

public class VizObjectUtil {
	/**
	 * Returns whether vizObjects1 and vizObjects2 have the same filters and values.
	 */
	public static boolean equalFiltersAndValues(List<VizObject> vizObjects1, List<VizObject> vizObjects2) {
		if(vizObjects1.size() != vizObjects2.size()) return false;
		for(int i = 0; i < vizObjects1.size(); i++) {
			VizObject vizObject1 = vizObjects1.get(i);
			VizObject vizObject2 = vizObjects2.get(i);
			if(!equalValues(vizObject1.getValues(), vizObject2.getValues())) {
				return false;
			}
			if(!equalValues(vizObject1.getFilters(), vizObject2.getFilters())) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Returns whether values1 and values2 have equal values.
	 */
	public static boolean equalValues(Map<String, ImmutableValue> values1, Map<String, ImmutableValue> values2) {
		if(!values1.keySet().equals(values2.keySet())) return false;
		for(String key:values1.keySet()) {
			if(!values1.get(key).getKey().equals(values2.get(key).getKey())) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Returns whether all the values in subValues are in superValues.
	 */
	public static boolean hasSubValues(Map<String, ImmutableValue> superValues, Map<String, ImmutableValue> subValues) {
		for(String key:subValues.keySet()) {
			if(!superValues.containsKey(key)) {
				return false;
			}
			if(!superValues.get(key).getKey().equals(subValues.get(key).getKey())) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Returns the vizObjects that are filters.
	 */
	public static List<VizObject> getVizObjectFilters(List<VizObject> vizObjects) {
		List<VizObject> vizObjectFilters = new ArrayList<>();
		for(VizObject vizObject:vizObjects) {
			if(vizObject.isFilterEnabled()) {
				vizObjectFilters.add(vizObject);
			}
		}
		return vizObjectFilters;
	}
	
	/**
	 * Returns value clamped between min and max.
	 */
	public static int ensureInRange(int value, int min, int max) {
		if(value < min) return min;
		if(value > max) return max;
		return value;
	}

	/**
	 * Returns the double value for the named property or the defaultValue.
	 */
	public static double getDoubleOrDefault(HasImmutableValueProperties vizObject, String name, double defaultValue) {
		ImmutableValue value = vizObject.getProperty(name);
		if(value instanceof DoubleValue) {
			return ((DoubleValue)value).getValue();
		} else {
			return defaultValue;
		}
	}
	
	/**
	 * Returns the String value for the named property or the defaultValue.
	 */
	public static String getStringOrDefault(HasImmutableValueProperties vizObject, String name, String defaultValue) {
		ImmutableValue value = vizObject.getProperty(name);
		if(value instanceof StringValue) {
			return ((StringValue)value).getValue();
		} else {
			return defaultValue;
		}
	}
	
	/**
	 * Returns the boolean value for the named property or the defaultValue.
	 */
	public static boolean getBooleanOrDefault(HasImmutableValueProperties vizObject, String name, boolean defaultValue) {
		ImmutableValue value = vizObject.getProperty(name);
		if(value instanceof BooleanValue) {
			return ((BooleanValue)value).getValue();
		} else {
			return defaultValue;
		}
	}
}
