from com.prsc.gs.plugins.listeners.action import InvUseOnObjectListener
from com.prsc.gs.plugins.listeners.executive import InvUseOnObjectExecutiveListener
from com.prsc.gs.plugins import PlugInterface

'''
@author: GORF
Called when a user is using anything from the inventory on an object
'''

class Refill(PlugInterface, InvUseOnObjectListener, InvUseOnObjectExecutiveListener):
	
	# VALID BLOCKING OBJECTS
	VALID_OBJECTS = [2, 466, 814, 48, 26, 86, 1130]
	# REFILLABLE ITEMS
	REFILLABLE = {21:50, 140:141, 341:342, 465:464}
	
	def onInvUseOnObject(self, obj, item, player):
		self.handleRefill(player, item)
	
	def handleRefill(self, player, item):
		script = player.getScriptHelper()
		id = item.getID()
		
		if script.hasItem(id):
			script.occupy()
			script.showBubble(id)
			script.sendSound("filljug")
			script.removeItem(id, 1)
			script.sleep(300)
			script.addItem(self.REFILLABLE[id], 1)
			script.displayMessage("You fill the " + item.getDef().getName().lower() + " with water")
			script.release()
	
	def blockInvUseOnObject(self, obj, item, player):
		for object_id in self.VALID_OBJECTS:
			if object_id == obj.getID():
				if self.REFILLABLE.has_key(item.getID()):
					return True
		return False
			
		
