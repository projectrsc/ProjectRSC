from org.darkquest.gs.plugins.listeners.action import TalkToNpcListener
from org.darkquest.gs.plugins.listeners.executive import TalkToNpcExecutiveListener
from org.darkquest.gs.plugins import PlugInterface

'''
@author: Mister Hat
The Bartender NPC in Varrock's Blue Moon Inn
'''

class Bartender(PlugInterface, TalkToNpcListener, TalkToNpcExecutiveListener):
	
	def onTalkToNpc(self, player, npc):
		player.setActiveNpc(npc)
		player.occupy()
		
		player.sendNpcChat("What can I do yer for?")
		
		option = player.pickOption(["A glass of your finest ale please", "Can you recommend anywhere an adventurer might make his fortune?", "Do you know where I can get some good equipment?"])
		if option == 0:
			player.sendNpcChat("No problemo", "That'll be 2 coins")
			if player.hasItem(10, 2):
				player.removeItem(10, 2)
				player.displayMessage("You buy a pint of beer")
				player.addItem(193, 1)
			else:
				player.sendPlayerChat("oh dear i don't seem to have enough coins")
		elif option == 1:
			player.sendNpcChat("Ooh I don't know if I should be giving away information", "Makes the computer game too easy")
			option = player.pickOption(["Oh ah well", "Computer game? What are you talking about?", "Just a small clue?"], False)
			
			if option == 0:
				player.sendPlayerChat("Oh ah well")
			if option == 1:
				player.sendPlayerChat("Computer game?", "What are you talking about?")
				player.sendNpcChat("This world around us..", "is all a computer game..", "called RuneScape")
				player.sendPlayerChat("Nope, still don't understand what you are talking about", "What's a computer?")
				player.sendNpcChat("It's a sort of magic box thing.", "which can do all sorts of different things")
				player.sendPlayerChat("I give up", "You're obviously completely mad!")
			elif option == 2:
				player.sendPlayerChat("Just a small clue?")
				player.sendNpcChat("Go and talk to the bartender in the Jolly Boar Inn", "He doesn't seem to mind giving away clues")
		elif option == 2:
			player.sendNpcChat("Well, there's the sword shop across the road.", "or there's also all sorts of shops up around the market")
				
		player.release()
	
	def blockTalkToNpc(self, player, npc):
		return npc.getID() == 12
