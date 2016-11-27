package server.controller.gui;
import javax.swing.JFrame;

import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JComboBox;
import javax.swing.JButton;
import server.end.Server;
import server.master.MasterServer;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.Color;
import java.awt.EventQueue;


//Run this class to open the admin tool that lets you deploy servers on this machine only.
//To deploy servers that connect to a remote master server, use the classes in server.controller.manual
@SuppressWarnings("serial")
public class AdminTool extends JFrame {

	private JPanel contentPane;

	private MasterServer master;
	private Server s1;
	private Server s2;
	private Server s3;
	private Server s4;

	public AdminTool() {
		try {
			UIManager.setLookAndFeel(
			        UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e2) {

		}
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		setTitle("Server Admin Tool");
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JLabel lblStatus = new JLabel("Status");
		lblStatus.setBounds(32, 103, 46, 14);
		contentPane.add(lblStatus);

		JLabel masterLabel = new JLabel("Master Server");
		masterLabel.setBounds(109, 125, 100, 14);
		contentPane.add(masterLabel);

		JLabel masterStatus = new JLabel("Offline");
		masterStatus.setForeground(Color.RED);
		masterStatus.setBounds(223, 125, 96, 14);
		contentPane.add(masterStatus);

		JLabel serverLabel1 = new JLabel("Server 1");
		serverLabel1.setBounds(109, 150, 60, 14);
		contentPane.add(serverLabel1);

		JLabel statusLabel1 = new JLabel("Offline");
		statusLabel1.setForeground(Color.RED);
		statusLabel1.setBounds(223, 150, 96, 14);
		contentPane.add(statusLabel1);

		JLabel serverLabel2 = new JLabel("Server 2");
		serverLabel2.setBounds(109, 175, 60, 14);
		contentPane.add(serverLabel2);

		JLabel statusLabel2 = new JLabel("Offline");
		statusLabel2.setForeground(Color.RED);
		statusLabel2.setBounds(223, 175, 96, 14);
		contentPane.add(statusLabel2);

		JLabel serverLabel3 = new JLabel("Server 3");
		serverLabel3.setBounds(109, 200, 60, 14);
		contentPane.add(serverLabel3);

		JLabel statusLabel3 = new JLabel("Offline");
		statusLabel3.setForeground(Color.RED);
		statusLabel3.setBounds(223, 200, 96, 14);
		contentPane.add(statusLabel3);

		JLabel serverLabel4 = new JLabel("Server 4");
		serverLabel4.setBounds(109, 225, 60, 14);
		contentPane.add(serverLabel4);

		JLabel statusLabel4 = new JLabel("Offline");
		statusLabel4.setForeground(Color.RED);
		statusLabel4.setBounds(223, 225, 96, 14);
		contentPane.add(statusLabel4);

		JComboBox <String> comboBox = new JComboBox<String>();
		comboBox.setToolTipText("");
		comboBox.setBounds(27, 42, 110, 20);
		contentPane.add(comboBox);

		comboBox.addItem("Master Server");
		comboBox.addItem("Server 1");
		comboBox.addItem("Server 2");
		comboBox.addItem("Server 3");
		comboBox.addItem("Server 4");

		JButton btnNewButton = new JButton("Run");
		btnNewButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent m) {
				String s=(String)comboBox.getSelectedItem();
				switch(s){
					case "Master Server": try{master = new MasterServer(6000); masterStatus.setText("Online");masterStatus.setForeground(Color.GREEN);break;}catch(Exception e){}
					case "Server 1": try{s1=new Server(1,"127.0.0.1",6000,6001);statusLabel1.setText("Online");statusLabel1.setForeground(Color.GREEN);break;}catch(Exception e){}
					case "Server 2":try{s2=new Server(2,"127.0.0.1",6000,6002);statusLabel2 .setText("Online");statusLabel2 .setForeground(Color.GREEN);break;}catch(Exception e){}
					case "Server 3":try{s3=new Server(3,"127.0.0.1",6000,6003);statusLabel3.setText("Online");statusLabel3.setForeground(Color.GREEN);break;}catch(Exception e){}
					case "Server 4":try{s4=new Server(4,"127.0.0.1",6000,6004);statusLabel4.setText("Online");statusLabel4.setForeground(Color.GREEN);break;}catch(Exception e){}
				}
			}
		});
		btnNewButton.setBounds(164, 41, 117, 23);
		contentPane.add(btnNewButton);

		JButton btnNewButton_1 = new JButton("Terminate");
		btnNewButton_1.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				String s=(String)comboBox.getSelectedItem();
				switch(s){
					case "Master Server": try{master.terminate(); masterStatus.setText("Offline");masterStatus.setForeground(Color.RED);}catch(Exception e1){}
					case "Server 1": try{s1.terminate();statusLabel1.setText("Offline");statusLabel1.setForeground(Color.RED);break;}catch(Exception e1){}
					case "Server 2":try{s2.terminate();statusLabel2.setText("Offline");statusLabel2.setForeground(Color.RED);break;}catch(Exception e1){}
					case "Server 3":try{s3.terminate();statusLabel3.setText("Offline");statusLabel3.setForeground(Color.RED);break;}catch(Exception e1){}
					case "Server 4":try{s4.terminate();statusLabel4.setText("Offline");statusLabel4.setForeground(Color.RED);break;}catch(Exception e1){}
				}
			}
		});
		btnNewButton_1.setBounds(308, 41, 117, 23);
		contentPane.add(btnNewButton_1);
	}

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					AdminTool frame = new AdminTool();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
}
