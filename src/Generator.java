import java.io.File;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.*;
import java.util.*;
import java.util.Random;

// genera un escenario y lo vuelca a un xml


public class Generator {
	
	public static void main(String[] args) {
	   
	  int num = Integer.parseInt(args[1]);
	  Print pf = new Print();	
	  // Si hacemos que estos datos se lean de un archivo
	  // logramos que Generador dependa por completo de 
	  // la interfaz Input sin entrar en su implementacion
	  
	  // nprod, nrec, rlowbound, rupbound, 
	  // utilowbound, utilupbound, cantreclbound, cantrecupbound
	  List<Integer> list = new ArrayList<Integer>();
	  //list.add(8); list.add(6); list.add(10); list.add(10000);
	  //list.add(0); list.add(10); list.add(1); list.add(99);
	  list.add(5); list.add(4); list.add(10); list.add(10000);
	  list.add(10); list.add(100); list.add(0); list.add(100);
	  
	  int nagen = Integer.parseInt(args[0]);
	 
	  for (int i=0; i<num; i++){	
		  //File dir = new File("./xmls");
		  File dir = new File("./xmls/SCN"+to_str(i));
		  
		  if (!dir.exists()) {
				try{
					dir.mkdir();
				} 
				catch(SecurityException se){
					System.out.println("No se pudo crear el directorio /xmls");
				 }
		  }
		  
		  
		   // crea archivo con id del escenario
		  //~ Random rand = new Random();
		  //~ int id = rand.nextInt(899)+100;
		  
		  //pf.set(0,"./xmls/id.txt");
		  //pf.write("SCN"+Integer.toString(id));
		  
		  pf.set(0,"./xmls/SCN"+to_str(i)+"/id.txt");
		  pf.write("SCN"+to_str(i));
		  pf.unset();
		  
		  
		  
		  
		  for (int j=0; j<nagen; j++){
			Input i1 = new Input();
			i1.create_input(list);
			create_xml("./xmls/SCN"+to_str(i)+"/file"+j+".xml",i1);
			//i1.PrintMe();
		  }  
	  
	  }
	 
	  
	  
	}  
	
	//funcion que crea un xml
	public static void create_xml(String path, Input in){
		try {
 		File file = new File(path);
		JAXBContext jaxbContext = JAXBContext.newInstance(Input.class);
		Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
		jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		jaxbMarshaller.marshal(in, file);
		} catch (JAXBException e) {
			e.printStackTrace();
		}
	}
	
	public static String to_str(int n){
		if (n<10)
			return "00"+Integer.toString(n);
		else if (n>=10 && n<100)
			return "0"+Integer.toString(n);
		else
			return Integer.toString(n);
	}
	
}
