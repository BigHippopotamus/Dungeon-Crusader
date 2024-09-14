package entities;

import java.awt.image.BufferedImage;
import java.io.IOException;

import world.Data;

public abstract class Entity {
	protected double x, y;
	private int width, height;
	private int hitWidth, hitHeight;
	
	protected BufferedImage[][] sequence;
	public Entity (int xPos, int yPos, int wd, int ht) throws IOException {		
		x = xPos;
		y = yPos;
		
		width = hitWidth = wd;
		height = hitHeight = ht;
		
		sequence = null;
	}

	public Entity (int xPos, int yPos, int wd, int ht, int hitWd, int hitHt) throws IOException {		
		x = xPos;
		y = yPos;
		
		width = wd;
		height = ht;
		
		hitWidth = hitWd;
		hitHeight = hitHt;
		
		sequence = null;
	}
	
	protected abstract void getSeq () throws IOException;
	
	public abstract BufferedImage getImg ();
	
	public boolean checkBounds () {
		return (x >= 0 && y >= 0 && x <= Data.WIDTH && y <= Data.HEIGHT);
	}

	public int getX () {
		return (int)x;
	}
	
	public int getY () {
		return (int)y;
	}
	
	public void setX (int xPos) {
		x = xPos;
	}
	
	public void setY (int yPos) {
		y = yPos;
	}

	public int getWidth () {
		return width;
	}
	
	public int getHeight () {
		return height;
	}
	
	public static boolean colliding (Entity a, Entity b) {
		boolean horizontal = Math.abs(a.x - b.x) < a.hitWidth/2 + b.hitWidth/2;
		boolean vertical = Math.abs(a.y - b.y) < a.hitHeight/2 + b.hitHeight/2;
		
		return horizontal && vertical;
	}
	
}
