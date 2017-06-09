package simple_server_manager;

import com.jcraft.jsch.UserInfo;

public class UserInfoClass implements UserInfo {
	String password = null;

	@Override
	public String getPassphrase() {
		return null;
	}

	@Override
	public String getPassword() {
		return password;
	}

	public void setPassword(String passwd) {
		password = passwd;
	}

	@Override
	public boolean promptPassphrase(String message) {
		return false;
	}

	@Override
	public boolean promptPassword(String message) {
		return false;
	}

	@Override
	public boolean promptYesNo(String message) {
		return true;
	}

	@Override
	public void showMessage(String message) {
		// TODO Auto-generated method stub

	}
}
