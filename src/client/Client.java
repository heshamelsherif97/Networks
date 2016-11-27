package client;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

import protocol.*;
import protocol.Command.*;

public class Client extends Thread{
	private String HOST;
	private int PORT;
	private Socket serverSocket;
	private ObjectOutputStream toServer;
	private boolean RUNNING;
	private int instanceID;
	private InputThread inputThread;
	private String clientName;
	private ClientListener listener;

	public Client(ClientListener listener, String host, int port)  {
		RUNNING = true;
		HOST = host;
		PORT=port;
		this.listener = listener;
	}

	public void join(String name) throws IOException  {
		if(RUNNING && clientName == null & instanceID == 0) {
			//System.out.println("Trying to establish a connection to the " + HOST + ":" + PORT);
			serverSocket = new Socket();
			serverSocket.connect(new InetSocketAddress(HOST, PORT), 10000);
			toServer = new ObjectOutputStream(serverSocket.getOutputStream());
			inputThread = new InputThread(this, listener); //Handles input from server
			RUNNING = true;
			//System.out.println("Connection has been established to the server!");
			toServer.writeObject(new JoinCmd(name));
		}

	}

	//Requests the memberlist from the server
	public void getMemberList(int instance) throws IOException {
		if (RUNNING && clientName != null && !serverSocket.isClosed()) {
			toServer.writeObject(new MemberListCmd(instance));
			toServer.flush();
		}
	}

	//Sends a message to another client that is connected to the server, identifiable by the destination client's name
	public void chat(String senderName, String destinationName, int TTL, String msg) throws IOException {
		if (RUNNING && clientName != null && !serverSocket.isClosed()) {
			ChatCmd chat = new ChatCmd(clientName, destinationName, 2, msg);
			toServer.writeObject(chat);
			toServer.flush();
		}
	}

	//Sends a quit command to the server, and terminates the application
	public void quit() throws IOException {
		if (RUNNING && clientName != null && !serverSocket.isClosed()) {
			toServer.writeObject(new Command(CommandType.QUIT));
			toServer.flush();
			terminate();
		}
	}

	public void setListener(ClientListener l) {
		listener = l;
		inputThread.setListener(l);
	}

	//Closes the appropriate sockets and streams, then terminates the application
		public void terminate() {
			RUNNING = false;
			if (inputThread != null && inputThread.isRunning())
				inputThread.terminate();
			if (serverSocket != null && !serverSocket.isClosed()) {
				try {
					serverSocket.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				//System.out.println("Chat has been terminated!");
			}
			listener.terminate();
		}

	public Socket getServerSocket() {
		return serverSocket;
	}

	public int getInstanceID() {
		return instanceID;
	}
	public void setInstanceID (int id) {
		instanceID = id;
	}


	public boolean isRunning() {
		return RUNNING;
	}

	public void setClientName(String name) {
		clientName = name;
	}

	public String getClientName() {
		return clientName;
	}



}
