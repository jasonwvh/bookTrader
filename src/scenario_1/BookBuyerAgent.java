package scenario_1;

import jade.core.Agent;
import jade.core.AID;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import jade.core.behaviours.*;

@SuppressWarnings("serial")
public class BookBuyerAgent extends Agent {
	private String bookTitle;
	private AID[] bookSellers;
	private BookBuyerGUI gui;
	private MessageTemplate template, buyTemplate;
	private AID seller;

	protected void setup() {
		System.out.println("Buyer " + this.getLocalName() + " is ready");
		gui = new BookBuyerGUI(this);
		this.addBehaviour(new ReceiveMessage());
	}

	private class RequestPerformer extends Behaviour {
		public void action() {

			ACLMessage message = new ACLMessage(ACLMessage.CFP);
			for (int i = 0; i < bookSellers.length; i++) {
				message.addReceiver(bookSellers[i]);
			}
			message.setLanguage("request");
			message.setContent(bookTitle);
			message.setConversationId("book-trade");
			message.setReplyWith("cfg" + System.currentTimeMillis());
			myAgent.send(message);
			template = MessageTemplate.and(MessageTemplate.MatchConversationId("book-trade"),
					MessageTemplate.MatchInReplyTo(message.getReplyWith()));
		}

		public boolean done() {
			return true;
		}

	}

	public void setBookTitle(String title) {
		bookTitle = title;
	}

	public void sendRequest() {
		this.addBehaviour(new OneShotBehaviour() {
			public void action() {
				DFAgentDescription agentDescription = new DFAgentDescription();
				ServiceDescription serviceDescription = new ServiceDescription();
				serviceDescription.setType("book-selling");
				agentDescription.addServices(serviceDescription);

				try {
					DFAgentDescription[] results = DFService.search(myAgent, agentDescription);
					bookSellers = new AID[results.length];
					System.out.println("the BookSellers is follow: ");
					for (int i = 0; i < results.length; i++) {
						bookSellers[i] = results[i].getName();
						System.out.println(bookSellers[i]);
					}

				} catch (FIPAException e) {
					e.printStackTrace();
				}
				myAgent.addBehaviour(new RequestPerformer());
			}

		});
	}

	public void buyBook(AID sellerAID) {
		this.seller = sellerAID;
		this.addBehaviour(new OneShotBehaviour() {
			public void action() {
				ACLMessage buy = new ACLMessage(ACLMessage.CFP);
				buy.setLanguage("buy");
				buy.setContent(bookTitle);
				buy.setConversationId("buy");
				buy.setReplyWith("cfg" + System.currentTimeMillis());
				buy.addReceiver(seller);
				myAgent.send(buy);
				buyTemplate = MessageTemplate.and(MessageTemplate.MatchConversationId("buy"),
						MessageTemplate.MatchInReplyTo(buy.getReplyWith()));
			}
		});
	}

	public class ReceivePropose extends Behaviour {
		boolean finish = false;

		public void action() {
			if (buyTemplate != null) {
				ACLMessage buyReply = myAgent.receive(buyTemplate);
				if (buyReply != null && buyReply.getLanguage().equalsIgnoreCase("thanks")) {
					System.out.println("Bought a book successfully");
					String msg = buyReply.getContent();
					System.out.println(msg + "\n");
				}
			}
		}

		public boolean done() {
			return finish;
		}

	}

	private class ReceiveMessage extends CyclicBehaviour {
		public void action() {
			ACLMessage reply = myAgent.receive(template);
			if (reply != null) {
				if (reply.getPerformative() == ACLMessage.PROPOSE) {
					if (reply.getLanguage().equalsIgnoreCase("Book")) {
						try {
							Book book = (Book) reply.getContentObject();
							gui.addSeller(book, reply.getSender().getLocalName());
							myAgent.addBehaviour(new ReceivePropose());
						} catch (UnreadableException e) {
							e.printStackTrace();
						}
					} else {
						System.out.println(1 + reply.getContent());
					}
				} else if (reply.getPerformative() == ACLMessage.REFUSE) {
					System.out.println(
							"REFUSE" + reply.getSender().getLocalName() + "TITLE" + bookTitle + "TITLE" + "\n");
				}
			}
		}

	}

}
