package frontend.compiler;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class JavaLanguageTest {
	Language javaLanguage;
	
	@Before
	public void before() {
		for(Language language:Languages.getLanguages()) {
			if(language.getName().equals("Java")) {
				javaLanguage = language;
			}
		}
	}
	
	@Test
	public void main() {
		String code = "public class Main {\n" +
			"\tpublic static void main(String[] args) {\n" +
			"\t}\n" +
			"}";
		
		assertEquals("Main", javaLanguage.getFileName(code));
	}
	
	@Test
	public void mainWithSpace() {
		String code = "public class  Main2 {\n" +
			"\tpublic  static  void  main ( String[] args ) {\n" +
			"\t}\n" +
			"}";
		
		assertEquals("Main2", javaLanguage.getFileName(code));
	}
	
	@Test
	public void methodContainingMain() {
		String code = "public class Main2 {\n" +
			"\tpublic static void mainExtended(String[] args) {\n" +
			"\t}\n" +
			"}";
		
		assertEquals("Main", javaLanguage.getFileName(code));
	}
	
	@Test
	public void modifiedName() {
		String code = "public class Main2 {\n" +
			"\tpublic static void main(String[] args) {\n" +
			"\t}\n" +
			"}";
		
		assertEquals("Main2", javaLanguage.getFileName(code));
	}
	
	@Test
	public void multipleClasses() {
		String code = "class Main2 {\n" +
			"}\n" +
			"public class Main3 {\n" +
			"\tpublic static void main(String[] args) {\n" +
			"\t}\n" +
			"}";
		
		assertEquals("Main3", javaLanguage.getFileName(code));
	}
}
