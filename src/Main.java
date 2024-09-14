import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URISyntaxException;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JPanel;

import audio.MusicSystem;
import audio.SoundSystem;
import entities.Entity;
import entities.attacks.Attack;
import entities.creatures.Player;
import entities.creatures.enemies.Enemy;
import entities.items.Item;
import world.*;

public class Main extends JPanel implements KeyListener {
	private static final long serialVersionUID = 1L;

	int roomX, roomY;

	Player player;
	Attack plyrSlash;
	
	MapMaker mapMaker;

	Thread gameLoop;
	long oldTime, newTime;

	public Main () {
		super();

		try {
			Universe.mus = new MusicSystem();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (UnsupportedAudioFileException e) {
			e.printStackTrace();
		} catch (LineUnavailableException e) {
			e.printStackTrace();
		}

		WorldBuilder wb = new WorldBuilder();

		try {
			int[] start = wb.createMap(Universe.map);
			roomX = start[0];
			roomY = start[1];
			Universe.map.get(roomY).get(roomX).createRoom();
			
			mapMaker = new MapMaker();
			setRoom();
			player = new Player();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}

		oldTime = newTime = 0;

		gameLoop = new Thread() {
			public void run () {
				while (Universe.playing) {
					newTime = System.nanoTime();
					Universe.deltaTime = (newTime - oldTime)/1E9; //The time between frames in seconds
					//System.out.println(Universe.deltaTime);
					oldTime = newTime;

					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

					repaint();
				}

				try {
					Universe.mus.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};

		gameLoop.start();

		addKeyListener(this);
		setFocusable(true);
	}

	@Override
	public void paintComponent (Graphics g) {
		super.paintComponent(g);

		BufferedImage screen = draw();
		g.drawImage(screen, 0, 0, Data.S_WIDTH, Data.S_HEIGHT, null);

		if (!Universe.inCutscene) calc();
	}

	public BufferedImage draw () {
		BufferedImage curRoom = new BufferedImage(Data.WIDTH, Data.HEIGHT, BufferedImage.TYPE_3BYTE_BGR);
		Graphics2D g = curRoom.createGraphics();

		g.drawImage(Universe.curRoom.getRoom(), 0, 0, null);

		Item item = Universe.getItem();
		if (item != null) {
			g.drawImage(item.getImg(), item.getX() - item.getWidth()/2, item.getY() - item.getHeight()/2, item.getWidth(), item.getHeight(), null);
			
			try {
				collect(item);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (LineUnavailableException e) {
				e.printStackTrace();
			}
		}
		
		Enemy[] enemies = Universe.getEnemies();
		for (Enemy en : enemies) {
			g.drawImage(en.getImg(), en.getX() - en.getWidth()/2, en.getY() - en.getHeight()/2, en.getWidth(), en.getHeight(), null);
		}
		
		g.drawImage(player.getImg(), player.getX() - player.getWidth()/2, player.getY() - player.getHeight()/2, player.getWidth(), player.getHeight(), null);
		
		Attack[] attacks = Universe.getAttacks();
		for (Attack atk : attacks) {
			g.drawImage(atk.getImg(), atk.getX() - atk.getWidth()/2, atk.getY() - atk.getHeight()/2, atk.getWidth(), atk.getHeight(), null);
		}
		
		Composite comp = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.6f);
		g.setComposite(comp);
		
		BufferedImage minimap = mapMaker.getMap(roomX, roomY);
		g.drawImage(minimap, 0, 0, null);

		return curRoom;
	}

	public void collect (Item item) throws IOException, LineUnavailableException {
		boolean touching = Entity.colliding(player, item);
		if (touching) {
			switch (Universe.roomType) {
			case 3:
				Universe.lavaBoots = true;
				break;

			case 4:
				Universe.blueKey = true;
				break;

			case 6:
				Universe.playing = false;
				break;

			case 7:
				Universe.greenKey = true;
				break;
			}
			SoundSystem.playSound(SoundSystem.Sounds.ITEM_COLLECT);
		}
	}

	public void calc () {
		//Character movements
		player.move();

		if (!player.checkBounds()) {
			if (player.getX() > Data.WIDTH) {
				roomX++;
				player.setX(0);
			}
			if (player.getY() > Data.HEIGHT) {
				roomY++;
				player.setY(0);
			}
			if (player.getX() < 0) {
				roomX--;
				player.setX(Data.WIDTH);
			}
			if (player.getY() < 0) {
				roomY--;
				player.setY(Data.HEIGHT);
			}
			setRoom();
		}
		
		Enemy[] enemies = Universe.getEnemies();
		for (Enemy en : enemies) {
			en.move();
			if (!player.isInvinc() && Entity.colliding(player, en)) {
				player.damage(1);
				player.knockBack(en);
			}
		}
	}

	private void setRoom () {
		try {
			Room t = Universe.map.get(roomY).get(roomX);
			Universe.updateRoom(t);
			mapMaker.drawRoom(roomX, roomY);
			new RoomGen(roomX, roomY).start();
		} catch (IndexOutOfBoundsException e) {
			System.out.println("Invalid location");
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {}

	@Override
	public void keyPressed(KeyEvent e) {
		int c = e.getKeyCode();
		if (c == KeyEvent.VK_RIGHT) Universe.movement.addDir(0);
		else if (c == KeyEvent.VK_DOWN) Universe.movement.addDir(1);
		else if (c == KeyEvent.VK_LEFT) Universe.movement.addDir(2);
		else if (c == KeyEvent.VK_UP) Universe.movement.addDir(3);
		else if (c == KeyEvent.VK_X) player.attack();
	}

	@Override
	public void keyReleased(KeyEvent e) {
		int c = e.getKeyCode();
		if (c == KeyEvent.VK_RIGHT) Universe.movement.removeDir(0);
		else if (c == KeyEvent.VK_DOWN) Universe.movement.removeDir(1);
		else if (c == KeyEvent.VK_LEFT) Universe.movement.removeDir(2);
		else if (c == KeyEvent.VK_UP) Universe.movement.removeDir(3);
	}
}