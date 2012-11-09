from org.darkquest.gs.plugins.listeners.action import TalkToNpcListener
from org.darkquest.gs.plugins.listeners.executive import TalkToNpcExecutiveListener
from org.darkquest.gs.plugins import NpcInterface

'''
@author: Mister Hat
Men and farmer dialogue
'''
class Man(NpcInterface, TalkToNpcListener, TalkToNpcExecutiveListener):
	
	def onTalkToNpc(self, player, npc):
		if npc.getID() == 11 or npc.getID() == 63:
			self.setParticipants(player, npc)
			self.occupy()
		
			selected = self.rand(0, 13)
		
			self.sendPlayerChat("Hello", "How's it going?")
		
			if selected == 0:
				self.sendNpcChat("Get out of my way", "I'm in a hurry")
			elif selected == 1:
				self.displayMessage("The man ignores you")
			elif selected == 2:
				self.sendNpcChat("Not too bad")
			elif selected == 3:
				self.sendNpcChat("Very well, thank you")
			elif selected == 4:
				self.sendNpcChat("Have this flier")
				self.addItem(201, 1)
			elif selected == 5:
				self.sendNpcChat("I'm a little worried", "I've heard there's lots of people going about,", "killing citizens at random")
			elif selected == 6:
				self.sendNpcChat("I'm fine", "How are you?")
				self.sendPlayerChat("Very well, thank you")
			elif selected == 7:
				self.sendNpcChat("Hello")
			elif selected == 8:
				self.sendNpcChat("Who are you?")
				self.sendPlayerChat("I am a bold adventurer")
				self.sendNpcChat("A very noble profession")
			elif selected == 9:
				self.sendNpcChat("Not too bad", "I'm a little worried about the increase in Goblins these days")
				self.sendPlayerChat("Don't worry. I'll kill them")
			elif selected == 10:
				self.sendNpcChat("Hello", "Nice weather we've been having")
			elif selected == 11:
				self.sendNpcChat("No, I don't want to buy anything")
			elif selected == 12:
				self.sendNpcChat("Do I know you?")
				self.sendPlayerChat("No, I was just wondering if you had anything interesting to say")
			elif selected == 13:
				self.sendNpcChat("How can I help you?")
				option = self.pickOption(["Do you wish to trade?", "I'm in search of a quest", "I'm in search of enemies to kill"])
				if option == 0:
					self.wishToTrade()
				elif option == 1:
					self.sendNpcChat("I'm sorry I can't help you there")
				elif option == 2:
					self.sendNpcChat("I've heard there are many fearsome creatures under the ground")
		
		self.release()
	
	def wishToTrade(self):
		self.sendNpcChat("No, I have nothing I wish to get rid of", "If you want some trading,", "there are plenty of shops and market stalls around though")
	
	def blockTalkToNpc(self, player, npc):
		return npc.getID() == 11 or npc.getID() == 63
