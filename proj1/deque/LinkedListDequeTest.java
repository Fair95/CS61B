package deque;

import edu.princeton.cs.algs4.StdRandom;
import jh61b.junit.In;
import org.junit.Test;

import java.util.Iterator;

import static org.junit.Assert.*;


/**
 * Performs some basic linked list tests.
 */
public class LinkedListDequeTest {

    @Test
    /** Adds a few things to the list, checking isEmpty() and size() are correct,
     * finally printing the results.
     *
     * && is the "and" operation. */
    public void addIsEmptySizeTest() {
        LinkedListDeque<String> lld1 = new LinkedListDeque<String>();
        ArrayDeque<String> ad1 = new ArrayDeque<>();

        assertTrue("A newly initialized LLDeque should be empty", lld1.isEmpty());
        assertTrue("A newly initialized ADeque should be empty", ad1.isEmpty());
        lld1.addFirst("front");
        ad1.addFirst("front");

        // The && operator is the same as "and" in Python.
        // It's a binary operator that returns true if both arguments true, and false otherwise.
        assertEquals(1, lld1.size());
        assertEquals(1, ad1.size());
        assertFalse("lld1 should now contain 1 item", lld1.isEmpty());
        assertFalse("ad1 should now contain 1 item", ad1.isEmpty());

        lld1.addLast("middle");
        ad1.addLast("middle");
        assertEquals(2, lld1.size());
        assertEquals(2, ad1.size());

        lld1.addLast("back");
        ad1.addLast("back");
        assertEquals(3, lld1.size());
        assertEquals(3, ad1.size());

        System.out.println("Printing out deque: ");
        lld1.printDeque();
        ad1.printDeque();
    }

    @Test
    /** Adds an item, then removes an item, and ensures that dll is empty afterwards. */
    public void addRemoveTest() {

        LinkedListDeque<Integer> lld1 = new LinkedListDeque<Integer>();
        ArrayDeque<Integer> ad1 = new ArrayDeque<>();
        // should be empty
        assertTrue("lld1 should be empty upon initialization", lld1.isEmpty());
        assertTrue("ad1 should be empty upon initialization", ad1.isEmpty());

        lld1.addFirst(10);
        ad1.addFirst(10);
        // should not be empty
        assertFalse("lld1 should contain 1 item", lld1.isEmpty());
        assertFalse("ad1 should contain 1 item", ad1.isEmpty());

        lld1.removeFirst();
        ad1.removeFirst();
        // should be empty
        assertTrue("lld1 should be empty after removal", lld1.isEmpty());
        assertTrue("ad1 should be empty after removal", ad1.isEmpty());

    }

    @Test
    /* Tests removing from an empty deque */
    public void removeEmptyTest() {

        LinkedListDeque<Integer> lld1 = new LinkedListDeque<>();

        lld1.addFirst(3);

        lld1.removeLast();
        lld1.removeFirst();
        lld1.removeLast();
        lld1.removeFirst();

        int size = lld1.size();
        String errorMsg = "  Bad size returned when removing from empty LinkedListDeque.\n";
        errorMsg += "  student size() returned " + size + "\n";
        errorMsg += "  actual size() returned 0\n";

        assertEquals(errorMsg, 0, size);

        ArrayDeque<Integer> ad1 = new ArrayDeque<>();
        ad1.addFirst(3);

        ad1.removeLast();
        ad1.removeFirst();
        ad1.removeLast();
        ad1.removeFirst();

        size = ad1.size();
        errorMsg = "  Bad size returned when removing from empty ArrayDeque.\n";
        errorMsg += "  student size() returned " + size + "\n";
        errorMsg += "  actual size() returned 0\n";

        assertEquals(errorMsg, 0, size);
    }

    @Test
    /* Check if you can create LinkedListDeques with different parameterized types*/
    public void multipleParamTest() {

        LinkedListDeque<String> lld1 = new LinkedListDeque<String>();
        LinkedListDeque<Double> lld2 = new LinkedListDeque<Double>();
        LinkedListDeque<Boolean> lld3 = new LinkedListDeque<Boolean>();

        lld1.addFirst("string");
        lld2.addFirst(3.14159);
        lld3.addFirst(true);

        String s = lld1.removeFirst();
        double d = lld2.removeFirst();
        boolean b = lld3.removeFirst();

        ArrayDeque<String> ad1 = new ArrayDeque<>();
        ArrayDeque<Double> ad2 = new ArrayDeque<>();
        ArrayDeque<Boolean> ad3 = new ArrayDeque<>();

        ad1.addFirst("string");
        ad2.addFirst(3.14159);
        ad3.addFirst(true);

        s = ad1.removeFirst();
        d = ad2.removeFirst();
        b = ad3.removeFirst();
    }

    @Test
    /* check if null is return when removing from an empty LinkedListDeque. */
    public void emptyNullReturnTest() {

        LinkedListDeque<Integer> lld1 = new LinkedListDeque<Integer>();

        boolean passed1 = false;
        boolean passed2 = false;
        assertEquals("Should return null when removeFirst is called on an empty Deque,", null, lld1.removeFirst());
        assertEquals("Should return null when removeLast is called on an empty Deque,", null, lld1.removeLast());

        ArrayDeque<Integer> ad1 = new ArrayDeque<>();

        passed1 = false;
        passed2 = false;
        assertEquals("Should return null when removeFirst is called on an empty Deque,", null, ad1.removeFirst());
        assertEquals("Should return null when removeLast is called on an empty Deque,", null, ad1.removeLast());

    }

