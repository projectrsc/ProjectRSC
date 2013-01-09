from com.prsc.gs.plugins.listeners.action import TalkToNpcListener
from com.prsc.gs.plugins.listeners.executive import TalkToNpcExecutiveListener
from com.prsc.gs.plugins import PlugInterface

'''
@author: GORF
This is a generic template for the npc named Hans 
'''

class Hans(PlugInterface, TalkToNpcListener, TalkToNpcExecutiveListener):
	
	# NPCs USED
	HANS = 5
	
	def onTalkToNpc(self, player, npc):
		script = player.getScriptHelper()
		
		script.setActiveNpc(npc)
		script.occupy()
		
		# START YOUR CODE HERE
		option = script.pickOption(["I'm looking for whoever is in charge of this place", 
								"I have come to kill everyone in this castle", "I don't know. I am lost. Where am I?"])
		if option == 0:
			script.sendNpcChat("Sorry I don't know where he is right now")
		elif option == 1:
			script.sendNpcChat("HELP HELP!")
		elif option == 2:
			script.sendNpcChat("You are in Lumbridge Castle")
		
		#END YOUR CODE HERE
		script.release()
	
	def blockTalkToNpc(self, player, npc):
		return npc.getID() == self.HANS # THE NPC WE ARE GOING TO HANDLE
