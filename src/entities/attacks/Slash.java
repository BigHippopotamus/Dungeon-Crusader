package entities.attacks;

import java.awt.image.BufferedImage;
import java.io.IOException;

import world.Data;
import world.FileGetter;
import world.Universe;

public class Slash extends Attack {

	private int dir;

	public Slash (int[] pos, int direction, boolean enemy) throws IOException {
		super(pos, Data.BITS, Data.BITS, 4, 0.075, enemy);

		dir = direction;

		getSeq();
	}
	
	public static Attack createNew (int[] pos, int direction, boolean enemy) {
		Attack atk = null;
		try {
			atk = new Slash(pos, direction, enemy);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return atk;
	}

	@Override
	protected void getSeq () throws IOException {
		sequence = new BufferedImage[1][seqLength];

		for (int i = 0; i < 4; i++) {
			sequence[0][i] = FileGetter.getImage("Assets/Attacks/Slash/"+dir+"/"+i+".png");
		}
	}

	@Override
	public BufferedImage getImg () {
		cycle();		
		if (cyclePos < seqLength) return sequence[0][cyclePos];
		else return null;
	}
	
	@Override
	protected void cycle () {
		localTime += Universe.deltaTime;
		while (localTime >= CYCLE_TIME) {
			localTime -= CYCLE_TIME;
			cyclePos++;
			if (cyclePos >= seqLength) delete();
		}
	}
}
