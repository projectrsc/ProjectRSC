from com.prsc.gs.plugins.listeners.action import InvActionListener
from com.prsc.gs.plugins.listeners.executive import InvActionExecutiveListener
from com.prsc.config import Constants
from com.prsc.gs.external import EntityHandler, ItemDef, ItemHerbDef, ItemHerbSecond
from com.prsc.gs.model import InvItem
from com.prsc.gs.plugins import PlugInterface

'''
@author: GORF
Herblaw skill
'''
class Herblaw(PlugInterface, InvActionListener, InvActionExecutiveListener):
	
	def onInvAction(self, item, player):
		script = player.getScriptHelper()
		herb_def = item.getUnIdentHerbDef()
		
		if herb_def == None or player.isBusy():
			return
		
		if script.getCurrentLevel(player.SkillType.HERBLAW) < herb_def.getLevelRequired():
			script.displayMessage("Your herblaw ability is not high enough to clean this herb.")
			return
		
		script.occupy()
		self.handleIdentify(script, player, item, herb_def)
		# handle batch
		if script.hasItem(item.getID()):
			if Constants.GameServer.BATCH_EVENTS:
				self.handleIdentify(script, player, item, herb_def)
		script.release()
	
	def handleIdentify(self, script, player, item, herb_def):
		herb_name = item.getDef().getName()
		script.displayMessage("You clean the mud off the " + herb_name)
		script.sleep(1000)
		script.removeItem(item.getID(), 1)
		script.addItem(herb_def.getNewId(), 1)
		script.advanceStat(player.SkillType.HERBLAW, herb_def.getExp(), True)
         	
	def blockInvAction(self, item, player):
		return player.canAccessMembers() and item.getDef().getCommand() == "Identify"
