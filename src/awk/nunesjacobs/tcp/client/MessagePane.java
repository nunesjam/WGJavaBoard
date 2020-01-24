package awk.nunesjacobs.tcp.client;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

public class MessagePane extends JPanel implements MessageListener {

	private static final long serialVersionUID = 1L;
	private final ChatClient client;
	private final String login;

	private DefaultListModel<String> listModel = new DefaultListModel<>();
	private JList<String> messageList = new JList<>(listModel);
	private JTextField inputField = new JTextField();

	public MessagePane(ChatClient client, String login) {
		this.client = client;
		this.login = login;

		client.registerMessageListener(this);

		setLayout(new BorderLayout());
		add(new JScrollPane(messageList), BorderLayout.CENTER);
		add(inputField, BorderLayout.SOUTH);

		inputField.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					String msgBody = inputField.getText();
					client.msg(login, msgBody);
					listModel.addElement("You: " + msgBody);
					inputField.setText("");
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
	}

	@Override
	public void onMessage(String fromLogin, String msgBody) {
		if (login.equalsIgnoreCase(fromLogin)) {
			String line = fromLogin + ": " + msgBody;
			listModel.addElement(line);
		}
	}

}
