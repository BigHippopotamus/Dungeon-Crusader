package audio;

import java.io.IOException;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import world.FileGetter;

public class SoundSystem {
	public static void playSound (Sounds sound) throws IOException, LineUnavailableException {
		AudioInputStream aud = null;
		String pathName = "Assets/Audio/SFX/";
		try {
			switch (sound) {
			case DOOR_OPEN:
				aud = FileGetter.getAudio(pathName + "door_opening.wav");
				break;
				
			case KEY_INSERT:
					aud = FileGetter.getAudio(pathName + "key_insert.wav");
					break;
					
			case ITEM_COLLECT:
					aud = FileGetter.getAudio(pathName + "item_collect.wav");
					break;
			}
		} catch (IOException | UnsupportedAudioFileException e) {
			e.printStackTrace();
		}
		
		Clip clip = AudioSystem.getClip();
		clip.open(aud);
		
		new Thread () {
			public void run () {
				clip.addLineListener(new LineListener() {
					public void update(LineEvent event) {
						if (event.getType().equals(LineEvent.Type.STOP)) clip.close();
					}
				});
				
				clip.start();
			}
		}.start();
	}
	
	public enum Sounds {
		DOOR_OPEN, KEY_INSERT, ITEM_COLLECT;
	}
}
