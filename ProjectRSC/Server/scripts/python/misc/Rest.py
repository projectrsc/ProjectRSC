from com.prsc.gs.plugins.listeners.action import InvActionListener, ObjectActionListener
from com.prsc.gs.plugins.listeners.executive import InvActionExecutiveListener, ObjectActionExecutiveListener
from com.prsc.gs.plugins import PlugInterface

'''
@author: GORF
Called when a user will be activating a sleeping event
'''

class Rest(PlugInterface, InvActionListener, ObjectActionListener, InvActionExecutiveListener, ObjectActionExecutiveListener):
	
	def onInvAction(self, item, player):
		script = player.getScriptHelper()
		script.occupy()
		script.sendSleep(False)	
		script.release()
	
	def onObjectAction(self, gameObject, command, player):
		script = player.getScriptHelper()
		script.sleep(1500)
		script.occupy()
		script.sendSleep(True)	
		script.release()
		
	def blockInvAction(self, item, player):
		return item.getID() == 1263 and not player.isSleeping()
	
	def blockObjectAction(self, gameObject, command, player):
		return command == "rest" and not player.isSleeping()
