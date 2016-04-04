package frontend.ui;

import java.util.*;

import com.google.common.collect.Lists;

public class ConsoleCompletion {
	private String completionPrefix = "";
	private String lastCompletion = "";
	private LinkedHashSet<String> words = new LinkedHashSet<>();

	public void setHistory(String prefix, List<String> history) {
		int skip = Math.max(0, history.size() - 1000);

		LinkedHashSet<String> prefixSet = new LinkedHashSet<>();
		LinkedHashSet<String> suffixSet = new LinkedHashSet<>();
		
		// Add recent words in history to suffixSet.
		for(String s:history) {
			if(skip > 0) {
				skip--;
				continue;
			}
			
			for(String w:s.split("\\W+")) {
				suffixSet.add(w);
			}
		}
		
		// Add prefix words not in suffixSet to prefixSet.
		for(String w:prefix.split("\\W+")) {
			if(!suffixSet.contains(w)) {
				prefixSet.add(w);
			}
		}
		
		words.clear();
		words.addAll(prefixSet);
		words.addAll(suffixSet);
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