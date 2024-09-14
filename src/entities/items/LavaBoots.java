package entities.items;

import java.io.IOException;

import world.Data;

public class LavaBoots extends Item {
	public LavaBoots (int xPos, int yPos) throws IOException {
		super(xPos, yPos, Data.BITS, Data.BITS, "Assets/Items/Lava Boots", 0.1);
	}
}