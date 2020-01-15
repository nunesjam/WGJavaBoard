package awk.nunesjacobs.tcp.client;

public interface UserStatusListener {
	public void online(String login);
	public void offline(String login); 
}
