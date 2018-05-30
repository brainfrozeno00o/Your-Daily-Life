package maze;

import java.util.HashMap;

public class Originator {
	
	private Object currentRoom = null;
	private int state = 0;
	private HashMap<Class<?>, Object> roomMap = new HashMap<Class<?>, Object>();
	private String previousString = "";
	private String[] inventory;
	
	public void setState(Object a, int b, HashMap<Class<?>, Object> c, String d, String[] e){
		this.currentRoom = a;
		this.state = b;
		this.roomMap = c;
		this.previousString = d;
		this.inventory = e;
	}
	
	public Object getCurrentRoom(){return currentRoom;}

	public int getState(){return state;}

	public HashMap<Class<?>, Object> getRoomMap(){return roomMap;}

	public String[] getInventory(){return inventory;}

	public String getLastMessage(){return previousString;}
	
	public Memento saveToMemento(){return new Memento(currentRoom, state, roomMap, previousString, inventory);}
	
	public void getStateFromMemento(Memento memento){
		currentRoom = memento.getCurrentRoom();
		state = memento.getState();
		roomMap = memento.getRoomMap();
		previousString = memento.getLastMessage();
		inventory = memento.getInventory();
	}
}
