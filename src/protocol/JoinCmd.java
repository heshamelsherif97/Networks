package protocol;

@SuppressWarnings("serial")
//This command is sent to the server by the client when the client wishes to join the server with the specific name.
public class JoinCmd extends Command{
	private String name;

	public JoinCmd(String name) {
		super(CommandType.JOIN);
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