    @Test
    /* Add large number of elements to deque; check if order is correct. */
    public void bigLLDequeTest() {

        LinkedListDeque<Integer> lld1 = new LinkedListDeque<Integer>();
        for (int i = 0; i < 1000000; i++) {
            lld1.addLast(i);
        }

        for (double i = 0; i < 500000; i++) {
            assertEquals("Should have the same value", i, (double) lld1.removeFirst(), 0.0);
        }

        for (double i = 999999; i > 500000; i--) {
            assertEquals("Should have the same value", i, (double) lld1.removeLast(), 0.0);
        }

        ArrayDeque<Integer> ad1 = new ArrayDeque<>();
        for (int i = 0; i < 1000000; i++) {
            ad1.addLast(i);
        }

        for (double i = 0; i < 500000; i++) {
            assertEquals("Should have the same value", i, (double) ad1.removeFirst(), 0.0);
        }

        for (double i = 999999; i > 500000; i--) {
            assertEquals("Should have the same value", i, (double) ad1.removeLast(), 0.0);
        }
    }

    @Test
    public void RandomisedTest() {
        LinkedListDeque<Integer> L = new LinkedListDeque<>();
        ArrayDeque<Integer> L2 = new ArrayDeque<>();

        int N = 50000;
        String process = "";
        for (int i = 0; i < N; i += 1) {
            int operationNumber = StdRandom.uniform(0, 6);
            if (operationNumber == 0) {
                // addFirst
                int randVal = StdRandom.uniform(0, 100);
                L.addFirst(randVal);
                L2.addFirst(randVal);
                process = process.concat("addFirst(" + randVal + ")\n");
            } else if (operationNumber == 1) {
                // addLast
                int randVal = StdRandom.uniform(0, 100);
                L.addLast(randVal);
                L2.addLast(randVal);
                process = process.concat("addLast(" + randVal + ")\n");
            } else if (operationNumber == 2) {
                // removeFirst
                if (!L.isEmpty()) {
                    Integer first = L.removeFirst();
                    Integer firstL2 = L2.removeFirst();
                    process = process.concat("removeFirst()\n");
                    assertEquals(process, first, firstL2);
                }
            } else if (operationNumber == 3) {
                // removeLast
                if (!L.isEmpty()) {
                    Integer last = L.removeLast();
                    Integer lastL2 = L2.removeLast();
                    process = process.concat("removeLast()\n");
                    assertEquals(process, last, lastL2);
                }
            } else if (operationNumber == 4) {
                // get
                if (!L.isEmpty()) {
                    int randIndex = StdRandom.uniform(0, L.size());
                    Integer item = L.get(randIndex);
                    Integer itemL2 = L2.get(randIndex);
                    process = process.concat(String.format("get(%d)\n", randIndex));
                    assertEquals(process, item, itemL2);
                }
            } else if (operationNumber == 5) {
                int size = L.size();
                int sizeL2 = L2.size();
                process = process.concat("size()\n");
                assertEquals(process, size, sizeL2);
            }
        }
    }

    @Test
    public void IteratorTest() {
        LinkedListDeque<Integer> L = new LinkedListDeque<>();
        ArrayDeque<Integer> L2 = new ArrayDeque<>();

        int N = 50;
        String process = "";
        for (int i = 0; i < N; i += 1) {
            int operationNumber = StdRandom.uniform(0, 2);
            if (operationNumber == 0) {
                // addFirst
                int randVal = StdRandom.uniform(0, 100);
                L.addFirst(randVal);
                L2.addFirst(randVal);
                process = process.concat("addFirst(" + randVal + ")\n");
            } else if (operationNumber == 1) {
                // addLast
                int randVal = StdRandom.uniform(0, 100);
                L.addLast(randVal);
                L2.addLast(randVal);
                process = process.concat("addLast(" + randVal + ")\n");
            }
        }
        Iterator<Integer> it = L2.iterator();
        for (Integer i : L) {
            assertEquals(i, it.next());
        }
    }
    @Test
    /* Tests removing from an empty deque */
    public void removeResizeTest() {
        ArrayDeque<Integer> ad1 = new ArrayDeque<>();
        for (int i = 0; i < 10000; i++) {
            ad1.addFirst(0);
        }
        for (int i = 0; i < 10000; i++) {
            ad1.removeFirst();
        }
        int capacity = ad1.getCapacity();
        String errorMsg = "  Bad capacity returned when removing from ArrayDeque.\n";
        errorMsg += "  student capacity returned " + capacity + "\n";
        errorMsg += "  actual capacity expected 16\n";

        assertEquals(errorMsg, 16, capacity);

        ArrayDeque<Integer> ad2 = new ArrayDeque<>();
        for (int i = 0; i < 8; i++) {
            ad2.addFirst(0);
        }
        for (int i = 0; i < 8; i++) {
            ad2.removeFirst();
        }
        int capacity2 = ad2.getCapacity();
        String errorMsg2 = "  Bad capacity returned when removing from ArrayDeque.\n";
        errorMsg2 += "  student capacity returned " + capacity2 + "\n";
        errorMsg2 += "  actual capacity expected 16\n";

        assertEquals(errorMsg2, 16, capacity2);
    }
}
