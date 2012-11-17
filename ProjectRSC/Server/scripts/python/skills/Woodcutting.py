from org.darkquest.gs.plugins.listeners.action import ObjectActionListener
from org.darkquest.gs.plugins.listeners.executive import ObjectActionExecutiveListener
from org.darkquest.gs.plugins import PlugInterface
from org.darkquest.gs.external import EntityHandler, GameObjectLoc, ObjectWoodcuttingDef, ItemDef
from org.darkquest.gs.model import Entity, Point
from org.darkquest.config import Formulae

'''
@author: GORF
Woodcutting skill
'''
class Woodcutting(PlugInterface, ObjectActionListener, ObjectActionExecutiveListener):
	
	VALID_AXE_IDS = [405, 204, 203, 428, 88, 12, 87]
	
	def onObjectAction(self, gameObject, command, player):
		script = player.getScriptHelper()
		woodcutting_def = EntityHandler.getObjectWoodcuttingDef(gameObject.getID())
		
		if woodcutting_def == None or player.isBusy() or not player.withinRange(gameObject, 2):
			return
		
		if script.getCurrentLevel(player.SkillType.WOODCUT) < woodcutting_def.getReqLevel():
			script.displayMessage("You need a woodcutting level of " + woodcutting_def.getReqLevel() + " to axe this tree")
			return
		
		axe_id = -1
		for axe in self.VALID_AXE_IDS:
			if script.countItem(axe) > 0:
				axe_id = axe
				break
		
		if axe_id < 0:
			script.displayMessage("You need an axe to chop this tree down")
			return
		if player.getFatigue() >= 7500:
			script.displayMessage("You are too tired to cut this tree")
			return
		
		axe_name = EntityHandler.getItemDef(axe_id).getName()
		script.occupy()
		script.showBubble(axe_id)
		script.displayMessage("You swing your " + axe_name.lower() + " at the tree...")
		self.woodcutEvent(gameObject, script, woodcutting_def, axe_id) # WE DO NOT NEED TO CALL A SHORT EVENT SINCE WE ARE ALREADY ON OUR OWN THREAD
		script.release()
	
	def woodcutEvent(self, game_object, script, woodcut_def, axe_id):
		player = script.getPlayer()
		script.sleep(1500)
		print("chop?")
		if Formulae.getLog(woodcut_def, script.getCurrentLevel(player.SkillType.WOODCUT), axe_id):
			script.addItem(woodcut_def.getLogId(), 1)
			script.displayMessage("You get some wood")
			script.advanceStat(player.SkillType.WOODCUT, woodcut_def.getExp(), True)
			if script.getRandom(1, 100) <= woodcut_def.getFell():
				script.spawnObject(game_object.getLocation(), 4, game_object.getDirection(), game_object.getType(), True, game_object.getLoc(), woodcut_def.getRespawnTime() * 1000)
		else:
			script.displayMessage("You slip and fail to hit the tree")
		script.release()
         	
	def blockObjectAction(self, gameObject, command, player):
		return command == "chop"
