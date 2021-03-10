import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class Hash {
    public static void main(String args[]) {
        BigInteger key = BigInteger.valueOf(3286);
        BigInteger key2 = BigInteger.valueOf(3287);
        BigInteger block_num = BigInteger.valueOf(126);
        BigInteger block_num2 = BigInteger.valueOf(125);
        BigInteger nonce = BigInteger.valueOf(324);
        BigInteger nonce2 = BigInteger.valueOf(323);

        // System.out.println(generate_seed(key, block_num, nonce));
        // System.out.println(generate_seed(key2, block_num, nonce));
        // System.out.println(generate_seed(key, block_num2, nonce));
        // System.out.println(generate_seed(key, block_num, nonce2));
        for (int idx = 0; true; idx++) {
            print_big_int_bits(generate_seed(key, block_num, nonce.add(BigInteger.valueOf(idx))));
        }
    }

    private static BigInteger generate_seed(BigInteger key, BigInteger block_num, BigInteger nonce) {
        short[][] matrix = fill_matrix(key, block_num, nonce);
        transpose(matrix);
        return convert_to_big_int(matrix);
    }

    private static void print_matrix(short[][] matrix) {
        for (int x = 0; x < 4; x++) {
            System.out.println();
            for (int y = 0; y < 4; y++) {
                System.out.println(matrix[x][y]);
            }
        }
    }

    private static void print_big_int_bits(BigInteger number) {
        byte[] bytes = number.toByteArray();
        for (int idx = 0; idx < bytes.length; idx++) {
            for (int j = 0; j < 8; j++)
                if ((bytes[idx] & (1 << (8 - j))) != 0)
                    System.out.print(1);
                else
                    System.out.print(0);
        }
        System.out.println();
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

    private static BigInteger convert_to_big_int(short[][] matrix) {
        byte[] list = new byte[matrix.length * matrix[0].length * 2];
        int idx = 0;
        for (int x = 0; x < matrix.length; x++) {
            for (int y = 0; y < matrix[x].length; y++) {
                list[idx] = (byte) (matrix[x][y] >> 0);
                idx++;
                list[idx] = (byte) (matrix[x][y] >> 8);
                idx++;
            }
        }
        return new BigInteger(list);
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

        // pour list into 2d array of shorts
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
        short[] input_values = new short[matrix[0].length];

        // 30 rounds
        for (int n = 0; n < 30; n++) {
            // rows
            for (int y = 0; y < matrix[0].length; y++) {
                for (int x = 0; x < matrix.length; x++) {
                    input_values[x] = matrix[x][y];
                }
                short[] output_values = calculate(input_values);
                for (int x = 0; x < matrix.length; x++) {
                    matrix[x][y] = output_values[x];
                }
            }

            // a b c d
            // e f g h
            // i j k l
            // m n o p

            // diagonals
            // x-offset changes with each set of values
            for (int x = 0; x < matrix.length; x++) {
                // put values in diagonal in list
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
    }

    // calculate new values for a list of 4 shorts
    private static short[] calculate(short[] input_values) {
        input_values[0] = (short) (input_values[0] + input_values[1]);
        input_values[3] = (short) (input_values[3] ^ input_values[1]);
        input_values[3] = (short) Integer.rotateLeft((int) input_values[3], 6);
        input_values[2] = (short) (input_values[2] + input_values[3]);
        input_values[1] = (short) (input_values[1] ^ input_values[2]);
        input_values[2] = (short) Integer.rotateLeft((int) input_values[1], 12);
        input_values[0] = (short) (input_values[0] + input_values[1]);
        input_values[3] = (short) (input_values[3] ^ input_values[1]);
        input_values[3] = (short) Integer.rotateLeft((int) input_values[3], 8);
        input_values[2] = (short) (input_values[2] + input_values[3]);
        input_values[1] = (short) (input_values[1] ^ input_values[2]);
        input_values[2] = (short) Integer.rotateLeft((int) input_values[1], 7);

        return input_values;
    }
}
