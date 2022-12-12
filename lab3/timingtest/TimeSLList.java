package timingtest;
import edu.princeton.cs.algs4.Stopwatch;

/**
 * Created by hug.
 */
public class TimeSLList {
    private static void printTimingTable(AList<Integer> Ns, AList<Double> times, AList<Integer> opCounts) {
        System.out.printf("%12s %12s %12s %12s\n", "N", "time (s)", "# ops", "microsec/op");
        System.out.printf("------------------------------------------------------------\n");
        for (int i = 0; i < Ns.size(); i += 1) {
            int N = Ns.get(i);
            double time = times.get(i);
            int opCount = opCounts.get(i);
            double timePerOp = time / opCount * 1e6;
            System.out.printf("%12d %12.2f %12d %12.2f\n", N, time, opCount, timePerOp);
        }
    }

    public static void main(String[] args) {
        timeGetLast();
    }

    public static void timeGetLast() {
        // TODO: YOUR CODE HERE
        int[] trials = new int[] {1000, 2000, 4000, 8000, 16000, 32000, 64000, 128000};
        AList<Integer> Ns = new AList<>();
        AList<Double> times = new AList<>();
        AList<Integer> opCounts = new AList<>();
        for (int i = 0; i < trials.length; i++){
            SLList<Integer> slList = new SLList<>();
            int n = trials[i];
            int dummy = 0;
            int num_calls = 10000;
            for (int j = 0; j < n; j++){
                slList.addFirst(dummy);
            }
            Stopwatch sw = new Stopwatch();
            for (int m = 0; m < num_calls; m++) {
                slList.getLast();
            }
            double timeInSeconds = sw.elapsedTime();
            Ns.addLast(n);
            times.addLast(timeInSeconds);
            opCounts.addLast(num_calls);
        }
        printTimingTable(Ns, times, opCounts);
    }

}
