package server.end.worker;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import protocol.Command;
import server.end.Server;

//An instance of this class is created for each client that connects to the server; This instance handles all interaction between the server and the associated client
public class ClientWorker extends Thread{
	private String clientName;
	private Server server;
	private Socket clientSocket;
	private ObjectInputStream fromClient;
	private ObjectOutputStream toClient;
	private boolean RUNNING = false;
	private boolean accepted = false;

	public ClientWorker(Server server, Socket clientSocket) {
		this.server = server;
		this.clientSocket = clientSocket;
		RUNNING = true;
		start();
	}

	public void run() {
		try {
			fromClient = new ObjectInputStream(clientSocket.getInputStream());
			toClient = new ObjectOutputStream(clientSocket.getOutputStream());
			toClient.flush();
			while(clientSocket != null && !clientSocket.isClosed() && RUNNING) {
				//Read all input commands from the client
				Command cmd = (Command)fromClient.readObject();
				//Process the command accordingly
				server.handleClientCommand(this, cmd);
			}
		} catch (IOException e) {
			RUNNING = false;
			terminate();
		} catch (ClassNotFoundException e) {
			RUNNING = false;
			terminate();
			System.out.println("A client sent an unknown command object to the server!");
		}
	}

	//Send this worker's client a given command
	public boolean send(Command cmd) {
		if (toClient != null) {
			try {
				toClient.writeObject(cmd);
				toClient.flush();
				return true;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return false;
	}


	//Closes the appropriate sockets and streams, and removes this worker from the list of workers on the server
	public void terminate() {
		closeSocket();
		server.remove(this);
	}

	public void closeSocket() {
		try {
			if (clientSocket != null && !clientSocket.isClosed())
				clientSocket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String getClientName() {
		return clientName;
	}

	public void setClientName(String clientName) {
		this.clientName = clientName;
	}

	public boolean isAccepted() {
		return accepted;
	}

	public void setAccepted(boolean b) {
		accepted = b;
	}



}
