import java.io.File;
import java.util.*;
import lpsolve.*;

// en este caso importa la librería lpsolve e implementa los métodos generales de un solver de un 
// problema de negociación en base a la API del solver que utiliza para este problema en concreto.

public class Solver{
	
	private Problem pr;
	private LpSolve solver;
	Matrix m;
	Utilfun u;
	Resources r;
	int cols,rec,nprod;
	
	public Solver(Problem p){
		pr = p;
		m = pr.getbyName("mat");
		u = pr.getbyName("uti");
		r = pr.getbyName("rec");
		cols = m.cols();
		rec = r.nrec();
		nprod = u.nprod();
	}
	
	public Solution Solve(){
		Solution sol = new Solution();
		int [] result;
		double [] res;
			
		result = new int[cols];
		try {
			solver = LpSolve.makeLp(0, cols);  		
			for (int k=1; k<=cols; k++)
				solver.setInt(k, true);								
			for (int i=0;i<rec; i++){
				String c = "";
				for (int j=0;j<cols;j++)
					c = c+" "+m.getM(i,j);
				solver.strAddConstraint(c, LpSolve.LE, r.get(i));		
			}
			for (int i=0;i<rec; i++){							
				String c = "";
				for (int j=0;j<cols;j++)
					c = c+" "+m.getM(i,j);
				solver.strAddConstraint(c, LpSolve.GE, 0);
			}
			for (int i=0;i<cols; i++){					
				String c = "";
				for (int j=0;j<cols;j++)
					if ( i!=j ) 
						c = c+" 0";
					else
						c = c+" 1";
				solver.strAddConstraint(c, LpSolve.GE, 0);
			}
			String of = "";
			for (int i=0;i<nprod;i++)				
				of = of+" "+u.get(i);
						
			solver.strSetObjFn(of);
			solver.setMaxim();								
			solver.setVerbose(0);
			solver.solve();
			res = solver.getPtrVariables();
			for (int j=0; j<nprod; j++)
					result[j] = (int)res[j];
			solver.deleteLp();
		}catch (LpSolveException e) {
			e.printStackTrace();
			System.out.println("Fallo al resolver Problem");
		}
		sol.create(result);
		return sol;
	}
	
	public Solution Solve(List<Problem> lp){
		Solution sol = new Solution();
		int [] result;
		double [] res;
			
		result = new int[cols];
		try {
			solver = LpSolve.makeLp(0, cols);  		
			for (int k=1; k<=cols; k++)
				solver.setInt(k, true);								
			for (int i=0;i<rec; i++){
				String c = "";
				for (int j=0;j<cols;j++)
					c = c+" "+m.getM(i,j);
				solver.strAddConstraint(c, LpSolve.LE, r.get(i));		
			}
			for (int i=0;i<rec; i++){							
				String c = "";
				for (int j=0;j<cols;j++)
					c = c+" "+m.getM(i,j);
				solver.strAddConstraint(c, LpSolve.GE, 0);
			}
			for (int i=0;i<cols; i++){					
				String c = "";
				for (int j=0;j<cols;j++)
					if ( i!=j ) 
						c = c+" 0";
					else
						c = c+" 1";
				solver.strAddConstraint(c, LpSolve.GE, 0);
			}
			
			int longitud = lp.size();
			Iterator itr = lp.iterator();
			int singlecols = 5;
			int iter = 0;
			
			while (itr.hasNext()){
				String c="";
				Problem ptmp = (Problem) itr.next();
				System.out.println("ESSTOSS SON LOS PROBLEMASSSSSSSSSSSSSSSSSSSSS");
				ptmp.PrintMe();
				
				if (iter==0)
					singlecols = ptmp.getbyName("uti").nprod();
				for (int k=0; k<cols; k++){
					if (k>=(iter*singlecols) && k<((iter+1)*singlecols))
						c = c+" "+ptmp.getbyName("uti").get(k-iter*singlecols);
					else
						c = c+" 0";
				}
				solver.strAddConstraint(c, LpSolve.GE, ptmp.Objetive().get());
				System.out.println("Equation extra: "+c+" >= "+ptmp.Objetive().get());
				iter++;
			}
			
			
			String of = "";
			for (int i=0;i<nprod;i++)				
				of = of+" "+u.get(i);
						
			solver.strSetObjFn(of);
			solver.setMaxim();								
			solver.setVerbose(0);
			solver.solve();
			res = solver.getPtrVariables();
			for (int j=0; j<nprod; j++)
					result[j] = (int)res[j];
			solver.deleteLp();
		}catch (LpSolveException e) {
			e.printStackTrace();
			System.out.println("Fallo al resolver Problem");
		}
		sol.create(result);
		return sol;
	}
	
}
