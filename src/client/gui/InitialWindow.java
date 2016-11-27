package client.gui;

import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.JLabel;

import java.awt.Font;

import javax.swing.JButton;

import client.Client;
import client.ClientListener;
import protocol.Command;
import protocol.Command.CommandType;
import protocol.JoinResponseCmd;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Pattern;
import java.awt.Color;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

@SuppressWarnings("serial")
public class InitialWindow extends JFrame implements ClientListener {
	private JPanel contentPane;
	private JTextField nameField;
	Client client=null;
	private JTextField serverField;
	private JTextField portField;
	private JLabel statusLabel;
	private static final Pattern PATTERN = Pattern.compile(
	        "^(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])$");

	public InitialWindow() {
		try {
			UIManager.setLookAndFeel(
			        UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e2) {

		}
		setVisible(true);
		setTitle("Client");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 470, 433);
		contentPane = new JPanel();
		contentPane.setBackground(UIManager.getColor("Button.background"));
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JLabel lblHeader = new JLabel("Please enter the following details to connect");
		lblHeader.setFont(new Font("Tahoma", Font.BOLD, 14));
		lblHeader.setBounds(83, 76, 305, 14);
		contentPane.add(lblHeader);

		JLabel lblNickname = new JLabel("Your Nickname:");
		lblNickname.setFont(new Font("Tahoma", Font.BOLD, 12));
		lblNickname.setBounds(65, 142, 114, 14);
		contentPane.add(lblNickname);

		nameField = new JTextField();
		nameField.setBounds(168, 137, 114, 26);
		nameField.setText("Den");
		nameField.setColumns(10);
		contentPane.add(nameField);

		JLabel lblServerIp = new JLabel("Server IP:");
		lblServerIp.setFont(new Font("Tahoma", Font.BOLD, 12));
		lblServerIp.setBounds(65, 193, 124, 14);
		contentPane.add(lblServerIp);

		serverField = new JTextField();
		serverField.setColumns(10);
		serverField.setBounds(168, 188, 114, 26);
		serverField.setText("127.0.0.1");
		contentPane.add(serverField);


		JLabel lblServerPort = new JLabel("Server Port:");
		lblServerPort.setFont(new Font("Tahoma", Font.BOLD, 12));
		lblServerPort.setBounds(65, 241, 96, 14);
		contentPane.add(lblServerPort);

		portField = new JTextField();
		portField.setColumns(10);
		portField.setBounds(168, 236, 114, 26);
		portField.setText("6001");
		contentPane.add(portField);

		JButton btnConnect = new JButton("Connect");
		btnConnect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
			}
		});

		statusLabel = new JLabel("");
		statusLabel.setFont(new Font("Tahoma", Font.PLAIN, 13));
		statusLabel.setForeground(new Color(255, 0, 0));
		statusLabel.setBounds(65, 285, 379, 47);
		contentPane.add(statusLabel);

		btnConnect.setBackground(UIManager.getColor("Button.background"));
		btnConnect.setOpaque(true);
		btnConnect.setBounds(330, 343, 96, 35);
		contentPane.add(btnConnect);
		InitialWindow initialListener = this;
		btnConnect.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				String name=nameField.getText();
				String serverIP = serverField.getText();
				String serverPort = portField.getText();

				statusLabel.setText("");
				if(name.equals("") || name.equals(" ")) {
					statusLabel.setText("Please enter a valid name!");
					statusLabel.setForeground(Color.RED);
				}
				else if (!isValidIP(serverIP)) {
					statusLabel.setText("Please enter a valid Server IP Address!");
					statusLabel.setForeground(Color.RED);
				}
				else {
					Thread attemptConnection = new Thread() {
						public void run() {
							try{
								int port = Integer.parseInt(serverPort);
								statusLabel.setText("Connecting, Please wait..");
								statusLabel.setForeground(Color.GREEN);
								if (client != null)
									client.terminate();
								client = new Client(initialListener, serverIP, port);
								client.join(name);
							}
							catch(NumberFormatException e) {
								statusLabel.setText("Please enter a valid Server Port Number!");
								statusLabel.setForeground(Color.RED);
							} catch (IOException e) {
								// TODO Auto-generated catch block
								statusLabel.setText("Could not connect to server!");
								statusLabel.setForeground(Color.RED);
								btnConnect.setEnabled(true);
							}
						}
					};
					attemptConnection.start();
				}

			}
		});
	}


	public static boolean isValidIP(final String ip) {
	    return PATTERN.matcher(ip).matches();
	}


	@Override
	public void refreshMemberlist(ArrayList<String> s) {
		statusLabel.setText("An unexpected error has occured!");
		statusLabel.setForeground(Color.RED);

	}

	@Override
	public void onReceiveCommand(Command cmd) {
		if (cmd.getType() == CommandType.JOINRESPONSE) {
			JoinResponseCmd jCmd = (JoinResponseCmd)cmd;
			if (jCmd.isAccepted()) {
				dispose();
				new ChatWindow(client);
			}
			else {
				statusLabel.setText("Name is already taken by another user on the network!");
				statusLabel.setForeground(Color.RED);
			}
		}
		else {
			statusLabel.setText("An unexpected error has occured!");
			statusLabel.setForeground(Color.RED);
		}

	}

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					InitialWindow frame = new InitialWindow();
					frame.setVisible(true);
				} catch (Exception e) {

				}
			}
		});
	}


	@Override
	public void terminate() {
		// TODO Auto-generated method stub

	}

}
