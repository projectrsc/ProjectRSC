from org.darkquest.gs.plugins.listeners.action import ObjectActionListener
from org.darkquest.gs.plugins.listeners.executive import ObjectActionExecutiveListener
from org.darkquest.gs.plugins import PlugInterface
from org.darkquest.gs.external import EntityHandler, GameObjectLoc, ObjectMiningDef, ItemDef
from org.darkquest.gs.model import Entity, Point, GameObject
from org.darkquest.config import Constants, Formulae
from org.darkquest.gs.world import World
from org.darkquest.gs.tools import DataConversions

'''
@author: GORF
Mining skill
'''

class Mining(PlugInterface, ObjectActionListener, ObjectActionExecutiveListener):
	
	VALID_ROCKS = [176, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 195, 196, 210, 211]
	VALID_PICKAXES = [1262, 1261, 1260, 1259, 1258, 156]
	PICKAXE_LEVELS = [41, 31, 21, 6, 1, 1]
	
	REQ_USE_LEVELS = {1262:41, 1261:31, 1260:21, 1259:6, 1258:1, 156:1}
	RETRIES = {1262:12, 1261:8, 1260:5, 1259:3, 1258:2, 156:1}
	BONUS = {1262:12, 1261:10, 1260:8, 1259:6, 1258:4, 156:2}
	
	def onObjectAction(self, gameObject, command, player):
		script = player.getScriptHelper()
		mining_def = EntityHandler.getObjectMiningDef(gameObject.getID())
		cur_level = script.getCurrentLevel(player.SkillType.MINING)
		
		if player.isBusy() or not player.withinRange(gameObject, 1):
			return
		
		if mining_def == None or mining_def.getRespawnTime() < 1:
			script.displayMessage("There is currently no ore available in this rock.")
			return
		
		script.occupy()
		
		if player.click == 1:
			prospected_ore = script.getItem(mining_def.getOreId())
			script.displayMessage("You examine the rock for ores")
			script.sleep(2000)
			script.displayMessage("This rock contains " + str(prospected_ore.getDef().getName()) + ".")
			script.release()
			return
		
		axe_id = self.determineAxe(cur_level, script)
		
		if axe_id < 0:
			script.displayMessage("You need a pickaxe to mine this rock.")
			script.release()
			return
		
		retries = self.determineAccuracy(axe_id)
		req_use_level = self.determineReqUseLevel(axe_id)
		req_ore_level = mining_def.getReqLevel()
		
		if req_use_level > cur_level: # PICKAXE REQ LEVEL CHECK
			script.displayMessage("You need to be level " + str(req_use_level) + " to use this pick.")
			script.release()
			return
		elif cur_level < req_ore_level: # ORE REQ LEVEL CHECK
			script.displayMessage("You need to be level " + str(req_ore_level) + " to mine this ore.")
			script.release()
			return
		elif player.getFatigue() >= 7500: # FATIGUE CHECK
			script.displayMessage("You are too tired to mine this rock")
			script.release()
			return

		self.handleMining(gameObject, script, mining_def, axe_id, retries)
		script.release()
	
	def determineAxe(self, cur_level, script):
		for axe in self.VALID_PICKAXES:
			for level in self.PICKAXE_LEVELS:
				if script.hasItem(axe):
					if cur_level >= level:
						return axe
		return -1
	
	def determineReqUseLevel(self, axe_id):
		if self.REQ_USE_LEVELS.has_key(axe_id):
			return self.REQ_USE_LEVELS[axe_id]
		return -1
	
	def determineAccuracy(self, axe_id):
		if self.RETRIES.has_key(axe_id):
			return self.RETRIES[axe_id]
		return -1
	
	def determineRespawn(self, mining_def, script):
		respawn = mining_def.getRespawnTime() * 1000
		total_players = script.getTotalPlayers()
		
		#if mining_def.getReqLevel() == 20 or mining_def.getReqLevel() == 40: # SILVER/GOLD
		#	respawn = (2 - (total_players / 2000)) * 60000
		#elif mining_def.getReqLevel() == 30: # COAL
		#	respawn = (60 - ((3 * total_players) / 200)) * 1000
		#elif mining_def.getReqLevel() == 55: # MITH
		#	respawn = (4 - (total_players / 1000)) * 60000
		#elif mining_def.getReqLevel() == 70: # ADDY
		#	respawn = (8 - (total_players / 500)) * 60000
		#elif mining_def.getReqLevel() == 85: # RUNE
		#	respawn = (25 - (total_players / 160)) * 60000
		
		return respawn
	
	def getOre(self, script, mining_def, mining_level, axe_id):
		 level_diff = mining_level - mining_def.getReqLevel()
		 
		 if level_diff > 50:
			 return script.getRandom(0, 9) != 1
			
		 if level_diff < 0:
			 return False
		 
		 bonus = self.BONUS[axe_id]
		 level_diff = level_diff + bonus
		 if level_diff > 40:
		 	level_diff = 60
		 else:
		 	level_diff = 20 + level_diff
		 return DataConversions.percentChance(level_diff)
	
	def handleMining(self, game_object, script, mining_def, axe_id, retries):
		player = script.getPlayer()
		if player.lastMineTries == -1: 
			player.lastMineTries = 0
		
		player.lastMineTries = player.lastMineTries + 1
		script.sendSound("mine")
		script.showBubble(1258)
		script.displayMessage("You swing your pick at the rock...")
		self.miningEvent(game_object, script, mining_def, axe_id, retries)
	
	def miningEvent(self, game_object, script, mining_def, axe_id, retries):
		player = script.getPlayer()
		script.sleep(1500)
		ore = script.getItem(mining_def.getOreId())
		
		if self.getOre(script, mining_def, script.getCurrentLevel(player.SkillType.MINING), axe_id):
			if script.getRandom(0, 200) == 0: # FOUND GEM
				script.addItem(Formulae.getGem(), 1)
				script.advanceStat(player.SkillType.MINING, 100, True)
				script.displayMessage("You found a gem!")
				player.lastMineTries = -1
			else: # NORMAL ORE
				script.addItem(mining_def.getOreId(), 1)
				script.displayMessage("You manage to obtain some " + ore.getDef().getName() + ".")
				script.advanceStat(player.SkillType.MINING, mining_def.getExp(), True)
				player.lastMineTries = -1
				respawn = self.determineRespawn(mining_def, script)
				new_rock = script.createNewObject(game_object)
				script.spawnObject(game_object.getLocation(), 98, game_object.getDirection(), game_object.getType(), True, new_rock.getLoc(), respawn)
				if not player.getInventory().full() and Constants.GameServer.BATCH_EVENTS:
					script.sleep(500)
					self.handleMining(ore, script)
		else:
			script.displayMessage("You only succeed in scratching the rock.")
			player.isMining(False)
			script.sleep(500)
			last_tries = player.lastMineTries
			if last_tries < retries:
				self.handleMining(game_object, script, mining_def, axe_id, retries)
		script.release()
    
	def blockObjectAction(self, gameObject, command, player):
		return command == "mine" or command == "prospect" and player.click == 1
