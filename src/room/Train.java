package room;

import anno.Direction;
import anno.Command;
import anno.EnterCheck;

@EnterCheck
public class Train extends GeneralRoom{

	@Direction(command="east")
	private NextStation east;

	private String s = "";

	public String getDescription(GameState state) {
		state.set(GameState.on_train);
		s = "You board the train and find a vacant seat.\n";
		s += "However, beside that seat is the strange person you encountered earlier.\n";
		s += "You can just 'standTrain' and pretend to ignore the person.\n";
		s += "You can awkwardly 'sitSeat' to sit beside this person.\n";
		return s;
	}

	@Command(doThis="standTrain")
	public String standTrain() {
		if (!super.contains("ID")) {
			s = "You pretend to ignore this strange person and keep standing.\n";
			s += "She notices you, however, and asks you to show her the love. Maybe you need to 'use' something?\n";
		}
		else {
			s = "You come to your senses and realize that no one's there and you were just seeing things.\n";
		}
		return s;
	}

	@Command(doThis="sitSeat")
	public String sitSeat(GameState state) {
		if (!super.contains("ID")) {
			s = "You sit beside the strange person,\n";
			if (state.is(GameState.brought_mouthwash)) {
				s += "Your conscience tells you to 'use mouthwash' for some reason.\n";
			}
			else {
				s += "As you sit, you realize you don't have your school ID. You wallow in your stupidity and lose the will to go to school... The End.\n";
				state.set(GameState.dead);
			}
		}
		else {
			s = "You sit on that seat but there's no one beside you. Maybe you were seeing things.\n";
		}
		return s;
	}

	@Command(doThis="take")
	public String take(String item){return "There is nothing here to take...\n";}

	@Command(doThis="use")
	public String use(String item, GameState state) {
		switch(item.toLowerCase()){
			case "bitten mouthwash bottle:":
			case "mouthwash":
				if(super.contains("mouthwash")){
					if(!state.is(GameState.used_mouthwash)){
						s = "As you sit, the girl disappears. Your mouthwash comes out from your bag and turns into your ID...\n";
						s += "Your ID comes out of your mouthwash bottle. You actually forgot it but something weird happened there...\n";
						s += "At least you have it now. However, the train suddenly breaks down and you are forced to walk on the railroad which goes east to the next station.\n";
						super.add("ID");
						state.set(GameState.used_mouthwash);					
					}else s = "You don't want to gargle anymore...\n";						
				}
				else if(super.contains("bitten mouthwash bottle")){
					if(!state.is(GameState.used_mouthwash)){
						s = "You throw your bitten mouthwash bottle into the train's bin. Suddenly the train breaks down and you are forced to walk on the railroad which goes east to the next station.\n";
						super.remove("bitten mouthwash bottle");
						state.set(GameState.used_mouthwash);
					}					
				}
				else s = "What mouthwash are you talking about?\n";
				break;
			case "notes":
				s = "You show her the notes from earlier...\n";
				s += "She teleports you to the very first room of the game except you can't get out... The End.\n";
				state.set(GameState.dead);
				break;
			case "Single Journey Ticket":
			case "ticket":
				s = "You see it's a Single Journey Ticket. Ride or die, baby.\n";
				break;
			case "money":
				s = "Oh... I wish I had money.\n";
				break;
			default:
				s = "You don't need to use " + item.toLowerCase() + ".\n";
				break;
		}
		return s;
	}
}