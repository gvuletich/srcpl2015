import jade.core.Agent;
import jade.wrapper.AgentContainer;
import jade.core.behaviours.*;
import jade.lang.acl.ACLMessage;
import java.util.*;
import jade.core.AID;
import jade.lang.acl.MessageTemplate;
import jade.wrapper.StaleProxyException;
import jade.wrapper.ControllerException;
import jade.domain.JADEAgentManagement.ShutdownPlatform;
import jade.content.lang.sl.SLCodec;
import jade.content.lang.Codec;
import jade.content.onto.Ontology;
import jade.domain.JADEAgentManagement.JADEManagementOntology;
import jade.content.AgentAction;
import jade.content.onto.basic.Action;
import java.io.File;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.*;
import jade.lang.acl.UnreadableException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

// Esta clase implementa un coordinador de la negociacion.
// es lanzada desde Platform

public class Controller extends Agent implements Values
{
	private Component initcomp;
	private Component finalcomp;
	private Print p;
	private int nagents;
	private Object[] args;
	BufferedReader idfile=null;	
	String idnum=null;
	File dir;
	File dirx;
	String outpath;
	String xmlpath;
	Utility uglobal;
	
	protected void setup()
	{
		
		System.out.println("Controller Agent initializes excecution...");
		args = getArguments();
		nagents = args.length-2;
		xmlpath = (String)args[nagents+1];
		System.out.println("CONTROLADOR OBTIENE PATH: "+xmlpath);
					
		try{
			idfile = new BufferedReader(new FileReader(xmlpath+"id.txt"));
			idnum = idfile.readLine();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		
		outpath = "./salida/"+idnum;
		dir = new File(outpath);		
		dir.mkdir();
		
		dirx = new File(outpath+"/xmls");
		dirx.mkdir();
		
		
		Print rondas = new Print();
		rondas.set(0,outpath+"/rondas");	
		int j=0;
		int rounds=50;
		int nrounds=0;
		int nready=0;
		int fail=0;
		int lastfail=-2;
		int nend = 0;
		int nst = 0;
		int debug=0;
		
		
		
		
		p = new Print();
		debug = Integer.parseInt((String)args[nagents]);
		p.set(debug,outpath+"/info");	
		
		
		
		int [] agenst = new int[nagents];		
		boolean [] states = new boolean [nagents];
		
		initcomp = new Component(6);
		finalcomp = new Component(6);
		
		for (int i=0;i<nagents;i++)
			states[i]=true;
		boolean endit = false;	
		
		p.show("I will control "+nagents+" agents.",HIGH);
		MessageTemplate template3 = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
		while (nst < nagents) {
			ACLMessage msgst = blockingReceive(template3);
            if (msgst!=null ){
				System.out.println("EL MENSAJE CONTIENE: "+msgst.getContent());
				//if (msgst.getContent().equals("1")){
					agenst[nst]= Integer.parseInt(msgst.getContent());
					nst++;
				//}
			}
			p.show("CONTROLLER: Agent number "+nst+" plays strategy: "+agenst[nst-1],1);
			
		}
		
		sndconfirm("strat");
		
		verify(initcomp,nagents);
		initcomp.PrintMe();
		if (initcomp.equal(finalcomp)==true)
			p.show("SIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIII",LOW);
		else
			p.show("noooooo yetttt",LOW);
		sndconfirm("verify");
		
		while (nready < nagents) {
		MessageTemplate template0 = MessageTemplate.MatchPerformative(ACLMessage.UNKNOWN);
		ACLMessage msg6 = blockingReceive(template0);
            if (msg6!=null){
				if ( msg6.getContent().equals("Ready") ){    //se bloquea hasta que los n agentes se reporten preparados
					nready++;
					System.out.println("YA CONTESTARON "+nready);
				}
			}
		}
		
		
		//UNA CHANCHADA, BURRADA, MOQUEADA PROGRAMACIONAL
		
		
		List<Problem> lp = new ArrayList<Problem>();
		Problem pro = new Problem(xmlpath+"file0.xml");
		lp.add(pro);
		Problem problema = new Problem(xmlpath+"file0.xml");
		for (int i=1; i<nagents; i++){
			Problem protemp = new Problem(xmlpath+"file"+i+".xml");
			lp.add(protemp);
			problema.getbyName("mat").mergeM(protemp.getbyName("mat"));
			problema.getbyName("uti").mergeU(protemp.getbyName("uti"));
			problema.getbyName("rec").mix(protemp.getbyName("rec"));
		}
		
		p.show("$$$$$$$$$$$ ESTE ES EL PROBLEMA OBJETIVO $$$$$$$$",HIGH);
		p.show("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$",HIGH);
		p.show("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$",HIGH);
		p.show("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$",HIGH);
		p.show("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$",HIGH);
		problema.PrintMe();
		synchronized (System.out) {		
		p.show("YYYYY MIIIII SOLUCION ESSSS:",HIGH);
		problema.Solve(lp).PrintMe();
		p.show("YYYYY MIIIII UTILIDAD ESSSS:",HIGH);
		uglobal = problema.Objetive(lp);
		uglobal.PrintMe();
		}
		

		
		p.show("Controller STARTS excecution!!!",HIGH);
		while (j< nagents*rounds) {
			
			boolean check = false;
			for (int i=0; i<nagents; i++)
				check = check || states[i];
		    if (endit==true){
				p.show("CATASTROPHEE!!!...",HIGH);
				break;
			}

			int init = (j % nagents);
			
			
			p.show(" -------------------------------------",HIGH);
			p.show("|  Agent "+init+" plays Initiator...  |",HIGH);
			p.show(" -------------------------------------",HIGH);
			
			p.show("Following agents plays Participant:",MID);
			ACLMessage msg = new ACLMessage(ACLMessage.UNKNOWN);
			for (int i = 0; i < args.length-1; i++) {
							if (i!=init){
							p.show("Agent: "+ ((String)args[i]),MID);		// selecciona quienes escucharan
							msg.addReceiver(new AID((String) args[i], AID.ISLOCALNAME));
							}
			}
			msg.setContent("Wait");			// y les avisa que en esta ronda deberan escuchar propuestas
			send(msg);
		
			ACLMessage msg2 = new ACLMessage(ACLMessage.UNKNOWN);
			msg2.addReceiver(new AID((String) args[init], AID.ISLOCALNAME));
			msg2.setContent("Go");	// le avisa al agente que en esta ronda debera ser licitador

			send(msg2);
			j++;
			
			
			MessageTemplate template = MessageTemplate.MatchPerformative(ACLMessage.UNKNOWN);
			
			p.show("Controller: Waiting for end of negotiation round...",MID);									
			
			ACLMessage msg4 = blockingReceive(template);
            if (msg4!=null){
				if ( msg4.getContent().equals("Done1") )		// espera que el iniciador le informe que completo
					p.show("We go on...",LOW);
					states[init]=states[init] && true;				// su licitacion
				if ( msg4.getContent().equals("Done0") ){
					p.show("Failed to agree , move on..",LOW);	
					states[init]=states[init] && false;
					if (check==false)
						endit = true;
				}
			}
			
			
				
		}

		p.show("NEGOTIATION IS OVER",HIGH);
		//p.Unset(); 
		
		
		while (nend < nagents) {
			ACLMessage msgF = new ACLMessage(ACLMessage.UNKNOWN);
			msgF.addReceiver(new AID((String) args[nend], AID.ISLOCALNAME));
			msgF.setContent("end");			// les avisa a todos los agentes que finalizo la negociacion
			send(msgF);
			nend++;
		}
		
		
		p.write(Integer.toString(nagents));
		p.write(Integer.toString(j));
		p.write(Integer.toString(agenst[0]));   //CORREGIR ESTO SI SE USAN STRATEG MIXTAS
		p.unset();		
		
		
		
		
		
		for (int i=0; i<j; i++)
			rondas.write(i+" "+uglobal.get());  // ACA VA LA UTILIDAD OBJETIVA
		rondas.unset();
		
		verify(finalcomp,nagents);
		//finalcomp.set(0,33);
		finalcomp.PrintMe();		
		if (initcomp.equal(finalcomp)==true)
			p.show("SIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIII",LOW);
		else
			p.show("NOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO",LOW);
		
		try {
			new File("APDescription.txt").delete();
			new File("MTPs-Main-Container.txt").delete();
		} catch (Exception e) {
			e.printStackTrace();
		}	
		
		Codec codec = new SLCodec();    
		Ontology jmo = JADEManagementOntology.getInstance();
		getContentManager().registerLanguage(codec);
		getContentManager().registerOntology(jmo);
		ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
		msg.addReceiver(getAMS());
		msg.setLanguage(codec.getName());
		msg.setOntology(jmo.getName());
		try {
			getContentManager().fillContent(msg, new Action(getAID(), new ShutdownPlatform()));
			send(msg);
		}
		catch (Exception e) {}
			
		}
		
	public int onEnd(){
		//doDelete();
        //System.exit(0); 
      	return 0;
	}
	
	
	private void verify(Component ctmp, int n){
		MessageTemplate template0 = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
		int nend = 0;
		Component c = null;
		
		p.show("Executing verification...",LOW);
		
		while (nend < n) {
		ACLMessage msg = blockingReceive(template0);
		if (msg!=null){
			try {
				if ("JavaSerialization".equals(msg.getLanguage())) {
					c = (Component)msg.getContentObject();								
				}
				else
					p.show("No recibi serializable!",LOW);
			} catch(UnreadableException e3){
					System.err.println(getLocalName()+ " catched exception "+e3.getMessage());
			}
			nend++;
		}
		
		ctmp.mix(c);
		}
	}
	
	private void sndconfirm(String s){
		ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
		for (int i = 0; i < nagents; i++) 
			msg.addReceiver(new AID((String) args[i], AID.ISLOCALNAME));
		msg.setContent(s);			
		send(msg);
	}
	
}
