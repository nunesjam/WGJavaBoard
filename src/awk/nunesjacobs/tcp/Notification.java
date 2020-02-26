package awk.nunesjacobs.tcp;

import java.awt.*;
import java.awt.TrayIcon.MessageType;

public class Notification {

	private String message;
	private String login;

	public Notification(String message, String login) throws AWTException {
		if (SystemTray.isSupported()) {
			this.message = message;
			this.login = login;
		} else {
			System.err.println("System tray not supported!");
		}
	}

	public void displayTray() throws AWTException {
		// Obtain only one instance of the SystemTray object
		SystemTray tray = SystemTray.getSystemTray();
		// If the icon is a file
		Image image = Toolkit.getDefaultToolkit().createImage("icon.png");
		// Alternative (if the icon is on the classpath):
		// Image image =
		// Toolkit.getDefaultToolkit().createImage(getClass().getResource("icon.png"));
		TrayIcon trayIcon = new TrayIcon(image, "New Message");
		// Let the system resize the image if needed
		trayIcon.setImageAutoSize(true);
		// Set tooltip text for the tray icon
		trayIcon.setToolTip("WG Boards Chat Client");
		tray.add(trayIcon);
		trayIcon.displayMessage("You have a new Message from " + this.login, this.message, MessageType.INFO);
	}
}