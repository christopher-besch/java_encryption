import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.Random;

public class App {
    public static void main(String args[]) {
        // read console arguments
        String input_files[];
        String output_file_or_folder;
        boolean random_nonce = false;
        BigInteger nonce;
        String passphrase;

        System.out.println("All done, enjoy!");
    }

    private static void encrypt_file(String in_file, String out_file, String input_passphrase, BigInteger nonce) {
        File input_file = new File(in_file);

        FileInputStream file_input_stream = null;
        byte[] bytes = new byte[(int) input_file.length()];
        try {
            file_input_stream = new FileInputStream(input_file);
            file_input_stream.read(bytes);
            file_input_stream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        BigInteger passphrase = division_hash(input_passphrase);

        BigInteger current_block_number = BigInteger.valueOf(0);
        byte[] current_block_key = new byte[64];
        int current_block_key_idx = 64;
        for (int idx = 0; idx < bytes.length; idx++) {
            if (current_block_key_idx == 64) {
                current_block_key = get_block_key(generate_seed(passphrase, current_block_number, nonce)).toByteArray();
                current_block_number.add(BigInteger.valueOf(1));
                current_block_key_idx = 0;
            }

            bytes[idx] = (byte) (bytes[idx] ^ current_block_key[current_block_key_idx]);
            current_block_key_idx++;
        }

        File output_file = new File(out_file);
        try {
            FileOutputStream output_stream = new FileOutputStream(output_file);
            output_stream.write(bytes);
            output_stream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try (FileOutputStream output_stream = new FileOutputStream(output_file)) {
            output_stream.write(bytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static BigInteger division_hash(String str) {
        // bounds checking
        if (str.length() == 0)
            throw new java.lang.Error("division_hash() executed with empty string!");

        BigInteger last_char = BigInteger.valueOf((int) str.charAt(str.length() - 1));
        if (str.length() == 1)
            return last_char;

        String substring = str.substring(0, str.length() - 1);
        BigInteger max_number = BigInteger.valueOf(2).pow(128);
        return (division_hash(substring).multiply(BigInteger.valueOf(31)).add(last_char)).mod(max_number);
    }

    private static BigInteger generate_seed(BigInteger passphrase, BigInteger block_num, BigInteger nonce) {
        short[][] matrix = fill_matrix(passphrase, block_num, nonce);
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

    private static short[][] fill_matrix(BigInteger passphrase, BigInteger block_num, BigInteger nonce) {
        // sizes in bytes
        int const_size = 8;
        int passphrase_size = 16;
        int block_num_size = 4;
        int nonce_size = 4;

        byte[] const_bytes = { 'I', 'L', 'o', 'v', 'e', 'Y', 'o', 'u' };
        byte[] passphrase_bytes = passphrase.toByteArray();
        byte[] block_num_bytes = block_num.toByteArray();
        byte[] nonce_bytes = nonce.toByteArray();

        byte[] list = new byte[passphrase_size + block_num_size + const_size + nonce_size];
        int last_idx = 0;
        last_idx = fill(list, const_bytes, last_idx, const_size);
        last_idx = fill(list, passphrase_bytes, last_idx, passphrase_size);
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

    // creates block key with n bits depending on seed
    private static BigInteger get_block_key(BigInteger seed) {
        BigInteger x = seed;
        int key_length = 512;
        BigInteger block_key = BigInteger.valueOf(0);
        int current_key_length = 0;
        while (current_key_length < key_length)// until bit length bigger or equal to n
        {
            // linear congruence generator
            x = x.multiply(BigInteger.valueOf(134775813)).add(BigInteger.valueOf(1)).mod(power_base_2(key_length));
            int x_length = x.bitLength();
            // mask of same length
            BigInteger mask = get_mask(x_length);
            // mask overlaps x values
            // example:
            // x :10100011
            // mask :00001111
            // right:00000011
            BigInteger right = x.and(mask);
            int r_length = right.bitLength();
            // right is appended to key
            // How: previous key is shifted by the bits of right(2^r_length)and afterwards
            // right is add to it
            block_key = block_key.multiply(power_base_2(r_length)).add(right);
            current_key_length += right.bitLength();
        }
        // key is cut to n bits
        return block_key.divide(power_base_2(current_key_length - (key_length)));
    }

    // create a mask, exists consists of zeros bits from left to middle and from
    // middle to consists of ones
    private static BigInteger get_mask(int length) {
        // example :
        // input : length: 8
        // mask : 00001111
        int middle = (int) length / 2;
        BigInteger mask = power_base_2(middle).subtract(BigInteger.valueOf(1));
        return mask;
    }

    // returns a power, input as exponent to base 2
    private static BigInteger power_base_2(int expo) {
        // example: input: expo= 4
        // 00001 -> 10000
        return BigInteger.valueOf(1).shiftLeft(expo);
    }

    // converts integer to bon
    public static String to_bin(BigInteger x) {
        String bin = "";
        if (0 == x.compareTo(BigInteger.valueOf(0))) {
            bin = "0";
        }
        while (1 == x.compareTo(BigInteger.valueOf(0))) {
            BigInteger r = x.mod(BigInteger.valueOf(2));
            x = x.divide(BigInteger.valueOf(2));
            bin = r + bin;
        }
        return bin;
    }
}
