from org.darkquest.gs.plugins.listeners.action import TalkToNpcListener
from org.darkquest.gs.plugins.listeners.executive import TalkToNpcExecutiveListener
from org.darkquest.gs.plugins import PlugInterface

'''
@author: GORF
Tramp wandering varrock
'''

class Tramp(PlugInterface, TalkToNpcListener, TalkToNpcExecutiveListener):
	
	TRAMP = 28 # MY NPC's ID
	
	def onTalkToNpc(self, player, npc):
		script = player.getScriptHelper()
		
		script.setActiveNpc(npc)
		script.occupy()
		
		# START YOUR CODE HERE
		script.sendNpcChat("Spare some change guv?")
		option = script.pickOption(["Sorry I haven't got any", "Go get a job", "Ok here you go", "Is there anything down this alleyway?"])
		if option == 0:
			script.sendNpcChat("Thanks anyways")
		elif option == 1:
			script.sendNpcChat("You startin?")
		elif option == 2:
			if script.hasItem(10):
				script.removeItem(10, 1)
				script.sendNpcChat("Thankyou, that's great")
				subOption = script.pickOption(["No problem", "So don't I get some sort of quest hint or something now"])
				if subOption == 1:
					script.sendNpcChat("No that's not why I'm asking for money", "I just need to eat")
		elif option == 3:
			script.sendNpcChat("Yes, there is actually", "A notorious gang of thieves and hoodlums", "Called the blackarm gang")
			subOption = script.pickOption(["Thanks for the warning", "Do you think they would let me join?"])
			if subOption == 0:
				script.sendNpcChat("Don't worry about it")
			elif subOption == 1:
				script.sendNpcChat("You never know", "You'll find a lady down there called Katrine", "Speak to her", "But don't upset her, she's pretty dangerous")
		#END YOUR CODE HERE
		script.release()
	
	def blockTalkToNpc(self, player, npc):
		return npc.getID() == self.TRAMP # THE NPC WE ARE GOING TO HANDLE
