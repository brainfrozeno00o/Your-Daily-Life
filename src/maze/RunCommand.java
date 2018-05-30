package maze;

public class RunCommand implements Command{
	private CommandCenter command;

	public RunCommand(CommandCenter com){
		command = com;
	}

	public String execute() throws Exception{
		return command.run();
	}
}