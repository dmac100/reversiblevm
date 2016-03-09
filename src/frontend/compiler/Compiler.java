package frontend.compiler;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.eclipse.swt.widgets.Display;

import frontend.controller.MainController;

import backend.instruction.Instruction;
import backend.runtime.Engine;
import backend.runtime.Runtime;

/**
 * Handles the interaction between the frontend and the backend compiler and runtime.
 * Runs the actions in a queue so that all compiler interactions are on the same thread.
 */
public class Compiler {
	private final ArrayBlockingQueue<Runnable> runnableQueue = new ArrayBlockingQueue<>(50);
	
	private Runtime runtime;
	private Engine engine;
	
	private boolean runningForward;
	private boolean runningBackward;

	private final MainController mainController;
	
	public Compiler(MainController mainController) {
		this.mainController = mainController;
		
		runnableQueue.add(new Runnable() {
			public void run() {
				compile("");
			}
		});
	}
	
	/**
	 * Starts a thread to handle the items posted to the runnable queue.
	 */
	public void startQueueThread() {
		Thread thread = new Thread(new Runnable() {
			public void run() {
				runQueue();
			}
		});
		thread.setDaemon(true);
		thread.start();
	}
	
	/**
	 * Handles the items in the runnable queue and keeps the vm running.
	 */
	private void runQueue() {
		while(true) {
			// Handle all items currently in the queue.
			while(!runnableQueue.isEmpty()) {
				runnableQueue.remove().run();
			}
			
			if(runningForward) {
				stepForward();
			} else if(runningBackward) {
				stepBackward();
			} else {
				try {
					// Wait for the next item in the queue.
					Runnable runnable = runnableQueue.poll(1, TimeUnit.MINUTES);
					if(runnable != null) {
						runnable.run();
					}
				} catch(InterruptedException e) {
					return;
				}
			}
			
			updateUi();
		}
	}

	/**
	 * Sends the state of the vm to the ui.
	 */
	private void updateUi() {
		boolean running = runningBackward || runningForward;
		
		final CompilerModel compilerModel = new CompilerModel();
		compilerModel.setOutput(new ArrayList<>(runtime.getOutput()));
		compilerModel.setErrors(new ArrayList<>(runtime.getErrors()));
		compilerModel.setLineNumber(-1);
		compilerModel.setStepBackwardEnabled(!running);
		compilerModel.setStepForwardEnabled(!running);
		compilerModel.setRunEnabled(!running);
		compilerModel.setStopEnabled(running);
		compilerModel.setCompileEnabled(true);
		
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				mainController.setCompilerModel(compilerModel);
			}
		});
	}

	/**
	 * Compiles the given program and runs it.
	 */
	public void compile(final String program) {
		runnableQueue.add(new Runnable() {
			public void run() {
				List<Instruction> instructions = Engine.compile(program);
				runtime = new Runtime();
				engine = new Engine(runtime, instructions);
				runForward();
			}
		});
	}

	/**
	 * Runs the current program.
	 */
	public void runForward() {
		runnableQueue.add(new Runnable() {
			public void run() {
				runningForward = true;
				runningBackward = false;
			}
		});
	}
	
	/**
	 * Stops the current program.
	 */
	public void stop() {
		runnableQueue.add(new Runnable() {
			public void run() {
				runningForward = false;
				runningBackward = false;
			}
		});
	}
	
	/**
	 * Steps forward through the current program.
	 */
	public void stepForward() {
		runnableQueue.add(new Runnable() {
			public void run() {
				if(runtime.getCurrentStackFrame() != null) {
					engine.stepForward();
				} else {
					runningForward = false;
				}
			}
		});
	}
	
	/**
	 * Steps backward through the current program.
	 */
	public void stepBackward() {
		runnableQueue.add(new Runnable() {
			public void run() {
				if(runtime.getCurrentStackFrame() != null) {
					engine.stepBackward();
				} else {
					runningBackward = false;
				}
			}
		});
	}
}