package integration;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Transform;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;

import backend.runtime.VizObject;

import com.google.common.eventbus.EventBus;

import frontend.ui.ColorCache;

public class GraphicsCanvas {
	private final Canvas canvas;
	private final ColorCache colorCache;
	
	private Map<Object, DisplayedVizObject> displayedVizObjects = new LinkedHashMap<>();
	private int transitionDelay = 400;
	private String boundsFit = "extend";
	
	private Thread refreshLoopThread;
	private final Object refreshLock = new Object();
	
	private Bounds bounds;
	
	public GraphicsCanvas(final EventBus eventBus, Composite parent) {
		canvas = new Canvas(parent, SWT.DOUBLE_BUFFERED);
		colorCache = new ColorCache(canvas.getDisplay());
		
		canvas.addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent event) {
				paint(event.display, event.gc);
			}
		});
	}
	
	private Thread startRefreshLoopThread() {
		Thread thread = new Thread(new Runnable() {
			public void run() {
				try {
					runRefreshLoop();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		});
		thread.setDaemon(true);
		thread.start();
		return thread;
	}
	
	private void runRefreshLoop() throws InterruptedException {
		while(true) {
			if(!canvas.isDisposed()) {
				final AtomicBoolean updatePending = new AtomicBoolean(false);
				canvas.getDisplay().syncExec(new Runnable() {
					public void run() {
						if(!canvas.isDisposed()) {
							if(redraw()) {
								updatePending.set(true);
							}
						}
					}
				});
				if(!updatePending.get()) {
					synchronized(refreshLock) {
						refreshLock.wait();
					}
				}
				Thread.sleep(10);
			}
		}
	}
	
	public void setVizObjectControlledSettings(VizObjectControlledSettings vizObjectControlledSettings) {
		transitionDelay = vizObjectControlledSettings.getTransitionDelay();
		boundsFit = vizObjectControlledSettings.getBoundsFit();
	}
	
	public void setVizObjects(List<VizObject> newVizObjects) {
		Map<Object, DisplayedVizObject> newDisplayedVizObjects = new LinkedHashMap<>();
		
		Set<Object> keys = new HashSet<>();
		
		for(VizObject newVizObject:newVizObjects) {
			Object key = newVizObject.getKey();
			if(displayedVizObjects.containsKey(key)) {
				DisplayedVizObject vizObject = displayedVizObjects.get(key);
				newDisplayedVizObjects.put(key, vizObject);
				vizObject.update(newVizObject);
			} else {
				DisplayedVizObject vizObject = new DisplayedVizObject(newVizObject);
				vizObject.create();
				newDisplayedVizObjects.put(key, vizObject);
			}
			keys.add(key);
		}
		
		for(Object key:displayedVizObjects.keySet()) {
			if(!keys.contains(key)) {
				DisplayedVizObject vizObject = displayedVizObjects.get(key);
				vizObject.delete();
				newDisplayedVizObjects.put(key, vizObject);
			}
		}
		
		displayedVizObjects = newDisplayedVizObjects;
		
		redraw();
		
		if(refreshLoopThread == null) {
			refreshLoopThread = startRefreshLoopThread();
		}
		
		synchronized (refreshLock) {
			refreshLock.notifyAll();
		}
	}
	
	public boolean redraw() {
		boolean updatePending = false;
		for(Iterator<DisplayedVizObject> iterator = displayedVizObjects.values().iterator(); iterator.hasNext();) {
			DisplayedVizObject vizObject = iterator.next();
			vizObject.redraw(transitionDelay);
			if(vizObject.isDeleted()) {
				iterator.remove();
			} else if(vizObject.isUpdatePending()) {
				updatePending = true;
			}
		}
		
		canvas.redraw();
		
		return updatePending;
	}
	
	private void paint(Display display, GC gc) {
		int canvasWidth = canvas.getBounds().width;
		int canvasHeight = canvas.getBounds().height;
		int canvasMargin = 6;
		
		// Draw background of canvas.
		gc.setBackground(colorCache.getColor(255, 255, 255));
		gc.fillRoundRectangle(canvasMargin / 2, canvasMargin / 2, canvasWidth - canvasMargin, canvasHeight - canvasMargin, 3, 3);
		
		// Clip to within the margin of the canvas.
		gc.setClipping(canvasMargin, canvasMargin, canvasWidth - canvasMargin * 2, canvasHeight - canvasMargin * 2);
		
		Transform transform = new Transform(display);
		transform.translate(canvasMargin, canvasMargin);
		if(bounds != null) {
			float boundsWidth = bounds.getMaxX() - bounds.getMinX();
			float boundsHeight = bounds.getMaxY() - bounds.getMinY();
			float canvasBoundsWidth = (canvasWidth - canvasMargin * 2);
			float canvasBoundsHeight = (canvasHeight - canvasMargin * 2);
			if(boundsFit.equals("extend")) {
				float scaleX = canvasBoundsWidth / boundsWidth;
				float scaleY = canvasBoundsHeight / boundsHeight;
				float scale = Math.min(scaleX, scaleY);
				transform.translate(-bounds.getMinX() * scale, -bounds.getMinY() * scale);
				transform.scale(scale, scale);
			} else if(boundsFit.equals("full")) {
				float scaleX = canvasBoundsWidth / boundsWidth;
				float scaleY = canvasBoundsHeight / boundsHeight;
				float scale = Math.min(scaleX, scaleY) * 0.95f;
				transform.translate(-bounds.getMinX() * scale, -bounds.getMinY() * scale);
				transform.translate(
					(boundsWidth * scale) * (scaleX / scale) / 2 - (boundsWidth / 2 * scale),
					(boundsHeight * scale) * (scaleY / scale) / 2 - (boundsHeight / 2 * scale)
				);
				transform.scale(scale, scale);
			}
		}
		
		// Paint the visual objects.
		GraphicsCanvasObjectRenderer renderer = new GraphicsCanvasObjectRenderer(colorCache);
		Bounds newBounds;
		if(boundsFit.equals("full")) {
			newBounds = new Bounds(Integer.MAX_VALUE, Integer.MAX_VALUE, -Integer.MAX_VALUE, -Integer.MAX_VALUE);
		} else {
			newBounds = new Bounds(canvasMargin, canvasMargin, canvasWidth - canvasMargin * 2, canvasHeight - canvasMargin * 2);
		}
		for(DisplayedVizObject vizObject:displayedVizObjects.values()) {
			renderer.paint(gc, transform, newBounds, vizObject);
		}
		if(!newBounds.equals(bounds)) {
			canvas.redraw();
		}
		this.bounds = newBounds;
		transform.dispose();
		
		// Draw border around canvas.
		gc.setClipping(0, 0, canvasWidth, canvasHeight);
		gc.setForeground(colorCache.getColor(150, 150, 150));
		gc.drawRoundRectangle(canvasMargin / 2, canvasMargin / 2, canvasWidth - canvasMargin, canvasHeight - canvasMargin, 3, 3);
	}

	public Control getControl() {
		return canvas;
	}
}