package maze;

public class HelpCommand implements Command{
	private CommandCenter command;

	public HelpCommand(CommandCenter com){
		command = com;
	}

	public String execute() throws Exception{
		return command.help();
	}
}