package frontend.compiler;

import java.io.*;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Importer {
	private List<String> jars;

	public Importer(List<String> jars) {
		this.jars = jars;
	}

	public Set<String> findImports(String className) {
		Set<String> imports = new TreeSet<String>(new Comparator<String>() {
			public int compare(String fullName1, String fullName2) {
				String simpleName1 = fullName1.replaceAll(".*\\.", "");
				String simpleName2 = fullName2.replaceAll(".*\\.", "");
				
				if(simpleName1.equalsIgnoreCase(simpleName2)) {
					return fullName1.compareToIgnoreCase(fullName2);
				} else {
					return simpleName1.compareToIgnoreCase(simpleName2);
				}
			}
		});

		for(String jar:jars) {
			for(String name:readClassesInJar(jar)) {
				if(name.contains("$")) continue;
				
				String localName = name.replaceAll(".*\\.", "");
				
				if(localName.toLowerCase().startsWith(className.toLowerCase())) {
					imports.add(name);
				}
			}
		}
		
		return imports;
	}

	private List<String> readClassesInJar(String jar) {
		List<String> classes = new ArrayList<String>();
		if(jar == null) return classes;
		
		try(ZipInputStream inputStream = new ZipInputStream(new FileInputStream(jar))) {
			ZipEntry entry;
			while((entry = inputStream.getNextEntry()) != null) {
				String name = entry.getName();
				if(name.endsWith(".class")) {
					name = name.replaceAll("\\.class$", "");
					name = name.replaceAll("[\\/]", ".");
					classes.add(name);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return classes;
	}
	
	/**
	 * Returns a list of absolute paths to all the jars specified in the given classpath.
	 */
	public static List<String> getJarsInClasspath(String classpath) {
		List<String> jars = new ArrayList<String>();
		for(String path:classpath.split("[;:]")) {
			if(path.toLowerCase().endsWith(".jar")) {
				// Add any classpath entries that are jar files.
				jars.add(new File(path).getAbsolutePath());
			} else if(path.endsWith("*")) {
				// Expand glob to contain all jar files in its parent directory.
				File[] files = new File(path.replaceAll("\\*.*", "")).listFiles();
				if(files != null) {
					for(File file:files) {
						if(file.getName().toLowerCase().endsWith(".jar")) {
							jars.add(file.getAbsolutePath());
						}
					}
				}
			}
		}
		return jars;
	}
}
