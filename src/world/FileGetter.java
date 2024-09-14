package world;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
public class FileGetter {
	public static BufferedImage getImage (String filename) throws IOException, IllegalArgumentException {
		InputStream is = Universe.class.getClassLoader().getResourceAsStream(filename);
		BufferedImage img = (BufferedImage) ImageIO.read(is);
		is.close();
		return img;
	}
	
	public static AudioInputStream getAudio (String filename) throws IOException, UnsupportedAudioFileException {
		URL url = Universe.class.getClassLoader().getResource(filename);
		AudioInputStream ais = AudioSystem.getAudioInputStream(url);
		return ais;
	}
}
