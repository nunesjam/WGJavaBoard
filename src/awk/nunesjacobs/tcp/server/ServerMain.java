package awk.nunesjacobs.tcp.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerMain {

	public static void main(String[] args) {
		int port = 8820;
		try {
			ServerSocket serverSocket = new ServerSocket(port);
			while (true) {
				Socket clientSocket = serverSocket.accept();
				ServerWorker worker = new ServerWorker(clientSocket);
				worker.start();
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
