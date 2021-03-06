package pseudo_key;
public class rand_key{
	
	public static String get_key(int seed, int l) 
	//erstellt Zufallskey mit Anfangsseed zur Verschlüsslung einer Nachricht der Länge l mit XOR.
	{
		//Anfangsstück des Keys wird erstellt.
		String key = to_bin(seed)+"";
		int x =seed;
		//n als noch zu füllende Länge
		int n = key.length();
		while(n<l) {
			//aus dem seed entstehende neue Pseudozufallszahl
			x=((x*145381457)+1)%(2^32);
			String bin_x= to_bin(x);
			//es wird sich die Hälfte alles unter x als Zahl gemerkt, aber nur die Hälfte zum Key hinzugefügt.
			String partOfkey=bin_x.substring(0, bin_x.length()/2);	
			key = key+partOfkey;
			n=key.length();
			if(n>=l) {
				//sobald die Länge l gefüllt wurde, wird der key mit der Länge l zurückgegeben.
				return key.substring(0, l);
			}
			
		}
		return key;
	
	}
	
	
	public static String to_bin(int x)
	//Integer in eine Binärzahl umwandeln (ohne spezielle "Bitlücken" aufzufüllen)
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