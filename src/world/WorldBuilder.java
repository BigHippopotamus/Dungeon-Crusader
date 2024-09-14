package world;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Random;

public class WorldBuilder {
	int rooms;
	int size;
	int stage;

	final int STG_1, STG_2, STG_3, STG_4, STG_5;
	
	Room test;
	/*
	 * Stages:
	 * 0 - Starting
	 * 1 - Lava boots room (locked)
	 * 2 - Lava boots room key
	 * 3 - Bow
	 * 4 - Boss room entrance (locked)
	 * 5 - Boss room key
	 */

	String add;
	Random rand;
	ArrayList<ArrayList<Room>> map;
	
	long time = System.nanoTime();
	public WorldBuilder () {
		rooms = 0;
		size = 1;
		stage = 0;

		STG_1 = 6;
		STG_2 = 12;
		STG_3 = 15;
		STG_4 = 25;
		STG_5 = 35;

		rand = new Random();
		add = "Assets/Rooms";
		map = new ArrayList<ArrayList<Room>>(0);
		map.add(new ArrayList<Room>(0));
		
		test = null;
	}

	public int[] createMap (ArrayList<ArrayList<Room>> world) throws IOException, URISyntaxException {
		//Stage 0: Generate starting room
		Room room = null;
		String start = add+"/1/"+(2*rand.nextInt(4))+".png";
		try {
			room = createRoom(start, 1);
		} catch (Exception e) {
			e.printStackTrace();
		}
		map.get(0).add(room);
		rooms++;
		stage++;

		while (stage <= 2) {
			gen1(room, new ArrayList<Room>(), 0);
			size++;
		}
		
		System.out.println("gen1 done");
		
		while (stage <= 5) {
			gen2(room, new ArrayList<Room>(), 0);
			size++;
		}
		
		System.out.println("gen2 done");

		gen3(room, new ArrayList<Room>());
		
		System.out.println("gen3 done");

		for (int i = 0; i < map.size(); i++) {
			world.add(new ArrayList<Room>());
			for (int j = 0; j < map.get(i).size(); j++) {
				world.get(i).add(map.get(i).get(j));
			}
		}

		return locate(room);
	}

	/*
	 * Stage 1 and 2 of generating: generates room with the lava boots and first key.
	 * While doing so, it is ensured that all links between rooms are pathways and not lava.
	 */
	private void gen1 (Room room, ArrayList<Room> order, int lvl) throws IOException {
		if (lvl >= size) return;

		order.add(room);
		//System.out.println(stage + " " + rooms);
		for (int i = 0; i < 4; i++) {
			//System.out.println(room + " " + i);
			//System.gc();
			int[] pos = locate(room);
			int x = pos[0];
			int y = pos[1];
			if (map.get(y).get(x).getDir(i) != 2) continue;

			int roomX = x;
			int roomY = y;
			if (i == 0) roomX = x+1;
			else if (i == 1) roomY = y+1;
			else if (i == 2) roomX = x-1;
			else roomY = y-1;
			if (roomX >= 0 && roomY >= 0 && roomX < map.get(0).size() && roomY < map.size() && map.get(roomY).get(roomX) != null) {
				Room t = map.get(roomY).get(roomX);
				if (order.indexOf(t) == -1) {
					gen1(t, order, lvl+1);
				}
				else continue;
			}
			else {
				Room newRoom = null;

				ArrayList<Room> set = new ArrayList<Room>();
				Room stageEnd = null;
				if (rooms == 1) {
					set.add(createRoom(add+"/Q/0/0.png", 2));
				}
				else {
					for (int j = 2; j <= 4; j++) {
						ArrayList<Room> type = new ArrayList<Room>();
						for (int k = 0; true; k++) {
							Room temp = null;
							try {
								temp = createRoom(add+"/"+j+"/"+k+".png", 0);
							} catch (IllegalArgumentException e) {
								break;
							}
							if (temp.getEnds() >= Math.max(2, Math.min(getLinks(roomX, roomY)[0]+1, 4)) && checkFit(temp, roomX, roomY)) type.add(temp);
						}
						for (int m = 1; m <= Math.min(9 - 2*j, type.size()); m++) set.add(type.get(rand.nextInt(type.size())));
					}
				}

				if (rooms >= STG_1 && stage == 1) {
					for (int m = 0; m < 4; m++) {
						Room temp = createRoom(add+"/Q/1/"+m+".png", 3);
						if (checkFit(temp, roomX, roomY)) {
							//System.out.println("yea");
							stageEnd = temp;
							for (int n = 0; n <= rooms - STG_1; n++) set.add(temp);
							//for (int n = 1; n < set.size(); n++) System.out.print(set.get(n) + " ");
						}
					}
				}
				if (rooms >= STG_2 && stage == 2) {
					for (int m = 0; m < 4; m++) {
						Room temp = createRoom(add+"/Q/2/"+m+".png", 4);
						if (checkFit(temp, roomX, roomY)) {
							//System.out.println("yea");
							stageEnd = temp;
							for (int n = 0; n <= rooms - STG_2; n++) set.add(temp);
							//for (int n = 1; n < set.size(); n++) System.out.print(set.get(n) + " ");
						}
					}
				}

				if (set.size() == 0) {
					for (int j = 1; j <= 4; j++) {
						ArrayList<Room> type = new ArrayList<Room>();
						for (int k = 0; true; k++) {
							Room temp = null;
							try {
								temp = createRoom(add+"/"+j+"/"+k+".png", 0);
							} catch (IllegalArgumentException e) {
								break;
							}
							if (checkFit(temp, roomX, roomY)) type.add(temp);
						}
						for (int m = 1; m <= Math.min(9 - 2*j, type.size()); m++) set.add(type.get(rand.nextInt(type.size())));
					}
				}

				newRoom = set.get(rand.nextInt(set.size()));
				if (newRoom == stageEnd) {
					//if (stage == 2) test = newRoom;
					stage++;
				}

				insert(roomX, roomY, newRoom);
				rooms++;
				gen1(newRoom, order, lvl+1);
			}
		}

		order.remove(room);
	}

