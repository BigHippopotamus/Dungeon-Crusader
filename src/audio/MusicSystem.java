package audio;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import world.FileGetter;

import java.io.IOException;

public class MusicSystem {
	private AudioInputStream aud;
	private Clip bgm;
	public MusicSystem () throws IOException, UnsupportedAudioFileException, LineUnavailableException {
		aud = FileGetter.getAudio("Assets/Audio/BGM/0.wav");
		bgm = AudioSystem.getClip();
		bgm.open(aud);
	}
	
	public void start () {
		if (!bgm.isRunning()) bgm.loop(Clip.LOOP_CONTINUOUSLY);
	}
	
	public void stop () {
		if (bgm.isRunning()) bgm.stop();
	}
	
	public void close () throws IOException {
		bgm.stop();
		aud.close();
		bgm.close();
	}
}
