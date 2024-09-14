package world;

import java.io.IOException;

public class RoomGen extends Thread {
	int roomX, roomY;
	
	public RoomGen (int x, int y) {
		roomX = x;
		roomY = y;
	}
	
	public void run () {
		
		for (int i = 1; i <= 4; i++) {
			if (Universe.map.get(roomY).get(roomX).getDir(i-1) == 0) continue;
			int newX = roomX, newY = roomY;
			
			if (i == 1) newX++;
			else if (i ==2) newY++;
			else if (i == 3) newX--;
			else newY--;
			
			
			Room newRoom = Universe.map.get(newY).get(newX);
			if (newRoom.getRoom() != null) continue;
			
			try {
				newRoom.createRoom();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}