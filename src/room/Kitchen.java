package room;

import anno.Direction;
import anno.Command;

public class Kitchen extends GeneralRoom{
	
	@Direction(command="north")
	private Bedroom north;

	@Direction(command = "east")
	private Outside east;
	
	private String s = "";
	
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
				s = "After eating 2 pancakes, you see a note on the pancake mix box.\nIt says 'The'.\n";
				super.add("Secret Note 2: The");
				if(state.is(GameState.word_found2) && state.is(GameState.word_found3) && state.is(GameState.word_found4)) {
					s += "You can now leave for school.\n";
				}
			}else if (answer < 2){
				s = "You eat too little and you die of hunger... The End.\n";
				state.set(GameState.dead);
			}else if (answer > 2){
				s = "You eat too much and you get diarrhea so you can't do anything but stay on the toilet... The End.\n";
				state.set(GameState.dead);
			}else if (answer < 0){
				s = "Your attempts to mess with the game backfire... your character is warped into another dimension... The End.\n";
				state.set(GameState.dead);
			}
		}else s = "You already ate...\n";
		return s;
	}
	
	@Command(doThis="lookKitchen")
	public String lookKitchen(){return "You see your extremely dirty kitchen.\n";}

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