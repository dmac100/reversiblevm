package integration;

import static backend.util.VizObjectUtil.getDoubleOrDefault;
import static backend.util.VizObjectUtil.getStringOrDefault;

import java.util.List;

import backend.runtime.VizObject;

public class VizObjectControlledSettings {
	private int instructionDelay = 0;
	private int transitionDelay = 400;
	private String boundsFit = "extend";
	
	/**
	 * Parses vizObjects and saves any relevant settings from their values.
	 */
	public VizObjectControlledSettings(List<VizObject> vizObjects) {
		for(VizObject vizObject:vizObjects) {
			if(vizObject.getName().equals("delay")) {
				transitionDelay = (int) getDoubleOrDefault(vizObject, "transition", transitionDelay);
				instructionDelay = (int) getDoubleOrDefault(vizObject, "instruction", instructionDelay);
			} else if(vizObject.getName().equals("bounds")) {
				boundsFit = getStringOrDefault(vizObject, "fit", boundsFit);
			}
		}
	}
	
	public int getInstructionDelay() {
		return instructionDelay;
	}

	public int getTransitionDelay() {
		return transitionDelay;
	}

	public String getBoundsFit() {
		return boundsFit;
	}
}
