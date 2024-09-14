package entities.creatures.enemies;

import java.awt.image.BufferedImage;
import java.io.IOException;

import world.Data;
import world.FileGetter;
import world.Universe;

public class Sparky extends Enemy {
	private int seqLength;
	private int cyclePos;
	private double localTime;
	final private double WALK_CYCLE_TIME;
	
	public Sparky (int health, int xPos, int yPos) throws IOException {
		super(health, xPos, yPos, 0, Data.BITS, Data.BITS, Data.BITS*14/16, Data.BITS*14/16);

		seqLength = 2;
		cyclePos = 0;
		localTime = 0;
		WALK_CYCLE_TIME = 0.1;
		
		getSeq();
	}

	@Override
	public void move() {
		//Enemy is stationary
		
		localTime += Universe.deltaTime;
		while (localTime >= WALK_CYCLE_TIME) {
			localTime -= WALK_CYCLE_TIME;
			cyclePos++;
			cyclePos %= seqLength;
		}
	}

	@Override
	protected void getSeq() throws IOException {
		sequence = new BufferedImage[1][seqLength];
		sequence[0][0] = FileGetter.getImage("Assets/Enemies/Sparky/0.png");
		sequence[0][1] = FileGetter.getImage("Assets/Enemies/Sparky/1.png");
	}

	@Override
	public BufferedImage getImg() {
		return sequence[0][cyclePos];
	}
}