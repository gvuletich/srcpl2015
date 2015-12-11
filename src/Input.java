import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.*;
import java.util.Random;

// Input es una clase intermedia que utilizan tanto el Generador de escenarios como un 
// Problema para codificar y decodificar los archivos xml que contienen el escenario de negociacion

@XmlRootElement(name="input")
public class Input implements Cloneable{
	@XmlElement(name="parts")
	public ArrayList<Component> parts;
	
	
	public void Input(){
		;//System.out.println("");
	}
	
	public void create(){
		parts = new ArrayList<Component>();
	}
	
	
	public void create_input(List<Integer> l) {
		Integer nprod;
		int nrec;
		int lrec,urec;
		int luti,uuti;
		int lprod,uprod;
		
		Iterator<Integer> itr = l.iterator();
		if (itr.hasNext()){
			nprod = itr.next();
			nrec = itr.next();
			lrec = itr.next();
			urec = itr.next();
			luti = itr.next();
			uuti = itr.next();
			lprod = itr.next();
			uprod = itr.next();
		}
		else {
			System.out.println("Bad arguments for create_input function!");
			return;
		}
		
		Random rand = new Random();
		Component m1 = new Component(nrec,nprod);
		m1.setName("mat");
		Component r1 = new Component(nrec);
		r1.setName("rec");
		Component u1 = new Component(nprod);		
		u1.setName("uti");
		for (int i=0; i<nrec; i++)
			for (int j=0; j<nprod; j++)
				m1.setM(i,j,rand.nextInt(uprod-lprod)+1);
					
		for (int i=0; i<nrec; i++)
			r1.set(i,rand.nextInt(urec-lrec)+1);
			
		for (int i=0; i<nprod; i++)
			u1.set(i,rand.nextInt(uuti-luti)+1);
		
		this.create();
		
		parts.add(m1);
		parts.add(r1);
		parts.add(u1);
		
		PrintMe();
		
	}
	
	public void PrintMe(){
		System.out.println("Input CREADO");
		Iterator<Component> itrinput = parts.iterator();
	    while (itrinput.hasNext()){
			Component cmp =itrinput.next();
			if (cmp.getName().equals("mat"))
				cmp.PrintMatrix();
			else
				cmp.PrintMe();
		}
	}
	
	@Override
	protected Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
}

