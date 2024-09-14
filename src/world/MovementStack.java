package world;

public class MovementStack {
	int[] stack;
	int top;
	public MovementStack () {
		top = -1;
		stack = new int[4];
		for (int i = 0; i < 4; i++) stack[i] = -1;
	}
	
	public void addDir (int d) {
		int pos = indexOf(d);
		if (pos == -1) {
			stack[++top] = d;
		}
		else {
			shift(pos);
			stack[top] = d;
		}
	}
	
	public void removeDir (int d) {
		int pos = indexOf(d);
		if (pos != -1) {
			shift(pos);
			stack[top--] = -1;
		}
	}
	
	public int getTop () {
		if (top == -1) return -1;
		else return stack[top];
	}
	
	private int indexOf (int d) {
		for (int i = 0; i <= top; i++) {
			if (stack[i] == d) {
				return i;
			}
		}
		return -1;
	}
	
	private void shift (int pos) {
		for (int i = pos; i < top; i++) {
			stack[i] = stack[i+1];
		}
	}
}
