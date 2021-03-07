package pseudo_key;
import java.math.BigInteger;
public class rand_key{


	public static BigInteger get_nextkey(BigInteger seed, BigInteger a, BigInteger c) 
	{
		//rechte Seite
		BigInteger key = BigInteger.valueOf(0);
		BigInteger x = seed;
		int current_keylength=0;
		
		while(current_keylength<64)//Die maximale Bytelänge soll 4 (64 Bit)sein 
		{
			//linearer Konguenzgenerator
			x = x.multiply(a).add(c).mod(power_base_2(64));
			int x_length = x.bitLength();
			//passende Maske wird erstellt
			BigInteger mask =get_mask(x_length);
			//Überlagere die Maske auf den Wert von x 
			//Beispiel:
			//x    :10100011
			//mask :00001111
			//right:00000011
			BigInteger right = x.and(mask);
			int r_length= right.bitLength();
			key=key.multiply(power_base_2(r_length)).add(right);
			current_keylength+=right.bitLength();
		}
		return key.divide(power_base_2(current_keylength-64));
	}
	
	public static BigInteger get_mask(int length) {
		//erstellt eine Maske, die von Links bis zur Hälfte aus Nulln und von der Hälfte nach Rechts aus Einsen besteht
		//Input: 8
		//Maske: 00001111
		int middel = (int)length/2;
		BigInteger mask= power_base_2(middel).subtract(BigInteger.valueOf(1));
		return mask;
	}
	
	public static int get_bitlength(BigInteger x) 
	{
		//Länge der Bits von x wird bestimmt
		BigInteger double_n = BigInteger.valueOf((long)(Math.log(x.longValue()) / Math.log(2)));
		return (int) (double_n.doubleValue())+1;
		
	}
	public static BigInteger power_base_2(int potenz) 
	{
		//Input: potenz = 4
		//00001 -> 10000
		return BigInteger.valueOf(1).shiftLeft(potenz);
	}

	
	public static String to_bin(BigInteger x)
	//Integer in eine Binärzahl umwandeln (ohne spezielle "Bitlücken" aufzufüllen)
	{
		String bin = "";
		if(0==x.compareTo(BigInteger.valueOf(0)))
		{
			bin="0";
		}
		while(1==x.compareTo(BigInteger.valueOf(0))) 
		{

			BigInteger r = x.mod(BigInteger.valueOf(2));
			x= x.divide(BigInteger.valueOf(2));
			bin=r+bin;
		}
		return bin;
	}
	//12781437123964999318
	//12771893727417150036
	//1011000100111110111000011010110100111011101111010010101001010100
	//1011000101100000110010010101100001100100111011001001011010010110

	
	public static void main(String []args) 
	{ 
		BigInteger seed = BigInteger.valueOf(88);
		BigInteger a = BigInteger.valueOf(134518036);
		BigInteger c = BigInteger.valueOf(1);
		BigInteger test_key = get_nextkey(seed, a, c);
		System.out.println("Key: "+test_key);
		System.out.println("Key in Bit: "+to_bin(test_key));
		/*System.out.println("Erste Maske: "+ get_mask(64));
		BigInteger test = BigInteger.valueOf(35);
		System.out.println("Alles: "+to_bin(test));
		BigInteger mask = get_mask(test.bitLength());
		BigInteger right_side= test.and(mask);
		System.out.println("Maske: "+to_bin(mask));
		System.out.println("Rechts: "+to_bin(right_side));*/
		
		

	} 
}