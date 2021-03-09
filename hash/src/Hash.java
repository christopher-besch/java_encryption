import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class Hash {
    public static void main(String args[]) {
        BigInteger key = BigInteger.valueOf(3287);
        BigInteger block_num = BigInteger.valueOf(126);
        BigInteger nonce = BigInteger.valueOf(324);
        short[][] matrix = fill_matrix(key, block_num, nonce);

        print_matrix(matrix);
        transpose(matrix);
        System.out.println();
        print_matrix(matrix);
    }

    private static void print_matrix(short[][] matrix) {
        for (int x = 0; x < 4; x++) {
            System.out.println();
            for (int y = 0; y < 4; y++) {
                System.out.println(matrix[x][y]);
            }
        }
    }

    private static int fill(byte[] list, byte[] input, int start_idx, int length) {
        for (int idx = 0; idx < length; idx++) {
            if (idx >= input.length)
                list[start_idx + idx] = 0;
            else
                list[start_idx + idx] = input[idx];
        }
        return start_idx + length;
    }

    private static short[][] fill_matrix(BigInteger key, BigInteger block_num, BigInteger nonce) {
        // sizes in bytes
        int const_size = 8;
        int key_size = 16;
        int block_num_size = 4;
        int nonce_size = 4;

        byte[] const_bytes = { 'I', 'L', 'o', 'v', 'e', 'Y', 'o', 'u' };
        byte[] key_bytes = key.toByteArray();
        byte[] block_num_bytes = block_num.toByteArray();
        byte[] nonce_bytes = nonce.toByteArray();

        byte[] list = new byte[key_size + block_num_size + const_size + nonce_size];
        int last_idx = 0;
        last_idx = fill(list, const_bytes, last_idx, const_size);
        last_idx = fill(list, key_bytes, last_idx, key_size);
        last_idx = fill(list, block_num_bytes, last_idx, block_num_size);
        last_idx = fill(list, nonce_bytes, last_idx, nonce_size);

        // should be calculated
        short[][] matrix = new short[4][4];

        // print debug
        for (int idx = 0; idx < list.length; idx++) {
            if (idx % 2 == 0)
                System.out.println();
            if (idx % 8 == 0)
                System.out.println();
            for (int j = 0; j < 8; j++)
                if ((list[idx] & (1 << (8 - j))) != 0)
                    System.out.print(1);
                else
                    System.out.print(0);
        }
        System.out.println();

        // pour list into 2d array
        int idx = 0;
        for (int x = 0; x < matrix.length; x++) {
            for (int y = 0; y < matrix[x].length; y++) {
                ByteBuffer buffer = ByteBuffer.allocate(2);
                buffer.order(ByteOrder.LITTLE_ENDIAN);
                buffer.put(list[idx]);
                idx++;
                buffer.put(list[idx]);
                idx++;
                matrix[x][y] = buffer.getShort(0);
            }
        }

        return matrix;
    }

    private static void transpose(short[][] matrix) {
        // columns
        for (int x = 0; x < matrix.length; x++) {
            matrix[x] = calculate(matrix[x]);
        }

        // a b c d
        // e f g h
        // i j k l
        // m n o p

        // diagonals
        // x-offset changes with each set of values
        for (int x = 0; x < matrix.length; x++) {
            // put values in diagonal in list
            short[] input_values = new short[matrix[x].length];
            // go one up and one to the left, starting at the offset for each new value
            for (int idx = 0; idx < input_values.length; idx++) {
                input_values[idx] = matrix[(x + idx) % matrix.length][idx];
            }

            // calculate new values and fill matrix
            short[] output_values = calculate(input_values);
            for (int idx = 0; idx < input_values.length; idx++) {
                matrix[(x + idx) % matrix.length][idx] = output_values[idx];
            }
        }
    }

    // calculate new values for a list of 4 shorts
    private static short[] calculate(short[] input_values) {
        short[] output_values = new short[4];
        output_values[0] = (short) (input_values[0] + input_values[2]);
        output_values[1] = (short) (input_values[1] + input_values[3]);
        output_values[3] = (short) (output_values[0] ^ output_values[1]);
        output_values[0] = (short) (output_values[0] - input_values[2]);
        output_values[2] = (short) (output_values[3] ^ input_values[0]);
        return output_values;
    }
}
