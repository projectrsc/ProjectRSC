from com.prsc.gs.plugins.listeners.action import InvActionListener
from com.prsc.gs.plugins.listeners.executive import InvActionExecutiveListener
from com.prsc.gs.plugins import PlugInterface

'''
@author: GORF
Called when a user will be opening a holiday gift
'''

class Present(PlugInterface, InvActionListener, InvActionExecutiveListener):
	
	# ITEMS USED
	RANDOM_ITEMS_GIVEN = ('221', '226', '230', '235', '192', '575', '10', '11', '81', '80', '93', '92', '116', '120', '196', '330', '373', '398', '401', '406', '408')
	
	MONEY_AMOUNTS = ('1000', '10000', '100000')
	
	MAX_ITEMS = 5
	
	def onInvAction(self, item, player):
		#script = player.getScriptHelper()
		#script.occupy()
		#script.displayMessage("You open the box")
		#script.sleep(2500)
		#script.removeItem(item.getID(), 1)
		#count = 1
		#amount = script.getRandom(1, self.MAX_ITEMS)
		#while count < amount:
		#	rand = script.getRandom(0, 20) 	
		#	selected = self.RANDOM_ITEMS_GIVEN[rand]
		#	selected = int(selected)
		#	n_item = script.getItem(selected)
			
		#	if n_item.getDef().isStackable():
		#		stackable_random = script.getRandom(0, 2)
		#		if n_item.getID() == 10 or n_item.getID == 11:
		#			amount = self.MONEY_AMOUNTS[stackable_random]
		#			stackable_amount = int(amount)
		#			script.addItem(selected, stackable_amount)
		#	else:
		#		script.addItem(selected, 1)
		#	script.displayMessage("You find " + n_item.getDef().getName())
		#	count = count + 1
		#script.release()
		
	def blockInvAction(self, item, player):
		return item.getID() == 980 and item.getDef().getCommand() == "Open"
