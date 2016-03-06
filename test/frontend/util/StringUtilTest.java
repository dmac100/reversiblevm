package frontend.util;

import static org.junit.Assert.*;

import org.junit.Test;

public class StringUtilTest {
	@Test
	public void noMatch() {
		String match = StringUtil.match("---", "\\w+");
		assertNull(match);
	}
	
	@Test
	public void match() {
		String match = StringUtil.match("abc 123", "\\w+");
		assertEquals("abc", match);
	}
	
	@Test
	public void matchGroup() {
		String match = StringUtil.match("abc 123", "\\w(\\w+)");
		assertEquals("bc", match);
	}

}
