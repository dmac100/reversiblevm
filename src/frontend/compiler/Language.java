package frontend.compiler;

import java.io.File;
import java.io.IOException;

import syntaxhighlighter.brush.Brush;
import frontend.util.StringUtil;

public class Language {
	private String name;
	private String extension;
	private Brush brush;
	private String compiler;
	private String run;
	private String filenameMatcher;
	private String template;
	private String defaultInput;
	private String standardImportJar;
	private String defaultClasspath;

	/**
	 * Creates a new language from the paramters. Compiler, run, filenameMatcher, defaultInput, and standardImportJar are optional.
	 * Commandlines are split by spaces and then variable substitutions are performed on $NAME, $EXT, and $CLASSPATH.
	 * @param name the name of the programming language.
	 * @param extension the file extension used for this language.
	 * @param brush the name of the brush to use for syntax highlighting such as 'BrushPlain'. Must exist in the JavaSyntaxHighlighter jar.
	 * @param compiler the commandline to compile the program, or null if no compilation is necessary.
	 * @param run the commandline to run the program, or null if the program is run by executing it directly.
	 * @param filenameMatcher the pattern to use to detect the filename from the file contents, or null for a default name.
	 * @param template the initial contents for the source code template.
	 * @param defaultInput the initial contents for the program input, or null if there is no default input.
	 * @param standardImportJar the jar that contains the libraries for the standard imports.
	 * @param defaultClasspath the classpath to use if it is not specified elsewhere.
	 */
	public Language(String name, String extension, Brush brush, String compiler, String run,
			String filenameMatcher, String template, String defaultInput, String standardImportJar, String defaultClasspath) {
		
		this.name = name;
		this.extension = extension;
		this.brush = brush;
		this.compiler = compiler;
		this.run = run;
		this.filenameMatcher = filenameMatcher;
		this.template = template;
		this.defaultInput = defaultInput;
		this.standardImportJar = standardImportJar;
		this.defaultClasspath = defaultClasspath;
	}
	
	/**
	 * Starts and returns a process for a compiler, or null if no compilation is needed.
	 * @param dir the directory to run in.
	 * @param name the name of the file excluding the extension.
	 * @param classpath the Java classpath.
	 */
	public Process createCompiler(File dir, String name, String classpath) throws IOException {
		if(compiler == null) {
			return null;
		}
		
		return createProcess(dir, name, compiler, classpath);
	}

	/**
	 * Starts and returns a process to run a file in this language.
	 * @param dir the directory to run in.
	 * @param name the name of the file excluding the extension.
	 * @param classpath the Java classpath.
	 */
	public Process runProgram(File dir, String name, String classpath) throws IOException {
		if(run == null) {
			return new ProcessBuilder()
				.directory(dir)
				.command(new File(dir, "main").getPath())
				.start();
		} else {
			return createProcess(dir, name, run, classpath);
		}
	}

	/**
	 * Starts and returns a new process.
	 * @param dir the directory to run in.
	 * @param name the name of the file excluding the extension.
	 * @param line the commandline to execute. The line is separated by spaces and then
	 *             variable substitutions are performed on $NAME, $EXT, and $CLASSPATH.
	 * @param classpath the Java classpath.
	 */
	private Process createProcess(File dir, String name, String line, String classpath) throws IOException {
		String[] args = line.split(" ");
		for(int i = 0; i < args.length; i++) {
			args[i] = args[i]
				.replaceAll("\\$CLASSPATH", classpath)
				.replaceAll("\\$NAME", name)
				.replaceAll("\\$EXT", extension);
		}
		
		return new ProcessBuilder()
			.directory(dir)
			.command(args)
			.start();
	}
	
	/**
	 * Returns the name of the language.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns the file extension of the language.
	 */
	public String getExtension() {
		return extension;
	}

	/**
	 * Returns the template to use as the default source code for the language.
	 */
	public String getTemplate() {
		return template;
	}
	
	/**
	 * Returns the default input to use, or null if there is no default input. 
	 */
	public String getDefaultInput() {
		return defaultInput;
	}

	/**
	 * Returns the brush to use for syntax highlighting.
	 */
	public Brush getBrush() {
		return brush;
	}
	
	/**
	 * Returns the path to the standard import jar, or null if there isn't any.
	 */
	public String getStandardImportJar() {
		return standardImportJar;
	}

	/**
	 * Returns the default classpath.
	 */
	public String getDefaultClasspath() {
		return defaultClasspath;
	}

	/**
	 * Returns the detected filename, excluding extension, from the file contents
	 * or a default if it can't be detected.
	 */
	public String getFileName(String contents) {
		if(filenameMatcher == null) {
			return "Main";
		} else {
			String match = StringUtil.match(contents, filenameMatcher);
			return (match == null) ? "Main" : match;
		}
	}
}