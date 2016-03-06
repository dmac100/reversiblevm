// Copyright (c) 2011 Chan Wai Shing
package frontend.compiler.brushes;

import java.util.*;
import java.util.regex.Pattern;

import syntaxhighlighter.brush.Brush;
import syntaxhighlighter.brush.RegExpRule;

/**
 * Scala brush.
 * @author Chan Wai Shing <cws1989@gmail.com>
 */
public class BrushScala extends Brush {

  public static final Pattern multiLineSingleQuotedString = Pattern.compile("'((\\\\.)|([^\\\\']))*+'", Pattern.DOTALL);	
	
  public BrushScala() {
    super();

    // Contributed by Yegor Jbanov and David Bernard.

    String keywords = "val sealed case def true trait implicit forSome import match object null finally super "
            + "override try lazy for var catch throw type extends class while with new final yield abstract "
            + "else do if return protected private this package false";
    String keyops = "[_:=><%#@]+";

    List<RegExpRule> _regExpRuleList = new ArrayList<RegExpRule>();
    _regExpRuleList.add(new RegExpRule(RegExpRule.singleLineCComments, "comments")); // one line comments
    _regExpRuleList.add(new RegExpRule(RegExpRule.multiLineCComments, "comments")); // multiline comments
    _regExpRuleList.add(new RegExpRule(multiLineSingleQuotedString, "string")); // multi-line strings
    // problem: scala should start multiple line string with triple double-quote
    _regExpRuleList.add(new RegExpRule(RegExpRule.multiLineDoubleQuotedString, "string")); // double-quoted string
    _regExpRuleList.add(new RegExpRule(RegExpRule.singleQuotedString, "string")); // strings
    _regExpRuleList.add(new RegExpRule("0x[a-f0-9]+|\\d+(\\.\\d+)?", Pattern.CASE_INSENSITIVE, "value")); // numbers
    _regExpRuleList.add(new RegExpRule(getKeywords(keywords), Pattern.MULTILINE, "keyword")); // keywords
    _regExpRuleList.add(new RegExpRule(keyops, Pattern.MULTILINE, "keyword")); // scala keyword
    setRegExpRuleList(_regExpRuleList);

    setCommonFileExtensionList(Arrays.asList("scl", "scala"));
  }
}
