package entities.attacks;

import java.io.IOException;

import entities.Entity;
import world.Universe;

public abstract class Attack extends Entity {
	protected int seqLength;
	protected int cyclePos;
	protected double localTime;
	final protected double CYCLE_TIME;
	
	boolean movable;
	boolean enemyAtk;
	
	public Attack(int[] pos, int wd, int ht, int sqLngth, double cycTime, boolean enemy) throws IOException {
		super(pos[0], pos[1], wd, ht);
		
		seqLength = sqLngth;
		cyclePos = 0;
		localTime = 0;
		CYCLE_TIME = cycTime;
		
		enemyAtk = enemy;
	}
	
	protected void cycle () {
		localTime += Universe.deltaTime;
		while (localTime >= CYCLE_TIME) {
			localTime -= CYCLE_TIME;
			cyclePos++;
			cyclePos %= seqLength;
		}
	}
	
	protected void delete () {
		Universe.attacks.remove(this);
	}
}
