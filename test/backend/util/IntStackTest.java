package backend.util;

import static org.junit.Assert.assertEquals;

import java.util.Random;

import org.junit.Test;

public class IntStackTest {
	private static final int LARGE_STACK_SIZE = 10_000_000;
	
	@Test
	public void testRandomValues() {
		Random random = new Random();
		
		int[] a = new int[LARGE_STACK_SIZE];
		for(int i = 0; i < a.length; i++) {
			a[i] = random.nextInt();
		}
		
		assertCanPushPopValues(a);
	}
	
	@Test
	public void testMaxValues() {
		assertCanPushPopValues(new int[] {
			0,
			Integer.MAX_VALUE,
			0,
			Integer.MIN_VALUE,
			0
		});
	}
	
	@Test
	public void testZeroValues() {
		int[] a = new int[LARGE_STACK_SIZE];
		for(int i = 0; i < a.length; i++) {
			a[i] = 0;
		}
		
		assertCanPushPopValues(a);
	}
	
	@Test
	public void testPositiveValues() {
		int[] a = new int[LARGE_STACK_SIZE];
		for(int i = 0; i < a.length; i++) {
			a[i] = i;
		}
		
		assertCanPushPopValues(a);
	}
	
	@Test
	public void testNegativeValues() {
		int[] a = new int[LARGE_STACK_SIZE];
		for(int i = 0; i < a.length; i++) {
			a[i] = -i;
		}
		
		assertCanPushPopValues(a);
	}

	private static void assertCanPushPopValues(int[] values) {
		IntStack stack = new IntStack();
		
		for(int i = 0; i < values.length; i++) {
			stack.push(values[i]);
		}
		
		System.out.println("Total size: " + stack.getStoredSize());
		
		for(int i = values.length - 1; i >= 0; i--) {
			assertEquals(values[i], stack.pop());
		}
	}
}