package client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import protocol.*;
import protocol.Command.CommandType;

public class InputThread extends Thread{
	private Client client;
	private ObjectInputStream fromServer;
	private boolean running = false;
	private ClientListener listener;

	public InputThread(Client _client, ClientListener _listener) {
		client = _client;
		running = true;
		listener = _listener;
		try {
			fromServer = new ObjectInputStream(client.getServerSocket().getInputStream());
			start();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public ClientListener getListener() {
		return listener;
	}

	public void setListener(ClientListener l) {
		this.listener = l;
	}

	public void run() {
		while(running) {
			try {
				Command cmd = (Command) fromServer.readObject();
			if (cmd.getType() == CommandType.JOINRESPONSE) {
					JoinResponseCmd response = (JoinResponseCmd)cmd;
					if (response.isAccepted()){
						client.setClientName(response.getClientName());
						client.setInstanceID(response.getInstanceID());
						listener.onReceiveCommand(cmd);
						//System.out.println("You've joined Server " + response.getInstanceID() + ", Welcome!");
					}
					else {
						//System.out.println("Name is already in use!");
						client.setInstanceID(response.getInstanceID());
						listener.onReceiveCommand(cmd);
					}
				}
				else if (cmd.getType() == CommandType.MEMBERLISTRESPONSE) {

					ArrayList<String> members = new ArrayList<String>();
					MemberListResponseCmd memberListCmd = (MemberListResponseCmd)cmd;
					HashMap<Integer, ArrayList<String>> memberList = memberListCmd.getMemberList();
					for (int instance: memberList.keySet()) {
						ArrayList<String> clients = memberList.get(instance);
						//System.out.println("Server #" + instance + " Members:");
						for (String client: clients){
							//System.out.println(client);
							members.add(client);
						}
						//System.out.println("------------");
					}
					listener.refreshMemberlist(members);
				}
				else if (cmd.getType() == CommandType.CHAT || cmd.getType() == CommandType.EVENT || cmd.getType() == CommandType.ERROR) {
					//System.out.println(((EventCmd)cmd).getMessage(client.getInstanceID()));
					listener.onReceiveCommand(cmd);
				}
			} catch (ClassNotFoundException | IOException e) {
				// TODO Auto-generated catch block
				terminate();
				if (client.isRunning())
					client.terminate();
			}
		}

	}

	public void terminate() {
		running = false;
	}

	public boolean isRunning() {
		return running;
	}

}
