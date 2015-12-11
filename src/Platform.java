import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.wrapper.AgentContainer;
import jade.core.Agent;
import jade.wrapper.AgentController;
import java.util.*;
import java.io.File;

import java.io.File;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.*;


// lanza al controlador y a los agentes con los parámetros adecuados

public class Platform implements Values {
	public static void main(String[] args) {
		int debug=0;
		int strat=1;
		String path="./xmls/SCN000/";
		
		if (args.length==0){
			System.out.println("Missing parameter. Number of agents MUST be spcified. Try again!");
			return;
		}
		int nagents= Integer.parseInt(args[0]);
		if  (args.length>1){
			String generate = args[1];
			if (Arrays.asList(args).contains("g")){
				int ind = Arrays.asList(args).indexOf("g");
				ind = ind +1;
				if (args.length<ind+1){
					System.out.println("Unspecified value of scenarios. Try again!");
					return;
				}
				
				if (Integer.parseInt(args[ind]) > 0 & Integer.parseInt(args[ind])<=999)
					debug = Integer.parseInt(args[ind]);
				else{
					System.out.println("Scenario value must be in [1...999]");
					return;
				}
				String [] argg = new String[2];
				argg[0] = Integer.toString(nagents);
				argg[1] = args[ind];
				Generator.main(argg);
				return;
			};
					
			if (Arrays.asList(args).contains("d")){
				int ind = Arrays.asList(args).indexOf("d");
				ind = ind +1;
				if (args.length<ind+1){
					System.out.println("Unspecified value of debug. Try again!");
					return;
				}
				
				if (Integer.parseInt(args[ind]) >= 0 & Integer.parseInt(args[ind])<=2)
					debug = Integer.parseInt(args[ind]);
				else{
					System.out.println("Debug value must be 0,1 or 2");
					return;
				}
			}	
			
			
			if (Arrays.asList(args).contains("s")){
				int ind = Arrays.asList(args).indexOf("s");
				ind = ind +1;
				if (args.length<ind+1){
					System.out.println("Unspecified strategy. Try again!");
					return;
				}
				
				if (Integer.parseInt(args[ind]) >= 0 & Integer.parseInt(args[ind])<=NSTRAT)
					strat = Integer.parseInt(args[ind]);
				else{
					System.out.print("Strategy number can be: ");
					for (int i=1;i<NSTRAT+1; i++)
						System.out.print(i+" ");
					System.out.println("");
					return;
				}
			}	
			
			if (Arrays.asList(args).contains("p")){
				int ind = Arrays.asList(args).indexOf("p");
				ind = ind +1;
				if (args.length<ind+1){
					System.out.println("Unspecified path. Try again!");
					return;
				}
				
				path = "";
				while (ind<args.length && args[ind]!=" ")
					path = path+args[ind++];
				
				if (path.substring(path.lastIndexOf("/")).equals("/"))
					System.out.println("PERFECT!!!");
				else
					path=path+"/";
				
				File f = new File(path);
				File f0 = new File(path+"file0.xml");
				if(f.exists() || f.isDirectory()){
					System.out.println("Reading xmls from: "+path);
					if (f0.exists()==false){
						System.out.println("Inappropiate xml files content: "+path+" . Please try again!");
						return;
					}	
				}
				else{
					System.out.println("Non-existing directory: "+path+" . Please try again!");
					return;
				}
			}	
			
		}		
		
		//int strat=1;
		jade.core.Runtime runtime = jade.core.Runtime.instance();
		Profile profile = new ProfileImpl();
		AgentContainer container = runtime.createMainContainer(profile);        
        Object [] argtmpfc = new Object[nagents+2]; // si no le pasamos el path restar 1
        
        for (int i=0; i<nagents; i++){
			argtmpfc[i]="f"+i;	
			System.out.println("argtmpfc["+i+"]="+i);
        }
        argtmpfc[nagents]=Integer.toString(debug);
        System.out.println("argtmpfc["+nagents+"]="+Integer.toString(debug));
        argtmpfc[nagents+1]=path;
        System.out.println("argtmpfc["+(nagents+1)+"]="+path);
        
        try{				
			container.createNewAgent( "fc","Controller", argtmpfc).start();
		}
		catch(Exception e){
			System.out.println(e);
		}
        
        char []xp = path.toCharArray();
        //~ System.out.println("ESTE ES XP: ");
        //~ for (int m=0;m<xp.length;m++)
			//~ System.out.print(xp[m]);
        
        for (int i=0; i<nagents; i++){
				try{

					Object [] argtmp = new Object[nagents+4];
					
					for (int l=0; l<FSTARGS; l++){
						switch(l){
							case 0: argtmp[0] = Integer.toString(i);
									System.out.println("argtmp["+0+"]="+l);
							case 1: argtmp[1] = Integer.toString(strat);
									System.out.println("argtmp["+1+"]="+strat);
							case 2: argtmp[2] = Integer.toString(nagents);
									System.out.println("argtmp["+2+"]="+nagents);
							case 3: argtmp[3] = Integer.toString(debug);
									System.out.println("argtmp["+3+"]="+debug);
							case 4: argtmp[4] = path;
									//for (int j=0;j<xp.length; j++)
										//argtmp[4+j]=(char)xp[j];
									System.out.println("argtmp["+4+"]="+path);
							
							default:
									break;
						}
					}
					
					for (int j=0, k=0; j<nagents-1; j++,k++){
						if (j==i)
						   k++;
						String name = "f"+k;
						argtmp[j+FSTARGS]=name;
						System.out.println("argtmp["+(j+FSTARGS)+"]="+name);
					 }
					container.createNewAgent( "f"+Integer.toString(i),"Negotiator", argtmp).start();
				}
				catch(Exception e){
					System.out.println(e);
				}
		}
		
		System.out.println("ADIOSSSSSSSSSS");
			   

	}
	
}
