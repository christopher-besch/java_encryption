package pseudo_key;

import java.math.BigInteger;

//erstellt einen beliebig langen Schlüssel abhängig vom seed
public class key_generator {
	private static BigInteger _x;
	private static BigInteger _a;
	private static BigInteger _c;
	private static int _n;
	
	//Erstellt die Anfangsparameter für den Generator
	public key_generator(BigInteger seed, BigInteger a, BigInteger c, int n) {
		this._x=seed;
		this._a=a;
		this._c=c;
		this._n=n;
	}
	//erzeugt den nächsten Teilkey mit n Bits aus dem Startwert _x
	private static BigInteger get_nextkey() 
	{
		BigInteger key = BigInteger.valueOf(0);
		int current_keylength=0;
		while(current_keylength<_n)//Die max. Länge soll n Bit sein.
		{
			//linearer Konguenzgenerator erzeugt x- Werte
			_x = _x.multiply(_a).add(_c).mod(power_base_2(_n));
			int x_length = _x.bitLength();
			//gleichlange Maske wird erstellt
			BigInteger mask =get_mask(x_length);
			//Überlagert die Maske auf den Wert von x 
			//Beispiel:
			//x    :10100011
			//mask :00001111
			//right:00000011
			BigInteger right = _x.and(mask);
			int r_length= right.bitLength();
			//right wird dem key angehängt 
			//Wie: Der vorherige key wird um die Bitlängen von right nach links verschoben(2^r_length)und anschließend mit right multipliziert.
			key=key.multiply(power_base_2(r_length)).add(right);
			current_keylength+=right.bitLength();
		}
		//Der Key wir genau auf die Bitlänge n zugeschnitten.
		return key.divide(power_base_2(current_keylength-(_n)));
	}
	
	private static BigInteger get_mask(int length) {
		//erstellt eine Maske, die von links bis zur Mitte aus Nullen und von der Mitte bis nach rechts aus Einsen besteht
		//Beispielinput: length: 8
		//Maske: 00001111
		int middel = (int)length/2;
		BigInteger mask= power_base_2(middel).subtract(BigInteger.valueOf(1));
		return mask;
	}
	//Es wird eine Potenzwert zurückgegeben. Der Eingabewert expo steht im Exponenten zur Basis 2. 
	private static BigInteger power_base_2(int expo) 
	{
		//Die Bits von 2^0=1 werden um expo-Einheiten nach links verschoben und so der Potenzwert 2^expo gebildet.
		//Beispielinput: expo= 4
		//00001 -> 10000
		return BigInteger.valueOf(1).shiftLeft(expo);
	}	
	public static String to_bin(BigInteger x)
	//Integer in eine Binärzahl umwandeln, unwichtig für die Funktion der Klasse an sich
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
		key_generator test = new key_generator(BigInteger.valueOf(88),BigInteger.valueOf(134518036),BigInteger.valueOf(1),128);
		BigInteger first_key = test.get_nextkey();
		BigInteger secound_key =test.get_nextkey();
		System.out.println(first_key+ " + "+secound_key);
		System.out.println(to_bin(first_key)+" + "+ to_bin(secound_key));
	} 

	

}
