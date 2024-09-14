package entities.items;

import java.awt.image.BufferedImage;
import java.io.IOException;

import entities.Entity;
import world.FileGetter;
import world.Universe;

public abstract class Item extends Entity {
	//Changes to be made in Main, Room, Universe when adding new items
	private int seqLength;
	private int cyclePos;
	private double localTime;
	final private double CYCLE_TIME;
	
	String directory;
	public Item(int xPos, int yPos, int wd, int ht, String dir, double cyTime) throws IOException {
		super(xPos, yPos, wd, ht);
		
		directory = dir;
		
		seqLength = 4;
		cyclePos = 0;
		localTime = 0;
		CYCLE_TIME = cyTime;
		
		sequence = new BufferedImage[1][seqLength];
		
		getSeq();
	}
	
	@Override
	public BufferedImage getImg () {
		return sequence[0][cyclePos];
	}

	@Override
	protected void getSeq() throws IOException {
		for (int i = 0; i < seqLength; i++) {
			sequence[0][i] = FileGetter.getImage(directory+"/"+i+".png");
		}
	}

	public void update () {
		localTime += Universe.deltaTime;
		while (localTime >= CYCLE_TIME) {
			localTime -= CYCLE_TIME;
			cyclePos++;
			cyclePos %= seqLength;
		}
	}
}
