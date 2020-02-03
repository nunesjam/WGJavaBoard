package awk.nunesjacobs.tcp.client;

import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import awk.nunesjacobs.tcp.IPandPort;

public class UserListPane extends JPanel implements UserStatusListener, IPandPort, MessageListener {

	private static final long serialVersionUID = 1L;
	private final ChatClient client;
	private JList<String> userListUI;
	private DefaultListModel<String> userListModel;

	public UserListPane(ChatClient client, String login) {
		this.client = client;
		this.client.registerUserStatusListener(this);
		this.client.registerMessageListener(this);
		userListModel = new DefaultListModel<>();
		userListUI = new JList<>(userListModel);
		setLayout(new BorderLayout());
		add(new JScrollPane(userListUI), BorderLayout.CENTER);

		userListUI.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() > 1) {
					String login = userListUI.getSelectedValue();
					MessagePane messagePane = new MessagePane(client, login);
					JFrame frame = new JFrame("Message: " + login);
					frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
					frame.setSize(500, 500);
					frame.getContentPane().add(messagePane, BorderLayout.CENTER);
					frame.setVisible(true);
				}
			}
		});
	}

	public UserListPane(ChatClient client) {
		this.client = client;
		this.client.registerUserStatusListener(this);
		userListModel = new DefaultListModel<>();
		userListUI = new JList<>(userListModel);
		setLayout(new BorderLayout());
		add(new JScrollPane(userListUI), BorderLayout.CENTER);
		userListUI.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() > 1) {
					String login = userListUI.getSelectedValue();
					MessagePane messagePane = new MessagePane(client, login);
					JFrame frame = new JFrame("Message: " + login);
					frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
					frame.setSize(500, 500);
					frame.getContentPane().add(messagePane, BorderLayout.CENTER);
					frame.setVisible(true);
				}
			}
		});
	}

	public static void main(String[] args) {
		ChatClient client = new ChatClient(IP, PORT);
		UserListPane userListPane = new UserListPane(client);
		JFrame frame = new JFrame(" User List ");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(400, 600);
		frame.getContentPane().add(userListPane, BorderLayout.CENTER);
		frame.setVisible(true);
		if (client.connect()) {
			try {
				client.login("guest", "guest");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void online(String login) {
		userListModel.addElement(login);
	}

	@Override
	public void offline(String login) {
		userListModel.removeElement(login);
	}

	@Override
	public void onMessage(String fromLogin, String msgBody) {
		try {
			Notification nd = new Notification(msgBody, fromLogin);
			nd.displayTray();
		} catch (AWTException e) {
			e.printStackTrace();
		}

	}

}
