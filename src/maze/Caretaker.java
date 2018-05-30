package maze;

import java.io.*;
import java.util.*;

public class Caretaker implements Serializable{

	private HashMap<String, Memento> sessions = new HashMap<String, Memento>();

	public Caretaker(){}
	
	public void save(String user, Memento session){sessions.put(user,session);}

	public boolean exists(String user) {if(sessions.containsKey(user)) return true; else return false;}

	public ArrayList<String> getUsers(){
		Set<String> userKeys = sessions.keySet();
		ArrayList<String> users = new ArrayList<String>(userKeys);
		return users;
	}

	public Memento load(String user){return sessions.get(user);}

	public int savedSessions(){return sessions.size();}
}