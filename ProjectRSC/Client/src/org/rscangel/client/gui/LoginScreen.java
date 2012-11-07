package org.rscangel.client.gui;

import org.rscangel.client.Menu;
import org.rscangel.client.mudclient;
import org.rscangel.client.gfx.JagGraphics;
import org.rscangel.client.util.Config;

public final class LoginScreen {

	private final mudclient mc;

	private Menu welcomeMenu, newUserMenu, loginMenu;

	private int loginStage = 0;
	
	//buttons
	private int newUserButton, existingUserButton, newUserOkButton, loginOk, loginCancel;

	//labels
	private int loginStatusLabel;

	//inputs
	private int usernameInput, passwordInput;

	public LoginScreen(mudclient mc) {
		this.mc = mc;
	}

	public void initialize() {
		welcomeMenu = new Menu(mc.gameGraphics, 50);
		int y = 40;
		
		if (!Config.MEMBERS_FEATURES) {
			welcomeMenu.createLabel(256, 200 + y, "Click on an option", 5, true);
			welcomeMenu.addButton(156, 240 + y, 120, 35);
			welcomeMenu.addButton(356, 240 + y, 120, 35);
			welcomeMenu.createLabel(156, 240 + y, "New User", 5, false);
			welcomeMenu.createLabel(356, 240 + y, "Existing User", 5, false);
			newUserButton = welcomeMenu.createButton(156, 240 + y, 120, 35);
			existingUserButton = welcomeMenu.createButton(356, 240 + y, 120, 35);
		} else {
			welcomeMenu.createLabel(256, 200 + y, "Welcome to RuneScape", 4, true);
			welcomeMenu.createLabel(256, 215 + y, "You need a member account to use this server", 4, true);
			welcomeMenu.addButton(256, 250 + y, 200, 35);
			welcomeMenu.createLabel(256, 250 + y, "Click here to login", 5, false);
			existingUserButton = welcomeMenu.createButton(256, 250 + y, 200, 35);
		}
		
		newUserMenu = new Menu(mc.gameGraphics, 50);
		y = 230;
		newUserMenu.createLabel(256, y + 8, "To create an account please go back to the", 4, true);
		y += 20;
		newUserMenu.createLabel(256, y + 8, "www.projectrsc.com front page, and choose 'register'", 4, true);
		y += 30;
		newUserMenu.addButton(256, y + 17, 150, 34);
		newUserMenu.createLabel(256, y + 17, "Ok", 5, false);
		newUserOkButton = newUserMenu.createButton(256, y + 17, 150, 34);
		
		loginMenu = new Menu(mc.gameGraphics, 50);
		y = 230;
		loginStatusLabel = loginMenu.createLabel(256, y - 10, "Please enter your username and password", 4, true);
		y += 28;
		loginMenu.addButton(140, y, 200, 40);
		loginMenu.createLabel(140, y - 10, "Username:", 4, false);
		usernameInput = loginMenu.createInput(140, y + 10, 200, 40, 4, 12, false, false);
		y += 47;
		loginMenu.addButton(190, y, 200, 40);
		loginMenu.createLabel(190, y - 10, "Password:", 4, false);
		passwordInput = loginMenu.createInput(190, y + 10, 200, 40, 4, 20, true, false);
		y -= 55;
		loginMenu.addButton(410, y, 120, 25);
		loginMenu.createLabel(410, y, "Ok", 4, false);
		loginOk = loginMenu.createButton(410, y, 120, 25);
		y += 30;
		loginMenu.addButton(410, y, 120, 25);
		loginMenu.createLabel(410, y, "Cancel", 4, false);
		loginCancel = loginMenu.createButton(410, y, 120, 25);
		y += 30;
		loginMenu.setFocus(usernameInput);
	}
	
