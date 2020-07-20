package scenario_1;

import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.domain.DFService;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.io.IOException;
import java.util.*;

@SuppressWarnings("serial")
public class BookSellerAgent extends Agent {

	private Hashtable books;
	private BookSellerGUI gui;
	private Book book = null;
	private String MyInformation;

	protected void setup() {

		System.out.println("setup the " + this.getName() + "Agent");
		MyInformation = this.getLocalName();
		books = new Hashtable();
		gui = new BookSellerGUI(this);

		DFAgentDescription agentDescription = new DFAgentDescription();
		agentDescription.setName(this.getAID());
		ServiceDescription service = new ServiceDescription();
		service.setType("book-selling");
		service.setName("BookSelling");
		agentDescription.addServices(service);

		try {
			DFService.register(this, agentDescription);
		} catch (FIPAException e) {
			e.printStackTrace();
		}
		this.addBehaviour(new OfferRequest());

	}

	public void addBook(final String title, final Book book) {
		this.addBehaviour(new OneShotBehaviour() {
			public void action() {
				books.put(title, book);
				gui.addBook(book);
			}
		});
	}

	public void removeBook(final String title) {
		this.addBehaviour(new OneShotBehaviour() {
			public void action() {
				System.out.println("removed book " + title);
				books.remove(title);
			}
		});
	}

	private class OfferRequest extends CyclicBehaviour {
		public void action() {
			MessageTemplate messageTemplate = MessageTemplate.MatchPerformative(ACLMessage.CFP);
			ACLMessage message = myAgent.receive(messageTemplate);

			if (message != null) {
				if (message.getLanguage().equalsIgnoreCase("request")) {
					String title = message.getContent();
					ACLMessage reply = message.createReply();
					book = (Book) books.get(title);
					System.out.println("REQUEST " + message.getSender().getLocalName() + " FOR BOOK " + title + "\n");
					if (book != null) {
						try {
							reply.setPerformative(ACLMessage.PROPOSE);
							reply.setContentObject(book);
							reply.setLanguage("Book");
						} catch (IOException e) {
							e.printStackTrace();
						}

					} else {
						reply.setPerformative(ACLMessage.REFUSE);
						reply.setContent("not-available");
					}
					myAgent.send(reply);
					System.out.println("send a reply");
				} else {
					ACLMessage reply = message.createReply();
					gui.addBuyer(book, message.getSender().getLocalName());
					System.out.println(message.getSender().getLocalName() + " SUCCESFULLY BOUGHT " + book.getName() + "\n");
					reply.setLanguage("thanks");
					reply.setContent(MyInformation + ": successfully bought a book of " + book.getName());
					reply.setPerformative(ACLMessage.INFORM);
					myAgent.send(reply);
					removeBook(book.getName());
					gui.removeBook(book.getName());
				}
			} else {
				this.block();
			}
		}
	}

	public Hashtable getBooks() {
		return books;
	}

	protected void takeDown() {
		System.out.println("Closing agent");
		try {
			DFService.deregister(this);
		} catch (FIPAException e) {
			e.printStackTrace();
		}
	}
}
