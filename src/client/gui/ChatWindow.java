package client.gui;

import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.AbstractButton;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import java.awt.Color;
import javax.swing.JButton;
import protocol.*;
import protocol.Command.CommandType;
import client.Client;
import client.ClientListener;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.awt.SystemColor;
import java.awt.Font;
import javax.swing.JTextPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

@SuppressWarnings("serial")
public class ChatWindow extends JFrame implements ClientListener  {

	private JPanel contentPane;
	private Client client = null;
	private ArrayList<JRadioButton> radioButtons = new ArrayList<JRadioButton>();
	private ButtonGroup buttonGrp = new ButtonGroup();
	private JPanel panel ;
	private JLabel lblFilterBy = new JLabel("Filter");
	private JComboBox<String> comboBox ;
	private JTextPane textPane;
	private JTextArea textArea;
	private DefaultStyledDocument messageDocument;
	private static final Style CHAT_MESSAGE = new StyleContext().addStyle("CHAT_MESSAGE", null);
	private static final Style DIRECTION_TO = new StyleContext().addStyle("DIRECTION_TO", null);
	private static final Style DIRECTION_FROM = new StyleContext().addStyle("DIRECTION_FROM", null);
	private static final Style MEMBER_JOIN = new StyleContext().addStyle("MEMBER_JOIN", null);
	private static final Style MEMBER_QUIT = new StyleContext().addStyle("MEMBER_QUIT", null);
	private static final Style ERROR = new StyleContext().addStyle("ERROR", null);


