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
	
	valid_axe_ids = [405, 204, 203, 428, 88, 12, 87]
	
	def onObjectAction(self, gameObject, command, player):
		script = player.getScriptHelper()
		woodcutting_def = EntityHandler.getObjectWoodcuttingDef(gameObject.getID())
		
		if woodcutting_def == None or player.isBusy() or not player.withinRange(gameObject, 2):
			return
		if script.getCurrentLevel(player.SkillType.WOODCUT) < woodcutting_def.getReqLevel():
			script.displayMessage("You need a woodcutting level of " + woodcutting_def.getReqLevel() + " to axe this tree")
			return
		
		axeId = -1
		for axe in self.valid_axe_ids:
			if script.countItem(axe) > 0:
				axeId = axe
				break
		
		if axeId < 0:
			script.displayMessage("You need an axe to chop this tree down")
			return
		if player.getFatigue() >= 7500:
			script.displayMessage("You are too tired to cut this tree")
			return
		
		axe_name = EntityHandler.getItemDef(axeId).getName()
		script.occupy()
		script.showBubble(axeId)
		script.displayMessage("You swing your " + axe_name.lower() + " at the tree...")
		self.woodcutEvent(player, gameObject, script, woodcutting_def, axeId) # WE DO NOT NEED TO CALL A SHORT EVENT SINCE WE ARE ALREADY ON OUR OWN THREAD
		script.release()
	
	def woodcutEvent(self, player, gameObject, script, woodcut_def, axeId):
		if Formulae.getLog(woodcut_def, script.getCurrentLevel(player.SkillType.WOODCUT), axeId):
			script.sleep(1500)
			script.addItem(woodcut_def.getLogId(), 1)
			script.displayMessage("You get some wood")
			script.advanceStat(player.SkillType.WOODCUT, woodcut_def.getExp(), True)
			if script.getRandom(1, 100) <= woodcut_def.getFell():
				script.spawnObject(gameObject.getLocation(), 4, gameObject.getDirection(), gameObject.getType(), True, gameObject.getLoc(), woodcut_def.getRespawnTime() * 1000)
		else:
			script.displayMessage("You slip and fail to hit the tree")
		script.release()
         	
	def blockObjectAction(self, gameObject, command, player):
		return command == "chop"
