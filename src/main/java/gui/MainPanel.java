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
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Properties;
import java.util.Random;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UserInfo;

import buttons.CustomJButton;
import lib.StringCrypter;
import simple_server_manager.UserInfoClass;
import streams.CustomInputStream;
import streams.CustomOutputStream;

@SuppressWarnings("serial")
public class MainPanel extends JPanel {
	StringCrypter crypter = new StringCrypter(new byte[] { 1, 4, 5, 6, 8, 9, 7, 8 });
	ArrayList<ShellSession> sessionList;
	Properties props = openProps();

	final String propsPath = "config.ini";

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
		sessionList = new ArrayList<ShellSession>();

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
				JButton removeNewButton = removeButton(newButton, false, credCon);

				log.info("Добавляю кнопки");
				panelUsers.add(newButton);
				panelUsers.add(removeNewButton);
				log.info("Добавлены кнопки");

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
			JButton removeNewButton = removeButton(newButton, false, credCon);
			panelUsers.add(newButton);
			panelUsers.add(removeNewButton);
		}
		revalidate();
		repaint();
	}

	private JButton removeButton(JButton newButtonarg, boolean isCommand, CredentialConnection credConn) {
		// String credStr = newButtonarg.ge;

		JButton removeNewButton = new JButton();
		// removeNewButton.setIcon(defaultIcon);
		removeNewButton.setIcon(iconDelete);
		removeNewButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				removeButtonFromPanel(newButtonarg);

				if (isCommand) {

					String propsKey = credConn.id + "." + "commands";
					String oldCommands = props.getProperty(propsKey);
					ArrayList<String> newCommands = new ArrayList<String>();

					for (String oldCom : oldCommands.split("@next@")) {
						if (!oldCom.split("@")[0].equals(((CustomJButton) newButtonarg).getText())) {
							log.info(oldCom.split("@")[0] + "!=" + ((CustomJButton) newButtonarg).getText());
							if (!oldCom.isEmpty() && oldCom != "") {
								newCommands.add(oldCom);
							}
						}
					}
					log.info(String.format(
							"###########DELETE isComman. propsKey: %s, oldCommands: %s, newCommands: %s, buttonText: %s",
							propsKey, oldCommands, newCommands, ((CustomJButton) newButtonarg).getText()));
					if (!newCommands.isEmpty()) {
						props.setProperty(propsKey, String.join("@next@", newCommands));
					}
				} else {
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
				}
				removeButtonFromPanel(removeNewButton);
				saveProps();
				revalidate();
				repaint();
			};
		});
		return removeNewButton;
	}

	private void removeButtonFromPanel(JButton btn) {
		btn.getParent().remove(btn);
	}

	private JButton newButton(CredentialConnection credCon) {
		JButton newButton = new JButton();
		newButton.setText(credCon.getUserAndHost());
		newButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				System.out.println(newButton.getText());

				ShellSession shellSession = null;
				try {
					// session = startShellSession(credCon.user, credCon.host,
					// credCon.password);

					shellSession = new ShellSession(credCon);
					sessionList.add(shellSession);

					JLayeredPane layeredPane = new JLayeredPane();
					tabbedPane.addTab(credCon.getUserAndHost(), null, layeredPane, null);
					layeredPane.setLayout(new BoxLayout(layeredPane, BoxLayout.X_AXIS));

					ConsoleMain console = new ConsoleMain();
					layeredPane.add(console, BorderLayout.EAST);

					// Channel channel = session.openChannel("shell");

					JTextField inputField = console.getInputComponent();

					CustomInputStream cis = new CustomInputStream(inputField);

					JTextArea textPane = console.getOutComponent();
					textPane.addMouseListener(new MouseAdapter() {
						@Override
						public void mouseClicked(MouseEvent e) {
							inputField.requestFocusInWindow();
						}
					});

					JButton btnSaveExtraPanel = new JButton();
					ExtraPanel extraPanel = new ExtraPanel(btnSaveExtraPanel, props);
					log.info("PRE SET");

					layeredPane.add(extraPanel);
					CustomOutputStream cos = new CustomOutputStream(textPane);
					// channel.setOutputStream(cos);
					shellSession.setStreams(cis, cos);
					shellSession.connect(3 * 1000);

					newButton.setBackground(Color.GREEN);

					String thisCommand = props.getProperty(credCon.id + ".commands");

					if (thisCommand != null) {
						for (String commands : thisCommand.split("@next@"))
							if (commands != null || !commands.isEmpty()) {
								String[] nameAndCommand = commands.split("@");
								newBtnCommand(nameAndCommand[0], nameAndCommand[1], extraPanel, credCon, cis);
							}

					}
					btnSaveExtraPanel.addMouseListener(new MouseAdapter() {
						@Override
						public void mouseClicked(MouseEvent e) {
							newBtnCommand(extraPanel.getSaveName(), extraPanel.getSaveCommand(), extraPanel, credCon,
									cis);

						}
					});
					props.putAll(credCon.getProps());
					// saveProps();

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

	private void newBtnCommand(String name, String command, ExtraPanel panel, CredentialConnection credCon,
			CustomInputStream cis) {
		CustomJButton btnCommand = new CustomJButton(name, command);
		btnCommand.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				log.info("USE COMMAND: " + btnCommand.getCommand());
				cis.sendCommand(btnCommand.getCommand());

			}

		});
		JButton btnRemoveCommand = removeButton(btnCommand, true, credCon);
		panel.addComponent(btnCommand, btnRemoveCommand);
		String propsKey;

		String nameAndCommand = String.format("%s@%s@next@", btnCommand.getText(), btnCommand.getCommand());
		propsKey = credCon.id + ".commands";

		String propsOld = props.getProperty(propsKey);

		props.put(credCon.id + ".commands", propsOld != null ? propsOld + nameAndCommand : nameAndCommand);
		// saveProps();
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

		HashSet<Integer> idSet = new HashSet<Integer>();
		for (Object key : props.keySet()) {
			String[] keys = key.toString().split(Pattern.quote("."));

			if (keys.length == 2)
				idSet.add(Integer.parseInt(keys[0]));
		}
		log.info("#####" + idSet.toString());

		for (Integer id : idSet) {
			String idStr = id.toString();

			CredentialConnection credCon = getCredConById(idStr);
			if (credCon == null) {
				credCon = new CredentialConnection(idStr, props);
				credConList.add(credCon);
			} else if (credCon != null) {
				credCon.setProps(props);
			}

			// String field =
			// key.toString().substring(key.toString().indexOf('.') + 1);
			// String value = props.getProperty(key.toString());
			// log.info(id + " " + field + " " + value);

		}
		for (CredentialConnection credCon : credConList) {
			if (!credCon.isEmpty()) {
				credConListChecked.add(credCon);
			}
		}
		credConListChecked.forEach(i -> log.info(i.toString()));
		return credConListChecked;

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

	public void closeConnections() {
		for (ShellSession shellSessionI : sessionList) {
			try {
				shellSessionI.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	class CredentialConnection {
		String user;
		String id;
		String password;

		String host;

		String commands;

		CredentialConnection(String id, Properties props) {
			this.id = id;
			setProps(props);
		}

		CredentialConnection(String login, String password, String id) {
			this.user = login.substring(0, login.indexOf('@'));
			this.host = login.substring(login.indexOf('@') + 1);
			this.password = password;
			this.id = id;

			this.commands = "";
		}

		void setProps(Properties props) {
			String propertyName;
			String propertyValue;

			log.info(props.get(id + "11") != null ? "!=0" : "==0");

			Field[] fields = this.getClass().getDeclaredFields();
			for (Field field : fields) {

				try {

					propertyName = id + "." + field.getName();
					propertyValue = props.getProperty(propertyName);

					if (propertyValue != null) {
						field.set(this, propertyValue);
					}
					log.info("###FIELD: " + field.getName() + ": " + field.get(this));
					// field.set(this, value);
				} catch (IllegalArgumentException | IllegalAccessException e) {
					e.printStackTrace();

				} catch (SecurityException e) {
					e.printStackTrace();
				}
			}
			log.warning("##################THIS:\n" + this.toString());
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
