package entities.creatures.enemies;

import java.io.IOException;

import entities.creatures.Creature;

public abstract class Enemy extends Creature {

	public Enemy(int health, int xPos, int yPos, double mvSpd, int wd, int ht) throws IOException {
		super(health, xPos, yPos, mvSpd, wd, ht);
	}

	public Enemy(int health, int xPos, int yPos, double mvSpd, int wd, int ht, int hitWd, int hitHt) throws IOException {
		super(health, xPos, yPos, mvSpd, wd, ht, hitWd, hitHt);
	}
}
