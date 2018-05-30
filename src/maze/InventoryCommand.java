package maze;

public class InventoryCommand implements Command{
	private CommandCenter command;

	public InventoryCommand(CommandCenter com){
		command = com;
	}

	public String execute() throws Exception{
		return command.inventory();
	}
}