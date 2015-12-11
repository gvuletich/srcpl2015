public interface Matrix{

	// esta interfaz define un componente del problema denominado Matrix. 
	// luego la clase component debe implementarla para poder actuar como una matriz.
	// podriamos haber hecho un componente "fila" que represente una restriccion en particular
	// y hacer que el problema en vez de tener como componente una matrix de m x n tuviese m componentes
	// que implementen la interfaz "fila". Se hizo asi por una cuestion de simplicidad.
	
	public void setM(int i,int j, int n);
	
	public int getM(int i, int j);
	
	public int rows();
	
	public int cols();
	
	public void mergeM(Component m);
		
	public void PrintMatrix();
	
	public String SerialM();
				
	
}
