package server.master;

import java.io.IOException;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

import protocol.*;
import protocol.Command.CommandType;

public class MasterServer extends Thread{
	private boolean RUNNING = true;
	private ServerSocket serverSocket;
	private ArrayList<Worker> instances;
	public MasterServer(int localPort) {
		try {
			serverSocket = new ServerSocket(localPort);
			RUNNING = true;
			instances = new ArrayList<Worker>();
			start();
			System.out.println("MASTER SERVER started and is listening for connections on PORT " + localPort + "!");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void handleCommand(Worker instance, Command cmd) {
		if (cmd.getType() == CommandType.JOIN) {
			JoinCmd joinCmd = (JoinCmd)cmd;
			if (isNameTaken(joinCmd.getName())) {
				instance.send(new JoinResponseCmd(joinCmd.getName(), false, instance.getInstanceID()));
			}
			else {
				instance.send(new JoinResponseCmd(joinCmd.getName(), true, instance.getInstanceID()));
				instance.addClient(joinCmd.getName());
				//System.out.println(joinCmd.getName() + " has joined Server #" + instance.getInstanceID());
				EventCmd eventCmd = new EventCmd(joinCmd.getName(), true, instance.getInstanceID());
				for (Worker currentInstance: instances) {
					if (currentInstance.getInstanceID() != instance.getInstanceID())
						currentInstance.send(eventCmd);
				}
			}

		}
		else if (cmd.getType() == CommandType.EVENT) {
			for (Worker currentInstance: instances) {
				if (currentInstance.getInstanceID() != instance.getInstanceID())
					currentInstance.send(cmd);
			}
			EventCmd eventCmd = (EventCmd)cmd;
			if (!eventCmd.isJoin()) {
				instance.removeClient(eventCmd.getClientName());
				//System.out.println(eventCmd.getClientName() + " has left Server #" + eventCmd.getInstanceID());
			}
		}
		else if(cmd.getType() == CommandType.CHAT) {
			ChatCmd chatCmd = (ChatCmd)cmd;
			boolean found = false;
			for (Worker currentInstance: instances) {
				if (currentInstance.getInstanceID() != instance.getInstanceID() && currentInstance.hasClient(chatCmd.getDestinationName())) {
					found = currentInstance.send(chatCmd);
					if (found)
						//System.out.println(chatCmd.getSenderName() + "(Server #" + instance.getInstanceID() + ") - > " + chatCmd.getDestinationName() + "(Server #" + currentInstance.getInstanceID() + "): "+chatCmd.getMessage());
					break;
				}
			}
			if (!found) {
				instance.send(new NotFoundCmd(chatCmd.getSenderName(), chatCmd.getDestinationName(), instance.getInstanceID()));
				System.out.println("ERROR:" + chatCmd.getSenderName() + " tried to send a message to a non existing Client(" + chatCmd.getDestinationName() + ")!");
			}
		}
		else if (cmd.getType() == CommandType.MEMBERLIST) {
			MemberListCmd mCmd = (MemberListCmd)cmd;
			HashMap<Integer, ArrayList<String>> list = new HashMap<Integer, ArrayList<String>>();
			for (Worker currentInstance: instances) {
				if (mCmd.getInstanceID() == 0 || mCmd.getInstanceID() == currentInstance.getInstanceID()) {
					ArrayList<String> currentClients = new ArrayList<String>();
					currentClients.addAll(currentInstance.getClients());
					list.put(currentInstance.getInstanceID(), currentClients);
				}
			}
			//System.out.println("Sending memberlist");
			instance.send(new MemberListResponseCmd(mCmd.getClientName(), list));
		}
		else {
			System.out.println("ERROR: Server #" + instance.getInstanceID() + " sent an illegal command!");
		}
	}

	public synchronized boolean isNameTaken(String name) {
		for (Worker currentInstance: instances) {
			if (currentInstance.getClients().contains(name)) {
				return true;
			}
		}
		return false;
	}

	public void run() {
		while(RUNNING) {
			try {
				Socket instanceSocket = serverSocket.accept();
				System.out.println("Received a server connection!");

				instances.add(new Worker(this, instanceSocket));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				terminate();
			}

		}
	}

	public void terminate() {
		System.out.println("Shutting down master server!");
		RUNNING = false;
		try {
			if (serverSocket != null && !serverSocket.isClosed())
				serverSocket.close();
		}
		catch(IOException e){}
	}
}
