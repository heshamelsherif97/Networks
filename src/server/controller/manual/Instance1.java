package server.controller.manual;

import server.end.Server;

public class Instance1 extends Instance{
	private static int localPort = 6001;

	public static void main(String[] args) {
		new Server(1, Instance.MASTER_SERVER_HOST, Instance.MASTER_SERVER_PORT, localPort);
	}
}
