package awk.nunesjacobs.tcp.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import awk.nunesjacobs.udp.ChatCommands;

public class ServerWorker extends Thread implements ChatCommands {

	private final Socket clientSocket;
	private final Server server;
	private String login = null;
	private OutputStream outputStream;

	public ServerWorker(Server server, Socket clientSocket) {
		this.server = server;
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
		this.outputStream = clientSocket.getOutputStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
		String line;
		while ((line = reader.readLine()) != null) {
			String[] tokens = StringUtils.split(line);
			if (tokens != null && tokens.length > 0) {
				String cmd = tokens[0];
				if (cmd.equalsIgnoreCase(TERMINATE)|| cmd.equalsIgnoreCase(LOGOFF)) {
					handleLogoff();
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

	private void handleLogoff() throws IOException {
		List<ServerWorker> workerList = server.getWorkerList();
		// send other online users current users status
		String onlineMsg = "offline " + login + "\n";
		for (ServerWorker worker : workerList) {
			if (!login.contentEquals(worker.getLogin())) {
				worker.send(onlineMsg);
			}
		}
		clientSocket.close();
	}

// informs and handles login i dont know why i put both in one method...
	private void handleLogin(OutputStream outputStream, String[] tokens) throws IOException {
		if (tokens.length == 3) {
			String login = tokens[1];
			String password = tokens[2];

			if (login.equals("guest") && password.equals("guest")) {

				String msg = "ok login \n";
				outputStream.write(msg.getBytes());

				this.login = login;

				System.out.println("SUCC youre logged in: " + this.login);

				List<ServerWorker> workerList = server.getWorkerList();

				// send current user all other online logins
				for (ServerWorker worker : workerList) {
					if (!login.contentEquals(worker.getLogin())) {
						if (worker.getLogin() != null) {
							String msg2 = "online " + worker.getLogin() + "\n";
							send(msg2);
						}
					}
				}

				// send other online users current users status
				String onlineMsg = "online " + login + "\n";
				for (ServerWorker worker : workerList) {
					if (!login.contentEquals(worker.getLogin())) {
						worker.send(onlineMsg);
					}
				}
			} else {
				String msg = "nah u cant pass here \n";
				outputStream.write(msg.getBytes());
			}
		}
	}

	private String getLogin() {

		return this.login;
	}

	private void send(String msg) throws IOException {
		if (login != null) {
			outputStream.write(msg.getBytes());
		}
	}

}
