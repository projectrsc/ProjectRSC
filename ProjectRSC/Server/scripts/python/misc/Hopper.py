from org.darkquest.gs.plugins.listeners.action import ObjectActionListener, InvUseOnObjectListener
from org.darkquest.gs.plugins.listeners.executive import ObjectActionExecutiveListener, InvUseOnObjectExecutiveListener
from org.darkquest.gs.plugins import PlugInterface

'''
@author: GORF
Called when using the hopper
'''

class Hopper(PlugInterface, ObjectActionListener, ObjectActionExecutiveListener, InvUseOnObjectListener, InvUseOnObjectExecutiveListener):
	
	HOPPER = 52
	FLOUR = 23
	GRAIN = 29
	
	def onInvUseOnObject(self, gameObject, item, player):
		script = player.getScriptHelper()
		
		if gameObject.containsItem() == self.GRAIN:
			script.displayMessage("There is already grain in the hopper")
			return
		
		script.occupy()
		script.removeItem(self.GRAIN, 1)
		script.showBubble(self.GRAIN)
		script.displayMessage("You put the grain in the hopper")
		gameObject.containsItem(self.GRAIN)
		script.release();
	
	def onObjectAction(self, gameObject, command, player):
		script = player.getScriptHelper()
		
		if gameObject.containsItem() != self.GRAIN:
			script.displayMessage("The hopper is empty...")
			return
		
		script.occupy()
		script.displayMessage("You operate the hopper..")
		script.sleep(1000)
		script.displayMessage("The grain slides down the chute")
		
		if gameObject.getX() == 179 and gameObject.getY() == 2371:
			script.spawnItem(179, 481, self.FLOUR, 1)
		else:
			script.spawnItem(166, 599, self.FLOUR, 1)
			
		gameObject.containsItem(-1)
		script.release()
	
	def blockInvUseOnObject(self, gameObj, item, player):
		return gameObj.getID() == self.HOPPER and item.getID() == self.GRAIN
	
	def blockObjectAction(self, gameObject, command, player):
		return command == "operate" and gameObject.getID() == self.HOPPER
