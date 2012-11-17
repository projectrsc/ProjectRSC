from org.darkquest.gs.plugins.listeners.action import TalkToNpcListener
from org.darkquest.gs.plugins.listeners.executive import TalkToNpcExecutiveListener
from org.darkquest.gs.plugins import PlugInterface

'''
@author: GORF
This is a generic template for an NPC handler (always document what the handler is)
'''

class Template(PlugInterface, TalkToNpcListener, TalkToNpcExecutiveListener):
	
	# NPCs USED
	MY_NPC = -1
	
	def onTalkToNpc(self, player, npc):
		script = player.getScriptHelper()
		
		script.setActiveNpc(npc)
		script.occupy()
		
		# START YOUR CODE HERE
		script.sendNpcChat("This will send an outgoing chat message from an npc")
		script.sendPlayerChat("This will send an outgoing chat message from an npc")
		
		option = script.pickOption(["Option 1", "Option 2", "Option 3"])
		
		#END YOUR CODE HERE
		script.release()
	
	def blockTalkToNpc(self, player, npc):
		return npc.getID() == self.MY_NPC # THE NPC WE ARE GOING TO HANDLE
