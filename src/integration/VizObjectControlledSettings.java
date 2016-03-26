package integration;

import java.util.List;

import backend.runtime.VizObject;
import backend.value.DoubleValue;
import backend.value.ImmutableValue;

public class VizObjectControlledSettings {
	private int instructionDelay = 1;
	private int transitionDelay = 400;
	
	/**
	 * Parses vizObjects and saves any relevant settings from their values.
	 */
	public VizObjectControlledSettings(List<VizObject> vizObjects) {
		for(VizObject vizObject:vizObjects) {
			if(vizObject.getName().equals("delay")) {
				transitionDelay = (int) getDoubleOrDefault(vizObject, "transition", transitionDelay);
				instructionDelay = (int) getDoubleOrDefault(vizObject, "instruction", instructionDelay);
			}
		}
	}
	
	private double getDoubleOrDefault(VizObject vizObject, String name, double defaultValue) {
		ImmutableValue value = vizObject.getProperty(name);
		if(value instanceof DoubleValue) {
			return ((DoubleValue)value).getValue();
		} else {
			return defaultValue;
		}
	}

	public int getInstructionDelay() {
		return instructionDelay;
	}

	public int getTransitionDelay() {
		return transitionDelay;
	}
}
