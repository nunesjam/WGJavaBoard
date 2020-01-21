package awk.nunesjacobs.tcp.server;

import awk.nunesjacobs.tcp.IPandPort;

public class ServerMain implements IPandPort {

	public static void main(String[] args) {
		int port = PORT;
		Server server = new Server(port);
		server.start();
	}

}
