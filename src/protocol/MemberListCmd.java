package protocol;

@SuppressWarnings("serial")
public class MemberListCmd extends Command{
	private int instanceID;
	private String clientName;
	public MemberListCmd(int instance) {
		super(CommandType.MEMBERLIST);
		instanceID = instance;
	}

	public int getInstanceID() {
		return instanceID;
	}

	public String getClientName() {
		return clientName;
	}

	public void setClientName(String name) {
		clientName = name;
	}
}
