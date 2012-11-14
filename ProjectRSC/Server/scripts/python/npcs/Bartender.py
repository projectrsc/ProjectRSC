from org.darkquest.gs.plugins.listeners.action import TalkToNpcListener
from org.darkquest.gs.plugins.listeners.executive import TalkToNpcExecutiveListener
from org.darkquest.gs.plugins import PlugInterface

'''
@author: Mister Hat
The Bartender NPC in Varrock's Blue Moon Inn
'''

class Bartender(PlugInterface, TalkToNpcListener, TalkToNpcExecutiveListener):
	
	def onTalkToNpc(self, player, npc):
		script = player.getScriptHelper()
		
		script.setActiveNpc(npc)
		script.occupy()
		script.sendNpcChat("What can I do yer for?")
		
		option = script.pickOption(["A glass of your finest ale please", "Can you recommend anywhere an adventurer might make his fortune?", "Do you know where I can get some good equipment?"])
		if option == 0:
			script.sendNpcChat("No problemo", "That'll be 2 coins")
			if script.hasItem(10, 2):
				script.removeItem(10, 2)
				script.displayMessage("You buy a pint of beer")
				script.addItem(193, 1)
			else:
				script.sendPlayerChat("oh dear i don't seem to have enough coins")
		elif option == 1:
			script.sendNpcChat("Ooh I don't know if I should be giving away information", "Makes the computer game too easy")
			option = script.pickOption(["Oh ah well", "Computer game? What are you talking about?", "Just a small clue?"], False)
			
			if option == 0:
				script.sendPlayerChat("Oh ah well")
			if option == 1:
				script.sendPlayerChat("Computer game?", "What are you talking about?")
				script.sendNpcChat("This world around us..", "is all a computer game..", "called RuneScape")
				script.sendPlayerChat("Nope, still don't understand what you are talking about", "What's a computer?")
				script.sendNpcChat("It's a sort of magic box thing.", "which can do all sorts of different things")
				script.sendPlayerChat("I give up", "You're obviously completely mad!")
			elif option == 2:
				script.sendPlayerChat("Just a small clue?")
				script.sendNpcChat("Go and talk to the bartender in the Jolly Boar Inn", "He doesn't seem to mind giving away clues")
		elif option == 2:
			script.sendNpcChat("Well, there's the sword shop across the road.", "or there's also all sorts of shops up around the market")
				
		script.release()
	
	def blockTalkToNpc(self, player, npc):
		return npc.getID() == 12
