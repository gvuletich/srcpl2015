import java.util.*;
import java.io.*;
import java.util.Random;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

// un problema en nuestra plataforma se representa como una lista de componentes.
// La taxonomia general de un problema es: P=(funcion_objetivo, variables, restricciones) . 
// Lo hacemos mas general, y hacemos que P=[p1,p2...pn] donde p1 puede ser una interfaz que 
// define los métodos que moldean a la clase component como una función objetivo, p2 se define como 
// una interfaz que moldea a la clase component como una restriccion (sea unitaria o un conjunto de restricciones en forma de matriz)
// etc. 
// En la taxonomia mencionada, deberiamos definir una interfaz que defina los métodos que hacen que un componente
// actue como una funcion objetivo, otra interfaz que define los métodos para que un componente actue como un conjunto 
// de restricciones, etc. 
// Para el problema del mercado por ejemplo, definimos una interfaz Matrix,una interfaz Resources y una Utilfun, donde cada
// interfaz le dá a la clase component las caracterísitas para que implemente un conjunto de recursos, una funcion de utilidades
// o una matrix.

@XmlRootElement(name="component")
public class Component implements Matrix, Utilfun, Resources , Serializable, Cloneable{
	
	@XmlElement(name="clist")
	protected ArrayList<Component> lc;
	
	@XmlElement(name="ilist")
	protected ArrayList<Issue> li;
	
	@XmlElement(name="cname")
	private String cname;
	
	@XmlElement(name="cid")
	private int cid;
	
	@XmlElement(name="depth")
	private int depth;
	
	@XmlElement(name="width")
	private int width;
	
	public Component(){
		lc = new ArrayList<Component>();
		li = new ArrayList<Issue>();
		depth=0;
		width=0;
	}
	
	
	// A unidimensional component of width n
	public Component(int n){
		this();
		for (int i=0; i<n; i++)
			addissue(new Issue(i,0));
	}
	
	// A bidimensional component of depth m each with width n
	public Component(int m, int n){
		this();
		for (int i=0; i<m; i++) {
			Component fila = new Component();
			for (int j=0; j<n; j++)
				fila.addissue(new Issue(j,0));
			addcomp(fila);
		}
	}
	
	public void addissue(Issue i){
		li.add(i);
		width++;
	}
	
	public void delissue(Issue i){
		li.remove(i);
		width--;
	}
	
	
	public void addissue(int i, int j, Issue e){
		lc.get(i).li.add(e);
	}
		
	
	public Issue getissue(int n){
		if (n>width-1) {
			System.out.println("Non existent issue: "+n);
			return null;
		}
		return li.get(n);
	}
	
	public Issue getissue(int c, int n){
		return getcomp(c).getissue(n);
	}
	
	public void setissue(int n, Issue i){
		li.set(n,i);
	}
	
	public void setissue(int c, int n, Issue i){
		lc.get(c).setissue(n,i);
	}
	
	public void addcomp(Component c){
		lc.add(c);
		depth++;
	}
	
	public void delcomp(Component c){
		lc.remove(c);
		depth--;
	}
	
	private Component getcomp(int n){
		if (n>depth-1) {
			System.out.println("Non existent component.");
			return null;
		}
		return lc.get(n);
	}
	
	public void setId(int i){
		cid=i;
	}
	
	public void set(int i,int n){
		setissue(i,new Issue(getissue(i).getId(),n));
	}
	
	public int get(int i){
		return getissue(i).getVal();
	}
	
	public int getId(){
		return cid;
	}
	
	public void setName(String s){
		cname=s;
	}
	
	public String getName(){
		return cname;
	}
		
	// ESTO NO DEBERIA LLAMARSE ASIS
	public int size(){
		return li.size();
	}
	
	public void PrintMe(){
		if (width>0){
			//System.out.println("Component: w="+width+" d="+depth);
			System.out.println("li: ");
			Iterator<Issue> itr = li.iterator();
			while (itr.hasNext())
				itr.next().PrintMe();
			//System.out.println("fin");	
		}
		if (depth>0){
			Iterator<Component> itr = lc.iterator();
			while (itr.hasNext())
				System.out.println("lc: ");
				itr.next().PrintMe();
		}
		
	}
	
