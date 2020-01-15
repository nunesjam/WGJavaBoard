package awk.nunesjacobs.tcp.client;

public interface MessageListener {
	public void onMessage(String fromLogin, String msgBody);
}
