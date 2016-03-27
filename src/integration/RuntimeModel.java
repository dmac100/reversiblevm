package integration;

import java.util.ArrayList;
import java.util.List;

import backend.runtime.OutputLine;
import backend.runtime.VizObject;

public class RuntimeModel {
	private List<OutputLine> output = new ArrayList<>();
	private List<VizObject> vizObjects = new ArrayList<>();
	private int lineNumber = -1;
	private boolean forwardEnabled = true;
	private boolean backwardEnabled = true;
	private boolean pauseEnabled = true;
	private boolean compileEnabled = true;
	
	public List<OutputLine> getOutput() {
		return output;
	}
	
	public void setOutput(List<OutputLine> output) {
		this.output = output;
	}
	
	public List<VizObject> getVizObjects() {
		return vizObjects;
	}

	public void setVizObjects(List<VizObject> vizObjects) {
		this.vizObjects = vizObjects;
	}

	public int getLineNumber() {
		return lineNumber;
	}
	
	public void setLineNumber(int lineNumber) {
		this.lineNumber = lineNumber;
	}

	public boolean isForwardEnabled() {
		return forwardEnabled;
	}

	public void setForwardEnabled(boolean forwardEnabled) {
		this.forwardEnabled = forwardEnabled;
	}

	public boolean isBackwardEnabled() {
		return backwardEnabled;
	}

	public void setBackwardEnabled(boolean backwardEnabled) {
		this.backwardEnabled = backwardEnabled;
	}

	public boolean isPauseEnabled() {
		return pauseEnabled;
	}

	public void setPauseEnabled(boolean pauseEnabled) {
		this.pauseEnabled = pauseEnabled;
	}

	public boolean isCompileEnabled() {
		return compileEnabled;
	}

	public void setCompileEnabled(boolean compileEnabled) {
		this.compileEnabled = compileEnabled;
	}
}