package tester;
import static org.junit.Assert.*;
import org.junit.Test;
import student.StudentArrayDeque;
import edu.princeton.cs.algs4.StdRandom;
public class TestArrayDequeEC {
    @Test
    public void TestBuggyAList(){
        StudentArrayDeque<Integer> L = new StudentArrayDeque<>();
        ArrayDequeSolution<Integer> L2 = new ArrayDequeSolution<>();

        int N = 50000;
        String process = "";
        for (int i = 0; i < N; i += 1) {
            int operationNumber = StdRandom.uniform(0, 5);
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
            }else if (operationNumber == 2){
                // removeFirst
                if (!L.isEmpty()){
                    int first = L.removeFirst();
                    int firstL2 = L2.removeFirst();
                    process = process.concat("removeFirst()\n");
                    assertEquals(process, first, firstL2);
                }
            } else if (operationNumber == 3) {
                // removeLast
                if (!L.isEmpty()){
                    int last = L.removeLast();
                    int lastL2 = L2.removeLast();
                    process = process.concat("removeLast()\n");
                    assertEquals(process, last, lastL2);
                }
            }
            else if (operationNumber == 4) {
                // get
                if (!L.isEmpty()) {
                    int randIndex = StdRandom.uniform(0, L.size());
                    int item = L.get(randIndex);
                    int itemL2 = L2.get(randIndex);
                    process = process.concat(String.format("get(%d)\n",randIndex));
                    assertEquals(process, item, itemL2);

                }
            }
        }
    }
}
