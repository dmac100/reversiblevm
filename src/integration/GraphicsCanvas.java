package integration;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;

import backend.runtime.VizObject;
import backend.value.BooleanValue;
import backend.value.DoubleValue;
import backend.value.ImmutableValue;
import backend.value.StringValue;

import com.google.common.eventbus.EventBus;

import frontend.ui.ColorCache;

public class GraphicsCanvas {
	private final Canvas canvas;
	private final ColorCache colorCache;
	
	private List<VizObject> vizObjects = new ArrayList<>();
	
	public GraphicsCanvas(final EventBus eventBus, Composite parent) {
		canvas = new Canvas(parent, SWT.DOUBLE_BUFFERED);
		colorCache = new ColorCache(canvas.getDisplay());
		
		canvas.addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent event) {
				paint(event.display, event.gc);
			}
		});
	}
	
	public void setVizObjects(List<VizObject> vizObjects) {
		this.vizObjects = vizObjects;
		redraw();
	}
	
	public void redraw() {
		canvas.redraw();
	}
	
	private void paint(Display display, GC gc) {
		int canvasWidth = canvas.getBounds().width;
		int canvasHeight = canvas.getBounds().height;
		int canvasMargin = 5;

		// Draw background of canvas.
		gc.setBackground(colorCache.getColor(255, 255, 255));
		gc.fillRoundRectangle(canvasMargin, canvasMargin, canvasWidth - canvasMargin * 2, canvasHeight - canvasMargin * 2, 3, 3);
		
		// Clip to within the margin of the canvas.
		gc.setClipping(canvasMargin + 1, canvasMargin + 1, canvasWidth - canvasMargin * 2, canvasHeight - canvasMargin * 2);
		
		// Paint the visual objects.
		paintVizObjects(gc, canvasWidth, canvasHeight, canvasMargin, vizObjects);
		
		// Draw border around canvas.
		gc.setClipping(0, 0, canvasWidth, canvasHeight);
		gc.setForeground(colorCache.getColor(150, 150, 150));
		gc.drawRoundRectangle(canvasMargin + 1, canvasMargin + 1, canvasWidth - canvasMargin * 2, canvasHeight - canvasMargin * 2, 3, 3);
	}

	private List<VizObject> getVizObjects() {
		String[] colors = { "red", "green", "blue", "yellow", "magenta", "cyan", "white", "lightgrey", "grey", "darkgrey", "black" };
		
		List<VizObject> vizObjects = new ArrayList<>();
		for(int i = 0; i < colors.length; i++) {
			VizObject vizObject = new VizObject("rect");
			vizObject.setProperty("x", new DoubleValue(50));
			vizObject.setProperty("y", new DoubleValue(i * 40));
			vizObject.setProperty("width", new DoubleValue(50));
			vizObject.setProperty("height", new DoubleValue(30));
			vizObject.setProperty("color", new StringValue(colors[i]));
			vizObjects.add(vizObject);
		}
		
		return vizObjects;
	}

	private void paintVizObjects(GC gc, int canvasWidth, int canvasHeight, int canvasMargin, List<VizObject> vizObjects) {
		for(VizObject vizObject:vizObjects) {
			String name = vizObject.getName();
			
			if(name.equals("rect")) {
				int x = (int) getDoubleOrDefault(vizObject, "x", 0);
				int y = (int) getDoubleOrDefault(vizObject, "y", 0);
				int width = (int) getDoubleOrDefault(vizObject, "width", 50);
				int height = (int) getDoubleOrDefault(vizObject, "height", 50);
				
				gc.setBackground(getColorOrDefault(vizObject, "color", "red"));
				gc.fillRectangle(x + canvasMargin, y + canvasMargin, width, height);
			}
		}
	}

	private Color getColorOrDefault(VizObject vizObject, String name, String defaultValue) {
		String color = getStringOrDefault(vizObject, name, "red");
		
		if(color.equals("red")) return colorCache.getColor(200, 100, 100);
		if(color.equals("green")) return colorCache.getColor(150, 200, 100);
		if(color.equals("blue")) return colorCache.getColor(100, 100, 200);
		if(color.equals("yellow")) return colorCache.getColor(200, 200, 100);
		if(color.equals("magenta")) return colorCache.getColor(200, 100, 200);
		if(color.equals("cyan")) return colorCache.getColor(100, 200, 200);
		if(color.equals("white")) return colorCache.getColor(255, 255, 255);
		if(color.equals("lightgrey")) return colorCache.getColor(200, 200, 200);
		if(color.equals("grey")) return colorCache.getColor(150, 150, 150);
		if(color.equals("darkgrey")) return colorCache.getColor(50, 50, 50);
		if(color.equals("black")) return colorCache.getColor(0, 0, 0);
		
		return colorCache.getColor(200, 100, 100);
	}

	private double getDoubleOrDefault(VizObject vizObject, String name, double defaultValue) {
		ImmutableValue value = vizObject.getProperty(name);
		if(value instanceof DoubleValue) {
			return ((DoubleValue)value).getValue();
		} else {
			return defaultValue;
		}
	}
	
	private String getStringOrDefault(VizObject vizObject, String name, String defaultValue) {
		ImmutableValue value = vizObject.getProperty(name);
		if(value instanceof StringValue) {
			return ((StringValue)value).getValue();
		} else {
			return defaultValue;
		}
	}
	
	private boolean getBooleanOrDefault(VizObject vizObject, String name, boolean defaultValue) {
		ImmutableValue value = vizObject.getProperty(name);
		if(value instanceof BooleanValue) {
			return ((BooleanValue)value).getValue();
		} else {
			return defaultValue;
		}
	}

	public Control getControl() {
		return canvas;
	}
}