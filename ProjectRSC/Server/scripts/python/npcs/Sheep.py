from org.darkquest.gs.plugins.listeners.action import InvUseOnNpcListener
from org.darkquest.gs.plugins.listeners.executive import InvUseOnNpcExecutiveListener
from org.darkquest.gs.plugins import PlugInterface

'''
@author: GORF
A sheep
'''
class Sheep(PlugInterface, InvUseOnNpcListener, InvUseOnNpcExecutiveListener):

    def onInvUseOnNpc(self, player, npc, item):
        player.setActiveNpc(npc)
        
        if item.getID() == 144: # USE PLAYER HERE, OTHERWISE THE OTHER PLAYER WILL BLOCK
            player.occupy()
            player.displayMessage("You attempt to shear the sheep")
            random = player.getRandom(0, 4)
            player.showBubble(144)
            player.sleep(2000)
            player.faceNpc(npc)
            if random != 0:
                player.displayMessage("You get some wool")
                player.addItem(145, 1)
            else:
                player.displayMessage("The sheep manages to get away from you!")
            player.release()
            
    def blockInvUseOnNpc(self, player, npc, item):
        return npc.getID() == 2 and item.getID() == 144
    
        
