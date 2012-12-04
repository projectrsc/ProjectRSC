from com.prsc.gs.plugins.listeners.action import ObjectActionListener
from com.prsc.gs.plugins.listeners.executive import ObjectActionExecutiveListener
from com.prsc.gs.plugins import PlugInterface

'''
@author: GORF
Called when a user is approaching a tree near Draynor Mansion
'''

class DeadTree(PlugInterface, ObjectActionListener, ObjectActionExecutiveListener):
	
	DEAD_TREE = 88
	
	def onObjectAction(self, gameObject, command, player):
		script = player.getScriptHelper()
		script.occupy()
		script.displayMessage("The tree seems to lash out at you!")	
		script.sleep(1000)
		damage = script.getRandom(1, 10)
		script.damagePlayer(damage)
		script.displayMessage("You are badly scratched by the tree")
		
		script.release()
	
	def blockObjectAction(self, gameObject, command, player):
		return command == "approach" or command == "search" and gameObject.getID() == 88
