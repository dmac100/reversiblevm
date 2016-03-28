package frontend.event;

import integration.RuntimeModel;

public class RuntimeModelChangedEvent {
	private final RuntimeModel runtimeModel;

	public RuntimeModelChangedEvent(RuntimeModel runtimeModel) {
		this.runtimeModel = runtimeModel;
	}
	
	public RuntimeModel getRuntimeModel() {
		return runtimeModel;
	}
}