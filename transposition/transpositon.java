public class transpositon {
    public static void main(String args[]) {
        long key = 109234;
        int block_num = 10;
        transpose(key, block_num);
    }

    private static int transpose(long key, int block_num) {
        // 64b key
        // 32b block_num
        // 8b chars * 4
        // -> 128b
        System.out.println(Integer.toBinaryString((int) key));
        System.out.println(Integer.toBinaryString((int) block_num));

        // pour into 4 by 4 matrix with 8 bits each
        byte matrix[] = new byte[16];
        matrix[0] = (byte) (key >> 0);
        matrix[1] = (byte) (key >> 8);
        matrix[2] = (byte) (key >> 16);
        matrix[3] = (byte) (key >> 24);
        matrix[4] = (byte) (block_num >> 0);
        matrix[5] = (byte) (block_num >> 8);

        for (int idx = 0; idx < 16; idx++) {
            for (int j = 0; j < 8; j++)
                if ((matrix[idx] & (1 << (8 - j))) != 0)
                    System.out.print(1);
                else
                    System.out.print(0);
            System.out.println();
        }

        return 0;
    }
}