	/*
	 * Stages 3 to 5 of generating: generates the bow, boss room, and key to the boss room.
	 */
	private void gen2 (Room room, ArrayList<Room> order, int lvl) throws IOException {
		if (lvl >= size) return;

		order.add(room);
		for (int i = 0; i < 4; i++) {
			//System.gc();
			int[] pos = locate(room);
			int x = pos[0];
			int y = pos[1];
			if (map.get(y).get(x).getDir(i) == 0) continue;

			int roomX = x;
			int roomY = y;
			if (i == 0) roomX = x+1;
			else if (i == 1) roomY = y+1;
			else if (i == 2) roomX = x-1;
			else roomY = y-1;
			if (roomX >= 0 && roomY >= 0 && roomX < map.get(0).size() && roomY < map.size() && map.get(roomY).get(roomX) != null) {
				Room t = map.get(roomY).get(roomX);
				if (order.indexOf(t) == -1) {
					gen2(t, order, lvl+1);
				}
				else continue;
			}
			else {
				Room newRoom = null;

				ArrayList<Room> set = new ArrayList<Room>();
				Room stageEnd = null;
				for (int j = 2; j <= 4; j++) {
					ArrayList<Room> type = new ArrayList<Room>();
					for (int k = 0; true; k++) {
						Room temp = null;
						try {
							temp = createRoom(add+"/"+j+"/"+k+".png", 0);
						} catch (IllegalArgumentException e) {
							break;
						}
						int[] links = getLinks(roomX, roomY);
						if (temp.getEnds() >= Math.max(2, Math.min(links[0]+links[1], 4)) && checkFit(temp, roomX, roomY)) type.add(temp);
					}
					for (int m = 1; m <= Math.min(9 - 2*j, type.size()); m++) set.add(type.get(rand.nextInt(type.size())));
				}

				if (rooms >= STG_3 && stage == 3) {
					for (int m = 0; m < 4; m++) {
						Room temp = createRoom(add+"/Q/3/"+m+".png", 5);
						if (checkFit(temp, roomX, roomY)) {
							stageEnd = temp;
							for (int n = 0; n <= rooms - STG_3; n++) set.add(temp);
						}
					}
				}

				if (rooms >= STG_4 && stage == 4) {
					for (int m = 0; m < 4; m++) {
						Room temp = createRoom(add+"/Q/4/"+m+".png", 6);
						if (checkFit(temp, roomX, roomY)) {
							stageEnd = temp;
							for (int n = 0; n <= rooms - STG_4; n++) set.add(temp);
						}
					}
				}

				if (rooms >= STG_5 && stage == 5) {
					for (int m = 0; m < 4; m++) {
						Room temp = createRoom(add+"/Q/5/"+m+".png", 7);
						if (checkFit(temp, roomX, roomY)) {
							stageEnd = temp;
							for (int n = 0; n <= rooms - STG_5; n++) set.add(temp);
						}
					}
				}

				if (set.size() == 0) {
					for (int j = 1; j <= 4; j++) {
						ArrayList<Room> type = new ArrayList<Room>();
						for (int k = 0; true; k++) {
							Room temp = null;
							try {
								temp = createRoom(add+"/"+j+"/"+k+".png", 0);
							} catch (IllegalArgumentException e) {
								break;
							}
							if (checkFit(temp, roomX, roomY)) type.add(temp);
						}
						for (int m = 1; m <= Math.min(9 - 2*j, type.size()); m++) set.add(type.get(rand.nextInt(type.size())));
					}
				}

				newRoom = set.get(rand.nextInt(set.size()));
				if (newRoom == stageEnd) {
					//if (stage == 4) test = newRoom;
					stage++;
				}

				insert(roomX, roomY, newRoom);
				rooms++;
				gen2(newRoom, order, lvl+1);
			}
		}

		order.remove(room);
	}

