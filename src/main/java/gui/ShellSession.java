package gui;

import java.io.Closeable;
import java.io.IOException;
import java.util.logging.Logger;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UserInfo;

import gui.MainPanel.CredentialConnection;
import simple_server_manager.UserInfoClass;
import streams.CustomInputStream;
import streams.CustomOutputStream;

public class ShellSession implements Closeable {
	final Session session;

	@SuppressWarnings("unused")
	private CustomInputStream cis;
	@SuppressWarnings("unused")
	private CustomOutputStream cos;

	private Channel channel;

	public ShellSession(CredentialConnection credCon) throws JSchException {
		session = startShellSession(credCon.user, credCon.host, credCon.password);
		channel = session.openChannel("shell");
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

	public boolean setStreams(CustomInputStream cis, CustomOutputStream cos) {
		this.cis = cis;
		this.cos = cos;

		channel.setInputStream(cis);
		channel.setOutputStream(cos);
		return true;
	}

	public void connect(int connectTimeout) {
		try {
			channel.connect(connectTimeout);
		} catch (JSchException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void close() throws IOException {
		try {
			if (channel != null && channel.isClosed()) {
				channel.disconnect();
				log.info("channel disonnected");
			}
			if (session != null && session.isConnected()) {
				session.disconnect();
				log.info("session disonnected");
			}
		} catch (Exception e) {
			log.warning("exception when i try close connection");

		} finally {
			log.info("shell closed");
		}

	}

	public static Logger log = Logger.getLogger(ShellSession.class.getName());
}
