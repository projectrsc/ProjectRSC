from com.prsc.gs.plugins.listeners.action import TalkToNpcListener
from com.prsc.gs.plugins.listeners.executive import TalkToNpcExecutiveListener
from com.prsc.gs.plugins import PlugInterface

'''
@author: Mister Hat
Men and farmer dialogue
'''
class Man(PlugInterface, TalkToNpcListener, TalkToNpcExecutiveListener):
	
	MAN = 11
	FARMER = 63
	
	def onTalkToNpc(self, player, npc):
		if npc.getID() == self.MAN or npc.getID() == self.FARMER:
			script = player.getScriptHelper()
			
			script.setActiveNpc(npc)
			script.occupy()
		
			selected = script.rand(0, 13)
		
			script.sendPlayerChat("Hello", "How's it going?")
		
			if selected == 0:
				script.sendNpcChat("Get out of my way", "I'm in a hurry")
			elif selected == 1:
				script.displayMessage("The man ignores you")
			elif selected == 2:
				script.sendNpcChat("Not too bad")
			elif selected == 3:
				script.sendNpcChat("Very well, thank you")
			elif selected == 4:
				script.sendNpcChat("Have this flier")
				script.addItem(201, 1)
			elif selected == 5:
				script.sendNpcChat("I'm a little worried", "I've heard there's lots of people going about,", "killing citizens at random")
			elif selected == 6:
				script.sendNpcChat("I'm fine", "How are you?")
				script.sendPlayerChat("Very well, thank you")
			elif selected == 7:
				script.sendNpcChat("Hello")
			elif selected == 8:
				script.sendNpcChat("Who are you?")
				script.sendPlayerChat("I am a bold adventurer")
				script.sendNpcChat("A very noble profession")
			elif selected == 9:
				script.sendNpcChat("Not too bad", "I'm a little worried about the increase in Goblins these days")
				script.sendPlayerChat("Don't worry. I'll kill them")
			elif selected == 10:
				script.sendNpcChat("Hello", "Nice weather we've been having")
			elif selected == 11:
				script.sendNpcChat("No, I don't want to buy anything")
			elif selected == 12:
				script.sendNpcChat("Do I know you?")
				script.sendPlayerChat("No, I was just wondering if you had anything interesting to say")
			elif selected == 13:
				script.sendNpcChat("How can I help you?")
				option = script.pickOption(["Do you wish to trade?", "I'm in search of a quest", "I'm in search of enemies to kill"])
				if option == 0:
					script.sendNpcChat("No, I have nothing I wish to get rid of", "If you want some trading,", "there are plenty of shops and market stalls around though")
				elif option == 1:
					script.sendNpcChat("I'm sorry I can't help you there")
				elif option == 2:
					script.sendNpcChat("I've heard there are many fearsome creatures under the ground")
		
		script.release()
	
	def blockTalkToNpc(self, player, npc):
		return npc.getID() == self.MAN or npc.getID() == self.FARMER
