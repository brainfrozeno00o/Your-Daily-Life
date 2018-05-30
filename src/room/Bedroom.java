package room;

import anno.Direction;
import anno.Command;

public class Bedroom extends GeneralRoom{

	@Direction(command="west")
	private Bathroom west;

	@Direction(command="north")
	private Outside north;

	@Direction(command="south")
	private Kitchen south;
	
	private String s = "";
	
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
			s = "You wear your clothes.\nYou see a note attached to your shirt that says 'Show'.\n";
			super.add("Secret Note 1: Show");
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
		if(state.is(GameState.greeted_dog)) s = "The dog ignores you.\n";
		else{
			if(state.is(GameState.brought_mouthwash)){
				state.set(GameState.greeted_dog);
				s = "You greet your dog. The dog bites the cap off the bottle of the mouthwash you brought from the bathroom.\nHalf of it spills all over the floor. The dog then runs away.\n";
				s += "You then can do other stuff.\n";
				super.remove("mouthwash");
				super.add("bitten mouthwash bottle");
			}else{
				s = "You greet your dog. He bites you and it turns out, he is a venomous dog.\nThe venom erases your will to go to school so you stay at home... The End.\n";
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
				if(super.contains("mouthwash") || super.contains("bitten mouthwash bottle")) return "You gargle 10 mL of mouthwash and then you spit it back there... what the heck.\n";
				else return "What mouthwash are you talking about?\n";
			case "notes":
				if(super.contains("Secret Note 1: Show") && super.contains("Secret Note 2: The") && super.contains("Secret Note 3: Love")){
					return "You form the phrase 'ShowTheLove'... when will you even use this?\n";
				}else return "You don't form anything out of it...\n";
			case "money":
				return "Oh... I wish I had money.\n";
			default:
				return "You don't need to use " + item.toLowerCase() + ".\n";
		}
	}	
}