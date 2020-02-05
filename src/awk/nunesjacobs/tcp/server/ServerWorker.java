package awk.nunesjacobs.tcp.server;

import java.io.*;
import java.net.Socket;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import awk.nunesjacobs.tcp.database.InsertQuery;

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
				switch (cmd.toLowerCase()) {
				case (TERMINATE):
					handleLogoff();
					break;
				case (LOGOFF):
					handleLogoff();
					break;
				case (LOGIN):
					handleLogin(outputStream, tokens);
					break;
				case (MESSAGE):
					String[] tokensMessage = StringUtils.split(line, null, 3);
					handleMessages(tokensMessage);
					break;
				case (JOINGROUP):
					String[] tokensGroup = StringUtils.split(line, null, 3);
					handleGroupChatJoin(tokensGroup);
					break;
				case (LEAVEGROUP):
					handleGroupChatLeave(tokens);
					break;
				default:
					String msg = "unknown " + cmd + "\n";
					outputStream.write(msg.getBytes());
					break;
				}
			}
		}
		clientSocket.close();
	}

	private void handleGroupChatLeave(String[] tokens) {
		boolean isItAGroupChat = tokens[1].charAt(0) == '@';
		if (isItAGroupChat) {
			String groupChatTopic = tokens[1];
			groupChatTopicHashSet.remove(groupChatTopic);
		}
	}

	public boolean isMemberOfCurrentTopic(String groupChatTopic) {
		return groupChatTopicHashSet.contains(groupChatTopic);
	}

	private void handleGroupChatJoin(String[] tokens) {
		boolean isItAGroupChat = tokens[1].charAt(0) == '@';
		if (isItAGroupChat) {
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
					//DATABASE INSERT
					InsertQuery insertstmnt = new InsertQuery(msgBody, this.login, receipentOrGroup);
					insertstmnt.insert();
					
					worker.send(msg);
				}
			} else {
				if (receipentOrGroup.equalsIgnoreCase(worker.getLogin())) {
					String msg = " msg " + "<" + this.login + ">" + " " + msgBody + "\n";
					
				//DATABASE INSERT
					InsertQuery insertstmnt = new InsertQuery(msgBody, this.login, receipentOrGroup);
					insertstmnt.insert();
					
					worker.send(msg);
				}
			}
		}
	}

	private void handleLogoff() throws IOException {
		server.removeWorker(this);
		List<ServerWorker> workerList = server.getWorkerList();

		// send other online users current users status
		String onlineMsg = "offline " + "<" + getLogin() + ">" + "\n";
		for (ServerWorker worker : workerList) {
			if (!login.equals(worker.getLogin())) {
				worker.send(onlineMsg);
			}
		}
		clientSocket.close();
	}

	public String getLogin() {

		return this.login;
	}

// informs and handles login i dont know why i put both in one method...
	private void handleLogin(OutputStream outputStream, String[] tokens) throws IOException {
		if (tokens.length == 3) {
			String login = tokens[1];
			String password = tokens[2];

			// login abfrage mit Datenbank wird hier sein
			if ((login.equals("guest") && password.equals("guest")
					|| login.equals("jeff") && password.equals("jeff"))) {

				String msg = "ok login\n";
				outputStream.write(msg.getBytes());
				outputStream.flush();

				this.login = login;
				System.out.println("successfully logged in: " + this.login);

				List<ServerWorker> workerList = server.getWorkerList();

				// send current user all other online logins
				for (ServerWorker worker : workerList) {
					if (worker.getLogin() != null) {
						if (!login.equals(worker.getLogin())) {
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
					if (!login.equals(worker.getLogin())) {
						worker.send(onlineMsg);
					}
				}
			} else {
				String msg = "nah Error login\n";
				outputStream.write(msg.getBytes());
				outputStream.flush();
				System.err.println("Login failed for " + login);
			}
		}
	}

	private void send(String msg) throws IOException {
		if (getLogin() != null) {
			try {
				outputStream.write(msg.getBytes());
				outputStream.flush();
			} catch (Exception ex) {
				ex.printStackTrace();
			}

		}
	}

}
