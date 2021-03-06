package pseudo_key;
public class rand_key{
	//Test
	public static long get_key(int seed, int key_length) 
	{
		long key = seed;
		long x =seed;
		int current_keylength= get_binlength(key);
		while(current_keylength<key_length)
		{
			x = ((x*134518036)+1)%(int) Math.pow(2,16);
			int n = get_binlength(x);
			int right_length = (int)Math.ceil((double)n/2);
			//Halbelänge des Binärcodes
			int cutter= (int) Math.pow(2.0,right_length);
			//Linke Bitseite wird weggeschnitten, x ist basisunabhängig
			long right= x%cutter;
			key=(key*cutter)+right;
			System.out.println("Key:" +key);
			System.out.println(to_bin(key));
			System.out.println("x:" +x);
			System.out.println(to_bin(x));
			System.out.println(to_bin(x).length());
			System.out.println(to_bin(right));
			System.out.println(to_bin(right).length());
			
	
			current_keylength+=right_length;
			System.out.println(current_keylength);
		}
		//rechts wegschneiden der Überflüssigen
		return key/(long)Math.pow(2,current_keylength-key_length);	
	}
	
	
	public static int get_binlength(long x) 
	{
		//Länge des Binärcodes von x wird bestimmt
		double double_n = Math.log(x)/ Math.log(2.f);
		return (int) Math.floor(double_n)+1;
		
	}

	
	public static String to_bin(long x)
	//Integer in eine Binärzahl umwandeln (ohne spezielle "Bitlücken" aufzufüllen)
	{
		String bin = "";
		if(x==0) 
		{
			bin="0";
		}
		while(x>0) 
		{
			long r=x%2;
			x= x/2;
			bin=r+bin;
		}
		return bin;
	}
	
	public static void main(String []args) 
	{ 
		long key=get_key(88,32+16);
		System.out.println(key);
		System.out.println(to_bin(key));
	} 
}