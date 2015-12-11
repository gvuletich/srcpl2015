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
import java.util.*;
import java.util.Random;


public class Strategy3 extends Strategy{
	protected int id;	
	
	public Strategy3(int n){
		id = n;
	}
	
	public int getID(){
		return id;
	}
	
	public Proposal InitialProposal(MentalState st){
		Random rand = new Random();
		System.out.println("SOY ESTRATEGIA "+getID());
		Solution s = st.getproblem().Solve();
		s.add(st.getproblem().getbyName("uti").maxIndex(),rand.nextInt(10) );
		
		Component app = s.apply(st.getproblem());		
		Component dif = st.getproblem().getbyName("rec").difference(app);
		
		int nrec = dif.nrec();
		int [] values = new int [nrec];
		for (int i=0;i<nrec;i++)
			values[i]=value(i,st);
		dif.filter4(values);
		
		Proposal p = new Proposal(dif);
		return p;
	}
	
	
	public Proposal CounterOffer(MentalState st,Proposal p){  
		Random rand = new Random();
		Solution snew = st.getproblem().Solve();
		//Solution snew = st.getproblem().SolveP(p);
			//snew.add(st.getproblem().getbyName("uti").minIndex(),2 );
			snew.add(st.getproblem().getbyName("uti").maxIndex(),rand.nextInt(3));	
			snew.add(0,rand.nextInt(2));
			snew.add(1,rand.nextInt(2));
			snew.add(2,rand.nextInt(2));
			snew.add(3,rand.nextInt(2));
			snew.add(4,rand.nextInt(2));
			
		if (st.getproblem().ObjectiveP(p).greaterThan(st.getproblem().Objetive())){
	
			Component proposed = new Component(p);
			
			
			
			Component necesary = snew.apply(st.getproblem());
			Component total = st.getproblem().getbyName("rec").sum(proposed);
			Component surplus = total.difference(necesary);	
			Component counteroffer = proposed.difference(surplus);
			
			int nrec = counteroffer.nrec();
			int [] values = new int [nrec];
			for (int i=0;i<nrec;i++)
				values[i]=value(i,st);
						
			
			counteroffer.reverse();
			counteroffer.filter4(values);
			Proposal coffer = new Proposal(counteroffer);
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
	
	private int value(int resource, MentalState st){
		//~ Component uti0=null;
		//~ try{
			 //~ uti0 = (Component)st.getproblem().getbyName("uti").clone();
		//~ }
		//~ catch(CloneNotSupportedException e){
			//~ ;
		//~ }
		
		Component uti0 = st.getproblem().getbyName("uti");
		
		Component m = st.getproblem().getbyName("mat");
		
		int nprod = uti0.nprod();
		int nrec = st.getproblem().getbyName("rec").nrec();
		
		int [] util = new int[nprod];

		for (int i=0; i<nprod; i++)
				util[i] = uti0.get(i);
		//~ System.out.println("ESTE ES UTILLLLLLLLLLLL: "+Arrays.toString(util));
		
		int [] prio = new int[nprod];
		
		for (int i=0; i<nprod; i++){
			prio[i]=minindex(util);
			util[prio[i]]=-1;
			//~ System.out.println("ESTE ES UTILLLLLLLLLLLL: "+Arrays.toString(util));
		}
		
		
		int [] cant = new int[nprod];
			
		for (int i=0;i<nprod;i++){
			int max = m.getM(resource,prio[i]);
			int pos = nrec-1;
			for (int n=0; n<nrec; n++){
				if ( m.getM(n,prio[i]) > max )
					pos--;
			}
			cant[prio[i]]=pos;
		}
		
		int result=0;
		for (int i=0; i<nprod; i++){
			result=result+i*cant[prio[i]];
		}
		
		//~ System.out.println("UTILIDAD ORDENADAAAAAAAAAAAAAAAAAAAAAAAAAAAA:"+Arrays.toString(prio));
		//~ System.out.println("DISTANCIA CERO EN CADA PRODUCTO:"+Arrays.toString(cant));
		//~ System.out.println("EL VALOR DEL RECURSO: "+resource+" ES :"+result);
		
		return result;
	}
	
	private int minindex(int [] v){
		int min=0;
		while (v[min]<0)
			min++;
		for (int i=0;i<v.length;i++)
			if ((v[i]>=0) & (v[i]<v[min]))
					min=i;
			else
				continue;
		return min;
	}
	
		
	
	
		
		
}
