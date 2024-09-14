package world;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

import entities.creatures.enemies.*;
import entities.items.*;
public class Room {
	private int[] dir; //right, down, left, up
	int[][] room;
	
	int type;
	
	int itemX, itemY;
	
	ArrayList<Enemy> list;

	private BufferedImage layout;
	private BufferedImage roomBase;
	private BufferedImage roomMap;
	
	Item item;
	public Room (BufferedImage img, int ty) throws IOException {
		layout = img;
		
		itemX = 0;
		itemY = 0;
		
		room = new int[Data.P_HEIGHT][Data.P_WIDTH];
		for (int i = 0; i < Data.P_HEIGHT; i++) {
			for (int j = 0; j < Data.P_WIDTH; j++) {
				room[i][j] = getTile(j, i);
			}
		}
		/*
		 * Generates the map of the room in integers:
		 * 0 - Black space/wall
		 * 1 - Path
		 * 2 - Lava
		 */

		dir = new int[4];
		dir[0] = room[Data.P_HEIGHT/2][Data.P_WIDTH-1];
		dir[1] = room[Data.P_HEIGHT-1][Data.P_WIDTH/2];
		dir[2] = room[Data.P_HEIGHT/2][0];
		dir[3] = room[0][Data.P_WIDTH/2];
		
		type = ty;

		roomBase = null;
		roomMap = null;
		
		switch (type) {
		case 3:
			item = new LavaBoots(itemX, itemY);
			break;

		case 4:
			item = new BlueKey(itemX, itemY);
			break;

		case 6:
			item = new Orb(itemX, itemY);
			break;
			
		case 7:
			item = new GreenKey(itemX, itemY);
			break;
			
		default:
			item = null;
		}
		
		list = new ArrayList<Enemy>();
		createList();
	}
	
	private void createList() throws IOException {
		if (type == 1) {
			list.add(new Sparky(4, Data.WIDTH/2 + 32, Data.HEIGHT/2));
		}
	}

	public BufferedImage getRoom () {
		if (roomBase == null) return null;
		
		BufferedImage overlay = new BufferedImage(Data.WIDTH, Data.HEIGHT, BufferedImage.TYPE_3BYTE_BGR);
		Graphics2D g = overlay.createGraphics();
		g.drawImage(roomBase, 0, 0, Data.WIDTH, Data.HEIGHT, null);
		g.drawImage(roomMap, 0, 0, Data.WIDTH, Data.HEIGHT, null);
		return overlay;
	}

	//Generates an image of what the room looks like
	public void createRoom () throws IOException {
		if (roomBase == null) getRoomBase();
		getRoomMap();
	}
	
	private void getRoomBase () throws IOException {
		if (roomBase == null) roomBase = new BufferedImage(Data.WIDTH, Data.HEIGHT, BufferedImage.TYPE_3BYTE_BGR);
		Graphics2D g = roomBase.createGraphics();
		
		BufferedImage lava = FileGetter.getImage("Assets/Tiles/L.png");
		g.setColor(Color.BLACK);
		
		for (int i = 0; i < room.length; i++) {
			for (int j = 0; j < room[i].length; j++) {
				if (type == 6) {
					g.fillRect(j*Data.BITS, i*Data.BITS, Data.BITS, Data.BITS);
				}
				else {
					g.drawImage(lava, j*Data.BITS, i*Data.BITS, Data.BITS, Data.BITS, null);
				}
			}
		}
	}
	
