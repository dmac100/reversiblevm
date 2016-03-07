package frontend.compiler;

import java.util.Arrays;
import java.util.regex.Pattern;

import syntaxhighlighter.brush.Brush;
import syntaxhighlighter.brush.RegExpRule;

public class BrushJs extends Brush {
	public static final Pattern multiLineSingleQuotedString = Pattern.compile("'((\\\\.)|([^\\\\']))*+'", Pattern.DOTALL);
	public static final Pattern multiLineDoubleQuotedString = Pattern.compile("\"((\\\\.)|([^\\\\\"]))*+\"", Pattern.DOTALL);
		
	public BrushJs() {
		String keywords = "do else false for function if null return this true var while";
		String functions = "print length charAt concat endsWith indexOf lastIndexOf repeat substring split startsWith toLowerCase toUpperCase trim keys length concat every filter find findIndex indexOf join lastIndexOf map reduce reduceRight some slice sort pop push reverse shift unshift splice";
		
		setRegExpRuleList(Arrays.asList(
			new RegExpRule(Pattern.compile("@[^;]+;?"), "preprocessor"),
			new RegExpRule(multiLineDoubleQuotedString, "string"),
			new RegExpRule(multiLineSingleQuotedString, "string"),
			new RegExpRule(RegExpRule.singleLineCComments, "comments"),
			new RegExpRule(RegExpRule.multiLineCComments, "comments"),
			new RegExpRule(getKeywords(keywords), Pattern.MULTILINE, "keyword"),
			new RegExpRule(getKeywords(functions), Pattern.MULTILINE, "functions")
		));
		
		setCommonFileExtensionList(Arrays.asList("js", "js"));
	}
}
