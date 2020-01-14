package awk.nunesjacobs.tcp.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

import org.apache.commons.lang3.StringUtils;

import awk.nunesjacobs.udp.ChatCommands;

public class ServerWorker extends Thread implements ChatCommands {

	private final Socket clientSocket;
	private String login = null;

	public ServerWorker(Socket clientSocket) {
		this.clientSocket = clientSocket;
	}

	@Override
	public void run() {
		try {
			handleClientSocket();
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void handleClientSocket() throws IOException, InterruptedException {
		InputStream inputStream = clientSocket.getInputStream();
		OutputStream outputStream = clientSocket.getOutputStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
		String line;
		while ((line = reader.readLine()) != null) {
			String[] tokens = StringUtils.split(line);
			if (tokens != null && tokens.length > 0) {
				String cmd = tokens[0];
				if (cmd.equalsIgnoreCase(TERMINATE)) {
					break;
				} else if (cmd.equalsIgnoreCase(LOGIN)) {
					handleLogin(outputStream, tokens);
				} else {
					String msg = "unknown " + cmd + "\n";
					outputStream.write(msg.getBytes());
				}
			}
		}
		clientSocket.close();
	}

	private void handleLogin(OutputStream outputStream, String[] tokens) throws IOException {
		if (tokens.length == 3) {
			String login = tokens[1];
			String password = tokens[2];

			if (login.equals("guest") && password.equals("guest")) {
				String msg = "ok login \n";
				outputStream.write(msg.getBytes());
				this.login =login;
				System.out.println("SUCC youre logged in: " + login);
			} else {
				String msg = "nah u cant pass here \n";
				outputStream.write(msg.getBytes());
			}
		}
	}

}
