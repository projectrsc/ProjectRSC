package org.rscangel.client;

import org.rscangel.client.gfx.JagGraphics;

public class Menu {

	private final int boxGradientTop, boxGradientBottom,
	boxBorder1, boxBorder2;

	public Menu(JagGraphics graphics, int components) {
		selectedComponent = -1;
		redStringText = true;
		this.graphics = graphics;
		componentAcceptsInput = new boolean[components];
		aBooleanArray184 = new boolean[components];
		componentIsPasswordField = new boolean[components];
		componentSkip = new boolean[components];
		componentWhiteText = new boolean[components];
		listShownEntries = new int[components];
		listLength = new int[components];
		anIntArray189 = new int[components];
		anIntArray190 = new int[components];
		componentX = new int[components];
		componentY = new int[components];
		componentType = new int[components];
		componentWidth = new int[components];
		componentHeight = new int[components];
		componentInputMaxLength = new int[components];
		componentTextSize = new int[components];
		componentText = new String[components];
		componentTextList = new String[components][];

		scrollBarGradientColorTop = rgbToInt(114, 114, 176);
		scrollBarGradientColorBottom = rgbToInt(14, 14, 62);
		scrollBarDraggingBarLine1Color = rgbToInt(200, 208, 232);
		scrollBarDraggingBarColor = rgbToInt(96, 129, 184);
		scrollBarDraggingBarLine2Color = rgbToInt(53, 95, 115);
		anInt212 = rgbToInt(117, 142, 171);
		anInt213 = rgbToInt(98, 122, 158);
		anInt214 = rgbToInt(86, 100, 136);
		boxGradientTop = rgbToInt(135, 146, 179);
		boxGradientBottom = rgbToInt(97, 112, 151);
		boxBorder1 = rgbToInt(88, 102, 136);
		boxBorder2 = rgbToInt(84, 93, 120);
	}

	public int rgbToInt(int red, int green, int blue) {
		return JagGraphics.convertRGBToLong((redModifier * red) / 114, (greeModifier * green) / 114, (blueModifier * blue) / 176);
	}

	public void mouseClick(int mouseX, int mouseY, int lastMouseButton, int mouseButton) {
		this.mouseX = mouseX;
		this.mouseY = mouseY;
		this.mouseButton = mouseButton;
		if (lastMouseButton != 0)
			this.lastMouseButton = lastMouseButton;
		
		if (lastMouseButton == 1) {
			for (int menuObject = 0; menuObject < menuObjectCount; menuObject++) {
				if (componentAcceptsInput[menuObject] && componentType[menuObject] == 10 && mouseX >= componentX[menuObject] && mouseY >= componentY[menuObject] && mouseX <= componentX[menuObject] + componentWidth[menuObject] && mouseY <= componentY[menuObject] + componentHeight[menuObject])
					componentSkip[menuObject] = true;
				if (componentAcceptsInput[menuObject] && componentType[menuObject] == 14 && mouseX >= componentX[menuObject] && mouseY >= componentY[menuObject] && mouseX <= componentX[menuObject] + componentWidth[menuObject] && mouseY <= componentY[menuObject] + componentHeight[menuObject])
					anIntArray189[menuObject] = 1 - anIntArray189[menuObject]; // no
			}
		}
		
		if (mouseButton == 1)
			mouseClicksConsecutive++;
		else
			mouseClicksConsecutive = 0;
		
		if (lastMouseButton == 1 || mouseClicksConsecutive > 20) {
			for (int j1 = 0; j1 < menuObjectCount; j1++)
				if (componentAcceptsInput[j1] && componentType[j1] == 15 && mouseX >= componentX[j1] && mouseY >= componentY[j1] && mouseX <= componentX[j1] + componentWidth[j1] && mouseY <= componentY[j1] + componentHeight[j1])
					componentSkip[j1] = true;

			mouseClicksConsecutive -= 5;
		}
	}

