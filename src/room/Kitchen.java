package room;

import java.lang.reflect.*;
import java.util.ArrayList;

import anno.Direction;
import anno.Command;

public class Kitchen{
	
	@Direction(command="north")
	private Bedroom north;

	@Direction(command = "east")
	private Outside east;
	
	private ArrayList<String> inventory;
	private String s = "";
	
	public void setInventory(ArrayList<String> invo){this.inventory = invo;}
	
	public ArrayList<String> getInventory(){return inventory;}
	
	public String getDescription(GameState state){
		s = "You go to the kitchen. You are hungry but you're not sure how many pancakes you want to eat.\n";
		s += "You can 'eatPancakes (number)' where number refers to how many you would like to eat.\n";
		s += "You can 'lookKitchen'.\n";
		if(state.is(GameState.word_found2) && state.is(GameState.word_found3) && state.is(GameState.word_found4)) {
			s += "You can now leave for school.\n";
		}
		return s;
	}
	
	@Command(doThis="eatPancakes") 
	public String eatPancakes(int answer, GameState state){
		if(!state.is(GameState.word_found4)){
			if(answer == 2) {
				state.set(GameState.word_found4);
				s = "After eating 2 pancakes, you see a note on the pancake mix box.\nIt says 'The'\n";
				inventory.add("Secret Note 2: The");
				if(state.is(GameState.word_found2) && state.is(GameState.word_found3) && state.is(GameState.word_found4)) {
					s += "You can now leave for school.\n";
				}
			}else if (answer < 2){
				s = "You eat too little and you die of hunger... The End\n";
				state.set(GameState.dead);
			}else if (answer > 3){
				s = "You eat too much and you get diarrhea so you can't do anything but stay on the toilet... The End\n";
				state.set(GameState.dead);
			}else if (answer < 0){
				s = "Your attempts to mess with the game backfire... your character is warped into another dimension... The End\n";
				state.set(GameState.dead);
			}
		}else s = "You already ate...\n";
		return s;
	}
	
	@Command(doThis="lookKitchen")
	public String lookKitchen(){return "You see your extremely dirty kitchen.\n";}

	@Command(doThis="take")
	public String take(String item){return "There is nothing here to take...;\n";}

	@Command(doThis="use")
	public String use(String item) {
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
		if(state.is(GameState.word_found4)) methods.remove("eatPancakes");
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