from com.prsc.gs.plugins.listeners.action import TalkToNpcListener
from com.prsc.gs.plugins.listeners.executive import TalkToNpcExecutiveListener
from com.prsc.gs.plugins import PlugInterface
from com.prsc.gs.model import Player, Npc

'''
@author: xEnt
This is the Banana collector script!
'''
class Luthas(PlugInterface, TalkToNpcListener, TalkToNpcExecutiveListener):

    NPC_ID = 164
    BANANA_ID = 249

    def onTalkToNpc(self, player, npc):
        script = player.getScriptHelper()
        
        script.setActiveNpc(npc)
        script.occupy()
        script.sendNpcChat("Hello, i am after 20 Bananas, do you have 20 you can sell?")
        opt = script.pickOption(["Yes i will sell you 20 bananas", "No sorry, i don't have any"])
        if opt == 0:
            script.sendNpcChat("I will give you 30gp for your 20 bananas is that ok?")
            opt = script.pickOption(["Sure", "Sorry, i would rather eat them"])
            if opt == 0:
                if script.hasItem(self.BANANA_ID, 20):
                    script.removeItem(self.BANANA_ID, 20)
                    script.addItem(10, 30)
                    script.displayMessage("You receive 30gp")
                else:
                    script.sendNpcChat("It looks like you don't have enough Bananas, don't waste my time.")
        elif opt == 1:
            script.sendNpcChat("Come back when you do!")
            
        script.release()
        
    def blockTalkToNpc(self, player, npc):
        return npc.getID() == self.NPC_ID
    
        
