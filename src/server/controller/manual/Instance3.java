package server.controller.manual;

import server.end.Server;

public class Instance3 extends Instance{
	private static int localPort = 6003;

	public static void main(String[] args) {
		new Server(3, Instance.MASTER_SERVER_HOST, Instance.MASTER_SERVER_PORT, localPort);
	}
}
