package integration;

public class CubicBezier {
	private final double cx1;
	private final double cy1;
	private final double cx2;
	private final double cy2;
	
	/**
	 * Creates a Bezier curve from (0, 0) to (1, 1) through control points (cx1, cy1) and (cx2, cy2).
	 */
	public CubicBezier(double cx1, double cy1, double cx2, double cy2) {
		this.cx1 = cx1;
		this.cy1 = cy1;
		this.cx2 = cx2;
		this.cy2 = cy2;
	}

	/**
	 * Returns the x or y value on the curve for a t value between 0 and 1 through
	 * control points c1 and c2.
	 */
	private double getBezierValue(double t, double c1, double c2) {
		return 3*(1-t)*(1-t)*t*c1 + 3*(1-t)*t*t*c2 + t*t*t;
	}

	/**
	 * Returns the corresponding y value of the curve for an x value.
	 */
	public double getBezierYValue(double x) {
		// Find value of t that gives an x value close to x using binary search.
		double minT = 0;
		double maxT = 1;
		double midT = 0;
		for(double i = 0; i < 20; i++) {
			midT = (minT + maxT) / 2;
			double foundX = getBezierValue(midT, cx1, cx2);
			if(foundX > x) {
				maxT = midT;
			} else {
				minT = midT;
			}
		}
		// Return y value using the found value of t.
		return getBezierValue(midT, cy1, cy2);
	}
}
