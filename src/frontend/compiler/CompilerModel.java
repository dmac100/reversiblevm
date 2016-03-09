package frontend.compiler;

import java.util.ArrayList;
import java.util.List;

public class CompilerModel {
	private List<String> output = new ArrayList<>();
	private List<String> errors = new ArrayList<>();
	private int lineNumber = -1;
	private boolean stepForwardEnabled = true;
	private boolean stepBackwardEnabled = true;
	private boolean runEnabled = true;
	private boolean stopEnabled = true;
	private boolean compileEnabled = true;
	
	public List<String> getOutput() {
		return output;
	}
	
	public void setOutput(List<String> output) {
		this.output = output;
	}
	
	public List<String> getErrors() {
		return errors;
	}
	
	public void setErrors(List<String> errors) {
		this.errors = errors;
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
	
	public boolean isRunEnabled() {
		return runEnabled;
	}
	
	public void setRunEnabled(boolean runEnabled) {
		this.runEnabled = runEnabled;
	}
	
	public boolean isStopEnabled() {
		return stopEnabled;
	}
	
	public void setStopEnabled(boolean stopEnabled) {
		this.stopEnabled = stopEnabled;
	}
	
	public boolean isCompileEnabled() {
		return compileEnabled;
	}
	
	public void setCompileEnabled(boolean compileEnabled) {
		this.compileEnabled = compileEnabled;
	}
}
