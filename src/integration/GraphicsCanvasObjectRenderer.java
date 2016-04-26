package integration;

import static backend.util.VizObjectUtil.ensureInRange;
import static backend.util.VizObjectUtil.getDoubleOrDefault;
import static backend.util.VizObjectUtil.getStringOrDefault;

import java.awt.Graphics2D;
import java.util.Arrays;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Path;
import org.eclipse.swt.graphics.TextLayout;
import org.eclipse.swt.graphics.Transform;

import frontend.ui.ColorCache;

public class GraphicsCanvasObjectRenderer {
	private final ColorCache colorCache;

	public GraphicsCanvasObjectRenderer(ColorCache colorCache) {
		this.colorCache = colorCache;
	}
	
	/**
	 * Draws a single visual object onto the graphics context.
	 */
	public void paint(GC gc, Transform transform, Bounds bounds, DisplayedVizObject vizObject) {
		String name = vizObject.getName();
		
		double x = getDoubleOrDefault(vizObject, "x", 0);
		double y = getDoubleOrDefault(vizObject, "y", 0);
		double x1 = getDoubleOrDefault(vizObject, "x1", 0);
		double y1 = getDoubleOrDefault(vizObject, "y1", 0);
		double x2 = getDoubleOrDefault(vizObject, "x2", 0);
		double y2 = getDoubleOrDefault(vizObject, "y2", 0);
		double cx = getDoubleOrDefault(vizObject, "cx", 0);
		double cy = getDoubleOrDefault(vizObject, "cy", 0);
		double width = getDoubleOrDefault(vizObject, "width", 50);
		double height = getDoubleOrDefault(vizObject, "height", 50);
		double r = getDoubleOrDefault(vizObject, "r", 0);
		double arcWidth = getDoubleOrDefault(vizObject, "rx", 0) * 2;
		double arcHeight = getDoubleOrDefault(vizObject, "ry", 0) * 2;
		double opacity = getDoubleOrDefault(vizObject, "opacity", 1);
		int colorRed = (int) getDoubleOrDefault(vizObject, "fill-red", 200);
		int colorGreen = (int) getDoubleOrDefault(vizObject, "fill-green", 200);
		int colorBlue = (int) getDoubleOrDefault(vizObject, "fill-blue", 200);
		int strokeRed = (int) getDoubleOrDefault(vizObject, "stroke-red", 200);
		int strokeGreen = (int) getDoubleOrDefault(vizObject, "stroke-green", 200);
		int strokeBlue = (int) getDoubleOrDefault(vizObject, "stroke-blue", 200);
		int strokeWidth = (int) getDoubleOrDefault(vizObject, "strokeWidth", 0);
		String strokeStyle = getStringOrDefault(vizObject, "strokeStyle", "solid");
		double arrowLength = getDoubleOrDefault(vizObject, "arrowLength", 0);
		double arrowAngle = getDoubleOrDefault(vizObject, "arrowAngle", 40);
		double startOffset = getDoubleOrDefault(vizObject, "startOffset", 0);
		double endOffset = getDoubleOrDefault(vizObject, "endOffset", 0);
		String text = getStringOrDefault(vizObject, "text", "");
		int fontSize = (int) getDoubleOrDefault(vizObject, "fontSize", 12);
		String fontName = getStringOrDefault(vizObject, "fontName", "Arial");
		String fontStyle = getStringOrDefault(vizObject, "fontStyle", "normal");
		String textAlign = getStringOrDefault(vizObject, "textAlign", "left");
		
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
		
		ScaledCanvas scaledCanvas = new ScaledCanvas(gc, transform, bounds);
		
		if(name.equals("rect")) {
			if(!vizObject.hasProperty("rx")) arcWidth = arcHeight;
			if(!vizObject.hasProperty("ry")) arcHeight = arcWidth;
			arcWidth = ensureInRange(arcWidth, 0, width);
			arcHeight = ensureInRange(arcHeight, 0, height);
			
			if(!getStringOrDefault(vizObject, "fill", "").equals("none")) {
				scaledCanvas.fillRoundRectangle(x, y, width, height, arcWidth, arcHeight);
			}
			if(strokeWidth > 0) {
				scaledCanvas.drawRoundRectangle(x, y, width, height, arcWidth, arcHeight);
			}
		} else if(name.equals("ellipse")) {
			if(!getStringOrDefault(vizObject, "fill", "").equals("none")) {
				scaledCanvas.fillOval(cx, cy, arcWidth * 2, arcHeight * 2);
			}
			if(strokeWidth > 0) {
				scaledCanvas.drawOval(cx, cy, arcWidth * 2, arcHeight * 2);
			}
		} else if(name.equals("circle")) {
			if(!getStringOrDefault(vizObject, "fill", "").equals("none")) {
				scaledCanvas.fillOval(cx, cy, r * 2, r * 2);
			}
			if(strokeWidth > 0) {
				scaledCanvas.drawOval(cx, cy, r * 2, r * 2);
			}
		} else if(name.equals("line")) {
			if(startOffset > 0 || endOffset > 0) {
				double theta = Math.atan2(y2 - y1, x2 - x1);
				x1 += startOffset * Math.cos(theta);
				y1 += startOffset * Math.sin(theta);
				x2 -= endOffset * Math.cos(theta);
				y2 -= endOffset * Math.sin(theta);
			}
			
			if(arrowLength > 0) {
				gc.setBackground(colorCache.getColor(strokeRed, strokeGreen, strokeBlue));
				scaledCanvas.drawArrow(x1, y1, x2, y2, arrowLength, Math.toRadians(arrowAngle));
			} else {
				scaledCanvas.drawLine(x1, y1, x2, y2);
			}
		} else if(name.equals("text")) {
			int style = SWT.NORMAL;
			if(fontStyle.toLowerCase().contains("bold")) style |= SWT.BOLD;
			if(fontStyle.toLowerCase().contains("italic")) style |= SWT.ITALIC;
			Font font = new Font(gc.getDevice(), new FontData(fontName, fontSize, style));

			scaledCanvas.drawText(text, font, textAlign, x, y, true);
			
			font.dispose();
		}
		
		gc.setLineStyle(SWT.LINE_SOLID);
		gc.setLineWidth(0);
		gc.setAlpha(255);
	}
}