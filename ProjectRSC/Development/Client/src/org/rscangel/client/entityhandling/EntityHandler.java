package org.rscangel.client.entityhandling;

import java.util.ArrayList;

import org.rscangel.client.mudclient;
import org.rscangel.client.util.PersistenceManager;

import defs.DoorDef;
import defs.ElevationDef;
import defs.GameObjectDef;
import defs.ItemDef;
import defs.NPCDef;
import defs.PrayerDef;
import defs.SpellDef;
import defs.TileDef;
import defs.extras.AnimationDef;
import defs.extras.TextureDef;

/**
 * This class handles the loading of entities from the conf files, and provides
 * methods for relaying these entities to the user.
 */
public class EntityHandler {

	private static NPCDef[] npcs;
	private static ItemDef[] items;
	private static TextureDef[] textures;
	private static AnimationDef[] animations;
	private static SpellDef[] spells;
	private static PrayerDef[] prayers;
	private static TileDef[] tiles;
	private static DoorDef[] doors;
	private static ElevationDef[] elevation;
	private static GameObjectDef[] objects;

	private static ArrayList<String> models = new ArrayList<String>();
	private static int invPictureCount = 0;

	public static int getModelCount() {
		return models.size();
	}

	public static String getModelName(int id) {
		if (id < 0 || id >= models.size()) {
			return null;
		}
		return models.get(id);
	}

	public static int invPictureCount() {
		return invPictureCount;
	}

	public static int npcCount() {
		return npcs.length;
	}

	public static NPCDef getNpcDef(int id) {
		if (id < 0 || id >= npcs.length) {
			return null;
		}
		return npcs[id];
	}

	public static int itemCount() {
		return items.length;
	}

	public static ItemDef getItemDef(int id) {
		if (id < 0 || id >= items.length) {
			return null;
		}
		return items[id];
	}

	public static int textureCount() {
		return textures.length;
	}

	public static TextureDef getTextureDef(int id) {
		if (id < 0 || id >= textures.length) {
			return null;
		}
		return textures[id];
	}

	public static int animationCount() {
		return animations.length;
	}

	public static AnimationDef getAnimationDef(int id) {
		if (id < 0 || id >= animations.length) {
			return null;
		}
		return animations[id];
	}

	public static int spellCount() {
		return spells.length;
	}

	public static SpellDef getSpellDef(int id) {
		if (id < 0 || id >= spells.length) {
			return null;
		}
		return spells[id];
	}

	public static int prayerCount() {
		return prayers.length;
	}

	public static PrayerDef getPrayerDef(int id) {
		if (id < 0 || id >= prayers.length) {
			return null;
		}
		return prayers[id];
	}

	public static int tileCount() {
		return tiles.length;
	}

	public static TileDef getTileDef(int id) {
		if (id < 0 || id >= tiles.length) {
			return null;
		}
		return tiles[id];
	}

	public static int doorCount() {
		return doors.length;
	}

	public static DoorDef getDoorDef(int id) {
		if (id < 0 || id >= doors.length) {
			return null;
		}
		return doors[id];
	}

	public static int elevationCount() {
		return elevation.length;
	}

	public static ElevationDef getElevationDef(int id) {
		if (id < 0 || id >= elevation.length) {
			return null;
		}
		return elevation[id];
	}

	public static int objectCount() {
		return objects.length;
	}

	public static GameObjectDef getObjectDef(int id) {
		if (id < 0 || id >= objects.length) {
			return null;
		}
		return objects[id];
	}

	public static void load(mudclient mc) {
		npcs = (NPCDef[]) PersistenceManager.load(mudclient.loadCachedFile("NPCs.rscd"));
		mc.drawLoadingBarText(3, "Checking local data files");
		
		items = (ItemDef[]) PersistenceManager.load(mudclient.loadCachedFile("Items.rscd"));
		mc.drawLoadingBarText(5, "Checking local data files");
		
		textures = (TextureDef[]) PersistenceManager.load(mudclient.loadCachedFile("Textures.rscd"));
		mc.drawLoadingBarText(7, "Checking local data files");		
		
		animations = (AnimationDef[]) PersistenceManager.load(mudclient.loadCachedFile("Animations.rscd"));
		mc.drawLoadingBarText(9, "Checking local data files");
		
		spells = (SpellDef[]) PersistenceManager.load(mudclient.loadCachedFile("Spells.rscd"));
		mc.drawLoadingBarText(11, "Checking local data files");
		
		prayers = (PrayerDef[]) PersistenceManager.load(mudclient.loadCachedFile("Prayers.rscd"));
		mc.drawLoadingBarText(13, "Checking local data files");
		
		tiles = (TileDef[]) PersistenceManager.load(mudclient.loadCachedFile("Tiles.rscd"));
		mc.drawLoadingBarText(15, "Checking local data files");
		
		doors = (DoorDef[]) PersistenceManager.load(mudclient.loadCachedFile("Doors.rscd"));
		mc.drawLoadingBarText(17, "Checking local data files");
		
		elevation = (ElevationDef[]) PersistenceManager.load(mudclient.loadCachedFile("Elevation.rscd"));
		mc.drawLoadingBarText(19, "Checking local data files");
		
		objects = (GameObjectDef[]) PersistenceManager.load(mudclient.loadCachedFile("Objects.rscd"));
		mc.drawLoadingBarText(21, "Checking local data files");
		
		for (int id = 0; id < items.length; id++) {
			if (items[id].getSprite() + 1 > invPictureCount) {
				invPictureCount = items[id].getSprite() + 1;
			}
		}

		for (int id = 0; id < objects.length; id++) {
			objects[id].modelID = storeModel(objects[id].getObjectModel());
		}
	}

	public static int storeModel(String name) {
		if (name.equalsIgnoreCase("na")) {
			return 0;
		}
		int index = models.indexOf(name);
		if (index < 0) {
			models.add(name);
			return models.size() - 1;
		}
		return index;
	}

}
