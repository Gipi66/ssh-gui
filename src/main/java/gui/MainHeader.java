package gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import org.eclipse.wb.swing.FocusTraversalOnArray;

@SuppressWarnings("serial")
public class MainHeader extends JPanel {
	private JTextField txtCiuaciua;
	private JPasswordField passwordField;
	private JButton btnAddUser;
	private JPanel panelUsers;
	private JPanel panel;

	JPanel panelNewUser;
	private JScrollPane scrollPane;
	private JScrollPane scrollPane_1;

	/**
	 * Create the panel.
	 */
	public MainHeader(JButton btnAddUser, JTextField txtCiuaciua, JPasswordField passwordField, JPanel panelUsers,
			JPanel panel) {
		this.btnAddUser = btnAddUser;
		this.passwordField = passwordField;
		this.txtCiuaciua = txtCiuaciua;
		txtCiuaciua.setText("@ci.ua");
		this.panelUsers = panelUsers;
		this.panel = panel;

		setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

		// JPanel panel = new JPanel();

		scrollPane = new JScrollPane(panel);
		panel.setLayout(new BorderLayout(0, 0));

		panelNewUser = new JPanel();
		panel.add(panelNewUser, BorderLayout.NORTH);
		panelNewUser.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

		scrollPane_1 = new JScrollPane(panelUsers);
		panel.add(scrollPane_1, BorderLayout.SOUTH);

		panelNewUser.add(txtCiuaciua);
		txtCiuaciua.setColumns(10);

		passwordField.setToolTipText("password");
		passwordField.setColumns(10);
		panelNewUser.add(passwordField);

		// JButton btnAddUser = new JButton("New User");
		panelNewUser.add(btnAddUser);

		// panelUsers = new JPanel();
		// panel.add(panelUsers);
		panelUsers.setLayout(new GridLayout(0, 2, 0, 0));
		add(scrollPane);
	}

	public MainHeader() {
		this(new JButton(), new JTextField(), new JPasswordField(), new JPanel(), new JPanel());

		setFocusTraversalPolicy(new FocusTraversalOnArray(new Component[] { scrollPane, panel, panelNewUser,
				txtCiuaciua, passwordField, btnAddUser, panelUsers }));
	}

}