	public boolean equal(Component c){
		boolean bli = true;
		boolean blc = true;
		for (int i=0; i<li.size(); i++)
			bli = bli && c.li.get(i).equal(li.get(i));
		for (int j=0; j<lc.size(); j++)
			blc = blc && c.lc.get(j).equal(lc.get(j));
		return (bli && blc);
		
	}
	
	// Only for resources by now
	public void toProposal(Proposal p){
			int n = size();
			NegObject neg = new NegObject();
			for (int i=0; i<n; i++){
				Issue tmp = new Issue(getissue(i).getId(),getissue(i).getVal());
				neg.add(tmp);
			}
			p.create(neg);	
		
	}
	
	public Component(Proposal p){
			this(p.size());
			Iterator<Issue> itr = p.obj.l.iterator();
			
			while (itr.hasNext()){
				Issue iss = itr.next();
				int i=iss.getId();
				int n=iss.getVal();
				set(i,n);
			}	
	}
		
	
	// Implements concrete component Matrix
	public void setM(int i,int j, int n){
		setissue(i,j,new Issue(getissue(i,j).getId(),n));
	}
	
	public void updateM(int i,int j, int n, int id){
		if (i<rows() && j<cols())
			setissue(i,j,new Issue(id,n));
		else
			addissue(i,j,new Issue(id,n));
	}
	
	public int getM(int i, int j){
		return getissue(i,j).getVal();
	}
	
	public int rows(){
		return lc.size();
	}
	
	public int cols(){
		return lc.get(0).li.size();
	}
	
	public String SerialM(){
		return null;
	}
	
	public void mergeM(Component m){
		int n1 = m.cols();
		int m1 = m.rows();
		int n2 = m.cols();
		int m2 = m.rows();
		for (int i=0; i<m1; i++)
			for (int j=0; j<n1; j++){
				lc.get(i).li.add(m.lc.get(i).li.get(j));
				lc.get(i).width++;
				//updateM(i,j,m.getissue(i,j-n1).getVal(), m.getissue(i,j-n1).getId());
			}	
	}
	
			
	// end implement of Matrix
	
	
	// Implements concrete component Utilfun
	public int nprod(){
		return li.size();
	}
	
	public String SerialU(){
		String s = "";
		for (int i=0;i<nrec();i++)
			s = s+getissue(i).getVal()+" ";
		return s;
	}
	
	public void mergeU(Component u){
		int n1 = nprod();
		int n2 = u.nprod();
		for (int i=0; i<n2; i++){
			li.add(u.li.get(i));
			width++;
		}
			//setissue(i+n1,u.getissue(i));
	}
	// end implement of Utilfun
	
	
	// Implements concrete component Resources
	public Component sum(Component r1){
		Component res = new Component(r1.size());
		for (int i=0; i<r1.size(); i++)
			res.set(i,get(i)+r1.get(i));
		return res;
	}
	
	public Component same(Component r1){
		Component res = new Component(r1.size());
		for (int i=0; i<r1.size(); i++)
			res.set(i,r1.get(i));
		r1.setName("rec");
		//r1.setName("rec");
		return res;
	}
	
	public void mergeR(Component r){
		int n1 = nrec();
		int n2 = r.nrec();
		for (int i=0; i<n2; i++)
			setissue(i+n1,r.getissue(i));
	}
	
	public void nullme(){
		for (int i=0; i<size(); i++)
			set(i,0);
	}
	
	public Component difference(Component r1){
		Component res = new Component(r1.nrec());
		for (int i=0; i<r1.nrec(); i++)
			res.set(i,get(i)-r1.get(i));
		return res;
	}
	
	public int product(Component r1){
		Component res = new Component(r1.nrec());
		for (int i=0; i<r1.nrec(); i++)
			res.set(i,get(i)*r1.get(i));
		
		int ret=0;
		for (int i=0; i<r1.nrec(); i++)
			ret = ret + res.get(i);
		return ret;
		
	}
	
	public int nrec(){
		return li.size();
	}
	
	public int minIndex(){
		int min=0;
		for (int i=1;i<size();i++)
			if (get(i) < get(min))
				min = i;
		return min;
	}
	