	public boolean hasClicked(int i) {
		if (componentAcceptsInput[i] && componentSkip[i]) {
			componentSkip[i] = false;
			return true;
		} else {
			return false;
		}
	}

	public void keyDown(int key) {
		if (key == 0)
			return;
		if (selectedComponent != -1
				&& componentText[selectedComponent] != null
				&& componentAcceptsInput[selectedComponent]) {
			int textLength = componentText[selectedComponent].length();
			if (key == 8 && textLength > 0) // backspace
				componentText[selectedComponent] = componentText[selectedComponent]
						.substring(0, textLength - 1);
			if ((key == 10 || key == 13) && textLength > 0) // enter/return
				componentSkip[selectedComponent] = true;
			String validCharSet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!\"\243$%^&*()-_=+[{]};:'@#~,<.>/?\\| ";
			if (textLength < componentInputMaxLength[selectedComponent]) {
				for (int k = 0; k < validCharSet.length(); k++)
					if (key == validCharSet.charAt(k))
						componentText[selectedComponent] += (char) key;

			}
			if (key == 9)
				do
					selectedComponent = (selectedComponent + 1)
					% menuObjectCount;
				while (componentType[selectedComponent] != 5
						&& componentType[selectedComponent] != 6);
		}
	}

	public void drawMenu() {
		for (int menuObject = 0; menuObject < menuObjectCount; menuObject++)
			if (componentAcceptsInput[menuObject]) {
				if (componentType[menuObject] == 0)
					drawTextAddHeight(menuObject, componentX[menuObject], componentY[menuObject], componentText[menuObject], componentTextSize[menuObject]);
				else if (componentType[menuObject] == 1)
					drawTextAddHeight(menuObject, componentX[menuObject] - graphics.textWidth(componentText[menuObject], componentTextSize[menuObject]) / 2, componentY[menuObject], componentText[menuObject], componentTextSize[menuObject]);
				else if (componentType[menuObject] == 2)
					drawButton(componentX[menuObject], componentY[menuObject], componentWidth[menuObject], componentHeight[menuObject]);
				else if (componentType[menuObject] == 3)
					method149(componentX[menuObject], componentY[menuObject], componentWidth[menuObject]);
				else if (componentType[menuObject] == 4)
					drawChatMessages(menuObject, componentX[menuObject], componentY[menuObject], componentWidth[menuObject], componentHeight[menuObject], componentTextSize[menuObject], componentTextList[menuObject], listLength[menuObject], listShownEntries[menuObject]);
				else if (componentType[menuObject] == 5 || componentType[menuObject] == 6)
					drawInput(menuObject, componentX[menuObject], componentY[menuObject], componentWidth[menuObject], componentHeight[menuObject], componentText[menuObject], componentTextSize[menuObject]);
				else if (componentType[menuObject] == 7)
					method152(menuObject, componentX[menuObject], componentY[menuObject], componentTextSize[menuObject], componentTextList[menuObject]);
				else if (componentType[menuObject] == 8)
					method153(menuObject, componentX[menuObject], componentY[menuObject], componentTextSize[menuObject], componentTextList[menuObject]);
				else if (componentType[menuObject] == 9)
					method154(menuObject, componentX[menuObject], componentY[menuObject], componentWidth[menuObject], componentHeight[menuObject], componentTextSize[menuObject], componentTextList[menuObject], listLength[menuObject], listShownEntries[menuObject]);
				else if (componentType[menuObject] == 11)
					method147(componentX[menuObject], componentY[menuObject], componentWidth[menuObject], componentHeight[menuObject]);
				else if (componentType[menuObject] == 12)
					method148(componentX[menuObject], componentY[menuObject], componentTextSize[menuObject]);
				else if (componentType[menuObject] == 14)
					method142(menuObject, componentX[menuObject], componentY[menuObject], componentWidth[menuObject], componentHeight[menuObject]);
				else if (componentType[menuObject] == 15)
					drawTextWithBackground(menuObject, componentX[menuObject] - graphics.textWidth(componentText[menuObject], componentTextSize[menuObject]) / 2, componentY[menuObject], componentText[menuObject], componentTextSize[menuObject]);
			}
			lastMouseButton = 0;
	}

