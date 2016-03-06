package callback;

public interface CanFireValueRead {
	public void fireCallbacks(HasCallbacks<ValueChangeCallback> hasValueChangeCallback);
}
