import jade.core.Agent;
import jade.core.behaviours.*;
import jade.lang.acl.ACLMessage;
import java.util.*;
import java.io.File;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.*;
import jade.core.AID;
import jade.proto.ContractNetInitiator;
import jade.domain.FIPANames;
import java.util.Date;
import java.util.Vector;
import java.util.Enumeration;
import jade.lang.acl.MessageTemplate;
import jade.proto.ContractNetResponder;
import jade.domain.FIPAAgentManagement.NotUnderstoodException;
import jade.domain.FIPAAgentManagement.RefuseException;
import jade.domain.FIPAAgentManagement.FailureException;
import jade.domain.FIPAAgentManagement.*;
import java.io.*;
import jade.domain.FIPAException;
import jade.lang.acl.UnreadableException;
import java.lang.reflect.*;
import jade.wrapper.StaleProxyException;
import jade.wrapper.ControllerException;
import jade.domain.JADEAgentManagement.ShutdownPlatform;
import jade.content.lang.sl.SLCodec;
import jade.content.lang.Codec;
import jade.content.onto.Ontology;
import jade.domain.JADEAgentManagement.JADEManagementOntology;
import jade.content.AgentAction;
import jade.content.onto.basic.Action;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;


// Implementa a un agente, tanto en su rol de Iniciador como de Participante

public class Negotiator extends Agent implements Values
{
	private MentalState mst;	 
	private int nResponders=1;
	int round=0;
	boolean agree;
	Proposal p;
	Print pr = new Print();
	Print pf = new Print();
	int debug=0;
	
