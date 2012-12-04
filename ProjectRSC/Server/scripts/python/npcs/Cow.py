from com.prsc.gs.plugins.listeners.action import InvUseOnNpcListener
from com.prsc.gs.plugins.listeners.executive import InvUseOnNpcExecutiveListener
from com.prsc.gs.plugins import PlugInterface

'''
@author: GORF
A cow
'''

class Cow(PlugInterface, InvUseOnNpcListener, InvUseOnNpcExecutiveListener):
	
	COW = 6
	BUCKET = 21
	MILK = 22
	
	def onInvUseOnNpc(self, player, npc, item):
		script = player.getScriptHelper()
		
		script.setActiveNpc(npc)
		script.occupy()
		script.removeItem(self.BUCKET, 1)
		script.showBubble(self.BUCKET)
		script.sendSound("filljug")
		script.displayMessage("You fill up the bucket with milk")
		script.addItem(self.MILK, 1)
		script.release()
		
	def blockInvUseOnNpc(self, player, npc, item):
		return npc.getID() == self.COW and item.getID() == self.BUCKET
