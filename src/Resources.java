import java.util.*;


// Esta interfaz define los métodos necesarios para considerar a un Component como un 
// vector de recursos. Luego la clase Component implementa estos métodos para poder actuar como dicho vector.

public interface Resources{

	public Component sum(Component r1);
	
	public Component difference(Component r1);
	
	public void mix(Component c);
	
	public void mergeR(Component r);
	
	public void mixMe(Problem p);
	
	public int get(int i);
	
	public int nrec();
	
	public String SerialR();

}
