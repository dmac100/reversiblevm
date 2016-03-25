package integration;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Path;

import backend.value.BooleanValue;
import backend.value.DoubleValue;
import backend.value.ImmutableValue;
import backend.value.StringValue;
import frontend.ui.ColorCache;

public class GraphicsCanvasObjectRenderer {
	private final ColorCache colorCache;

	public GraphicsCanvasObjectRenderer(ColorCache colorCache) {
		this.colorCache = colorCache;
	}
	
	/**
	 * Draws a single visual object onto the graphics context.
	 */
	public void paint(GC gc, DisplayedVizObject vizObject) {
		String name = vizObject.getName();
		
		int x = (int) getDoubleOrDefault(vizObject, "x", 0);
		int y = (int) getDoubleOrDefault(vizObject, "y", 0);
		int x1 = (int) getDoubleOrDefault(vizObject, "x1", 0);
		int y1 = (int) getDoubleOrDefault(vizObject, "y1", 0);
		int x2 = (int) getDoubleOrDefault(vizObject, "x2", 0);
		int y2 = (int) getDoubleOrDefault(vizObject, "y2", 0);
		int cx = (int) getDoubleOrDefault(vizObject, "cx", 0);
		int cy = (int) getDoubleOrDefault(vizObject, "cy", 0);
		int width = (int) getDoubleOrDefault(vizObject, "width", 50);
		int height = (int) getDoubleOrDefault(vizObject, "height", 50);
		int r = (int) getDoubleOrDefault(vizObject, "r", 0);
		int arcWidth = (int) getDoubleOrDefault(vizObject, "rx", 0) * 2;
		int arcHeight = (int) getDoubleOrDefault(vizObject, "ry", 0) * 2;
		double opacity = (double) getDoubleOrDefault(vizObject, "opacity", 1);
		int colorRed = (int) getDoubleOrDefault(vizObject, "fill-red", 200);
		int colorGreen = (int) getDoubleOrDefault(vizObject, "fill-green", 200);
		int colorBlue = (int) getDoubleOrDefault(vizObject, "fill-blue", 200);
		int strokeRed = (int) getDoubleOrDefault(vizObject, "stroke-red", 200);
		int strokeGreen = (int) getDoubleOrDefault(vizObject, "stroke-green", 200);
		int strokeBlue = (int) getDoubleOrDefault(vizObject, "stroke-blue", 200);
		int strokeWidth = (int) getDoubleOrDefault(vizObject, "strokeWidth", 0);
		String strokeStyle = getStringOrDefault(vizObject, "strokeStyle", "solid");
		int arrowLength = (int) getDoubleOrDefault(vizObject, "arrowLength", 0);
		int arrowAngle = (int) getDoubleOrDefault(vizObject, "arrowAngle", 40);
		String text = getStringOrDefault(vizObject, "text", "");
		int fontSize = (int) getDoubleOrDefault(vizObject, "fontSize", 12);
		String fontName = getStringOrDefault(vizObject, "fontName", "Arial");
		String fontStyle = getStringOrDefault(vizObject, "fontStyle", "normal");
		
		arrowAngle = ensureInRange(arrowAngle, 0, 90);
		
		gc.setAlpha((int)(opacity * 255));
		gc.setBackground(colorCache.getColor(colorRed, colorGreen, colorBlue));
		gc.setForeground(colorCache.getColor(strokeRed, strokeGreen, strokeBlue));
		gc.setLineWidth(strokeWidth);
		
		if(strokeStyle.equals("solid")) gc.setLineStyle(SWT.LINE_SOLID);
		if(strokeStyle.equals("dot")) gc.setLineStyle(SWT.LINE_DOT);
		if(strokeStyle.equals("dash")) gc.setLineStyle(SWT.LINE_DASH);
		if(strokeStyle.equals("dashdot")) gc.setLineStyle(SWT.LINE_DASHDOT);
		if(strokeStyle.equals("dashdotdot")) gc.setLineStyle(SWT.LINE_DASHDOTDOT);
		
		if(name.equals("rect")) {
			if(!vizObject.hasProperty("rx")) arcWidth = arcHeight;
			if(!vizObject.hasProperty("ry")) arcHeight = arcWidth;
			arcWidth = ensureInRange(arcWidth, 0, width);
			arcHeight = ensureInRange(arcHeight, 0, height);
			
			if(!getStringOrDefault(vizObject, "fill", "").equals("none")) {
				gc.fillRoundRectangle(x, y, width, height, arcWidth, arcHeight);
			}
			if(strokeWidth > 0) {
				gc.drawRoundRectangle(x, y, width, height, arcWidth, arcHeight);
			}
		} else if(name.equals("ellipse")) {
			if(!getStringOrDefault(vizObject, "fill", "").equals("none")) {
				gc.fillOval(cx - arcWidth, cy - arcHeight, arcWidth * 2, arcHeight * 2);
			}
			if(strokeWidth > 0) {
				gc.drawOval(cx - arcWidth, cy - arcHeight, arcWidth * 2, arcHeight * 2);
			}
		} else if(name.equals("circle")) {
			if(!getStringOrDefault(vizObject, "fill", "").equals("none")) {
				gc.fillOval(cx - r, cy - r, r * 2, r * 2);
			}
			if(strokeWidth > 0) {
				gc.drawOval(cx - r, cy - r, r * 2, r * 2);
			}
		} else if(name.equals("line")) {
			if(arrowLength > 0) {
				gc.setBackground(colorCache.getColor(strokeRed, strokeGreen, strokeBlue));
				drawArrow(gc, x1, y1, x2, y2, arrowLength, Math.toRadians(arrowAngle));
			} else {
				gc.drawLine(x1, y1, x2, y2);
			}
		} else if(name.equals("text")) {
			int style = SWT.NORMAL;
			if(fontStyle.toLowerCase().contains("bold")) style |= SWT.BOLD;
			if(fontStyle.toLowerCase().contains("italic")) style |= SWT.ITALIC;
			Font font = new Font(gc.getDevice(), new FontData(fontName, fontSize, style));
			gc.setFont(font);
			gc.drawText(text, x, y, true);
			font.dispose();
		}
		
		gc.setLineStyle(SWT.LINE_SOLID);
		gc.setLineWidth(0);
		gc.setAlpha(255);
	}
	
