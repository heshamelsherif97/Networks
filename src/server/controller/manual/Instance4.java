package server.controller.manual;

import server.end.Server;

public class Instance4 extends Instance{
	private static int localPort = 6004;

	public static void main(String[] args) {
		new Server(4, Instance.MASTER_SERVER_HOST, Instance.MASTER_SERVER_PORT, localPort);
	}
}
