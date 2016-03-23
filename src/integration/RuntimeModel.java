package integration;

import java.util.ArrayList;
import java.util.List;

import backend.runtime.OutputLine;
import backend.runtime.VizObject;

public class RuntimeModel {
	private List<OutputLine> output = new ArrayList<>();
	private List<VizObject> vizObjects = new ArrayList<>();
	private int lineNumber = -1;
	private boolean stepForwardEnabled = true;
	private boolean stepBackwardEnabled = true;
	private boolean runForwardEnabled = true;
	private boolean runBackwardEnabled = true;
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
	
	public boolean isStepForwardEnabled() {
		return stepForwardEnabled;
	}
	
	public void setStepForwardEnabled(boolean stepForwardEnabled) {
		this.stepForwardEnabled = stepForwardEnabled;
	}
	
	public boolean isStepBackwardEnabled() {
		return stepBackwardEnabled;
	}
	
	public void setStepBackwardEnabled(boolean stepBackwardEnabled) {
		this.stepBackwardEnabled = stepBackwardEnabled;
	}
	
	public boolean isRunForwardEnabled() {
		return runForwardEnabled;
	}
	
	public void setRunForwardEnabled(boolean runForwardEnabled) {
		this.runForwardEnabled = runForwardEnabled;
	}
	
	public boolean isRunBackwardEnabled() {
		return runBackwardEnabled;
	}
	
	public void setRunBackwardEnabled(boolean runBackwardEnabled) {
		this.runBackwardEnabled = runBackwardEnabled;
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
