from org.darkquest.gs.plugins.listeners.action import TalkToNpcListener
from org.darkquest.gs.plugins.listeners.executive import TalkToNpcExecutiveListener
from org.darkquest.gs.plugins import PlugInterface

'''
@author: GORF
Handles boat npcs
'''

class BoatCaptains(PlugInterface, TalkToNpcListener, TalkToNpcExecutiveListener):
	
	# NPCs USED
	CAPTAINS = [166, 170, 171, 163, 317, 316, 280, 764]
	# ILLEGAL ITEMS
	KARAMJA_RUM = 318
	
	def onTalkToNpc(self, player, npc):
		script = player.getScriptHelper()
		
		script.setActiveNpc(npc)
		script.occupy()
		
		# START YOUR CODE HERE
		if npc.getID() == 163: # customs officer
			option = script.pickOption(["Can I board this ship?", "Does Karamja have any unusual customs then?"])
			if option == 0:
				script.sendNpcChat("You need to be searched before you can board")
				self.incrementalOptions(script, 3)
			elif option == 1:
				script.sendNpcChat("I'm not that sort of customs officer")
		else: # port sarim
			script.sendNpcChat("Do you want to go on a trip to Karamja?")
			option = script.pickOption(["I'd rather go to Crandor Isle", "Yes please", "No thankyou"])
			if option == 0:
				script.sendNpcChat("No I need to stay alive", "I have a wife and family to support")
			elif option == 1:
				if script.hasItem(10, 30):
					script.displayMessage("You pay 30 gold", "You board the ship")
					script.sleep(600)
					script.movePlayer(324, 713, False)
					script.sleep(600)
					script.displayMessage("The ship arrives at Karajma")
				else:
					script.sendPlayerChat("Oh dear I don't seem to have enough money")
					script.release()
		
		#END YOUR CODE HERE
		script.release()
		
	def incrementalOptions(self, script, amount):
		if amount == 3:
			sub_option = script.pickOption(["Why?", "Search away I have nothing to hide", "You're not putting your hands on my things"])
			if sub_option == 0:
				script.sendNpcChat("Because Asgarnia has banned the import of intoxicating opiates")
				self.incrementalOptions(script, 2)
			elif sub_option == 1:
				script.sendNpcChat("Well you've got some odd stuff, but it's all legal", "Now you need to pay a boarding charge of 30 gold")
				go_option = script.pickOption(["Ok", "Oh, I'll not bother then"])
				if go_option == 0:
					if script.hasItem(10, 30):
						script.displayMessage("You pay 30 gold", "You board the ship")
						script.sleep(600)
						script.movePlayer(268, 650, False)
						script.sleep(600)
						script.displayMessage("The ship arrives at Port Sarim")
					else:
						script.sendPlayerChat("Oh dear I don't seem to have enough money")
						script.release()
				elif go_option == 1:
					script.release()
			elif sub_option == 2:
				script.sendNpcChat("You're not getting on this ship then")
				script.release()
		elif amount == 2:
			sub_option = script.pickOption(["Search away I have nothing to hide", "You're not putting your hands on my things"])
			if sub_option == 0:
				if script.hasItem(self.KARAMJA_RUM, 1):
					script.displayMessage("The custom officer searches you...")
					script.sleep(600)
					script.sendNpcChat("What is this we found here? I'm going to have to confiscate that")
					script.removeItem(self.KARAMJA_RUM, 1)
				else:
					script.sendNpcChat("Well you've got some odd stuff, but it's all legal", "Now you need to pay a boarding charge of 30 gold")
					go_option = script.pickOption(["Ok", "Oh, I'll not bother then"])
					if go_option == 0:
						if script.hasItem(10, 30):
							script.displayMessage("You pay 30 gold", "You board the ship")
							script.sleep(800)
							script.movePlayer(268, 650, False)
							script.displayMessage("The ship arrives at Port Sarim")
						else:
							script.sendPlayerChat("Oh dear I don't seem to have enough money")
							script.release()
					elif go_option == 1:
						script.release()
			elif sub_option == 1:
				script.sendNpcChat("You're not getting on this ship then")
				script.release()
	
	def blockTalkToNpc(self, player, npc):
		for captain in self.CAPTAINS:
			if npc.getID() == captain:
				return True  # THE NPC WE ARE GOING TO HANDLE
		return False
