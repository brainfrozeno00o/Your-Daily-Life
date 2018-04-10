package room;

import java.lang.reflect.*;
import java.util.ArrayList;

import anno.Direction;
import anno.Command;

public class Bedroom{

	@Direction(command="west")
	private Bathroom west;

	@Direction(command="north")
	private Outside north;

	@Direction(command="south")
	private Kitchen south;
	
	private ArrayList<String> inventory;
	private String s = "";
	
	public void setInventory(ArrayList<String> invo){this.inventory = invo;}
	
	public ArrayList<String> getInventory(){return inventory;}
	
	public String getDescription(GameState state){
		s = "You finish your bathroom activity and head to your room to change clothes.\n";
		s += "Along the way, you see your dog. He hates you.\n";
		s += "You can 'greetDog' to try greeting your dog.\n";
		s += "You can 'returnRoom' and put on your school clothes.\n";
		if(state.is(GameState.word_found2) && state.is(GameState.word_found3) && state.is(GameState.word_found4)) {
			s += "You can now leave for school.\n";
		}
		return s;
	}
	
	@Command(doThis="returnRoom") 
	public String returnRoom(GameState state){
		if(!state.is(GameState.wore_clothes)){
			state.set(GameState.wore_clothes);
			if(!state.is(GameState.greeted_dog)) s = "You ignore the dog and go to your room's closet.\n You can 'wearClothes'.\n";
			else s = "You go to your room's closet.\n You can 'wearClothes'.\n";		
		}else{
			if(!state.is(GameState.greeted_dog))  s = "You go to your room and see nothing else to do aside from the dog who has invaded your room.\n";
			else s = "There is nothing else of interest in your room.\n";
		}
		return s;
	}
	
	@Command(doThis="wearClothes") 
	public String wearClothes(GameState state){
		if(state.is(GameState.wore_clothes)){
			state.set(GameState.word_found3);
			s = "You wear your clothes.\nYou see a note attached to your shirt that says 'Show'\n";
			inventory.add("Secret Note 1: Show");
			if(state.is(GameState.word_found2) && state.is(GameState.word_found3) && state.is(GameState.word_found4)) {
				s += "You can now leave for school.\n";
			}
		}else{
			if(state.is(GameState.word_found3)) s = "You already have clothes on.\n"; 
			else s = "You weirdly don't have any other clothes.\n";
		}
		return s;
	}
	
	@Command(doThis="greetDog") 
	public String greetDog(GameState state){
		if(state.is(GameState.greeted_dog)) s = "The dog ignores you\n";
		else{
			if(state.is(GameState.brought_mouthwash)){
				state.set(GameState.greeted_dog);
				s = "You greet your dog. The dog bites the cap off the bottle of the mouthwash you brought from the bathroom.\nHalf of it spills all over the floor. The dog then runs away.\n";
				s += "You then can do other stuff.\n";
				inventory.remove("mouthwash");
				inventory.add("bitten mouthwash bottle");
			}else{
				s = "You greet your dog. He bites you and it turns out, he is a venomous dog.\nThe venom erases your will to go to school so you stay at home... The End\n";
				state.set(GameState.dead);
			}			
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
		if(state.is(GameState.greeted_dog)) methods.remove("greetDog");
		if(state.is(GameState.word_found3)) methods.remove("wearClothes");
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