import java.io.File;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.*;
import java.util.*;
import lpsolve.*;


// como se menciona en la clase Component, un problema en la plataforma se concibe como una lista
// de partes P=[p1,p2,...,pn] donde cada parte es de tipo component pero implementa una interfaz 
// correspondiente al tipo de componente del problema. En el caso del mercado un problema constará
// de una lista de componentes P=[Matrix,Resources,Utilfun], donde esos tres tipos son interfaces que
// definen a una Matrix, un Resource o una UtilFun (funcion objetivo) y luego son implementados por la 
// clase componente.
// Ademas la clase Problem define los métodos generales de un problema de negociación como se lista debajo.

public class Problem  implements Cloneable{
	private Input in;
	public String path;
	private ArrayList<Component> parts;
	
	public Problem(String s){
		path=s;
		Input in1;
		parts = new ArrayList<Component>();
		
		File xml = new File(s);
		
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(Input.class);
			Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
			in1 = (Input) unmarshaller.unmarshal(xml);
		} catch (JAXBException e) {
			e.printStackTrace();
			System.out.println("Error al crear objeto Problem desde archivo xml");
			return;
	    }
	    
	   in = in1;
	   this.create();
	}
	
	public Problem(Input in1){
		in = in1;
		//create();
	}
		
	public void create(){
	    ListIterator<Component> itrinput = in.parts.listIterator();
	    while (itrinput.hasNext())
			parts.add(itrinput.next());
		while(itrinput.hasPrevious())
			itrinput.previous();
	}
	
	public Input getInput(){
		return in;
	}
	
	public void setInput(Input i){
		in = i;
	}
			
	public Solution Solve(){
		Solver solver = new Solver(this);
		Solution sol = solver.Solve();
		return sol;
	}
	
	public Solution Solve(List<Problem> lp){
		Solver solver = new Solver(this);
		Solution sol = solver.Solve(lp);
		return sol;
	}
	
	public Solution SolveP(Proposal p){
		Problem ptmp = new Problem(path);
		
		synchronized(System.out) {
		//System.out.println("EL PROBLEMA CREADO COMPLETO ES: ");
		//ptmp.PrintMe();	
		//System.out.println("Entro a SolveP con los recursos: ");
		//getbyName("rec").PrintMe();
		
		ptmp.getbyName("rec").nullme();
		getbyName("rec").mixMe(ptmp);
			
		//System.out.println("EL PROBLEMA CREADO COMPLETO ES: ");
		//ptmp.PrintMe();
		
		Component rtmp = new Component(p);
		rtmp.mixMe(ptmp);
		
		//System.out.println("Salgo de SolveP con los recursos: ");
		//getbyName("rec").PrintMe();
		}
	
		return ptmp.Solve();						
	}
			
	public Utility Objetive(){
		return Solve().apply(getbyName("uti"));
	}
	
	public Utility Objetive(List<Problem> lp){
		return Solve(lp).apply(getbyName("uti"));
	}
	
	public Utility ObjectiveP(Proposal p){
		return SolveP(p).apply(getbyName("uti"));
	}
	
	
	public void Accept(Proposal p){;
		Component rtmp = new Component(p);
		//r.mix(rtmp);
		rtmp.mixMe(this);
	}
	
	public void modifybyName(Component c){	// VERIFICAR QUE ANDA
		int index = parts.indexOf(getbyName(c.getName()));
		System.out.print("EL INDICE DE REC ES: "+index);
		parts.set(index,c);
	}
	
	
	public Component getbyId(int id){ 
	for (int i = 0; i < parts.size(); i++) 
		if (parts.get(i).getId() == id) 
			return parts.get(i);
		return null;
	}	
	
	public Component getbyName(String str){ 
	for (int i = 0; i < parts.size(); i++) 
		if (parts.get(i).getName().equals(str)) 
			return parts.get(i);
		return null;
	}
	
	public void PrintMe(){
		synchronized (System.out) {
		System.out.println("Problem:");
		in.PrintMe();
		}
	}
	
	public String Serialize(){
		return getbyName("rec").SerialR() + getbyName("uti").SerialU() + Objetive().SerialUt();
	}
	
	@Override
	protected Object clone() throws CloneNotSupportedException {
		Problem cloned = (Problem)super.clone();
		cloned.setInput((Input)cloned.getInput().clone());
	return cloned;
	}
					
}
	    
