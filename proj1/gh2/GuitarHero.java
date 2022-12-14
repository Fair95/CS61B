package gh2;

import edu.princeton.cs.algs4.StdAudio;
import edu.princeton.cs.algs4.StdDraw;

import java.util.Arrays;

public class GuitarHero {
    public static final double CONCERT = 440.0;
    public static final String keyboard = "q2we4r5ty7u8i9op-[=zxdcfvgbnjmk,.;/' ";

    private static double getConcert(int index) {
        return CONCERT * Math.pow(2, (index - 24.0) / 12.0);
    }


    public static void main(String[] args) {
        /* create two guitar strings, for concert A and C */
        GuitarString[] gStrings = new GuitarString[keyboard.length()];
        for (int i = 0; i < keyboard.length(); i++) {
            gStrings[i] = new GuitarString(getConcert(i));
        }
        while (true) {
            int index;
            /* check if the user has typed a key; if so, process it */
            if (StdDraw.hasNextKeyTyped()) {
                char key = StdDraw.nextKeyTyped();
                index = keyboard.indexOf(key);
                if (index != -1) {
                    gStrings[index].pluck();
                }
            }

            /* compute the superposition of samples */
            double sample = 0;
            for (GuitarString g : gStrings) {
                sample += g.sample();
            }

            /* play the sample on standard audio */
            StdAudio.play(sample);

            /* advance the simulation of each guitar string by one step */
            for (GuitarString g : gStrings) {
                g.tic();
            }
        }
    }
}
