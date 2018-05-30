package maze;

public class CommandCenter{
	private String[] command;
	private DailyLifeGUI gui;

	public CommandCenter(String[] a, DailyLifeGUI b){
		command = a;
		gui = b;
	}

	public String doCommand() throws Exception{
		if(gui.getCurrState().stateNumber() == 0) return "What are you trying to do?\n";
		else return ((MazeMaker) gui.getCurrState()).execute(command);
	}

	public String move() throws Exception{
		return ((MazeMaker) gui.getCurrState()).move(command[1]);
	}

	public String help() throws Exception{
		return ((MazeMaker) gui.getCurrState()).help();
	}

	public String inventory() throws Exception{
		return ((MazeMaker) gui.getCurrState()).inventory();
	}

	public String load() throws Exception{
		return ((MazeMaker) gui.getCurrState()).load();
	}

	public String quit() throws Exception{
		return gui.getCurrState().quit();
	}

	public String register() throws Exception{
		gui.getCurrState().register(command);
		if(gui.getCurrState().stateNumber() == 0){
			gui.getCurrState().changeState(gui);
			gui.playable();	
			return gui.getCurrState().startIt();
		}else{
			return ((MazeMaker) gui.getCurrState()).load();
		}
	}

	public String reload() throws Exception{
		if(gui.getCurrState().stateNumber() == 0){
			return "Register a name first!\n";
		}else{
			gui.getCurrState().changeState(gui);
			return gui.getCurrState().startIt();
		}
	}

	public String run(){
		if(gui.getCurrStrat().getStratNumber() == 0) gui.setCurrStrat(new ReadFile());
		else gui.setCurrStrat(new TypeCommand());
		return gui.getCurrStrat().message();
	}

	public String save() throws Exception{
		return ((MazeMaker) gui.getCurrState()).save();
	}
}