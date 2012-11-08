'''
Created on Oct 23, 2012

Example Python based Quest

@author: openfrog
'''
from org.darkquest.gs.plugins import Quest;
from org.darkquest.gs.plugins.listeners.executive import TalkToNpcExecutiveListener, InvUseOnObjectExecutiveListener;
from org.darkquest.gs.plugins.listeners.action import TalkToNpcListener

from org.darkquest.gs.model import Player, Npc, GameObject, InvItem

class Dorics(Quest, TalkToNpcListener, TalkToNpcExecutiveListener, InvUseOnObjectExecutiveListener):
    '''
    classdocs
    '''
    def getQuestId(self):
        return 3
    
    def getQuestName(self):
        return "Doric's Quest"
    
    def isMembers(self):
        return False
    
    def handleReward(self):
        self.displayMessage("You have completed Dorics quest")
        self.advanceStat(self.SkillType.MINING, 2200)
        self.displayMessage("@gre@You have gained 1 quest point!")
        self.addQuestPoints(1)
            
    def onTalkToNpc(self, player, npc):
        #if npc.getID() != 144:
        #    return 
        
        self.setParticipants(player, npc)
        stage = self.getQuestStage()
        self.occupy()
        
        if stage == 0:
            self.sendNpcChat("Hello traveller, what brings you to my humble smithy?")
            
        option = self.pickOption(["Option 1", "Option 2", "Option 3"]) 
    
    def blockTalkToNpc(self, player, npc):
        self.setParticipant(player)
        return npc.getID() == 144 and self.getQuestStage() >= 0 # doric
    
    def blockInvUseOnObject(self, gameObj, invItem, player):
        return gameObj.getID() == 177 # dorics anvil
        
    
        