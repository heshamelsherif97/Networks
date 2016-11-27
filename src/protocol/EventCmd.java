package protocol;

@SuppressWarnings("serial")
//This command is sent by the server to a client, and the message within is displayed. Used for general info messages, such as Join/Leave messages
public class EventCmd extends Command{
	private String clientName;
	private int instanceID;
	private boolean join;
	public EventCmd(String name, boolean _joined, int instance) {
		super(CommandType.EVENT);
		clientName = name;
		instanceID = instance;
		join = _joined;
	}

	public String getClientName() {
		return clientName;
	}

	public int getInstanceID() {
		return instanceID;
	}

	public boolean isJoin() {
		return join;
	}

	public String getMessage(int instance) {
		if (join)
			return (instance == instanceID)?(clientName + " has joined this server!"):(clientName + " has joined Server #" + instanceID + "!");
		else
			return clientName + " has left Server #" + instanceID + "!";
	}
}
