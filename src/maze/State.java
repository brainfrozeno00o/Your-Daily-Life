package maze;

public interface State{
	public String startIt() throws Exception;
	public void register(String[] username);
	public void changeState(DailyLifeGUI gui);
	public int stateNumber();
	public String quit() throws Exception;
}