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
	
	def onTalkToNpc(self, player, npc):
		player.setActiveNpc(npc)
		player.occupy()
		player.sendNpcChat("Hello i'm thrander the smith", "I'm an expert in armour modification", "Give me your armour designed for men", "and I can convert it into something more comfortable for a woman", "and vice versa")
		player.release()
	
	def onInvUseOnNpc(self, player, npc, item):
		player.setActiveNpc(npc)
		newID = 0
		itemID = item.getID()
		player.occupy()
		if self.item_directory.has_key(itemID):
			newItem = self.item_directory.get(itemID)
			player.displayMessage("You give Thrander a " + item.getDef().getName())
			player.sleep(500)
			player.displayMessage("Thrander hammers it for a bit")
			player.removeItem(itemID, 1)
			player.sleep(1000)
			changedItem = player.getItem(newItem)
			player.displayMessage("Thrander gives you a " + changedItem.getDef().getName())
			player.addItem(newItem, 1)
		player.release()
	
	def blockTalkToNpc(self, player, npc):
		return npc.getID() == 160
	
	def blockInvUseOnNpc(self, player, npc, item):
		return npc.getID() == 160 and self.item_directory.has_key(item.getID())
