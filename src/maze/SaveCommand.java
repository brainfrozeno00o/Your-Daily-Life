package maze;

public class SaveCommand implements Command{
	private CommandCenter command;

	public SaveCommand(CommandCenter com){
		command = com;
	}

	public String execute() throws Exception{
		return command.save();
	}
}