package server.end;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import protocol.*;
import protocol.Command.*;
import server.end.worker.ClientWorker;
import server.end.worker.ServerWorker;

public class Server extends Thread{
	private int instanceID;
	private ServerWorker masterServerWorker;
	private ServerSocket serverSocket;
	private Socket masterSocket;
	private boolean RUNNING = false;
	private ArrayList<ClientWorker> workers;
	public Server(int _instanceID, String masterServerHost, int masterServerPort, int localPort) {
		boolean connectedToMain = false;
		try {
			instanceID = _instanceID;
			masterSocket = new Socket();
			masterSocket.connect(new InetSocketAddress(masterServerHost, masterServerPort), 10000);
			masterServerWorker = new ServerWorker(this, masterSocket);
			connectedToMain = true;
			System.out.println("Connected to master server successfully!");
			serverSocket = new ServerSocket(localPort);
			workers = new ArrayList<ClientWorker>();
			System.out.println("Server #" + instanceID + " is started and is listening for connections on PORT " + localPort + "!");
			RUNNING = true;
			start();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			if (connectedToMain)
				System.out.println("Failed to create server socket to listen on! ");
			else
				System.out.println("Failed to connect to master server on "+masterServerHost+":"+masterServerPort);
			e.printStackTrace();
		}
	}

	public void run() {
		while(RUNNING && serverSocket != null && !serverSocket.isClosed()) {
			try {
				//Listen for incoming connections
				Socket clientSocket = serverSocket.accept();
				//Create a new worker for this client to service him, and add it to the list of workers
				System.out.println("A client initiated a new connection to the server!");
				add(new ClientWorker(this, clientSocket));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				RUNNING = false;
			}
		}
	}

	//Finds the target worker, and sends it the message to forward it to its client
	public void route(ClientWorker senderWorker, ChatCmd cmd) {
		//Traverse all workers to find destination
		boolean found = false;
		cmd.setSenderName(senderWorker.getClientName());
		for (ClientWorker worker : workers) {
			if (worker.isAccepted() && cmd.getDestinationName().equals(worker.getClientName())) {
				//Destination found, send it the message
				found = true;
				worker.send(cmd);
				System.out.println(senderWorker.getClientName() +" -> " + worker.getClientName() + ": " + cmd.getMessage());
			}
		}
		if (!found) {
			//Destination not found, send to the master server
			masterServerWorker.send(cmd);
		}

	}

	//Sends the client a response to their join request
	public void joinResponse(ClientWorker worker, String name, boolean accepted) {
		if (accepted) {
			//Set the client's id, name, and accept state.
			worker.setClientName(name);
			worker.setAccepted(true);
			//Create and send a successful response cmd to client
			JoinResponseCmd response = new JoinResponseCmd(name, true, instanceID);
			worker.send(response);
			//System.out.println(name + " has joined the server!");
			informClientsOnEvent(new EventCmd(name, true, instanceID));
		}
		else {
			//This client was rejected for having a name that was in use by another client
			worker.send(new JoinResponseCmd(false, -1));
			System.out.println("A client tried to join with a used name(" + name +") and was rejected!");
		}
	}

	//Creates a list of all the connected clients, and sends it to the client who requested the list
	public void memberListResponse(ClientWorker worker) {
		HashMap<Integer, ArrayList<String>> list = new HashMap<Integer, ArrayList<String>>();
		ArrayList<String> currentClients = new ArrayList<String>();
		for (ClientWorker currentWorker : workers) {
			if (currentWorker.isAccepted()) {
				currentClients.add(currentWorker.getClientName());
			}
		}
		list.put(instanceID, currentClients);
		MemberListResponseCmd memberList = new MemberListResponseCmd(list);
		worker.send(memberList);
		//System.out.println(worker.getClientName() + " has requested the member list!");

	}

	//Informs other clients when a client connects/disconnects
	public void informClientsOnEvent(EventCmd eventCmd) {
		for (ClientWorker currentWorker: workers) {
			if (currentWorker.getClientName() != null && currentWorker.isAccepted() && !eventCmd.getClientName().equals(currentWorker.getClientName()))
				currentWorker.send(eventCmd);
		}
	}
	//