	protected void method142(int i, int j, int k, int l, int i1) {
		graphics.drawBox(j, k, l, i1, 0xffffff);
		graphics.drawLineX(j, k, l, boxGradientTop);
		graphics.drawLineY(j, k, i1, boxGradientTop);
		graphics.drawLineX(j, (k + i1) - 1, l, boxBorder2);
		graphics.drawLineY((j + l) - 1, k, i1, boxBorder2);
		if (anIntArray189[i] == 1) {
			for (int j1 = 0; j1 < i1; j1++) {
				graphics.drawLineX(j + j1, k + j1, 1, 0);
				graphics.drawLineX((j + l) - 1 - j1, k + j1, 1, 0);
			}

		}
	}

	protected void drawTextAddHeight(int componentId, int x, int y, String text, int type) {
		int i1 = y + graphics.fontHeight(type) / 3;
		drawTextWithMask(componentId, x, i1, text, type);
	}
	
	protected void drawTextWithBackground(int componentId, int x, int y, String text, int type) {
		int yy = y + graphics.fontHeight(type) / 3;
		
		int color = 0;
		if (componentWhiteText[componentId])
			color = 0xffffff;
		
	    if (!text.equalsIgnoreCase(""))
	    	graphics.drawBoxAlpha(x - 256, y - 20, 700, graphics.fontHeight(type) + 15, 0, 120);
	    
		graphics.drawString(text, x, yy, type, color);
	}

	protected void drawTextWithMask(int menuObject, int x, int y, String text, int type) {
		int color;
		if (componentWhiteText[menuObject])
			color = 0xffffff;
		else
			color = 0;
		graphics.drawString(text, x, y, type, color);
	}

	protected void drawInput(int i, int j, int k, int l, int i1, String s,
			int j1) {
		if (componentIsPasswordField[i]) {
			int k1 = s.length();
			s = "";
			for (int i2 = 0; i2 < k1; i2++)
				s = s + "X";

		}
		if (componentType[i] == 5) {
			if (lastMouseButton == 1 && mouseX >= j && mouseY >= k - i1 / 2
					&& mouseX <= j + l && mouseY <= k + i1 / 2)
				selectedComponent = i;
		} else if (componentType[i] == 6) {
			if (lastMouseButton == 1 && mouseX >= j - l / 2
					&& mouseY >= k - i1 / 2 && mouseX <= j + l / 2
					&& mouseY <= k + i1 / 2)
				selectedComponent = i;
			j -= graphics.textWidth(s, j1) / 2;
		}
		if (selectedComponent == i)
			s = s + "*";
		int l1 = k + graphics.fontHeight(j1) / 3;
		drawTextWithMask(i, j, l1, s, j1);
	}

	private void drawButton(int i, int j, int k, int l) {
		graphics.setDimensions(i, j, i + k, j + l);
		graphics.method214(i, j, k, l, boxBorder2, boxGradientTop);
		if (aBoolean220) {
			for (int i1 = i - (j & 0x3f); i1 < i + k; i1 += 128) {
				for (int j1 = j - (j & 0x1f); j1 < j + l; j1 += 128)
					graphics.method232(i1, j1, 6 + mudclient.SPRITE_UTIL_START, 128);
			}
		}
		graphics.drawLineX(i, j, k, boxGradientTop);
		graphics.drawLineX(i + 1, j + 1, k - 2, boxGradientTop);
		graphics.drawLineX(i + 2, j + 2, k - 4, boxGradientBottom);
		graphics.drawLineY(i, j, l, boxGradientTop);
		graphics.drawLineY(i + 1, j + 1, l - 2, boxGradientTop);
		graphics.drawLineY(i + 2, j + 2, l - 4, boxGradientBottom);
		graphics.drawLineX(i, (j + l) - 1, k, boxBorder2);
		graphics.drawLineX(i + 1, (j + l) - 2, k - 2, boxBorder2);
		graphics.drawLineX(i + 2, (j + l) - 3, k - 4, boxBorder1);
		graphics.drawLineY((i + k) - 1, j, l, boxBorder2);
		graphics.drawLineY((i + k) - 2, j + 1, l - 2, boxBorder2);
		graphics.drawLineY((i + k) - 3, j + 2, l - 4, boxBorder1);
		graphics.resetDimensions();
	}

