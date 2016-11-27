package server.end.worker;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import protocol.Command;
import server.end.Server;

public class ServerWorker extends Thread{
	private Server instance;
	private Socket mainServerSocket;
	private boolean RUNNING = false;
	private ObjectInputStream fromMasterServer;
	private ObjectOutputStream toMasterServer;
	public ServerWorker(Server _instance, Socket _mainServerSocket) throws IOException {
		instance = _instance;
		mainServerSocket = _mainServerSocket;
		toMasterServer = new ObjectOutputStream(mainServerSocket.getOutputStream());
		fromMasterServer = new ObjectInputStream(mainServerSocket.getInputStream());
		toMasterServer.writeInt(instance.getInstanceID());
		toMasterServer.flush();
		RUNNING = true;
		start();
	}

	public void run() {
		try {
			while(RUNNING) {
				Command cmd = (Command) fromMasterServer.readObject();
				instance.handleMasterServerCommand(cmd);
			}
		} catch (IOException | ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			terminate();
		}

	}

	public void send(Command cmd) {
		if (RUNNING && toMasterServer != null) {
			try {
				toMasterServer.writeObject(cmd);
				toMasterServer.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				terminate();
			}
		}
	}

	public void terminate() {
		System.out.println("Lost connection with Master Server; Terminating Server!");
		RUNNING = false;
		instance.terminate();
	}
}
