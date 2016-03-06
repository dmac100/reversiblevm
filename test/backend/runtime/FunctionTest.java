package backend.runtime;

import static backend.runtime.EngineAsserts.assertOutput;

import org.junit.Test;

public class FunctionTest {
	@Test
	public void thisValues() {
		assertOutput("{f:[Function]}", "a = { f: function() { print(this); } }; a.f();");
		assertOutput("[[Function]]", "f = [function() { print(this); }]; f[0]();");
		assertOutput("[[Function]]", "f = [[function() { print(this); }]]; f[0][0]();");
		assertOutput("null", "a = { f: function() { print(this); } }; var g = a.f; g();");
		assertOutput("[[Function]]", "a = { f: [function() { print(this); }] }; a.f[0]();");
	}
}