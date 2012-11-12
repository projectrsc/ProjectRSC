from org.darkquest.gs.plugins.listeners.action import TalkToNpcListener
from org.darkquest.gs.plugins.listeners.executive import TalkToNpcExecutiveListener
from org.darkquest.gs.plugins import PlugInterface
from org.darkquest.gs.model import Player, Npc

'''
@author: xEnt
This is the Banana collector script!
'''
class Luthas(PlugInterface, TalkToNpcListener, TalkToNpcExecutiveListener):

    NPC_ID = 164
    BANANA_ID = 249

    def onTalkToNpc(self, player, npc):
        self.setParticipants(player, npc)
        self.sendNpcChat("Hello, i am after 20 Bananas, do you have 20 you can sell?")
        opt = self.pickOption(["Yes i will sell you 20 bananas", "No sorry, i don't have any"])
        if opt == 0:
            self.sendNpcChat("I will give you 30gp for your 20 bananas is that ok?")
            opt = self.pickOption(["Sure", "Sorry, i would rather eat them"])
            if opt == 0:
                if self.hasItem(self.BANANA_ID, 20):
                    self.removeItem(self.BANANA_ID, 20)
                    self.addItem(10, 30)
                    self.displayMessage("You receive 30gp")
                else:
                    self.sendNpcChat("It looks like you don't have enough Bananas, don't waste my time.")
        elif opt == 1:
            self.sendNpcChat("Come back when you do!")
            
        self.release()
        
    def blockTalkToNpc(self, player, npc):
        return npc.getID() == self.NPC_ID
    
        
