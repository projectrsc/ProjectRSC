from com.prsc.gs.plugins.listeners.action import TalkToNpcListener
from com.prsc.gs.plugins.listeners.executive import TalkToNpcExecutiveListener
from com.prsc.gs.plugins import PlugInterface

'''
@author: GORF
@location: Port Sarim
@description: Monk of entrana 
'''

class MonkOfEntrana(PlugInterface, TalkToNpcListener, TalkToNpcExecutiveListener):
	
	# NPCs USED
	MONK_OF_ENTRANA = 212
	
	DISALLOWED_ITEMS = ["spear", "emerald", "ruby", "sapphire", "diamond", "dragon", "staff", "battle", "amulet", "dagger", "sword", "shield", "helmet", "top", "body", "legs", "skirt", "stake", "mace", "axe", "staff", "armour", "scimitar", "bow", "arrows", "amulet"]
	
	def onTalkToNpc(self, player, npc):
		script = player.getScriptHelper()
		script.setActiveNpc(npc)
		script.occupy()
		
		# START YOUR CODE HERE
		onEntrana = player.getLocation().inBounds(390, 530, 440, 580)
		script.sendNpcChat("Are you looking to take passage to our holy island?", "if so your weapons and armour must be left behind")
		option = script.pickOption(["No I don't wish to go", "Yes, Okay I'm ready to go"])
		if option == 1:
			script.displayMessage("The monk quickly searches you")
			script.sleep(1500)
			failed = False
			for item in player.getInventory().getItems():
				item_name = item.getDef().getName().lower()
				for disallowed in self.DISALLOWED_ITEMS:
					if item_name.startswith("unstrung") and item_name.endswith("bow"):
						failed = False
						continue
					if item_name.startswith("headless") and item_name.endswith("arrows"):
						failed = False
						continue
					if item_name.startswith(disallowed):
						failed = True
						break
					if item_name.endswith(disallowed):
						failed = True
						break
			if failed:
				script.sendNpcChat("Sorry we cannot allow you on to our island", 
								"Make sure you are not carrying weapons or armour please")
			else:
				script.displayMessage("You board the ship")
				script.sleep(1500)
				if onEntrana:
					script.movePlayer(263, 659, False)
					script.sleep(2000)
					script.displayMessage("The ship arrives at Port Sarim")
				else:
					script.movePlayer(418, 570, False)
					script.sleep(2000)
					script.displayMessage("The ship arrives at Entrana")
		#END YOUR CODE HERE
		script.release()
	
	def blockTalkToNpc(self, player, npc):
		return player.canAccessMembers() and npc.getID() == self.MONK_OF_ENTRANA # THE NPC WE ARE GOING TO HANDLE
