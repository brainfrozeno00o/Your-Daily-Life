package maze;

import java.io.*;

public class ReadFile implements Strategy{
	public String message(){
		return "You have switched to file read mode. Input the file name using this format '<filename>.txt'\n";
	}

	@Override
	public Object getOperation(String inputText, DailyLifeGUI gui) throws Exception{
		CommandInvoker invoke = new CommandInvoker();

		try(BufferedReader br = new BufferedReader(new FileReader(inputText))){
    		for(String line; (line = br.readLine()) != null;){
    			String[] okayString;
    			String spaceChecker = new String(line);

				if(spaceChecker.matches(".*\\s+.*")) okayString = line.trim().split("\\s+");
				else okayString = new String[] {line};

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

				invoke.addCommand(doThis);
    		} 
    		br.close();

    		return invoke.executeMany();
		}catch(FileNotFoundException e){
			e.printStackTrace();
			return null;
		}
	}
	public int getStratNumber(){return 1;}
}