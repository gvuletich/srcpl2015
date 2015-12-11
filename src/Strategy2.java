import java.util.Vector;
import java.util.Enumeration;
import java.io.File;
import jade.core.AID;
import java.util.Vector;
import java.util.Enumeration;
import jade.core.Agent;
import jade.core.behaviours.*;
import jade.lang.acl.ACLMessage;
import jade.domain.FIPAAgentManagement.*;
import java.io.*;
import jade.lang.acl.UnreadableException;

public class Strategy2 extends Strategy{
	protected int id;	
	
	public Strategy2(int n){
		id = n;
	}
	
	public int getID(){
		return id;
	}
	
	public Proposal InitialProposal(MentalState st){
		System.out.println("SOY ESTRATEGIA "+getID());
		Solution s = st.getproblem().Solve();
		Component app = s.apply(st.getproblem());		
		Component dif = st.getproblem().getbyName("rec").difference(app);
		Proposal p = new Proposal(dif);
		return p;
	}
	
	
	public Proposal CounterOffer(MentalState st,Proposal p){  

		Solution s = st.getproblem().SolveP(p);
		
		System.out.println("ESTRATEGIA1 - Sol actual: "+st.getproblem().Objetive().SerialUt()+" Sol propuesta: "+st.getproblem().ObjectiveP(p).SerialUt());
		
		if (st.getproblem().ObjectiveP(p).greaterThan(st.getproblem().Objetive())){
	
			Component proposed = new Component(p);
			Component necesary = st.getproblem().SolveP(p).apply(st.getproblem());
			Component total = st.getproblem().getbyName("rec").sum(proposed);
			Component surplus = total.difference(necesary);	
			Component counteroffer = proposed.difference(surplus);
						
			Proposal coffer = new Proposal(counteroffer);
			coffer.reverse();
			return coffer;		
			
		}
		else
			return null;
	}
	
	public Proposal BestProposal(MentalState st, Enumeration e, Vector responses){
		
		Proposal bestProposal = null;
		Utility bestUtility = st.getproblem().Objetive();
		while (e.hasMoreElements()) {
			ACLMessage msg = (ACLMessage) e.nextElement();		
			if (msg.getPerformative() == ACLMessage.PROPOSE) {								
				try{
					if ("JavaSerialization".equals(msg.getLanguage())) {
						Proposal proposal = (Proposal)msg.getContentObject();
						Utility tmp = st.getproblem().ObjectiveP(proposal);
						if(tmp.greaterThan(bestUtility)){
							bestProposal=proposal;
							bestUtility=tmp;
							bestProposal.setProposer(msg.getSender());
						}
					}
					else
						System.out.println("Serializable not received!");
				} catch(UnreadableException e3){
					System.err.println("Catched exception "+e3.getMessage());
				}
			}
		}		
		return bestProposal;
	}
	
	
	
	public void PrintMe(){
		System.out.println("Strategy: "+id);
	}
}
