package pseudo_key;

import java.math.BigInteger;

//generates a key of any length depending on seed
public class key_generator {
	private static BigInteger _x;
	private static BigInteger _a;
	private static BigInteger _c;
	private static int _n;
	
	//constructor 
	public key_generator(BigInteger seed, BigInteger a, BigInteger c, int n) {
		this._x=seed;
		this._a=a;
		this._c=c;
		this._n=n;
	}
	//creates next key with n bits dependig on starting value _x
	private static BigInteger get_nextkey() 
	{
		BigInteger key = BigInteger.valueOf(0);
		int current_keylength=0;
		while(current_keylength<_n)//until bit length bigger or equal to n
		{
			//linear congruence generator 
			_x = _x.multiply(_a).add(_c).mod(power_base_2(_n));
			int x_length = _x.bitLength();
			//mask of same length
			BigInteger mask =get_mask(x_length);
			//mask overlaps x values
			//example:
			//x    :10100011
			//mask :00001111
			//right:00000011
			BigInteger right = _x.and(mask);
			int r_length= right.bitLength();
			//right is appended to key
			//How: previous key is shifted by the bits of right(2^r_length)and afterwards right is add to it
			key=key.multiply(power_base_2(r_length)).add(right);
			current_keylength+=right.bitLength();
		}
		//key is cut to n bits
		return key.divide(power_base_2(current_keylength-(_n)));
	}
	//creats a mask, exists consists of zeros bits from left to middle and from middle to consists of ones
	private static BigInteger get_mask(int length) {
		//example	: 
		//input		: length: 8
		//mask		: 00001111
		int middel = (int)length/2;
		BigInteger mask= power_base_2(middel).subtract(BigInteger.valueOf(1));
		return mask;
	}
	//returns a power, input as exponent to base 2 
	private static BigInteger power_base_2(int expo) 
	{
		//example: input: expo= 4
		//00001 -> 10000
		return BigInteger.valueOf(1).shiftLeft(expo);
	}	
	//converts integer to bon 
	public static String to_bin(BigInteger x)
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
