package frontend.controller;

import java.util.Timer;
import java.util.TimerTask;

import org.eclipse.swt.widgets.Display;

import frontend.compiler.Appender;
import frontend.ui.ConsoleText;

/**
 * Buffers and writes text to a ConsoleText.
 */
public class ConsoleAppender implements Appender {
	private ConsoleText consoleText;
	
	private StringBuilder buffer = new StringBuilder();
	private Timer timer = new Timer(true);
	private boolean timerRunning = false;
	private boolean closed = false;

	public ConsoleAppender(ConsoleText consoleText) {
		this.consoleText = consoleText;
	}
	
	public synchronized void append(final String s) {
		buffer.append(s);
		
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