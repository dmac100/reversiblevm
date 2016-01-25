package runtime;

import static org.hamcrest.Matchers.isA;
import instruction.GetElementInstruction;
import instruction.GetPropertyInstruction;
import instruction.Instruction;
import instruction.LoadInstruction;
import instruction.PopInstruction;
import instruction.PushInstruction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.hamcrest.Matcher;
import org.hamcrest.Matchers;

interface Replacement<T> {
	T replace(List<? extends T> matched);
}

public class Optimizer {
	public List<Instruction> optimize(List<Instruction> list) {
		//int initialSize = list.size();
		
		while(true) {
			List<Instruction> newList = applyReplacements(list);
			
			if(newList.equals(list)) {
				//System.out.println("Initial Size: " + initialSize + " - New Size: " + list.size());
				return list;
			}
			
			list = newList;
		}
	}
	
	private static Replacement<Instruction> IndexedReplacement(final int index) {
		return new Replacement<Instruction>() {
			public Instruction replace(List<? extends Instruction> matched) {
				return matched.get(index);
			}
		};
	}
	
	private static Replacement<Instruction> ConstantReplacement(final Instruction replacement) {
		return new Replacement<Instruction>() {
			public Instruction replace(List<? extends Instruction> matched) {
				return replacement;
			}
		};
	}

	private static List<Instruction> applyReplacements(List<Instruction> list) {
		list = replace(list,
			Arrays.asList(isA(PushInstruction.class), isA(PopInstruction.class)),
			new ArrayList<Replacement<Instruction>>()
		);
		
		list = replace(list,
			Arrays.asList(isA(LoadInstruction.class), isA(PopInstruction.class)),
			new ArrayList<Replacement<Instruction>>()
		);
		
		list = replace(list,
			Arrays.asList(isA(GetPropertyInstruction.class), isA(PopInstruction.class)),
			Arrays.asList(ConstantReplacement(new PopInstruction()))
		);
		
		return list;
	}

	private static <T> List<T> replace(
		List<? extends T> list,
		List<? extends Matcher<? extends T>> matchers,
		List<? extends Replacement<T>> replacement
	) {
		List<T> newList = new ArrayList<>();
		
		for(int x = 0; x < list.size(); x++) {
			if(matches(list.subList(x, Math.min(list.size(), x + matchers.size())), matchers)) {
				List<T> matched = new ArrayList<>();
				matched.addAll(list.subList(x, x + matchers.size()));
				for(int y = 0; y < replacement.size(); y++) {
					newList.add(replacement.get(y).replace(matched));
				}
				x += matchers.size() - 1;
			} else {
				newList.add(list.get(x));
			}
		}
		
		return newList;
	}
	
	private static <T> boolean matches(List<? extends T> list, List<? extends Matcher<? extends T>> matchers) {
		if(matchers.size() > list.size()) {
			return false;
		}
		
		for(int i = 0; i < matchers.size(); i++) {
			if(!matchers.get(i).matches(list.get(i))) {
				return false;
			}
		}
		
		return true;
	}
}
