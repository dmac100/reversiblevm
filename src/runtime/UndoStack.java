package runtime;

import java.util.ArrayList;
import java.util.List;

public class UndoStack {
	private final List<Runnable> commands = new ArrayList<>();

	public void add(Runnable command) {
		commands.add(command);
	}

	public void undo() {
		while(!commands.isEmpty()) {
			Runnable command = commands.remove(commands.size() - 1);
			if(command == null) {
				return;
			}
			command.run();
		}
	}

	public void saveUndoPoint() {
		commands.add(null);
	}
}
