package com.prsc.gs.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import com.prsc.config.Constants;
import com.prsc.config.Formulae;
import com.prsc.gs.Server;
import com.prsc.gs.external.EntityHandler;
import com.prsc.gs.external.GameObjectLoc;
import com.prsc.gs.external.ItemDef;
import com.prsc.gs.external.ItemDropDef;
import com.prsc.gs.external.ItemLoc;
import com.prsc.gs.external.NPCDef;
import com.prsc.gs.external.NPCLoc;
import com.prsc.gs.model.GameObject;
import com.prsc.gs.model.Item;
import com.prsc.gs.model.Npc;
import com.prsc.gs.model.Point;
import com.prsc.gs.util.Logger;
import com.prsc.gs.world.World;

public final class DBConnection {

	static {
		testForDriver();
	}

	private Connection con;

	private Statement statement;

	private static void testForDriver() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException cnfe) {
			cnfe.printStackTrace();
		}
	}

	public DBConnection() {
		if (!createConnection()) {
			System.exit(1);
		}
	}
	
	public boolean createConnection(String database) {
		try {
			con = DriverManager.getConnection("jdbc:mysql://" + Constants.GameServer.MYSQL_HOST + "/" + database, Constants.GameServer.MYSQL_USER, Constants.GameServer.MYSQL_PASS);
			statement = con.createStatement();
			statement.setEscapeProcessing(true);
			return isConnected();
		}
		catch(SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean createConnection() {
		try {
			Server.print("Connecting to MySQL", false);
			con = DriverManager.getConnection("jdbc:mysql://" + Constants.GameServer.MYSQL_HOST + "/" + Constants.GameServer.MYSQL_DB, Constants.GameServer.MYSQL_USER, Constants.GameServer.MYSQL_PASS);
			statement = con.createStatement();
			statement.setEscapeProcessing(true);
			return isConnected();
		} catch (Exception e) {
			Server.print("ERROR", true);
			Logger.error(e);
			return false;
		} finally {
			Server.print("COMPLETE", true);
		}
	}

	public boolean isConnected() {
		try {
			statement.executeQuery("SELECT CURRENT_DATE");
			return true;
		} catch (SQLException e) {
			return false;
		}
	}

	public Connection getConnection() {
		return con;
	}

	public void close() throws SQLException {
		con.close();
		con = null;
	}

	/**
	 * Loads npcdefs, objects, npcs and items from the Database
	 */
	public void loadObjects(World world) {
		ResultSet result;
		try {
			ArrayList<NPCDef> defs = new ArrayList<NPCDef>();
			result = this.con.createStatement().executeQuery("SELECT * FROM `" + Constants.GameServer.MYSQL_TABLE_PREFIX + "npcdef`");
			while (result.next()) {
				NPCDef def = new NPCDef();
				def.name = result.getString("name");
				def.description = result.getString("description");
				def.command = result.getString("command");
				def.attack = result.getInt("attack");
				def.strength = result.getInt("strength");
				def.hits = result.getInt("hits");
				def.defense = result.getInt("defense");
				def.attackable = result.getBoolean("attackable");
				def.aggressive = result.getBoolean("aggressive");
				def.respawnTime = result.getInt("respawnTime");
				for (int i = 0; i < 12; i++) {
					def.sprites[i] = result.getInt("sprites" + (i + 1));
				}
				def.hairColour = result.getInt("hairColour");
				def.topColour = result.getInt("topColour");
				def.bottomColour = result.getInt("bottomColour");
				def.skinColour = result.getInt("skinColour");
				def.camera1 = result.getInt("camera1");
				def.camera2 = result.getInt("camera2");
				def.walkModel = result.getInt("walkModel");
				def.combatModel = result.getInt("combatModel");
				def.combatSprite = result.getInt("combatSprite");

				ArrayList<ItemDropDef> drops = new ArrayList<ItemDropDef>();
				ResultSet data = this.statement.executeQuery("SELECT * FROM `" + Constants.GameServer.MYSQL_TABLE_PREFIX + "npcdrops` WHERE npcdef_id = '" + result.getInt("id") + "'");
				while (data.next()) {
					ItemDropDef drop = new ItemDropDef(data.getInt("id"), data.getInt("amount"), data.getInt("weight"));
					drops.add(drop);
				}
				def.drops = drops.toArray(new ItemDropDef[]{});
				defs.add(def);
			}
			EntityHandler.npcs = defs.toArray(new NPCDef[]{});
			for (NPCDef n : EntityHandler.npcs) {
				if (n.isAttackable()) {
					n.respawnTime -= (n.respawnTime / 3);
				}
			}
			result = this.statement.executeQuery("SELECT * FROM `" + Constants.GameServer.MYSQL_TABLE_PREFIX + "itemdef` order by id asc");
			ArrayList<ItemDef> itemdefs = new ArrayList<ItemDef>();
			while (result.next()) {
				ItemDef i = new ItemDef();
				i.name = result.getString("name");
				i.basePrice = result.getInt("basePrice");
				i.command = result.getString("command");
				i.mask = result.getInt("mask");
				i.members = result.getInt("members") == 1;
				i.sprite = result.getInt("sprite");
				i.stackable = result.getInt("stackable") == 1;
				i.trade = result.getInt("trade") == 1;
				i.wieldable = result.getInt("wieldable");
				itemdefs.add(i);
			}
			EntityHandler.items = itemdefs.toArray(new ItemDef[]{});
			result = this.statement.executeQuery("SELECT * FROM `" + Constants.GameServer.MYSQL_TABLE_PREFIX + "objects`");
			while (result.next()) {
				Point p = new Point(result.getInt("x"), result.getInt("y"));
				if (Formulae.isP2P(false, p.getX(), p.getY()) && !Constants.GameServer.MEMBER_WORLD) {
					continue;
				} 
				GameObject obj = new GameObject(p, result.getInt("id"), result.getInt("direction"), result.getInt("type"));


				world.registerGameObject(obj);
			}

			result = this.statement.executeQuery("SELECT * FROM `" + Constants.GameServer.MYSQL_TABLE_PREFIX + "npclocs`");
			while (result.next()) {
				NPCLoc n = new NPCLoc(result.getInt("id"), result.getInt("startX"), result.getInt("startY"), result.getInt("minX"), result.getInt("maxX"), result.getInt("minY"), result.getInt("maxY"));
				if (Formulae.isP2P(false, n) && !Constants.GameServer.MEMBER_WORLD) {
					n = null;
					continue;
				} else {

				}

				world.registerNpc(new Npc(n));
			}
			result = this.statement.executeQuery("SELECT * FROM `" + Constants.GameServer.MYSQL_TABLE_PREFIX + "items`");
			while (result.next()) {
				ItemLoc i = new ItemLoc();
				i.id = result.getInt("id");
				i.x = result.getInt("x");
				i.y = result.getInt("y");
				i.amount = result.getInt("amount");
				i.respawnTime = result.getInt("respawn");

				if (!Constants.GameServer.MEMBER_WORLD) {
					if (EntityHandler.getItemDef(i.id).isMembers()) {
						continue;
					}
				}
				if (Formulae.isP2P(false, i) && !Constants.GameServer.MEMBER_WORLD) {
					i = null;
					continue;
				}

				world.registerItem(new Item(i));
			}
		} catch (SQLException e) {
			System.out.println("Unable to load objects from database");
			e.printStackTrace();
			System.exit(1);
		}

	}
	
	public void loadShops() {
		try {
			ResultSet result;
			result = this.statement.executeQuery("SELECT * FROM `" + Constants.GameServer.MYSQL_TABLE_PREFIX + "items`");
		} catch(SQLException e) {
			e.printStackTrace();
		}
	}

	public void storeGroundItemToDatabase(Item item) {
		ItemLoc i = item.getLoc();
		try {
			World.getWorld().getDB().getConnection().createStatement().execute("INSERT INTO `" + Constants.GameServer.MYSQL_TABLE_PREFIX + "items`(`id`, `x`, `y`, `amount`, `respawn`) VALUES ('" + item.getID() + "', '" + i.getX() + "', '" + i.getY() + "', '" + i.getAmount() + "', '" + i.getRespawnTime() + "')");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void storeGameObjectToDatabase(GameObject obj) {
		GameObjectLoc gameObject = obj.getLoc();
		try {
			World.getWorld().getDB().getConnection().createStatement().execute("INSERT INTO `" + Constants.GameServer.MYSQL_TABLE_PREFIX + "objects`(`x`, `y`, `id`, `direction`, `type`) VALUES ('" + gameObject.getX() + "', '" + gameObject.getY() + "', '" + gameObject.getId() + "', '" + gameObject.getDirection() + "', '" + gameObject.getType() + "')");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void deleteGameObjectFromDatabase(GameObject obj) {
		GameObjectLoc gameObject = obj.getLoc();
		try {
			this.statement.execute("DELETE FROM `" + Constants.GameServer.MYSQL_TABLE_PREFIX + "objects` WHERE `x` = '" + gameObject.getX() + "' AND `y` =  '" + gameObject.getY() + "' AND `id` = '" + gameObject.getId() + "' AND `direction` = '" + gameObject.getDirection() + "' AND `type` = '" + gameObject.getType() + "'");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void storeNpcToDatabase(Npc n) {
		NPCLoc npc = n.getLoc();
		try {
			this.statement.execute("INSERT INTO `" + Constants.GameServer.MYSQL_TABLE_PREFIX + "npclocs`(`id`,`startX`,`minX`,`maxX`,`startY`,`minY`,`maxY`) VALUES('" + npc.getId() + "', '" + npc.startX() + "', '" + npc.minX() + "', '" + npc.maxX() + "','" + npc.startY() + "','" + npc.minY() + "','" + npc.maxY() + "')");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
