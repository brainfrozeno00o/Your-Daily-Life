package room;

import java.lang.reflect.*;
import java.util.ArrayList;

import anno.Direction;
import anno.Command;
import anno.EnterCheck;

@EnterCheck
public class Outside{
		
	@Direction(command="south")
	private Bedroom south;

	@Direction(command="west")
	private Kitchen west;

	@Direction(command="east")
	private TrainStation east;
	
	private ArrayList<String> inventory;
	private String s = "";
	
	public void setInventory(ArrayList<String> invo){this.inventory = invo;}
	
	public ArrayList<String> getInventory(){return inventory;}

	public String getDescription(GameState state){
		s = "You get out of the house and head to the corner of the street you live on.\n";
		s += "As you turn left, you bump into someone.\n";
		s += "'What do the notes say?' She asks\n";
		s += "You can 'sayNotes (string)' where string is the formed phrase with multiple words (e.g. ILoveYou).\n";
		s += "You can 'runHome' to run away from this weird person.\n";
		s += "You can 'lookSky' to think about how to avoid this weird person.\n";
		return s;
	}
	
	@Command(doThis="runHome") 
	public String runHome(GameState state){
		if(state.is(GameState.brought_mouthwash)){
			s = "You get confused and give the mouthwash bottle to the girl. You suddenly wake up again and find yourself in an eternal state of suspended animation.\nTurns out, it was all a dream... The End\n";
			state.set(GameState.dead);
		}else s = "In a flash of wisdom, you resist. Wise men say only fools rush and you're not a fool.\n";
		return s;
	}
	
	@Command(doThis="sayNotes")
	public String sayNotes(String passcode, GameState state){
		if(passcode.equalsIgnoreCase("ShowTheLove")){
			if(state.is(GameState.greeted_dog)){
				s = "That is correct. The girl, however, notices your bitten bottle of mouthwash and you are suddenly sucked into another dimension... The End.\n";
				state.set(GameState.dead);
			}else {
				s = "That is correct. The girl apologizes for bumping into you without explaining the notes stuff and she walks away. ????\n";
				s += "You have now finished the first half of the game! :)\n";
				s += "You can now go to the train station east of here.\n";
				state.set(GameState.completed_note);
			}
		}else{
			s = "That is wrong. The girl shows you the love by obliterating you with her pet chicken's laser eye attack... The End\n";
			state.set(GameState.dead);
		}
		return s;
	}
	
	@Command(doThis="lookSky")
	public String lookSky(){return "You look up and ponder about it... but there is no way around this person.\n";}

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
		Method[] getMethods = this.getClass().getSuperclass().getDeclaredMethods();
		ArrayList<String> methods = new ArrayList<String>(getMethods.length);
		for(Method m: getMethods) methods.add(m.getName());
		methods.remove("setInventory");
		methods.remove("getInventory");
		methods.remove("getDescription");
		if(state.is(GameState.completed_note)) {
			methods.remove("sayNotes");
			methods.remove("lookSky");
			methods.remove("runHome");
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