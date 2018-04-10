package room;

import java.lang.reflect.*;
import java.util.ArrayList;

import anno.Direction;
import anno.Command;

public class Street {

	@Direction(command="south")
	private NextStation south;

	@Direction(command="north")
	private School north;

	private ArrayList<String> inventory;
	private String s = "";
	
	public void setInventory(ArrayList<String> invo){this.inventory = invo;}
	
	public ArrayList<String> getInventory(){return inventory;}

	public String getDescription(GameState state) {
		s = "You exit the station and arrive at the street that leads to your school.\n";
		s += "You suddenly see the strange person again. She is foaming at the mouth.\n";
		s += "You can check if you can 'use <item>' one of your items to help her.\n";
		s += "You can 'ignoreGirl' her because you've had enough of this as it's only been an hour since you woke up.\n";
		return s;
	}

	@Command(doThis="ignoreGirl")
	public String ignoreGirl(GameState state) {
		if(!state.is(GameState.ignored_foam)) {
			if(!state.is(GameState.brought_mouthwash)) {
				s = "The person keeps foaming at the mouth until you are enveloped by it and you die... The End\n";
				state.set(GameState.dead);
			}
			else {
				s = "The person crawls towards you and steals your mouthwash. She uses it and is healed.\n";
				s += "You suddenly forget what just happened. Oh well... school is up north.\n";
				state.set(GameState.ignored_foam);
				if(inventory.contains("mouthwash")) inventory.remove("mouthwash");
				else if (inventory.contains("bitten mouthwash bottle")) inventory.remove("bitten mouthwash bottle");
			}
		}
		else s = "Ignore what? There's nothing here but the road ahead. Not even other people...\n";
		return s;
	}

	@Command(doThis="take")
	public String take(String item, GameState state){
		if(item.equalsIgnoreCase("money")){
			if(!state.is(GameState.got_money)){
				s = "You take the money and wonder when you are going to use it.\n";
				inventory.add("money");
				state.set(GameState.got_money);
			}else s = "You wished you had more money... although there is no more money to be found.\n";
		}else s = "You can't take " + item.toLowerCase() + " from here.\n";
		return s;
	}

	@Command(doThis="use")
	public String use(String item, GameState state) {
		switch(item.toLowerCase()){
			case "mouthwash":
				if(!state.is(GameState.got_money)) {
					if (inventory.contains("mouthwash")) {
						s = "You bring out the mouthwash. She uses it and the foaming stops.\n";
						s += "She thanks you and disappears and then drops some money.\n";
						s += "You have the option to 'take <item>' the money or continue northward to school.\n";
						inventory.remove("mouthwash");
					}
					else{
						s = "You don't have mouthwash. It has been a recurring theme in this game already.\n";
						s += "The person keeps foaming at the mouth until you are enveloped by it and you die... The End\n";
						state.set(GameState.dead);
					}
				}
				else s = "You don't have the mouthwash anymore...\n";		
				break;
			case "notes":
				s = "You really think you can use this again...\n";
				break;
			case "money":
				if(inventory.contains("money")) s = "Well, finally you have it! Wonder when you are going to use it...\n";
				else s = "Oh... I wish I had money\n";;
				break;
			case "ID":
				if(inventory.contains("ID")) s = "You're going to need this for school.\n";
				else s = "You wonder how you are going to get in...\n";
				break;
			default:
				s = "You don't need to use " + item.toLowerCase() + "\n";
				break;
		}
		return s;
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
		Method[] getMethods = this.getClass().getDeclaredMethods();
		ArrayList<String> methods = new ArrayList<String>(getMethods.length);
		for(Method m: getMethods) methods.add(m.getName());
		methods.remove("setInventory");
		methods.remove("getInventory");
		methods.remove("getDescription");
		if(state.is(GameState.got_money)) methods.remove("ignoreGirl");
		if(state.is(GameState.ignored_foam)) methods.remove("ignoreGirl");
		s = "Commands you can try doing:\n";
		for(String doable : methods) {
			if(doable.equals("use") || doable.equals("take")) s += "You can '" + doable + " <item>'\n";
			else s += "You can '" + doable + "'\n";
		}
		s += "\n";
		for(Field f: this.getClass().getDeclaredFields()){
			if(f.isAnnotationPresent(Direction.class)){
				s += "You can 'go " + f.getAnnotation(Direction.class).command() + "' to go to " + f.getType().getName().replace("room.", "") + "\n";
			}
		}
		return s;
	}
}