package world;

import audio.MusicSystem;
import audio.SoundSystem;
import entities.attacks.Attack;
import entities.creatures.enemies.Enemy;
import entities.items.Item;

import javax.sound.sampled.LineUnavailableException;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
public class Universe {
	public static ArrayList<ArrayList<Room>> map = new ArrayList<ArrayList<Room>>();

	public static ArrayList<Attack> attacks = new ArrayList<Attack>();	

	public static MusicSystem mus = null;
	
	private static Thread cutscene = null;
	public static boolean inCutscene = false; //If the game is in the middle of a cutscene
	public static double timer = 0; //The time taken for a cutscene to occur
	
	public static boolean playing = true; //Whether the game is over or not
	public static Room curRoom = null;
	public static int roomType = 0;
	public static double deltaTime = 0; //The time the previous frame took to execute

	public static boolean sword = true;
	public static boolean blueKey = false; //If the blue key has been collected
	public static boolean blueDoor = false; //If the blue door has been opened
	public static boolean lavaBoots = false; //If the lava boots have been collected
	public static boolean bow = false; //If the bow has been collected
	public static boolean greenKey = false; //If the green key has been collected
	public static boolean greenDoor = false; //If the green door has been opened
	
	public static MovementStack movement = new MovementStack();
	
	public static Item getItem () {
		Item item = null;
		if (roomType == 3 && !lavaBoots || roomType == 4 && !blueKey || roomType == 6 && playing || roomType == 7 && !greenKey) item = curRoom.getItem();
		
		if (item != null) item.update();
		
		return item;
	}
	
	public static void updateRoom (Room rm) {
		curRoom = rm;
		roomType = rm.type;
		if (roomType == 6) mus.stop();
		else mus.start();
	}
	
	public static Enemy[] getEnemies () {
		return curRoom.getEnemies();
	}

	public static Attack[] getAttacks () {
		Attack[] atks = new Attack[attacks.size()];
		attacks.toArray(atks);
		
		return atks;
	}
	
	public static void openBlue (int x, int y) {
		blueDoor = true;
		cutscene = new Thread () {
			public synchronized void run () {
				try {
					inCutscene = true;
					curRoom.addKey(x, y);
					SoundSystem.playSound(SoundSystem.Sounds.KEY_INSERT);
					curRoom.createRoom();
					wait(1000);
					
					curRoom.unlock(x, y);
					SoundSystem.playSound(SoundSystem.Sounds.DOOR_OPEN);
					curRoom.createRoom();
					inCutscene = false;
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (LineUnavailableException e) {
					e.printStackTrace();
				}
			}
		};
		
		cutscene.start();
	}

	public static void openGreen(int x, int y) {
		greenDoor = true;
		cutscene = new Thread () {
			public synchronized void run () {
				try {
					inCutscene = true;
					curRoom.addKey(x, y);
					SoundSystem.playSound(SoundSystem.Sounds.KEY_INSERT);
					curRoom.createRoom();
					wait(1000);
					
					curRoom.unlock(x, y);
					SoundSystem.playSound(SoundSystem.Sounds.DOOR_OPEN);
					curRoom.createRoom();
					inCutscene = false;
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (LineUnavailableException e) {
					e.printStackTrace();
				}
			}
		};
		
		cutscene.start();
	}
	
	public static int getSize () {
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		return Math.min(dim.width/Data.WIDTH, dim.height/Data.HEIGHT);
	}
}
