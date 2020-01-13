package awk.nunesjacobs;

import java.util.ArrayList;

public interface ChatRecord {

	ArrayList<ChatRecord> chatRecord = new ArrayList<>();
	

	public ArrayList<ChatRecord> getChatRecord();

	public void setChatRecord(String message);
	
	public void printChatRecord();
	
}
