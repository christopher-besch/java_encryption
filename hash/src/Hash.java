import java.math.BigInteger;

public class Hash {
    public static void main(String args[]) {
        BigInteger key = BigInteger.valueOf(3287);
        BigInteger block_num = BigInteger.valueOf(126);
        BigInteger nonce = BigInteger.valueOf(324);
        transpose(key, block_num, nonce);
    }

    private static int fill(byte[] matrix, byte[] input, int start_idx, int length) {
        for (int idx = 0; idx < length; idx++) {
            if (idx >= input.length)
                matrix[start_idx + idx] = 0;
            else
                matrix[start_idx + idx] = input[idx];
        }
        return start_idx + length;
    }

    private static int transpose(BigInteger key, BigInteger block_num, BigInteger nonce) {
        // 64b key
        // 32b block_num
        // 8b chars * 4
        // -> 128b

        // sizes in bytes
        int const_size = 8;
        int key_size = 16;
        int block_num_size = 4;
        int nonce_size = 4;

        byte[] const_bytes = { 'I', 'L', 'o', 'v', 'e', 'Y', 'o', 'u' };
        byte[] key_bytes = key.toByteArray();
        byte[] block_num_bytes = block_num.toByteArray();
        byte[] nonce_bytes = nonce.toByteArray();

        byte[] matrix = new byte[key_size + block_num_size + const_size + nonce_size];
        int last_idx = 0;
        last_idx = fill(matrix, const_bytes, last_idx, const_size);
        last_idx = fill(matrix, key_bytes, last_idx, key_size);
        last_idx = fill(matrix, block_num_bytes, last_idx, block_num_size);
        last_idx = fill(matrix, nonce_bytes, last_idx, nonce_size);

        for (int idx = 0; idx < matrix.length; idx++) {
            if (idx % 4 == 0)
                System.out.println();
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
