package maze;

import room.GameState;
import anno.Direction;

import java.util.*;
import java.lang.reflect.*;

public class Helper{
	private String s;
	private Object currentRoom;
	private GameState gs;
	private ArrayList<String> commands = new ArrayList<String>();
	private HashMap<String, String> locations = new HashMap<String, String>();

	public Helper(Object cr, GameState st){
		currentRoom = cr;
		gs = st;
	}

	public void checkRoom(ArrayList<String> methods){
		String roomName;

		if(currentRoom instanceof EnterCondition) roomName = currentRoom.getClass().getSuperclass().getName().split(".*\\.|\\$.*")[1];
		else roomName = currentRoom.getClass().getName().split(".*\\.")[1];

		methods.remove("setInventory");
		methods.remove("getInventory");
		methods.remove("getDescription");
		methods.add("quit");
		methods.add("inventory");

		switch(roomName.toLowerCase()){
			case "bathroom":
				if(gs.is(GameState.word_found2)){
					methods.remove("showerYourself");
					methods.remove("conditionHair");
				}
				break;
			case "bedroom":
				if(gs.is(GameState.greeted_dog)) methods.remove("greetDog");
				if(gs.is(GameState.word_found3)) methods.remove("wearClothes");
				break;
			case "kitchen":
				if(gs.is(GameState.word_found4)) methods.remove("eatPancakes");
				break;
			case "outside":
				if(gs.is(GameState.completed_note)){
					methods.remove("sayNotes");
					methods.remove("lookSky");
					methods.remove("runHome");
				}
				break;
			case "trainstation":
				if(gs.is(GameState.bought_ticket)){
					methods.remove("waitCounter");
					methods.remove("useMachine");
				}
				break;
			case "train":
				if(gs.is(GameState.used_mouthwash)){
					methods.remove("sitSeat");
					methods.remove("standTrain");
				}
				break;
			case "nextstation":
				if(gs.is(GameState.train_fixed)) methods.remove("fixTrain");
				if(gs.is(GameState.pet_duck)) methods.remove("shooDuck");
				break;
			case "street":
				if(gs.is(GameState.got_money) || gs.is(GameState.ignored_foam)) methods.remove("ignoreGirl");
				break;
			case "school":
				if(gs.is(GameState.scanned_ID)) methods.remove("scan_ID");
				else{
					methods.remove("petDuck");
					methods.remove("studyComputerScience");
					methods.remove("distractGuardian");
				}
				break;
		}
	}

	public String help(){
 
 		if(currentRoom instanceof EnterCondition){
			for(Method method : currentRoom.getClass().getSuperclass().getDeclaredMethods()) commands.add(method.getName());
			for(Field room : currentRoom.getClass().getSuperclass().getDeclaredFields()){
				if(room.isAnnotationPresent(Direction.class)) locations.put(room.getAnnotation(Direction.class).command(), room.getType().getName().split(".*\\.|\\$.*")[1]);
			}
		}else{
			for(Method method : currentRoom.getClass().getDeclaredMethods()) commands.add(method.getName());
			for(Field room : currentRoom.getClass().getDeclaredFields()){
				if(room.isAnnotationPresent(Direction.class)) locations.put(room.getAnnotation(Direction.class).command(), room.getType().getName().split(".*\\.")[1]);
			}		
		}

		checkRoom(commands);

		s = "Commands you can try:\n";
		for(String doable : commands) {
			if(doable.equals("use") || doable.equals("take")) s += "You can '" + doable + " <item>'\n";
			else s += "You can '" + doable + "'\n";
		}
		s += "\n";
		for(String direction : locations.keySet()) s += "You can 'go " + direction + "' to go to " + locations.get(direction) + "\n";

		return s;
	}

}