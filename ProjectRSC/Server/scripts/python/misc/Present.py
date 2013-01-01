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
		script.displayMessage("Nothing interesting happens")
		
	def blockInvAction(self, item, player):
		return item.getID() == 980 and item.getDef().getCommand() == "Open"
