package maze;

public interface Strategy{
	public String message();
	public Object getOperation(String inputText, DailyLifeGUI gui) throws Exception;
	public int getStratNumber();
}