	public void createSprites() {
		int z = 0;
		byte x = 50;
		byte y = 50;
		
		mc.engineHandle.loadSector(x * 48 + 23, y * 48 + 23, z);
		mc.engineHandle.loadSectorObjects(mc.gameDataModels);
		
		char cameraX = '\u2600';
		char cameraY = '\u1900';
		char c3 = '\u044C';
		char c4 = '\u0378';
		
		mc.gameCamera.zoom1 = 4100;
		mc.gameCamera.zoom2 = 4100;
		mc.gameCamera.zoom3 = 1;
		mc.gameCamera.zoom4 = 4000;
		mc.gameCamera.setCamera(cameraX, -mc.engineHandle.getAveragedElevation(cameraX, cameraY), cameraY, 912, c4, 0, c3 * 2);
		mc.gameCamera.finishCamera();
		mc.gameGraphics.fadePixels();
		mc.gameGraphics.fadePixels();
		mc.gameGraphics.drawBox(0, 0, 512, 6, 0);
		
		for(int i1 = 6; i1 >= 1; i1--)
			mc.gameGraphics.method221(0, i1, 0, i1, 512, 8);

		mc.gameGraphics.drawBox(0, 194, 512, 20, 0);
		for(int j1 = 6; j1 >= 1; j1--)
			mc.gameGraphics.method221(0, j1, 0, 194 - j1, 512, 8);

		mc.gameGraphics.drawPicture(15, 15, mudclient.SPRITE_MEDIA_START + 10);
		mc.gameGraphics.storeSpriteVert(mudclient.SPRITE_TEXTURE_START, 0, 0, 512, 200);
		mc.gameGraphics.cbl(mudclient.SPRITE_TEXTURE_START);

		//2nd scene
		cameraX = '\u2400';
		cameraY = '\u2400';
		c3 = '\u044C';
		c4 = '\u0378';
		mc.gameCamera.zoom1 = 4100;
		mc.gameCamera.zoom2 = 4100;
		mc.gameCamera.zoom3 = 1;
		mc.gameCamera.zoom4 = 4000;
		mc.gameCamera.setCamera(cameraX, -mc.engineHandle.getAveragedElevation(cameraX, cameraY), cameraY, 912, c4, 0, c3 * 2);
		mc.gameCamera.finishCamera();
		mc.gameGraphics.fadePixels();
		mc.gameGraphics.fadePixels();
		mc.gameGraphics.drawBox(0, 0, 512, 6, 0);
		
		for(int i1 = 6; i1 >= 1; i1--)
			mc.gameGraphics.method221(0, i1, 0, i1, 512, 8);

		mc.gameGraphics.drawBox(0, 194, 512, 20, 0);
		for(int j1 = 6; j1 >= 1; j1--)
			mc.gameGraphics.method221(0, j1, 0, 194 - j1, 512, 8);
		
		mc.gameGraphics.drawPicture(15, 15, mudclient.SPRITE_MEDIA_START + 10);
		mc.gameGraphics.storeSpriteVert(mudclient.SPRITE_TEXTURE_START + 1, 0, 0, 512, 200);
		mc.gameGraphics.cbl(mudclient.SPRITE_TEXTURE_START + 1);

		//3rd scene
		for(int i2 = 0; i2 < 64; i2++) {
			mc.gameCamera.removeModel(mc.engineHandle.aModelArrayArray598[0][i2]);
			mc.gameCamera.removeModel(mc.engineHandle.aModelArrayArray580[1][i2]);
			mc.gameCamera.removeModel(mc.engineHandle.aModelArrayArray598[1][i2]);
			mc.gameCamera.removeModel(mc.engineHandle.aModelArrayArray580[2][i2]);
			mc.gameCamera.removeModel(mc.engineHandle.aModelArrayArray598[2][i2]);
		}
		
		cameraX = '\u2B80';
		cameraY = '\u2880';
		c3 = '\u01F4';
		c4 = '\u0178';
		mc.gameCamera.zoom1 = 4100;
		mc.gameCamera.zoom2 = 4100;
		mc.gameCamera.zoom3 = 1;
		mc.gameCamera.zoom4 = 4000;
		mc.gameCamera.setCamera(cameraX, -mc.engineHandle.getAveragedElevation(cameraX, cameraY), cameraY, 912, c4, 0, c3 * 2);
		mc.gameCamera.finishCamera();
		mc.gameGraphics.fadePixels();
		mc.gameGraphics.fadePixels();
		mc.gameGraphics.drawBox(0, 0, 512, 6, 0);
		
		for(int i1 = 6; i1 >= 1; i1--)
			mc.gameGraphics.method221(0, i1, 0, i1, 512, 8);

		mc.gameGraphics.drawBox(0, 194, 512, 20, 0);
		for(int j1 = 6; j1 >= 1; j1--)
			mc.gameGraphics.method221(0, j1, 0, 194 - j1, 512, 8);
		
		mc.gameGraphics.drawPicture(15, 15, mudclient.SPRITE_MEDIA_START + 10);
		mc.gameGraphics.storeSpriteVert(mudclient.SPRITE_MEDIA_START + 10, 0, 0, 512, 200);
		mc.gameGraphics.cbl(mudclient.SPRITE_MEDIA_START + 10);
	}

