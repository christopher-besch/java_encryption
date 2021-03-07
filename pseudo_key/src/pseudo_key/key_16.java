package pseudo_key;

public class key_16 {
    // erstellt einen beliebig langen Schl�ssel abh�ngig vom seed
    private int _x;

    // erstelle 1. key mit seed
    public key_16(int seed) {

        this._x = (int) get_key(seed, 16);
    }

    // erstellt n�chsten Teilkey mit 16 Bit
    public int next_key() {
        int x = _x;
        this._x = (int) (get_key(_x, 32) % (long) (Math.pow(2, 16)));
        return x;
    }

    private long get_key(int seed, int key_length)

    {
        long key = seed;
        long x = seed;
        int current_keylength = get_binlength(key);
        while (current_keylength < key_length) {
            x = ((x * 134518036) + 1) % (int) Math.pow(2, 16);
            int n = get_binlength(x);
            int right_length = (int) Math.ceil((double) n / 2);
            // Halbel�nge des Bin�rcodes
            int cutter = (int) Math.pow(2.0, right_length);
            // Linke Bitseite wird weggeschnitten, x ist basisunabh�ngig
            long right = x % cutter;
            key = (key * cutter) + right;
            /*
             * System.out.println("Key:" +key); System.out.println(to_bin(key));
             * System.out.println("x:" +x); System.out.println(to_bin(x));
             * System.out.println(to_bin(x).length()); System.out.println(to_bin(right));
             * System.out.println(to_bin(right).length());
             */

            current_keylength += right_length;
        }
        // rechts wegschneiden der �berfl�ssigen
        return key / (long) Math.pow(2, current_keylength - key_length);
    }

    // L�nge des Bin�rcodes von x wird bestimmt
    private static int get_binlength(long x) {

        double double_n = Math.log(x) / Math.log(2.f);
        return (int) Math.floor(double_n) + 1;

    }

    // Integer in eine Bin�rzahl umwandeln (ohne spezielle "Bitl�cken" aufzuf�llen)
    private static String to_bin(long x) {
        String bin = "";
        if (x == 0) {
            bin = "0";
        }
        while (x > 0) {
            long r = x % 2;
            x = x / 2;
            bin = r + bin;
        }
        return bin;
    }

    public static void main(String[] args) {
        /*
         * //1, 16 //Soll: 1000101011010010 //Ist: 1000101011010010 key_16 gen2 = new
         * key_16(1); long key2=gen2.next_key(); System.out.print(to_bin(key2)); return;
         */
        /*
         * //1,32 //Soll: 10001010110100101111001011110010 //Ist:
         * 10001010110100101101001001101010 key_16 gen2 = new key_16(1); long
         * key2=gen2.next_key(); System.out.print(to_bin(key2)); key2=gen2.next_key();
         * System.out.print(to_bin(key2));
         * 
         * return;
         */
        /*
         * key_16 first_key = new key_16(88); for(int i =0;i<4;i++) { long
         * key=first_key.next_key(); System.out.print(to_bin(key)); }
         */
        // 88,63
        // 11000011001010101001011110010111100101110010111100101111
        // 1011000110000110111100101110101100101111001011111011000101110

        // 88,32+16
        // Soll: 101100011000011001010101001011110010111100101110
        // Ist:
    }

}
