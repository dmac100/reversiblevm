package frontend.compiler;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.eclipse.swt.widgets.Display;

import backend.instruction.Instruction;
import backend.runtime.CompileException;
import backend.runtime.Engine;
import backend.runtime.ExecutionException;
import backend.runtime.Runtime;
import frontend.controller.MainController;

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
	private Set<Instruction> lineBreakpoints = new HashSet<>();

	private final MainController mainController;
	
	public Compiler(MainController mainController) {
		this.mainController = mainController;
		compileEmpty();
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

		// Restart thread on exception.
		thread.setUncaughtExceptionHandler(new UncaughtExceptionHandler() {
			public void uncaughtException(Thread t, Throwable e) {
				runnableQueue.clear();
				compileEmpty();
				
				e.printStackTrace();
				startQueueThread();
				
				addError("Error: " + e.getMessage());
			}
		});
	}
	
	/**
	 * Handles the items in the runnable queue and keeps the vm running.
	 */
	private void runQueue() {
		long lastUiUpdate = 0;
		
		while(true) {
			// Handle all items currently in the queue.
			while(!runnableQueue.isEmpty()) {
				runnableQueue.remove().run();
			}
			
			if(runningForward) {
				stepForwardSync();
			} else if(runningBackward) {
				stepBackwardSync();
			} else {
				try {
					updateUi();
					
					// Wait for the next item in the queue.
					Runnable runnable = runnableQueue.poll(1, TimeUnit.MINUTES);
					if(runnable != null) {
						runnable.run();
					}
				} catch(InterruptedException e) {
					return;
				}
			}
			
			if(System.currentTimeMillis() - lastUiUpdate > 100) {
				lastUiUpdate = System.currentTimeMillis();
				updateUi();
			}
		}
	}

	/**
	 * Sends the state of the vm to the ui.
	 */
	private void updateUi() {
		boolean running = runningBackward || runningForward;
		
		int bufferSize = 1000;
		List<String> output = runtime.getOutput();
		List<String> errors = runtime.getErrors();
		output = output.subList(Math.max(0, output.size() - bufferSize), output.size());
		errors = errors.subList(Math.max(0, errors.size() - bufferSize), errors.size());
		
		final CompilerModel compilerModel = new CompilerModel();
		compilerModel.setOutput(new ArrayList<>(output));
		compilerModel.setErrors(new ArrayList<>(errors));
		compilerModel.setLineNumber(runtime.getLineNumber());
		compilerModel.setStepBackwardEnabled(!running);
		compilerModel.setStepForwardEnabled(!running);
		compilerModel.setRunForwardEnabled(!running);
		compilerModel.setRunBackwardEnabled(!running);
		compilerModel.setPauseEnabled(running);
		compilerModel.setCompileEnabled(true);
		
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				mainController.setCompilerModel(compilerModel);
			}
		});
	}

	private void addError(final String error) {
		runnableQueue.add(new Runnable() {
			public void run() {
				runtime.throwError(error);
			}
		});
	}
	
	/**
	 * Compiles an empty program.
	 */
	private void compileEmpty() {
		runnableQueue.add(new Runnable() {
			public void run() {
				compile("");
				runningForward = false;
				runningBackward = false;
				lineBreakpoints.clear();
			}
		});
	}
	
	/**
	 * Compiles the given program.
	 */
	public void compile(final String program) {
		runnableQueue.add(new Runnable() {
			public void run() {
				runtime = new Runtime();
				engine = new Engine(runtime, new ArrayList<Instruction>());
				runningForward = false;
				runningBackward = false;
				lineBreakpoints.clear();
					
				try {
					List<Instruction> instructions = Engine.compile(program);
					engine = new Engine(runtime, instructions);
				} catch(CompileException e) {
					runtime.throwError(e.getMessage());
				}
			}
		});
	}

	/**
	 * Runs the current program forward.
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
	 * Runs the current program backward
	 */
	public void runBackward() {
		runnableQueue.add(new Runnable() {
			public void run() {
				runningForward = false;
				runningBackward = true;
			}
		});
	}
	
	/**
	 * Stops the current program.
	 */
	public void pause() {
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
				stepForwardSync();
			}
		});
	}

	/**
	 * Steps forward through the current program without adding to the queue.
	 */
	private void stepForwardSync() {
		try {
			int lineNumber = runtime.getLineNumber();
			
			while(runtime.getLineNumber() == lineNumber || runtime.getLineNumber() <= 0) {
				if(runtime.getCurrentStackFrame() == null) {
					runningForward = false;
					return;
				}
				engine.stepForward();
			}
			
			lineBreakpoints.add(runtime.getInstruction());
		} catch(ExecutionException e) {
			runtime.throwError(e.getMessage());
			runningForward = false;
		}
	}
	
	/**
	 * Steps backward through the current program.
	 */
	public void stepBackward() {
		runnableQueue.add(new Runnable() {
			public void run() {
				stepBackwardSync();
			}
		});
	}
	
	/**
	 * Steps backward through the current program without adding to the queue.
	 */
	private void stepBackwardSync() {
		try {
			engine.stepBackward();
			
			while(!lineBreakpoints.contains(runtime.getInstruction())) {
				engine.stepBackward();
				if(runtime.getUndoStack().getSize() == 0) {
					runningBackward = false;
					return;
				}
			}
		} catch(ExecutionException e) {
			runtime.throwError(e.getMessage());
			runningBackward = false;
		}
	}
}