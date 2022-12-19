package flik;
import static org.junit.Assert.*;
import org.junit.Test;

public class FilkTest {
    @Test
    public void TestFilk(){
        assertTrue(Flik.isSameNumber(1,1));
        int int1 = 1;
        int int2 = 129;
        Integer integer1 = 1;
        Integer integer2 = 129;
        assertTrue(Flik.isSameNumber(int1, integer1));
        assertTrue(Flik.isSameNumber(int2, integer2));

        StringBuilder sb = new StringBuilder();
        sb.append(String.format("int1 was %d, int2 was %d\t", int1, int2));
        int1 += 128;
        integer1 += 128;
        sb.append(String.format("int1 is now %d, int2 is now %d\n", int1, int2));
        String err = sb.toString();
        assertTrue(err, Flik.isSameNumber(int1, int2));

    }
    @Test
    public void TestMain(){
        int i = 0;
        for (int j = 0; i < 5000; ++i, ++j) {
            assertTrue(Flik.isSameNumber(i, j));
        }
        System.out.println("i is " + i);
    }
}
