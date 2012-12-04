from com.prsc.gs.plugins.listeners.action import TalkToNpcListener
from com.prsc.gs.plugins.listeners.executive import TalkToNpcExecutiveListener
from com.prsc.gs.plugins import PlugInterface

'''
@authour: Mister Hat
The Bartender NPC in Varrock's Jolly Boar Inn (near wilderness)
'''

class JollyBoarBartender(PlugInterface, TalkToNpcListener, TalkToNpcExecutiveListener):
	BARTENDER = 44

	def onTalkToNpc(self, player, npc):
		script = player.getScriptHelper()
		script.setActiveNpc(npc)
		
		script.occupy()
		
		script.sendNpcChat("Yes please?")
		
		option = script.pickOption(["I'll have a beer please", "Any hints where I can go adventuring?", "Heard any good gossip?"], False)
		if option == 0:
			script.sendPlayerChat("I'll have a pint of beer please")
			script.sendNpcChat("Ok, that'll be two coins")
			
			if script.hasItem(10, 2):
				script.displayMessage("You buy a pint of beer")
				script.removeItem(10, 2)
				script.addItem(193, 1)
			else:
				script.sendPlayerChat("Oh dear, I don't seem to have enough money")
		elif option == 1:
			script.sendPlayerChat("Any hints where I can go adventuring?")
			script.sendNpcChat(
				"It's funny you should say that",
				"An adventurer passed through here, the other day,",
				"claiming to have found a dungeon full of treasure,",
				"guarded by vicious skeletal warriors",
				"He said he found the entrance in a ruined town",
				"deep in the woods to the west of here, behind the palace",
				"Now how much faith you put in that story is up to you,",
				"but it probably wouldn't do any harm to have a look"
			)
			
			script.sendPlayerChat("Thanks", "I may try that at some point")
		elif option == 2:
			script.sendPlayerChat("Heard any good gossip?")
			script.sendNpcChat(
				"I'm that well up on the gossip out here",
				"I've heard that the bartender in the Blue Moon Inn has gone a little crazy",
				"He keeps claiming he is part of something called a computer game",
				"What that means, I don't know",
				"That's probably old news by now though"
			)
		
		script.release()

	def blockTalkToNpc(self, player, npc):
		return npc.getID() == self.BARTENDER
