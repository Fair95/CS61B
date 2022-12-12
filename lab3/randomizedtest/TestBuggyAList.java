package randomizedtest;

import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Created by hug.
 */
public class TestBuggyAList {
  // YOUR TESTS HERE
    @Test
    public void testThreeAddThreeRemove(){
        AListNoResizing<Integer> alistNoResize = new AListNoResizing<>();
        BuggyAList<Integer> alistBuggy = new BuggyAList<>();
        int[] threeInts = {2,5,7};
        for (int i : threeInts){
            alistNoResize.addLast(i);
            alistBuggy.addLast(i);
        }
        for (int i = 0; i < 3; i++){
            int i1 = alistNoResize.removeLast();
            int i2 = alistBuggy.removeLast();
            assertEquals(i1, i2);
        }
    }

    @Test
    public void randomizedTest(){
        AListNoResizing<Integer> L = new AListNoResizing<>();

        int N = 500;
        for (int i = 0; i < N; i += 1) {
            int operationNumber = StdRandom.uniform(0, 4);
            if (operationNumber == 0) {
                // addLast
                int randVal = StdRandom.uniform(0, 100);
                L.addLast(randVal);
                System.out.println("addLast(" + randVal + ")");
            } else if (operationNumber == 1) {
                // size
                int size = L.size();
                System.out.println("size: " + size);
            } else if (operationNumber == 2) {
                // getLast
                if (L.size() > 0){
                    int last = L.getLast();
                    System.out.println("getLast():" + last);
                }
            } else if (operationNumber == 3) {
                // removeLast
                if (L.size() > 0){
                    int last = L.removeLast();
                    System.out.println("removeLast():" + last);
                }
            }
        }
    }

    @Test
    public void TestBuggyAList(){
        BuggyAList<Integer> L = new BuggyAList<>();
        AListNoResizing<Integer> L2 = new AListNoResizing<>();

        int N = 50000;
        for (int i = 0; i < N; i += 1) {
            int operationNumber = StdRandom.uniform(0, 4);
            if (operationNumber == 0) {
                // addLast
                int randVal = StdRandom.uniform(0, 100);
                L.addLast(randVal);
                L2.addLast(randVal);
                System.out.println("addLast(" + randVal + ")");
            } else if (operationNumber == 1) {
                // size
                int size = L.size();
                int sizeL2 = L2.size();
                assertEquals(size, sizeL2);
                System.out.println("size: " + size + ", sizeL2:" + sizeL2);
            } else if (operationNumber == 2) {
                // getLast
                if (L.size() > 0){
                    int last = L.getLast();
                    int lastL2 = L2.getLast();
                    assertEquals(last, lastL2);
                    System.out.println("getLast() of L:" + last + ", getLast() of L2:" + lastL2);
                }
            } else if (operationNumber == 3) {
                // removeLast
                if (L.size() > 0){
                    int last = L.removeLast();
                    int lastL2 = L2.removeLast();
                    assertEquals(last, lastL2);
                    System.out.println("removeLast():" + last + ", removeLast() of L2:" + lastL2);
                }
            }
        }
    }
}
