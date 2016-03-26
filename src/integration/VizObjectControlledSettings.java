package integration;

import static backend.util.VizObjectUtil.getDoubleOrDefault;

import java.util.List;

import backend.runtime.VizObject;

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
	
	public int getInstructionDelay() {
		return instructionDelay;
	}

	public int getTransitionDelay() {
		return transitionDelay;
	}
}
