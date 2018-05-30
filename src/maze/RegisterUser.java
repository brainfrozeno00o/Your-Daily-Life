package maze;

import java.io.*;	
import java.util.*;

public class RegisterUser implements State{

	private String s, user = "";
	private boolean fromMaze = false;

	public RegisterUser(boolean checked){fromMaze = checked;}

	@SuppressWarnings("unchecked")
	public String startIt() throws Exception{
		if(!fromMaze){
			s = "Welcome to the game of Your Daily Life! Please register or load a user by typing 'register <name>'.\n";

			try{
				FileInputStream file = new FileInputStream("users.ser");
				ObjectInputStream in = new ObjectInputStream(file);
				ArrayList<String> checkNames = (ArrayList<String>) in.readObject();
				in.close();

				s += "\nRegistered users: \n";
				for(String user : checkNames) s += "- " + user + "\n";
			}catch(Exception e){s += "\nNo registered users so far...\n";}
		}else s = "wat r u doing dumb bird u destroyed da game.\n";

		return s;
	}

	public int stateNumber(){return 0;}

	public void changeState(DailyLifeGUI gui){gui.setCurrState(new MazeMaker(user));}

	public void register(String[] getUsername){
		if(getUsername.length > 2){
			for(int i = 1; i < getUsername.length; i++) {
				if(i != getUsername.length - 1) user += getUsername[i] + " ";
				else user += getUsername[i];
			}
		}else user = getUsername[1];
	}

	public String quit() throws Exception{return "Thanks for playing!\n";}
}