	protected void setup()
	{
		final Object[] args = getArguments();  
		String myname = getLocalName();
		String id = null;
		Strategy strat=null;
		BufferedReader idfile=null;	
		String idnum=null;
		File dir;
		File dirx;
		String outpath;
		String xmlpath = "";
		
		if (args != null){
			pr.show("List of arguments:",MID);
			for (int i=0; i<args.length; i++)
				pr.show(i+": "+(String)args[i],MID);
			
			id = (String) args[0];	
			strat = set_strat(Integer.parseInt((String)args[1]));   
			nResponders = (Integer.parseInt((String)args[2])) - 1;  
			debug =  Integer.parseInt((String)args[3]);
			xmlpath = ((String)args[4]);
			//xmlpath = xmlpath.substring(0,1);
			//quinto argumento 
			//~ int ilen = Integer.parseInt((String)args[4]);
			//~ int i=0;
			//~ while (i<ilen)
				//~ xmlpath = xmlpath+args[5+i++];
				
		}
		else
			end("Wrong arguments feed!");
		
		System.out.println("SOY EL AGENTE "+id);
		System.out.println("NIVEL DE DEBUG ES: "+debug);
		System.out.println("NRO DE RESPONDERS ES: "+nResponders);
		System.out.println("ME PASARON EL PATH "+xmlpath);
		
		try{
			idfile = new BufferedReader(new FileReader(xmlpath+"id.txt"));
			idnum = idfile.readLine();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
			
		outpath = "./salida/"+idnum;
		dir = new File(outpath);

		pr.set(debug,outpath+"/agente"+id);
	    //pf.set(debug,outpath+"/info");
	    
		String path = xmlpath+"file"+id+".xml";
		mst = new MentalState((new Problem(path)),set_strat(strat.getID()));
		mst.setnrounds(round);
		mst.setnagen(nResponders);
		mst.PrintMe();
					
		send_info(((Strategy)strat).getID());					
		rcvconfirm("strat");
		
		verify(mst.getproblem().getbyName("rec"));
		rcvconfirm("verify");
				
		addBehaviour(
			
			new SimpleBehaviour(this)
			{
				
				public void action()
				{
					
					MessageTemplate template = MessageTemplate.MatchPerformative(ACLMessage.UNKNOWN);							
					ACLMessage msgt = myAgent.receive(template);
					
                    if (msgt!=null){
						if ( msgt.getContent().equals("Go") ){		
							
						pr.show(myAgent.getName()+" starting to propose...",HIGH);
						pr.show("Trying to get a contract with one from "+nResponders+" responders.",MID);
						final ACLMessage msg = new ACLMessage(ACLMessage.CFP);
						for (int i = 0; i < mst.getnagents(); ++i) {   
							msg.addReceiver(new AID((String) args[i+FSTARGS], AID.ISLOCALNAME));
						}	
						msg.setProtocol(FIPANames.InteractionProtocol.FIPA_CONTRACT_NET);
						msg.setReplyByDate(new Date(System.currentTimeMillis() + 10000));
						
						Proposal p_init = mst.getstrat().InitialProposal(mst);  
						p_init.PrintMe();
						
						pr.show("My utility now:",LOW);
						mst.getproblem().Objetive().PrintMe();
						pr.show("and my resources:  ",MID);
						mst.getproblem().getbyName("rec").PrintMe();
						
						try {
						msg.setLanguage("JavaSerialization");		
						msg.setContentObject(p_init);
						} catch (IOException e ) {
							e.printStackTrace();
						}
						mst.setlastsnd(p_init);							
						round++;						
						agree = false;
						
						pr.write(round+" "+mst.Serialize());
						
						// Inicia Behaviour ContractNetInitiator
						addBehaviour(new ContractNetInitiator(myAgent, msg) {   
							
							protected void handlePropose(ACLMessage propose, Vector v) {	
								try {
									if ("JavaSerialization".equals(propose.getLanguage())) {
										Proposal p = (Proposal)propose.getContentObject();	
										pr.show("Agent "+propose.getSender().getName()+" proposed ", HIGH);
										p.PrintMe();
									}
									else
										pr.show("Did not receive a serializable",MID);
								} catch(UnreadableException e3){
										  System.err.println(getLocalName()+ " catched exception "+e3.getMessage());
								}
								
								
							}
				
							protected void handleRefuse(ACLMessage refuse) {
								pr.show("Agent "+refuse.getSender().getName()+" was rejected",HIGH);
							}
				
							protected void handleFailure(ACLMessage failure) {
								if (failure.getSender().equals(myAgent.getAMS())) {
									pr.show("Non existent responder",HIGH);
								}
								else {
									pr.show("Agent "+failure.getSender().getName()+" failed",HIGH);
								}
								nResponders--;
							}
							
							
							protected void handleAllResponses(Vector responses, Vector acceptances) {
								if (responses.size() < nResponders) {
									pr.show("Timeout expired: "+(nResponders - responses.size())+" responses were lost.",HIGH);
								}	
								
								AID bestProposer = null;
								ACLMessage accept = null;
								Enumeration e = responses.elements();
								while (e.hasMoreElements()) {
									ACLMessage msg = (ACLMessage) e.nextElement();
									if (msg.getPerformative() == ACLMessage.PROPOSE) {
										ACLMessage reply = msg.createReply();
										reply.setPerformative(ACLMessage.REJECT_PROPOSAL);  
										acceptances.addElement(reply);
									}
								}			
								
								Enumeration e1 = responses.elements();
								Proposal bestProposal = mst.getstrat().BestProposal(mst,e1,responses);	 	
										
															
								Enumeration a = acceptances.elements();
								if (bestProposal!=null){
									bestProposer = bestProposal.getProposer();
									while (a.hasMoreElements()) {
										ACLMessage msg = (ACLMessage) a.nextElement();
										if (msg.getPerformative() == ACLMessage.REJECT_PROPOSAL) {
											Iterator it = msg.getAllReceiver();
											while (it.hasNext()){
												AID receiver = (AID) it.next();
												if (receiver.equals(bestProposal.getProposer())){ 
													msg.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
													accept = msg;				
												}
											}
										}
									}	
								}
								
								if (accept != null){
									pr.show("Acceptin proposal "+bestProposal.toString()+" from Responder "+bestProposer.getName(),HIGH);
									mst.getproblem().Accept(bestProposal);
									agree = true;	
								}
								
							
							}
							
							protected void handleInform(ACLMessage inform) {
								pr.show("Agent "+inform.getSender().getName()+" succesfully performed the required action.",HIGH);
							}
							
							public int onEnd(){
								removeBehaviour(this);								
								pr.show("Agent "+getLocalName()+" finalizes Initiator!!",HIGH);
								ACLMessage msg3 = new ACLMessage(ACLMessage.UNKNOWN);
								msg3.addReceiver(new AID((String) "fc", AID.ISLOCALNAME));
								if (agree == true)
									msg3.setContent("Done1");
								else
									msg3.setContent("Done0");
									
								send(msg3);
								
								return 0;
							}
							
						} );
							
							
					
						}
						
						if ( msgt.getContent().equals("end") ){   
							pr.show(getLocalName()+": End of negotiations received!",MID);
							verify(mst.getproblem().getbyName("rec"));
							pr.unset();
							done();
							removeBehaviour(this);
							doDelete();
							return;
						}
					}	  
					
				}
				
				public boolean done(){return false;}
				
			});
			
			
		
		
		// Inicia ContractNetResponder
				
		MessageTemplate template = MessageTemplate.and(
										MessageTemplate.MatchProtocol(FIPANames.InteractionProtocol.FIPA_CONTRACT_NET),
										MessageTemplate.MatchPerformative(ACLMessage.CFP)
									);
				
		addBehaviour(new ContractNetResponder(this, template) {
			
			@Override
			protected ACLMessage handleCfp(ACLMessage cfp) throws NotUnderstoodException, RefuseException {		
				
				round++;
				pr.write(round+" "+mst.Serialize());
							
				try {
				if ("JavaSerialization".equals(cfp.getLanguage())) {
					p = (Proposal)cfp.getContentObject();	
					mst.setlastrcv(p);		
					pr.show("Agent "+getLocalName()+": CFP received "+cfp.getSender().getName()+". Proposal is: ",HIGH);//+p.toString());
					p.PrintMe();					
				}
				else
					pr.show("Didn't receive serializable!",MID);
				} catch(UnreadableException e3){
					System.err.println(getLocalName()+ " catched exception "+e3.getMessage());
				}
				
				Proposal proposal = mst.getstrat().CounterOffer(mst,p);	
				
				if (proposal==null){
					pr.show("Agent "+getLocalName()+" rejects initial proposal",MID);
					throw new RefuseException("Proposal is not considered");
				}
				
				else{
				
				ACLMessage propose = cfp.createReply();				
				propose.setPerformative(ACLMessage.PROPOSE);
					
				try {
					propose.setLanguage("JavaSerialization");
					propose.setContentObject(proposal);
				} catch (IOException e ) {
						e.printStackTrace();
				}
					
				mst.setlastsnd(proposal);
				return propose;
				}
				
			}
				
				
			@Override
			protected ACLMessage handleAcceptProposal(ACLMessage cfp, ACLMessage propose,ACLMessage accept) throws FailureException {
			pr.show("Agent "+getLocalName()+": Accedpted proposal",HIGH);			
			if (performAction()) {							
				ACLMessage inform = accept.createReply();
				inform.setPerformative(ACLMessage.INFORM);
				return inform;
				}
			else {
				pr.show("Agent "+getLocalName()+": Couldn't receive proposal succesfully.",MID);
				throw new FailureException("unexpected error!");
			}
			
					
			}
			
			protected void handleRejectProposal(ACLMessage cfp, ACLMessage propose, ACLMessage reject) {
				synchronized (System.out) {		
				pr.show("Agent "+getLocalName()+": Rejected proposal",HIGH);
				}
			}
					
			public int onEnd(){pr.show("Finalizes Responder!!",MID); return 0;}
					
		} );
					
	
		ACLMessage msg5 = new ACLMessage(ACLMessage.UNKNOWN);
		msg5.addReceiver(new AID((String) "fc", AID.ISLOCALNAME));
		msg5.setContent("Ready");		
		send(msg5);						
		
		Generator gen = new Generator();
		//dirx = new File(outpath+"/xmls");
		gen.create_xml(outpath+"/xmls/file"+id+".xml",mst.getproblem().getInput());
		
	}
	
	public int onEnd(){
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
		return 0;
	}
	
	
	private boolean performAction() {		
		Proposal lsend = mst.getlastsnd();
		if (lsend!=null){
			lsend.reverse();
			mst.getproblem().Accept(lsend);   
			return true;
		}
		return false;
	}
	
	
	private void end(String s){
		System.out.println(s);
	}
	
	
	private Strategy set_strat(int s){
		   Strategy str=null;
		   switch (s) {
			case 1: str = new Strategy1(1);
					break;
			case 2: str = new Strategy2(2);
					break;
			case 3: str = new Strategy3(3);
					break;
			//~ case 4: str = new Strategy4(4);
					//~ break;
			//~ case 5: str = new Strategy5(5);
					//~ break;
			//~ case 6: str = new Strategy6(6);
					//~ break;
			//~ case 7: str = new Strategy6(7);
					//~ break;
			//~ case 8: str = new Strategy6(8);
					//~ break;
			default: System.out.println("Non-existent strategy!"); 
					break;
			}
		   return str;	
	}
	
	
	private void send_info(int s){
		ACLMessage msgst = new ACLMessage(ACLMessage.INFORM);
		msgst.addReceiver(new AID((String) "fc", AID.ISLOCALNAME));
		System.out.println("Seteamos ST con el valor: "+s);
		msgst.setContent(Integer.toString(s));		
		send(msgst);
	}
	
	private void verify(Component p){
		try {
			ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
			msg.setLanguage("JavaSerialization");		
			msg.setContentObject(p);
			msg.addReceiver(new AID((String) "fc", AID.ISLOCALNAME));
			send(msg);
		} catch (IOException e ) {
			e.printStackTrace();
		}
	}
	
	public void rcvconfirm(String s){
		MessageTemplate template = MessageTemplate.MatchPerformative(ACLMessage.INFORM);							
		ACLMessage msgt = blockingReceive(template);
		if (msgt!=null)
			if ( msgt.getContent().equals(s) )
				return;
	}
								

		
}
