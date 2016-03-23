package backend.runtime;

public class OutputLine {
	public enum OutputType {
		STANDARD, ERROR, INFO
	}
	
	private final String text;
	private final OutputType type;
	
	public OutputLine(String text, OutputType type) {
		this.text = text;
		this.type = type;
	}
	
	public String getText() {
		return text;
	}
	
	public OutputType getType() {
		return type;
	}
	
	public String toString() {
		return text;
	}
}
