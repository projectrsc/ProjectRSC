from com.prsc.gs.plugins.listeners.action import InvUseOnNpcListener
from com.prsc.gs.plugins.listeners.executive import InvUseOnNpcExecutiveListener
from com.prsc.gs.plugins import PlugInterface

'''
@author: GORF
A sheep
'''
class Sheep(PlugInterface, InvUseOnNpcListener, InvUseOnNpcExecutiveListener):
    
    SHEEP = 2
    SHEARS = 144
    WOOL = 145

    def onInvUseOnNpc(self, player, npc, item):
        script = player.getScriptHelper()
        script.setActiveNpc(npc)
        
        script.occupy()
        script.displayMessage("You attempt to shear the sheep")
        random = script.getRandom(0, 4)
        script.showBubble(self.SHEARS)
        script.sleep(2000)
        script.faceNpc(npc)
        
        if random != 0:
            script.displayMessage("You get some wool")
            script.addItem(self.WOOL, 1)
        else:
            script.displayMessage("The sheep manages to get away from you!")
            
        script.release()
            
    def blockInvUseOnNpc(self, player, npc, item):
        return npc.getID() == self.SHEEP and item.getID() == self.SHEARS # REMOVE THE OBJECT IF ITS USED MORE 
    
        
