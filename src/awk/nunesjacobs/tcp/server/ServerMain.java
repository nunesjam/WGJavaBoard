package awk.nunesjacobs.tcp.server;


public class ServerMain {

	public static void main(String[] args) {
		int port = 8818;
		Server server = new Server(port);
		server.start();
	}

}
