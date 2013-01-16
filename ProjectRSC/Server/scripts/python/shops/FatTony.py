from org.darkquest.gs.plugins.listeners.action import TalkToNpcListener
from org.darkquest.gs.plugins.listeners.executive import TalkToNpcExecutiveListener
from org.darkquest.gs.plugins import ShopInterface
from org.darkquest.gs.model import InvItem
from org.darkquest.gs.world import Shop

'''
@author: GORF
This is a generic template for an NPC handler (always document what the handler is)
'''

class FatTony(ShopInterface, TalkToNpcListener, TalkToNpcExecutiveListener):
	
	# NPCs USED
	FAT_TONY = 235
	
	item = [InvItem(321, 30)]
	shop = Shop(False, 5000, 4, 2, item)
	
	def onTalkToNpc(self, player, npc):
		script = player.getScriptHelper()
		
		script.setActiveNpc(npc)
		script.occupy()
		
		# START YOUR CODE HERE
		script.sendNpcChat("Go away I'm very busy")		
		option = script.pickOption(["Sorry to disturb you", "What are you busy doing?", "Have you anything to sell?"])
		if option == 1:
			script.sendNpcChat("I'm cooking pizzas for the people in this camp", 
							"Not that these louts appreciate my gourmet cooking")
			sub_option = script.pickOption(["So what is a gourmet chef doing cooking for bandits?", "Can I have some pizza too?",
										"OK I'll leave you to it"])
			if sub_option == 0:
				script.sendNpcChat("Well I'm an outlaw", "I was accused of giving the king food poisoning",
								"The thought of it - I think he just drank too much wine that night", 
								"I had to flee the kingdom of Misthalin")
				remaining_option = script.pickOption(["Can I have some pizza too?", "OK I'll leave you to it"])
				if remaining_option == 0:
					self.wantsPizza(script)
			elif sub_option == 1:
				self.wantsPizza(script)
		elif option == 2:
			self.sendNpcChat("Well I guess I can sell you some half made pizzas")
			player.setAccessingShop(shop);
			player.getActionSender().showShop(shop);
		#END YOUR CODE HERE
		script.release()
	
	def wantsPizza(self, script):
		script.sendNpcChat("Well this pizza is really meant for the bandits")
		next_option = script.pickOption(["Yes Okay", "Oh if I have to pay I don't want any"])
		if next_option == 0:
			script.sendNpcChat("I guess I could sell you some pizza bases though")
			player.setAccessingShop(shop);
			player.getActionSender().showShop(shop);
		script.release()
	
	def blockTalkToNpc(self, player, npc):
		return npc.getID() == self.FAT_TONY # THE NPC WE ARE GOING TO HANDLE
