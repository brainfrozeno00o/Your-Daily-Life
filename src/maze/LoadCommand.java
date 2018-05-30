package maze;

public class LoadCommand implements Command{
	private CommandCenter command;

	public LoadCommand(CommandCenter com){
		command = com;
	}

	public String execute() throws Exception{
		return command.load();
	}
}