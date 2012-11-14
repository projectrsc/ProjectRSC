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
        player.setActiveQuest(self)
        
        player.displayMessage("You give some milk, an egg, and some flour to the cook")
        player.sleep(500)
        player.removeItem(22, 1)
        player.removeItem(19, 1)
        player.removeItem(136, 1)
        
        player.displayMessage("Well done. You have completed the cook's assistant quest")
        player.displayMessage("@gre@You just advanced 1 quest point!")
        
        player.addQuestPoints(1)
        player.advanceStat(player.SkillType.COOKING, 270)
    
    def onTalkToNpc(self, player, npc):        
        if npc.getID() == 7:
            player.setActiveNpc(npc)
            player.setActiveQuest(self)
            stage = player.getQuestStage()
            player.occupy()
            if stage == 0:
                self.start_quest(player)
            elif stage == 1:
                self.request_ingredients(player)
    
    def blockInvUseOnObject(self, gameObj, invItem, player):
        return gameObj.getID() == 133

    def start_quest(self, player):
        player.sendNpcChat("What am i to do?")
        
        option = player.pickOption(self.introduction_question_responses)
        if option == 0:
            self.cook_request_help(player)
        elif option == 1:
            player.sendNpcChat("Haha very funny")
            player.release()
        elif option == 2:
            player.sendNpcChat("No i'm not")
            self.cook_request_help(player)
        elif option == 3:
            player.sendNpcChat("Err thank you -it's a pretty ordinary cooks hat really")
            player.release()

    def cook_request_help(self, player):
        player.sendNpcChat("Ooh dear i'm in a terrible mess"
            , "it's the duke's bithday today", "i'm meant to be making him a big cake for this evening",
            "unfortunately, i've forgotten to buy some of the ingredients", "i'll never get them in time now",
            "i don't suppose you could help me?")
        option = player.pickOption(self.cook_request_help_responses)
        if option == 0:
            player.sendNpcChat("oh thank you, thank you", "i need milk, eggs, and flour", "i'd be very grateful if you could get them to me")
            player.setQuestStage(1)
        elif option == 1:
            player.sendNpcChat("ok, suit yourself")
        player.release()

    def request_ingredients(self, player):
        player.sendNpcChat("how are you getting on with finding those ingredients?")
        milk = player.hasItem(22)
        eggs = player.hasItem(19)
        flour = player.hasItem(136)

        if not milk and not eggs and not flour:
            player.sendPlayerChat("i'm afraid i don't have any yet!")
            player.sendNpcChat("oh dear oh dear!", "i need flour, eggs, and milk", "without them i am doomed!")
        else:
            if milk and eggs and flour:
                player.sendPlayerChat("i now have everything you need for your cake", "milk, flour, and an egg!")
                player.sendNpcChat("i am saved thankyou!")
                player.setQuestStage(-1)
                player.setQuestCompleted()
            else:
                player.sendPlayerChat("i have found some of the things you asked for:")
                if milk:
                    player.sendPlayerChat("i have some milk")
                if flour:
                    player.sendPlayerChat("i have some flour")
                if eggs:
                    player.sendPlayerChat("i have an egg")
                player.sendNpcChat("great, but can you get the other ingredients as well?", "you still need to find")
                if not milk:
                    player.sendNpcChat("some milk")
                if not flour:
                    player.sendNpcChat("some flour")
                if not eggs:
                    player.sendNpcChat("an egg")
                player.sendPlayerChat("ok i'll try to find that for you")
        player.release()
    
    def blockTalkToNpc(self, player, npc):
        player.setActiveQuest(self)
        return npc.getID() == 7 and player.getQuestStage() >= 0
