package maze;

public class TypeCommand implements Strategy{
	public String message(){
		return "You have switched to input command mode. Input the command in the input text box.\n";
	}

	@Override
	public Object getOperation(String inputText, DailyLifeGUI gui) throws Exception{
		String okayString[];

		if(inputText.trim().isEmpty()) return null;
		else{
			String spaceChecker = new String(inputText);

			if(spaceChecker.matches(".*\\s+.*")) okayString = inputText.trim().split("\\s+");
			else okayString = new String[] {inputText};

			CommandCenter command = new CommandCenter(okayString, gui);
			Command doThis;

			switch(okayString[0].toLowerCase()){
				case "go":
					doThis = new MoveCommand(command);
					break;
				case "help":
					doThis = new HelpCommand(command);
					break;
				case "inventory":
					doThis = new InventoryCommand(command);
					break;
				case "load":
					doThis = new LoadCommand(command);
					break;
				case "quit":
					doThis = new QuitCommand(command);
					break;
				case "register":
					doThis = new RegisterCommand(command);
					break;
				case "reload":
					doThis = new ReloadCommand(command);
					break;
				case "run":
					doThis = new RunCommand(command);
					break;
				case "save":
					doThis = new SaveCommand(command);
					break;
				default:
					doThis = new ExecuteCommand(command);
					break;
			}

			return new CommandInvoker().executeSingle(doThis);
		}
	}

	public int getStratNumber(){return 0;}
}