	public void method147(int i, int j, int k, int l) {
		graphics.drawBox(i, j, k, l, 0);
		graphics.drawBoxEdge(i, j, k, l, anInt212);
		graphics.drawBoxEdge(i + 1, j + 1, k - 2, l - 2, anInt213);
		graphics.drawBoxEdge(i + 2, j + 2, k - 4, l - 4, anInt214);
		graphics.drawPicture(i, j, 2 + mudclient.SPRITE_UTIL_START);
		graphics.drawPicture((i + k) - 7, j, 3 + mudclient.SPRITE_UTIL_START);
		graphics.drawPicture(i, (j + l) - 7, 4 + mudclient.SPRITE_UTIL_START);
		graphics.drawPicture((i + k) - 7, (j + l) - 7, 5 + mudclient.SPRITE_UTIL_START);
	}

	protected void method148(int i, int j, int k) {
		graphics.drawPicture(i, j, k);
	}

	protected void method149(int i, int j, int k) {
		graphics.drawLineX(i, j, k, 0);
	}

	protected void drawChatMessages(int component, int x, int y, int width, int height, int font, String[] list, int listLength, int listEntriesShown) {
		//drawChatMessages(menuObject, componentX[menuObject], componentY[menuObject], componentWidth[menuObject], componentHeight[menuObject], componentTextSize[menuObject], componentTextList[menuObject], listLength[menuObject], listShownEntries[menuObject]);
		int i2 = height / graphics.fontHeight(font);
		if (listEntriesShown > listLength - i2)
			listEntriesShown = listLength - i2;
		if (listEntriesShown < 0)
			listEntriesShown = 0;
		listShownEntries[component] = listEntriesShown;
		if (i2 < listLength) {
			int endX = (x + width) - 12;
			int l2 = ((height - 27) * i2) / listLength;
			if (l2 < 6)
				l2 = 6;
			int j3 = ((height - 27 - l2) * listEntriesShown) / (listLength - i2);
			if (mouseButton == 1 && mouseX >= endX && mouseX <= endX + 12) {
				if (mouseY > y && mouseY < y + 12 && listEntriesShown > 0) {
					listEntriesShown--;
				}
				if (mouseY > (y + height) - 12 && mouseY < y + height && listEntriesShown < listLength - i2) {
					listEntriesShown++;
				}
				listShownEntries[component] = listEntriesShown;
			}
			if (mouseButton == 1 && (mouseX >= endX && mouseX <= endX + 12 || mouseX >= endX - 12 && mouseX <= endX + 24 && aBooleanArray184[component])) {
				if (mouseY > y + 12 && mouseY < (y + height) - 12) {
					aBooleanArray184[component] = true;
					int l3 = mouseY - y - 12 - l2 / 2;
					listEntriesShown = (l3 * listLength) / (height - 24);
					if (listEntriesShown > listLength - i2) {
						listEntriesShown = listLength - i2;
					}
					if (listEntriesShown < 0) {
						listEntriesShown = 0;
					}
					listShownEntries[component] = listEntriesShown;
				}
			} else {
				aBooleanArray184[component] = false;
			}
			j3 = ((height - 27 - l2) * listEntriesShown) / (listLength - i2);
			method151(x, y, width, height, j3, l2);
		}
		int k2 = height - i2 * graphics.fontHeight(font);
		int yy = y + (graphics.fontHeight(font) * 5) / 6 + k2 / 2;
		
		for (int i = listEntriesShown; i < listLength; i++) {
			drawTextWithMask(component, x + 2, yy, list[i], font);
			yy += graphics.fontHeight(font) - chatMenuTextHighlightedMod;
			if (yy >= y + height) {
				return;
			}
		}

	}

