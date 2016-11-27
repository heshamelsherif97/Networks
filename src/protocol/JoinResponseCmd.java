package protocol;

@SuppressWarnings("serial")
//This command is sent to the client by the server, indicating the status of the join request that the server previously received from this client
public class JoinResponseCmd extends Command{
	private boolean accepted;
	private int instanceID;
	private String clientName;
	public JoinResponseCmd(boolean response, int id) {
		super(CommandType.JOINRESPONSE);
		accepted = response;
		instanceID = id;
	}

	public JoinResponseCmd(String name, boolean response, int id) {
		super(CommandType.JOINRESPONSE);
		accepted = response;
		instanceID = id;
		clientName = name;
	}

	public String getClientName() {
		return clientName;
	}

	public void setClientName(String name) {
		clientName = name;
	}

	public int getInstanceID() {
		return instanceID;
	}

	public boolean isAccepted() {
		return accepted;
	}
}
