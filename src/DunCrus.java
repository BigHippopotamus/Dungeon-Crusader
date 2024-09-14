import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.JPanel;

import world.Data;
import world.FileGetter;
import world.Universe;
public class DunCrus implements WindowListener {
	JFrame frame;
	public static void main (String[] args) {
		try {
			new DunCrus();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public DunCrus () throws Exception {
		frame = new JFrame("Dungeon Crusader");
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		JPanel pane = new Main();
		pane.setPreferredSize(new Dimension(Data.S_WIDTH, Data.S_HEIGHT));
		frame.getContentPane().add(pane, BorderLayout.CENTER);
		frame.setResizable(false);
		frame.pack();
		frame.addWindowListener(this);
		
		BufferedImage img = FileGetter.getImage("Assets/Icon.png");
		frame.setIconImage(img);
		
		frame.setVisible(true);
	}

	public void windowClosing (WindowEvent w) {
		Universe.playing = false;
	}

	public void windowDeactivated (WindowEvent w) {}

	public void windowActivated (WindowEvent w) {}

	public void windowDeiconified (WindowEvent w) {}

	public void windowIconified (WindowEvent w) {}

	public void windowOpened (WindowEvent w) {}

	public void windowClosed (WindowEvent w) {}
}