package room;

import java.lang.reflect.*;
import java.util.ArrayList;

import anno.Direction;
import anno.Command;

public class NextStation {

	@Direction(command="north")
	private Street north;

	private ArrayList<String> inventory;
	private String s = "";
	private int counter = 0;
	
	public void setInventory(ArrayList<String> invo){this.inventory = invo;}
	
	public ArrayList<String> getInventory(){return inventory;}

	public String getDescription(GameState state) {
		++counter;
		if(counter == 1){
			inventory.remove("Single Journey Ticket");
			s = "You are now at the train station which is the one near school and you are exhausted.\n";			
		}else{
			s = "You weirdly find yourself in the same scenario you were a while ago...\n";
		}
		s += "You see a duck in front of you.\n";
		s += "You can 'petDuck' to pet the duck.\n";
		s += "You can 'shooDuck' to get that dumb duck out of the way.\n";
		s += "You can try to 'fixTrain' to do something good for once in your life.\n";
		return s;
	}

	@Command(doThis="petDuck")
	public String petDuck(GameState state) {
		if (!state.is(GameState.pet_duck)) {
			s = "You pet the duck and show it some love. A shiny golden feather appears from it and integrates into your soul. You feel blessed.\n";
			state.set(GameState.pet_duck);
		}
		else
			s = "You pet the duck and show it some more love. The duck is happy.\n";
		return s;
	}

	@Command(doThis="shooDuck")
	public String shooDuck(GameState state) {
		s = "You try to shoo away the duck.\n";
		s += "A whole group of them suddenly gangs up on you and your clothes are ruined.\n";
		s += "You stay on the ground and there's nothing you can do so you end up not going to school anymore... The End.\n";
		state.set(GameState.dead);
		return s;
	}

	@Command(doThis="fixTrain")
	public String fixTrain(GameState state) {
		s = "You try to help fix the train even though you know nothing about trains.\n";
		s += "The train starts working again once you accidentally touch some random part. The employees thank you and you make your way to school up north.\n";
		s += "School is northbound.\n";
		state.set(GameState.train_fixed);
		return s;
	}

	@Command(doThis="take")
	public String take(String item){return "There is nothing here to take...\n";}

	@Command(doThis="use")
	public String use(String item, GameState state) {
		switch(item.toLowerCase()){
			case "mouthwash":
				if(inventory.contains("mouthwash")) return "You don't want to gargle anymore...\n";
				else return "Well, you don't have one for some reason.\n";
			case "notes":
				return "You already used them... but will there come a time you will use them again?\n";
			case "money":
				return "Oh... I wish I had money\n";
			case "ID":
				if(inventory.contains("ID")) return "You look at your ID and you see your beautiful face.\n";
				else return "For some reason, you do not have your ID... you might be doomed.\n";
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
		Method[] getMethods = this.getClass().getDeclaredMethods();
		ArrayList<String> methods = new ArrayList<String>(getMethods.length);
		for(Method m: getMethods) methods.add(m.getName());
		methods.remove("setInventory");
		methods.remove("getInventory");
		methods.remove("getDescription");
		if(state.is(GameState.train_fixed)) methods.remove("fixTrain");
		if(state.is(GameState.pet_duck)) methods.remove("shooDuck");
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