from org.darkquest.gs.plugins.listeners.action import InvUseOnNpcListener
from org.darkquest.gs.plugins.listeners.executive import InvUseOnNpcExecutiveListener
from org.darkquest.gs.plugins import PlugInterface

'''
@author: GORF
A cow
'''

class Cow(PlugInterface, InvUseOnNpcListener, InvUseOnNpcExecutiveListener):
	
	def onInvUseOnNpc(self, player, npc, item):
		self.setParticipants(player, npc)
		self.occupy()
		
		if item.getID() == 21: # Milk bucket
			self.removeItem(21, 1)
			self.showBubble(21)
			self.sendSound("filljug")
			self.displayMessage("You fill up the bucket with milk")
			self.addItem(22, 1)
			
		self.release()
		
	def blockInvUseOnNpc(self, player, npc, item):
		return npc.getID() == 6
