package scenario_1;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.*;
import java.util.*;

@SuppressWarnings("serial")
public class BookSellerGUI extends JFrame {
	private BookSellerAgent seller;
	private JTable bookTable, transactionTable;
	private DefaultTableModel books, transactions;
	private JButton addBook, removeBook, updateBook;
	private Hashtable bookTitles;

	public BookSellerGUI(BookSellerAgent seller) {
		super("Seller: " + seller.getName());
		this.seller = seller;
		bookTitles = seller.getBooks();
		initial();
	}

	public void initial() {
		initialTable();
		initialTransaction();

		JPanel mainPanel = new JPanel();

		JPanel inputPane = new JPanel();
		addBook = new JButton("Add Book");
		addBook.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new AddBookGui(seller).showGui();
			}
		});
		inputPane.add(addBook);

		removeBook = new JButton("Remove Book");
		removeBook.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				removeBook();
			}
		});
		inputPane.add(removeBook);

		mainPanel.setLayout(new BorderLayout());
		mainPanel.add(inputPane, BorderLayout.NORTH);
		mainPanel.add(new JScrollPane(bookTable), BorderLayout.CENTER);
		
		JPanel transPanel = new JPanel();
		transPanel.setLayout(new BoxLayout(transPanel, BoxLayout.Y_AXIS));
		transPanel.add(new JLabel("Transaction History"), BorderLayout.NORTH);
		transPanel.add(new JScrollPane(transactionTable), BorderLayout.SOUTH);
		
		Container c = this.getContentPane();
		c.setLayout(new GridLayout(2, 2));
		c.add(mainPanel);
		c.add(transPanel);

		this.setSize(600, 400);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);
	}

	public void initialTable() {
		books = new DefaultTableModel();
		books.addColumn("Author");
		books.addColumn("Name");
		books.addColumn("Price");
		bookTable = new JTable(books);
	}

	public void initialTransaction() {
		transactions = new DefaultTableModel();
		transactions.addColumn("Agent");
		transactions.addColumn("Book");
		transactions.addColumn("Price");
		transactionTable = new JTable(transactions);
	}

	public void addBook(Book book) {
		String[] element = new String[3];
		element[0] = book.getAuthor();
		element[1] = book.getName();
		element[2] = book.getPrice();
		books.addRow(element);
		System.out.println("Book added: " + element[1] + "\n");
	}

	public void addBuyer(Book book, String agent) {
		String[] element = new String[3];
		element[0] = agent;
		element[1] = book.getName();
		element[2] = book.getPrice();
		transactions.addRow(element);
	}

	public void removeBook() {
		int select = bookTable.getSelectedRow();
		if (select == -1) {
			JOptionPane.showMessageDialog(this, "Please select a book.");
		} else {
			String title = (String) bookTable.getValueAt(select, 1);
			seller.removeBook(title);
			books.removeRow(select);
			System.out.println("Book Removed: " + title + "\n");
		}
	}

	public void removeBook(String title) {
		seller.removeBook(title);
		for (int i = books.getRowCount() - 1; i > -1; i--) {
			if ((String) books.getValueAt(i, 1) == title) {
				books.removeRow(i);
			}
		}
		System.out.println("Book Removed: " + title + "\n");
	}
}

@SuppressWarnings("serial")
class AddBookGui extends JFrame implements KeyListener {
	private BookSellerAgent myAgent;
	private JButton addButton;
	private JTextField titleField, priceField, authorField;

	public AddBookGui(BookSellerAgent a) {
		super(a.getLocalName());
		myAgent = a;

		JPanel p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));

		JPanel p1 = new JPanel();
		p1.setLayout(new GridLayout(3, 2));

		p1.add(new JLabel("Title:"));
		titleField = new JTextField(15);
		titleField.addKeyListener(this);
		p1.add(titleField);
		p1.add(new JLabel("Author:"));
		authorField = new JTextField(15);
		authorField.addKeyListener(this);
		p1.add(authorField);
		p1.add(new JLabel("Price:"));
		priceField = new JTextField(15);
		priceField.addKeyListener(this);
		p1.add(priceField);
		
		p.add(p1);
		getContentPane().add(p, BorderLayout.CENTER);

		addButton = new JButton("Add");
		addButton.addKeyListener(this);
		addButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				addBook();
			}
		});
		//p = new JPanel();
		p.add(addButton);
		getContentPane().add(p, BorderLayout.SOUTH);

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				// myAgent.doDelete();
				AddBookGui.this.setEnabled(false);
			}
		});
		setResizable(false);
	}

	public void addBook() {
		try {
			Book book;
			String title = titleField.getText().trim();
			String author = authorField.getText().trim();
			String price = priceField.getText().trim();
			book = new Book();
			book.setAuthor(author);
			book.setName(title);
			book.setPrice(price);
			myAgent.addBook(title, book);
			titleField.setText("");
			priceField.setText("");
			authorField.setText("");
		} catch (Exception e) {
			JOptionPane.showMessageDialog(AddBookGui.this, "Invalid values. " + e.getMessage(), "Error",
					JOptionPane.ERROR_MESSAGE);
		}
		super.dispose();
	}

	public void showGui() {
		pack();
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int centerX = (int) screenSize.getWidth() / 2;
		int centerY = (int) screenSize.getHeight() / 2;
		setLocation(centerX - getWidth() / 2, centerY - getHeight() / 2);
		super.setVisible(true);
	}

	public void keyPressed(KeyEvent e) {
		Object source = e.getSource();
		if (e.getKeyCode() == KeyEvent.VK_ENTER) {
			if (source == titleField) {
				authorField.requestFocus();
			} else if (source == authorField) {
				priceField.requestFocus();
			} else if (source == priceField) {
				addButton.requestFocus();
			} else if (source == addButton) {
				addBook();
				titleField.requestFocus();
			}
		}
	};

	public void keyReleased(KeyEvent e) {
	};

	public void keyTyped(KeyEvent e) {
	};
}
