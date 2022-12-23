package bstmap;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Stack;

public class BSTMap<K extends Comparable<K>, V> implements Map61B<K, V> {
    private class BSTNode {
        V value;
        K key;
        BSTNode left;
        BSTNode right;

        BSTNode(K key, V value) {
            this.key = key;
            this.value = value;
            this.left = null;
            this.right = null;
        }

        public BSTNode get(BSTNode node, K k) {
            if (node == null || node.key.equals(k)) {
                return node;
            } else if (node.key.compareTo(k) > 0) {
                return get(node.left, k);
            } else {
                return get(node.right, k);
            }
        }

        public BSTNode insert(BSTNode node, K k, V v) {
            if (node == null){
                size += 1;
                return new BSTNode(k, v);
            } else if (node.key.compareTo(k) > 0) {
                node.left = insert(node.left, k, v);
            } else if (node.key.compareTo(k) < 0) {
                node.right = insert(node.right, k, v);
            } else{
                node.value = v;
            }
            return node;
        }

        private BSTNode swapSmallest(BSTNode toDelete, BSTNode swapTarget) {
            // This is the hard part! Think thoroughly how this works
            if (swapTarget.left != null){
                // set the left child of the current node. Will not change unless the successor node is found
                swapTarget.left = swapSmallest(toDelete, swapTarget.left);
                // return itself
                return swapTarget;
            } else {
                toDelete.key = swapTarget.key;
                toDelete.value = swapTarget.value;
                // the right child of the swapped node must be the left child of its parent
                return swapTarget.right;
            }
        }
        public BSTNode delete(BSTNode node, K k){
            if (node == null){
                return null;
            }
            if (node.key.compareTo(k) > 0) {
                node.left = delete(node.left, k);
            } else if (node.key.compareTo(k) < 0) {
                node.right = delete(node.right, k);
            } else if (node.left == null) {
                size -= 1;
                return node.right;
            } else if (node.right == null) {
                size -= 1;
                return node.left;
            } else {
                size -= 1;
                node.right = swapSmallest(node, node.right);
            }
            return node;
        }
        public BSTNode delete(BSTNode node, K k, V v){
            if (node == null || !node.value.equals(v)){
                return null;
            }
            if (node.key.compareTo(k) > 0) {
                node.left = delete(node.left, k);
            } else if (node.key.compareTo(k) < 0) {
                node.right = delete(node.right, k);
            } else if (node.left == null) {
                size -= 1;
                return node.right;
            } else if (node.right == null) {
                size -= 1;
                return node.left;
            } else {
                node.right = swapSmallest(node, node.right);
                size -= 1;
            }
            return node;
        }

        @Override
        public String toString(){
            return String.format("{key: %s, value: %s}", key, value);
        }

    }
    private BSTNode root = null;
    private int size = 0;

    @Override
    public void clear() {
        root = null;
        size = 0;
    }

    @Override
    public boolean containsKey(K key) {
        if (root == null) {
            return false;
        }
        return root.get(root, key) != null;
    }

    @Override
    public V get(K key) {
        if (root == null) {
            return null;
        }
        BSTNode result = root.get(root, key);
        return result != null ? result.value : null;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void put(K key, V value) {
        if (root == null) {
            size += 1;
            root = new BSTNode(key, value);
        }
        root = root.insert(root, key, value);
    }

    private void dfsKeySet(Set<K> set, BSTNode node){
        if (node != null) {
            dfsKeySet(set, node.left);
            set.add(node.key);
            dfsKeySet(set, node.right);
        }
    }
    @Override
    public Set<K> keySet() {
        Set<K> set = new HashSet<>();
        dfsKeySet(set, root);
        return set;
    }

    @Override
    public V remove(K key) {
        if (root == null){
            return null;
        }
        BSTNode toRemove = root.get(root, key);
        if (toRemove == null){
            return null;
        }
        V removeValue = toRemove.value;
        root = root.delete(root, key);
        return removeValue;
    }

    @Override
    public V remove(K key, V value) {
        if (root == null){
            return null;
        }
        BSTNode toRemove = root.get(root, key);
        if (toRemove == null){
            return null;
        }
        V removeValue = toRemove.value;
        root = root.delete(root, key, value);
        return removeValue == value ? removeValue : null;
    }

    public class BSTIterator implements Iterator<K> {
        private final Stack<BSTNode> stack = new Stack<>();
        private void pushSmallest(BSTNode node){
            if (node!=null) {
                stack.push(node);
                pushSmallest(node.left);
            }
        }
        BSTIterator(){
            pushSmallest(root);
        }

        @Override
        public boolean hasNext() {
            return stack.empty();
        }

        @Override
        public K next() {
            BSTNode node = stack.pop();
            pushSmallest(node.right);
            return node.key;
        }
    }

    @Override
    public Iterator<K> iterator() {
        return new BSTIterator();
    }

    private void dfsPrint(BSTNode node){
        if (node != null) {
            dfsPrint(node.left);
            System.out.print(node + " -> ");
            dfsPrint(node.right);
        }
    }
    public void printInOrder() {
        dfsPrint(root);
        System.out.print("END");
    }
}
