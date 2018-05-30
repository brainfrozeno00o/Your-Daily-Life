package maze;

import room.GameState;

public class Interceptor{

	public static boolean canEnter(GameState state, Class<?> clazz){
		boolean ifPossible = false;
		if(clazz.toString().contains("Outside")){
			if(state.is(GameState.word_found2) && state.is(GameState.word_found3) && state.is(GameState.word_found4)) ifPossible = true;
			else ifPossible = false;			
		}
		if(clazz.toString().contains("Train")){
			if(state.is(GameState.bought_ticket)) ifPossible = true; else ifPossible = false;
		}
		if(clazz.toString().contains("TrainStation")){
			if(state.is(GameState.completed_note)) ifPossible = true; else ifPossible = false;
		}
		return ifPossible;
	}
	
	public static String enterMessage(){return "You can finally move on!\n";}
	
	public static String unableToEnterMessage(){return "You might have forgotten something...\n";}
	
}