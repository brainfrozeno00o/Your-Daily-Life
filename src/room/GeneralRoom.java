package room;

import java.io.*;
import java.util.*;

public class GeneralRoom implements Serializable{
	private Inventory inventory = Inventory.getInstance();

	public void setInventory(ArrayList<String> invo){inventory.setInventory(invo);}
	
	public ArrayList<String> getInventory(){return inventory.getInventory();}

	public void add(String item){inventory.add(item);}

	public void remove(String item){inventory.remove(item);}

	public boolean contains(String item){return inventory.contains(item);}
}