from org.darkquest.gs.plugins.listeners.action import TalkToNpcListener
from org.darkquest.gs.plugins.listeners.executive import TalkToNpcExecutiveListener
from org.darkquest.gs.plugins import PlugInterface

'''
@author: xEnt
NPC: Wyson The Gardener
Location: Falador
'''
class Wyson_The_Gardener(PlugInterface, TalkToNpcListener, TalkToNpcExecutiveListener):

    NPC_ID = 116
    WOAD_LEAF = 281

    def onTalkToNpc(self, player, npc):
        player.setActiveNpc(npc)
        player.occupy();
        
        player.sendNpcChat(["i am the gardener round here", 
                          "do you have any gardening that needs doing?"])
        opt = player.pickOption(["I'm looking for woad leaves", "Not right now thanks"])
        if opt == 0:
            player.sendNpcChat("well luckily for you i may have some around here somewhere")
            player.sendPlayerChat("can i buy one please?")
            player.sendNpcChat("how much are you willing to pay?")
            opt = player.pickOption(["How about 5 coins?", "How about 10 coins?",
                                   "How about 15 coins?", "How about 20 coins?"])
            if opt == 0 or opt == 1:
                player.sendNpcChat(["no no thats far too little. woad leaves are hard to get you know",
                                  "i used to have plenty but someone kept stealing them off me"])
            elif opt == 2 or opt == 3:
                player.sendNpcChat("mmmm ok that sounds fair." if opt == 2 else
                                  "i used to have plenty but someone kept stealing them off me")
                amount = (5 * opt) + 5
                if player.hasItem(10, amount):
                    player.removeItem(10, amount)
                    player.displayMessage("you give wyson " + amount + " coins")
                    player.addItem(self.WOAD_LEAF)
                    player.displayMessage("wyson the gardener gives you some woad leaves")
       
                else:
                    player.sendPlayerChat("i dont have enough coins to buy the leaves. i'll come back later")
        player.release()
        
    def blockTalkToNpc(self, player, npc):
        return npc.getID() == self.NPC_ID
        
