import java.util.*;

// Define la utilidad de una solución al problema de un agente. 
// Para el caso de uso modelado es un número entero resultado de aplicar la función de utilidades
// de una empresa a una Solución.

public class Utility{
	public int u;
	
	public void Utility(){
		;
	}
	
	public void set(int u1){
		u=u1;
	}
	
	public int get(){
		return u;
	}
	
	public boolean greaterThan(Utility u1){
		return this.u > u1.u;
	}
	
	public boolean equalThan(Utility u1){
		return this.u == u1.u;
	}
	
	public void PrintMe(){
		System.out.print("Utilidad: "+u);
	}
	
	public String SerialUt(){
		return Integer.toString(u);
	}
}