	/*
	 * Stage 6 of generating: finishes the dungeon using only rooms that perfectly fit in place
	 */
	private void gen3 (Room room, ArrayList<Room> order) throws IOException {
		order.add(room);
		for (int i = 0; i < 4; i++) {
			//System.gc();
			int[] pos = locate(room);
			int x = pos[0];
			int y = pos[1];
			if (map.get(y).get(x).getDir(i) == 0) continue;

			int roomX = x;
			int roomY = y;
			if (i == 0) roomX = x+1;
			else if (i == 1) roomY = y+1;
			else if (i == 2) roomX = x-1;
			else roomY = y-1;
			if (roomX >= 0 && roomY >= 0 && roomX < map.get(0).size() && roomY < map.size() && map.get(roomY).get(roomX) != null) {
				Room t = map.get(roomY).get(roomX);
				if (order.indexOf(t) == -1) {
					gen3(t, order);
				}
				else continue;
			}
			else {
				Room newRoom = null;

				int[] links = getLinks(roomX, roomY);
				for (int k = 0; true; k++) {
					Room temp = null;
					try {
						temp = createRoom(add+"/"+(links[0]+links[1])+"/"+k+".png", 0);
					} catch (IllegalArgumentException e) {
						break;
					}
					if (checkFit(temp, roomX, roomY)) {
						newRoom = temp;
						break;
					}
				}

				insert(roomX, roomY, newRoom);
				rooms++;
			}
		}

		order.remove(room);
	}

	private void insert (int x, int y, Room room) {
		/*
		 * dir refers to the direction of adding:
		 * 0 - Right
		 * 1 - Down
		 * 2 - Left
		 * 3 - Up
		 */
		//System.out.println(room);
		//for (int i = 0; i < 4; i++) System.out.print(room.getDir(i] + " ");
		//System.out.println();

		if (x == map.get(0).size()) {
			for (int i = 0; i < map.size(); i++) {
				map.get(i).add(null);
			}
		}
		if (y == map.size()) {
			ArrayList<Room> temp = new ArrayList<Room>(map.get(0).size());
			for (int i = 0; i < map.get(0).size(); i++) {
				temp.add(null);
			}
			map.add(temp);
		}
		if (x == -1) {
			for (int i = 0; i < map.size(); i++) {
				map.get(i).add(0, null);
			}
			x++;
		}
		if (y == -1) {
			ArrayList<Room> temp = new ArrayList<Room>(map.get(0).size());
			for (int i = 0; i < map.get(0).size(); i++) {
				temp.add(null);
			}
			map.add(0, temp);
			y++;
		}
		map.get(y).set(x, room);
	}


	private int[] locate (Room find) {
		int[] loc = {-1, -1}; //The x and y position of the room
		for (int i = 0; i < map.size(); i++) {
			int j = map.get(i).indexOf(find);
			if (j != -1) {
				loc[0] = j;
				loc[1] = i;
				break;
			}
		}
		return loc;
	}


	private Room createRoom (String filename, int type) throws IOException, IllegalArgumentException {
		BufferedImage img = FileGetter.getImage(filename);
		Room room = new Room(img, type);
		return room;
	}


	private boolean checkFit (Room room, int x, int y) {
		if (x+1 < map.get(0).size() && y >= 0 && y < map.size()) {
			Room t = map.get(y).get(x+1);
			if (t != null) {
				if (room.getDir(0) != t.getDir(2)) return false;
			}
		}
		if (y+1 < map.size() && x >= 0 && x < map.get(0).size()) {
			Room t = map.get(y+1).get(x);
			if (t != null) {
				if (room.getDir(1) != t.getDir(3)) return false;
			}
		}
		if (x-1 >= 0 && y >= 0 && y < map.size()) {
			Room t = map.get(y).get(x-1);
			if (t != null) {
				if (room.getDir(2) != t.getDir(0)) return false;
			}
		}
		if (y-1 >= 0 && x >= 0 && x < map.get(0).size()) {
			Room t = map.get(y-1).get(x);
			if (t != null) {
				if (room.getDir(3) != t.getDir(1)) return false;
			}
		}
		return true;
	}


	private int[] getLinks (int x, int y) {
		int path = 0;
		int lava = 0;

		if (x+1 < map.get(0).size() && y >= 0 && y < map.size()) {
			Room t = map.get(y).get(x+1);
			if (t != null) {
				if (t.getDir(2) == 1) lava++;
				else if (t.getDir(2) == 2) path++;
			}
		}
		if (y+1 < map.size() && x >= 0 && x < map.get(0).size()) {
			Room t = map.get(y+1).get(x);
			if (t != null) {
				if (t.getDir(3) == 1) lava++;
				else if (t.getDir(3) == 2) path++;
			}
		}
		if (x-1 >= 0 && y >= 0 && y < map.size()) {
			Room t = map.get(y).get(x-1);
			if (t != null) {
				if (t.getDir(0) == 1) lava++;
				else if (t.getDir(0) == 2) path++;
			}
		}
		if (y-1 >= 0 && x >= 0 && x < map.get(0).size()) {
			Room t = map.get(y-1).get(x);
			if (t != null) {
				if (t.getDir(1) == 1) lava++;
				else if (t.getDir(1) == 2) path++;
			}
		}
		
		return new int[] {path, lava};
	}
}
