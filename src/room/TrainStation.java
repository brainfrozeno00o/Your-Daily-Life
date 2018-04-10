package room;

import java.lang.reflect.*;
import java.util.ArrayList;

import anno.Direction;
import anno.Command;
import anno.EnterCheck;

@EnterCheck
public class TrainStation {

	@Direction(command="south")
	private Train south;

	private ArrayList<String> inventory;
	private String s = "";
	
	public void setInventory(ArrayList<String> invo){this.inventory = invo;}

	public ArrayList<String> getInventory(){return inventory;}

	public String getDescription(GameState state) {
		s = "You arrive at the train station.\n";
		s += "You see two ways of getting a ticket.\n";
		s += "You can either 'useMachine' and get an ez ticket or 'waitCounter' with a long line.\n";
		return s;
	}

	@Command(doThis="useMachine")
	public String useMachine(GameState state) {
		if(state.is(GameState.bought_ticket)) s = "You can already board the train.\n";
		else {
			s = "The ticket machine provides the ticket after you pay for it.\n";
			s += "You can 'take <item>' the ticket.\n";
		}
		return s;
	}

	@Command(doThis="waitCounter")
	public String waitCounter(GameState state) {
		if(state.is(GameState.bought_ticket)) s = "You can already board the train.\n";
		else {
			s = "You line up at the counter for some reason and it takes you 3 hours to get a ticket.\n";
			s += "You get mad at yourself for being stupid and you just go home after buying your ticket... The End\n";
			state.set(GameState.dead);
		}
		return s;
	}

	@Command(doThis="take")
	public String take(String item, GameState state) {
		if(item.equalsIgnoreCase("ticket")) {
			if(!state.is(GameState.bought_ticket)) {
				state.set(GameState.bought_ticket);
				s = "You take the train ticket. The train is to your south.\n";
				s += "You can finally then 'useMachine'\n";
				inventory.add("Single Journey Ticket");
			}
			else s = "You have to pay for another one but you don't want to do that.\n";
		}
		else s = "You can't take " + item.toLowerCase() + " from here.\n";
		return s;
	}

	@Command(doThis="use")
	public String use(String item, GameState state) {
		switch(item.toLowerCase()){
			case "bitten mouthwash bottle":
			case "mouthwash":
				if(inventory.contains("mouthwash") || inventory.contains("bitten mouthwash bottle")) return "You gargle 10 mL of mouthwash and then you spit it back there... what the heck\n";
				else return "What mouthwash are you talking about?\n";
			case "notes":
				return "You already used them... but will there come a time you will use them again?\n";
			case "Single Journey Ticket":
			case "ticket":
				if(inventory.contains("Single Journey Ticket")) {
					return "You now finally board the train to school.\n";
				}
				else return "What ticket?... probably do 'take ticket' to get one I guess.\n";
			case "money":
				return "Oh... I wish I had money.\n";
			default:
				return "You don't need to use " + item.toLowerCase() + "\n";
		}
	}

	@Command(doThis="inventory")
	public String inventory(){
		if(inventory.isEmpty()) s = "You currently have nothing in your inventory.\n";
		else{
			s = "You currently have the following in your inventory: \n";
			for(String item : inventory) s += "- " + item + "\n";
		}
		return s;
	}

	@Command(doThis="help")
	public String help(GameState state){
		Method[] getMethods = this.getClass().getSuperclass().getDeclaredMethods();
		ArrayList<String> methods = new ArrayList<String>(getMethods.length);
		for(Method m: getMethods) methods.add(m.getName());
		methods.remove("setInventory");
		methods.remove("getInventory");
		methods.remove("getDescription");
		if(state.is(GameState.bought_ticket)) {
			methods.remove("waitCounter");
			methods.remove("useMachine");
		}
		s = "Commands you can try doing:\n";
		for(String doable : methods) {
			if(doable.equals("use") || doable.equals("take")) s += "You can '" + doable + " <item>'\n";
			else s += "You can '" + doable + "'\n";
		}
		s += "\n";
		for(Field f: this.getClass().getSuperclass().getDeclaredFields()){
			if(f.isAnnotationPresent(Direction.class)){
				s += "You can 'go " + f.getAnnotation(Direction.class).command() + "' to go to " + f.getType().getName().replace("room.", "") + "\n";
			}
		}
		return s;
	}	
}