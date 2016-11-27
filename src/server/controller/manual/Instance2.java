package server.controller.manual;

import server.end.Server;

public class Instance2 extends Instance{
	private static int localPort = 6002;

	public static void main(String[] args) {
		new Server(2, Instance.MASTER_SERVER_HOST, Instance.MASTER_SERVER_PORT, localPort);
	}
}
