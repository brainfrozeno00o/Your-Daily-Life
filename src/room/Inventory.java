package room;

import java.util.*;
import java.io.*;

public class Inventory implements Serializable{
	private static Inventory invo = null;
	private static ArrayList<String> inventory = new ArrayList<String>();

	private Inventory(){}

	public static Inventory getInstance(){
		if(invo == null) invo = new Inventory();
		return invo;
	}

	public void add(String item){inventory.add(item);}

	public void remove(String item){inventory.remove(item);}

	public boolean contains(String item){if(inventory.contains(item)) return true; else return false;}

	public ArrayList<String> getInventory(){return inventory;}

	public void setInventory(ArrayList<String> setIt){
		inventory = new ArrayList<String>();
		inventory.addAll(setIt);
	}
}