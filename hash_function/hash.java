public class hash {
    public static void main(String args[]) {
        String input = "Hello World!";
        System.out.println(division_hash(input, 991));
    }

    // recursive function
    private static int division_hash(String str, int p) {
        // bounds checking
        if (str.length() == 0)
            throw new java.lang.Error("division_hash() executed with empty string!");

        int last_char = (int) str.charAt(str.length() - 1);
        if (str.length() == 1)
            return last_char;

        String substring = str.substring(0, str.length() - 1);
        return (division_hash(substring, p) * 31 + last_char) % p;
    }
}
