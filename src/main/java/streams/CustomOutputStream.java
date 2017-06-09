package streams;

import java.io.IOException;
import java.io.OutputStream;

import javax.swing.JTextArea;

import simple_server_manager.Main;

public class CustomOutputStream extends OutputStream {

	private JTextArea textArea;

	public CustomOutputStream(JTextArea textArea) {
		this.textArea = textArea;
	}

	@Override
	public void write(int b) throws IOException {
		// redirects data to the text area
		textArea.append(new String(String.valueOf((char) b).getBytes("iso-8859-1"), "iso-8859-1"));
		// scrolls the text area to the end of data
		textArea.setCaretPosition(textArea.getText().length());
	}

	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		String s = new String(b, off, len);
		textArea.append(s);
		textArea.setCaretPosition(textArea.getText().length());
	}

	@Override
	public void write(byte[] b) throws IOException {
		this.write(b, 0, b.length);
	}
}