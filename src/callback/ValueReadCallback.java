package callback;

public interface ValueReadCallback {
	void onValueRead(HasCallbacks<ValueChangeCallback> hasValueChangeCallbacks);
}