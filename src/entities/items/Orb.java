package entities.items;

import java.io.IOException;

import world.Data;

public class Orb extends Item {
	public Orb (int xPos, int yPos) throws IOException {
		super(xPos, yPos, Data.BITS, Data.BITS, "Assets/Items/Orb", 0.1);
	}
}