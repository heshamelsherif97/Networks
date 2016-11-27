package protocol;

@SuppressWarnings("serial")
//This command is exchanged between the server and the client, in both directions.
//When a client receives this command, it displays the message and the sender name.
//When the server receives this command from a client, it forwards it to the destination client if available on the network.
public class ChatCmd extends Command{
	private int instanceID;
	private String senderName;
	private int destinationID;
	private String destinationName;
	private int TTL;
	private String message;

	public ChatCmd(String sendName, String destName, int TTL, String message) {
		super(CommandType.CHAT);
		senderName = sendName;
		destinationName = destName;
		this.TTL = TTL;
		this.message = message;
	}

	public ChatCmd(String sendName, String destName, int TTL, String message, int instance) {
		this(sendName, destName, TTL, message);
		instanceID = instance;
	}

	public String getSenderName() {
		return senderName;
	}

	public void setSenderName(String name) {
		senderName = name;
	}

	public String getDestinationName() {
		return destinationName;
	}

	public void setDestinationName(String name) {
		destinationName = name;
	}

	public int getDestinationID() {
		return destinationID;
	}

	public void setDestinationID(int destinationID) {
		this.destinationID = destinationID;
	}

	public int getTTL() {
		return TTL;
	}

	public void setTTL(int tTL) {
		TTL = tTL;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public void setInstanceID(int instance) {
		instanceID = instance;
	}

	public int getInstanceID() {
		return instanceID;
	}



}