	public ChatWindow(Client c) {
		client = c;
		client.setListener(this);
		try {
			UIManager.setLookAndFeel(
			        UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e2) {

		}
		setTitle("Client");
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 703, 474);
		contentPane = new JPanel();
		contentPane.setForeground(new Color(135, 206, 250));
		contentPane.setBackground(new Color(135, 206, 235));
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JLabel lblNewLabel = new JLabel("Connected to Server #"+ c.getInstanceID() + " as: " + c.getClientName());
		lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 12));
		lblNewLabel.setBounds(10, 13, 668, 16);
		contentPane.add(lblNewLabel);

		comboBox = new JComboBox<String>();
		JLabel lblMembersOnline = new JLabel("Members Online");
		lblMembersOnline.setFont(new Font("Tahoma", Font.BOLD, 12));
		lblMembersOnline.setBounds(10, 26, 106, 42);
		contentPane.add(lblMembersOnline);
		lblFilterBy.setFont(new Font("Tahoma", Font.BOLD, 12));

		lblFilterBy.setBounds(118, 40, 46, 14);
		contentPane.add(lblFilterBy);
		comboBox.setBounds(155, 38, 83, 20);
		contentPane.add(comboBox);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(233, 63, 444, 273);
		contentPane.add(scrollPane);
		messageDocument = new DefaultStyledDocument();

		//Set text colors
		StyleConstants.setForeground(CHAT_MESSAGE, Color.BLACK);
		StyleConstants.setForeground(DIRECTION_TO, new Color(0, 150, 0));
		StyleConstants.setForeground(DIRECTION_FROM, Color.BLUE);
		StyleConstants.setForeground(MEMBER_JOIN, new Color(0, 200, 0));
		StyleConstants.setItalic(MEMBER_JOIN, true);
		StyleConstants.setForeground(MEMBER_QUIT, Color.RED);
		StyleConstants.setItalic(MEMBER_QUIT, true);
		StyleConstants.setForeground(ERROR, Color.RED);
		StyleConstants.setItalic(ERROR, true);

		textPane = new JTextPane(messageDocument);
		textPane.setFont(new Font("Tahoma", Font.BOLD, 11));
		textPane.setEditable(false);
		scrollPane.setViewportView(textPane);

		panel = new JPanel();
		panel.setForeground(new Color(135, 206, 235));
		panel.setBackground(new Color(135, 206, 235));
		//panel.setBounds(10, 120, 126, 264);
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.setOpaque(true);

		textArea = new JTextArea();
		textArea.setFont(textArea.getFont().deriveFont(13f));
		JScrollPane scrollPane_1 = new JScrollPane(textArea);
		scrollPane_1.setBounds(233, 347, 318, 65);
		contentPane.add(scrollPane_1);

		JScrollPane scrollPane_2 = new JScrollPane(panel);
		scrollPane_2.setBounds(10, 111, 163, 254);
		scrollPane_2.setBackground(SystemColor.activeCaptionBorder);
		scrollPane_2.setOpaque(true);
		contentPane.add(scrollPane_2);

		JLabel lblChooseAMember = new JLabel("Choose a member to chat with");
		lblChooseAMember.setFont(new Font("Tahoma", Font.BOLD, 12));
		lblChooseAMember.setBounds(10, 79, 216, 14);
		contentPane.add(lblChooseAMember);

		comboBox.addItem("All");
		comboBox.addItem("Server 1");
		comboBox.addItem("Server 2");
		comboBox.addItem("Server 3");
		comboBox.addItem("Server 4");

		comboBox.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				requestMemberList();
			}
	    });
		requestMemberList();

		JButton btnNewButton = new JButton("Send");
		btnNewButton.setBounds(561, 376, 117, 36);
		contentPane.add(btnNewButton);
		btnNewButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				if(buttonGrp.getSelection()==null){
					JOptionPane.showMessageDialog(null,"You didn't specify a member","Error", JOptionPane.ERROR_MESSAGE);
				}
				else{
					String enteredText=textArea.getText();
					String destination=buttonGrp.getSelection().getActionCommand();
					if(!(enteredText.equals(""))){
						enteredText = enteredText.trim() +  "\r\n";
						try {
							c.chat(c.getClientName(), destination, 2, enteredText);
							textArea.setText("");
							messageDocument.insertString(messageDocument.getLength(), "TO " + destination + ": ", DIRECTION_TO);
							messageDocument.insertString(messageDocument.getLength(), enteredText, CHAT_MESSAGE);
						} catch (IOException e) {} catch (BadLocationException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}
		});

		JButton btnQuit = new JButton("Quit");
		btnQuit.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				try {
					c.quit();
					dispose();
					EventQueue.invokeLater(new Runnable() {
						public void run() {
							try {
								InitialWindow frame2 = new InitialWindow();
								frame2.setVisible(true);
							} catch (Exception e) {

							}
						}
					});
				} catch (IOException e) {

				}
			}
		});
		btnQuit.setBounds(10, 401, 89, 23);
		contentPane.add(btnQuit);

	}

	public void requestMemberList(){
		if(comboBox.getSelectedItem().equals("All")){
			try {
				client.getMemberList(0);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else if(comboBox.getSelectedItem().equals("Server 1")){
			try {
				client.getMemberList(1);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else if(comboBox.getSelectedItem().equals("Server 2")){
			try {
				client.getMemberList(2);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else if(comboBox.getSelectedItem().equals("Server 3")){
			try {
				client.getMemberList(3);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else if(comboBox.getSelectedItem().equals("Server 4")){
			try {
				client.getMemberList(4);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}


	@Override
	public void refreshMemberlist(ArrayList<String> s) {
		panel.removeAll();
		radioButtons=new ArrayList<JRadioButton>();
		buttonGrp=new ButtonGroup();
		for (int i = 0; i <s.size(); i++) {
			if(!(s.get(i).equals(client.getClientName()))){
				JRadioButton j=new JRadioButton(s.get(i));
				j.setBackground(null);
				j.setActionCommand(s.get(i));
				radioButtons.add(j);
				buttonGrp.add(j);
				panel.add(j);
			}
		}
		panel.revalidate();
		panel.repaint();
		validate();
		repaint();
	}


	@Override
	public void onReceiveCommand(Command cmd) {
		try {
			String newLine = "\r\n";
			int length = messageDocument.getLength();
			if (cmd.getType() == CommandType.CHAT) {
				ChatCmd chatCmd = (ChatCmd)cmd;
				String sender = chatCmd.getSenderName();
				messageDocument.insertString(length, "FROM " + sender + ": ", DIRECTION_FROM);
				messageDocument.insertString(messageDocument.getLength(), chatCmd.getMessage().trim() + newLine, CHAT_MESSAGE);
			}
			else if (cmd.getType() == CommandType.EVENT) {
				EventCmd eventCmd = (EventCmd)cmd;
				messageDocument.insertString(length, eventCmd.getMessage(client.getInstanceID()) + newLine, (eventCmd.isJoin())?MEMBER_JOIN:MEMBER_QUIT);
				if (buttonGrp != null) {
					Enumeration<AbstractButton> elements = buttonGrp.getElements();
					while(elements.hasMoreElements()) {
						JRadioButton bttn = (JRadioButton)elements.nextElement();
						if (bttn.getText().equals(eventCmd.getClientName())) {
							radioButtons.remove(bttn);
							buttonGrp.remove(bttn);
							panel.remove(bttn);
							panel.revalidate();
							panel.repaint();
							validate();
							repaint();
						}
					}
				}
				requestMemberList();
			}
		}catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public void terminate() {
		if (client.getClientName() != null && client.getInstanceID() != 0) {
			JOptionPane.showMessageDialog(null,"Connection to the server has timed out!","Error", JOptionPane.ERROR_MESSAGE);
			dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
			dispose();
		}
	}
}
