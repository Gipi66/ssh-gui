package gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

public class ExtraPanel extends JPanel {
	private JTextField txtName;
	private JTextField txtCommand;
	private JPanel panelSnippetList;
	private JButton btnSave;
	private JTextField inputField;

	private ArrayList<JButton> commandsList;

	/**
	 * Create the panel.
	 */
	public ExtraPanel(JButton btnSaveExtraPanel) {
		this.inputField = inputField;
		this.btnSave = btnSaveExtraPanel;
		btnSave.setText("save");
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

		panelNewSnippet.add(btnSave);

		panelSnippetList = new JPanel();
		panel.add(panelSnippetList);
		// panelSnippetList.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

		JScrollPane scrollPane = new JScrollPane(panelSnippetList);
		panelSnippetList.setLayout(new GridLayout(0, 2, 0, 0));
		panel.add(scrollPane);

	}

	public JButton getSaveButton() {
		return btnSave;
	}

	public String getSaveName() {
		return txtName.getText();
	}

	public String getSaveCommand() {
		return txtCommand.getText();
	}

	public void addComponent(JButton btnCommand, JButton btnRemoveCommand) {
		panelSnippetList.add(btnCommand);
		panelSnippetList.add(btnRemoveCommand);
		revalidate();
		repaint();
	}

}
