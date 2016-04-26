package integration;

public class Bounds {
	private float minX;
	private float minY;
	private float maxX;
	private float maxY;
	
	public Bounds(double minX, double minY, double maxX, double maxY) {
		this((float) minX, (float) minY, (float) maxX, (float) maxY);
	}
	
	public Bounds(float minX, float minY, float maxX, float maxY) {
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

	public float getMinX() {
		return minX;
	}
	
	public float getMinY() {
		return minY;
	}
	
	public float getMaxX() {
		return maxX;
	}
	
	public float getMaxY() {
		return maxY;
	}
	
	public void extendBounds(double minX, double minY, double maxX, double maxY) {
		extendBounds((float) minX, (float) minY, (float) maxX, (float) maxY);
	}
	
	public void extendBounds(float minX, float minY, float maxX, float maxY) {
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