	public int maxIndex(){
		int max=0;
		for (int i=1;i<size();i++)
			if (get(i) > get(max))
				max = i;
		return max;
	}
		
	public void mix(Component c){
			for (int i=0; i<c.nrec(); i++)
				set(i,get(i)+c.get(i));
	}
	
	public boolean smallThan(Component c){
			boolean v=true;
			for (int i=0; i<c.nrec(); i++)
				v=v&&(get(i) <= c.get(i));
			return v;
	}
	
	public boolean bigThan(Component c){
			boolean v=true;
			for (int i=0; i<c.nrec(); i++)
				v=v&&(get(i) >= c.get(i));
			return v;
	}
	
	public void mixMe(Problem p){
		(p.getbyName("rec")).mix(this);
	}
	
	public void add(int n){
		for (int i=0; i<nprod(); i++)
				set(i,get(i)+n);
	}
	
	public void unify(){
		for (int i=0; i<nprod(); i++)
				set(i,get(i)/2);
	}
	
	public void reverse(){
		for (int i=0; i<nprod(); i++)
				set(i,get(i)*(-1));
	}
	
	public void divby(int n){
		for (int i=0; i<nprod(); i++)
				set(i,get(i)/n);
	}
	
	public void randomize(){
		Random rand = new Random();
		for (int i=0; i<nprod(); i++){
				int k = rand.nextInt(nprod());
				if (k!=i){
					int n=get(i);
					if (n<0){
						n = n*(-1);
						n = rand.nextInt(n);
						n = n*(-1);
					}
					else
						n = rand.nextInt(n+1)-1;				
					set(i,n);
				}
				else
					set(i,0);
		}
	}
	
	public void filter(int [] v){
		int med=0;
		for(int i=0;i<nrec();i++)
			med = med + v[i];
		med = med/nrec();
		for (int i=0; i<nrec(); i++){
				if(get(i)>=0 && v[i]>=med)
					//~ set(i,1);
					set(i,get(i)/2);
				if(get(i)>=0 && v[i]<med)
					;
				if(get(i)<0 && v[i]>=med)
					set(i,get(i)*2);
				if(get(i)<0 && v[i]<med)
						;
		}
	}
	
	public void filter2(int [] v){
		int med=0;
		for(int i=0;i<nrec();i++)
			med = med + v[i];
		med = med/nrec();
		for (int i=0; i<nrec(); i++){
				if(get(i)>=0 && v[i]>=med)
					//~ set(i,1);
					set(i,get(i)/2);
				if(get(i)>=0 && v[i]<med)
					;
				if(get(i)<0 && v[i]>=med)
						;
				if(get(i)<0 && v[i]<med)
						;
		}
	}
	
	
	public void filter3(int [] v){
		int med=0;
		for(int i=0;i<nrec();i++)
			med = med + v[i];
		med = med/nrec();
		for (int i=0; i<nrec(); i++){
				if(v[i]>=med)
					set(i,get(i)/2*(-1));
				if(v[i]<med)
					set(i,get(i)/2);
		}
	}
	
	public void filter4(int [] v){
		int med=0;
		for(int i=0;i<nrec();i++)
			med = med + v[i];
		med = med/nrec();
		for (int i=0; i<nrec(); i++)
				if(get(i)>=0)
					set(i,get(i)/(v[i]/med+1));
				else 
					set(i,get(i)*(v[i]/med+1));
	}
		
	
	public void PrintMatrix(){
		System.out.println("Matrix:");
		for (int i=0; i<rows(); i++){
			for (int j=0; j<cols(); j++)
				System.out.print("["+getissue(i,j).getVal()+"]");
			System.out.println("");
		}
	}	
	
	
	
	public String SerialR(){
		String s = "";
		for (int i=0;i<nrec();i++)
			s = s+getissue(i).getVal()+" ";
		return s;
	}
	
	
	
	@Override
	protected Object clone() throws CloneNotSupportedException {
		Component cloned = (Component)super.clone();
		//cloned.setInput((Input)cloned.getInput().clone());
	return cloned;
	}
			
}	
	
