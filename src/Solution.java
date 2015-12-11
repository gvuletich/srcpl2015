import java.util.*;

// Implementa la solución del problema que cada agente debe resolver y por el cual
// participa en la negociación. 
// Para el caso de uso modelado, una solución es un vector solución x para el problema P.x <= r0 
// y se implementa como un vector de números enteros. El Solver se define para devolver estos objetos.

public class Solution{
	private int [] x;
	private int n;
	
	public void Solution(){
		;
	}
	
	public void create(int [] x1){
		x=x1;
		n=x1.length;
	}
	
	public int [] get(){
		return x;
	}
	
	public void set(int [] x1){
		x = x1;
	}
	
	public int size(){
		return n;
	}
	
	public Utility apply(Component c){
			if (n!=c.nprod())
				return null;
			int util=0;
			for (int i=0; i<n; i++)
				util=util+ (c.get(i)*x[i]);
			Utility u = new Utility();
			u.set(util);
			return u;
	}
	
	public Component apply(Problem P){     
		Matrix m = P.getbyName("mat");
		Component res = new Component(m.rows());
		for (int i=0; i<m.rows(); i++){
			int suma=0;
			for (int j=0; j<m.cols(); j++)
				suma = suma + m.getM(i,j)*x[j];
			res.set(i,suma);
		}
		return res;
	}
	
	public void PrintMe(){
		System.out.println("Solution");
		System.out.println(Arrays.toString(x));		
	}
	
	public void add(int n){
		for (int j=0; j<size(); j++)
			x[j] = x[j]+n;
	}
	
	public void unzerome(){
		for (int j=0; j<size(); j++)
			if (x[j] == 0)
			  x[j]++;
	}
	
	public void normalize(){
		int sum=0;
		for (int j=0; j<size(); j++)
			sum = sum + x[j];
		sum = sum/size();
		for (int j=0; j<size(); j++)
			if (x[j] == 0)
			  x[j]=sum;
		
	}
	
	
	public void add(int i,int n){
		x[i]=x[i]+n;
	}
	
	public void addmin(int n){
		int min=0;
		for (int i=0;i<size();i++)
			if (x[i]<x[min])
				min=i;
		x[min]=x[min]+n;
	}
	
	public void addmax(int n){
		int max=0;
		for (int i=0;i<size();i++)
			if (x[i]>x[max])
				max=i;
		x[max]=x[max]+n;
	}
		
}
