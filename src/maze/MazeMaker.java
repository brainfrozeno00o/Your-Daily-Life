package maze;

import io.github.lukehutch.fastclasspathscanner.FastClasspathScanner;
import io.github.lukehutch.fastclasspathscanner.scanner.ScanResult;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.DynamicType.Loaded;
import net.bytebuddy.dynamic.DynamicType.Unloaded;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;

import java.util.*;
import java.lang.reflect.*;
import java.io.*;

import anno.Direction;
import anno.Command;
import anno.EnterCheck;
import room.GameState;

public class MazeMaker implements State{

	private HashMap<Class<?>, Object> roomMap = new HashMap<Class<?>, Object>();
	private GameState gs = new GameState();
	private String s = "", user = "", previousString = "";
	private Object currentRoom;
	private Method m;
	private Class<?> clazz;
	private boolean ifProxy, registered = false, loaded = false;
	private Caretaker ct = new Caretaker();
	private Originator ot = new Originator();

	public MazeMaker(String user){this.user = user;}

	@Override
	public void changeState(DailyLifeGUI gui) {gui.setCurrState(new RegisterUser(true));}

	public void register(String[] getUsername){
		if(getUsername.length > 2){
			for(int i = 1; i < getUsername.length; i++) {
				if(i != getUsername.length - 1) user += getUsername[i] + " ";
				else user += getUsername[i];
			}
		}else user = getUsername[1];
		registered = true;
	}

	public int stateNumber(){return 1;}
	
	public boolean checkIfDead(){return gs.is(GameState.dead);}

	public String startIt() throws Exception{
		FastClasspathScanner scanner = new FastClasspathScanner("room");
		ScanResult result = scanner.scan();

		List<String> allClasses = result.getNamesOfAllClasses();
		allClasses.remove("room.GameState");
		allClasses.remove("room.Inventory");
		
		for (String className : allClasses){
			Class<?> clazz = Class.forName(className);
			Object instance = null;			

			if(clazz.getName().contains("room")){
				if(clazz.isAnnotationPresent(EnterCheck.class)){
					ByteBuddy byteBuddy = new ByteBuddy();
					DynamicType.Builder<?> builder = byteBuddy.subclass(clazz).implement(EnterCondition.class);
					builder = builder.name(clazz.getName() + "$ByteBuddy$").serialVersionUid((long) clazz.getName().hashCode());
					builder = builder.method(ElementMatchers.named("canEnter")).intercept(MethodDelegation.to(Interceptor.class));
					builder = builder.method(ElementMatchers.named("enterMessage")).intercept(MethodDelegation.to(Interceptor.class));
					builder = builder.method(ElementMatchers.named("unableToEnterMessage")).intercept(MethodDelegation.to(Interceptor.class));
					
					Unloaded<?> unloadedClass = builder.make();
					Loaded<?> loaded = unloadedClass.load(getClass().getClassLoader());
					Class<?> dynamicType = loaded.getLoaded();
					instance = dynamicType.newInstance();
				}else instance = clazz.newInstance();
			}
			roomMap.put(clazz, instance);				
		}

		try{
			FileInputStream input = new FileInputStream("sessions.ser");
			ObjectInputStream in = new ObjectInputStream(input);
			ct = (Caretaker) in.readObject();
			in.close();

			loaded = true;
			load();
		}catch(Exception e){
			currentRoom = roomMap.get(room.Somewhere.class);
			s = "Welcome to the game, " + user + "\n\n" + printDescription();
		}

		previousString = s.split("\n\n")[1];
		return s;
	}

	public String quit() throws Exception{
		try{
			if(ct.savedSessions() != 0){
				FileOutputStream file = new FileOutputStream("sessions.ser");
				ObjectOutputStream out = new ObjectOutputStream(file);
				out.writeObject(ct);
				out.close();

				FileOutputStream users = new FileOutputStream("users.ser");
				ObjectOutputStream out2 = new ObjectOutputStream(users);
				out2.writeObject(ct.getUsers());
				out2.close();
			}
		}catch(IOException e){}	
		return "Thanks for playing!\n";
	}

	@SuppressWarnings("unchecked")
	public String save() throws Exception{
		if(currentRoom instanceof EnterCondition) m = currentRoom.getClass().getSuperclass().getSuperclass().getDeclaredMethod("getInventory");
		else m = currentRoom.getClass().getSuperclass().getDeclaredMethod("getInventory");
		ArrayList<String> invToArray = (ArrayList<String>) m.invoke(currentRoom);
		String[] inventory = invToArray.toArray(new String[invToArray.size()]);

		ot.setState(currentRoom, gs.getState(), roomMap, previousString, inventory);
		ct.save(user, ot.saveToMemento());
		return "Saving game... Carry on and continue your game.\n";
	}

