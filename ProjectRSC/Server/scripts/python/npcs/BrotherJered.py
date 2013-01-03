from com.prsc.gs.plugins.listeners.action import TalkToNpcListener
from com.prsc.gs.plugins.listeners.executive import TalkToNpcExecutiveListener
from com.prsc.gs.plugins import PlugInterface

'''
@author: GORF
Brother Jered in Upstairs Edgeville Monastary
'''

class BrotherJered(PlugInterface, TalkToNpcListener, TalkToNpcExecutiveListener):
	
	# NPCs USED
	BROTHER_JERED = 176
	
	# ITEMS USED
	UNBLESSED_AMULET_OF_SARADOMIN = 45
	BLESSED_AMULET_OF_SARADOMIN = 385
	
	def onTalkToNpc(self, player, npc):
		script = player.getScriptHelper()
		
		script.setActiveNpc(npc)
		script.occupy()
		
		# START YOUR CODE HERE
		script.sendNpcChat("Hello friend, would you like me to bless your amulet of saradomin?")		
		option = script.pickOption(["Yes please", "No thanks"])
		if option == 0:
			if script.hasItem(self.UNBLESSED_AMULET_OF_SARADOMIN, 1):
				script.displayMessage("He quickly takes your amulet")
				script.removeItem(self.UNBLESSED_AMULET_OF_SARADOMIN, 1)
				script.sleep(2500)
				script.displayMessage("He hands you back a blessed amulet of saradomin")
				script.addItem(self.BLESSED_AMULET_OF_SARADOMIN, 1)
			else:
				script.sendNpcChat("Oh dear looks like you don't have any amulet to bless")
		#END YOUR CODE HERE
		script.release()
	
	def blockTalkToNpc(self, player, npc):
		return npc.getID() == self.BROTHER_JERED # THE NPC WE ARE GOING TO HANDLE
