package frontend.ui;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

/**
 * Stores used colors so they can be cached, and disposed when needed.
 */
public class ColorCache implements DisposeListener {
	private Display display;
	private Map<RGB, Color> colors = new HashMap<>();

	public ColorCache(Display display) {
		this.display = display;
	}

	public Color getColor(java.awt.Color color) {
		RGB rgb = new RGB(
			color.getRed(),
			color.getGreen(),
			color.getBlue()
		);
		
		if(colors.containsKey(rgb)) {
			return colors.get(rgb);
		} else {
			Color c = new Color(display, rgb);
			colors.put(rgb, c);
			return c;
		}
	}
	
	public Color getColor(int r, int g, int b) {
		return getColor(new java.awt.Color(r, g, b));
	}
	
	public Color getColor(RGB rgb) {
		return getColor(rgb.red, rgb.green, rgb.blue);
	}
	
	public void dispose() {
		for(Color color:colors.values()) {
			color.dispose();
		}
	}

	public void widgetDisposed(DisposeEvent event) {
		dispose();
	}
}
