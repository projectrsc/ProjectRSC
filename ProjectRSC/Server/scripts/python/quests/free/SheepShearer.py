'''
Created on Nov 13, 2012

Quest: Sheep Shearer

@author: GORF
@difficulty: Easy
'''
from com.prsc.gs.plugins import Quest
from com.prsc.gs.plugins.listeners.executive import TalkToNpcExecutiveListener
from com.prsc.gs.plugins.listeners.action import TalkToNpcListener

class SheepShearer(Quest, TalkToNpcListener, TalkToNpcExecutiveListener):
    
    # NPCs USED
    FARMER_FRED = 77
    
    # ITEMS USED
    MONEY = 10
    WOOL = 145
    BALL_OF_WOOL = 207
    
    # CONDITION
    AMOUNT_NEEDED = 20
    
    def getQuestId(self):
        return 11
    
    def getQuestName(self):
        return "Sheep Shearer"
    
    def isMembers(self):
        return False
    
    def handleReward(self, player):
        script = player.getScriptHelper()
        script.setActiveQuest(self)
        
        script.displayMessage("The farmer hands you some coins")
        script.addItem(self.MONEY, 60)
        script.displayMessage("Well done you have completed the sheep shearer quest")
        script.advanceStat(player.SkillType.CRAFTING, 150)
        script.displayMessage("@gre@You have gained 1 quest point!")
        script.addQuestPoints(1)
            
    def onTalkToNpc(self, player, npc):
        script = player.getScriptHelper()
        
        script.setActiveNpc(npc)
        script.setActiveQuest(self)
        stage = script.getQuestStage()
        script.occupy()
        
        if stage == -1: # completed stage
            script.sendNpcChat("what are you doing on my land?", "you're not the one who keeps leaving all my gates open?", "and letting out all my sheep?")
            option = script.pickOption(["I'm looking for something to kill", "I'm lost"])
            if option == 0:
                script.sendNpcChat("what on my land?", "leave my livestock alone you scoundrel")
            elif option == 1:
                script.sendNpcChat("how can you be lost?", "just follow the road east and south")
            script.release()
        elif stage == 0: # starting stage
            script.sendNpcChat("what are you doing on my land?", "you're not the one who keeps leaving all my gates open?", "and letting out all my sheep?")
            option = script.pickOption(["I'm looking for a quest", "I'm looking for something to kill", "I'm lost"])
            if option == 0:
                script.sendNpcChat("you're after a quest, you say?", "actually i could do with a bit of help", "my sheep are getting mighty woolly", "if you could sheer them", "and while your at it spin the wool for me too", "yes, that's it. bring me 20 balls of wool", "and i'm sure i can sort out some sort of payment", "of course, there's the small matter of the thing")
                subOption = script.pickOption(["Yes okay. I can do that", "That doesn't sound a very exciting quest", "What do you mean, the thing?"])
                if subOption == 0:
                    self.startQuest(script)
                elif subOption == 1:
                    script.sendNpcChat("well what do you expect if you ask a farmer for a quest?", "now are you going to help me or not?")
                    startQuest = script.pickOption(["Yes okay. I can do that", "No I'll give it a miss"])
                    if startQuest == 0:
                        self.startQuest(script)
                    else:
                        script.release()
                elif subOption == 2:
                    script.sendNpcChat("i wouldn't worry about it", "something ate all the previous shears", "they probably got unlucky", "so are you going to help me?")
                    startQuest = script.pickOption(["Yes okay. I can do that", "Erm I'm a bit worried about this thing"])
                    if startQuest == 0:
                        self.startQuest(script)
                    else:
                        script.sendNpcChat("i'm sure it's nothing to worry about", "it's possible the other shearers aren't dead at all", "and are just hiding in the woods or something")
                        script.sendPlayerChat("i'm not convinced")
                        script.release()
            elif option == 1:
                script.sendNpcChat("what on my land?", "leave my livestock alone you scoundrel")
                script.release()
            elif option == 2:
                script.sendNpcChat("how can you be lost?", "just follow the road east and south", "you'll end up in Lumbridge fairly quickly")
                script.release()
        elif stage == 1: # final stage (TODO: not complete)
            script.sendNpcChat("how are you doing getting those balls of wool?")
            if script.countItem(self.BALL_OF_WOOL) >= self.AMOUNT_NEEDED:
                script.sendPlayerChat("i have some")
                script.sendNpcChat("give em here then")
                while script.hasItem(self.BALL_OF_WOOL):
                    script.displayMessage("You give Fred a ball of wool")
                    script.removeItem(self.BALL_OF_WOOL, 1)
                    script.sleep(2500)
                script.sendPlayerChat("that's all of them")
                script.sendNpcChat("i guess i'd better pay you then")
                script.release()
                script.setQuestStage(-1)
                script.setQuestCompleted()
            elif script.hasItem(self.BALL_OF_WOOL):
                script.sendPlayerChat("i have some")
                script.sendNpcChat("i need more before i can pay you")
            elif script.hasItem(self.WOOL):
                script.sendPlayerChat("well i've got some wool", "i've not managed to make it into a ball though")
                script.sendNpcChat("well go find a spinning wheel then", "and get spinning")
            else:
                script.sendPlayerChat("i haven't got any at the moment")
                script.sendNpcChat("ah well at least you haven't been eaten")
        script.release()
    
    def startQuest(self, script):
        script.sendNpcChat("ok i'll see you when you have some wool")
        script.setQuestStage(1)
        script.release()
    
    def blockTalkToNpc(self, player, npc):
        return npc.getID() == self.FARMER_FRED  
        