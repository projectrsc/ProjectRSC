from org.darkquest.gs.plugins.listeners.action import TalkToNpcListener
from org.darkquest.gs.plugins.listeners.executive import TalkToNpcExecutiveListener
from org.darkquest.gs.plugins import NpcInterface

'''
@author: xEnt
NPC: Wyson The Gardener
Location: Falador
'''
class Wyson_The_Gardener(NpcInterface, TalkToNpcListener, TalkToNpcExecutiveListener):

    NPC_ID = 116
    WOAD_LEAF = 281

    def onTalkToNpc(self, player, npc):
        #if npc.getID() != self.NPC_ID:
        #    return
        
        self.setParticipants(player, npc)
        self.occupy();
        
        self.sendNpcChat(["i am the gardener round here", 
                          "do you have any gardening that needs doing?"])
        opt = self.pickOption(["I'm looking for woad leaves", "Not right now thanks"])
        if opt == 0:
            self.sendNpcChat("well luckily for you i may have some around here somewhere")
            self.sendPlayerChat("can i buy one please?")
            self.sendNpcChat("how much are you willing to pay?")
            opt = self.pickOption(["How about 5 coins?", "How about 10 coins?",
                                   "How about 15 coins?", "How about 20 coins?"])
            if opt == 0 or opt == 1:
                self.sendNpcChat(["no no thats far too little. woad leaves are hard to get you know",
                                  "i used to have plenty but someone kept stealing them off me"])
            elif opt == 2 or opt == 3:
                self.sendNpcChat("mmmm ok that sounds fair." if opt == 2 else
                                  "i used to have plenty but someone kept stealing them off me")
                amount = (5 * opt) + 5
                if self.hasItem(10, amount):
                    self.removeItem(10, amount)
                    self.displayMessage("you give wyson " + amount + " coins")
                    self.addItem(self.WOAD_LEAF)
                    self.displayMessage("wyson the gardener gives you some woad leaves")
       
                else:
                    self.sendPlayerChat("i dont have enough coins to buy the leaves. i'll come back later")
        self.release()
        
    def blockTalkToNpc(self, player, npc):
        return npc.getID() == self.NPC_ID
    
        
