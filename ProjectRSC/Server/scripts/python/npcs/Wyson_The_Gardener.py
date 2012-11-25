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
        script = player.getScriptHelper()
        script.setActiveNpc(npc)
        script.occupy();
        
        script.sendNpcChat(["i am the gardener round here", 
                          "do you have any gardening that needs doing?"])
        opt = script.pickOption(["I'm looking for woad leaves", "Not right now thanks"])
        if opt == 0:
            script.sendNpcChat("well luckily for you i may have some around here somewhere")
            script.sendPlayerChat("can i buy one please?")
            script.sendNpcChat("how much are you willing to pay?")
            opt = script.pickOption(["How about 5 coins?", "How about 10 coins?",
                                   "How about 15 coins?", "How about 20 coins?"])
            if opt == 0 or opt == 1:
                script.sendNpcChat(["no no thats far too little. woad leaves are hard to get you know",
                                  "i used to have plenty but someone kept stealing them off me"])
            elif opt == 2 or opt == 3:
                if opt == 2:
                    script.sendNpcChat("mmmm ok that sounds fair.")
                else:
                    script.sendNpcChat("i used to have plenty but someone kept stealing them off me")
                v_amount = 5 * opt
                amount = v_amount + 5
                if script.hasItem(10, amount):
                    script.removeItem(10, amount)
                    script.displayMessage("you give wyson " + str(amount) + " coins")
                    script.addItem(self.WOAD_LEAF)
                    script.displayMessage("wyson the gardener gives you some woad leaves")
                else:
                    script.sendPlayerChat("i dont have enough coins to buy the leaves. i'll come back later")
        script.release()
        
    def blockTalkToNpc(self, player, npc):
        return npc.getID() == self.NPC_ID
        
