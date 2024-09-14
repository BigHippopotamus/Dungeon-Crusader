package entities.creatures;

import java.awt.image.BufferedImage;
import java.io.IOException;

import entities.Entity;
import entities.attacks.Slash;
import world.*;
public class Player extends Creature {
	private int seqLength;
	private int cyclePos;
	private double localTime;
	final private double WALK_CYCLE_TIME, SWORD_CYCLE_TIME, INVINC_CYCLE_TIME;

	private boolean attacking; //If the character is attacking
	
	int knockback; //The direction of the knockback
	int kbDist; //How far the character is getting knocked back
	
	double invincTimer; //How long the character is invincible for
	double invincBuffer; //Time to the next "flash" during invincibility
	boolean invincFrame; //Whether the character is visible or not during invincibility frames
	
	public Player () throws IOException {
		super(10, Data.WIDTH/2, Data.HEIGHT/2, 80, Data.BITS, Data.BITS, Data.BITS*14/16, Data.BITS*14/16);

		seqLength = 4;
		cyclePos = 0;
		localTime = 0;
		WALK_CYCLE_TIME = 0.15;
		SWORD_CYCLE_TIME = 0.075;
		INVINC_CYCLE_TIME = 0.1;
		
		knockback = -1;
		kbDist = 0;
		
		invincTimer = 0;
		invincFrame = true;

		getSeq();
	}

	@Override
	protected void getSeq () throws IOException {
		sequence = new BufferedImage[8][seqLength];

		//Walking animation
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < seqLength; j++) {
				sequence[i][j] = FileGetter.getImage("Assets/Player/Walk/"+i+"/"+j+".png");
			}
		}

		//Sword animation
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < seqLength; j++) {
				sequence[i+4][j] = FileGetter.getImage("Assets/Player/Sword/"+i+"/"+j+".png");
			}
		}
	}

	@Override
	public BufferedImage getImg () {
		if (attacking) {
			localTime += Universe.deltaTime;
			while (localTime >= SWORD_CYCLE_TIME) {
				localTime -= SWORD_CYCLE_TIME;
				cyclePos++;
				if (cyclePos >= 4) {
					attacking = false;
					cyclePos = 0;
				}
			}
		}

		//Makes the character flash while invincible
		if (invincTimer > 0) {
			invincTimer -= Math.min(invincTimer, Universe.deltaTime);
			
			invincBuffer += Universe.deltaTime;
			int temp = (int)(invincBuffer/INVINC_CYCLE_TIME);
			invincBuffer %= INVINC_CYCLE_TIME;
			
			invincFrame = (temp%2==0)?invincFrame:!invincFrame;
			
			if (invincFrame) return null;
		}
		
		if (attacking) {	
			BufferedImage img = sequence[dir+4][cyclePos];
			return img;
		}
		else {
			BufferedImage img = sequence[dir][cyclePos];
			return img;
		}
	}

	public void attack () {
		if (!attacking && Universe.sword && !Universe.inCutscene) {
			cyclePos = 0;
			localTime = 0;
			attacking = true;
			
			Universe.attacks.add(Slash.createNew(Calculate.getFacing(getX(), getY(), dir), dir, false));
		}
	}

	@Override
	public void move () {
		int direction = Universe.movement.getTop();
		
		if (direction != -1 && !attacking && knockback == -1) {
			if (direction != dir) {
				cyclePos = 0;
				localTime = 0;
			}
			localTime += Universe.deltaTime;
			while (localTime >= WALK_CYCLE_TIME) {
				localTime -= WALK_CYCLE_TIME;
				cyclePos++;
				cyclePos %= seqLength;
			}

			double speedConst = Universe.deltaTime*movementSpeed;
			if (dir%2 != direction %2) {
				if (dir%2 != 0) {
					double newY = Math.round((y/Data.BITS)*2);
					newY *= Data.BITS/2;
					double dif = newY-y;
					if (dif < 0) {
						dir = 3;
						y += Math.max(dif, -speedConst);
					}
					else if (dif > 0) {
						dir = 1;
						y += Math.min(dif, speedConst);
					}
					else {
						dir = direction;
					}
				}
				else {
					double newX = Math.round((x/Data.BITS)*2);
					newX *= Data.BITS/2;
					double dif = newX-x;
					if (dif < 0) {
						dir = 2;
						x += Math.max(dif, -speedConst);
					}
					else if (dif > 0) {
						dir = 0;
						x += Math.min(dif, speedConst);
					}
					else {
						dir = direction;
					}
				}
			}
			else {
				dir = direction;
				if (direction == 0) x += speedConst;
				else if (direction == 1) y += speedConst;
				else if (direction == 2) x -= speedConst;
				else if (direction == 3) y -= speedConst;
			}
		}
		else if (knockback != -1) {
			double speedConst = 3*Universe.deltaTime*movementSpeed;
			double moveDist = Math.min(speedConst, kbDist);
			
			if (knockback == 0) x += moveDist;
			else if (knockback == 1) y += moveDist;
			else if (knockback == 2) x -= moveDist;
			else if (knockback == 3) y -= moveDist;
			
			kbDist -= moveDist;
			if (kbDist <= 0) knockback = -1;
		}
		adjust();
	}

	private void adjust () {
		int arX = (int)(x/Data.BITS);
		int arY = (int)(y/Data.BITS);
		int[][] roomMap = Universe.curRoom.getMap();
		for (int i = arY-1; i <= arY+1; i++) {
			for (int j = arX-1; j <= arX+1; j++) {
				if (i < 0 || j < 0 || i >= roomMap.length || j >= roomMap[0].length) continue; //If i or j is out of bounds

				int tile = roomMap[i][j];
				boolean walkable = (tile == 1 && Universe.lavaBoots) || (tile == 2) || (tile == 3); //If the player can walk on the tile

				if (!walkable && y - getHeight()/2 < i*Data.BITS + Data.BITS && y + getHeight()/2 > i*Data.BITS && x + getWidth()/2 > j*Data.BITS && x - getWidth()/2 < j*Data.BITS + Data.BITS) {
					if (tile == 4 && Universe.blueKey) Universe.openBlue(j, i);
					if (tile == 5 && Universe.greenKey) Universe.openGreen(j, i);

					if (dir%2 == 0) {
						if (x > j*Data.BITS) {
							while (x - getWidth()/2 <= j*Data.BITS + Data.BITS) x++;
						}

						if (x < j*Data.BITS + Data.BITS) {
							while (x + getWidth()/2 > j*Data.BITS) x--;
							x++;
						}
					}
					else {
						if (y > i*Data.BITS) {
							while (y - getHeight()/2 <= i*Data.BITS + Data.BITS) y++;
						}

						if (y < i*Data.BITS + Data.BITS) {
							while (y + getHeight()/2 > i*Data.BITS) y--;
							y++;
						}
					}
				}
			}
		}
	}
	
	public void damage(int amt) {
		hp -= amt;
		invincTimer = 1;
	}

	public void knockBack (Entity en) {
		double xDis = Math.abs(x - en.getX());
		double yDis = Math.abs(y - en.getY());
		
		if (xDis > yDis) {
			if (x > en.getX()) knockback = 0;
			else knockback = 2;
		}
		else {
			if (y > en.getY()) knockback = 1;
			else knockback = 3;
		}
		
		kbDist = 2*Data.BITS;
	}

	public boolean isInvinc () {
		return (invincTimer > 0);
	}
}
