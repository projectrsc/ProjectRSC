'''
Created on Nov 12, 2012

Quest: Doric's Quest

@author: GORF
'''
from org.darkquest.gs.plugins import Quest;
from org.darkquest.gs.plugins.listeners.executive import TalkToNpcExecutiveListener, InvUseOnObjectExecutiveListener;
from org.darkquest.gs.plugins.listeners.action import TalkToNpcListener, InvUseOnObjectListener

class Dorics(Quest, TalkToNpcListener, InvUseOnObjectListener, TalkToNpcExecutiveListener, InvUseOnObjectExecutiveListener):
    
    def getQuestId(self):
        return 3
    
    def getQuestName(self):
        return "Doric's Quest"
    
    def isMembers(self):
        return False
    
    def handleReward(self, player):
        player.setActiveQuest(self)
        
        player.displayMessage("You have completed Doric's quest")
        player.advanceStat(player.SkillType.MINING, 2200)
        player.displayMessage("@gre@You have gained 1 quest point!")
        player.addQuestPoints(1)
            
    def onTalkToNpc(self, player, npc):
        player.setActiveNpc(npc)
        player.setActiveQuest(self)
        stage = player.getQuestStage()
        player.occupy()
        
        if stage == -1: # completed stage
            player.sendNpcChat("Hello traveller, how is your Metalworking coming along?")
            player.sendPlayerChat("Not too bad thanks Doric")
            player.sendNpcChat("Good, the love of metal is a thing close to my heart")
        elif stage == 0: # starting stage
            player.sendNpcChat("Hello traveller, what brings you to my humble smithy?")
            option = player.pickOption(["I wanted to use your anvils", "Mind your own business, shortstuff", "I was just checking out the landscape", "What do you make here?"])
            if option == 0:
                player.sendNpcChat("My anvils get enough work with my own use", "I make amulets, it takes a lot of work.", "If you could get me some more materials I could let you use them")
                subOption = player.pickOption(["Yes I will get you materials", "No, hitting rocks is for the boring people, sorry."])
                if subOption == 0:
                    player.sendNpcChat("Well, clay is what I use more than anything. I make casts", "Could you get me 6 clay, and 4 copper ore and 2 iron ore please?", "I could pay a little, and let you use my anvils")
                    player.sendPlayerChat("Certainly, I will get them for you. goodbye")
                    player.setQuestStage(1)
                    player.release()
                elif subOption == 1:
                    player.sendNpcChat("That is your choice, nice to meet you anyway")
                    player.release()
            elif option == 1:
                player.sendNpcChat("How nice to meet someone with such pleasant manners", "Do come again when you need to shout at someone smaller than you")
                player.release()
            elif option == 2:
                player.sendNpcChat("We have a fine town here, it suits us very well", "Please enjoy your travels. And do visit my friends in their mine")
                player.release()
            elif option == 3:
                player.sendNpcChat("I make amulets. I am the best maker of them in Runescape")
                player.sendPlayerChat("Do you have any to sell?")
                player.sendNpcChat("Not at the moment, sorry. Try again later")
        elif stage == 1: # final stage
            player.sendNpcChat("Have you got my materials yet traveller?")
            if player.countItem(149) >= 6 and player.countItem(150) >= 4 and player.countItem(151) >= 2:
                player.sendPlayerChat("I have everything you need")
                player.sendNpcChat("Many thanks, pass them here please")
                player.displayMessage("You hand the clay, copper and iron to Doric")
                player.sleep(500)
                player.removeItem(149, 6)
                player.removeItem(150, 4)
                player.removeItem(151, 2)
                player.sleep(500)
                player.sendNpcChat("I can spare you some coins for your trouble")
                player.displayMessage("Doric hands you 180 coins")
                player.sleep(500)
                player.addItem(10, 180)
                player.sendNpcChat("Please use my anvils any time you want")
                player.setQuestStage(-1)
                player.setQuestCompleted()
            else:
                player.sendPlayerChat("Sorry, I don't have them all yet")
                player.sendNpcChat("Not to worry, stick at it", "Remember I need 6 Clay, 4 Copper ore and 2 Iron ore")
            
        player.release() 
    
    def onInvUseOnObject(self, gameObj, item, player):
        doric = player.getNpc(144)
        player.setActiveNpc(doric)
        player.setActiveQuest(self)
        stage = player.getQuestStage()
        player.occupy()
        
        if stage >= 0:
            if doric.isBusy():
                player.displayMessage("I'd better go ask Doric if I can use this first")
            else:
                player.faceNpc(doric)
                player.sendNpcChat("Heh who said you could use that?")
                
        player.release()
    
    def blockTalkToNpc(self, player, npc):
        return npc.getID() == 144 
    
    def blockInvUseOnObject(self, gameObj, invItem, player):
        return gameObj.getID() == 177 and invItem.getID() == 168 # dorics anvil and hammer
        
    
        