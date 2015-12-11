import java.util.*; 
import java.io.*;

// un objeto de negociacion es una lista de "cuestiones", es decir, en un objeto de 
// negociación uno puede enumerar una serie de cuestiones sobre las que está negociando
// con dicho objeto

public class NegObject implements Serializable{
	protected ArrayList<Issue> l;
	
	public NegObject(){
		l = new ArrayList<Issue>();
	}
	
	public void add(Issue i){
		l.add(i);
	}
	
	public void PrintMe(){
		System.out.print("NegotiationObject: [");
		Iterator<Issue> itr = l.iterator();
		while (itr.hasNext())
			itr.next().PrintMe();
		System.out.print(" ]");
	}
	
	public void reverse(){
		Iterator<Issue> itr = l.iterator();
		while (itr.hasNext())
			itr.next().reverse();
	}
	
	public int size(){
		return l.size();
	}
}
	
	
