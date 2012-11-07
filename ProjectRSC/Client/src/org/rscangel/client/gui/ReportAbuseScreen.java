package org.rscangel.client.gui;

import org.rscangel.client.mudclient;
import org.rscangel.client.gfx.JagGraphics;
import org.rscangel.client.util.DataConversions;

public final class ReportAbuseScreen {

	private final mudclient mc;

	private int reportStage;

	private String reportedUser;

	private boolean mutePlayer = false;

	public ReportAbuseScreen(mudclient mc) {
		this.mc = mc;
	}

	public void render(JagGraphics graphics) {
		if (reportStage == 1) { // ask who they want to report
			int normalHeight = graphics.fontHeight(1);
			int boldHeight = graphics.fontHeight(4);

			int width = 400;
			int height = (mc.ourPlayer.admin >= 1 ? normalHeight + 5 : 0) + 70;
			int boxX = -(width / 2) + 256;
			int boxY = -(height / 2) + 180;
			graphics.drawBox(boxX, boxY, width, height, 0);
			graphics.drawBoxEdge(boxX, boxY, width, height, 0xffffff);

			graphics.drawText("Enter the name of the player you wish to report:", 256, normalHeight + 5 + boxY, 1, 0xffff00);
			int baseY = 2 + normalHeight;
			graphics.drawText(mc.inputText + "*", 256, boldHeight + 3 + baseY + (boxY + 5), 4, 0xffffff);

			int y = 2 + (boldHeight + baseY + (5 + boxY + 3 + normalHeight));
			int color = 0xffffff;

			if (mc.ourPlayer.admin > 0) { //we staff?
				String mute = (mutePlayer ? "[X]" : "[ ]") + " Mute player";

				int muteWidth = graphics.textWidth(mute, 1);

				if (mc.mouseX > 256 - (muteWidth / 2) && mc.mouseX < 256 + (muteWidth / 2) && (mc.mouseY > y + -normalHeight) && (mc.mouseY < y)) {
					color = 0xff0000;

					if (mc.mouseButtonClick != 0) {
						mc.mouseButtonClick = 0;
						mutePlayer = !mutePlayer;
					}
				}
				graphics.drawText(mute, 256, y, 1, color);
				y += 10 + normalHeight;
			}
			color = 0xffffff;

			if ((mc.mouseX ^ 0xFFFFFFFF) < -211 && (mc.mouseX ^ 0xFFFFFFFF) > -229 && ((mc.mouseY ^ 0xFFFFFFFF) < (y + -normalHeight ^ 0xFFFFFFFF)) && y > mc.mouseY) {
				if (mc.mouseButtonClick != 0 && mc.inputText.length() > 1) { // if they clicked OK and the text is larger than 2
					mc.mouseButtonClick = 0;
					reportedUser = mc.inputText;
					reportStage = 2;
					mc.inputText = "";
					mc.enteredText = "";
				}
				color = 0xffff00;
			}
			graphics.drawString("OK", 210, y, 1, color);
			color = 0xffffff;

			if (mc.mouseX > 264 && (mc.mouseX ^ 0xFFFFFFFF) > -305 && mc.mouseY > y + -normalHeight && y > mc.mouseY) {
				if (mc.mouseButtonClick != 0) {
					mc.mouseButtonClick = 0;
					reportStage = 0;
					mutePlayer = false;
					mc.inputText = "";
					mc.enteredText = "";
				}
				color = 0xffff00;
			}
			graphics.drawString("Cancel", 264, y, 1, color);

			if (mc.mouseButtonClick == 1 && (boxX > mc.mouseX || mc.mouseX > width + boxX || boxY > mc.mouseY || mc.mouseY > height + boxY)) {
				mc.mouseButtonClick = 0;
				reportStage = 0;
				mutePlayer = false;
				mc.inputText = "";
				mc.enteredText = "";
			}
		} else { // ask why they want to report the user
			int[] reasonLocs = { mc.windowWidth / 2 - 150, mc.windowWidth / 2, mc.windowWidth / 2 + 150 };
			int[] labelLocs = { mc.windowHeight / 2, mc.windowHeight / 2 + 21 };
			int y = labelLocs[0] - 117;
			String[] labels = { "Honour", "Respect", "Security" };
			graphics.drawBox(reasonLocs[1] - 225, labelLocs[0] - 132, 450, 275, 0);
			graphics.drawBoxEdge(reasonLocs[1] - 225, labelLocs[0] - 132, 450, 275, 16777215);
			graphics.drawText("This form is for reporting players who are breaking our rules", reasonLocs[1], y, 1, 16777215);
			y += 15;
			graphics.drawText("Using it sends a snapshot of the last 60 seconds of activity to us", reasonLocs[1], y, 1, 16777215);
			y += 15;
			graphics.drawText("If you misuse this form, you will be banned.", reasonLocs[1], y, 1, 16744448);
			y += 25;
			graphics.drawText("Click on the most suitable option from the Rules of RuneScape.", reasonLocs[1], y, 1, 16776960);
			y += 15;
			graphics.drawText("This will send a report to our Player Support team for investigation.", reasonLocs[1], y, 1, 16776960);
			y += 18;

			for (int j = 0; j < 3; j++) {
				graphics.drawText(labels[j], reasonLocs[j], y, 4, 16711680);
			}

			y += 20;
			String[][] arrayOfString = { { "Buying or", "selling an account" }, { "Seriously offensive", "language" }, { "Asking for or providing", "contact information" } };
			String[] arrayOfString2 = { "Encouraging rule-breaking", "Solicitation", "Breaking real-world laws", "Staff impersonation", "Disruptive behaviour", "Advertising websites", "Macroing or use of bots", "Offensive account name", "Scamming", "Real-life threats", "Exploiting a bug" };

			int color = 16777215;
			for (int i = 0; i < 3; i++) {
				if (mouseWithinCenteredCoords(reasonLocs[i], labelLocs[0] - 8, 140, 30)) {
					graphics.drawCenteredBoxAlpha(reasonLocs[i], labelLocs[0] - 8, 140, 30, JagGraphics.convertRGBToLong(64, 64, 64), 160);
					color = 16744448;

					if (mc.mouseButtonClick != 0) {
						mc.mouseButtonClick = 0;
						sendReport(i);
					}
				}
				graphics.drawCenteredBoxEdge(reasonLocs[i], labelLocs[0] - 8, 140, 30, JagGraphics.convertRGBToLong(64, 64, 64));
				graphics.drawText(arrayOfString[i][0], reasonLocs[i], labelLocs[0] - 11, 0, color);
				graphics.drawText(arrayOfString[i][1], reasonLocs[i], labelLocs[0] + 1, 0, color);
				color = 16777215;

				if (mouseWithinCenteredCoords(reasonLocs[i], labelLocs[1] - 3, 140, 16)) {
					graphics.drawCenteredBoxAlpha(reasonLocs[i], labelLocs[1] - 3, 140, 16, JagGraphics.convertRGBToLong(64, 64, 64), 160);
					color = 16744448;

					if (mc.mouseButtonClick != 0) {
						mc.mouseButtonClick = 0;
						sendReport(i + 3);
					}
				}
				graphics.drawCenteredBoxEdge(reasonLocs[i], labelLocs[1] - 3, 140, 18, JagGraphics.convertRGBToLong(64, 64, 64));
				graphics.drawText(arrayOfString2[i], reasonLocs[i], labelLocs[1], 0, color);
				color = 16777215;

				if (mouseWithinCenteredCoords(reasonLocs[i], labelLocs[1] + 17, 140, 16)) {
					graphics.drawCenteredBoxAlpha(reasonLocs[i], labelLocs[1] + 17, 140, 16, JagGraphics.convertRGBToLong(64, 64, 64), 160);
					color = 16744448;

					if (mc.mouseButtonClick != 0) {
						mc.mouseButtonClick = 0;
						sendReport(i + 6);
					}
				}
				graphics.drawCenteredBoxEdge(reasonLocs[i], labelLocs[1] + 17, 140, 18, JagGraphics.convertRGBToLong(64, 64, 64));
				graphics.drawText(arrayOfString2[(i + 3)], reasonLocs[i], labelLocs[1] + 20, 0, color);
				color = 16777215;
			}

			for (int i = 6; i < 8; i++) {
				if (mouseWithinCenteredCoords(reasonLocs[(i - 6)], labelLocs[1] + 37, 140, 16)) {
					graphics.drawCenteredBoxAlpha(reasonLocs[(i - 6)], labelLocs[1] + 37, 140, 16, JagGraphics.convertRGBToLong(64, 64, 64), 160);
					color = 16744448;
					if (mc.mouseButtonClick != 0) {
						mc.mouseButtonClick = 0;
						sendReport(i + 4);
					}
				}
				graphics.drawCenteredBoxEdge(reasonLocs[(i - 6)], labelLocs[1] + 37, 140, 18, JagGraphics.convertRGBToLong(64, 64, 64));
				graphics.drawText(arrayOfString2[i], reasonLocs[(i - 6)], labelLocs[1] + 40, 0, color);
				color = 16777215;

				if (mouseWithinCenteredCoords(reasonLocs[(i - 6)], labelLocs[1] + 57, 140, 16)) {
					graphics.drawCenteredBoxAlpha(reasonLocs[(i - 6)], labelLocs[1] + 57, 140, 16, JagGraphics.convertRGBToLong(64, 64, 64), 160);
					color = 16744448;
					if (mc.mouseButtonClick != 0) {
						mc.mouseButtonClick = 0;
						sendReport(i + 4);
					}
				}
				graphics.drawCenteredBoxEdge(reasonLocs[(i - 6)], labelLocs[1] + 57, 140, 18, JagGraphics.convertRGBToLong(64, 64, 64));
				graphics.drawText(arrayOfString2[(i + 2)], reasonLocs[(i - 6)], labelLocs[1] + 60, 0, color);
				color = 16777215;
			}

			if (mouseWithinCenteredCoords(reasonLocs[0], labelLocs[1] + 77, 140, 16)) {
				graphics.drawCenteredBoxAlpha(reasonLocs[0], labelLocs[1] + 77, 140, 16, JagGraphics.convertRGBToLong(64, 64, 64), 160);
				color = 16744448;
				if (mc.mouseButtonClick != 0) {
					mc.mouseButtonClick = 0;
					sendReport(11);
				}
			}
			graphics.drawCenteredBoxEdge(reasonLocs[0], labelLocs[1] + 77, 140, 18, JagGraphics.convertRGBToLong(64, 64, 64));
			graphics.drawText(arrayOfString2[10], reasonLocs[0], labelLocs[1] + 80, 0, color);
			color = 16777215;

			if (mouseWithinCenteredCoords(reasonLocs[1], labelLocs[0] + 133, 115, 13)) {
				color = 16776960;
				if (mc.mouseButtonClick != 0) {
					mc.mouseButtonClick = 0;
					reportStage = 0;
					reportedUser = "";
					mutePlayer = false;
				}
			}
			graphics.drawText("Click here to cancel", reasonLocs[1], labelLocs[0] + 134, 1, color);
		}
	}

	public void tick() {
	}

	public int getReportStage() {
		return reportStage;
	}

	public void setReportStage(int reportStage) {
		this.reportStage = reportStage;
	}

	public void setReportedUser(String reportedUser) {
		this.reportedUser = reportedUser;
	}

	private boolean mouseWithinCenteredCoords(int x, int y, int width, int height) {
		return (mc.mouseX >= x - width / 2) && (mc.mouseX <= x + width / 2) && (mc.mouseY >= y - height / 2) && (mc.mouseY <= y + height / 2);
	}

	private void sendReport(int reason) {
		mc.streamClass.createPacket(13); //TODO need to change!
		mc.streamClass.addLong(DataConversions.usernameToHash(reportedUser));
		mc.streamClass.addByte(reason);
		mc.streamClass.addByte(mutePlayer ? 1 : 0);
		mc.streamClass.formatPacket();
		reportStage = 0;
		reportedUser = "";
		mutePlayer = false;
		mc.inputText = "";
		mc.enteredText = "";
	}

}
