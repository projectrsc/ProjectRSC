package org.darkquest.gs.service.impl;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;

import org.darkquest.gs.model.Player;
import org.darkquest.gs.service.Service;

public final class Captcha extends Service {

	public String fontFolder = "../" + File.separator + "conf" + File.separator + "server" + File.separator + "fonts" + File.separator;

	private Font loadedFonts[];

	private Random rand = new Random();

	@Override
	public void start() {
		File fontFolderFile = new File(fontFolder);
		String[] fonts = fontFolderFile.list();
		loadedFonts = new Font[fonts.length];
		for (int i = 0; i < fonts.length; i++) {
			File fontFile = new File(fontFolder + fonts[i]);
			
			if (fontFile.isHidden() || fontFile.isDirectory()) {
				continue;
			}
			
			try {
				FileInputStream stream = new FileInputStream(fontFile);
				Font temp = Font.createFont(Font.PLAIN, stream);
				loadedFonts[i] = temp.deriveFont(Float.valueOf(random(35, 40)));
				stream.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public byte[] generateCaptcha(Player p) {
		BufferedImage image = new BufferedImage(256, 49, BufferedImage.TYPE_BYTE_GRAY);
		Graphics2D gfx = image.createGraphics();
		String captcha = "";

		gfx.setColor(Color.BLACK);
		gfx.fillRect(0, 0, 256, 49);

		gfx.setColor(Color.WHITE);

		for (int i = 1; i <= 5; i++) {
			char temp = generateLetter();
			if (temp == 'i' || temp == 'l') {
				temp = 'q'; // ez fix.
			}
			captcha += temp;
			gfx.setFont(loadedFonts[random(0, loadedFonts.length - 1)]);
			double shear = 0.1;
			gfx.shear(shear, 0);
			gfx.drawString(String.valueOf(temp), i * random(40, 45), random(30, 40));
			gfx.shear(-shear, 0);
		}

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] returnVal = null;
		try {
			ImageIO.write(image, "PNG", baos);
			returnVal = baos.toByteArray();
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
		gfx.dispose();
		p.setSleepword(captcha);
		return returnVal;
	}

	private char generateLetter() {
		char returnVal = '-';
		switch (random(0, 1)) {
		case 0:
			returnVal = (char) random(65, 89);
			break;
		case 1:
			returnVal = (char) random(97, 121);
			break;
		}
		return returnVal;
	}

	/**
	 * returns a random number within the given bounds
	 */
	private int random(int low, int high) {
		return low + rand.nextInt(high - low + 1);
	}

}
