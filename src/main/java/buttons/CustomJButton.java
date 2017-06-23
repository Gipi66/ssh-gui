package buttons;

import javax.swing.JButton;
import javax.swing.JTextField;

public class CustomJButton extends JButton {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1028834873984792327L;

	private String command;

	public CustomJButton(String name, String command) {
		super(name);
		this.command = command;
		System.out.println("CONSTR: command - " + command);
	}

	public String getCommand() {
		return command;
	}

}
