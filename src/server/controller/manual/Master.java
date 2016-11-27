package server.controller.manual;

import server.master.MasterServer;

public class Master extends Instance{
	private static int localPort = 6000;

	public static void main(String[] args) {
		new MasterServer(localPort);
	}
}
