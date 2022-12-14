package deque;

import java.util.Iterator;

public class ArrayDeque<T> implements Deque<T>, Iterable<T> {
	private int size;
	private int front;
	private int back;
	private int capacity;
	private T[] arr;

	public ArrayDeque() {
		size = 0;
		// front and back must point to non-occupied index,
		// otherwise we do know to start at front + 1 or front
		front = 0;
		back = 1;
		capacity = 8;
		arr = (T[]) new Object[capacity];
	}

	private void extendArray() {
		int newCapacity = capacity * 2;
		T[] newArr = (T[]) new Object[newCapacity];
		// When front + 1 will wrap around, we only need to copy once
		//  start        end
		//    |           |
		//    x x x x x x x front(back)
		if (front + 1 == capacity) {
			// We put it at pos 1 cuz front sits at 0 in extended arr
			System.arraycopy(arr, 0, newArr, 1, size);
		} else {
			// When front + 1 will not immediately wrap around, we need two copy steps
			// 2nd start         2nd end     1st start       1st end
			// |                   |             |             |
			// x         x x x x x x front(back) x      x x    x
			// copy from front + 1 to the end
			// We put it at pos 1 cuz front sits at 0 in extended arr
			System.arraycopy(arr, front + 1, newArr, 1, arr.length - (front + 1));
			// copy the rest from 0 to back-1 (because back pointing to non-occupied index)
			// We start at arr.length - (front+1) + 1 = arr.length - front
			System.arraycopy(arr, 0, newArr, arr.length - front, back);
		}

		arr = newArr;
		capacity = newCapacity;
		// make sure front and back pointing to non-occupied index
		front = 0;
		back = size + 1;
		//System.out.println("Expanded-> capacity:" + this.capacity + " size:"+this.size());
	}

	@Override
	public void addFirst(T item) {
		arr[front] = item;
		front = (front == 0) ? capacity - 1 : front - 1;
		size++;
		// This is when front and back meet each other
		if (size == capacity - 1) {
			extendArray();
		}
	}

	@Override
	public void addLast(T item) {
		arr[back] = item;
		back = (back == capacity - 1) ? 0 : back + 1;
		size++;
		// This is when front and back meet each other
		if (size == capacity - 1) {
			extendArray();
		}
	}

	@Override
	public int size() {
		return size;
	}

	@Override
	public void printDeque() {
		for (int i = 0; i < size; i++) {
			System.out.print(arr[(front + 1 + i) % capacity] + " ");
		}
		System.out.print("\n");
	}

	private void shrinkArray() {
		int newCapacity = capacity / 2;
		T[] newArr = (T[]) new Object[newCapacity];
		// When front + 1 will wrap around, we only need to copy once
		//  start        end
		//    |           |
		//    x x x x x x x back _ _ ... _ _ front
		if (front + 1 > capacity) {
			System.arraycopy(arr, 0, newArr, 1, size);
		} else if (front < back) {
			//                start         end
			//                  |            |
			//    _ _ ... _ _ front x x x x back _ _ ... _ _
			System.arraycopy(arr, front + 1, newArr, 1, size);
		} else {
			// When front + 1 will not immediately wrap around, we need two copy steps
			// 2nd start         2nd end           1st start       1st end
			// |                   |                   |             |
			// x         x x x x x x back _ _ ... _ _ front  x x      x    x
			// We put it at pos 1 cuz front sits at 0 in extended arr
			System.arraycopy(arr, front + 1, newArr, 1, arr.length - (front + 1));
			// copy the rest from 0 to back-1 (because back pointing to non-occupied index)
			// We start at arr.length - (front+1) + 1 = arr.length - front
			System.arraycopy(arr, 0, newArr, arr.length - front, back);
		}
		arr = newArr;
		capacity = newCapacity;
		// make sure front and back pointing to non-occupied index
		front = 0;
		back = size + 1;
		//System.out.println("Shrinked-> capacity:" + this.capacity + " size:"+this.size());
	}

	@Override
	public T removeFirst() {
		if (this.isEmpty()) return null;
		front = (front == capacity - 1) ? 0 : front + 1;
		T returnValue = arr[front];
		size--;
		if (size < capacity / 4 && size > 16) {
			shrinkArray();
		}
		return returnValue;
	}

	@Override
	public T removeLast() {
		if (this.isEmpty()) return null;
		back = (back == 0) ? capacity - 1 : back - 1;
		T returnValue = arr[back];
		size--;
		if (size < capacity / 4 && size >= 16) {
			shrinkArray();
		}
		return returnValue;
	}

	private class ArrayDequeIterator implements Iterator<T> {
		private int loc;

		ArrayDequeIterator() {
			loc = 0;
		}

		@Override
		public boolean hasNext() {
			return loc < size;
		}

		@Override
		public T next() {
			T returnValue = get(loc);
			loc++;
			return returnValue;
		}
	}

	@Override
	public T get(int index) {
		if (index >= size) return null;
		return arr[(front + 1 + index) % capacity];
	}

	public Iterator<T> iterator() {
		return new ArrayDequeIterator();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o instanceof Deque) {
			Deque<T> dq = (Deque<T>) o;
			if (this.size() != dq.size()) return false;
			for (int i = 0; i < dq.size(); i++) {
				if (!dq.get(i).equals(this.get(i))) return false;
			}
			return true;
		}
		return false;
	}
}
