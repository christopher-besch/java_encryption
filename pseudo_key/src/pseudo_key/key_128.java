package pseudo_key;

import java.math.BigInteger;

public class key_128 {
	//erstellt einen beliebig langen Schlüssel abhängig vom seed
	private static BigInteger _x;
	private static BigInteger _a;
	private static BigInteger _c;
	
	//erstelle 1. key mit seed 
	public key_128(BigInteger seed, BigInteger a, BigInteger c) {
		this._x=seed;
		this._a=a;
		this._c=c;
	}
	
	//erstellt nächsten Teilkey mit 128 Bit
	public BigInteger next_key() 
	{
		BigInteger x = _x;
		this._x= get_key().mod(power_base_2(128));
		return _x;
	}
	private static BigInteger get_key() 
	{
		BigInteger key = BigInteger.valueOf(0);
		int current_keylength=0;
		
		while(current_keylength<256)//Die maximale Bytelänge soll 256 Bit sein 
		{
			//linearer Konguenzgenerator
			_x = _x.multiply(_a).add(_c).mod(power_base_2(256));
			int x_length = _x.bitLength();
			//passende Maske wird erstellt
			BigInteger mask =get_mask(x_length);
			//Überlagere die Maske auf den Wert von x 
			//Beispiel:
			//x    :10100011
			//mask :00001111
			//right:00000011
			BigInteger right = _x.and(mask);
			int r_length= right.bitLength();
			key=key.multiply(power_base_2(r_length)).add(right);
			current_keylength+=right.bitLength();
		}
		return key.divide(power_base_2(current_keylength-256));
	}
	
	private static BigInteger get_mask(int length) {
		//erstellt eine Maske, die von Links bis zur Hälfte aus Nulln und von der Hälfte nach Rechts aus Einsen besteht
		//Input: 8
		//Maske: 00001111
		int middel = (int)length/2;
		BigInteger mask= power_base_2(middel).subtract(BigInteger.valueOf(1));
		return mask;
	}
	private static BigInteger power_base_2(int potenz) 
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
	public static void main(String []args) 
	{ 
		key_128 test = new key_128(BigInteger.valueOf(88),BigInteger.valueOf(134518036),BigInteger.valueOf(1));
		BigInteger first_key = test.next_key();
		System.out.println(first_key);
		System.out.println(to_bin(first_key));
	} 
	

}
