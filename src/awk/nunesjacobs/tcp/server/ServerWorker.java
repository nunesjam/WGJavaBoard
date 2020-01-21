package awk.nunesjacobs.tcp.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

public class ServerWorker extends Thread implements ChatCommands {

	private final Socket clientSocket;
	private final Server server;
	private String login = null;
	private OutputStream outputStream;
	private HashSet<String> groupChatTopicHashSet = new HashSet<>();

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

				if (cmd.equalsIgnoreCase(TERMINATE) || cmd.equalsIgnoreCase(LOGOFF)) {
					handleLogoff();
					break;
				} else if (cmd.equalsIgnoreCase(LOGIN)) {
					handleLogin(outputStream, tokens);
				} else if (cmd.equalsIgnoreCase(MESSAGE)) {
					String[] msgToken = StringUtils.split(line, null, 3);
					handleMessages(msgToken);
				} else if (cmd.equalsIgnoreCase(JOINGROUP)) {
					handleGroupChatJoin(tokens);
				} else if (cmd.equalsIgnoreCase(LEAVEGROUP)) {
					handleGroupChatLeave(tokens);
				} else {
					String msg = "<" + getLogin() + ">" + " " + line + "\n";
					outputStream.write(msg.getBytes());
				}
			}
		}
		clientSocket.close();
	}

	private void handleGroupChatLeave(String[] tokens) {
		if (tokens.length > 1) {
			String groupChatTopic = tokens[1];
			groupChatTopicHashSet.remove(groupChatTopic);
		}
	}

	public boolean isMemberOfCurrentTopic(String groupChatTopic) {
		return groupChatTopicHashSet.contains(groupChatTopic);
	}

	private void handleGroupChatJoin(String[] tokens) {
		if (tokens.length > 1) {
			String groupChatTopic = tokens[1];
			groupChatTopicHashSet.add(groupChatTopic);

		}
	}

	// format: "msg "login" msg
	private void handleMessages(String[] tokens) throws IOException {
		String receipentOrGroup = tokens[1];
		String msgBody = tokens[2];

		boolean isItAGroupChat = receipentOrGroup.charAt(0) == '@';

		List<ServerWorker> workerList = server.getWorkerList();

		for (ServerWorker worker : workerList) {
			if (isItAGroupChat) {
				if (worker.isMemberOfCurrentTopic(receipentOrGroup)) {
					String msg = "msg " + receipentOrGroup + ": " + "<" + getLogin() + "> " + " " + msgBody + "\n";
					worker.send(msg);
				}
			} else if (receipentOrGroup.equalsIgnoreCase(worker.getLogin())) {
				String msg = " msg " + "<" + getLogin() + ">" + " " + msgBody + "\n";
				worker.send(msg);
			}
		}
	}

	private void handleLogoff() throws IOException {
		List<ServerWorker> workerList = server.getWorkerList();
		// send other online users current users status
		String onlineMsg = "offline " + "<" + getLogin() + ">" + "\n";
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

				System.out.println("successfully logged in: " + this.login );

				List<ServerWorker> workerList = server.getWorkerList();

				// send current user all other online logins
				for (ServerWorker worker : workerList) {
					if (!login.contentEquals(worker.getLogin())) {
						if (worker.getLogin() != null) {
							String msg2 = "online " + worker.getLogin() + "\n";
							send(msg2);
						}
					} else {
						System.err.println("User is already signed in.");
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
				String msg = "nah u cant park here \n";
				outputStream.write(msg.getBytes());
				System.err.println("Login failed for " + login);
			}
		}
	}

	private String getLogin() {

		return this.login;
	}

	private void send(String msg) throws IOException {
		if (getLogin() != null) {
			outputStream.write(msg.getBytes());
		}
	}

}
