package backend.value;

public class Identifier {
	private final String name;
	private final int lineNumber;
	private final int columnNumber;
	
	public Identifier(String name) {
		this(name, 0, 0);
	}
	
	public Identifier(String name, int lineNumber, int columnNumber) {
		this.name = name;
		this.lineNumber = lineNumber;
		this.columnNumber = columnNumber;
	}

	public String getName() {
		return name;
	}

	public int getLineNumber() {
		return lineNumber;
	}

	public int getColumnNumber() {
		return columnNumber;
	}
	
	public String toString() {
		return name + ":" + lineNumber + ":" + columnNumber;
	}
}