package frontend.controller;

import java.util.Timer;
import java.util.TimerTask;

import org.eclipse.swt.widgets.Display;

import frontend.compiler.Appender;
import frontend.ui.ConsoleText;

/**
 * Buffers and writes text to a ConsoleText surrounded by some ansi color codes.
 */
public class ConsoleAppender implements Appender {
	public static final String COLOR_BLUE = "\u001B[34m";
	public static final String COLOR_RED = "\u001B[31m";
	public static final String COLOR_OFF = "\u001B[0m";
	
	private String color;
	
	private ConsoleText consoleText;
	
	private StringBuilder buffer = new StringBuilder();
	private Timer timer = new Timer(true);
	private boolean timerRunning = false;
	private boolean closed = false;

	/**
	 * Creates a ConsoleAppender that appends to consoleText with a color. If color
	 * is null then the text is output with no color transformations.
	 */
	public ConsoleAppender(ConsoleText consoleText, String color) {
		this.consoleText = consoleText;
		this.color = color;
	}
	
	public synchronized void append(final String s) {
		if(color == null) {
			buffer.append(s);
		} else {
			buffer.append(color + s + COLOR_OFF);
		}
		
		if(!timerRunning) {
			timerRunning = true;
			
			timer.schedule(new TimerTask() {
				public void run() {
					timerRunning = false;
					timerFinished();
				}
			}, 100);
		}
	}
	
	private void timerFinished() {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				flush();
			}
		});
	}
	
	private synchronized void flush() {
		if(!closed) {
			consoleText.append(buffer.toString());
			buffer.setLength(0);
		}
	}
	
	public synchronized void close() {
		closed = true;
	}
}