	public void render(JagGraphics graphics) {
		mc.loginScreenShown = false;
		graphics.interlace = false;
		graphics.clearScreen();
		
		if (loginStage <= 2 && loginStage >= 0) {
			int timer = (mc.tick * 2) % 3072;

			if(timer < 1024) {
				graphics.drawPicture(0, 10, mudclient.SPRITE_TEXTURE_START);
				
				if(timer > 768) {
					graphics.drawTransparentPicture(0, 10, mudclient.SPRITE_TEXTURE_START + 1, timer - 768);
				}
			} else {
				if(timer < 2048) {
					graphics.drawPicture(0, 10, mudclient.SPRITE_TEXTURE_START + 1);
					
					if(timer > 1792) {
						graphics.drawTransparentPicture(0, 10, mudclient.SPRITE_MEDIA_START + 10, timer - 1792);
					}
				} else {
					graphics.drawPicture(0, 10, mudclient.SPRITE_MEDIA_START + 10);
					
					if(timer > 2816) {
						graphics.drawTransparentPicture(0, 10, mudclient.SPRITE_TEXTURE_START, timer - 2816);
					}
				}
			}
		}
		
		if(loginStage == 0) {
			welcomeMenu.drawMenu();
		} else if(loginStage == 1) {
			newUserMenu.drawMenu();
		} else if(loginStage == 2) {
			loginMenu.drawMenu();
		}
		
		graphics.drawPicture(0, mc.windowHeight, mudclient.SPRITE_MEDIA_START + 22);
		graphics.drawImage(mc.aGraphics936, 0, 0);
	}

	public void tick() {
		if (mc.socketTimeout > 0) {
			mc.socketTimeout--;
		}

		switch (loginStage) {
		case 0:
			welcomeMenu.mouseClick(mc.mouseX, mc.mouseY, mc.lastMouseButton, mc.mouseButton);

			if (welcomeMenu.hasClicked(newUserButton)) {
				loginStage = 1;
			} else if (welcomeMenu.hasClicked(existingUserButton)) {
				loginStage = 2;
				loginMenu.setText(loginStatusLabel, "Please enter your username and password");
				loginMenu.setText(usernameInput, "");
				loginMenu.setText(passwordInput, "");
				loginMenu.setFocus(usernameInput);
			}
			break;
		case 1:
			newUserMenu.mouseClick(mc.mouseX, mc.mouseY, mc.lastMouseButton, mc.mouseButton);

			if (newUserMenu.hasClicked(newUserOkButton)) {
				loginStage = 0;
			}
			break;
		case 2:
			loginMenu.mouseClick(mc.mouseX, mc.mouseY, mc.lastMouseButton, mc.mouseButton);

			if (loginMenu.hasClicked(loginCancel)) {
				loginStage = 0;
			}

			if (loginMenu.hasClicked(usernameInput)) {
				loginMenu.setFocus(passwordInput);
			}

			if (loginMenu.hasClicked(passwordInput) || loginMenu.hasClicked(loginOk)) {
				mc.currentUser = loginMenu.getText(usernameInput);
				mc.currentPass = loginMenu.getText(passwordInput);
				mc.login(mc.currentUser, mc.currentPass, false);
			}
			break;
		}
	}
	
	public void updateLoginStatus(String top, String bottom) {
		if (loginStage == 2 && loginMenu != null) {
			loginMenu.setText(loginStatusLabel, top + " " + bottom);
		}
	}
	
	public void keyDown(int key) {
		if (loginStage == 0) {
			if (welcomeMenu != null)
				welcomeMenu.keyDown(key);
		} else if (loginStage == 1) {
			if (newUserMenu != null)
				newUserMenu.keyDown(key);
		} else if (loginStage == 2) {
			if (loginMenu != null)
				loginMenu.keyDown(key);
		}
	}
	
	public void setLoginStage(int stage) {
		loginStage = stage;
	}
	
	public int getLoginStage() {
		return loginStage;
	}

}
