package world;

public interface Data {
	final public static int P_WIDTH = 21, P_HEIGHT = 15;
	final public static int BITS = 16;
	final public static int WIDTH = P_WIDTH*BITS, HEIGHT = P_HEIGHT*BITS;
	final public static int SIZE = Universe.getSize();
	final public static int S_WIDTH = WIDTH*SIZE, S_HEIGHT = HEIGHT*SIZE;
}