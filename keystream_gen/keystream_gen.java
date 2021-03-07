import java.math.BigInteger;

public class keystream_gen {
    public static void main(String args[]) {
        long x = 23898456;
        while (true) {
            x = gen(x);
            System.out.println(x);
        }
    }

    private static long gen(long seed) {
        long x = seed;

        return ((x * 134518036) + 1) % 9223372036854775808L;
    }
}
