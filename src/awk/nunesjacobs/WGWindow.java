package awk.nunesjacobs;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JButton;
import javax.swing.JTextPane;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Font;
import javax.swing.JMenuBar;
import java.awt.TextArea;
import javax.swing.JEditorPane;


public class WGWindow extends JFrame {

	private JPanel contentPane;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					WGWindow frame = new WGWindow();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public WGWindow() {
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 1070, 625);
		
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		JButton btnCloseWindow = new JButton("close connection");
		menuBar.add(btnCloseWindow);
		btnCloseWindow.setFont(new Font("Bahnschrift", Font.BOLD, 14));
		btnCloseWindow.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JTextPane textPane = new JTextPane();
		textPane.setBounds(20, 378, 935, 166);
		contentPane.add(textPane);
		
		TextArea chatWindowReadOnly = new TextArea();
		chatWindowReadOnly.setBounds(10, 10, 1044, 363);
		chatWindowReadOnly.setFont(new Font("Bahnschrift", Font.PLAIN, 15));
		chatWindowReadOnly.setEditable(false);
		contentPane.add(chatWindowReadOnly);
		
		JEditorPane editorPane = new JEditorPane();
		editorPane.setBounds(5, 379, 650, 154);
		contentPane.add(editorPane);
		
		JButton btnNewButton = new JButton("New button");
		btnNewButton.setBounds(965, 378, 89, 56);
		contentPane.add(btnNewButton);
	}
}
