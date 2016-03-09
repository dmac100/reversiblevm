package frontend.event;

import frontend.compiler.CompilerModel;

public class CompilerModelChangedEvent {
	private final CompilerModel compilerModel;

	public CompilerModelChangedEvent(CompilerModel compilerModel) {
		this.compilerModel = compilerModel;
	}
	
	public CompilerModel getCompilerModel() {
		return compilerModel;
	}
}