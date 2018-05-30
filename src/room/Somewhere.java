package room;

import anno.Direction;
import anno.Command;

public class Somewhere extends GeneralRoom{
	
	@Direction(command = "north")
	private Bathroom north;

	private String s = "";

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