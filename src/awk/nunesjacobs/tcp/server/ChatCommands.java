package awk.nunesjacobs.tcp.server;

public interface ChatCommands {
	static final String TERMINATE = "exit";
	static final String LOGIN ="login";
	static final String LOGOFF = "logoff";
	static final String MESSAGE = "msg";
	static final String JOINGROUP = "join";
	static final String LEAVEGROUP = "leave";
	static final String ONLINE = "online";
	static final String OFFLINE = "offline";
}
