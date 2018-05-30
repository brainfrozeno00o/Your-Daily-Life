package room;

import anno.Direction;
import anno.Command;

public class School extends GeneralRoom{

	private String s = "";

	public String getDescription(GameState state) {
		s = "You finally get to school. An ID-scanning machine is at the gate.\n";
		s += "You can 'scanID' to get into school.\n";
		return s;
	}

	@Command(doThis="scanID")
	public String scanID(GameState state) {
		if (super.contains("ID")) {
			s = "You get into the school gates and see another duck.\n";
			s += "You can 'petDuck' again.\n";
			s += "You can head straight to class and 'studyComputerScience'.\n";
			state.set(GameState.scanned_ID);
		}
		else {
			s = "You don't see your ID in your bag or anywhere on your person. You can't get into school.\n";
			s += "There's nothing else you can do. You cut class and you get mad because you wasted so much time and effort today... The End.\n";
			state.set(GameState.dead);
		}
		return s;
	}

	@Command(doThis="petDuck")
	public String petDuck(GameState state){
		if(state.is(GameState.scanned_ID)){
			if (state.is(GameState.pet_duck)){
				s = "A huge guy suddenly appears. He is the duck guardian and he asks for compensation for petting the duck.\n";
				s += "You can 'use <item>' to pay him.\n";
				s += "You can try to 'distractGuardian' if you don't want to pay him.\n";
			}
			else {
				s = "You try to pet the duck but it disappears. You become sad and just go home... The End.\n";
				state.set(GameState.dead);
			}
		}else s = "You haven't scanned your ID... what are you trying to do?\n";
		return s;
	}

	@Command(doThis="distractGuardian")
	public String distractGuardian(GameState state) {
		if(state.is(GameState.pet_duck)) {
			s = "You try to distract him but you fail and he catches you. You are disappointed so you go home... The End.\n";
			state.set(GameState.dead);
		}
		else s = "Distract who? The other students? You don't want to embarass yourself.\n";
		return s;
	}

	@Command(doThis="studyComputerScience")
	public String studyComputerScience(GameState state) {
		if(state.is(GameState.scanned_ID)){
			if (state.is(GameState.pet_duck)) {
				s = "You rush to class but then it turns out, you forgot that there was no class today.\n";
				s += "You get pissed and go straight home. All you did went to nothing like so much effort in this world... The End.\n";
				state.set(GameState.dead);
			}
			else{
				s = "The duck glows and smites you with thunder because you ignored it. You are disintegrated... The End.\n";
				state.set(GameState.dead);
			}
		}else s = "You haven't scanned your ID... what are you trying to do?\n";
		return s;
	}

	@Command(doThis="take")
	public String take(String item){return "There is nothing here to take...\n";}

	@Command(doThis="use")
	public String use(String item, GameState state) {
		switch(item.toLowerCase()){
			case "notes":
				s = "Will these notes help you in finishing school...\n";
				break;
			case "money":
				if(super.contains("money")){
					s = "The duck guardian accepts your payment. You then proceed to pet the duck.\n";
					s += "The golden feather reappears and starts glowing. The duck is revealed to be the Zuck Duck. You are blessed eternally with good fortune.\n";
					s += "Congratulations! You have now finished whatever this is.\n";
					state.set(GameState.dead);				
				}else{
					s = "You don't have enough money to pay him. He doesn't let you pet the duck.\n";
					s += "You become sad and go home... The End\n";
					state.set(GameState.dead);					
				}
				break;
			default:
				s = "You don't need to use " + item.toLowerCase() + "\n";
				break;
		}
		return s;
	}
}