package awk.nunesjacobs.tcp.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import awk.nunesjacobs.tcp.server.ChatCommands;
import awk.nunesjacobs.tcp.server.ServerData;

import org.apache.commons.lang3.StringUtils;

// ====> https://www.youtube.com/watch?v=CqWorn8dR_A <=====

public class ChatClient implements ChatCommands, ServerData {
	private final String serverName;
	private final int serverPort;
	private Socket socket;
	private OutputStream serverOut;
	private InputStream serverIn;
	private BufferedReader bufferedIn;

	private ArrayList<UserStatusListener> userStatusListeners = new ArrayList<>();
	private ArrayList<MessageListener> messageListeners = new ArrayList<>();

	public ChatClient(String serverName, int serverPort) {
		this.serverName = serverName;
		this.serverPort = serverPort;
	}

	public static void main(String[] args) throws IOException {
		ChatClient client = new ChatClient(IP, PORT);

		// register User agent that listens to everyone that goes online and offline
		client.registerUserStatusListener(new UserStatusListener() {
			@Override
			public void online(String login) {
				System.out.println("ONLINE: " + login);
			}

			@Override
			public void offline(String login) {
				System.out.println("OFFLINE: " + login);
			}
		});

		// register User agent that listens to in- and outcoming messages
		client.registerMessageListener(new MessageListener() {

			@Override
			public void onMessage(String fromLogin, String msgBody) {
				System.out.println("Yout got a message from <" + fromLogin + "> ==> " + msgBody);

			}
		});

		if (!client.connect()) {
			System.err.println("Connection failed.");
		} else {
			System.out.println("Connection established.");
			// login
			if (client.login("guest", "guest")) {
				System.out.println("Login successful");
			} else {
				System.err.println("Login invalid");
			}
			client.logoff();
		}
	}

	private boolean connect() {
		try {
			this.socket = new Socket(serverName, serverPort);
			System.out.println("Client port is " + socket.getLocalPort());
			this.serverOut = socket.getOutputStream();
			this.serverIn = socket.getInputStream();
			this.bufferedIn = new BufferedReader(new InputStreamReader(serverIn));
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	private boolean login(String username, String password) throws IOException {
		String loginMsg = "login " + username + " " + password;
		serverOut.write(loginMsg.getBytes());
		// read line
		String responseFromServer = bufferedIn.readLine();
		System.out.println("Response from server: " + responseFromServer);
		if (responseFromServer.equalsIgnoreCase("ok login")) {
			startMessageReader();
			return true;
		} else {
			return false;
		}
	}

	private void logoff() throws IOException {
		String loginMsg = "logoff \n";
		serverOut.write(loginMsg.getBytes());
	}

	private void startMessageReader() {
		Thread t = new Thread() {
			@Override
			public void run() {
				readMessageLoop();
			}
		};
		t.start();
	}

	private void readMessageLoop() {
		String line;
		try {
			while ((line = bufferedIn.readLine()) != null) {

				String[] tokens = StringUtils.split(line);

				if (tokens != null && tokens.length > 0) {
					String cmd = tokens[0];

					if (cmd.equalsIgnoreCase(ONLINE)) {
						handleOnline(tokens);
					} else if (cmd.equalsIgnoreCase(OFFLINE)) {
						handleOffline(tokens);
					} else if (cmd.equalsIgnoreCase(MESSAGE)) {
						String[] msgToken = StringUtils.split(line, null, 3);
						handleMessage(msgToken);
					}

				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			try {
				socket.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}

	private void handleMessage(String[] msgToken) {
		String login = msgToken[1];
		String msgBody = msgToken[2];
		for (MessageListener listener : messageListeners) {
			listener.onMessage(login, msgBody);
		}
	}

	private void handleOffline(String[] tokens) {
		String login = tokens[1];
		for (UserStatusListener listener : userStatusListeners) {
			listener.offline(login);
		}
	}

	private void handleOnline(String[] tokens) {
		String login = tokens[1];
		for (UserStatusListener listener : userStatusListeners) {
			listener.online(login);
		}
	}

	public void registerUserStatusListener(UserStatusListener listener) {
		userStatusListeners.add(listener);
	}

	public void unregisterUserStatusListener(UserStatusListener listener) {
		userStatusListeners.remove(listener);
	}

	public void registerMessageListener(MessageListener listener) {
		messageListeners.add(listener);
	}

	public void unregisterMessageListener(MessageListener listener) {
		messageListeners.remove(listener);
	}

}
