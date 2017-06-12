package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;
import java.util.Random;
import java.util.logging.Logger;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
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

import lib.StringCrypter;
import simple_server_manager.UserInfoClass;
import streams.CustomInputStream;
import streams.CustomOutputStream;

@SuppressWarnings("serial")
public class MainPanel extends JPanel {
	StringCrypter crypter = new StringCrypter(new byte[] { 1, 4, 5, 6, 8, 9, 7, 8 });

	Properties props = openProps();

	final String propsPath = "config.properties";

	OutputStream output = null;
	InputStream input = null;

	JButton btnAddUser = new JButton("save credentials");
	JPasswordField passwordField = new JPasswordField();
	JTextField txtCiuaciua = new JTextField();
	JPanel panelUsers = new JPanel();
	JPanel headerPanel = new JPanel();
	JTabbedPane tabbedPane;

	private ImageIcon iconDelete = null;

	private ArrayList<CredentialConnection> credConList = new ArrayList<CredentialConnection>();

	public HashMap<String, Session> sessions = new HashMap<String, Session>();

	public MainPanel() {

		iconDelete = new ImageIcon(getClass().getClassLoader().getResource("round-delete-button.png"));

		loadButtons();

		setLayout(new BorderLayout(0, 0));

		JPanel header = new JPanel();
		add(header, BorderLayout.NORTH);
		MainHeader mainHeader = new MainHeader(btnAddUser, txtCiuaciua, passwordField, panelUsers, headerPanel);

		btnAddUser.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				String inputText = txtCiuaciua.getText();
				String inputPasswd = new String(passwordField.getPassword());

				CredentialConnection credCon = new CredentialConnection(inputText, inputPasswd, newId());
				credConList.add(credCon);

				log.info(credCon.user + " " + credCon.password);

				JButton newButton = newButton(credCon);
				JButton removeNewButton = removeButton(newButton);

				panelUsers.add(newButton);
				panelUsers.add(removeNewButton);

				revalidate();
				repaint();

				log.info(credConList.toString());

			}
		});
		header.add(mainHeader);

		JPanel body = new JPanel();
		add(body, BorderLayout.CENTER);
		body.setLayout(new BorderLayout(0, 0));

		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		body.add(tabbedPane);

	}

	private void loadButtons() {
		log.info(props.toString());
		credConList = getListFromProperty(props);
		log.info(credConList.toString());
		for (CredentialConnection credCon : credConList) {
			log.info(credCon.toString());
			JButton newButton = newButton(credCon);
			JButton removeNewButton = removeButton(newButton);
			panelUsers.add(newButton);
			panelUsers.add(removeNewButton);
		}
		revalidate();
		repaint();
	}

	private JButton removeButton(JButton newButtonarg) {
		// String credStr = newButtonarg.ge;

		JButton removeNewButton = new JButton();
		// removeNewButton.setIcon(defaultIcon);
		removeNewButton.setIcon(iconDelete);
		removeNewButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				panelUsers.remove(newButtonarg);
				CredentialConnection targetCredCon = null;
				for (CredentialConnection credCon : credConList) {
					if (credCon.getUserAndHost().equals(newButtonarg.getText())) {
						targetCredCon = credCon;
					}
				}
				if (targetCredCon != null) {
					credConList.remove(credConList.indexOf(targetCredCon));
					for (Object key : targetCredCon.getProps().keySet()) {
						props.remove(key.toString());
					}
				}
				panelUsers.remove(removeNewButton);
				saveProps();
				revalidate();
				repaint();
			};
		});
		return removeNewButton;
	}

	private JButton newButton(CredentialConnection credCon) {
		JButton newButton = new JButton();
		newButton.setText(credCon.getUserAndHost());
		newButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				System.out.println(newButton.getText());

				Session session = null;
				try {
					session = startShellSession(credCon.user, credCon.host, credCon.password);

					JLayeredPane layeredPane = new JLayeredPane();
					tabbedPane.addTab(credCon.getUserAndHost(), null, layeredPane, null);
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
					newButton.setBackground(Color.GREEN);
					// pack();
					props.putAll(credCon.getProps());
					saveProps();

				} catch (JSchException err) {
					newButton.setBackground(Color.RED);
				} catch (Exception err) {
					err.printStackTrace();
				}
				// session.disconnect();

			}
		});
		return newButton;
	}

	private void saveProps() {
		Properties propsForSave = new Properties();
		propsForSave.putAll(props);

		for (Object key : props.keySet()) {
			if (key.toString().contains("password")) {
				String encBase64Str = crypter.encrypt(props.get(key.toString()).toString());
				log.info(encBase64Str);
				propsForSave.put(key.toString(), encBase64Str);
			}
		}
		log.info("propsForSave\n" + propsForSave.toString());

		try {
			output = new FileOutputStream(propsPath);
			propsForSave.store(output, null);
		} catch (Exception err) {
			err.printStackTrace();
		} finally {
			try {
				output.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private Properties openProps() {
		Properties openedProps = new Properties();
		// openedProps.credConList = credConList;
		try {
			input = new FileInputStream(propsPath);
			openedProps.load(input);
		} catch (Exception err) {
			err.printStackTrace();
		} finally {
			Properties propLoop = new Properties();
			propLoop.putAll(openedProps);
			for (Object key : propLoop.keySet()) {
				log.info("key: \n\n" + key.toString());
				if (key.toString().contains("password")) {
					String enValue = openedProps.getProperty(key.toString()).toString();

					String deValue = crypter.decrypt(enValue);

					// log.info("enValue: " +
					// crypter.decrypt(enValue).toString());
					log.info("deValue " + deValue);
					// // log.info("decryptedStr " + deValue);
					openedProps.put(key.toString(), deValue);
				}
			}
			log.info(openedProps.toString());
		}

		return openedProps;
	}

	private CredentialConnection getCredConById(String id) {
		CredentialConnection results = null;
		for (CredentialConnection credCon : credConList) {
			if (credCon.id.equals(id)) {
				results = credCon;
				break;
			}
		}
		return results;
	}

	private ArrayList<CredentialConnection> getListFromProperty(Properties props) {

		ArrayList<CredentialConnection> credConListChecked = new ArrayList<CredentialConnection>();

		for (Object key : props.keySet()) {
			String id = key.toString().substring(0, key.toString().indexOf('.'));
			String field = key.toString().substring(key.toString().indexOf('.') + 1);
			String value = props.getProperty(key.toString());
			log.info(id + " " + field + " " + value);
			CredentialConnection credCon = getCredConById(id);
			// log.info(credCon.toString());
			if (credCon == null) {
				credCon = new CredentialConnection(id);
				credConList.add(credCon);
			}
			credCon.setField(field, value);
		}
		for (CredentialConnection credCon : credConList) {
			if (!credCon.isEmpty()) {
				credConListChecked.add(credCon);
			}
		}
		credConListChecked.forEach(i -> log.info(i.toString()));
		return credConListChecked;

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

	String newId() {
		Random rand = new Random();
		boolean isEqual = false;
		String newId = null;
		while (true) {
			newId = new Integer(rand.nextInt(10000)).toString();
			for (CredentialConnection crCon : credConList) {
				if (crCon.isEqualID(newId)) {
					isEqual = true;
				}
			}
			if (isEqual != true) {
				break;
			}
		}
		return newId;
	}

	class CredentialConnection {
		String user = null;
		String id = null;
		String password = null;

		String host = null;

		CredentialConnection(String id) {
			this.id = id;
		}

		CredentialConnection(String login, String password, String id) {
			this.user = login.substring(0, login.indexOf('@'));
			this.host = login.substring(login.indexOf('@') + 1);
			this.password = password;
			this.id = id;
		}

		boolean isEqualID(String id) {
			return this.id.equals(id);
		}

		void setField(String fieldName, String value) {

			if (fieldName.equals("user")) {
				this.user = value;
			}
			if (fieldName.equals("host")) {
				this.host = value;
			}
			if (fieldName.equals("password")) {
				this.password = value;
			}
		}

		boolean isEmpty() {
			boolean results = false;
			if (user == null || password == null || host == null) {
				results = true;
			}
			// log.info(results ? "Empty" : "NOT EMPTY");
			return results;
		}

		Properties getProps() {
			return new Properties() {
				{
					put(id + ".user", user);
					put(id + ".password", password);
					put(id + ".host", host);
				}
			};
		}

		String getUserAndHost() {
			return user + "@" + host;
		}

		public String toString() {
			return String.format("id: %s,  login: %s, password: %s", id, getUserAndHost(), password);
		}
	}
}
