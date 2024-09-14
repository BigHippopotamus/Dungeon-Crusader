package entities.items;

import java.io.IOException;

import world.Data;
public class BlueKey extends Item {
	public BlueKey (int xPos, int yPos) throws IOException {
		super(xPos, yPos, Data.BITS, Data.BITS, "Assets/Items/Blue Key", 0.1);
	}
}
