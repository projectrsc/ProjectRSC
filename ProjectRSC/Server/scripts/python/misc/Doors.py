from com.prsc.gs.plugins.listeners.action import WallObjectActionListener
from com.prsc.gs.plugins.listeners.executive import WallObjectActionExecutiveListener
from com.prsc.gs.plugins import PlugInterface
from com.prsc.gs.model import World, GameObject
from com.prsc.gs.external import GameObjectLoc
from com.prsc.config import Constants

'''
@author: GORF
Called when a user will be opening/closing a door
'''

class Doors(PlugInterface, WallObjectActionListener, WallObjectActionExecutiveListener):
	
	# ITEMS USED
	BRONZE_MED_HELMET = 104
	IRON_CHAIN = 7
	CHEFS_HAT = 192
	CHEFS_APRON = 182
	CRAFTERS_APRON = 191
	
	# NPCS USED
	COOKING_GUILD_COOK = 133
	MINING_GUILD_DWARF = 191
	CRAFTING_GUILD_MASTER = 231
	
	# LEVELS NEEDED FOR ACCESS
	REQUIRED_COOKING_GUILD_LEVEL = 32
	REQUIRED_CRAFTING_GUILD_LEVEL = 40
	REQUIRED_MINING_GUILD_LEVEL = 60
	REQUIRED_SKILLTOTAL_LEVEL = 800
	
	def onWallObjectAction(self, gameObject, command, player):
		script = player.getScriptHelper()
		door_id = gameObject.getID()
		print("Door: " + str(door_id))
		
		if door_id == 1:
			self.replaceGameObject(2, False, script, gameObject)
			
		elif door_id == 2:
			self.replaceGameObject(1, True, script, gameObject)
			
		elif door_id == 8:
			self.replaceGameObject(9, True, script, gameObject)
			
		elif door_id == 9:
			self.replaceGameObject(8, False, script, gameObject)
			
		elif door_id == 20:
			self.replaceGameObject(1, True, script, gameObject)
			
		elif door_id == 22: # EDGE DUNGEON WALL
			if gameObject.getX() == 219 and gameObject.getY() == 3282:
				self.doSecretDoor(script, gameObject)
				World.getWorld().delayedSpawnObject(gameObject.getLoc(), 1000)
				script.displayMessage("You just went through a secret door")
				if player.getX() <= 218:
					script.movePlayer(219, 3282, False)
				else:
					script.movePlayer(218, 3282, False)
			elif gameObject.getX() == 273 and gameObject.getY() == 435:
				self.doSecretDoor(script, gameObject)
				World.getWorld().delayedSpawnObject(gameObject.getLoc(), 1000)
				script.displayMessage("You just went through a secret door")
				if player.getY() >= 435:
					script.movePlayer(273, 434, False)
				else:
					script.movePlayer(273, 435, False)
			elif gameObject.getX() == 281 and gameObject.getY() == 2325:
				self.doSecretDoor(script, gameObject)
				World.getWorld().delayedSpawnObject(gameObject.getLoc(), 1000)
				script.displayMessage("You just went through a secret door")
				if player.getY() >= 2325:
					script.movePlayer(281, 2324, False)
				else:
					script.movePlayer(281, 2325, False)
			else:
				script.displayMessage("Nothing interesting happens")
				
		elif door_id == 23 or door_id == 30 or door_id == 94 or door_id == 142:
			script.displayMessage("The door is locked")
			
		elif door_id == 36: # DRAYNOR MANSION FRONT DOOR
			if gameObject.getX() != 210 or gameObject.getY() != 553:
				return
			if player.getY() >= 553:
				self.doNormalDoor(script, gameObject)
				World.getWorld().delayedSpawnObject(gameObject.getLoc(), 1000)
				script.movePlayer(210, 552, False)
			else:
				script.displayMessage("The door is locked shut")
				
		elif door_id == 37: # DRAYNOR MANSION BACK DOOR
			if gameObject.getX() != 199 or gameObject.getY() != 551:
				return
			if player.getY() >= 551:
				self.doNormalDoor(script, gameObject)
				World.getWorld().delayedSpawnObject(gameObject.getLoc(), 1000)
				script.movePlayer(199, 550, False)
			else:
				script.displayMessage("The door is locked shut")
				
		elif door_id == 38: # BLACK KNIGHTS GUARD DOOR
			if gameObject.getX() != 271 or gameObject.getY() != 441:
				return
			if player.getX() <= 270:
				if not script.isWielding(self.BRONZE_MED_HELMET) and not script.isWielding(self.IRON_CHAIN):
					script.displayMessage("Only guards are allowed in there!")
					return
				self.doNormalDoor(script, gameObject)
				World.getWorld().delayedSpawnObject(gameObject.getLoc(), 1000)
				script.movePlayer(271, 441, False)
			else:
				self.doNormalDoor(script, gameObject)
				World.getWorld().delayedSpawnObject(gameObject.getLoc(), 1000)
				script.movePlayer(270, 441, False)
				
		elif door_id == 43: # COOKING GUILD DOOR
			if gameObject.getX() != 179 or gameObject.getY() != 488:
				return
			if player.getY() >= 488:
				chef_npc = script.closestNpc(self.COOKING_GUILD_COOK, 10)
				if chef_npc == None:
					return
				script.setActiveNpc(chef_npc)
				if script.getCurrentLevel(player.SkillType.COOKING) < self.REQUIRED_COOKING_GUILD_LEVEL:
					script.occupy()
					script.sendNpcChat("Hello only the top cooks are allowed in here")
					script.sleep(2000)
					script.displayMessage("You need a cooking level of " + str(self.REQUIRED_COOKING_GUILD_LEVEL) + " to enter")
					script.release()
				elif not script.isWielding(self.CHEFS_HAT):
					script.occupy()
					script.sendNpcChat("Where is your chef's hat?")
					script.release()
				elif not script.isWielding(self.CHEFS_APRON):
					script.occupy()
					script.sendNpcChat("Where is your apron?")
					script.release()
				else:
					self.doNormalDoor(script, gameObject)
					World.getWorld().delayedSpawnObject(gameObject.getLoc(), 1000)
					script.movePlayer(179, 487, False)
			else:
				self.doNormalDoor(script, gameObject)
				World.getWorld().delayedSpawnObject(gameObject.getLoc(), 1000)
				script.movePlayer(179, 488, False)
			
		elif door_id == 44: # CHAMPS GUILD DOOR
			if gameObject.getX() != 150 and gameObject.getY() != 554:
				return
			if player.getSkillTotal() < self.REQUIRED_SKILLTOTAL_LEVEL:
				script.displayMessage("You need a total of " + str(self.REQUIRED_SKILLTOTAL_LEVEL) + "skill total to enter this guild.")
				return
			self.doNormalDoor(script, gameObject)
			World.getWorld().delayedSpawnObject(gameObject.getLoc(), 1000)
			if player.getY() >= 554:
				script.movePlayer(150, 553, False)
			else:
				script.movePlayer(150, 554, False)
		
		elif door_id == 55: # MINING GUILD DOOR
			if gameObject.getX() != 268 or gameObject.getY() != 3381:
				return
			if player.getY() <= 3380:
				dwarf_npc = script.closestNpc(self.MINING_GUILD_DWARF, 10)
				if dwarf_npc == None:
					return
				script.setActiveNpc(dwarf_npc)
				if script.getCurrentLevel(player.SkillType.MINING) < self.REQUIRED_MINING_GUILD_LEVEL:
					script.occupy()
					script.sendNpcChat("Hello only the top miners are allowed in here")
					script.sleep(2000)
					script.displayMessage("You need a mining level of " + str(self.REQUIRED_MINING_GUILD_LEVEL) + " to enter")
					script.release()
				else:
					self.doNormalDoor(script, gameObject)
					World.getWorld().delayedSpawnObject(gameObject.getLoc(), 1000)
					script.movePlayer(268, 3381, False)
			else:
				self.doNormalDoor(script, gameObject)
				World.getWorld().delayedSpawnObject(gameObject.getLoc(), 1000)
				script.movePlayer(268, 3380, False)
		
		elif door_id == 58 and command == "push": # Karamja -> cranador wall
			if gameObject.getX() != 406 or gameObject.getY() != 3518:
				return
			script.displayMessage("Nothing interesting happens")
		
		elif door_id == 68: # CRAFTING GUILD DOOR
			if gameObject.getX() != 347 or gameObject.getY() != 601:
				return
			if player.getY() <= 600:
				crafting_npc = script.closestNpc(self.CRAFTING_GUILD_MASTER, 10)
				if crafting_npc == None:
					return
				script.setActiveNpc(crafting_npc)
				if script.getCurrentLevel(player.SkillType.CRAFTING) < self.REQUIRED_CRAFTING_GUILD_LEVEL:
					script.occupy()
					script.sendNpcChat("Hello only the top crafters are allowed in here")
					script.sleep(2000)
					script.displayMessage("You need a crafting level of " + str(self.REQUIRED_CRAFTING_GUILD_LEVEL) + " to enter")
					script.release()
				elif not script.isWielding(self.CRAFTERS_APRON):
					script.occupy()
					script.sendNpcChat("Where is your apron?")
					script.release()
				else:
					self.doNormalDoor(script, gameObject)
					World.getWorld().delayedSpawnObject(gameObject.getLoc(), 1000)
					script.movePlayer(347, 601, False)
			else:
				self.doNormalDoor(script, gameObject)
				World.getWorld().delayedSpawnObject(gameObject.getLoc(), 1000)
				script.movePlayer(347, 600, False)
		
		elif door_id == 74: # HEROS GUILD DOOR (NEEDS TO FINISH GOOD PORTION OF QUESTS)
			if gameObject.getX() != 372 or gameObject.getY() != 441:
				return
			
			self.doNormalDoor(script, gameObject)
			World.getWorld().delayedSpawnObject(gameObject.getLoc(), 1000)
			if player.getY() >= 441:
				script.movePlayer(372, 440, False)
			else:
				script.movePlayer(372, 441, False)
		
		else:
			script.displayMessage("Nothing interesting happens")
	
	def doNormalDoor(self, script, gameObject):
		script.sendSound("opendoor")
		World.getWorld().unregisterGameObject(gameObject)
        
	def doSecretDoor(self, script, gameObject):
		script.sendSound("secretdoor")
		World.getWorld().unregisterGameObject(gameObject)
	
	def replaceGameObject(self, new_id, open, script, object):
		if open:
			script.displayMessage("The door swings open")
			script.sendSound("opendoor")
		else:
			script.displayMessage("The door creaks shut")
			script.sendSound("closedoor")
			
		World.getWorld().unregisterGameObject(object)
		World.getWorld().registerGameObject(GameObject(object.getLocation(), new_id, object.getDirection(), object.getType()))
	
	def blockWallObjectAction(self, gameObj, click, player):
		command = ""
		
		if gameObj.getDoorDef() == None:
			return False
		
		if click == 0:
			command = gameObj.getDoorDef().getCommand1()
		else:
			command = gameObj.getDoorDef().getCommand2()	
			
		return command == "open" or command == "close" or command == "push"
