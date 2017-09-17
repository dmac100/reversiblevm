package integration;

import static org.apache.commons.lang3.StringEscapeUtils.escapeEcmaScript;
import static org.apache.commons.lang3.StringUtils.join;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.Charsets;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import backend.instruction.Instruction;
import backend.runtime.Engine;
import backend.runtime.Runtime;
import backend.runtime.VizObject;
import backend.util.VizObjectUtil;
import rufus.lzstring4java.LZString;

public class HtmlExporter {
	private Map<Object, Integer> keyMap = new HashMap<>();
	
	/**
	 * Converts a program into an html file by exporting the data generated from
	 * it into a html template.
	 */
	public void exportHtml(String program, File target) throws IOException {
		String data = getProgramJsonObject(program);
		String compressedData = LZString.compressToBase64(data.toString());
		String javascriptVariable = toJavascriptVariable(compressedData);
		
		try(InputStream inputStream = getClass().getResourceAsStream("/integration/htmlExport.html")) {
			String targetContents = IOUtils.toString(inputStream, Charsets.UTF_8);
			targetContents = targetContents.replace("[DATA]", javascriptVariable);
			FileUtils.write(target, targetContents);
		}
	}
	
	/**
	 * Converts a string into a Javascript snippet that writes it to a variable. 
	 */
	private String toJavascriptVariable(String data) {
		StringBuilder javascriptVariable = new StringBuilder();
		javascriptVariable.append("var data = ''\n");
		for(int i = 0; i < data.length(); i += 120) {
			String line = data.substring(i, Math.min(i + 120, data.length()));
			javascriptVariable.append("data += '" + line + "'\n");
		}
		return javascriptVariable.toString();
	}
	
	/**
	 * Returns all the data from a program as a JSON object.
	 */
	private String getProgramJsonObject(String program) {
		Runtime runtime = new Runtime();
		List<Instruction> instructions = Engine.compile(program);
		Engine engine = new Engine(runtime, instructions);
		
		List<VizObject> vizObjects = runtime.getVizObjects();
		
		StringBuilder data = new StringBuilder("[");
		
		while(!runtime.atEnd()) {
			int lineNumber = runtime.getLineNumber();
			
			do {
				engine.stepForward();
				if(runtime.atEnd()) {
					break;
				}
			} while(runtime.getLineNumber() <= 0 || runtime.getLineNumber() == lineNumber);
			
			List<VizObject> newVizObjects = runtime.getVizObjects();
			if(!VizObjectUtil.equalFiltersAndValues(vizObjects, newVizObjects)) {
				vizObjects = newVizObjects;
				
				String jsObjects = "[" + join(getJsonObjects(vizObjects), ",") + "]\n";
				if(data.length() > 1) {
					data.append(",");
				}
				data.append(jsObjects);
			}
		}
		
		data.append("]");
		
		return data.toString();
	}

	/**
	 * Returns a list of vizObjects as a JSON object.
	 */
	private List<String> getJsonObjects(List<VizObject> vizObjects) {
		List<String> properties = new ArrayList<>();
		for(VizObject vizObject:vizObjects) {
			properties.add("{" + join(getJsonObject(vizObject), ", ") + "}");
		}
		return properties;
	}

	/**
	 * Returns a vizObject as a JSON object.
	 */
	private List<String> getJsonObject(VizObject vizObject) {
		List<String> properties = new ArrayList<>();
		String name = vizObject.getName();
		properties.add("\"key\": \"" + escapeEcmaScript(getKey(vizObject.getKey())) + "\"");
		properties.add("\"name\": \"" + escapeEcmaScript(name) + "\"");
		for(String property:vizObject.getPropertyNames()) {
			String value = vizObject.getProperty(property).toString();
			properties.add("\"" + escapeEcmaScript(property) + "\": \"" + escapeEcmaScript(value) + "\"");
		}
		return properties;
	}

	/**
	 * Returns key as a String, where equal keys have the same String and different keys have different Strings.
	 */
	private String getKey(Object key) {
		if(keyMap.containsKey(key)) {
			return keyMap.get(key).toString();
		} else {
			Integer value = keyMap.size() + 1;
			keyMap.put(key, value);
			return value.toString();
		}
	}
}