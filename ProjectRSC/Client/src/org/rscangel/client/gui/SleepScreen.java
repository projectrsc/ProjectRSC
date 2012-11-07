package org.rscangel.client.gui;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;

import javax.imageio.ImageIO;

import org.rscangel.client.DataOperations;
import org.rscangel.client.mudclient;
import org.rscangel.client.gfx.JagGraphics;

public final class SleepScreen {

	private final mudclient mc;

	private String sleepStatusText;

	private boolean sleeping = false;

	private int fatigue, captchaWidth, captchaHeight, captchaPixels[][];

	public SleepScreen(mudclient mc) {
		this.mc = mc;
	}

	public void render(JagGraphics graphics) {
		graphics.fadePixels();
		if(Math.random() < 0.14999999999999999D)
			graphics.drawText("ZZZ", (int)(Math.random() * 80D), (int)(Math.random() * 334D), 5, (int)(Math.random() * 16777215D));
		if(Math.random() < 0.14999999999999999D)
			graphics.drawText("ZZZ", 512 - (int)(Math.random() * 80D), (int)(Math.random() * 334D), 5, (int)(Math.random() * 16777215D));
		graphics.drawBox(mc.windowWidth / 2 - 100, 160, 200, 40, 0);
		graphics.drawText("You are sleeping", mc.windowWidth / 2, 50, 7, 0xffff00);
		graphics.drawText("Fatigue: " + (fatigue * 100) / 750 + "%", mc.windowWidth / 2, 90, 7, 0xffff00);
		graphics.drawText("When you want to wake up just use your", mc.windowWidth / 2, 140, 5, 0xffffff);
		graphics.drawText("keyboard to type the word in the box below", mc.windowWidth / 2, 160, 5, 0xffffff);
		graphics.drawText(mc.inputText + "*", mc.windowWidth / 2, 180, 5, 65535);
		if (sleepStatusText == null) {
			graphics.drawPixels(captchaPixels, mc.windowWidth / 2 - 127, 230, 256, 49);
		} else {
			graphics.drawText(sleepStatusText, mc.windowWidth / 2, 260, 5, 0xff0000);
		}
		graphics.drawBoxEdge(mc.windowWidth / 2 - 128, 229, 257, 42, 0xffffff);
		mc.drawChatMessageTabs();
		graphics.drawText("If you can't read the word", mc.windowWidth / 2, 290, 1, 0xffffff);
		graphics.drawText("@yel@click here@whi@ to get a different one", mc.windowWidth / 2, 305, 1, 0xffffff);
		graphics.drawImage(mc.aGraphics936, 0, 0);
	}

	public void tick() {
		if (mc.enteredText.length() > 0) {
			if (mc.enteredText.equalsIgnoreCase("::lostcon")) {
				mc.streamClass.closeStream();
			} else {
				mc.streamClass.createPacket(200);
				mc.streamClass.addString(mc.enteredText);

				/*if(!mc.sleepWordDelay) {
					mc.streamClass.addByte(0);
					mc.sleepWordDelay = true;
				}*/

				mc.streamClass.formatPacket();
				mc.inputText = "";
				mc.enteredText = "";
				sleepStatusText = "Please wait...";
			}
		}
		if (mc.lastMouseButton == 1 && mc.mouseY > 275 && mc.mouseY < 310 && mc.mouseX > 56 && mc.mouseX < 456) {
			mc.streamClass.createPacket(200);
			mc.streamClass.addString("-null-");
			/*if(!mc.sleepWordDelay) {
				mc.streamClass.addByte(0);
				mc.sleepWordDelay = true;
			}*/
			mc.streamClass.formatPacket();
			mc.inputText = "";
			mc.enteredText = "";
			sleepStatusText = "Please wait...";
		}
		mc.lastMouseButton = 0;
	}

	public boolean handlePacket(int packetId, byte[] data, int length) {
		switch (packetId) {
		case 126:
			fatigue = DataOperations.getUnsigned2Bytes(data, 1);
			System.out.println(fatigue);
			return true;
		case 206:
			sleeping = true;
			mc.inputText = "";
			mc.enteredText = "";

			try {
				BufferedImage image = ImageIO.read(new ByteArrayInputStream(data, 1, length));
				captchaWidth = image.getWidth();
				captchaHeight = image.getHeight();
				captchaPixels = new int[captchaWidth][captchaHeight];
				for(int x = 0; x < captchaWidth; x++) {
					for(int y = 0; y < captchaHeight; y++) {
						captchaPixels[x][y] = image.getRGB(x, y);
					}
				}
			} catch(Exception e) {
				e.printStackTrace();
			}
			sleepStatusText = null;
			return true;
		case 224:
			sleeping = false;
			return true;
		case 225:
			sleepStatusText = "Incorrect - Please wait...";
			return true;
		default:
			return false;
		}
	}

	public void resetSleeping() {
		sleeping = false;
	}

	public boolean isSleeping() {
		return sleeping;
	}

	public int getFatigue() {
		return fatigue;
	}

}
