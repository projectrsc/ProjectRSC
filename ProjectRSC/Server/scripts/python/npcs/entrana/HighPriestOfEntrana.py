from com.prsc.gs.plugins.listeners.action import TalkToNpcListener
from com.prsc.gs.plugins.listeners.executive import TalkToNpcExecutiveListener
from com.prsc.gs.plugins import PlugInterface

'''
@author: GORF
@location: Entrana
@description: High priest in Entrana
'''

class HighPriestOfEntrana(PlugInterface, TalkToNpcListener, TalkToNpcExecutiveListener):
	
	# NPCs USED
	HIGH_PRIEST_OF_ENTRANA = 395
	
	def onTalkToNpc(self, player, npc):
		script = player.getScriptHelper()
		
		script.setActiveNpc(npc)
		script.occupy()
		
		# START YOUR CODE HERE
		script.sendNpcChat("Many greetings welcome to our fair island", "enjoy your stay here", 
						"May it be spiritually uplifting")		
		#END YOUR CODE HERE
		script.release()
	
	def blockTalkToNpc(self, player, npc):
		return npc.getID() == self.HIGH_PRIEST_OF_ENTRANA # THE NPC WE ARE GOING TO HANDLE
