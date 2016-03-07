package frontend.compiler;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import backend.runtime.Engine;
import backend.runtime.Runtime;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import frontend.ui.Callback;

public class Compiler {
	private static ExecutorService executor = Executors.newCachedThreadPool(new ThreadFactoryBuilder().setDaemon(true).build());
	
	public Compiler() {
	}

	/**
	 * Compiles and runs a source file. Displays output to out,
	 * and compilation and program errors to err.
	 * 
	 * @param contents The source code as a String.
	 * @param input The input to use as the standard input stream. 
	 * @param name The name of the Java class.
	 * @param out Appender to send output to.
	 * @param err Appender to send errors to.
	 * @param callback The callback to call when the run is finished.
	 */
	public Future<?> runFile(final String contents, final String input, final Appender out, final Appender err, final Appender info, final Callback<Void> finishedCallback) {
		return executor.submit(new Callable<Void>() {
			public Void call() throws Exception {
				runFileSync(contents, input, out, err, info, finishedCallback);
				return null;
			}
		});
	}
	
	private void runFileSync(String contents, String input, Appender out, Appender err, Appender info, Callback<Void> finishedCallback) throws InterruptedException {
		try {
			Runtime runtime = new Runtime();
			Engine engine = new Engine(runtime, Engine.compile(contents));
			
			while(runtime.getCurrentStackFrame() != null) {
				engine.stepForward();
				if(Thread.currentThread().isInterrupted()) {
					return;
				}
			}
			
			for(String line:runtime.getOutput()) {
				out.append(line + "\n");
			}
			
			for(String line:runtime.getErrors()) {
				err.append(line + "\n");
			}
		} catch(Exception e) {
			err.append("Exception running program: " + e.toString() + "\n");
			e.printStackTrace();
		} finally {
			finishedCallback.onCallback(null);
		}
	}
}
