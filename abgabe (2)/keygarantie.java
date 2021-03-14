import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.Random;

public class keygarantie {
    static Random random_generator = new Random();

    public static void terminate_program(String message) {
        System.err.println("keygarantie: " + message);
        System.err.println("Try 'java keygarantie --help' for more information.");
        System.exit(1);
    }

    // create bit mask, left half 0s, right half 1s
    private static BigInteger get_mask(int length) {
        int middle = (int) length / 2;
        return power_base_2(middle).subtract(BigInteger.valueOf(1));
    }

    private static BigInteger power_base_2(int expo) {
        return BigInteger.valueOf(1).shiftLeft(expo);
    }

    public static void main(String args[]) {
        String input_files[];
        String output_file_or_folder;

        boolean random_nonce = false;
        BigInteger nonce = BigInteger.valueOf(0);
        boolean nonce_missing = true;

        String passphrase = "";
        boolean passphrase_missing = true;

        ////////////////////////////
        // read console arguments //
        ////////////////////////////

        int idx = 0;
        for (; idx < args.length; idx++) {
            if (args[idx].charAt(0) == '-') {
                switch (args[idx]) {
                case "--help":
                    System.out.println("Usage: java keygarantie [OPTION]... [SOURCE] [DEST]");
                    System.out.println("  or:  java keygarantie [OPTION]... [SOURCE]... [DIRECTORY]");
                    System.out.println("Encrypt SOURCE into DEST, or multiple SOURCE(s) into DIRECTORY.");
                    System.out.println();
                    System.out.println(
                            "-r\t\tcreate, use and output random nonce for each input file instead of user defined nonce");
                    System.out.println(
                            "-p [PASSPHRASE]\tuse this passphrase for every file (not recommended for multiple files)");
                    System.out.println(
                            "-n [NONCE]\tuse this nonce for the encryption (will be overwritten by -r option)");
                    System.exit(0);
                case "-r":
                    random_nonce = true;
                    nonce_missing = false;
                    break;
                case "-p":
                    if (idx == args.length)
                        terminate_program("-p requires passphrase");
                    idx++;
                    passphrase = args[idx];
                    passphrase_missing = false;
                    break;
                case "-n":
                    if (idx == args.length)
                        terminate_program("-n requires nonce");
                    idx++;
                    // can be converted to number?
                    try {
                        nonce = new BigInteger(args[idx]);
                    } catch (NumberFormatException e) {
                        terminate_program("-n requires a numeric value as nonce");
                    }
                    nonce_missing = false;
                    break;
                default:
                    terminate_program("unrecognized option '" + args[idx] + "'");
                }
            } else
                break;
        }

        if (nonce_missing || passphrase_missing)
            terminate_program("passphrase and/or nonce missing");

        // are there enough paths given?
        if (idx + 2 > args.length)
            terminate_program("missing file operand");
        input_files = Arrays.copyOfRange(args, idx, args.length - 1);
        output_file_or_folder = args[args.length - 1];

        // catch any unexpected errors
        try {
            //////////////////
            // encrypt file //
            //////////////////

            if (input_files.length == 1) {
                // create nonce if required
                if (random_nonce)
                    nonce = BigInteger.valueOf(random_generator.nextInt());
                System.out.println("encrypting '" + input_files[0] + "' with nonce " + nonce + "...");
                encrypt_file(input_files[0], output_file_or_folder, passphrase, nonce);
            } else {
                // create output folder
                File out_directory = new File(output_file_or_folder);
                if (!out_directory.exists())
                    out_directory.mkdir();

                // go over all input files
                for (int file_idx = 0; file_idx < input_files.length; file_idx++) {
                    // create nonce if required
                    if (random_nonce)
                        nonce = BigInteger.valueOf(random_generator.nextInt());

                    System.out.println("encrypting '" + input_files[file_idx] + "' with nonce " + nonce + "...");
                    String[] input_path_split = input_files[file_idx].split("/");
                    String out_path = output_file_or_folder + "/" + input_path_split[input_path_split.length - 1];
                    encrypt_file(input_files[file_idx], out_path, passphrase, nonce);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
        System.out.println("All done, enjoy!");
    }

    private static void encrypt_file(String in_file_name, String out_file_name, String input_passphrase,
            BigInteger nonce) {
        // read input file
        byte[] bytes = {};
        try {
            File input_file = new File(in_file_name);
            bytes = new byte[(int) input_file.length()];
            FileInputStream file_input_stream = new FileInputStream(input_file);
            file_input_stream.read(bytes);
            file_input_stream.close();
        } catch (Exception e) {
            terminate_program("can't read file '" + in_file_name + "'");
        }

        // convert into 128-bit passphrase
        BigInteger passphrase = division_hash(input_passphrase);

        // cut input into 512-bit blocks and encrypt each on their own
        BigInteger current_block_number = BigInteger.valueOf(0);
        byte[] current_block_key = new byte[64];
        int current_block_byte_idx = 64;
        for (int idx = 0; idx < bytes.length; idx++) {
            // create new block key if old block is done
            if (current_block_byte_idx == 64) {
                current_block_key = get_block_key(generate_seed(passphrase, current_block_number, nonce)).toByteArray();
                current_block_number.add(BigInteger.valueOf(1));
                current_block_byte_idx = 0;
            }

            // perform main xor en-/decryption
            bytes[idx] ^= current_block_key[current_block_byte_idx];
            current_block_byte_idx++;
        }

        // write output file
        try {
            File output_file = new File(out_file_name);
            FileOutputStream output_stream = new FileOutputStream(output_file);
            output_stream.write(bytes);
            output_stream.close();
        } catch (Exception e) {
            terminate_program("can't write to output file '" + out_file_name + "'");
        }
    }

    // recursive function
    // convert input_passphrase into 128-bit passphrase
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
        do_rounds(matrix);

        // convert to byte array
        byte[] list = new byte[matrix.length * matrix[0].length * 2];
        int idx = 0;
        // read row by row
        for (int x = 0; x < matrix.length; x++) {
            for (int y = 0; y < matrix[x].length; y++) {
                // convert one short into two bytes
                list[idx] = (byte) (matrix[x][y] >> 0);
                idx++;
                list[idx] = (byte) (matrix[x][y] >> 8);
                idx++;
            }
        }
        return new BigInteger(list);
    }

    // fill 4x4 matrix with shorts
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

        // create and fill list
        byte[] list = new byte[passphrase_size + block_num_size + const_size + nonce_size];
        int last_idx = 0;
        last_idx = fill(list, const_bytes, last_idx, const_size);
        last_idx = fill(list, passphrase_bytes, last_idx, passphrase_size);
        last_idx = fill(list, block_num_bytes, last_idx, block_num_size);
        last_idx = fill(list, nonce_bytes, last_idx, nonce_size);

        short[][] matrix = new short[4][4];

        // pour list into matrix of shorts
        int idx = 0;
        for (int x = 0; x < matrix.length; x++) {
            for (int y = 0; y < matrix[x].length; y++) {
                // convert 2 bytes into 1 short
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

    // copy elements from [input] into [list] starting at [start_idx] inside list
    // if fewer than [length] elements are present in [input], a padding out of 0s
    // will be used
    private static int fill(byte[] list, byte[] input, int start_idx, int length) {
        for (int idx = 0; idx < length; idx++) {
            if (idx >= input.length)
                // padding
                list[start_idx + idx] = 0;
            else
                list[start_idx + idx] = input[idx];
        }
        // return new last filled index inside list
        return start_idx + length;
    }

    private static void do_rounds(short[][] matrix) {
        short[] input_values = new short[matrix[0].length];

        // 30 rounds
        for (int n = 0; n < 30; n++) {
            // columns
            for (int x = 0; x < matrix[0].length; x++) {
                // fill array from matrix
                for (int y = 0; y < matrix.length; y++) {
                    input_values[y] = matrix[x][y];
                }
                short[] output_values = quaver_round(input_values);
                // write array to matrix
                for (int y = 0; y < matrix.length; y++) {
                    matrix[x][y] = output_values[y];
                }
            }

            // diagonals
            // x-offset changes with each set of values
            for (int x = 0; x < matrix.length; x++) {
                // fill array from matrix
                // go one down and one to the left, starting at the offset for each new value
                for (int idx = 0; idx < input_values.length; idx++) {
                    input_values[idx] = matrix[(x + idx) % matrix.length][idx];
                }

                // calculate new values
                short[] output_values = quaver_round(input_values);
                // write array to matrix
                for (int idx = 0; idx < input_values.length; idx++) {
                    matrix[(x + idx) % matrix.length][idx] = output_values[idx];
                }
            }
        }
    }

    // calculate new values for a list of 4 shorts
    private static short[] quaver_round(short[] input_values) {
        input_values[0] += input_values[1];
        input_values[3] ^= input_values[1];
        input_values[3] = (short) Integer.rotateLeft((int) input_values[3], 6);

        input_values[2] += input_values[3];
        input_values[1] ^= input_values[2];
        input_values[2] = (short) Integer.rotateLeft((int) input_values[1], 12);

        input_values[0] += input_values[1];
        input_values[3] ^= input_values[1];
        input_values[3] = (short) Integer.rotateLeft((int) input_values[3], 8);

        input_values[2] += input_values[3];
        input_values[1] ^= input_values[2];
        input_values[2] = (short) Integer.rotateLeft((int) input_values[1], 7);

        return input_values;
    }

    // create 512-bit block key defined by seed
    private static BigInteger get_block_key(BigInteger seed) {
        // current value
        BigInteger x_i = seed;
        BigInteger block_key = BigInteger.valueOf(0);

        int current_key_length = 0;
        while (current_key_length < 512) {
            // linear congruence generator
            x_i = x_i.multiply(BigInteger.valueOf(134775813)).add(BigInteger.valueOf(1)).mod(power_base_2(64));

            // remove left half
            BigInteger mask = get_mask(x_i.bitLength());
            BigInteger right = x_i.and(mask);
            // right is appended to current block key
            // How: x_i gets shifted by the bits of right
            block_key = block_key.multiply(power_base_2(right.bitLength())).add(right);
            current_key_length += right.bitLength();
        }
        // key is cut to 512 bits
        return block_key.divide(power_base_2(current_key_length - (512)));
    }

}
