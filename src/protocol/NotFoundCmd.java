package protocol;

@SuppressWarnings("serial")
public class NotFoundCmd extends Command{
	private int instanceID;
	private String clientName;
	private String destinationName;
	public NotFoundCmd(String _clientName, String destName, int instance) {
		super(CommandType.ERROR);
		clientName = _clientName;
		destinationName = destName;
		instanceID = instance;
	}

	public String getMessage() {
		return destinationName + " was not found on the network!";
	}

	public String getClientName() {
		return clientName;
	}

	public int getInstanceID() {
		return instanceID;
	}
}
