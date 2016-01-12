import runtime.Engine;

public class Main {
	public static void main(String[] args) {
		new Engine().run(Engine.parseFile("/runtime/main.js"));
	}
}
