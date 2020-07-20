package scenario_1;

import jade.core.AID;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Date;
import javax.swing.event.*;
import javax.swing.table.*;

@SuppressWarnings("serial")
public class BookBuyerGUI extends JFrame {
	private String title;
	private BookBuyerAgent buyer;
	private JTextField search;
	private JTable bookList;
	private DefaultTableModel model;
	private JButton searchButton, buyButton;

	public BookBuyerGUI(BookBuyerAgent b) {
		super("Buyer: " + b.getName());
		this.buyer = b;
		
		initialTable();
		
		Container mainPanel = this.getContentPane();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		
		JPanel searchPane = new JPanel();
		String[] bStrings = { "Java", "C++", "History" };
		JComboBox bList = new JComboBox(bStrings);
		title = bList.getSelectedItem().toString();
		bList.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				title = bList.getSelectedItem().toString();
			}
		});
		searchPane.add(bList);
		
		searchButton = new JButton("Search");
		searchButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				search();
			}
		});
		searchPane.add(searchButton);
		
		mainPanel.add(searchPane);
		mainPanel.add(new JScrollPane(bookList));

		JPanel buttonPane = new JPanel();
		buyButton = new JButton("Buy");
		buyButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				buyBook();
			}
		});
		buttonPane.add(buyButton);
		mainPanel.add(buttonPane);

		this.setSize(500, 400);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);
	}

	public void addSeller(Book book, String seller) {
		String[] element = new String[4];
		element[0] = seller;
		element[1] = book.getName();
		element[2] = book.getAuthor();
		element[3] = book.getPrice();
		model.addRow(element);
		System.out.println("Added a seller");
	}

	public void initialTable() {
		model = new DefaultTableModel();
		model.addColumn("Agent");
		model.addColumn("Book");
		model.addColumn("Author");
		model.addColumn("Price");
		bookList = new JTable(model);
		bookList.addMouseListener(new MouseListener() {
			public void mouseClicked(MouseEvent arg0) {
			}
			public void mouseEntered(MouseEvent arg0) {
			}
			public void mouseExited(MouseEvent arg0) {
			}
			public void mousePressed(MouseEvent arg0) {
			}
			public void mouseReleased(MouseEvent arg0) {
			}
		});

	}

	public void clearTable() {
		System.out.println(model.getRowCount());
		for (int i = model.getRowCount() - 1; i >= 0; i--) {
			model.removeRow(i);
		}
	}
	
	public void search() {
		if (title != null || title != "") {
			clearTable();
			buyer.setBookTitle(title);
			buyer.sendRequest();
		}
	}

	public void buyBook() {
		AID aid = new AID();
		int i = bookList.getSelectedRow();
		int j = 0;
		String agentName = (String) model.getValueAt(i, j);
		aid.setLocalName(agentName);
		buyer.buyBook(aid);

		JOptionPane.showMessageDialog(this, "You have successfully purchased the book '" + title);
		model.removeRow(i);
		
	}
}