	protected void method151(int i, int j, int k, int l, int i1, int j1) {
		int k1 = (i + k) - 12;
		graphics.drawBoxEdge(k1, j, 12, l, 0);
		graphics.drawPicture(k1 + 1, j + 1, mudclient.SPRITE_UTIL_START);
		graphics.drawPicture(k1 + 1, (j + l) - 12,
				1 + mudclient.SPRITE_UTIL_START);
		graphics.drawLineX(k1, j + 13, 12, 0);
		graphics.drawLineX(k1, (j + l) - 13, 12, 0);
		graphics.method214(k1 + 1, j + 14, 11, l - 27, scrollBarGradientColorTop, scrollBarGradientColorBottom);
		graphics.drawBox(k1 + 3, i1 + j + 14, 7, j1, scrollBarDraggingBarColor);
		graphics.drawLineY(k1 + 2, i1 + j + 14, j1, scrollBarDraggingBarLine1Color);
		graphics.drawLineY(k1 + 2 + 8, i1 + j + 14, j1, scrollBarDraggingBarLine2Color);
	}

	protected void method152(int i, int j, int k, int l, String as[]) {
		int i1 = 0;
		int j1 = as.length;
		for (int k1 = 0; k1 < j1; k1++) {
			i1 += graphics.textWidth(as[k1], l);
			if (k1 < j1 - 1)
				i1 += graphics.textWidth("  ", l);
		}

		int l1 = j - i1 / 2;
		int i2 = k + graphics.fontHeight(l) / 3;
		for (int j2 = 0; j2 < j1; j2++) {
			int k2;
			if (componentWhiteText[i])
				k2 = 0xffffff;
			else
				k2 = 0;
			if (mouseX >= l1 && mouseX <= l1 + graphics.textWidth(as[j2], l)
					&& mouseY <= i2
					&& mouseY > i2 - graphics.fontHeight(l)) {
				if (componentWhiteText[i])
					k2 = 0x808080;
				else
					k2 = 0xffffff;
				if (lastMouseButton == 1) {
					anIntArray189[i] = j2;
					componentSkip[i] = true;
				}
			}
			if (anIntArray189[i] == j2)
				if (componentWhiteText[i])
					k2 = 0xff0000;
				else
					k2 = 0xc00000;
			graphics.drawString(as[j2], l1, i2, l, k2);
			l1 += graphics.textWidth(as[j2] + "  ", l);
		}
		//adm
	}

	protected void method153(int i, int j, int k, int l, String as[]) {
		int i1 = as.length;
		int j1 = k - (graphics.fontHeight(l) * (i1 - 1)) / 2;
		for (int k1 = 0; k1 < i1; k1++) {
			int l1;
			if (componentWhiteText[i])
				l1 = 0xffffff;
			else
				l1 = 0;
			int i2 = graphics.textWidth(as[k1], l);
			if (mouseX >= j - i2 / 2 && mouseX <= j + i2 / 2
					&& mouseY - 2 <= j1
					&& mouseY - 2 > j1 - graphics.fontHeight(l)) {
				if (componentWhiteText[i])
					l1 = 0x808080;
				else
					l1 = 0xffffff;
				if (lastMouseButton == 1) {
					anIntArray189[i] = k1;
					componentSkip[i] = true;
				}
			}
			if (anIntArray189[i] == k1)
				if (componentWhiteText[i])
					l1 = 0xff0000;
				else
					l1 = 0xc00000;
			graphics.drawString(as[k1], j - i2 / 2, j1, l, l1);
			j1 += graphics.fontHeight(l);
		}

	}

