from org.darkquest.gs.plugins.listeners.action import InvUseOnNpcListener
from org.darkquest.gs.plugins.listeners.executive import InvUseOnNpcExecutiveListener
from org.darkquest.gs.plugins import PlugInterface

'''
@author: GORF
A cow
'''

class Cow(PlugInterface, InvUseOnNpcListener, InvUseOnNpcExecutiveListener):
	
	def onInvUseOnNpc(self, player, npc, item):
		player.setActiveNpc(npc)
		player.occupy()
		
		if item.getID() == 21: # Milk bucket
			player.removeItem(21, 1)
			player.showBubble(21)
			player.sendSound("filljug")
			player.displayMessage("You fill up the bucket with milk")
			player.addItem(22, 1)
			
		player.release()
		
	def blockInvUseOnNpc(self, player, npc, item):
		return npc.getID() == 6 and item.getID() == 21
