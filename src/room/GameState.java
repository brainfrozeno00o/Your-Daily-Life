package room;

import java.io.*;

public class GameState implements Serializable{

	public static final int brushing_teeth = 1;
	public static final int showered = 2;
	public static final int brought_mouthwash = 4;
	public static final int word_found2 = 8;
	public static final int greeted_dog = 16;
	public static final int wore_clothes = 32;
	public static final int word_found3 = 64;
	public static final int word_found4 = 128;
	public static final int completed_note = 256;
	public static final int bought_ticket = 512;
	public static final int on_train = 1024;
	public static final int used_mouthwash = 2048;
	public static final int pet_duck = 4096;
	public static final int train_fixed = 8192;
	public static final int got_money = 16384;
	public static final int ignored_foam = 32768;
	public static final int scanned_ID = 65536;
	public static final int dead = 131072;
	private int state = 0;

	public boolean is(int constant){return ((state & constant) == constant);}

	public void set(int constant){state = state | constant;}

	public void setPreviousState(int prev){state = prev;}

	public int getState(){return state;}
}