	protected void method154(int i, int j, int k, int l, int i1, int j1,
			String as[], int k1, int l1) {
		int i2 = i1 / graphics.fontHeight(j1);
		if (i2 < k1) {
			int j2 = (j + l) - 12;
			int l2 = ((i1 - 27) * i2) / k1;
			if (l2 < 6)
				l2 = 6;
			int j3 = ((i1 - 27 - l2) * l1) / (k1 - i2);
			if (mouseButton == 1 && mouseX >= j2 && mouseX <= j2 + 12) {
				if (mouseY > k && mouseY < k + 12 && l1 > 0)
					l1--;
				if (mouseY > (k + i1) - 12 && mouseY < k + i1 && l1 < k1 - i2)
					l1++;
				listShownEntries[i] = l1;
			}
			if (mouseButton == 1
					&& (mouseX >= j2 && mouseX <= j2 + 12 || mouseX >= j2 - 12
					&& mouseX <= j2 + 24 && aBooleanArray184[i])) {
				if (mouseY > k + 12 && mouseY < (k + i1) - 12) {
					aBooleanArray184[i] = true;
					int l3 = mouseY - k - 12 - l2 / 2;
					l1 = (l3 * k1) / (i1 - 24);
					if (l1 < 0)
						l1 = 0;
					if (l1 > k1 - i2)
						l1 = k1 - i2;
					listShownEntries[i] = l1;
				}
			} else {
				aBooleanArray184[i] = false;
			}
			j3 = ((i1 - 27 - l2) * l1) / (k1 - i2);
			method151(j, k, l, i1, j3, l2);
		} else {
			l1 = 0;
			listShownEntries[i] = 0;
		}
		anIntArray190[i] = -1;
		int k2 = i1 - i2 * graphics.fontHeight(j1);
		int i3 = k + (graphics.fontHeight(j1) * 5) / 6 + k2 / 2;
		for (int k3 = l1; k3 < k1; k3++) {
			int i4;
			if (componentWhiteText[i])
				i4 = 0xffffff;
			else
				i4 = 0;
			if (mouseX >= j + 2
					&& mouseX <= j + 2 + graphics.textWidth(as[k3], j1)
					&& mouseY - 2 <= i3
					&& mouseY - 2 > i3 - graphics.fontHeight(j1)) {
				if (componentWhiteText[i])
					i4 = 0x808080;
				else
					i4 = 0xffffff;
				anIntArray190[i] = k3;
				if (lastMouseButton == 1) {
					anIntArray189[i] = k3;
					componentSkip[i] = true;
				}
			}
			if (anIntArray189[i] == k3 && redStringText)
				i4 = 0xff0000;
			graphics.drawString(as[k3], j + 2, i3, j1, i4);
			i3 += graphics.fontHeight(j1);
			if (i3 >= k + i1)
				return;
		}

	}

	public int createLabel(int x, int y, String text, int fontSize, boolean whiteText) {
		componentType[menuObjectCount] = 1;
		componentAcceptsInput[menuObjectCount] = true;
		componentSkip[menuObjectCount] = false;
		componentTextSize[menuObjectCount] = fontSize;
		componentWhiteText[menuObjectCount] = whiteText;
		componentX[menuObjectCount] = x;
		componentY[menuObjectCount] = y;
		componentText[menuObjectCount] = text;
		return menuObjectCount++;
	}
	
	public int createTransparentLabel(int x, int y, String text, int fontSize, boolean whiteText) {
		componentType[menuObjectCount] = 15;
		componentAcceptsInput[menuObjectCount] = true;
		componentSkip[menuObjectCount] = false;
		componentTextSize[menuObjectCount] = fontSize;
		componentWhiteText[menuObjectCount] = whiteText;
		componentX[menuObjectCount] = x;
		componentY[menuObjectCount] = y;
		componentText[menuObjectCount] = text;
		return menuObjectCount++;
	}

