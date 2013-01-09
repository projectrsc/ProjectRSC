from com.prsc.gs.plugins.listeners.action import TalkToNpcListener
from com.prsc.gs.plugins.listeners.executive import TalkToNpcExecutiveListener
from com.prsc.gs.plugins import PlugInterface

'''
@author: GORF
This is a generic template for the Duke of Lumbridge
'''

class DukeOfLumbridge(PlugInterface, TalkToNpcListener, TalkToNpcExecutiveListener):
	
	# NPCs USED
	DUKE_OF_LUMBRIDGE = 198
	
	def onTalkToNpc(self, player, npc):
		script = player.getScriptHelper()
		
		script.setActiveNpc(npc)
		script.occupy()
		
		# START YOUR CODE HERE
		script.sendNpcChat("Greetings welcome to my castle")
		option = script.pickOption(["Have you any quests for me?", "Where can I find money?"])
		if option == 0:
			script.sendNpcChat("All is well for me")
		elif option == 1:
			script.sendNpcChat("I've heard the blacksmiths are prosperous amoung us", "Maybe you can try your hand at that")
		
		#END YOUR CODE HERE
		script.release()
	
	def blockTalkToNpc(self, player, npc):
		return npc.getID() == self.DUKE_OF_LUMBRIDGE # THE NPC WE ARE GOING TO HANDLE