	public String load() throws Exception{
		if(ct.exists(user)){
			if(loaded) s = "Welcome back to the game, " + user + ".\n\n"; else s = "Loading from previous save...\n\n";

			ot.getStateFromMemento(ct.load(user));

			currentRoom = ot.getCurrentRoom();

			if (currentRoom instanceof EnterCondition) m = currentRoom.getClass().getSuperclass().getSuperclass().getDeclaredMethod("setInventory", ArrayList.class);
			else m = currentRoom.getClass().getSuperclass().getDeclaredMethod("setInventory", ArrayList.class);
			m.invoke(currentRoom, new ArrayList<String>(Arrays.asList(ot.getInventory())));

			if(loaded) roomMap = ot.getRoomMap();

			gs.setPreviousState(ot.getState());

			s += ot.getLastMessage();
		}else{
			if(loaded || registered){
				gs.setPreviousState(0);
				currentRoom = roomMap.get(room.Somewhere.class);
				m = currentRoom.getClass().getSuperclass().getDeclaredMethod("setInventory", ArrayList.class);
				m.invoke(currentRoom, new ArrayList<String>(Arrays.asList(new String[0])));
				s = "Welcome to the game, " + user + "\n\n" + printDescription();
				previousString = s.split("\n\n")[1];
			}else s = "No previous saves... carry on and continue your game.\n";
		}
		if(loaded) loaded = false;			
		if(registered) registered = false;
		return s;
	}
	
	public String printDescription() throws Exception{	
		if(currentRoom instanceof EnterCondition)  m = currentRoom.getClass().getSuperclass().getDeclaredMethod("getDescription", GameState.class); 
		else m = currentRoom.getClass().getDeclaredMethod("getDescription", GameState.class);

		s = (String) m.invoke(currentRoom, gs);
		return s;	
	}
	
	public String move(String direction) throws Exception{
		s = "Where are you going?\n";

		if(currentRoom instanceof EnterCondition){
			ifProxy = true;
			clazz = currentRoom.getClass().getSuperclass();
		}else{
			ifProxy = false;
			clazz = currentRoom.getClass();
		}

		try{
			Field[] fields;

			if(ifProxy) fields = currentRoom.getClass().getSuperclass().getDeclaredFields();
			else fields = currentRoom.getClass().getDeclaredFields();

			for (Field f : fields){
				if (f.isAnnotationPresent(Direction.class)){
					if (f.getAnnotation(Direction.class).command().equals(direction)){
						Class<?> fieldClass = f.getType();
						currentRoom = roomMap.get(fieldClass);

						if(currentRoom instanceof EnterCondition){
							if(((EnterCondition) currentRoom).canEnter(gs, fieldClass)){
								s = ((EnterCondition) currentRoom).enterMessage() + "\n";
								s += printDescription();
							}else{
								s = ((EnterCondition) currentRoom).unableToEnterMessage() + "\n";
								currentRoom = roomMap.get(clazz);
								s += printDescription();
							}
						}else s = printDescription();
					}
				}
			}

			previousString = s;
		}catch(Exception e){return "You can't do this.\n";}

		return s;
	}
	
	public String execute(String[] methodParams) throws Exception{
		s = "What are you trying to do?\n";

		if(currentRoom instanceof EnterCondition) ifProxy = true; else ifProxy = false;

		try{
			Method[] methods;

			if (ifProxy) methods = currentRoom.getClass().getSuperclass().getDeclaredMethods();
			else methods = currentRoom.getClass().getDeclaredMethods();

			for(Method m : methods){
				if(m.isAnnotationPresent(Command.class)){
					if(m.getAnnotation(Command.class).doThis().equals(methodParams[0])){
						Class<?>[] methodParameters = m.getParameterTypes();

						if(ifProxy) m = currentRoom.getClass().getSuperclass().getDeclaredMethod(methodParams[0], methodParameters);
						else m = currentRoom.getClass().getDeclaredMethod(methodParams[0], methodParameters);

						switch (methodParameters.length){
							case 1:
								try{
									s = (String) m.invoke(currentRoom, methodParams[1]);
								}catch(Exception e){
									s = (String) m.invoke(currentRoom, gs);
								}
								break;
							case 2:
								try{
									s = (String) m.invoke(currentRoom, Integer.parseInt(methodParams[1]), gs);
								}catch(Exception e){
									s = (String) m.invoke(currentRoom, methodParams[1], gs);
								}
								break;
							default:
								s = (String) m.invoke(currentRoom);
								break;
						}
					}
				}
			}
			

			previousString = s;
		}catch(Exception e){return "You can't do this\n";}
		
		return s;
	}

	public String help() throws Exception{
		return new Helper(currentRoom, gs).help();
	}

	@SuppressWarnings("unchecked")
	public String inventory() throws Exception{
		if(currentRoom instanceof EnterCondition) m = currentRoom.getClass().getSuperclass().getSuperclass().getDeclaredMethod("getInventory");
		else m = currentRoom.getClass().getSuperclass().getDeclaredMethod("getInventory");

		ArrayList<String> printInventory = (ArrayList<String>) m.invoke(currentRoom);
		if(printInventory.isEmpty()) s = "You currently have nothing in your inventory.\n";
		else{
			s = "You currently have the following in your inventory: \n";
			for(String item : printInventory) s += "- " + item + "\n";
		}

		previousString = s;
		return s;
	}
}