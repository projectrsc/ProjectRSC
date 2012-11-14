from org.darkquest.gs.plugins.listeners.action import TalkToNpcListener
from org.darkquest.gs.plugins.listeners.executive import TalkToNpcExecutiveListener
from org.darkquest.gs.plugins import PlugInterface

'''
@author: Mister Hat
Men and farmer dialogue
'''
class Man(PlugInterface, TalkToNpcListener, TalkToNpcExecutiveListener):
	
	def onTalkToNpc(self, player, npc):
		if npc.getID() == 11 or npc.getID() == 63:
			player.setActiveNpc(npc)
			player.occupy()
		
			selected = player.rand(0, 13)
		
			player.sendPlayerChat("Hello", "How's it going?")
		
			if selected == 0:
				player.sendNpcChat("Get out of my way", "I'm in a hurry")
			elif selected == 1:
				player.displayMessage("The man ignores you")
			elif selected == 2:
				player.sendNpcChat("Not too bad")
			elif selected == 3:
				player.sendNpcChat("Very well, thank you")
			elif selected == 4:
				player.sendNpcChat("Have this flier")
				player.addItem(201, 1)
			elif selected == 5:
				player.sendNpcChat("I'm a little worried", "I've heard there's lots of people going about,", "killing citizens at random")
			elif selected == 6:
				player.sendNpcChat("I'm fine", "How are you?")
				player.sendPlayerChat("Very well, thank you")
			elif selected == 7:
				player.sendNpcChat("Hello")
			elif selected == 8:
				player.sendNpcChat("Who are you?")
				player.sendPlayerChat("I am a bold adventurer")
				player.sendNpcChat("A very noble profession")
			elif selected == 9:
				player.sendNpcChat("Not too bad", "I'm a little worried about the increase in Goblins these days")
				player.sendPlayerChat("Don't worry. I'll kill them")
			elif selected == 10:
				player.sendNpcChat("Hello", "Nice weather we've been having")
			elif selected == 11:
				player.sendNpcChat("No, I don't want to buy anything")
			elif selected == 12:
				player.sendNpcChat("Do I know you?")
				player.sendPlayerChat("No, I was just wondering if you had anything interesting to say")
			elif selected == 13:
				player.sendNpcChat("How can I help you?")
				option = player.pickOption(["Do you wish to trade?", "I'm in search of a quest", "I'm in search of enemies to kill"])
				if option == 0:
					player.sendNpcChat("No, I have nothing I wish to get rid of", "If you want some trading,", "there are plenty of shops and market stalls around though")
				elif option == 1:
					player.sendNpcChat("I'm sorry I can't help you there")
				elif option == 2:
					player.sendNpcChat("I've heard there are many fearsome creatures under the ground")
		
		player.release()
	
	def blockTalkToNpc(self, player, npc):
		return npc.getID() == 11 or npc.getID() == 63
