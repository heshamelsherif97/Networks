package protocol;

import java.io.Serializable;

@SuppressWarnings("serial")
//This class is the super class of all commands that are exchanged between the client and the server
public class Command implements Serializable{
	//The only types that commands can take are:
	public static enum CommandType{JOIN, MEMBERLISTRESPONSE, QUIT, CHAT, MEMBERLIST, TERMINATE, EVENT, ERROR, JOINRESPONSE };
	private CommandType type;

	public Command(CommandType t) {
		type = t;
	}

	public CommandType getType() {
		return type;
	}
}
