from org.darkquest.gs.plugins.listeners.action import TalkToNpcListener
from org.darkquest.gs.plugins.listeners.executive import TalkToNpcExecutiveListener
from org.darkquest.gs.plugins import NpcInterface

'''
@author: GORF
This is a default handler
NOTE: You must add npcs in here that will be IN PERSISTENT USE ONLY (THAT MEANS NO QUEST NPCS/PLEASE KEEP THEM IN ORDER)
'''
class Default(NpcInterface, TalkToNpcListener, TalkToNpcExecutiveListener):
    
    USABLE_NPCS = [1, 11, 12, 26, 28, 33, 48, 54, 55, 56, 58, 59, 63, 69, 75, 82, 83, 84, 
                   85, 87, 88, 90, 93, 95, 101, 103, 105, 106, 115, 116, 125, 129, 130, 131, 
                   141, 142, 143, 144, 149, 155, 156, 157, 160, 161, 163, 164, 165, 166, 167, 
                   168, 169, 170, 171, 172, 173, 185, 186, 212, 224, 225, 226, 227, 231, 267, 268, 
                   280, 299, 308, 316, 317, 339, 341, 347, 348, 369, 370, 466, 467, 485, 501, 540, 
                   617, 764, 773, 778, 780, 792, 794, 795] # ANY NPCS CURRENTLY BEING USED ARE ADDED HERE

    def onTalkToNpc(self, player, npc): 
        if npc.getID() >= 0 and npc.getID() <= 796:
            for id in self.USABLE_NPCS:
                if npc.getID() == id:
                    return
                       
        self.setParticipants(player, npc)
        self.displayMessage("Nothing interesting happens")
        self.release()
        
    def blockTalkToNpc(self, player, npc):
        if npc.getID() >= 0 and npc.getID() <= 796:
            for id in self.USABLE_NPCS:
                if npc.getID() == id:
                    return False
            return True
    
        
