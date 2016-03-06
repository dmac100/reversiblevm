package frontend.compiler.brushes;

import java.util.*;
import java.util.regex.Pattern;

import syntaxhighlighter.brush.Brush;
import syntaxhighlighter.brush.RegExpRule;

/**
 * Adapted from: https://gist.github.com/daimatz/3969549#file-shbrushhaskell-js
 */
public class BrushHaskell extends Brush {
	public BrushHaskell() {
		String constants = "True False Nothing Just Left Right LT EQ GT";
		
		String datatypes = "Bool Maybe Either Ordering Char String Int Integer Float Double Rational"
			+ " IO ReadS ShowS FilePath IOError Monad Functor Show Read"
			+ " Eq Ord Enum Bounded Num Real Integral Fractional Floating RealFrac RealFloat";
		
		String functions = "abs acos acosh all and any appendFile applyM asTypeOf asin asinh atan atan2 atanh"
			+ " break catch ceiling compare concat concatMap const cos cosh curry cycle"
			+ " decodeFloat div divMod drop dropWhile elem encodeFloat enumFrom enumFromThen"
			+ " enumFromThenTo enumFromTo error even exp exponent fail filter flip floatDigits"
			+ " floatRadix floatRange floor fmap foldl foldl1 foldr foldr1 fromEnum fromInteger"
			+ " fromIntegral fromRational fst gcd getChar getContents getLine head id init interact"
			+ " ioError isDenormalized isIEEE isInfinite isNaN isNegativeZero iterate last lcm"
			+ " length lex lines log logBase lookup map mapM mapM_ max maxBound maximum maybe min"
			+ " minBound minimum mod negate not notElem null odd or otherwise pi pred print product"
			+ " properFraction putChar putStr putStrLn quot quotRem read readFile readIO readList"
			+ " readLn readParen reads readsPrec realToFrac recip rem repeat replicate return"
			+ " reverse round scaleFloat scanl scanl1 scanr scanr1 seq sequence sequence_ show"
			+ " showChar showList showParen showString shows showsPrec significand signum sin sinh"
			+ " snd span splitAt sqrt subtract succ sum tail take takeWhile tan tanh toEnum"
			+ " toInteger toRational truncate uncurry undefined unlines until unwords unzip unzip3"
			+ " userError words writeFile zip zip3 zipWith zipWith3";
		
		String keywords = "as case of class data default deriving do forall foreign hiding"
			+ " if then else import instance let in mdo module newtype qualified type where";
		
		setRegExpRuleList(Arrays.asList(
			new RegExpRule("\\{-#[\\s\\S]*?#-\\}", "preprocessor"),
			new RegExpRule("--.*", "comments"),
			new RegExpRule("\\{-(?!\\$)[\\s\\S]*?-\\}", Pattern.MULTILINE, "comments"),
			new RegExpRule("'.'", "string"),
			new RegExpRule(RegExpRule.doubleQuotedString, "string"),
			new RegExpRule("(-|!|#|\\$|%|&amp;|\\*|\\+|\\/|&lt;|=|&gt;|\\?|@|\\^|\\||~|:|\\.|\\\\)+", "keyword"),
			new RegExpRule("`[a-z][a-z0-9_']*`", "keyword"),
			new RegExpRule("\\b(\\d+|0x[0-9a-f]+)\\b", Pattern.CASE_INSENSITIVE, "constants"),
			new RegExpRule("\\b\\d+(\\.\\d*)?([eE][+-]?\\d+)?\\b", Pattern.CASE_INSENSITIVE, "constants"),
			new RegExpRule(getKeywords(constants), "color3"),
			new RegExpRule(getKeywords(datatypes), "color3"),
			new RegExpRule(getKeywords(functions), "functions"),
			new RegExpRule(getKeywords(keywords), Pattern.MULTILINE, "keyword")
		));
	}
}