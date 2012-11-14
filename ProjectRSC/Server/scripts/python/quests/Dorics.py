'''
Created on Nov 12, 2012

Quest: Doric's Quest

@author: GORF
'''
from org.darkquest.gs.plugins import Quest
from org.darkquest.gs.plugins.listeners.executive import TalkToNpcExecutiveListener, InvUseOnObjectExecutiveListener
from org.darkquest.gs.plugins.listeners.action import TalkToNpcListener, InvUseOnObjectListener

class Dorics(Quest, TalkToNpcListener, InvUseOnObjectListener, TalkToNpcExecutiveListener, InvUseOnObjectExecutiveListener):
    
    bar_ids = [169, 170, 171, 172, 173, 174, 408]
    
    def getQuestId(self):
        return 3
    
    def getQuestName(self):
        return "Doric's Quest"
    
    def isMembers(self):
        return False
    
    def handleReward(self, player):
        script = player.getScriptHelper()
        script.setActiveQuest(self)
        
        script.displayMessage("You have completed Doric's quest")
        script.advanceStat(player.SkillType.MINING, 2200)
        script.displayMessage("@gre@You have gained 1 quest point!")
        script.addQuestPoints(1)
            
    def onTalkToNpc(self, player, npc):
        script = player.getScriptHelper()
        
        script.setActiveNpc(npc)
        script.setActiveQuest(self)
        stage = script.getQuestStage()
        script.occupy()
        
        if stage == -1: # completed stage
            script.sendNpcChat("Hello traveller, how is your Metalworking coming along?")
            script.sendPlayerChat("Not too bad thanks Doric")
            script.sendNpcChat("Good, the love of metal is a thing close to my heart")
        elif stage == 0: # starting stage
            script.sendNpcChat("Hello traveller, what brings you to my humble smithy?")
            option = script.pickOption(["I wanted to use your anvils", "Mind your own business, shortstuff", "I was just checking out the landscape", "What do you make here?"])
            if option == 0:
                script.sendNpcChat("My anvils get enough work with my own use", "I make amulets, it takes a lot of work.", "If you could get me some more materials I could let you use them")
                subOption = script.pickOption(["Yes I will get you materials", "No, hitting rocks is for the boring people, sorry."])
                if subOption == 0:
                    script.sendNpcChat("Well, clay is what I use more than anything. I make casts", "Could you get me 6 clay, and 4 copper ore and 2 iron ore please?", "I could pay a little, and let you use my anvils")
                    script.sendPlayerChat("Certainly, I will get them for you. goodbye")
                    script.setQuestStage(1)
                    script.release()
                elif subOption == 1:
                    script.sendNpcChat("That is your choice, nice to meet you anyway")
                    script.release()
            elif option == 1:
                script.sendNpcChat("How nice to meet someone with such pleasant manners", "Do come again when you need to shout at someone smaller than you")
                script.release()
            elif option == 2:
                script.sendNpcChat("We have a fine town here, it suits us very well", "Please enjoy your travels. And do visit my friends in their mine")
                script.release()
            elif option == 3:
                script.sendNpcChat("I make amulets. I am the best maker of them in Runescape")
                script.sendPlayerChat("Do you have any to sell?")
                script.sendNpcChat("Not at the moment, sorry. Try again later")
        elif stage == 1: # final stage
            script.sendNpcChat("Have you got my materials yet traveller?")
            if script.countItem(149) >= 6 and script.countItem(150) >= 4 and script.countItem(151) >= 2:
                script.sendPlayerChat("I have everything you need")
                script.sendNpcChat("Many thanks, pass them here please")
                script.displayMessage("You hand the clay, copper and iron to Doric")
                script.sleep(500)
                script.removeItem(149, 6)
                script.removeItem(150, 4)
                script.removeItem(151, 2)
                script.sleep(500)
                script.sendNpcChat("I can spare you some coins for your trouble")
                script.displayMessage("Doric hands you 180 coins")
                script.sleep(500)
                script.addItem(10, 180)
                script.sendNpcChat("Please use my anvils any time you want")
                script.setQuestStage(-1)
                script.setQuestCompleted()
            else:
                script.sendPlayerChat("Sorry, I don't have them all yet")
                script.sendNpcChat("Not to worry, stick at it", "Remember I need 6 Clay, 4 Copper ore and 2 Iron ore")
            
        script.release() 
    
    def onInvUseOnObject(self, gameObj, item, player):
        script = player.getScriptHelper()
        
        doric = script.getNpc(144)
        script.setActiveNpc(doric)
        script.setActiveQuest(self)
        stage = script.getQuestStage()
        script.occupy()
        
        if stage >= 0:
            if doric.isBusy():
                script.displayMessage("I'd better go ask Doric if I can use this first")
            else:
                script.faceNpc(doric)
                script.sendNpcChat("Heh who said you could use that?")
                
        script.release()
    
    def blockTalkToNpc(self, player, npc):
        return npc.getID() == 144 
    
    def blockInvUseOnObject(self, gameObj, invItem, player):
        return gameObj.getID() == 177 and self.bar_ids.count(invItem.getID()) > 0 # dorics anvil and hammer
        
    
        