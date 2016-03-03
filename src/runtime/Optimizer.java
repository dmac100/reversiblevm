package runtime;

import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.instanceOf;
import instruction.Instruction;
import instruction.array.GetElementInstruction;
import instruction.object.GetPropertyInstruction;
import instruction.stack.DupInstruction;
import instruction.stack.PopInstruction;
import instruction.stack.PushInstruction;
import instruction.stack.SwapInstruction;
import instruction.variable.LoadInstruction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.hamcrest.Matcher;

interface Replacement<T> {
	T replace(List<? extends T> matched);
}

public class Optimizer {
	public List<Instruction> optimize(List<Instruction> instructions) {
		instructions = JumpRewriter.convertToLabelledJumps(instructions);
		instructions = optimizeInstructions(instructions);
		instructions = JumpRewriter.convertToOffsetJumps(instructions);
		return instructions;
	}
	
	private List<Instruction> optimizeInstructions(List<Instruction> instructions) {
		//int initialSize = instructions.size();
		
		while(true) {
			List<Instruction> newList = applyReplacements(instructions);
			
			if(newList.equals(instructions)) {
				//System.out.println("Initial Size: " + initialSize + " - New Size: " + instructions.size());
				return instructions;
			}
			
			instructions = newList;
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
		list = replace(list, Arrays.asList(
			anyOf(
				instanceOf(PushInstruction.class),
				instanceOf(LoadInstruction.class)
			),
			instanceOf(PopInstruction.class)
		), new ArrayList<Replacement<Instruction>>());
		
		list = replace(list, Arrays.asList(
			instanceOf(GetPropertyInstruction.class),
			instanceOf(PopInstruction.class)
		), Arrays.asList(ConstantReplacement(new PopInstruction())));
		
		list = replace(list, Arrays.asList(
			instanceOf(LoadInstruction.class),
			instanceOf(PushInstruction.class),
			instanceOf(SwapInstruction.class)
		), Arrays.asList(IndexedReplacement(1), IndexedReplacement(0)));
		
		list = replace(list, Arrays.asList(
			instanceOf(LoadInstruction.class),
			instanceOf(DupInstruction.class),
			instanceOf(GetPropertyInstruction.class),
			instanceOf(PushInstruction.class),
			instanceOf(SwapInstruction.class)
		), Arrays.asList(IndexedReplacement(0), IndexedReplacement(3), IndexedReplacement(0), IndexedReplacement(2)));
		
		list = replace(list, Arrays.asList(
			instanceOf(LoadInstruction.class),
			instanceOf(LoadInstruction.class),
			instanceOf(LoadInstruction.class),
			instanceOf(GetElementInstruction.class),
			instanceOf(SwapInstruction.class)
		), Arrays.asList(IndexedReplacement(1), IndexedReplacement(2), IndexedReplacement(3), IndexedReplacement(0)));
		
		list = replace(list, Arrays.asList(
			instanceOf(LoadInstruction.class),
			instanceOf(LoadInstruction.class),
			instanceOf(LoadInstruction.class),
			instanceOf(GetElementInstruction.class),
			instanceOf(SwapInstruction.class)
		), Arrays.asList(IndexedReplacement(1), IndexedReplacement(2), IndexedReplacement(3), IndexedReplacement(0)));
		
		return list;
	}

	private static <T> List<T> replace(
		List<? extends T> list,
		List<? extends Matcher<? extends Object>> matchers,
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