	//Handles all commands sent by clients
	public void handleClientCommand(ClientWorker worker, Command cmd) {
		//The client has specified a valid name to be used and is accepted
		if (worker.isAccepted()) {
			if (cmd.getType() == CommandType.CHAT) {
				route(worker, (ChatCmd)cmd);
			}
			else if (cmd.getType() == CommandType.MEMBERLIST) {
				MemberListCmd mCmd = (MemberListCmd)cmd;
				if (mCmd.getInstanceID() == instanceID)
					memberListResponse(worker);
				else {
					mCmd.setClientName(worker.getClientName());
					masterServerWorker.send(mCmd);
				}
			}
		}
		//The client has not specific a valid name or this is the first command sent by the client ( always the latter )
		else if(cmd.getType() == CommandType.JOIN) {
			JoinCmd joinCmd = (JoinCmd)cmd;
			if (!isNameTaken(joinCmd.getName())) {
				worker.setClientName(joinCmd.getName());
				masterServerWorker.send(joinCmd);
			}
			else
				joinResponse(worker, joinCmd.getName(), false);
		}
		else {
			System.out.println("ERROR: Client sent an unacceptable command" + cmd.getType()); //This client's first command was not a join command.
		}
	}

	public void handleMasterServerCommand(Command cmd) {
		if (cmd.getType() == CommandType.JOINRESPONSE) {
			JoinResponseCmd joinCmd = (JoinResponseCmd)cmd;
			boolean found = false;
			for (ClientWorker worker: workers) {
				if (worker.getClientName().equals(joinCmd.getClientName())) {
					joinResponse(worker, joinCmd.getClientName(), joinCmd.isAccepted());
					found = true;
					break;
				}
			}
			if (!found) {
				EventCmd eventCmd = new EventCmd(joinCmd.getClientName(), false, instanceID);
				informClientsOnEvent(eventCmd);
				masterServerWorker.send(eventCmd);
			}
		}
		else if (cmd.getType() == CommandType.CHAT) {
			ChatCmd chatCmd = (ChatCmd)cmd;
			boolean found = false;
			for (ClientWorker worker : workers) {
				if (chatCmd.getDestinationName().equals(worker.getClientName())) {
					//Destination found, send it the message
					found = true;
					worker.send(cmd);
					//System.out.println(chatCmd.getSenderName() +" -> " + worker.getClientName() + ": " + chatCmd.getMessage());
					break;
				}
			}
			if (!found) {
				//Destination not found, send an error back to master server
				masterServerWorker.send(new NotFoundCmd(chatCmd.getSenderName(), chatCmd.getDestinationName(), instanceID));
			}
		}
		else if (cmd.getType() == CommandType.MEMBERLISTRESPONSE) {
			MemberListResponseCmd mCmd = (MemberListResponseCmd)cmd;
			for (ClientWorker worker: workers) {
				if (worker.isAccepted() && worker.getClientName().equals(mCmd.getClientName())) {
					worker.send(mCmd);
					break;
				}
			}
		}
		else if (cmd.getType() == CommandType.EVENT) {
			informClientsOnEvent((EventCmd)cmd);
		}
		else if (cmd.getType() == CommandType.ERROR) {
			NotFoundCmd nCmd = (NotFoundCmd)cmd;
			for (ClientWorker worker: workers) {
				if (worker.isAccepted() && worker.getClientName().equals(nCmd.getClientName())) {
					worker.send(nCmd);
					break;
				}
			}
		}
		else {
			System.out.println("ERROR: Master Server sent an illegal command " + cmd.getType());
		}
	}

	//Adds a worker to the server's list of workers
	public synchronized void add(ClientWorker worker) {
		workers.add(worker);
	}

	//Remove a worker from the server
	public synchronized void remove(ClientWorker worker) {
		workers.remove(worker);
		if (worker.getClientName() != null) {
			if (worker.isAccepted()) {
				EventCmd eventCmd = new EventCmd(worker.getClientName(), false, instanceID);
				masterServerWorker.send(eventCmd);
				//System.out.println(worker.getClientName() + " has left the server!");
				informClientsOnEvent(eventCmd);
			}
		}
	}

	//Check to see if a name is already taken by a client
	public synchronized boolean isNameTaken(String name) {
		for (ClientWorker worker : workers)
			if (worker.isAccepted() && worker.getClientName().equals(name))
				return true;
		return false;
	}

	public int getInstanceID() {
		return instanceID;
	}

	public void terminate() {
		System.out.println("Terminating Server #"+instanceID+"!");
		RUNNING = false;
		for (Iterator<ClientWorker> itr = workers.iterator(); itr.hasNext();) {
			ClientWorker worker = itr.next();
			worker.closeSocket();
			itr.remove();
		}
		try {
			serverSocket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
