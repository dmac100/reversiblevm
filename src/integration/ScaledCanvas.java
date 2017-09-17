package integration;

import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Path;
import org.eclipse.swt.graphics.TextLayout;
import org.eclipse.swt.graphics.Transform;

public class ScaledCanvas {
	private final GC gc;
	private final Bounds bounds;
	private final float[] transformElements;

	public ScaledCanvas(GC gc, Transform transform, Bounds bounds) {
		float[] elements = new float[6];
		transform.getElements(elements);
		
		this.gc = gc;
		this.bounds = bounds;
		this.transformElements = elements;
	}
	
	public void drawOval(double cx, double cy, double width, double height) {
		gc.drawOval(tx(cx - width / 2), ty(cy - height / 2), tx(cx + width / 2) - tx(cx - width / 2), ty(cy + height / 2) - ty(cy - height / 2));
		bounds.extendBounds(cx - width / 2, cy - height / 2, cx + width / 2, cy + height / 2);
	}

	public void fillOval(double cx, double cy, double width, double height) {
		gc.fillOval(tx(cx - width / 2), ty(cy - height / 2), tx(cx + width / 2) - tx(cx - width / 2), ty(cy + height / 2) - ty(cy - height / 2));
		bounds.extendBounds(cx - width / 2, cy - height / 2, cx + width / 2, cy + height / 2);
	}

	public void drawRoundRectangle(double x, double y, double width, double height, double arcWidth, double arcHeight) {
		gc.drawRoundRectangle(tx(x), ty(y), tx(width) - tx(0), ty(height) - ty(0), tx(arcWidth) - tx(0), ty(arcHeight) - ty(0));
		bounds.extendBounds(x, y, x + width, y + height);
	}

	public void fillRoundRectangle(double x, double y, double width, double height, double arcWidth, double arcHeight) {
		gc.fillRoundRectangle(tx(x), ty(y), tx(width) - tx(0), ty(height) - ty(0), tx(arcWidth) - tx(0), ty(arcHeight) - ty(0));
		bounds.extendBounds(x, y, x + width, y + height);
	}

	public void drawLine(double x1, double y1, double x2, double y2) {
		gc.drawLine(tx(x1), ty(y1), tx(x2), ty(y2));
		bounds.extendBounds(Math.min(x1, x2), Math.min(y1, y2), Math.max(x1, x2), Math.max(y1, y2));
	}

	public void drawText(String text, Font font, String textAlign, double x, double y, boolean transparent) {
		TextLayout textLayout = new TextLayout(gc.getDevice());
		textLayout.setFont(font);
		textLayout.setText(text);
		
		double offsetX = 0;
		double offsetY = 0;
		
		if(textAlign.toLowerCase().contains("center")) {
			offsetX -= textLayout.getBounds().width / 2;
		} else if(textAlign.toLowerCase().contains("right")) {
			offsetX -= textLayout.getBounds().width;
		}
		
		if(textAlign.toLowerCase().contains("middle")) {
			offsetY -= textLayout.getBounds().height / 2;
		} else if(textAlign.toLowerCase().contains("bottom")) {
			offsetY -= textLayout.getBounds().height;
		}
		
		gc.setFont(font);
		gc.drawText(text, tx(x) + (int) offsetX, ty(y) + (int) offsetY, transparent);
		textLayout.dispose();
		bounds.extendBounds(x, y, x, y);
	}
	
	public void drawArrow(double x1, double y1, double x2, double y2, double arrowLength, double arrowAngle) {
		double theta = Math.atan2(y2 - y1, x2 - x1);
        double offset = (tx(arrowLength) - tx(0)) * Math.cos(arrowAngle);
        
        gc.drawLine(tx(x1), ty(y1), (int) (tx(x2) - offset * Math.cos(theta)), (int) (ty(y2) - offset * Math.sin(theta)));

		Path path = new Path(gc.getDevice());
		path.moveTo(tx(x2 - arrowLength * Math.cos(theta - arrowAngle)), ty(y2 - arrowLength * Math.sin(theta - arrowAngle)));
		path.lineTo(tx(x2), ty(y2));
		path.lineTo(tx(x2 - arrowLength * Math.cos(theta + arrowAngle)), ty(y2 - arrowLength * Math.sin(theta + arrowAngle)));
		path.close();
		
		gc.fillPath(path);
		
		path.dispose();
        
        bounds.extendBounds(Math.min(x1, x2), Math.min(y1, y2), Math.max(x1, x2), Math.max(y1, y2));
	}
	
	private int tx(double x) {
		return (int) (x * transformElements[0] + transformElements[4]);
	}
	
	private int ty(double y) {
		return (int) (y * transformElements[3] + transformElements[5]);
	}
}