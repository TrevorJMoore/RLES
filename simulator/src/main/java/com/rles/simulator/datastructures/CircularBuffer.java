package com.rles.simulator.datastructures;

// A simple generic circular buffer to store the last N records
// If the buffer is full, overwrite the oldest element.
public class CircularBuffer<T> {

	private final Object[] buffer;
	
	// Number of elements in the buffer
	private int size = 0;
	
	// Pointers to head and tail of array
	private int head = 0;
	private int tail = 0;
	
	// Create a CircularBuffer of a set capacity
	public CircularBuffer(int capacity) {
		if (capacity <= 0) {
			throw new IllegalArgumentException("Capacity must be non-negative or greater than 0.");
		}
		buffer = new Object[capacity];
	}
	

	// Add an element and overwrite if the buffer is full
	public void add(T element) {
		buffer[tail] = element;
		tail = (tail + 1) % buffer.length;
		
		// If the current size has reached our buffer length
		if (size == buffer.length) {
			// Push the head forward (causing the oldest record to drop).
			head = (head + 1) % buffer.length;
		} else {
			// Otherwise, increment size.
			size++;
		}
	}
	
	// Get record by oldest (head)
	@SuppressWarnings("unchecked")
	public T getOldest(int index) {
		if (index < 0 || index >= size) {
			throw new IndexOutOfBoundsException("Index of " + index + ", falls outside current size " + size);
		}
		
		return (T) buffer[(head+index) % buffer.length];
	}
	
	// Get record by newest (tail)
	@SuppressWarnings("unchecked")
	public T getNewest(int index) {
		if (index < 0 || index >= size) {
			throw new IndexOutOfBoundsException("Index of " + index + ", falls outside current size " + size);
		}
		// Tail points to next write position so newest element is at "tail - 1 - index".
		// Likewise, if tail is at the beginning, we can go negative.
		// Adding buffer.length guarantees a positive number.
		return (T) buffer[(tail - 1 - index + buffer.length) % buffer.length];
	}
	
	public int size() { return size; }
	public int capacity() { return buffer.length; }
	public boolean isFull() { return size == buffer.length; }
	public boolean isEmpty() { return size == 0; }
	
}
