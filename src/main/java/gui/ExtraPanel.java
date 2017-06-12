package gui;

import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import javax.swing.JButton;
import java.awt.GridLayout;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.ActionEvent;
import javax.swing.JScrollPane;

public class ExtraPanel extends JPanel {
	private JTextField txtName;
	private JTextField txtCommand;

	private JButton btnSave;

	/**
	 * Create the panel.
	 */
	public ExtraPanel() {
		setLayout(new BorderLayout(0, 0));

		JPanel panel = new JPanel();
		add(panel, BorderLayout.NORTH);
		panel.setLayout(new GridLayout(0, 1, 0, 0));

		JPanel panelNewSnippet = new JPanel();
		panel.add(panelNewSnippet);
		panelNewSnippet.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

		txtName = new JTextField();
		panelNewSnippet.add(txtName);
		txtName.setHorizontalAlignment(SwingConstants.CENTER);
		txtName.setText("name");
		txtName.setColumns(10);

		txtCommand = new JTextField();
		panelNewSnippet.add(txtCommand);
		txtCommand.setHorizontalAlignment(SwingConstants.CENTER);
		txtCommand.setText("command");
		txtCommand.setColumns(10);

		btnSave = new JButton("save");

		panelNewSnippet.add(btnSave);

		JPanel panelSnippetList = new JPanel();
		panel.add(panelSnippetList);
		panelSnippetList.setLayout(new BorderLayout(0, 0));

		JButton btnSnippetRemove = new JButton("remove");
		panelSnippetList.add(btnSnippetRemove, BorderLayout.EAST);

		JButton btnSnippet = new JButton("New button");
		panelSnippetList.add(btnSnippet, BorderLayout.CENTER);

		JScrollPane scrollPane = new JScrollPane(panelSnippetList);
		panel.add(scrollPane);

	}

	public JButton getSaveButton() {
		return btnSave;
	}

}
