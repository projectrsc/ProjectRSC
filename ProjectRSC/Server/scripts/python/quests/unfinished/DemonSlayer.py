from com.prsc.gs.plugins import Quest
from com.prsc.gs.plugins.listeners.action import TalkToNpcListener
from com.prsc.gs.plugins.listeners.executive import TalkToNpcExecutiveListener, InvUseOnObjectExecutiveListener

class DemonSlayer(Quest, TalkToNpcListener, TalkToNpcExecutiveListener, InvUseOnObjectExecutiveListener):
    
    # NPCs used
    GYPSY = 14
    
    def getQuestId(self):
        return 2
    
    def getQuestName(self):
        return "Demon Slayer"
    
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
        script = player.getScriptHelper()
        script.setActiveNpc(npc)
        script.setActiveQuest(self)
        stage = script.getQuestStage()
        script.occupy()
        npc_id = npc.getID()
        
        if npc_id == self.GYPSY:
            if stage == 0:
                script.sendNpcChat("Hello, young one", "cross my palm with silver and the future will be revealed to you")
                option = script.pickOption(["Ok, here you go", "Who are you calling young one!?", "No I don't believe in that stuff"])
                if option == 0:
                    script.sendNpcChat("")
                elif option == 1:
                    script.sendNpcChat("You have been on this world", "A relatively short time", "at least compared to me")
                    script.sendNpcChat("so do you want you fortune told or not/")
                    sub_option = script.pickOption(["Yes please", "No I don't believe in that stuff", "ooh how old are you then?"])
                    if sub_option == 0:
                        script.sendNpcChat("")
                    elif sub_option == 1:
                        script.sendNpcChat("Ok suit yourself")
                    elif sub_option == 2:
                        script.sendNpcChat("Older than you imagine")
                        more_option = script.pickOption(["Believe me, I have a good imagination", "How do you know how old I think you are?",
                                                         "Oh pretty old then"])
                        if more_option == 0:
                            script.sendNpcChat("You seem like just the sort of person", "who would want their fortune told")
                            extra_option = script.pickOption([""])
                        if more_option == 1:
                            script.sendNpcChat("I have the power to know", "just as I have the power to foresee the future")
                            extra_option = script.pickOption(["ok what am I thinking now?", "ok but how old are you?", "Go on then, what's my future?"])
                            if extra_option == 0:
                                script.sendNpcChat("You are thinking that I'll never guess what you are thinking")
                            elif extra_option == 1:
                                script.sendNpcChat("Count the number of legs of the chairs in the blue moon inn", 
                                                   "and multiply that number by seven")
                                script.sendPlayerChat("Errr yeah whatever")
                            elif extra_option == 2:
                                script.sendNpcChat("")
                        if more_option == 2:
                            script.sendNpcChat("Yes i'm old", "don't rub it in")
                elif option == 2:
                    script.sendNpcChat("Ok suit yourself")
        script.release()
    
    def blockTalkToNpc(self, player, npc):
        return npc.getID() == self.GYPSY
