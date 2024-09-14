package world;

public class Calculate {
	public static int[] getFacing(int x, int y, int dir) {
		int[] coords = {x, y};
		
		if (dir == 0) coords[0] = x + Data.BITS;
		else if (dir == 1) coords[1] = y + Data.BITS;
		else if (dir == 2) coords[0] = x - Data.BITS;
		else if (dir == 3) coords[1] = y - Data.BITS;
		
		return coords;
	}
}
