package deque;

import org.junit.Test;

import java.util.Comparator;

import static org.junit.Assert.*;

public class MaxArrayDequeTest {
    public class StringComparator implements Comparator<String> {
        @Override
        public int compare(String o1, String o2) {
            return o1.compareTo(o2);
        }
    }

    public class StringLenComparator implements Comparator<String> {
        @Override
        public int compare(String o1, String o2) {
            return o1.length() - o2.length();
        }
    }

    public class ValueNode implements Comparable<ValueNode> {
        public int value;
        public String name;

        public ValueNode(int value, String name) {
            this.value = value;
            this.name = name;
        }

        @Override
        public int compareTo(ValueNode o) {
            return this.value - o.value;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o instanceof ValueNode) {
                ValueNode vn = (ValueNode) o;
                return this.name.equals(vn.name) && this.value == vn.value;
            }
            return false;
        }
    }

    public class ValueNodeComparator implements Comparator<ValueNode> {
        @Override
        public int compare(ValueNode o1, ValueNode o2) {
            return o1.compareTo(o2);
        }
    }

    @Test
    public void TestCmpString() {
        Comparator<String> c1 = new StringComparator();
        Comparator<String> c2 = new StringLenComparator();
        MaxArrayDeque<String> mdq = new MaxArrayDeque<>(c1);
        mdq.addFirst("AAAABB");
        mdq.addFirst("BBBBCC");
        mdq.addLast("CCCCDD");
        mdq.addFirst("AAAACC");
        mdq.addLast("CCCCAAA");
        assertEquals("CCCCDD", mdq.max());
        assertEquals("CCCCAAA", mdq.max(c2));
    }

    public void TestCmpValueNode() {
        Comparator<ValueNode> c = new ValueNodeComparator();
        MaxArrayDeque<ValueNode> vdq = new MaxArrayDeque<>(c);
        ValueNode n1 = new ValueNode(10, "boy");
        ValueNode n2 = new ValueNode(20, "girl");
        ValueNode n3 = new ValueNode(5, "man");
        ValueNode n4 = new ValueNode(1, "woman");
        vdq.addFirst(n1);
        vdq.addLast(n2);
        vdq.addLast(n3);
        vdq.addFirst(n4);

        ValueNode expectNode = new ValueNode(20, "girl");
        ValueNode notExpectNode = new ValueNode(20, "Girl");
        assertEquals(n2, vdq.max());
        assertEquals(n2, vdq.max(c));
        assertEquals(vdq.max(), vdq.max(c));
        assertEquals(expectNode, vdq.max());
        assertNotEquals(notExpectNode, vdq.max());
    }
}
