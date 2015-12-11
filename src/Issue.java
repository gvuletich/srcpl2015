import java.io.*;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


// Define el elemento básico de negociación. 
// En este caso la modelamos como una variable id que identifica al recurso
// y una variable val que identifica la cantidad de dicho recurso.

@XmlRootElement(name="issue")
public class Issue implements Serializable, Cloneable{
	@XmlElement(name="id")
	private int id;
	@XmlElement(name="val")
	private int val;
	@XmlElement(name="comp")
	private Component c;
	
	public Issue(){
		;
	}
	
	public Issue(int i, int v){
		id=i;
		val=v;
		c=null;
	}
	
	public Issue(Component c1, int...num){
		c=c1;
		id=num[0];
		val=num[1];
	}
	
	public void reverse(){
		val=val*(-1);
	}
	
	public int getId(){
		return id;
	}
	
	public int getVal(){
		return val;
	}
	
	public Component getComponent(){
		return c;
	}
	
	public boolean equal(Issue i){
		return ((id==i.id)&&(val==i.getVal())&&(c==i.getComponent()));
	}
	
	public void PrintMe(){
		//System.out.println("Issue:");
		System.out.print(" "+val+",");
		//System.out.println("This issue belongs to a Component of the "+c.getClass());
	}
}
