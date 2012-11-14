from org.darkquest.gs.plugins.listeners.action import InvUseOnNpcListener
from org.darkquest.gs.plugins.listeners.executive import InvUseOnNpcExecutiveListener
from org.darkquest.gs.plugins import PlugInterface

'''
@author: GORF
A cow
'''

class Cow(PlugInterface, InvUseOnNpcListener, InvUseOnNpcExecutiveListener):
	
	def onInvUseOnNpc(self, player, npc, item):
		script = player.getScriptHelper()
		
		script.setActiveNpc(npc)
		script.occupy()
		
		if item.getID() == 21: # Milk bucket
			script.removeItem(21, 1)
			script.showBubble(21)
			script.sendSound("filljug")
			script.displayMessage("You fill up the bucket with milk")
			script.addItem(22, 1)
			
		script.release()
		
	def blockInvUseOnNpc(self, player, npc, item):
		return npc.getID() == 6 and item.getID() == 21
