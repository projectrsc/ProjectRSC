from com.prsc.gs.plugins.listeners.action import ObjectActionListener
from com.prsc.gs.plugins.listeners.executive import ObjectActionExecutiveListener
from com.prsc.gs.plugins import PlugInterface

'''
@author: GORF
Called when a user is hitting a dummy in varrock
'''

class Dummy(PlugInterface, ObjectActionListener, ObjectActionExecutiveListener):
	
	DUMMY = 49
	MAX_LEVEL = 7
	
	def onObjectAction(self, gameObject, command, player):
		script = player.getScriptHelper()
		script.occupy()
		script.displayMessage("You swing at the dummy")	
		script.sleep(3500)
		
		if script.getCurrentLevel(player.SkillType.ATTACK) > self.MAX_LEVEL:
			script.displayMessage("There is only so much you can learn from hitting a dummy")
			script.release()
			return
		
		script.displayMessage("You hit the dummy")
		script.advanceStat(player.SkillType.ATTACK, 5, True)
		script.release()
	
	def blockObjectAction(self, gameObject, command, player):
		return gameObject.getID() == self.DUMMY and command == "hit"
