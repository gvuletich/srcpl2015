import java.util.*;
import java.io.*;
import jade.core.AID;

// una propuesta es un par (objeto_de_negociacion, emisor), es decir, es basicamente
// un objeto de negociación mas la información del agente que emite dicho objeto

public class Proposal implements Serializable{
	public NegObject obj;
	private AID proposer; 
	
	public Proposal(NegObject g){
		create(g);
	}
	
	public Proposal(){
		obj=null;
	}
	
	public Proposal(Component r){
		r.toProposal(this);
	}
	
	public void create(NegObject g){
		obj=g;
	}
		

				
	public void setProposer(AID name){		
		proposer = name;
	}
	
	public AID getProposer(){				
		return proposer;
	}
	
	public void reverse(){
		obj.reverse();
	}
	
	public int size(){
		return obj.size();
	}
	
	public void PrintMe(){
		if (obj==null)
			return;
		System.out.print("Proposal: ");
		obj.PrintMe();
		System.out.println("");
	}
	
	
}
