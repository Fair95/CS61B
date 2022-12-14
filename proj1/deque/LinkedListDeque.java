package deque;

import java.util.Iterator;

public class LinkedListDeque<T> implements Deque<T>, Iterable<T> {
    private int size;

    private class Node {
        T value;
        Node prev;
        Node next;

        public Node(T value, Node prev, Node next) {
            this.value = value;
            this.prev = prev;
            this.next = next;
        }
    }

    private final Node sentinel;

    public LinkedListDeque() {
        sentinel = new Node(null, null, null);
        sentinel.prev = sentinel;
        sentinel.next = sentinel;
        size = 0;
    }

    @Override
    public void addFirst(T item) {
        Node newNode = new Node(item, sentinel, null);
        newNode.next = sentinel.next;
        if (sentinel.next != null) {
            sentinel.next.prev = newNode;
        }
        sentinel.next = newNode;
        size++;
    }

    @Override
    public void addLast(T item) {
        Node newNode = new Node(item, null, sentinel);
        newNode.prev = sentinel.prev;
        if (sentinel.prev != null) {
            sentinel.prev.next = newNode;
        }
        sentinel.prev = newNode;
        size++;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void printDeque() {
        if (this.isEmpty()) System.out.print("\n");
        Node start = sentinel.next;
        while (start != sentinel) {
            System.out.print(start.value + " ");
            start = start.next;
        }
        System.out.print("\n");
    }

    @Override
    public T removeFirst() {
        if (this.isEmpty()) return null;
        Node returnNode = sentinel.next;
        sentinel.next = returnNode.next;
        returnNode.next.prev = sentinel;
        size--;
        return returnNode.value;
    }

    @Override
    public T removeLast() {
        if (this.isEmpty()) return null;
        Node returnNode = sentinel.prev;
        sentinel.prev = returnNode.prev;
        returnNode.prev.next = sentinel;
        size--;
        return returnNode.value;
    }

    @Override
    public T get(int index) {
        if (index >= this.size()) return null;
        Node start = sentinel.next;
        for (int i = 0; i < index; i++) {
            start = start.next;
        }
        return start.value;
    }

    private T recursiveCall(Node n, int index) {
        if (index == 0) return n.value;
        return recursiveCall(n.next, index - 1);
    }

    public T getRecursive(int index) {
        if (index >= this.size()) return null;
        return recursiveCall(sentinel.next, index);

    }

    private class LinkedListDequeIterator implements Iterator<T> {
        Node ptr;

        public LinkedListDequeIterator() {
            ptr = sentinel;
        }

        @Override
        public boolean hasNext() {
            return ptr.next != null;
        }

        @Override
        public T next() {
            ptr = ptr.next;
            return ptr.value;
        }
    }

    public Iterator<T> iterator() {
        return new LinkedListDequeIterator();
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

