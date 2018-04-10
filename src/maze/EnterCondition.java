package maze;

import room.GameState;

public interface EnterCondition {
	public boolean canEnter(GameState state, Class<?> clazz);
	public String enterMessage();
	public String unableToEnterMessage();
}