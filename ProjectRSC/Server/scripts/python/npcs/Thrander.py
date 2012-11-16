from org.darkquest.gs.plugins.listeners.action import TalkToNpcListener, InvUseOnNpcListener
from org.darkquest.gs.plugins.listeners.executive import TalkToNpcExecutiveListener, InvUseOnNpcExecutiveListener
from org.darkquest.gs.plugins import PlugInterface

'''
@author: GORF
Armor change guy
'''

class Thrander(PlugInterface, TalkToNpcListener, TalkToNpcExecutiveListener, InvUseOnNpcListener, InvUseOnNpcExecutiveListener):
	
	item_directory = {308:117, 312:8, 309:118, 313:196, 310:119, 311:120, 407:401, 117:308, 8:312, 118:309, 196:313, 119:310,
					120:311, 401:407, 214:206, 215:9, 225:121, 434:248, 226:122, 227:123, 406:402, 206:214, 9:215, 121:225,
					248:434, 122:226, 123:227, 402:406}
	
	THRANDER = 160
	
	def onTalkToNpc(self, player, npc):
		script = player.getScriptHelper()
		script.setActiveNpc(npc)
		script.occupy()
		script.sendNpcChat("Hello i'm thrander the smith", "I'm an expert in armour modification", "Give me your armour designed for men", "and I can convert it into something more comfortable for a woman", "and vice versa")
		script.release()
	
	def onInvUseOnNpc(self, player, npc, item):
		script = player.getScriptHelper()
		script.setActiveNpc(npc)
		newID = 0
		itemID = item.getID()
		script.occupy()
		
		newItem = self.item_directory[itemID]
		script.displayMessage("You give Thrander a " + item.getDef().getName())
		script.sleep(500)
		script.displayMessage("Thrander hammers it for a bit")
		script.removeItem(itemID, 1)
		script.sleep(1000)
		changedItem = script.getItem(newItem)
		script.displayMessage("Thrander gives you a " + changedItem.getDef().getName())
		script.addItem(newItem, 1)
			
		script.release()
	
	def blockTalkToNpc(self, player, npc):
		return npc.getID() == self.THRANDER
	
	def blockInvUseOnNpc(self, player, npc, item):
		return npc.getID() == self.THRANDER and self.item_directory.has_key(item.getID())