	public int addButton(int i, int j, int k, int l) {
		componentType[menuObjectCount] = 2;
		componentAcceptsInput[menuObjectCount] = true;
		componentSkip[menuObjectCount] = false;
		componentX[menuObjectCount] = i - k / 2;
		componentY[menuObjectCount] = j - l / 2;
		componentWidth[menuObjectCount] = k;
		componentHeight[menuObjectCount] = l;
		return menuObjectCount++;
	}

	public int method157(int i, int j, int k, int l) {
		componentType[menuObjectCount] = 11;
		componentAcceptsInput[menuObjectCount] = true;
		componentSkip[menuObjectCount] = false;
		componentX[menuObjectCount] = i - k / 2;
		componentY[menuObjectCount] = j - l / 2;
		componentWidth[menuObjectCount] = k;
		componentHeight[menuObjectCount] = l;
		return menuObjectCount++;
	}

	public int method158(int i, int j, int k) {
		int l = graphics.sprites[k].getWidth();
		int i1 = graphics.sprites[k].getHeight();
		componentType[menuObjectCount] = 12;
		componentAcceptsInput[menuObjectCount] = true;
		componentSkip[menuObjectCount] = false;
		componentX[menuObjectCount] = i - l / 2;
		componentY[menuObjectCount] = j - i1 / 2;
		componentWidth[menuObjectCount] = l;
		componentHeight[menuObjectCount] = i1;
		componentTextSize[menuObjectCount] = k;
		return menuObjectCount++;
	}

	public int createChatMenuList(int x, int y, int width, int height, int font, int maxLen, boolean white) {
		componentType[menuObjectCount] = 4;
		componentAcceptsInput[menuObjectCount] = true;
		componentSkip[menuObjectCount] = false;
		componentX[menuObjectCount] = x;
		componentY[menuObjectCount] = y;
		componentWidth[menuObjectCount] = width;
		componentHeight[menuObjectCount] = height;
		componentWhiteText[menuObjectCount] = white;
		componentTextSize[menuObjectCount] = font;
		componentInputMaxLength[menuObjectCount] = maxLen;
		listLength[menuObjectCount] = 0;
		listShownEntries[menuObjectCount] = 0;
		componentTextList[menuObjectCount] = new String[maxLen];
		return menuObjectCount++;
	}

	public int createChatInput(int x, int y, int width, int height, int font, int maxLen, boolean password, boolean white) {
		componentType[menuObjectCount] = 5;
		componentAcceptsInput[menuObjectCount] = true;
		componentIsPasswordField[menuObjectCount] = password;
		componentSkip[menuObjectCount] = false;
		componentTextSize[menuObjectCount] = font;
		componentWhiteText[menuObjectCount] = white;
		componentX[menuObjectCount] = x;
		componentY[menuObjectCount] = y;
		componentWidth[menuObjectCount] = width;
		componentHeight[menuObjectCount] = height;
		componentInputMaxLength[menuObjectCount] = maxLen;
		componentText[menuObjectCount] = "";
		return menuObjectCount++;
	}

	public int createInput(int x, int y, int width, int height, int fontSize, int maxLength, boolean password, boolean white) {
		componentType[menuObjectCount] = 6;
		componentAcceptsInput[menuObjectCount] = true;
		componentIsPasswordField[menuObjectCount] = password;
		componentSkip[menuObjectCount] = false;
		componentTextSize[menuObjectCount] = fontSize;
		componentWhiteText[menuObjectCount] = white;
		componentX[menuObjectCount] = x;
		componentY[menuObjectCount] = y;
		componentWidth[menuObjectCount] = width;
		componentHeight[menuObjectCount] = height;
		componentInputMaxLength[menuObjectCount] = maxLength;
		componentText[menuObjectCount] = "";
		return menuObjectCount++;
	}

