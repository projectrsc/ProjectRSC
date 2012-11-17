from org.darkquest.gs.plugins import Quest
from org.darkquest.gs.plugins.listeners.action import TalkToNpcListener
from org.darkquest.gs.plugins.listeners.executive import TalkToNpcExecutiveListener, InvUseOnObjectExecutiveListener

class CooksAssistant(Quest, TalkToNpcListener, TalkToNpcExecutiveListener, InvUseOnObjectExecutiveListener):

    introduction_question_responses = ["What's wrong?", "Well you could give me all your money", "You don't look very happy", "Nice hat"]

    cook_request_help_responses = ["Yes, I'll help you", "No, i don't feel like it. Maybe later"]
    
    def getQuestId(self):
        return 1
    
    def getQuestName(self):
        return "Cook's Assistant"
    
    def isMembers(self):
        return False
    
    def handleReward(self, player):
        script = player.getScriptHelper()
        script.setActiveQuest(self)
        
        script.displayMessage("You give some milk, an egg, and some flour to the cook")
        script.sleep(500)
        script.removeItem(22, 1)
        script.removeItem(19, 1)
        script.removeItem(136, 1)
        
        script.displayMessage("Well done. You have completed the cook's assistant quest")
        script.displayMessage("@gre@You just advanced 1 quest point!")
        
        script.addQuestPoints(1)
        script.advanceStat(player.SkillType.COOKING, 270)
    
    def onTalkToNpc(self, player, npc):        
        if npc.getID() == 7:
            script = player.getScriptHelper()
            script.setActiveNpc(npc)
            script.setActiveQuest(self)
            stage = script.getQuestStage()
            script.occupy()
            if stage == 0:
                self.startQuest(script)
            elif stage == 1:
                self.requestIngredients(script)
    
    def blockInvUseOnObject(self, gameObj, invItem, player):
        return gameObj.getID() == 133

    def startQuest(self, script):
        script.sendNpcChat("What am i to do?")
        
        option = script.pickOption(self.introduction_question_responses)
        if option == 0:
            self.cookRequestHelp(script)
        elif option == 1:
            script.sendNpcChat("Haha very funny")
            script.release()
        elif option == 2:
            script.sendNpcChat("No i'm not")
            self.cookRequestHelp(script)
        elif option == 3:
            script.sendNpcChat("Err thank you -it's a pretty ordinary cooks hat really")
            script.release()

    def cookRequestHelp(self, script):
        script.sendNpcChat("Ooh dear i'm in a terrible mess"
            , "it's the duke's bithday today", "i'm meant to be making him a big cake for this evening",
            "unfortunately, i've forgotten to buy some of the ingredients", "i'll never get them in time now",
            "i don't suppose you could help me?")
        option = script.pickOption(self.cook_request_help_responses)
        if option == 0:
            script.sendNpcChat("oh thank you, thank you", "i need milk, eggs, and flour", "i'd be very grateful if you could get them to me")
            script.setQuestStage(1)
        elif option == 1:
            script.sendNpcChat("ok, suit yourself")
        script.release()

    def requestIngredients(self, script):
        script.sendNpcChat("how are you getting on with finding those ingredients?")
        milk = script.hasItem(22)
        eggs = script.hasItem(19)
        flour = script.hasItem(136)

        if not milk and not eggs and not flour:
            script.sendPlayerChat("i'm afraid i don't have any yet!")
            script.sendNpcChat("oh dear oh dear!", "i need flour, eggs, and milk", "without them i am doomed!")
        else:
            if milk and eggs and flour:
                script.sendPlayerChat("i now have everything you need for your cake", "milk, flour, and an egg!")
                script.sendNpcChat("i am saved thankyou!")
                script.setQuestStage(-1)
                script.setQuestCompleted()
            else:
                script.sendPlayerChat("i have found some of the things you asked for:")
                if milk:
                    script.sendPlayerChat("i have some milk")
                if flour:
                    script.sendPlayerChat("i have some flour")
                if eggs:
                    script.sendPlayerChat("i have an egg")
                script.sendNpcChat("great, but can you get the other ingredients as well?", "you still need to find")
                if not milk:
                    script.sendNpcChat("some milk")
                if not flour:
                    script.sendNpcChat("some flour")
                if not eggs:
                    script.sendNpcChat("an egg")
                script.sendPlayerChat("ok i'll try to find that for you")
        script.release()
    
    def blockTalkToNpc(self, player, npc):
        script = player.getScriptHelper()
        script.setActiveQuest(self)
        return npc.getID() == 7 and script.getQuestStage() >= 0
