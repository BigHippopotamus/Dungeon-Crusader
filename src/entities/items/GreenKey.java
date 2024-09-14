package entities.items;

import java.io.IOException;

import world.Data;

public class GreenKey extends Item {
	public GreenKey (int xPos, int yPos) throws IOException {
		super(xPos, yPos, Data.BITS, Data.BITS, "Assets/Items/Green Key", 0.1);
	}
}
