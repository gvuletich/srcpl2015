import java.util.Vector;
import java.util.Enumeration;

// clase abstracta que define los métodos de una estrategia.
// luego cada estrategia debe implementarla.

public abstract class Strategy{
	
	public abstract Proposal InitialProposal(MentalState st);	
	
	public abstract Proposal CounterOffer(MentalState st,Proposal p);
	
	public abstract Proposal BestProposal(MentalState st, Enumeration e, Vector responses);
	
	public abstract void PrintMe();
	
	public abstract int getID();
	
}
