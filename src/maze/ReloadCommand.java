package maze;

public class ReloadCommand implements Command{
	private CommandCenter command;

	public ReloadCommand(CommandCenter com){
		command = com;
	}

	public String execute() throws Exception{
		return command.reload();
	}
}