from org.darkquest.gs.plugins.listeners.action import InvUseOnNpcListener
from org.darkquest.gs.plugins.listeners.executive import InvUseOnNpcExecutiveListener
from org.darkquest.gs.plugins import PlugInterface

'''
@author: GORF
A sheep
'''
class Sheep(PlugInterface, InvUseOnNpcListener, InvUseOnNpcExecutiveListener):

    def onInvUseOnNpc(self, player, npc, item):
        script = player.getScriptHelper()
        script.setActiveNpc(npc)
        
        if item.getID() == 144: # USE PLAYER HERE, OTHERWISE THE OTHER PLAYER WILL BLOCK
            script.occupy()
            script.displayMessage("You attempt to shear the sheep")
            random = script.getRandom(0, 4)
            script.showBubble(144)
            script.sleep(2000)
            script.faceNpc(npc)
            if random != 0:
                script.displayMessage("You get some wool")
                script.addItem(145, 1)
            else:
                script.displayMessage("The sheep manages to get away from you!")
            script.release()
            
    def blockInvUseOnNpc(self, player, npc, item):
        return npc.getID() == 2 and item.getID() == 144
    
        
