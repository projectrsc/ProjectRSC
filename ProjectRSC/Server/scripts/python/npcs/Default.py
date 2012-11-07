from org.darkquest.gs.plugins.listeners.action import TalkToNpcListener
from org.darkquest.gs.plugins.listeners.executive import TalkToNpcExecutiveListener
from org.darkquest.gs.plugins import NpcInterface
from org.darkquest.gs.model import Player, Npc

'''
@author: xEnt
This is the Banana collector script!
'''
class Default(NpcInterface, TalkToNpcListener, TalkToNpcExecutiveListener):
    
    USABLE_NPCS = [11, 12, 26, 93, 95, 116, 125, 142, 144, 148, 149, 160, 161, 163, 164, 166, 170, 171, 172, 212, 224, 225, 226, 227, 231, 267, 268, 280, 299, 316, 317, 339, 341, 347, 348, 369, 370, 466, 467, 485, 540, 617, 764, 778, 792, 794, 795] # ANY NPCS CURRENTLY BEING USED ARE ADDED HERE

    def onTalkToNpc(self, player, npc):        
        self.setParticipants(player, npc)
        self.displayMessage("Nothing interesting happens")
        self.release()
        
    def blockTalkToNpc(self, player, npc):
        if npc.getID() >= 0 and npc.getID() <= 796:
            for id in self.USABLE_NPCS:
                if npc.getID() == id:
                    return False
            return True
    
        