	private static void drawArrow(GC gc, int x1, int y1, int x2, int y2, double arrowLength, double arrowAngle) {
        double theta = Math.atan2(y2 - y1, x2 - x1);
        double offset = (arrowLength - 2) * Math.cos(arrowAngle);
        
        gc.drawLine(x1, y1, (int)(x2 - offset * Math.cos(theta)), (int)(y2 - offset * Math.sin(theta)));
		
		Path path = new Path(gc.getDevice());
		path.moveTo((float)(x2 - arrowLength * Math.cos(theta - arrowAngle)), (float)(y2 - arrowLength * Math.sin(theta - arrowAngle)));
		path.lineTo((float)x2, (float)y2);
		path.lineTo((float)(x2 - arrowLength * Math.cos(theta + arrowAngle)), (float)(y2 - arrowLength * Math.sin(theta + arrowAngle)));
		path.close();
		
		gc.fillPath(path);
		
		path.dispose();
	}

	private static int ensureInRange(int value, int min, int max) {
		if(value < min) return min;
		if(value > max) return max;
		return value;
	}

	private double getDoubleOrDefault(DisplayedVizObject vizObject, String name, double defaultValue) {
		ImmutableValue value = vizObject.getProperty(name);
		if(value instanceof DoubleValue) {
			return ((DoubleValue)value).getValue();
		} else {
			return defaultValue;
		}
	}
	
	private String getStringOrDefault(DisplayedVizObject vizObject, String name, String defaultValue) {
		ImmutableValue value = vizObject.getProperty(name);
		if(value instanceof StringValue) {
			return ((StringValue)value).getValue();
		} else {
			return defaultValue;
		}
	}
	
	private boolean getBooleanOrDefault(DisplayedVizObject vizObject, String name, boolean defaultValue) {
		ImmutableValue value = vizObject.getProperty(name);
		if(value instanceof BooleanValue) {
			return ((BooleanValue)value).getValue();
		} else {
			return defaultValue;
		}
	}
}