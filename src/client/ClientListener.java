package client;

import java.util.ArrayList;

import protocol.Command;

public interface ClientListener {
	void refreshMemberlist(ArrayList<String> s);
	void onReceiveCommand(Command cmd);
	void terminate();
}

