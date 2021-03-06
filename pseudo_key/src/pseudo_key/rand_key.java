package pseudo_key;
public class rand_key{
	
	public static String get_key(int seed, int l) 
	//erstellt Zufallskey mit Anfangsseed zur Verschl�sslung einer Nachricht der L�nge l mit XOR.
	{
		//Anfangsst�ck des Keys wird erstellt.
		String key = to_bin(seed)+"";
		int x =seed;
		//n als noch zu f�llende L�nge
		int n = key.length();
		while(n<l) {
			//aus dem seed entstehende neue Pseudozufallszahl
			x=((x*145381457)+1)%(2^32);
			String bin_x= to_bin(x);
			//es wird sich die H�lfte alles unter x als Zahl gemerkt, aber nur die H�lfte zum Key hinzugef�gt.
			String partOfkey=bin_x.substring(0, bin_x.length()/2);	
			key = key+partOfkey;
			n=key.length();
			if(n>=l) {
				//sobald die L�nge l gef�llt wurde, wird der key mit der L�nge l zur�ckgegeben.
				return key.substring(0, l);
			}
			
		}
		return key;
	
	}
	
	
	public static String to_bin(int x)
	//Integer in eine Bin�rzahl umwandeln (ohne spezielle "Bitl�cken" aufzuf�llen)
	{
		
		String bin = "";
		if(x==0) 
		{
			bin="0";
		}
		while(x>0) 
		{
			int r=x%2;
			x= x/2;
			bin=r+bin;
		}
		return bin;
	}
	
	public static void main(String []args) 
	{ 
		String key=get_key(5,10);
		System.out.print(key);
	} 
}