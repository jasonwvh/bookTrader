package scenario_2;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

@SuppressWarnings("serial")
class BookBuyerGUI extends JFrame {
	public static JList lstBooks;
	private BookBuyerAgent myAgent;

	private JTextField titleField;

	BookBuyerGUI(BookBuyerAgent a) {
		super(a.getLocalName());

		myAgent = a;

		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new GridLayout(2, 2));
		
		
		mainPanel.add(new JLabel("Please insert book title to buy:"));
		titleField = new JTextField(25);
		mainPanel.add(titleField);
		getContentPane().add(mainPanel, BorderLayout.CENTER);

		JButton buyButton = new JButton("Buy");
		buyButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				try {
					String title = titleField.getText().trim();
					if (!title.isEmpty()) {
						myAgent.buyBook(title);
						titleField.setText("");
					} else {
						JOptionPane.showMessageDialog(BookBuyerGUI.this, "Please enter a title.");
					}
				} catch (Exception e) {
					JOptionPane.showMessageDialog(BookBuyerGUI.this, "Invalid values. " + e.getMessage(), "Error",
							JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		mainPanel = new JPanel();
		mainPanel.add(buyButton);
		getContentPane().add(mainPanel, BorderLayout.SOUTH);

		// Make the agent terminate when the user closes
		// the GUI using the button on the upper right corner
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				myAgent.doDelete();
			}
		});

		setResizable(false);
	}

	public void bookUnavailable(String title) {
		JOptionPane.showMessageDialog(this, "We are sorry. The book '" + title + "' is unavailable.");
	}

	public void bookPurchased(String title, double price) {
		DateFormat df = new SimpleDateFormat("dd MMM yy HH:mm:ss");
		Date dateobj = new Date();

		JOptionPane.showMessageDialog(this, "You have successfully purchased the book '" + title
				+ ".' The book price is RM" + price + ".\nTransaction date: " + df.format(dateobj));
	}

	public void showGui() {
		pack();
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int centerX = (int) screenSize.getWidth() / 2;
		int centerY = (int) screenSize.getHeight() / 2;
		setLocation(centerX - getWidth() / 2, centerY - getHeight() / 2);
		super.setVisible(true);
	}
}