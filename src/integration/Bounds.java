package integration;

public class Bounds {
	private int minX;
	private int minY;
	private int maxX;
	private int maxY;
	
	public Bounds(int minX, int minY, int maxX, int maxY) {
		this.minX = minX;
		this.minY = minY;
		this.maxX = maxX;
		this.maxY = maxY;
	}

	public Bounds(Bounds other) {
		this.minX = other.minX;
		this.minY = other.minY;
		this.maxX = other.maxX;
		this.maxY = other.maxY;
	}

	public int getMinX() {
		return minX;
	}
	
	public int getMinY() {
		return minY;
	}
	
	public int getMaxX() {
		return maxX;
	}
	
	public int getMaxY() {
		return maxY;
	}
	
	public void extendBounds(int minX, int minY, int maxX, int maxY) {
		this.minX = Math.min(this.minX, minX);
		this.minY = Math.min(this.minY, minY);
		this.maxX = Math.max(this.maxX, maxX);
		this.maxY = Math.max(this.maxY, maxY);
	}
	
	public boolean equals(Object other) {
		if(other == null || other.getClass() != Bounds.class) return false;
		Bounds otherBounds = ((Bounds)other);
		if(minX != otherBounds.minX) return false;
		if(minY != otherBounds.minY) return false;
		if(maxX != otherBounds.maxX) return false;
		if(maxY != otherBounds.maxY) return false;
		return true;
	}
	
	public String toString() {
		return String.format("[%d, %d, %d, %d]", minX, minY, maxX, maxY);
	}
}
