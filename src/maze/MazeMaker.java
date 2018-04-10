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

import anno.Direction;
import anno.Command;
import anno.EnterCheck;
import room.GameState;

public class MazeMaker {

	private HashMap<Class<?>, Object> roomMap = new HashMap<Class<?>, Object>();
	private GameState gs = new GameState();
	private ArrayList<String> inventory = new ArrayList<String>();
	private List<String> allClasses = new ArrayList<String>();
	private String s = "";
	private Object currentRoom;
	private Method m;
	private Class<?> clazz;
	private boolean ifProxy;
	
	public boolean checkIfDead(){return gs.is(GameState.dead);}
	
	public String load() throws Exception{
		FastClasspathScanner scanner = new FastClasspathScanner("room");
		ScanResult result = scanner.scan();

		allClasses = result.getNamesOfAllClasses();
		allClasses.remove("room.GameState");
		
		for (String className : allClasses){
			Class<?> clazz = Class.forName(className);
			Object instance = null;			

			if(clazz.getName().contains("room")){
				if(clazz.isAnnotationPresent(EnterCheck.class)){
					ByteBuddy byteBuddy = new ByteBuddy();
					DynamicType.Builder<?> builder = byteBuddy.subclass(clazz).implement(EnterCondition.class);
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
				
		currentRoom = roomMap.get(room.Somewhere.class);
		Method setInventory = currentRoom.getClass().getDeclaredMethod("setInventory", ArrayList.class);
		setInventory.invoke(currentRoom, inventory);
		return printDescription();
	}	
	
	public String printDescription() throws Exception{	
		if(currentRoom instanceof EnterCondition){
			if(((EnterCondition) currentRoom).canEnter(gs, currentRoom.getClass())) m = currentRoom.getClass().getSuperclass().getDeclaredMethod("getDescription", GameState.class); 
		}else m = currentRoom.getClass().getDeclaredMethod("getDescription", GameState.class);
		s = (String) m.invoke(currentRoom, gs);
		return s;	
	}
	
	@SuppressWarnings("unchecked")
	public String move(String direction) throws Exception{
		s = "Where are you going?\n";
		if(currentRoom instanceof EnterCondition){
			if(((EnterCondition) currentRoom).canEnter(gs, currentRoom.getClass())){ifProxy = true;}
		}else {
			ifProxy = false;
			clazz = currentRoom.getClass();
		}
		try{
			Method setInventory, getInventory;
			Field[] fields;
			String[] getDirection = direction.trim().split("\\s+");
			if(ifProxy) fields = currentRoom.getClass().getSuperclass().getDeclaredFields();
			else fields = currentRoom.getClass().getDeclaredFields();
			for (Field f : fields){
				if (f.isAnnotationPresent(Direction.class)){
					if (f.getAnnotation(Direction.class).command().equals(getDirection[1])){
						if(ifProxy) getInventory = currentRoom.getClass().getSuperclass().getDeclaredMethod("getInventory");
						else getInventory = currentRoom.getClass().getDeclaredMethod("getInventory");
						inventory = (ArrayList<String>) getInventory.invoke(currentRoom);
						Class<?> fieldClass = f.getType();
						currentRoom = roomMap.get(fieldClass);
						if(currentRoom instanceof EnterCondition){
							if(((EnterCondition) currentRoom).canEnter(gs, fieldClass)){
								s = ((EnterCondition) currentRoom).enterMessage() + "\n";
								setInventory = currentRoom.getClass().getSuperclass().getDeclaredMethod("setInventory", ArrayList.class);
								setInventory.invoke(currentRoom, inventory);
								s += printDescription();
							}else {
								s = ((EnterCondition) currentRoom).unableToEnterMessage() + "\n";
								currentRoom = roomMap.get(clazz); 
								setInventory = currentRoom.getClass().getDeclaredMethod("setInventory", ArrayList.class);
								setInventory.invoke(currentRoom, inventory);
								s += printDescription();
							}
						}else{
							setInventory = currentRoom.getClass().getDeclaredMethod("setInventory", ArrayList.class);
							setInventory.invoke(currentRoom, inventory);
							s = printDescription();
						}
					}
				}
			}
		}catch(Exception e){e.printStackTrace();}
		return s;
	}
	
	public String execute(String command){
		s = "What are you trying to do?\n";
		if(currentRoom instanceof EnterCondition){
			if(((EnterCondition) currentRoom).canEnter(gs, currentRoom.getClass())) ifProxy = true;
		}else ifProxy = false;
		try{
			Method[] methods;
			if (ifProxy) methods = currentRoom.getClass().getSuperclass().getDeclaredMethods();
			else methods = currentRoom.getClass().getDeclaredMethods();
			String checkString = new String(command);
			if(checkString.matches(".*\\s+.*")){
				String[] methodParams = command.trim().split("\\s+");
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
			}else{
				for(Method m: methods){
					if(m.isAnnotationPresent(Command.class)){				
						if(m.getAnnotation(Command.class).doThis().equals(command)){
							Class<?>[] methodParameters = m.getParameterTypes();
							if(ifProxy) m = currentRoom.getClass().getSuperclass().getDeclaredMethod(command, methodParameters);
							else m = currentRoom.getClass().getDeclaredMethod(command, methodParameters);
							switch(methodParameters.length){
								case 1:
									s = (String) m.invoke(currentRoom, gs);
									break;
								default:
									s = (String) m.invoke(currentRoom);
									break;
							}
						}
					}
				}
			}
		}catch(Exception e){return "You can't do this\n";}
		return s;
	}
}