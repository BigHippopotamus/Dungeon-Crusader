package world;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class MapMaker {
	ArrayList<ArrayList<Room>> world;
	BufferedImage map;
	Graphics2D mapGraphics;
	
	public MapMaker () {
		world = Universe.map;
		map = new BufferedImage(world.get(0).size()*4 + 1, world.size()*4 + 1, BufferedImage.TYPE_4BYTE_ABGR);
		mapGraphics = map.createGraphics();
	}
	
	public void drawRoom (int x, int y) {
		Room room = world.get(y).get(x);
		BufferedImage temp = new BufferedImage(5, 5, BufferedImage.TYPE_4BYTE_ABGR);
		Graphics2D g = temp.createGraphics();
		
		g.setColor(new Color(0, 0, 200));
		g.fillRect(1, 1, 3, 3);
		if (room.getDir(0) != 0) g.fillRect(4, 2, 1, 1);
		if (room.getDir(1) != 0) g.fillRect(2, 4, 1, 1);
		if (room.getDir(2) != 0) g.fillRect(0, 2, 1, 1);
		if (room.getDir(3) != 0) g.fillRect(2, 0, 1, 1);
		
		mapGraphics.drawImage(temp, x*4, y*4, 5, 5, null);
	}
	
	public BufferedImage getMap (int x, int y) {
		BufferedImage curMap = new BufferedImage(map.getWidth(), map.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
		Graphics2D g = curMap.createGraphics();
		g.drawImage(map, 0, 0, null);
		g.setColor(new Color(0, 200, 0));
		g.fillRect(x*4 + 1, y*4 + 1, 3, 3);
		return curMap;
	}
}
