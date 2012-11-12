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
    
    def handleReward(self):
        self.displayMessage("You give some milk, an egg, and some flour to the cook")
        self.sleep(500)
        self.removeItem(22, 1)
        self.removeItem(19, 1)
        self.removeItem(136, 1)
        
        self.displayMessage("Well done. You have completed the cook's assistant quest")
        self.displayMessage("@gre@You just advanced 1 quest point!")
        
        self.addQuestPoints(1)
        self.advanceStat(self.SkillType.COOKING, 270)
    
    def onTalkToNpc(self, player, npc):        
        if npc.getID() == 7:
            self.setParticipants(player, npc)
            stage = self.getQuestStage()
            self.occupy()
            if stage == 0:
                self.start_quest()
            elif stage == 1:
                self.request_ingredients()
    
    def blockInvUseOnObject(self, gameObj, invItem, player):
        return gameObj.getID() == 133

    def start_quest(self):
        self.sendNpcChat("What am i to do?")
        
        option = self.pickOption(self.introduction_question_responses)
        if option == 0:
            self.cook_request_help()
        elif option == 1:
            self.sendNpcChat("Haha very funny")
            self.release()
        elif option == 2:
            self.sendNpcChat("No i'm not")
            self.cook_request_help()
        elif option == 3:
            self.sendNpcChat("Err thank you -it's a pretty ordinary cooks hat really")
            self.release()

    def cook_request_help(self):
        self.sendNpcChat("Ooh dear i'm in a terrible mess"
            , "it's the duke's bithday today", "i'm meant to be making him a big cake for this evening",
            "unfortunately, i've forgotten to buy some of the ingredients", "i'll never get them in time now",
            "i don't suppose you could help me?")
        option = self.pickOption(self.cook_request_help_responses)
        if option == 0:
            self.sendNpcChat("oh thank you, thank you", "i need milk, eggs, and flour", "i'd be very grateful if you could get them to me")
            self.setQuestStage(1)
        elif option == 1:
            self.sendNpcChat("ok, suit yourself")
        self.release()

    def request_ingredients(self):
        self.sendNpcChat("how are you getting on with finding those ingredients?")
        milk = self.hasItem(22)
        eggs = self.hasItem(19)
        flour = self.hasItem(136)

        if not milk and not eggs and not flour:
            self.sendPlayerChat("i'm afraid i don't have any yet!")
            self.sendNpcChat("oh dear oh dear!", "i need flour, eggs, and milk", "without them i am doomed!")
        else:
            if milk and eggs and flour:
                self.sendPlayerChat("i now have everything you need for your cake", "milk, flour, and an egg!")
                self.sendNpcChat("i am saved thankyou!")
                self.setQuestStage(-1)
                self.setQuestCompleted()
            else:
                self.sendPlayerChat("i have found some of the things you asked for:")
                if milk:
                    self.sendPlayerChat("i have some milk")
                if flour:
                    self.sendPlayerChat("i have some flour")
                if eggs:
                    self.sendPlayerChat("i have an egg")
                self.sendNpcChat("great, but can you get the other ingredients as well?", "you still need to find")
                if not milk:
                    self.sendNpcChat("some milk")
                if not flour:
                    self.sendNpcChat("some flour")
                if not eggs:
                    self.sendNpcChat("an egg")
                self.sendPlayerChat("ok i'll try to find that for you")
        self.release()
    
    def blockTalkToNpc(self, player, npc):
        return npc.getID() == 7
