package frontend.ui;

import java.util.*;

import com.google.common.collect.Lists;

public class ConsoleCompletion {
	private String completionPrefix = "";
	private String lastCompletion = "";
	private LinkedHashSet<String> words = new LinkedHashSet<String>();

	public void setHistory(List<String> history) {
		int skip = Math.max(0, history.size() - 1000);
		
		for(String s:history) {
			if(skip > 0) {
				skip--;
				continue;
			}
			
			for(String w:s.split("\\W+")) {
				words.add(w);
			}
		}
	}

	public String getCompletion(String text) {
		String prefix = text.replaceAll("\\w*$", "");
		String suffix = text.substring(prefix.length());
		
		List<String> list = Lists.reverse(Lists.newArrayList(words));
		
		if(!words.contains(completionPrefix)) {
			list.add(completionPrefix);
		}
		
		list.addAll(Lists.newArrayList(list));
		
		if(completionPrefix.isEmpty()) {
			completionPrefix = suffix;
		}

		boolean startCompletion = false;
		for(String word:list) {
			if(word.toLowerCase().startsWith(completionPrefix.toLowerCase())) {
				if(startCompletion) {
					lastCompletion = word;
					return prefix + word;
				}
				
				if(word.equals(lastCompletion) || lastCompletion.isEmpty()) {
					if(lastCompletion.isEmpty()) {
						lastCompletion = word;
						return prefix + word;
					}
					startCompletion = true;
				}
			}
		}
		
		return text;
	}

	public void dismiss() {
		completionPrefix = "";
		lastCompletion = "";
		words.clear();
	}
}