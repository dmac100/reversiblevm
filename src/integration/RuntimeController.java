package integration;

import static com.google.common.util.concurrent.Uninterruptibles.sleepUninterruptibly;

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
import backend.runtime.OutputLine;
import backend.runtime.Runtime;
import backend.runtime.VizObject;
import backend.util.VizObjectUtil;
import frontend.controller.MainController;

/**
 * Handles the interaction between the frontend and the backend compiler and runtime.
 * Runs the actions in a queue so that all compiler interactions are on the same thread.
 */
public class RuntimeController {
	private final ArrayBlockingQueue<Runnable> runnableQueue = new ArrayBlockingQueue<>(50);
	
	private Runtime runtime;
	private Engine engine;
	
	private boolean runningForward;
	private boolean runningBackward;
	private Set<Instruction> lineBreakpoints = new HashSet<>();
	private Set<Integer> userBreakpoints = new HashSet<>();
	private int instructionDelay = 1;
	private int maxInstructionsExecutedCount = 0;

	private final MainController mainController;
	
	public RuntimeController(MainController mainController) {
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
				
				addError("Unhandled Error: " + e.getClass().getName() + ": " + e.getMessage());
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
				sleepUninterruptibly(instructionDelay, TimeUnit.MILLISECONDS);
			} else if(runningBackward) {
				stepBackwardSync();
				sleepUninterruptibly(instructionDelay, TimeUnit.MILLISECONDS);
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
			
			if(System.currentTimeMillis() - lastUiUpdate > 50) {
				updateUi();
				lastUiUpdate = System.currentTimeMillis();
			}
		}
	}

	/**
	 * Sends the state of the vm to the ui.
	 */
	private void updateUi() {
		boolean running = runningBackward || runningForward;
		
		int bufferSize = 1000;
		List<OutputLine> output = runtime.getOutput();
		output = new ArrayList<>(output.subList(Math.max(0, output.size() - bufferSize), output.size()));
		
		List<VizObject> vizObjects = runtime.getVizObjects();
		
		final RuntimeModel runtimeModel = new RuntimeModel();
		runtimeModel.setOutput(new ArrayList<>(output));
		runtimeModel.setVizObjects(vizObjects);
		runtimeModel.setLineNumber(runtime.getLineNumber());
		runtimeModel.setBackwardEnabled(!running && !runtime.atStart());
		runtimeModel.setForwardEnabled(!running && !runtime.atEnd());
		runtimeModel.setPauseEnabled(running);
		runtimeModel.setCompileEnabled(true);
		runtimeModel.setLinesExecutedCount(runtime.getInstructionsExecutedCount());
		runtimeModel.setInstructionsExecutedCount(maxInstructionsExecutedCount);
		
		final VizObjectControlledSettings vizObjectControlledSettings = new VizObjectControlledSettings(vizObjects);
		this.instructionDelay = vizObjectControlledSettings.getInstructionDelay();
		
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				try {
					mainController.setVizObjectControlledSettings(vizObjectControlledSettings);
					mainController.setRuntimeModel(runtimeModel);
				} catch(Exception e) {
					e.printStackTrace();
				}
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
	
	public void setUserBreakpoints(final Set<Integer> breakpoints) {
		runnableQueue.add(new Runnable() {
			public void run() {
				RuntimeController.this.userBreakpoints = new HashSet<>(breakpoints);
			}
		});
	}
	
	/**
	 * Compiles an empty program.
	 */
	private void compileEmpty() {
		runnableQueue.add(new Runnable() {
			public void run() {
				compileSync("");
			}
		});
	}
	
	/**
	 * Compiles the given program.
	 */
	public void compile(final String program) {
		runnableQueue.add(new Runnable() {
			public void run() {
				compileSync(program);
			}
		});
	}
	
	/**
	 * Compiles the given program without adding to the queue.
	 */
	private void compileSync(String program) {
		runtime = new Runtime();
		engine = new Engine(runtime, new ArrayList<Instruction>());
		runningForward = false;
		runningBackward = false;
		maxInstructionsExecutedCount = 0;
		lineBreakpoints.clear();
			
		try {
			List<Instruction> instructions = Engine.compile(program);
			engine = new Engine(runtime, instructions);
			
			if(!instructions.isEmpty()) {
				lineBreakpoints.add(instructions.get(0));
			}
		} catch(CompileException e) {
			runtime.throwError(e.getMessage());
		}
	}
	
	/**
	 * Runs the given command in the current runtime.
	 */
	public void runCommand(final String command) {
		runnableQueue.add(new Runnable() {
			public void run() {
				runCommandSync(command);
			}
		});
	}
	
	/**
	 * Runs the given command in the current runtime without adding to the queue.
	 */
	private void runCommandSync(String command) {
		try {
			List<Instruction> instructions = Engine.compile(command, false);
			runtime.runInstructions(formatCommand(command), instructions);
		} catch(CompileException | ExecutionException e) {
			runtime.throwError(e.getMessage());
		}
	}

	/**
	 * Returns command with a prompt symbol added before each line.
	 */
	private String formatCommand(String command) {
		StringBuilder s = new StringBuilder();
		for(String line:command.trim().split("\n")) {
			s.append("> " + line).append("\n");
		}
		return s.toString().trim();
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
	 * Steps backward to the previous visual change.
	 */
	public void prevVisual() {
		runnableQueue.add(new Runnable() {
			public void run() {
				prevVisualSync();
			}
		});
	}
	
	/**
	 * Steps backward to the previous visual change without adding to the queue.
	 */
	private void prevVisualSync() {
		List<VizObject> vizObjects = runtime.getVizObjects();
		
		while(true) {
			stepBackwardSync();
			if(!VizObjectUtil.equalFiltersAndValues(vizObjects, runtime.getVizObjects())) return;
			if(runtime.atStart()) return;
		}
	}
	
	/**
	 * Steps forward to the next visual change.
	 */
	public void nextVisual() {
		runnableQueue.add(new Runnable() {
			public void run() {
				nextVisualSync();
			}
		});
	}
	
	/**
	 * Steps forward to the next visual change without adding to the queue.
	 */
	private void nextVisualSync() {
		List<VizObject> vizObjects = runtime.getVizObjects();
		
		while(true) {
			stepForwardSync();
			if(!VizObjectUtil.equalFiltersAndValues(vizObjects, runtime.getVizObjects())) return;
			if(runtime.atEnd()) return;
		}
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
				if(runtime.atEnd()) {
					maxInstructionsExecutedCount = runtime.getInstructionsExecutedCount();
					updateUi();
					
					runningForward = false;
					return;
				}
				engine.stepForward();
			}
			
			maxInstructionsExecutedCount = Math.max(maxInstructionsExecutedCount, runtime.getInstructionsExecutedCount());
			
			if(userBreakpoints.contains(runtime.getLineNumber())) {
				runningForward = false;
			}
			
			lineBreakpoints.add(runtime.getInstruction());
		} catch(ExecutionException e) {
			runtime.throwError(e.getMessage());
			runningForward = false;
		}
		
		if(runtime.atEnd()) {
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
			int lineNumber = runtime.getLineNumber();
			do {
				engine.stepBackward();
			
				while(runtime.getCurrentStackFrame() != null && !lineBreakpoints.contains(runtime.getInstruction())) {
					engine.stepBackward();
					if(runtime.atStart()) {
						runningBackward = false;
						return;
					}
				}
			} while(lineNumber == runtime.getLineNumber());
			
			if(userBreakpoints.contains(runtime.getLineNumber())) {
				runningBackward = false;
			}
		} catch(ExecutionException e) {
			runtime.throwError(e.getMessage());
			runningBackward = false;
		}
		
		if(runtime.atStart()) {
			runningBackward = false;
		}
	}
	
	public void setExecutionPoint(final int executionPoint) {
		runnableQueue.add(new Runnable() {
			public void run() {
				while(runtime.getInstructionsExecutedCount() > executionPoint && !runtime.atStart()) {
					stepBackwardSync();
				}
				
				while(runtime.getInstructionsExecutedCount() < executionPoint && !runtime.atEnd()) {
					stepForwardSync();
				}
				
				updateUi();
			}
		});
	}

	public void hover(final int lineNumber, final int characterNumber) {
		runnableQueue.add(new Runnable() {
			public void run() {
				final String value = runtime.getValueAt(lineNumber, characterNumber);
				if(value != null) {
					Display.getDefault().asyncExec(new Runnable() {
						public void run() {
							mainController.setEditorHover(value);
						}
					});
				}
			}
		});
	}
}