	private void getRoomMap () throws IOException {
		if (roomMap == null) roomMap = new BufferedImage(Data.WIDTH, Data.HEIGHT, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = roomMap.createGraphics();
		
		g.setColor(Color.BLACK);
		for (int i = 0; i < room.length; i++) {
			for (int j = 0; j < room[i].length; j++) {
				if (room[i][j] == 0) {
					g.fillRect(j*Data.BITS, i*Data.BITS, Data.BITS, Data.BITS);
				}
			}
		}
		
		for (int i = 0; i < room.length; i++) {
			for (int j = 0; j < room[i].length; j++) {
				if (room[i][j] == 0 || room[i][j] == 1) continue;
				
				boolean u, r, d, l, ur, dr, dl, ul;
				u = r = d = l = ur = dr = dl = ul = false;
				if (j+1 >= room[i].length || room[i][j+1] == 2) r = true;
				if (i+1 >= room.length || room[i+1][j] == 2) d = true;
				if (j-1 < 0 || room[i][j-1] == 2) l = true;
				if (i-1 < 0 || room[i-1][j] == 2) u = true;
				if ((j+1 >= room[i].length || i-1 < 0) || room[i-1][j+1] == 2) ur = true;
				if ((j+1 >= room[i].length || i+1 >= room.length) || room[i+1][j+1] == 2) dr = true;
				if ((j-1 < 0 || i+1 >= room.length) || room[i+1][j-1] == 2) dl = true;
				if ((j-1 < 0 || i-1 < 0) || room[i-1][j-1] == 2) ul = true;

				BufferedImage img = null;
				String fn = "Assets/Tiles/Floor/";

				if (room[i][j] == 3) fn += "J1.png";
				else if (room[i][j] == 4) fn += "LKB.png";
				else if (room[i][j] == 5) fn += "LKG.png";
				else if (room[i][j] == 6) fn += "ULKB.png";
				else if (room[i][j] == 7) fn += "ULKG.png";
				else if (!r && !d && !l && !u) fn += "O.png";
				else if (!r && d && l && !u) {
					if (dl) fn += "C1.png";
					else fn += "IC1.png";
				}
				else if (!r && !d && l && u) {
					if (ul) fn += "C2.png";
					else fn += "IC2.png";
				}
				else if (r && !d && !l && u) {
					if (ur) fn += "C3.png";
					else fn += "IC3.png";
				}
				else if (r && d && !l && !u) {
					if (dr) fn += "C4.png";
					else fn += "IC4.png";
				}
				else if (!r && d && l && u) {
					if (!dl && !ul) fn += "T1.png";
					else if (!dl && ul) fn += "LS11.png";
					else if (dl && !ul) fn += "LS12.png";
					else fn += "S1.png";
				}
				else if (r && !d && l && u) {
					if (!ur && !ul) fn += "T2.png";
					else if (ur && !ul) fn += "LS21.png";
					else if (!ur && ul) fn += "LS22.png";
					else fn += "S2.png";
				}
				else if (r && d && !l && u) {
					if (!ur && !dr) fn += "T3.png";
					else if (!ur && dr) fn += "LS31.png";
					else if (ur && !dr) fn += "LS32.png";
					else fn += "S3.png";
				}
				else if (r && d && l && !u) {
					if (!dr && !dl) fn += "T4.png";
					else if (!dr && dl) fn += "LS41.png";
					else if (dr && !dl) fn += "LS42.png";
					else fn += "S4.png";
				}
				else if (r && !d && l && !u) fn += "I1.png";
				else if (!r && d && !l && u) fn += "I2.png";
				else if (!r && !d && l && !u) fn += "E1.png";
				else if (!r && !d && !l && u) fn += "E2.png";
				else if (r && !d && !l && !u) fn += "E3.png";
				else if (!r && d && !l && !u) fn += "E4.png";
				else {
					if (!ur && !dr && !dl && !ul) fn += "X.png";
					else if (!ur && dr && dl && ul) fn += "CS1.png";
					else if (ur && !dr && dl && ul) fn += "CS2.png";
					else if (ur && dr && !dl && ul) fn += "CS3.png";
					else if (ur && dr && dl && !ul) fn += "CS4.png";
					else if (!ur && !dr && dl && ul) fn += "B1.png";
					else if (ur && !dr && !dl && ul) fn += "B2.png";
					else if (ur && dr && !dl && !ul) fn += "B3.png";
					else if (!ur && dr && dl && !ul) fn += "B4.png";
					else if (!ur && dr && !dl && ul) fn += "K1.png";
					else if (ur && !dr && dl && !ul) fn += "K2.png";
					else if (!ur && !dr && dl && !ul) fn += "L1.png";
					else if (!ur && !dr && !dl && ul) fn += "L2.png";
					else if (ur && !dr && !dl && !ul) fn += "L3.png";
					else if (!ur && dr && !dl && !ul) fn += "L4.png";
					else fn += "N.png";
				}
					img = FileGetter.getImage(fn);
				g.drawImage(img, j*Data.BITS, i*Data.BITS, Data.BITS, Data.BITS, null);
			}
		}
	}

	//Returns the integer value of a tile based on the color of the map's pixel
	private int getTile (int x, int y) {
		Color col = new Color(layout.getRGB(x, y));
		
		int tile;
		
		if (col.getRed() == 0) tile = 0;
		else if (col.getRed() == 100) tile = 2;
		else if (col.getRed() == 80) tile = 3;
		else if (col.getRed() == 160) tile = 4;
		else if (col.getRed() == 192) tile = 5;
		else tile = 1;
		
		if (col.getBlue() == 80) {
			itemX = Data.BITS/2 + x*Data.BITS;
			itemY = Data.BITS/2 + y*Data.BITS;
		}
		
		return tile;
	}
	
	public void addKey (int x, int y) {
		room[y][x] += 2;
	}
	
	public void unlock (int x, int y) {
		room[y][x] = 2;
	}

	//Returns the number of exits/entrances which are a path and not lava
	public int getEnds () {
		int ends = 0;
		for (int i = 0; i < 4; i++) {
			if (dir[i] == 2) ends++;
		}
		return ends;
	}
	
	public int getDir (int side) {
		return dir[side];
	}
	
	public int[][] getMap () {
		return room;
	}
	
	public Item getItem () {
		return item;
	}

	public Enemy[] getEnemies() {
		Enemy[] enemies = new Enemy[list.size()];
		return list.toArray(enemies);
	}
}
