package maze;

public class ExecuteCommand implements Command{
	private CommandCenter command;

	public ExecuteCommand(CommandCenter com){
		command = com;
	}

	public String execute() throws Exception{
		return command.doCommand();
	}
}