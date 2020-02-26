package awk.nunesjacobs.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.Scanner;

import awk.nunesjacobs.tcp.server.ChatCommands;

class ChatClient extends ServerMain implements ChatCommands {

	static volatile boolean exitFlag = false;
	static String userName;
	static String group;
	static int port;

	public static void main(String[] args) {
		try {
			Scanner sc = new Scanner(System.in); // Scanner
			group = "238.0.0.0";
			port = 1234;
			System.out.print("Enter your name: ");
			userName = sc.nextLine();

			ServerMain server = new ServerMain(group, port, userName); // Server starten
			server.joinGroup();

			while (true) {
				String message;
				message = sc.nextLine();
				if (message.equalsIgnoreCase(ChatCommands.TERMINATE)) { // Wenn jemand Exit oder exit schreibt beendet
																		// man das programm (Man kann hier den Input
																		// des Schlieﬂen Knopfs referenzieren)
					exitFlag = true;
					server.leaveGroup();
					server.close();
					sc.close();
					System.exit(0);
					break;
				}
				message = userName + ": " + message;
				byte[] buffer = message.getBytes();
				DatagramPacket datagram = new DatagramPacket(buffer, buffer.length, InetAddress.getByName(group), port);
				server.send(datagram);
			}
		} catch (IOException ie) {
			System.out.println("Error reading/writing from/to socket");
			ie.printStackTrace();
		}
	}

}
