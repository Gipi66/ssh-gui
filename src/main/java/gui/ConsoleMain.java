package gui;

import javax.swing.JPanel;
import java.awt.BorderLayout;
import javax.swing.JLabel;
import javax.swing.JTextPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import java.awt.Color;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class ConsoleMain extends JPanel {
	private JTextField inputField;
	private JLabel lblNewLabel;
	JTextArea textPane;

	/**
	 * Create the panel.
	 */
	public ConsoleMain(String consoleName) {

		setLayout(new BorderLayout(0, 0));

		JPanel panel = new JPanel();
		add(panel, BorderLayout.CENTER);
		panel.setLayout(new BorderLayout(0, 0));

		// lblNewLabel = new JLabel(consoleName);
		// panel.add(lblNewLabel, BorderLayout.NORTH);

		textPane = new JTextArea();
		textPane.setBackground(new Color(128, 0, 128));
		textPane.setForeground(Color.white);
		textPane.setEditable(false);

		textPane.setText("Супер пупер shh клиент!\n");

		JScrollPane scrollPane = new JScrollPane(textPane);
		panel.add(scrollPane);

		inputField = new JTextField();

		panel.add(inputField, BorderLayout.SOUTH);
		inputField.setColumns(10);
	}

	public ConsoleMain() {
		this(new String());
	}

	public String getConsole() {
		return textPane.getText();
	}

	public boolean updateConsole(String text) {
		textPane.setText(textPane.getText() + "\n" + text);
		return true;
	}

	public JTextField getInputComponent() {
		return inputField;
	}

	public JTextArea getOutComponent() {
		return textPane;
	}

}
