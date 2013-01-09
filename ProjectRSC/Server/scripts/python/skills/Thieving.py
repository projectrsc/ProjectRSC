from com.prsc.gs.plugins.listeners.action import NpcCommandListener, ObjectActionListener
from com.prsc.gs.plugins.listeners.executive import NpcCommandExecutiveListener, ObjectActionExecutiveListener
from com.prsc.config import Constants
from com.prsc.gs.external import EntityHandler, NPCDef, ItemDef
from com.prsc.gs.model import InvItem
from com.prsc.gs.plugins import PlugInterface

'''
@author: GORF
Thieving skill
'''
class Thieving(PlugInterface, NpcCommandListener, ObjectActionListener, NpcCommandExecutiveListener, ObjectActionExecutiveListener):
	
	# AVAILABLE MONEY POCKED BY NPC
	MONEY_BY_NPC_ID = {11:3, 63:9, 83:18, 65:30, 100:30, 321:30, 322:50}
	
	# EXP GIVEN BY NPC
	EXP_BY_NPC_ID = {11:8, 63:14.5, 83:26, 722:0, 342:35.5, 65:46.5, 100:46.5, 321:46.5, 322:84.5, 574:137.5, 323:152, 551:198, 324:274}
	
	# REQUIRED LEVEL NEEDED FOR NPC
	REQUIRED_LEVEL_BY_NPC_ID = {11:1, 63:10, 83:25, 722:25, 342:32, 65:40, 100:40, 321:40, 322:55, 574:65, 323:70, 551:75, 324:80}
	
	# REQUIRED LEVEL NEEDED FOR OBJ
	REQUIRED_LEVEL_BY_OBJ = {322:1}
	
	# OBJECTS USED
	CAKE_STALL = 322
	SPICES_STALL = 326
	
	# ITEMS USED
	MONEY = 10
	SPECIMEN_BRUSH = 1115
	SPADE = 211
	ROPE = 237
	ROCK_SAMPLE = 1117
	BUCKET = 21
	AIR_RUNE = 33
	LOCKPICK = 714
	WINE = 142
	POISONED_IRON_DAGGER = 559
	BREAD = 138
	CHAOS_RUNE = 41
	GOLD = 152
	EARTH_RUNE = 34
	SWAMP_TOAD = 895
	KING_WORM = 897
	FIRE_ORB = 612
	DIAMOND = 161
	DEATH_RUNE = 38
	BLOOD_RUNE = 619
	CAKE = 330
	
	# SPECIAL LOOT NPCS
	WORKMAN = 722
	ROGUE = 342
	WATCHMEN = 574
	PALADIN = 323 #632, 633?
	GNOME = 551 #?
	HERO = 324
	ARD_GUARD = 321
	BAKER = 325
	
	def onNpcCommand(self, npc, player):
		script = player.getScriptHelper()	
		npc_name = npc.getDef().getName().lower()
		npc_id = npc.getID()
		
		if not player.withinRange(npc, 2) or player.isBusy():
			return
		
		if player.getFatigue() >= 7500:
			script.displayMessage("@gre@You are too tired to gain experience, get some rest")
		
		script.occupy()
		script.setActiveNpc(npc)
		self.handleThieving(script, npc, player)
		script.release()
	
	def handleThieving(self, script, npc, player):
		npc_name = npc.getDef().getName().lower()
		npc_id = npc.getID()
		req_level = self.REQUIRED_LEVEL_BY_NPC_ID[npc_id]
		
		script.displayMessage("You attempt to pick the " + npc_name + "'s pocket")
		script.sleep(1000)
		
		if self.REQUIRED_LEVEL_BY_NPC_ID.has_key(npc_id) and script.getCurrentLevel(player.SkillType.THIEVING) < self.REQUIRED_LEVEL_BY_NPC_ID[npc_id]:
			script.displayMessage("Your theiving ability is not high enough to thieve the " + npc_name)
			return
		# You fail to pick the npc's pocket } what do you think you're doing?
		if self.canThieve(script, player, req_level):
			script.displayMessage("You pick the " + npc_name + "'s pocket")
			script.sleep(300)
			if npc_id == self.WORKMAN:
				script.addItem(self.MONEY, 25)
				script.addItem(self.MONEY, 40)
			elif npc_id == self.ROGUE:
				script.addItem(self.MONEY, 10)
			elif npc_id == self.WATCHMEN:
				script.addItem(self.MONEY, 100)
			elif npc_id == self.PALADIN:
				script.addItem(self.MONEY, 200)
			elif npc_id == self.GNOME:
				script.addItem(self.MONEY, 400)
			elif npc_id == self.HERO:
				script.addItem(self.MONEY, 500)
			elif self.MONEY_BY_NPC_ID.has_key(npc_id):
				script.addItem(self.MONEY, self.MONEY_BY_NPC_ID[npc_id])
			script.advanceStat(player.SkillType.THIEVING, self.EXP_BY_NPC_ID[npc_id], True)
		else:
			script.displayMessage("You fail to pick the " + npc_name + "'s pocket")
			script.sendNpcChat("What do you think you're doing?")
			script.release()
			script.attackPlayer(npc)
	
	def onObjectAction(self, gameObj, command, player):
		script = player.getScriptHelper()
		obj_id = gameObj.getID()
		obj_name = gameObj.getGameObjectDef().getName().lower()
		
		if not player.withinRange(gameObj, 1):
			return
		
		if player.getFatigue() >= 7500:
			script.displayMessage("@gre@You are too tired to gain experience, get some rest")
		
		script.occupy()
		
		if obj_id == self.CAKE_STALL:
			if self.REQUIRED_LEVEL_BY_OBJ.has_key(obj_id) and script.getCurrentLevel(player.SkillType.THIEVING) < self.REQUIRED_LEVEL_BY_OBJ[obj_id]:
				script.displayMessage("Your theiving ability is not high enough to thieve the " + obj_name)
				return
			
			cake = script.getItem(self.CAKE)
			guard = script.closestNpc(self.ARD_GUARD, 6)
			self.handleStealing(script, gameObj, player, cake, guard)
		
		
		script.release()
	
	def handleStealing(self, script, gameObj, player, item_obtained, npc_watching):
		obj_id = gameObj.getID()
		obj_name = gameObj.getGameObjectDef().getName().lower()
		item_name = item_obtained.getDef().getName().lower()
		req_level = self.REQUIRED_LEVEL_BY_OBJ[obj_id]
		
		script.displayMessage("You attempt to steal some " + item_name + "from the " + obj_name)
		script.sleep(1000)
		
		if self.canThieve(script, player, req_level):
			script.displayMessage("You steal " + item_name + " from the " + obj_name)
			script.sleep(300)
			script.addItem(item_obtained.getID(), 1)
		else:
			script.displayMessage("You fail to steal from the " + obj_name)
			if npc_watching != None:
				script.setActiveNpc(npc_watching)
				script.sendNpcChat("Hey get your hands off there!")
				script.attackPlayer(npc_watching)
			else:
				if obj_id == self.CAKE_STALL: # BAKER ONLY
					baker = script.getNpc(self.BAKER)
					script.setActiveNpc(baker)
					script.sendNpcChat("Take your hands off my stall")
	
	def canThieve(self, script, player, req_level):
		level_diff = script.getCurrentLevel(player.SkillType.THIEVING) - req_level
		percent = script.rand(1, 100)
		
		if level_diff < 0:
			return False
		
		if level_diff > 40:
		 	level_diff = 60
		else:
		 	level_diff = 30 + level_diff
		
		return percent <= level_diff
         	
	def blockNpcCommand(self, npc, player):
		return player.canAccessMembers() and npc.getDef().getCommand() == "pickpocket"
	
	def blockObjectAction(self, gameObj, command, player):
		return player.canAccessMembers() and command == "steal from"