	public int method162(int i, int j, int k, int l, int i1, int j1, boolean flag) {
		componentType[menuObjectCount] = 9;
		componentAcceptsInput[menuObjectCount] = true;
		componentSkip[menuObjectCount] = false;
		componentTextSize[menuObjectCount] = i1;
		componentWhiteText[menuObjectCount] = flag;
		componentX[menuObjectCount] = i;
		componentY[menuObjectCount] = j;
		componentWidth[menuObjectCount] = k;
		componentHeight[menuObjectCount] = l;
		componentInputMaxLength[menuObjectCount] = j1;
		componentTextList[menuObjectCount] = new String[j1];
		listLength[menuObjectCount] = 0;
		listShownEntries[menuObjectCount] = 0;
		anIntArray189[menuObjectCount] = -1;
		anIntArray190[menuObjectCount] = -1;
		return menuObjectCount++;
	}

	public int createButton(int i, int j, int k, int l) {
		componentType[menuObjectCount] = 10;
		componentAcceptsInput[menuObjectCount] = true;
		componentSkip[menuObjectCount] = false;
		componentX[menuObjectCount] = i - k / 2;
		componentY[menuObjectCount] = j - l / 2;
		componentWidth[menuObjectCount] = k;
		componentHeight[menuObjectCount] = l;
		return menuObjectCount++;
	}

	public void clearList(int menuHandle) {
		listLength[menuHandle] = 0;
	}

	public void switchList(int i, int base) {
		listShownEntries[i] = base;
		anIntArray190[i] = -1;
	}

	public void addListItem(int menuHandle, int index, String text) {
		componentTextList[menuHandle][index] = text;
		if (index + 1 > listLength[menuHandle])
			listLength[menuHandle] = index + 1;
	}

	public void addMessage(int i, String s, boolean flag) {
		int j = listLength[i]++;
		if (j >= componentInputMaxLength[i]) {
			j--;
			listLength[i]--;
			for (int k = 0; k < j; k++)
				componentTextList[i][k] = componentTextList[i][k + 1];

		}
		componentTextList[i][j] = s;
		if (flag)
			listShownEntries[i] = 0xf423f;
	}

	public void setText(int component, String text) {
		componentText[component] = text;
	}

	public String getText(int component) {
		if (componentText[component] == null)
			return "null";
		else
			return componentText[component];
	}

	public void acceptInput(int i) {
		componentAcceptsInput[i] = true;
	}

	public void declineInput(int i) {
		componentAcceptsInput[i] = false;
	}

	public void setFocus(int i) {
		selectedComponent = i;
	}

	public int getHighlightedEntry(int i) {
		int j = anIntArray190[i];
		return j;
	}

	public int getMenuIndex(int i) {
		return listShownEntries[i];
	}

	protected JagGraphics graphics;
	int menuObjectCount;
	public boolean componentAcceptsInput[];
	public boolean aBooleanArray184[];
	public boolean componentIsPasswordField[];
	public boolean componentSkip[];
	public int listShownEntries[];
	public int listLength[];
	public int anIntArray189[];
	public int anIntArray190[];
	boolean componentWhiteText[];
	int componentX[];
	int componentY[];
	int componentType[];
	int componentWidth[];
	int componentHeight[];
	int componentInputMaxLength[];
	int componentTextSize[];
	String componentText[];
	String componentTextList[][];
	int mouseX;
	int mouseY;
	int lastMouseButton;
	int mouseButton;
	int selectedComponent;
	int mouseClicksConsecutive;
	int scrollBarGradientColorTop;
	int scrollBarGradientColorBottom;
	int scrollBarDraggingBarLine1Color;
	int scrollBarDraggingBarColor;
	int scrollBarDraggingBarLine2Color;
	int anInt212;
	int anInt213;
	int anInt214;
	public boolean redStringText;
	public static boolean aBoolean220 = true;
	public static int redModifier = 114;
	public static int greeModifier = 114;
	public static int blueModifier = 176;
	public static int chatMenuTextHighlightedMod;

}
