package backend.util;

public class IntStack {
	private static final int NEGATIVE_OFFSET = 10;
	
	private byte[] values = new byte[1024];
	private int count = 0;
	private int size = 0;
	private int topValue = 0;
	
	public void clear() {
		size = 0;
		topValue = 0;
		count = 0;
	}
	
	public int size() {
		return count;
	}
	
	public void push(int value) {
		ensureCapacity(size + 5);
		
		int storedValue = value - topValue + NEGATIVE_OFFSET;
		topValue = value;
		
		values[size++] = (byte)(storedValue & 0x7F);
		storedValue >>>= 7;
		while(storedValue > 0) {
			values[size++] = (byte)((storedValue & 0x7F) | 0x80);
			storedValue >>>= 7;
		}
		
		count++;
	}
	
	public int pop() {
		int storedValue = (values[--size] & 0x7F);
		while((values[size] & 0x80) > 0) {
			storedValue <<= 7;
			storedValue += (values[--size] & 0x7F);
		}
		
		int value = topValue;
		topValue = topValue - storedValue + NEGATIVE_OFFSET;
		
		count--;
		
		return value;
	}
	
	public int peek() {
		return topValue;
	}
	
	public boolean isEmpty() {
		return size == 0;
	}
	
	private void ensureCapacity(int capacity) {
		if(capacity > values.length) {
			byte[] newValues = new byte[values.length << 1];
			System.arraycopy(values, 0, newValues, 0, values.length);
			values = newValues;
		}
	}
	
	public int getStoredSize() {
		return values.length;
	}
}