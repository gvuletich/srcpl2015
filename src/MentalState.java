// define el estado mental de un agente y es agregada por el agente. consta de una 
// referencia a un problema ya creado, y una referencia a una estrategia concreta.

public class MentalState {
	private Problem p;
	private Strategy s;
	private int nagents;
	private int nrounds;
	private Proposal lastsnd;
	private Proposal lastrcv;
	private int id;
	
	public MentalState(Problem p1,Strategy s1){
		p = p1;
		s = s1;
	}
	
	public void setstrat(Strategy e){
		s=e;
	}
	
	public void setnagen(int n){
		nagents=n;
	}
	
	public void setnrounds(int n){
		nrounds=n;
	}
	
	public void setlastsnd(Proposal p){
		lastsnd=p;
	}
	
	public void setlastrcv(Proposal p){
		lastrcv=p;
	}
	
	public Proposal getlastsnd(){
		return lastsnd;
	}
	
	public Proposal getlastrcv(){
		return lastrcv;
	}
	
	public int getnagents(){
		return nagents;
	}
	
	public Strategy getstrat(){
		return s;
	}
	
	public Problem getproblem(){
		return p;
	}
	
	public void PrintMe(){
		System.out.println("Mental State:");
		p.PrintMe();
		s.PrintMe();
	}
	
	public String Serialize(){
		return Integer.toString(s.getID())+" "+p.Serialize();
	}
}
