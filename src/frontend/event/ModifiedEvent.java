package frontend.event;

public class ModifiedEvent {
	private boolean modified;

	public ModifiedEvent(boolean modified) {
		this.modified = modified;
	}
	
	public boolean getModified() {
		return modified;
	}
}
