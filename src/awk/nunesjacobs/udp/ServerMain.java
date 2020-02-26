package awk.nunesjacobs.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

class ServerMain {

	static volatile boolean exitFlag = false;
	static String userName;
	private InetAddress chatIP;
	private int port;
	private MulticastSocket socket;

	public ServerMain() {
		System.out.println("Enter valid Parameters");
	}

	public ServerMain(String chatIP, int port, String userName) {
		try {
			this.chatIP = InetAddress.getByName(chatIP);
			this.port = port;
			ServerMain.userName = userName;
			this.socket = new MulticastSocket(port);

		} catch (IOException e) {
			System.out.println("Error creating socket");
			e.printStackTrace();
		} 

	}

	public void joinGroup() {
		try {
			// Time to Live auf 0 auf Localhost -> Intranet würde ich Anzahl der Computer
			// plus 1 setzen.
			this.socket.setTimeToLive(0);
			this.socket.joinGroup(this.chatIP);
			// Thread der nebenläufig läuft und einkommende Nachrichten liest
			Thread threadChat = new Thread(new ReadThread(this.socket, this.chatIP, this.port));
			threadChat.start();
		} catch (IOException e) {
			System.out.println("Error reading/writing from/to socket");
			e.printStackTrace();
		}
	}

	public void leaveGroup() {
		try {
			this.socket.leaveGroup(this.chatIP);
		} catch (IOException e) {
			System.out.println("Error reading/writing from/to socket");
			e.printStackTrace();
		}
	}

	public void close() {
		this.socket.close();
	}

	public void send(DatagramPacket datagram) {
		try {
			this.socket.send(datagram);
		} catch (IOException e) {
			System.out.println("Error reading/writing from/to socket");
			e.printStackTrace();
		}

	}

}

class ReadThread implements Runnable {
	private MulticastSocket socket;
	private InetAddress group;
	private int port;
	private static final int MAX_LEN = 1000;

	ReadThread(MulticastSocket socket, InetAddress group, int port) {
		this.socket = socket;
		this.group = group;
		this.port = port;
	}

	@Override
	public void run() {
		while (!ServerMain.exitFlag) {
			byte[] buffer = new byte[ReadThread.MAX_LEN];
			DatagramPacket datagram = new DatagramPacket(buffer, buffer.length, group, port);
			String message;
			try {
				socket.receive(datagram);
				message = new String(buffer, 0, datagram.getLength(), "UTF-8");
				if (!message.startsWith(ServerMain.userName))
					System.out.println(message); // Auf den Display "printen"
			} catch (IOException e) {
				System.out.println("Socket closed!");
			}
		}
	}
}
