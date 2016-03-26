package backend.instruction.viz;

import backend.instruction.Instruction;
import backend.runtime.Runtime;

public class EnableVizUpdatesInstruction extends Instruction {
	private boolean enabled;
	
	public EnableVizUpdatesInstruction(boolean enabled) {
		this.enabled = enabled;
	}
	
	public static Instruction EnableVizUpdatesInstruction(boolean enabled) {
		return new EnableVizUpdatesInstruction(enabled);
	}
	
	public Instruction copy() {
		return new EnableVizUpdatesInstruction(enabled);
	}
	
	public void execute(final Runtime runtime) {
		final boolean oldEnabled = runtime.getVizUpdatesEnabled();
		runtime.getUndoStack().addCommandUndo(new Runnable() {
			public void run() {
				runtime.setVizUpdatesEnabled(oldEnabled);
			}
			
			public String toString() {
				return "[ENABLED VIZ UPDATES]";
			}
		});
		
		if(!enabled && runtime.getVizUpdatesEnabled()) {
			runtime.refreshVizObjects();
		}
		runtime.setVizUpdatesEnabled(enabled);
	}
	
	public void undo(Runtime runtime) {
	}
	
	public String toString() {
		return super.toString() + "ENABLEVIZUPDATES: " + enabled;
	}
}