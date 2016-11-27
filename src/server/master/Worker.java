package server.master;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

import protocol.Command;

public class Worker extends Thread{
	private MasterServer master;
	private Socket instance;
	private ObjectInputStream fromInstance;
	private ObjectOutputStream toInstance;
	private int instanceID;
	private boolean RUNNING = false;
	private ArrayList<String> clients;

	public Worker(MasterServer _master, Socket _instance) throws IOException {
		master = _master;
		instance = _instance;
		clients = new ArrayList<String>();
		RUNNING = true;
		toInstance = new ObjectOutputStream(instance.getOutputStream());
		toInstance.flush();
		fromInstance = new ObjectInputStream(instance.getInputStream());
		start();
	}

	public boolean send(Command cmd) {
		if (instance != null && !instance.isClosed() && toInstance != null)
			try {
				toInstance.writeObject(cmd);
				toInstance.flush();
				return true;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.out.println("ERROR: Failed to send message to Server #" + instanceID);
				terminate();
			}
		return false;
	}

	public void run() {
		try {
			 while(RUNNING) {
				if (instanceID == 0) {
					instanceID = fromInstance.readInt();
					System.out.println("Server #" + instanceID + " is connected!");
				}
				else {
					Command cmd = (Command)fromInstance.readObject();
					master.handleCommand(this, cmd);
				}
			}
		}
		catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			terminate();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			terminate();
		}
	}

	public ArrayList<String> getClients() {
		return clients;
	}

	public boolean hasClient(String name) {
		return clients.contains(name);
	}

	public int getInstanceID() {
		return instanceID;
	}

	public void addClient(String name) {
		clients.add(name);
	}

	public void removeClient(String name) {
		clients.remove(name);
	}

	public void terminate() {
		try {
			if (instance != null && !instance.isClosed())
				instance.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Terminating Server #" + instanceID);
		RUNNING = false;
	}
}
