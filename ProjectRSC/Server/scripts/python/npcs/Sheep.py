from org.darkquest.gs.plugins.listeners.action import InvUseOnNpcListener
from org.darkquest.gs.plugins.listeners.executive import InvUseOnNpcExecutiveListener
from org.darkquest.gs.plugins import PlugInterface

'''
@author: GORF
A sheep
'''
class Sheep(PlugInterface, InvUseOnNpcListener, InvUseOnNpcExecutiveListener):

    def onInvUseOnNpc(self, player, npc, item):
        self.setParticipant(player)
        
        if item.getID() == 144:
            self.displayMessage("You attempt to shear the sheep")
            random = self.getRandom(0, 4)
            if random != 0:
                self.displayMessage("You get some wool")
                self.addItem(145, 1)
            else:
                self.displayMessage("The sheep manages to get away from you!")
            
    def blockInvUseOnNpc(self, player, npc, item):
        return npc.getID() == 2
    
        
