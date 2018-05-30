package maze;

public class MoveCommand implements Command{
	private CommandCenter command;

	public MoveCommand(CommandCenter com){
		command = com;
	}

	public String execute() throws Exception{
		return command.move();
	}
}