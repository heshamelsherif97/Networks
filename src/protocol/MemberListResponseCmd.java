package protocol;

import java.util.ArrayList;
import java.util.HashMap;

@SuppressWarnings("serial")
//This command is sent to the client by the server, and contains the list of members currently connected to the server.
public class MemberListResponseCmd extends Command{
	private String clientName;
	private HashMap<Integer, ArrayList<String>> list;
	public MemberListResponseCmd(HashMap<Integer, ArrayList<String>> list) {
		super(CommandType.MEMBERLISTRESPONSE);
		this.list = list;
	}

	public MemberListResponseCmd(String name, HashMap<Integer, ArrayList<String>> list) {
		this(list);
		clientName = name;
	}

	public String getClientName() {
		return clientName;
	}

	public void setClientName(String name) {
		clientName = name;
	}

	public HashMap<Integer, ArrayList<String>> getMemberList() {
		return list;
	}
}
