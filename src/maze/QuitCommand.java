package maze;

public class QuitCommand implements Command{
	private CommandCenter command;

	public QuitCommand(CommandCenter com){
		command = com;
	}

	public String execute() throws Exception{
		return command.quit();
	}
}