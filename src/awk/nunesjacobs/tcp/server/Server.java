package awk.nunesjacobs.tcp.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server extends Thread {

	private final int serverPort;
	
	private ArrayList<ServerWorker> workerList = new ArrayList <>();

	public Server(int serverPort) {
		this.serverPort = serverPort;
	}
	
	public List<ServerWorker> getWorkerList() {
		return workerList;
	}

	@Override
	public void run() {
		try {
			ServerSocket serverSocket = new ServerSocket(serverPort);
			while (true) {
				Socket clientSocket = serverSocket.accept();
				ServerWorker worker = new ServerWorker(this, clientSocket);
				workerList.add(worker);
				worker.start();
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
