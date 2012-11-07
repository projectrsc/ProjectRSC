package org.rscangel.client;

import java.applet.Applet;
import java.awt.Color;
import java.awt.Event;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.IndexColorModel;
import java.awt.image.MemoryImageSource;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;

import javax.imageio.ImageIO;

import org.rscangel.client.bz2.DataFileDecrypter;
import org.rscangel.client.gfx.JagGraphics;
import org.rscangel.client.util.Config;
import org.rscangel.client.util.SHA1Checksum;

public class GameWindow extends Applet implements Runnable {

	private static final long serialVersionUID = -8976121820127349733L;

	public static final Font UPDATE_FONT = new Font("Helvetica", 1, 13);

	public static final Font LOADING_FONT = new Font("TimesRoman", 0, 15);
	
	public static final Font INFO_FONT = new Font("Helvetica", 1, 13);

	private final SHA1Checksum sha1 = new SHA1Checksum();

	private Image updateImage, jagexLogo;

	private volatile boolean checkingForUpdates = true;

	public GameWindow() {
		appletWidth = 512;
		appletHeight = 384;
		threadSleepModifier = 20;
		anInt5 = 1000;
		currentTimeArray = new long[10];
		loadingScreen = 1;
		loadingBarText = "";
		keyLeftBraceDown = false;
		keyRightBraceDown = false;
		keyLeftDown = false;
		keyRightDown = false;
		keyUpDown = false;
		keyDownDown = false;
		keySpaceDown = false;
		keyDelDown = false;
		keyNMDown = false;
		threadSleepTime = 1;
		keyF1Toggle = false;
		f2Toggle = false;
		inputText = "";
		enteredText = "";
		inputMessage = "";
		enteredMessage = "";
		try {
			URL url = new URL("http://world" + System.getProperty("world", "1") + ".projectrsc.com/updated.jpg");
			updateImage = ImageIO.read(url);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void startGame() {}

	protected synchronized void tick() {}

	protected void logoutAndStop() {}

	protected synchronized void method4() {}

	protected final void createWindow(int width, int height, String title,
			boolean resizable) {
		appletMode = false;
		appletWidth = width;
		appletHeight = height;
		gameFrame = new GameFrame(this, width, height, title, resizable, false);
		loadingScreen = 1;
		gameWindowThread = new Thread(this);
		gameWindowThread.start();
		gameWindowThread.setPriority(1);
	}
	
	protected final void setRefreshRate(int i) {
		threadSleepModifier = 1000 / i;
	}

	protected final void resetCurrentTimeArray() {
		for (int i = 0; i < 10; i++) {
			currentTimeArray[i] = 0L;
		}
	}

	public final synchronized boolean keyDown(Event event, int key) {
        handleMenuKeyDown(key);

        if(key == 1006)
            keyLeftDown = true;
        if(key == 1007)
            keyRightDown = true;
        if(key == 1004)
            keyUpDown = true;
        if(key == 1005)
            keyDownDown = true;
        if(key == KeyEvent.VK_SPACE)
            keySpaceDown = true;
        if(key == KeyEvent.VK_N || key == KeyEvent.VK_M)
            keyNMDown = true;
        if(key == 1008)
            keyF1Toggle = !keyF1Toggle;
        
		boolean validKeyDown = false;
		for (int j = 0; j < charSet.length(); j++) {
			if (key != charSet.charAt(j))
				continue;

			validKeyDown = true;
			break;
		}

		if (validKeyDown && inputText.length() < 20)
			inputText += (char) key;
		if (validKeyDown && inputMessage.length() < 80)
			inputMessage += (char) key;
		if (key == 8 && inputText.length() > 0) // backspace
			inputText = inputText.substring(0, inputText.length() - 1);
		if (key == 8 && inputMessage.length() > 0) // backspace
			inputMessage = inputMessage.substring(0, inputMessage.length() - 1);
		if (key == 10 || key == 13) { // enter/return
			enteredText = inputText;
			enteredMessage = inputMessage;
		}
		return true;
	}

	protected void handleMenuKeyDown(int key) {
	}

	public final synchronized boolean keyUp(Event event, int key) {
        if(key == 1006)
            keyLeftDown = false;
        if(key == 1007)
            keyRightDown = false;
        if(key == 1004)
            keyUpDown = false;
        if(key == 1005)
            keyDownDown = false;
        if(key == KeyEvent.VK_SPACE)
            keySpaceDown = false;
        if(key == KeyEvent.VK_N || key == KeyEvent.VK_M)
            keyNMDown = false;
        return true;
	}

	public final synchronized boolean mouseMove(Event event, int i, int j) {
		mouseX = i;
		mouseY = j + yOffset;
		mouseButton = 0;
		lastActionTimeout = 0;
		return true;
	}

	public final synchronized boolean mouseUp(Event event, int i, int j) {
		mouseX = i;
		mouseY = j + yOffset;
		mouseButton = 0;
		return true;
	}

	public final synchronized boolean mouseDown(Event event, int i, int j) {
		mouseX = i;
		mouseY = j + yOffset;
		mouseButton = event.metaDown() ? 2 : 1;
		lastMouseButton = mouseButton;
		lastActionTimeout = 0;
		handleMouseDown(mouseButton, i, j);
		return true;
	}

	protected void handleMouseDown(int button, int x, int y) {
	}

	public final synchronized boolean mouseDrag(Event event, int i, int j) {
		mouseX = i;
		mouseY = j + yOffset;
		mouseButton = event.metaDown() ? 2 : 1;
		return true;
	}

	protected void handleMouseDrag(MouseEvent mouse, int x, int y, int button) {
	}

	public final void init() {
		appletMode = true;
		System.out.println("Started applet");
		Config.initConfig();

		appletWidth = 512;
		appletHeight = 344;
		loadingScreen = 1;
		startThread(this);
	}

	public final void start() {
		if (exitTimeout >= 0) {
			exitTimeout = 0;
		}
	}

	public final void stop() {
		if (exitTimeout >= 0) {
			exitTimeout = 4000 / threadSleepModifier;
		}
	}

	@SuppressWarnings("deprecation")
	public final void destroy() {
		exitTimeout = -1;
		try {
			Thread.sleep(2000L);
		} catch (Exception e) {
		}
		if (exitTimeout == -1) {
			System.out.println("2 seconds expired, forcing kill");
			close();
			if (gameWindowThread != null) {
				gameWindowThread.stop();
				gameWindowThread = null;
			}
		}
	}

	private final void close() {
		exitTimeout = -2;
		System.out.println("Closing program");
		logoutAndStop();
		try {
			Thread.sleep(1000L);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (gameFrame != null) {
			gameFrame.dispose();
		}
		System.exit(0);
	}

	private void loadFonts() {
		byte jagexArchive[] = unpackData("jagex.jag", "Jagex library", 0);

		byte[] logo = DataOperations.loadData("logo.tga", 0, jagexArchive);

		if (logo != null) {
			jagexLogo = createLogo(logo);
		} else {
			System.out.println("null logo!");
		}
		
		JagGraphics.addFont(DataOperations.loadData("h11p.jf", 0, jagexArchive));
		JagGraphics.addFont(DataOperations.loadData("h12b.jf", 0, jagexArchive));
		JagGraphics.addFont(DataOperations.loadData("h12p.jf", 0, jagexArchive));
		JagGraphics.addFont(DataOperations.loadData("h13b.jf", 0, jagexArchive));
		JagGraphics.addFont(DataOperations.loadData("h14b.jf", 0, jagexArchive));
		JagGraphics.addFont(DataOperations.loadData("h16b.jf", 0, jagexArchive));
		JagGraphics.addFont(DataOperations.loadData("h20b.jf", 0, jagexArchive));
		JagGraphics.addFont(DataOperations.loadData("h24b.jf", 0, jagexArchive));
	}
	
    public Image createLogo(byte[] data) {
        int width = data[13] * 256 + data[12];
        int height = data[15] * 256 + data[14];
        
        byte r[] = new byte[256];
        byte g[] = new byte[256];
        byte b[] = new byte[256];
        
        for(int i1 = 0; i1 < 256; i1++) {
            r[i1] = data[20 + i1 * 3];
            g[i1] = data[19 + i1 * 3];
            b[i1] = data[18 + i1 * 3];
        }

        IndexColorModel indexcolormodel = new IndexColorModel(8, 256, r, g, b);
      
        byte pixels[] = new byte[width * height];
        int pixel = 0;
        
        for(int y = height - 1; y >= 0; y--) {
            for(int x = 0; x < width; x++) {
                pixels[pixel++] = data[786 + x + y * width];
            }
        }

        MemoryImageSource memoryimagesource = new MemoryImageSource(width, height, indexcolormodel, pixels, 0, width);
        Image image = createImage(memoryimagesource);
        return image;
    }

	private void checkForUpdates() {
		drawLoadingBarText(0, "Connecting to update server");
		Runnable updateThread = new Runnable() {
			@Override
			public void run() {
				try {
					StringBuilder contents = new StringBuilder();
					URL url = new URL("http://world" + System.getProperty("world", "1") + ".projectrsc.com/update.php");
					URLConnection connection = url.openConnection();
					BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
					String inputLine;
					while ((inputLine = in.readLine()) != null) {
						contents.append(inputLine);
					}
					in.close();

					String[] entries = contents.toString().split(";");

					for (int i = 0; i < entries.length; ++i) {
						int colonIndex = entries[i].indexOf(':');
						String name = entries[i].substring(0, colonIndex).replaceAll(" ", "");
						String hash = entries[i].substring(colonIndex + 1, entries[i].length());

						File file = new File(Config.CONF_DIR + File.separator + name);

						if (file.exists()) {
							String ourHash = sha1.getChecksum(file);

							if (!hash.equalsIgnoreCase(ourHash)) {
								drawLoadingBarText((i / entries.length) * 100, "Downloading " + name);
								download(Config.CONF_DIR + File.separator + name, "http://world" + System.getProperty("world", "1") + ".projectrsc.com/client/" + name);
							}
						} else { // we don't have the file...
							file.getParentFile().mkdirs();
							drawLoadingBarText((i / entries.length) * 100, "Downloading " + name);
							download(Config.CONF_DIR + File.separator + name, "http://world" + System.getProperty("world", "1") + ".projectrsc.com/client/" + name);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				checkingForUpdates = false;
			}
		};
		startThread(updateThread);
	}

	private void download(String filename, String urlString) {
		try {
			BufferedInputStream in = null;
			FileOutputStream fout = null;
			try {
				in = new BufferedInputStream(new URL(urlString).openStream());
				fout = new FileOutputStream(filename);

				byte data[] = new byte[1024];
				int count;
				while ((count = in.read(data, 0, 1024)) != -1)
				{
					fout.write(data, 0, count);
				}
			} finally {
				if (in != null)
					in.close();
				if (fout != null)
					fout.close();
			}
		} catch (Exception e) {}
	}

	public final void run() {
		if (loadingScreen == 1) {
			loadingScreen = 2;
			graphics = getGraphics();
			drawLoadingLogo();
			drawLoadingBarText(0, "Loading...");
			startGame();
			loadingScreen = 0;
		}

		int i = 0;
		int j = 256;
		int sleepTime = 1;
		int i1 = 0;
		for (int j1 = 0; j1 < 10; j1++)
			currentTimeArray[j1] = System.currentTimeMillis();

		while (exitTimeout >= 0) {
			if (exitTimeout > 0) {
				exitTimeout--;
				if (exitTimeout == 0) {
					close();
					gameWindowThread = null;
					return;
				}
			}
			int k1 = j;
			int i2 = sleepTime;
			j = 300;
			sleepTime = 1;
			long l1 = System.currentTimeMillis();
			if (currentTimeArray[i] == 0L) {
				j = k1;
				sleepTime = i2;
			} else if (l1 > currentTimeArray[i])
				j = (int) ((long) (2560 * threadSleepModifier) / (l1 - currentTimeArray[i]));
			if (j < 25)
				j = 25;
			if (j > 256) {
				j = 256;
				sleepTime = (int) ((long) threadSleepModifier - (l1 - currentTimeArray[i]) / 10L);
				try {
					if(sleepTime > 60) sleepTime = 60;
					Thread.sleep(sleepTime);
				} catch (InterruptedException _ex) {
					if (sleepTime < threadSleepTime)
						sleepTime = threadSleepTime;
				}
			}
			try {
				Thread.sleep(sleepTime);
			} catch (InterruptedException _ex) {
			}
			currentTimeArray[i] = l1;
			i = (i + 1) % 10;
			if (sleepTime > 1) {
				for (int j2 = 0; j2 < 10; j2++)
					if (currentTimeArray[j2] != 0L)
						currentTimeArray[j2] += sleepTime;

			}
			int k2 = 0;
			while (i1 < 256) {
				tick();
				i1 += j;
				if (++k2 > anInt5) {
					i1 = 0;
					anInt10 += 6;
					if (anInt10 > 25) {
						anInt10 = 0;
						keyF1Toggle = true;
						f2Toggle = false;
					}
					break;
				}
			}
			anInt10--;
			i1 &= 0xff;
			method4();
		}
		if (exitTimeout == -1)
			close();
		gameWindowThread = null;
	}

	public final void update(Graphics g) {
		paint(g);
	}

	public final void paint(Graphics g) {
		if (loadingScreen == 2 && jagexLogo != null) {
			drawLoadingBarText(loadingBarPercent, loadingBarText);
		}
	}

	private final void drawLoadingLogo() {
		graphics.setColor(Color.black);

		if (!Config.SERVER_IP.equalsIgnoreCase("localhost")) { // if we're not running locally!
			checkForUpdates();
			do {} while (checkingForUpdates); // wait while the update the thread is running
		}

		loadFonts();
	}

	public final void drawLoadingBarText(int percent, String text) {
		try {
			int x = (appletWidth - 281) / 2;
			int y = (appletHeight - 148) / 2;

			if (checkingForUpdates && updateImage != null) {
				graphics.drawImage(updateImage, 0, 0, null);
			} else  {
				graphics.setColor(Color.BLACK);
				graphics.fillRect(0, 0, appletWidth, appletHeight);
				
				if (!checkingForUpdates) {
					graphics.drawImage(jagexLogo, x, y - 5, null);
				}
			}
			
			x += 2;
			y += 90;

			loadingBarPercent = percent;
			loadingBarText = text;
			int barWidth = (277 * percent) / 100;
			if (checkingForUpdates) {
				graphics.setColor(new Color(170, 0, 25));
				graphics.drawRect(x - 2, y - 2, 280, 23);
				graphics.fillRect(x, y, barWidth, 20);
				graphics.setColor(Color.black);
				graphics.fillRect(x + barWidth, y, 277 - barWidth, 20);
				graphics.setColor(Color.WHITE);
				drawString(graphics, "RuneScape has been updated!", UPDATE_FONT, x + 138, y - 60);
				drawString(graphics, "Please wait - Fetching new files...", UPDATE_FONT, x + 138, y - 45);
			} else { 
				graphics.setColor(new Color(132, 132, 132));
				graphics.drawRect(x - 2, y - 2, 280, 23);
				graphics.fillRect(x, y, barWidth, 20);
				graphics.setColor(Color.black);
				graphics.fillRect(x + barWidth, y, 277 - barWidth, 20);
				graphics.setColor(new Color(198, 198, 198));
				
				drawString(graphics, "Created by JAGeX - visit www.jagex.com", INFO_FONT, x + 138, y + 30);
				drawString(graphics, "Copyright \2512000 Andrew Gower", INFO_FONT, x + 138, y + 44);
			}
			drawString(graphics, text, LOADING_FONT, x + 138, y + 10);
			return;
		}
		catch(Exception _ex) {
			return;
		}
	}

	protected final void drawString(Graphics g, String s, Font font, int i,
			int j) {
		FontMetrics fontmetrics = (gameFrame == null ? this : gameFrame)
				.getFontMetrics(font);
		fontmetrics.stringWidth(s);
		g.setFont(font);
		g.drawString(s, i - fontmetrics.stringWidth(s) / 2, j
				+ fontmetrics.getHeight() / 4);
	}

	protected final static void drawStringStatic(Graphics g, String s,
			Font font, int i, int j) {
		FontMetrics fontmetrics = gameFrame.getFontMetrics(font);
		fontmetrics.stringWidth(s);
		g.setFont(font);
		g.drawString(s, i, j);
	}


	protected byte[] unpackData(String filename, String fileTitle, int startPercentage) {
		int decmp_len = 0;
		int cmp_len = 0;
		byte data[] = null;
		try {
			drawLoadingBarText(startPercentage, "Loading " + fileTitle + " - 0%");
			java.io.InputStream inputstream = DataOperations.streamFromPath(Config.CONF_DIR + File.separator + filename);
			DataInputStream datainputstream = new DataInputStream(inputstream);
			byte headers[] = new byte[6];
			datainputstream.readFully(headers, 0, 6);
			decmp_len = ((headers[0] & 0xff) << 16) + ((headers[1] & 0xff) << 8) + (headers[2] & 0xff);
			cmp_len = ((headers[3] & 0xff) << 16) + ((headers[4] & 0xff) << 8) + (headers[5] & 0xff);
			drawLoadingBarText(startPercentage, "Loading " + fileTitle + " - 5%");
			int l = 0;
			data = new byte[cmp_len];
			while(l < cmp_len)  {
				int i1 = cmp_len - l;
				if(i1 > 1000)
					i1 = 1000;
				datainputstream.readFully(data, l, i1);
				l += i1;
				drawLoadingBarText(startPercentage, "Loading " + fileTitle + " - " + (5 + (l * 95) / cmp_len) + "%");
			}
			datainputstream.close();
		}
		catch(IOException _ex) { }
		drawLoadingBarText(startPercentage, "Unpacking " + fileTitle);
		if(cmp_len != decmp_len) {
			byte buffer[] = new byte[decmp_len];
			DataFileDecrypter.unpackData(buffer, decmp_len, data, cmp_len, 0);
			return buffer;
		} else {
			return data;
		}
	}

	public Graphics getGraphics() {
		if (gameFrame != null) {
			return gameFrame.getGraphics();
		}
		return super.getGraphics();
	}

	public Image createImage(int i, int j) {
		if (gameFrame != null) {
			return gameFrame.createImage(i, j);
		}
		return super.createImage(i, j);
	}

	protected Socket makeSocket(String address, int port) throws IOException {
		Socket socket = new Socket(InetAddress.getByName(address), port);
		socket.setSoTimeout(30000);
		socket.setTcpNoDelay(true);
		return socket;
	}

	public void startThread(Runnable runnable) {
		Thread thread = new Thread(runnable);
		thread.setDaemon(true);
		thread.start();
	}

	protected static int appletWidth;
	protected static int appletHeight;
	private Thread gameWindowThread;
	private int threadSleepModifier;
	private int anInt5;
	private long currentTimeArray[];
	public static GameFrame gameFrame = null;
	private boolean appletMode;
	private int exitTimeout;
	private int anInt10;
	public int yOffset;
	public int lastActionTimeout;
	public int loadingScreen;
	public String loadingString;
	private int loadingBarPercent;
	private String loadingBarText;
	private static Graphics graphics;
	private static String charSet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!\"\243$%^&*()-_=+[{]};:'@#~,<.>/?\\| ";
	public boolean keyLeftBraceDown;
	public boolean keyRightBraceDown;
	public boolean keyLeftDown;
	public boolean keyRightDown;
	public boolean keyUpDown;
	public boolean keyDownDown;
	public boolean keyDelDown;
	public boolean keySpaceDown;
	public boolean keyNMDown;
	public int threadSleepTime;
	public int mouseX;
	public int mouseY;
	public int mouseButton;
	public int lastMouseButton;
	public int keyDown;
	public int keyDown2;
	public boolean keyF1Toggle, f2Toggle;
	public String inputText;
	public String enteredText;
	public String inputMessage;
	public String enteredMessage;

}
