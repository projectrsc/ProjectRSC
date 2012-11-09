from org.darkquest.gs.plugins.listeners.action import TalkToNpcListener
from org.darkquest.gs.plugins.listeners.executive import TalkToNpcExecutiveListener
from org.darkquest.gs.plugins import NpcInterface

'''
@author: Mister Hat
The Bartender NPC in Varrock's Blue Moon Inn
'''

class Bartender(NpcInterface, TalkToNpcListener, TalkToNpcExecutiveListener):
	
	def onTalkToNpc(self, player, npc):
		self.setParticipants(player, npc)
		self.occupy()
		
		self.sendNpcChat("What can I do yer for?")
		
		option = self.pickOption(["A glass of your finest ale please", "Can you recommend anywhere an adventurer might make his fortune?", "Do you know where I can get some good equipment?"])
		if option == 0:
			self.sendNpcChat("No problemo", "That'll be 2 coins")
			if self.hasItem(10, 2):
				self.removeItem(10, 2)
				self.displayMessage("You buy a pint of beer")
				self.addItem(193, 1)
			else:
				self.sendPlayerChat("oh dear i don't seem to have enough coins")
		elif option == 1:
			self.sendNpcChat("Ooh I don't know if I should be giving away information", "Makes the computer game too easy")
			option = self.pickOption(["Oh ah well", "Computer game? What are you talking about?", "Just a small clue?"])
			
			if option == 0:
				self.sendPlayerChat("Oh ah well")
			if option == 1:
				self.sendPlayerChat("Computer game?", "What are you talking about?")
				self.sendNpcChat("This world around us..", "is all a computer game..", "called RuneScape")
				self.sendPlayerChat("Nope, still don't understand what you are talking about", "What's a computer?")
				self.sendNpcChat("It's a sort of magic box thing.", "which can do all sorts of different things")
				self.sendPlayerChat("I give up", "You're obviously completely mad!")
			elif option == 2:
				self.sendPlayerChat("Just a small clue?")
				self.sendNpcChat("Go and talk to the bartender in the Jolly Boar Inn", "He doesn't seem to mind giving away clues")
		elif option == 2:
			self.sendNpcChat("Well, there's the sword shop across the road.", "or there's also all sorts of shops up around the market")
				
		self.release()
	
	def blockTalkToNpc(self, player, npc):
		return npc.getID() == 12
