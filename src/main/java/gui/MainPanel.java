package gui;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UserInfo;

import simple_server_manager.UserInfoClass;
import streams.CustomInputStream;
import streams.CustomOutputStream;

@SuppressWarnings("serial")
public class MainPanel extends JPanel {
	JButton btnAddUser = new JButton("New User");
	JPasswordField passwordField = new JPasswordField();
	JTextField txtCiuaciua = new JTextField();
	JPanel panelUsers = new JPanel();
	JPanel headerPanel = new JPanel();
	JTabbedPane tabbedPane;

	ArrayList<JButton> userButtons = new ArrayList<JButton>();

	public HashMap<String, Session> sessions = new HashMap<String, Session>();
	public HashMap<String, String> users = new HashMap<String, String>();

	public MainPanel() {
		setLayout(new BorderLayout(0, 0));

		JPanel header = new JPanel();
		add(header, BorderLayout.NORTH);
		MainHeader mainHeader = new MainHeader(users, btnAddUser, txtCiuaciua, passwordField, panelUsers, headerPanel);

		btnAddUser.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				String inputText = txtCiuaciua.getText();
				String inputPasswd = new String(passwordField.getPassword());

				log.info(inputText + " " + inputPasswd);

				users.put(inputText, inputPasswd);

				JButton newButton = new JButton();
				newButton.setText(inputText);
				newButton.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent e) {
						System.out.println(newButton.getText());

						String buttonText = newButton.getText();
						String user = buttonText.substring(0, buttonText.indexOf('@'));
						String host = buttonText.substring(buttonText.indexOf('@') + 1);
						String password = users.get(buttonText);

						Session session = null;
						try {
							session = startShellSession(user, host, password);

							JLayeredPane layeredPane = new JLayeredPane();
							tabbedPane.addTab(buttonText, null, layeredPane, null);
							layeredPane.setLayout(new BoxLayout(layeredPane, BoxLayout.X_AXIS));

							ConsoleMain console = new ConsoleMain();
							layeredPane.add(console);

							Channel channel = session.openChannel("shell");

							OutputStream outChannel = channel.getOutputStream();
							// InputStream inChannel = channel.getInputStream();
							channel.setOutputStream(System.out);
							// channel.setInputStream(System.in);

							JTextField inputField = console.getInputComponent();
							channel.setInputStream(new CustomInputStream(inputField));
							JTextArea textPane = console.getOutComponent();
							channel.setOutputStream(new CustomOutputStream(textPane));
							channel.connect(3 * 1000);
							// pack();

						} catch (Exception err) {
							err.printStackTrace();
						}
						// session.disconnect();

					}
				});
				panelUsers.add(newButton);

				revalidate();
				repaint();

				log.info(users.toString());

			}
		});
		header.add(mainHeader);

		JPanel body = new JPanel();
		add(body, BorderLayout.CENTER);
		body.setLayout(new BorderLayout(0, 0));

		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		body.add(tabbedPane);

	}

	private Session startSession(String user, String host, String password) throws JSchException {

		JSch jsch = new JSch();
		Session session = jsch.getSession(user, host, 22);
		session.setPassword(password);
		// session.connect(30000);
		UserInfo ui = new UserInfoClass();
		session.setUserInfo(ui);
		session.connect();

		return session;
	}

	private Session startShellSession(String user, String host, String password) throws JSchException {

		JSch jsch = new JSch();
		Session session = jsch.getSession(user, host, 22);
		session.setPassword(password);

		UserInfo ui = new UserInfoClass();
		session.setUserInfo(ui);
		session.connect(30000);

		return session;
	}

	public static Logger log = Logger.getLogger(MainPanel.class.getName());

}
