package room;

import anno.Direction;
import anno.Command;

import java.lang.reflect.*;
import java.util.ArrayList;

public class Bathroom{

	@Direction(command="east")
	private Bedroom east;

	@Direction(command="south")
	private Somewhere south;

	private ArrayList<String> inventory;
	private String s = "";

	public void setInventory(ArrayList<String> invo){this.inventory = invo;}
	
	public ArrayList<String> getInventory(){return inventory;}

	public String getDescription(GameState state){
		s = "The door leads to a bathroom. It is a standard setup with a toilet, sink, and a shower area.\n";
		s += "You see a toothbrush and toothpaste.\n";
		s += "You can command to 'brushTeeth' to brush your teeth.\n";
		s += "You can command to 'showerYourself'.\n";
		if(state.is(GameState.word_found2) && state.is(GameState.word_found3) && state.is(GameState.word_found4)) {
			s += "You can now leave for school.\n";
		}
		return s;
	}

	@Command(doThis="brushTeeth") 
	public String brushTeeth(GameState state){
		state.set(GameState.brushing_teeth);
		if(!state.is(GameState.brought_mouthwash)){
			s = "You brush your teeth and then find some mouthwash you can bring to school.\n";
			s += "You can 'take <item>' the mouthwash\n";
		}else{
			s = "You brush your teeth. Bright and shiny...\n";
		}
		return s;
	}

	@Command(doThis="showerYourself") 
	public String showerYourself(GameState state){
		if(!state.is(GameState.showered)){
			state.set(GameState.showered);
			s = "You find some conditioner in the shower. It looks like it will make your hair silky smooth.\n";
			s += "You can command 'conditionHair' to use the conditioner.\n";
		}else s = "You are done with your shower.\n";
		return s;
	}

	@Command(doThis="conditionHair")
	public String conditionHair(GameState state){
		if(!state.is(GameState.showered) || state.is(GameState.word_found2)){
			s = "You use conditioner without going into the shower first. Because of your stupidity, you decide to just stay at home in your dark room. The End\n";
			state.set(GameState.dead);
		}else{
			state.set(GameState.word_found2);
			inventory.add("Secret Note 3: Love");
			s = "You use the conditioner. Your hair is now silky smooth.\nYou see a note behind the conditioner bottle that says 'Love'.\n";
			if(state.is(GameState.word_found2) && state.is(GameState.word_found3) && state.is(GameState.word_found4)) s += "You can now leave for school.\n"; 
		}
		return s;
	}

	@Command(doThis="take")
	public String take(String item, GameState state) {
		if(item.equalsIgnoreCase("mouthwash")) {
			if(state.is(GameState.brushing_teeth)) {
				if(!state.is(GameState.brought_mouthwash)) {
					state.set(GameState.brought_mouthwash);
					s = "You take the mouthwash.\n"; 
					inventory.add("mouthwash");
				}
				else s = "Nothing else you can bring\n";
			}
			else {
				if(state.is(GameState.brought_mouthwash)) s = "You already have it dummy...\n";
				else s = "You want to bring it but... where is it?\n";
			}
		}
		else s = "You can't take " + item.toLowerCase() + " from here\n";
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
				if(inventory.contains("Secret Note 1: Show") && inventory.contains("Secret Note 2: The") && inventory.contains("Secret Note 3: Love")){
					return "You form the phrase 'ShowTheLove'... when will you even use this?\n";
				}
				if(!inventory.contains("Secret Note 1: Show") && inventory.contains("Secret Note 2: The") && !inventory.contains("Secret Note 3: Love")){
					return "You only have 'The'... the what?\n";
				}
				if(inventory.contains("Secret Note 1: Show") && !inventory.contains("Secret Note 2: The") && !inventory.contains("Secret Note 3: Love")){
					return "You only have 'Show'... show what?\n";
				}
				if(inventory.contains("Secret Note 1: Show") && inventory.contains("Secret Note 2: The") && !inventory.contains("Secret Note 3: Love")){
					return "You only have 'Show' and 'The'... show the what? show the money?\n";
				}
				if(!inventory.contains("Secret Note 1: Show") && !inventory.contains("Secret Note 2: The") && inventory.contains("Secret Note 3: Love")){
					return "You only have 'Love'... love who? love what? love when? Why even love?\n";
				}
				if(!inventory.contains("Secret Note 1: Show") && inventory.contains("Secret Note 2: The") && inventory.contains("Secret Note 3: Love")){
					return "You only have 'Love' and 'The'... love the what?\n";
				}
				if(inventory.contains("Secret Note 1: Show") && !inventory.contains("Secret Note 2: The") && inventory.contains("Secret Note 3: Love")){
					return "You only have 'Show' and 'Love'... show love? Why even?\n";
				}
				if(inventory.contains("Secret Note 1: Show") && inventory.contains("Secret Note 2: The") && inventory.contains("Secret Note 3: Love")){
					return "You don't form anything out of it...\n";
				}
			case "money":
				return "Oh... I wish I had money\n";
			default:
				return "You don't need to use " + item + "\n";
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
		Method[] getMethods = this.getClass().getDeclaredMethods();
		ArrayList<String> methods = new ArrayList<String>(getMethods.length);
		for(Method m: getMethods) methods.add(m.getName());
		methods.remove("setInventory");
		methods.remove("getInventory");
		methods.remove("getDescription");
		if(state.is(GameState.word_found2)) methods.remove("conditionHair");
		s = "Commands you can try:\n";
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