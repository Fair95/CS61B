package deque;

public class LinkedListDeque<T> {
    private int size;
    private class Node {
        T value;
        Node prev;
        Node next;
        public Node (T value, Node prev, Node next){
            this.value = value;
            this.prev = prev;
            this.next= next;
        }
    }
    private Node sentinel;
    public LinkedListDeque(){
        sentinel = new Node(null, null, null);
        sentinel.prev = sentinel.next;
        sentinel.next = sentinel.prev;
        size = 0;
    }
    public void addFirst(T item){
        Node newNode = new Node(item, sentinel, null);
        newNode.next = sentinel.next;
        sentinel.next.prev = newNode;
        sentinel.next = newNode;
        size++;
    }
    public void addLast(T item){
        Node newNode = new Node(item, null, sentinel);
        newNode.prev = sentinel.prev;
        sentinel.prev.next = newNode;
        sentinel.prev = newNode;
        size++;
    }
    public boolean isEmpty() {return size == 0;}
    public int size(){return size;}
    public void printDeque(){
        if (this.isEmpty()) System.out.print("\n");
        Node start = sentinel.next;
        while (start != sentinel){
            System.out.print(start.value + " ");
            start = start.next;
        }
        System.out.print("\n");
    }
    public T removeFirst(){
        if (this.isEmpty()) return null;
        Node returnNode = sentinel.next;
        sentinel.next = returnNode.next;
        returnNode.next.prev = sentinel;
        size --;
        return returnNode.value;
    }
    public T removeLast(){
        if (this.isEmpty()) return null;
        Node returnNode = sentinel.prev;
        sentinel.prev = returnNode.prev;
        returnNode.prev.next = sentinel;
        size --;
        return returnNode.value;
    }
    public T get(int index){
        if (index >= this.size()) return null;
        Node start = sentinel.next;
        for (int i = 0; i < index; i++){
            start = start.next;
        }
        return start.value;
    }

    private T recursiveCall(Node n, int index ){
        if (index == 0) return n.value;
        return recursiveCall(n.next, index-1);
    }
    public T getRecursive(int index){
        if (index >= this.size()) return null;
        return recursiveCall(sentinel.next, index);

    }
}

class LinkedTest {
    public static void main(String args[]) {
        LinkedListDeque lld = new LinkedListDeque();

    }
}
