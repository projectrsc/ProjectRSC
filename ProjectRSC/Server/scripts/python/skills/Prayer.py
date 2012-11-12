from org.darkquest.gs.plugins.listeners.action import ObjectActionListener
from org.darkquest.gs.plugins.listeners.executive import ObjectActionExecutiveListener
from org.darkquest.gs.plugins import PlugInterface

'''
@author: GORF
Prayer skill
'''
class Prayer(PlugInterface, ObjectActionListener, ObjectActionExecutiveListener):
	
	def onObjectAction(self, gameObject, command, player):
		self.setParticipant(player)
		self.displayMessage("You recharge at the altar.")
		self.sendSound("recharge")
		maxPray = self.getMaxLevel(self.SkillType.PRAYER)
		cLevel = self.getCurrentLevel(self.SkillType.PRAYER)
		
		if gameObject.getID() == 200:
			maxPray = self.getMaxLevel(self.SkillType.PRAYER) + 2
		if cLevel < maxPray:
			self.restoreStat(self.SkillType.PRAYER, maxPray)
         	
	def blockObjectAction(self, gameObject, command, player):
		return command == "recharge at"
