package frontend.compiler;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.commons.io.FileUtils;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import frontend.ui.Callback;

public class Compiler {
	private static ExecutorService executor = Executors.newCachedThreadPool(new ThreadFactoryBuilder().setDaemon(true).build());
	
	private final Language language;
	private final String classpath;
	
	private static class StreamReader implements Runnable {
		private BufferedReader reader;
		private Appender appender;
		private Appender info;
		private String name;
		
		public StreamReader(String name, InputStream inputStream, Appender appender, Appender info) {
			this.name = name;
			this.reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
			this.appender = appender;
			this.info = info;
		}
		
		public void run() {
			try {
				String line;
				while((line = reader.readLine()) != null) {
					appender.append(line + "\n");
				}
			} catch(IOException e) {
				info.append("ERROR: IOException reading stream: " + name + "\n");
				e.printStackTrace();
			}
		}
	}
	
	public Compiler(Language language, String classpath) {
		this.language = language;
		this.classpath = classpath;
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
		File dir = null;
		Process compilerProcess = null;
		Process runProcess = null;
		
		try {
			String name = language.getFileName(contents);
			
			dir = Files.createTempDirectory("scratchpad").toFile();
			File source = new File(dir, name + "." + language.getExtension());
			
			FileUtils.write(source, contents, StandardCharsets.UTF_8);
			
			compilerProcess = language.createCompiler(dir, name, classpath);
			if(compilerProcess != null) {
				Future<?> outFuture = executor.submit(new StreamReader("Compiler Output", compilerProcess.getInputStream(), out, info));
				Future<?> errFuture = executor.submit(new StreamReader("Compiler Error", compilerProcess.getErrorStream(), err, info));
				
				// Wait for program to exit and all output to be read.
				outFuture.get();
				errFuture.get();
				int result = compilerProcess.waitFor();
				
				compilerProcess = null;
				if(result != 0) {
					return;
				}
			}
			
			runProcess = language.runProgram(dir, name, classpath);
			
			Future<?> outFuture = executor.submit(new StreamReader("Output", runProcess.getInputStream(), out, info));
			Future<?> errFuture = executor.submit(new StreamReader("Error", runProcess.getErrorStream(), err, info));

			try(BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(runProcess.getOutputStream(), StandardCharsets.UTF_8))) {
				writer.append(input);
			}
			
			// Wait for program to exit and all output to be read.
			outFuture.get();
			errFuture.get();
			int exitValue = runProcess.waitFor();
			
			if(exitValue != 0) {
				err.append("Exited with error value: " + exitValue);
			}
			
			runProcess = null;
		} catch(Exception e) {
			info.append("ERROR: Exception running program: " + e.getMessage() + "\n");
			e.printStackTrace();
		} finally {
			if(compilerProcess != null) {
				compilerProcess.destroy();
			}
			
			if(runProcess != null) {
				runProcess.destroy();
			}
			
			if(dir != null) {
				try {
					FileUtils.deleteDirectory(dir);
				} catch (IOException e) {
				}
			}
			
			finishedCallback.onCallback(null);
		}
	}
}
