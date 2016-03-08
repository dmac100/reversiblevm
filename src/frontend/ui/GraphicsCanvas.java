package frontend.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import com.google.common.eventbus.EventBus;

public class GraphicsCanvas {
	private final Canvas canvas;
	
	public GraphicsCanvas(final EventBus eventBus, Composite parent) {
		canvas = new Canvas(parent, SWT.NONE);
	}
	
	public Control getControl() {
		return canvas;
	}
}
