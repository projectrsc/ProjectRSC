'''
Created on Nov 15, 2012

Quest: Vampire Slayer

@author: GORF
'''
from org.darkquest.gs.plugins import Quest
from org.darkquest.gs.plugins.listeners.executive import TalkToNpcExecutiveListener
from org.darkquest.gs.plugins.listeners.action import TalkToNpcListener

class VampireSlayer(Quest, TalkToNpcListener, TalkToNpcExecutiveListener):
    
    MORGAN = 97
    HARLOW = 98
    
    def getQuestId(self):
        return 6
    
    def getQuestName(self):
        return "Vampire slayer"
    
    def isMembers(self):
        return False
    
    def handleReward(self, player):
        script = player.getScriptHelper()
        script.setActiveQuest(self)
        
        script.advanceStat(player.SkillType.ATTACK, 4825)
        script.displayMessage("@gre@You have gained 3 quest points!")
        script.addQuestPoints(3)
        script.sleep(1000)
        script.displayMessage("Well done, you have completed vampire slayer quest")
            
    def onTalkToNpc(self, player, npc):
        script = player.getScriptHelper()
        
        script.setActiveNpc(npc)
        script.setActiveQuest(self)
        stage = script.getQuestStage()
        script.occupy()
        
        # START CODE HERE
        
        # END CODE HERE
        script.release()
    
    def blockTalkToNpc(self, player, npc):
        return npc.getID() == self.MORGAN or npc.getID() == self.HARLOW  
        