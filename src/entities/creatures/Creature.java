package entities.creatures;

import java.io.IOException;

import entities.Entity;

public abstract class Creature extends Entity {
	int hp;
	final int MAX_HP;
	double movementSpeed;
	int dir;

	boolean moving;
	public Creature(int health, int xPos, int yPos, double mvSpd, int wd, int ht) throws IOException {
		super(xPos, yPos, wd, ht);
		
		hp = health;
		MAX_HP = health;
		
		movementSpeed = mvSpd;
		
		dir = 0;
		moving = false;
	}

	public Creature(int health, int xPos, int yPos, double mvSpd, int wd, int ht, int hitWd, int hitHt) throws IOException {
		super(xPos, yPos, wd, ht, hitWd, hitHt);
		
		hp = health;
		MAX_HP = health;
		
		movementSpeed = mvSpd;
		
		dir = 0;
		moving = false;
	}
	
	public abstract void move ();
}
