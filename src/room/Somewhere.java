package room;

import anno.Direction;
import anno.Command;

import java.util.ArrayList;
import java.lang.reflect.*;

public class Somewhere{
	
	@Direction(command = "north")
	private Bathroom north;

	private ArrayList<String> inventory;
	private String s = "";

	public void setInventory(ArrayList<String> invo){this.inventory = invo;}

	public ArrayList<String> getInventory(){return inventory;}

	public String getDescription(GameState state){
		s = "You wake up and you do not know where you are. However, you see a door in front of you.\n";
		s += "Enter 'go' + 'north/south/east/west' to go to different areas of the house.\n";
		if(state.is(GameState.word_found2) && state.is(GameState.word_found3) && state.is(GameState.word_found4)){
			s += "You can now leave for school.\n";
		}
		return s;
	}

	@Command(doThis="take")
	public String take(String item){return "There is nothing here to take...\n";}

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