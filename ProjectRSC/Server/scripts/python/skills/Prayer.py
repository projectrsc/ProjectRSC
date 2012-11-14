from org.darkquest.gs.plugins.listeners.action import ObjectActionListener
from org.darkquest.gs.plugins.listeners.executive import ObjectActionExecutiveListener
from org.darkquest.gs.plugins import PlugInterface

'''
@author: GORF
Prayer skill
'''
class Prayer(PlugInterface, ObjectActionListener, ObjectActionExecutiveListener):
	
	def onObjectAction(self, gameObject, command, player):
		player.displayMessage("You recharge at the altar.")
		player.sendSound("recharge")
		maxPray = player.getMaxLevel(player.SkillType.PRAYER)
		cLevel = player.getCurrentLevel(player.SkillType.PRAYER)
		
		if gameObject.getID() == 200:
			maxPray = player.getMaxLevel(player.SkillType.PRAYER) + 2
		if cLevel < maxPray:
			player.restoreStat(player.SkillType.PRAYER, maxPray)
         	
	def blockObjectAction(self, gameObject, command, player):
		return command == "recharge at"
