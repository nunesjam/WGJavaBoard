package awk.nunesjacobs.tcp.server;


public class ServerMain implements ServerData {

	public static void main(String[] args) {
		int port = PORT;
		Server server = new Server(port);
		server.start();
	}

}
