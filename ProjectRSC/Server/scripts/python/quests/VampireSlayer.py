'''
Created on Nov 15, 2012

Quest: Vampire Slayer

@author: GORF
@difficulty: Medium
'''
from org.darkquest.gs.plugins import Quest
from org.darkquest.gs.plugins.listeners.executive import TalkToNpcExecutiveListener, ObjectActionExecutiveListener, PlayerAttackNpcExecutiveListener, PlayerKilledNpcExecutiveListener
from org.darkquest.gs.plugins.listeners.action import TalkToNpcListener, ObjectActionListener, PlayerAttackNpcListener, PlayerKilledNpcListener
from org.darkquest.gs.event.impl import FightEvent

class VampireSlayer(Quest, TalkToNpcListener, ObjectActionListener, PlayerAttackNpcListener, PlayerKilledNpcListener, 
                    TalkToNpcExecutiveListener, ObjectActionExecutiveListener, PlayerAttackNpcExecutiveListener, PlayerKilledNpcExecutiveListener):
    
    # Quest NPCs
    MORGAN = 97
    HARLOW = 98
    COUNT_DRAYNOR = 96
    
    # ITEMS USED
    BEER = 193
    STAKE = 217
    HAMMER = 168
    GARLIC = 218
    
    # GAME OBJECTS USED
    DRAYNOR_MANSION_COFFIN = 136
    
    def getQuestId(self):
        return 6
    
    def getQuestName(self):
        return "Vampire slayer"
    
    def isMembers(self):
        return False
    
    def handleReward(self, player):
        script = player.getScriptHelper()
        script.setActiveQuest(self)
        
        script.advanceStat(player.SkillType.ATTACK, 4825)
        script.displayMessage("@gre@You have gained 3 quest points!")
        script.addQuestPoints(3)
        script.sleep(1000)
        script.displayMessage("Well done, you have completed vampire slayer quest")
            
    def onTalkToNpc(self, player, npc):
        script = player.getScriptHelper()
        
        script.setActiveNpc(npc)
        script.setActiveQuest(self)
        stage = script.getQuestStage()
        script.occupy()
        
        # START CODE HERE
        if npc.getID() == self.MORGAN:
            if stage == -1: 
                script.sendNpcChat("How are you doing with your quest?")
                script.sendPlayerChat("I have slain the foul creature")  
                script.sendNpcChat("Thank you, thank you", "You will always be a hero in our village")
            elif stage == 0:
                script.sendNpcChat("Please, please help us bold hero!")
                script.sendPlayerChat("What's the problem?")
                script.sendNpcChat("Our little village has been dreadfully ravaged by an evil vampire!", "There's hardly any of us left", 
                                   "We need someone to get rid of him once and for good")
                option = script.pickOption(["No. Vampires are scary", "Ok I'm up for an adventure", "I tried fighting him. He wouldn't die"])
                if option == 0:
                    script.sendNpcChat("I don't blame you")
                elif option == 1:
                    script.sendNpcChat("I think first you should seek help",
                    "I have a friend who is a retired vampire hunter called Dr Harlow",
                    "He may be able to give you some tips",
                    "He's usually found in the Jolly Bar Inn these days", 
                    "He's a bit of an old soak", "Mention his old friend Morgan",
                    "I'm sure he wouldn't want me to be killed by a vampire")
                    script.sendPlayerChat("I'll look him up then")
                    script.setQuestStage(1)
                elif option == 2:
                    script.sendNpcChat("You need special tools when dealing with a vampire!",
                    "I think first you should seek help",
                    "I have a friend who is a retired vampire hunter called Dr Harlow",
                    "He may be able to give you some tips",
                    "He's usually found in the Jolly Bar Inn these days", 
                    "He's a bit of an old soak", "Mention his old friend Morgan",
                    "I'm sure he wouldn't want me to be killed by a vampire")
                    script.sendPlayerChat("I'll look him up then")
                    script.setQuestStage(1)
            elif stage == 1 or stage == 2 or stage == 3:
                script.sendNpcChat("How are you doing with your quest?")
                script.sendPlayerChat("I'm working on it still")
                script.sendNpcChat("Please hurry", "Every day we live in fear of our lives",
                            "Afraid that we will be the vampire's next victim")
        elif npc.getID() == self.HARLOW:
            script.sendNpcChat("Buy me a drrink pleassh")
            if stage == 1:
                option = script.pickOption(["No, you've had enough", "Ok mate", "Morgan needs your help"])
                if option == 0:
                    script.sendNpcChat("Sheys you matey!")
                elif option == 1:
                    self.playHowToKillDialog(script)
                elif option == 2:
                    script.sendNpcChat("Morgan you sshay?")
                    script.sendPlayerChat("His village is being terrorized by a vampire",
                                          "He wanted me to ask you how I should go about stopping it")
                    script.sendNpcChat("Buy me a beer then I'll teash you what you need to know")
                    option = script.pickOption(["Ok mate", "But this is your friend Morgan we're talking about"])
                    if option == 0:
                        self.playHowToKillDialog(script)
                    elif option == 1:
                        script.sendNpcChat("Buy ush a drink anyway")
            elif stage == 2 or stage == 3:
                option = script.pickOption(["No, you've had enough", "Ok mate"])
                if option == 0:
                    script.sendNpcChat("Sheys you matey!")
                elif option == 1:
                    self.playHowToKillDialog(script)
            else:
                option = script.pickOption(["No, you've had enough", "Ok mate"])
                if option == 0:
                    script.sendNpcChat("Sheys you matey!")
                elif option == 1:
                    if script.hasItem(self.BEER): # WE HAVE A BEER
                        script.displayMessage("You give a beer to Dr Harlow")
                        script.sleep(500)
                        script.removeItem(self.BEER, 1)
                        script.sendNpcChat("Cheersh Matey")
                    else:
                        script.sendPlayerChat("I'll just go and buy one")
        # END CODE HERE
        script.release()
    
    def onObjectAction(self, gameObject, command, player):
        script = player.getScriptHelper()
        
        script.setActiveQuest(self)
        stage = script.getQuestStage()
        script.occupy()

        script.displayMessage("you search the coffin")
        script.sleep(500)
        script.displayMessage("A vampire jumps out of the coffin")
        script.spawnNpc(self.COUNT_DRAYNOR, 205, 3382, 300000, False) # SPAWN FOR 5 MINS
        
        script.release()
    
    def onPlayerAttackNpc(self, player, npc):
        script = player.getScriptHelper()
        
        script.setActiveQuest(self)
        stage = script.getQuestStage()
        
        if script.hasItem(self.GARLIC):
            npc.weakenAttack(1)
            npc.weakenDefense(2)
            npc.weakenStrength(1)
            script.displayMessage("The vampire appears to weaken")
                
        fightEvent = script.fightNpc(npc, True) # Grab FightEvent handle
        
        while player.isBusy():
            if script.isWielding(self.STAKE) and script.hasItem(self.HAMMER) and npc.getHits() <= 10:
                fightEvent.setOpponentInvincible(False)
                script.removeItem(self.STAKE, 1)
                script.displayMessage("You hammer the stake into the vampire chest")
                script.setQuestStage(3)
                break
            elif script.isWielding(self.STAKE) and not script.hasItem(self.HAMMER) and npc.getHits() > 10:
                script.displayMessage("You are unable to push the stake far in enough, the stake breaks!")
                script.removeItem(self.STAKE, 1)
                break
            elif npc.getHits() <= 0:
                script.displayMessage("The vampire seems to regenerate")
    
    def onPlayerKilledNpc(self, player, npc):
        script = player.getScriptHelper()
        
        script.setActiveQuest(self)
        stage = script.getQuestStage()
        script.occupy()
        
        script.setQuestStage(-1)
        script.setQuestCompleted()
        
        script.release() 
    
    def playHowToKillDialog(self, script):
        if script.hasItem(self.BEER): # WE HAVE A BEER
            script.displayMessage("You give a beer to Dr Harlow")
            script.sleep(500)
            script.removeItem(self.BEER, 1)
            script.sendNpcChat("Cheersh Matey")
            script.sendPlayerChat("So tell me how to kill vampires then")
            script.sendNpcChat("Yesh yesh vampires, I was very good at killing em once")
            script.displayMessage("Dr Harlow appears to sober up slightly")
            script.sleep(500)
            script.sendNpcChat("Well you're gonna have to kill it with a stake",
                               "Otherwise he'll just regenerate", 
                               "Yes your killing blow must be done with a stake",
                               "I jusht happen to have one on me")
            script.displayMessage("He hands you a stake")
            script.sleep(500)
            script.addItem(self.STAKE, 1)
            script.sendNpcChat("You'll need a hammer in hand to drive it in properly as well",
                               "One last thing", "It's wise to carry garlic with you",
                               "Vampires are weakened somewhat if they smell garlic",
                               "Dunno where you'd find that though",
                               "Remember even then a vampire is a dangeroush foe")
            script.sendPlayerChat("Thank you very much")
            script.setQuestStage(2)
        else:
            script.sendPlayerChat("I'll just go and buy one")
        script.release()
    
    def blockTalkToNpc(self, player, npc):
        return npc.getID() == self.MORGAN or npc.getID() == self.HARLOW 
    
    def blockPlayerAttackNpc(self, player, npc):
        script = player.getScriptHelper()
        script.setActiveQuest(self)
        stage = script.getQuestStage()
        return npc.getID() == self.COUNT_DRAYNOR and stage == 2 or npc.getID() == self.COUNT_DRAYNOR and stage == 3
    
    def blockPlayerKilledNpc(self, player, npc):
        script = player.getScriptHelper()
        script.setActiveQuest(self)
        stage = script.getQuestStage()
        return npc.getID() == self.COUNT_DRAYNOR and stage == 3
    
    def blockObjectAction(self, gameObject, command, player): # WE MUST OVERRIDE THE BLOCKING FOR RANDOM OJBS
        script = player.getScriptHelper()
        script.setActiveQuest(self)
        stage = script.getQuestStage()
        return command == "search" and gameObject.getID() == self.DRAYNOR_MANSION_COFFIN and stage >= 0
        