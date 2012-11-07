package org.rscangel.client.gfx;

import java.awt.Component;

import org.rscangel.client.mudclient;

public final class GameGraphics extends JagGraphics {

	public GameGraphics(int width, int height, int spriteCount, Component component) {
		super(width, height, spriteCount, component);
	}

	public final void drawVisibleEntity(int x, int y, int width, int height, int entityId, int param1, int param2) {
		if (entityId >= 50000) {
			client.drawTeleBubble(x, y, width, height, entityId - 50000, param1, param2);
			return;
		}
		if (entityId >= 40000) {
			client.drawItem(x, y, width, height, entityId - 40000, param1, param2);
			return;
		}
		if (entityId >= 20000) {
			client.drawNpc(x, y, width, height, entityId - 20000, param1, param2);
			return;
		}
		if (entityId >= 5000) {
			client.drawPlayer(x, y, width, height, entityId - 5000, param1, param2);
			return;
		}
		super.drawSprite(x, y, width, height, entityId);
	}

	public mudclient client;
}
