package backend.util;

import java.util.ArrayList;
import java.util.List;

public class IntStack {
	private static final int NEGATIVE_OFFSET = 50;
	
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
		count++;
		
		ensureCapacity(size + 6);
		
		// Store value as an offset of the last value.
		int storedValue = value - topValue + NEGATIVE_OFFSET;
		topValue = value;
		
		// Apply run-length encoding. The 7th bit being set indicates the first 6 bits
		// represent the number of times the value given in the first 6 bits of the last
		// byte is repeated.
		if(storedValue < 0x40 && size > 0) {
			if((values[size - 1] & 0xC0) == 0) {
				if((values[size - 1] & 0x3F) == storedValue) {
					// Save first repeated value.
					values[size++] = 0x40;
					return;
				}
			} else if((values[size - 1] & 0x80) == 0 && size > 1) {
				if((values[size - 2] & 0x3F) == storedValue) {
					if(values[size - 1] < 0x7F) {
						// Increment repeated value.
						values[size - 1] += 1;
						return;
					}
				}
			}
		}
		
		// Variable-length encoding. The 8th bit being set indicates that there is another
		// byte for this value.
		values[size++] = (byte)(storedValue & 0x7F);
		storedValue >>>= 7;
		while(storedValue > 0) {
			values[size++] = (byte)((storedValue & 0x7F) | 0x80);
			storedValue >>>= 7;
		}
		
		// Ensure that the 7th is not set in the last byte without run-length encoding.
		if((values[size - 1] & 0x40) > 0) {
			values[size++] = (byte) 0x80;
		}
	}
	
	public int pop() {
		count--;
		
		int storedValue;
		
		if((values[size - 1] & 0x40) > 0) {
			// Read run-length encoding.
			storedValue = values[size - 2];
			if((values[size - 1] & 0x3F) == 0) {
				size--;
			} else {
				values[size - 1]--;
			}
		} else {
			// Read variable length value.
			storedValue = (values[--size] & 0x7F);
			while((values[size] & 0x80) > 0) {
				storedValue <<= 7;
				storedValue += (values[--size] & 0x7F);
			}
		}
		
		// Apply offset.
		int value = topValue;
		topValue = topValue - storedValue + NEGATIVE_OFFSET;
		
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
	
	protected List<String> getBinaryValues() {
		List<String> binaryValues = new ArrayList<>();
		for(int i = 0; i < size; i++) {
			binaryValues.add(Integer.toBinaryString(values[i] & 0xFF));
		}
		return binaryValues;
	}
	
	public int getStoredSize() {
		return values.length;
	}
}