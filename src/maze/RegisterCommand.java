package maze;

public class RegisterCommand implements Command{
	private CommandCenter command;

	public RegisterCommand(CommandCenter com){
		command = com;
	}

	public String execute() throws Exception{
		return command.register();
	}
}