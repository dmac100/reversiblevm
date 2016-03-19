package frontend.event;

import integration.RuntimeModel;

public class CompilerModelChangedEvent {
	private final RuntimeModel compilerModel;

	public CompilerModelChangedEvent(RuntimeModel compilerModel) {
		this.compilerModel = compilerModel;
	}
	
	public RuntimeModel getCompilerModel() {
		return compilerModel;
	}
}