package maze;

import java.util.*;

public class CommandInvoker{
	private ArrayList<Command> commands = new ArrayList<Command>();
	private ArrayList<String> responses = new ArrayList<String>();

	public String executeSingle(Command command) throws Exception{
		return command.execute();
	}

	public void addCommand(Command command){
		commands.add(command);
	}

	public ArrayList<String> executeMany() throws Exception{
		for(Command com : commands){
			responses.add(com.execute());
		}
		commands.clear();
		return responses;
	} 
}