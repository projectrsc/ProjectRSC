'''
Created on Oct 23, 2012

Example Python based Quest

@author: openfrog
'''
from org.darkquest.gs.plugins import Quest;
from org.darkquest.gs.plugins.listeners.action import TalkToNpcListener, PlayerKilledNpcListener, ObjectActionListener, InvUseOnObjectListener
from org.darkquest.gs.plugins.listeners.executive import TalkToNpcExecutiveListener

from org.darkquest.gs.model import Player, Npc, GameObject, InvItem

class TheMissingPumpkins(Quest, TalkToNpcListener, PlayerKilledNpcListener, ObjectActionListener, InvUseOnObjectListener, TalkToNpcExecutiveListener):
    '''
    classdocs
    '''
    MASKS = [828, 831, 832]
    PUMPKIN = 422
    
    GRIM = 796 # should be 796
    DEMON = 22 #22
    FATHER = 32 # father lawrence
    CAMEL = 13
    NED = 165 #124
    GHOST = 15 
    HETTY = 148#148 
    KING = 42
    GUNTHOR = 78
    WHITE_KNIGHT = 102
    BETTY = 149#149
    
    def getQuestId(self):
        return 55
    
    def getQuestName(self):
        return "The Missing Pumpkins"
    
    def isMembers(self):
        return False
    
    def handleReward(self):
        self.displayMessage("You see a mask appear out of nowhere")
        self.sleep(100)
        chosenMask = self.getRandom(0, 2)
        maskId = self.MASKS[chosenMask]
        self.spawnItem(self.getParticipant().getX(), self.getParticipant().getY(), maskId, 1) # 
        
    def onOptionChosen(self, option): 
        stage = self.getQuestStage()
        self.release()
        
        if stage == 3:
            if option == 0:
                self.sendChat(self.getActiveNpc(), self.getParticipant(), "Check the altars again")
                self.setQuestStage(2)
            if option == 1:
                self.sendChat(self.getActiveNpc(), self.getParticipant(), "I'm not certain, but I think I heard of a camel In al-Kharid", "who balances one on his back..")
                self.sendChat(self.getParticipant(), self.getActiveNpc(), " O-Kay...")
                self.setQuestStage(4)
                
        if stage == 5:
            if option == 0:
                if self.hasItem(50) or self.hasItem(141) or self.hasItem(342): # needs to empy water
                    self.sendChat(self.getActiveNpc(), self.getParticipant(), "Om Nom Nom")
                    self.removeAllItem(50)
                    self.removeAllItem(141)
                    self.removeAllItem(342)
                    self.displayMessage("While the camel is drinking the water the pumpkin rolls off his back and you pick it up.", "You get a pumpkin")
                    self.addItem(self.PUMPKIN, 1)
                    self.displayMessage("A tingling sensation in your balls tell you to go to karamja")
                    self.setQuestStage(6)
                else:
                    self.sendChat(self.getActiveNpc(), self.getParticipant(), "Hmph")
                    self.displayMessage("You do not have any water...", "She seems annoyed")
                    
        if stage == 7:
            if option == 0:
                if self.hasItem(350, 5) and self.hasItem(249, 5):
                    self.sendChat(self.getActiveNpc(), self.getParticipant(), "No take backsies!")
                    self.displayMessage("Zambo snatches your food and gives you the pumpkin!")
                    self.sleep(500)
                    self.removeAllItem(350)
                    self.removeAllItem(249)
                    self.sleep(100)
                    self.addItem(self.PUMPKIN, 1)
                    self.setQuestStage(8)
                else:
                    self.sendChat(self.getActiveNpc(), self.getParticipant(), "No you're missing something...")
    
    def onTalkToNpc(self, player, npc): 
        self.setParticipants(player, npc) # must always set as first call
        stage = self.getQuestStage()
        self.occupy()
        
        if npc.getID() == self.GRIM: # 793 should be grim reaper (796) NEEDING TO DO BLOCKING
            if stage == 0 or stage == -1: 
                self.sendPlayerChat("Hi, come here often?")
                self.sendNpcChat("Just around these times.")
                self.sendPlayerChat("What times?")
                self.sendNpcChat("Halloween, duh.")
                self.sendPlayerChat("Nice mask, where did you get it from?")
                self.sendNpcChat("I've always liked halloween but it's all about the treat", "and not enough about the trick", "So, ive been stealking trick-or-treaters masks to show", "them what it's really about.")
                self.sendNpcChat("Took them years to figure it out, I have tons of masks, but.. ", "Apparently they got it, cause they stole my pumpkins", "and spread them all over the map.", "Thing is, I eat pumpkins to live.")
                self.sendPlayerChat("You don't eat souls?")
                self.sendNpcChat("Not anymore, I'm vegan now! ")
                self.sendPlayerChat("I'd rather be dead...")
                self.sendNpcChat("If you get my pumpkins back you can have a mask!")
                self.sendPlayerChat("Sounds like a fair trade.. where can I find them?")
                self.sendNpcChat("They stole 7 Pumpkins, I saw one of those kids heading toward edgeville.")
                self.sendPlayerChat("Gotcha! I'll check there!")
                self.setQuestStage(1)
            if stage == 21 and self.hasItem(self.PUMPKIN, 7): # 7
                self.sendPlayerChat("I have your pumpkins!")
                self.sendNpcChat("My what?")
                self.sendPlayerChat("Are you serious?! I thought you needed them to live!")
                self.displayMessage("Grim quickly hides the steak he was eating.")
                self.sendNpcChat("Oh yeah.. sure kid, sure.")
                self.sendPlayerChat("GIVE ME MY MASk!")
                self.sendNpcChat("Woah, chill out buddy, here you go")
                self.sleep(200)
                self.displayMessage("You notice Grim starts chanting what seems to be latin")
                self.sendNpcChat("O magna custos audire verba mea", "Offero hoc humanum, prout est Sacrificium in honorem vobis")
                self.displayMessage("You start to feel a strong force surround you")
                self.sendNpcChat("Magnus Luthorion, audi verba mea", "Creator restituere potero delere")
                count = 0
                while count < 43:
                    self.summoningRitual(1, 1)
                    self.summoningRitual(2, 1)
                    count = count + 1
                oldNpc = self.getActiveNpc()
                talkingNpc = self.spawnNpc(self.DEMON, player.getX() + 2, player.getY() + 1, 180000, False) # demon (22)
                self.setActiveNpc(talkingNpc)
                self.sendNpcChat("You..are mine")
                self.sendPlayerChat("What the...?")
                self.release()
                self.attackPlayer(talkingNpc)
                self.setActiveNpc(oldNpc)
                self.setQuestStage(22) #delay
                
        if npc.getID() == self.FATHER: # should be father lawrence in edgeville
            if stage == 1: 
                self.sendPlayerChat("So, I hear your kid may have stolen a pumpkin!")
                self.sendNpcChat("I don't know anything about that", "though I do sometimes catch Timmy playing in the wilderness.")
                self.sendPlayerChat("So hypothetically, if he had stolen a pumpkin..")
                self.sendNpcChat("It would be at one of the altars ahead")
                self.sendPlayerChat("Great..")
                self.setQuestStage(2)
            if stage == 3:
                self.pickOption(["I seem to have lost the pumpkin.. any ideas?", "Any idea where I could find another pumpkin?"], self.onOptionChosen)
        
        if npc.getID() == self.CAMEL: # camel
            if stage == 4:
                self.sendPlayerChat("Hello Camel")
                self.sendNpcChat("Water...")
                self.displayMessage("She seems quite thirsty...")
                self.setQuestStage(5)
            if stage == 5:
                self.pickOption(["I've brought you some water...", "I forgot."], self.onOptionChosen)
                
        if npc.getID() == self.NED: # NED 124 instead of ZAMBO
            if stage == 6:
                self.sendPlayerChat("What up my brother from another mother?!")
                self.sendNpcChat("???")
                self.sendPlayerChat("Is that a pumpkin? Can I have it?")
                self.sendNpcChat("Sure you can.. if you give me 5 banannas, and 5 shrimp! Haha!")
                self.sendPlayerChat("Alrighty!")
                self.sleep(500)
                self.displayMessage("As you walk away you hear zambo chucking about how foolish you are.")
                self.sendNpcChat("Hahaha, one pumpkin for all that food?! Hahaha!")
                self.setQuestStage(7)
            if stage == 7:
                self.pickOption(["I have your food!", "I don't even know why I'm talking to you."], self.onOptionChosen)
            if stage == 8:
                self.sendPlayerChat("Know anyone else with a pumpkin?")
                self.sendNpcChat("My etherial friend in lumbridge was complaining about something pumpkin related..")
                self.sendPlayerChat("Lumbridge? Really??")
                self.setQuestStage(9)
        
        if npc.getID() == self.GHOST: # lumby ghost
            if stage == 9:
                self.sendPlayerChat("Wooooo Woo Woooooooooo")
                self.sendNpcChat("Yes, I do have a pumpkin problem!")
                self.sendPlayerChat("Wooo Woooooooooo ooooo boooo")
                self.sendNpcChat("Someone took my head and replaced it with a pumpkin!")
                self.sendPlayerChat("ooo?")
                self.sendNpcChat("Yes, if you get my skull back you can take that wretched pumpkin with you!")
                self.sendPlayerChat("Word.")
                self.setQuestStage(10)
            if stage == 10:
                self.sendPlayerChat("I got your head back!")
                if self.hasItem(27):
                    self.sendNpcChat("Thank you! Get that stupid pumpkin out of my sight!")
                    self.removeItem(27, 1)
                    self.sleep(500)
                    self.displayMessage("The ghost gives you the pumpkin.")
                    self.sleep(100)
                    self.addItem(self.PUMPKIN, 1)
                    self.sendNpcChat("If you're looking for another one I heard the rimmington witch", "Bragging about making a potion with a pumpkin!")
                    self.setQuestStage(11)
                else:
                    self.sendNpcChat("Don't try to pull one over on me! I can still see without a head!")
                    self.sendPlayerChat("Ooo ooo...")
        #
        #if npc.getID() == self.HETTY: # hetty the witch
        #    if stage == 11:
        #        self.sendPlayerChat("HAND IT OVER!")
        #        self.sendNpcChat("Sorry, I'm all out of hand soup!")
        #        self.sendPlayerChat("No no, the pumpkin!")
        #        self.sendNpcChat("But I'm making pumpkin soup!", "I would be making Newt soup but Betty wont sell me any!")
        #        self.sendPlayerChat("Okay okay, how many do you need.. *sigh*")
        #        self.sendNpcChat("5 please!")
        #        self.setQuestStage(12)
       #     if stage == 12:
        #        if self.hasItem(270, 5):
        #            self.sendNpcChat("MuHuHaHaHa...")
        #            self.removeAllItem(270)
        #            self.sleep(500)
        #            self.displayMessage("You snatch the pumpkin up while she is laughing. ")
        #            self.sleep(100)
        #            self.addItem(self.PUMPKIN, 1)
        #            self.sendPlayerChat("Any hints on who else might have a pumpkin?")
        #            self.sendNpcChat("Try the old Varrock King..")
        #            self.sendPlayerChat("Of course..")
         #           self.setQuestStage(13)
        #        else:
         #           self.displayMessage("You might be missing something...")
            #
        
        if npc.getID() == self.KING: # varrock king
            if stage == 13:
                self.sendPlayerChat("Okay.. how does this go? ", "You have a pumpkin or something.. I need it.. blah blah blah")
                self.sendNpcChat("Rude.")
                self.sendPlayerChat("Please, can I just have it?")
                self.sendNpcChat("Sure, I just need you to give this beer to Gunthor.")
                self.displayMessage("The king hands you a specially crafted beer")
                self.sleep(200)
                self.addItem(193, 1)
                self.sendPlayerChat("Fine.")
                self.setQuestStage(14)
            if stage == 15:
                self.sendNpcChat("Thank you for being a good delivery boy. Now off with you.")
                self.sendPlayerChat("Woah hey, what about my pumpkin??")
                self.sendNpcChat("Oh fine.. Peasants.")
                self.displayMessage("The King tosses a pumpkin to you, which you almost drop!")
                self.sleep(500)
                self.addItem(self.PUMPKIN, 1)
                self.sleep(100)
                self.sendPlayerChat("Where can I find another?")
                self.sendNpcChat("Go talk to a white knight")
                self.setQuestStage(16)
        
        if npc.getID() == self.GUNTHOR: # gunthor
            if stage == 14:
                if self.hasItem(193):
                    self.displayMessage("You hand Gunthor a beer")
                    self.removeAllItem(193)
                    self.sleep(500)
                    self.sendNpcChat("Cheers!")
                    self.setQuestStage(15)
                else:
                    self.displayMessage("He looks like he's expecting something...")
        
        if npc.getID() == self.WHITE_KNIGHT: # white knight
            if stage == 16:
                self.sendNpcChat("You! Squire! I have a quest for you!")
                self.sendPlayerChat("Does it's reward resemble that pumpkin you've got there?")
                self.sendNpcChat("How astute! I rquire 10 tin ore. Don't ask what for, it's a secret!")
                self.sendPlayerChat("Be right back..")
                self.setQuestStage(17)
            if stage == 17:
                if self.hasItem(202, 10):
                    self.sendPlayerChat("I have the ores you asked for.")
                    self.sendNpcChat("And the pumpkin is yours! Hurrah!")
                    self.removeAllItem(202)
                    self.displayMessage("The knight gives you the pumpkin and then sneaks off in the Black Knights Fortress.. ")
                    self.sleep(500)
                    self.addItem(self.PUMPKIN, 1)
                    self.sleep(100)
                    self.displayMessage('As he leaves, you see a flyer advertising for', '"Adventurers services, REWARD, See Betty In Sarim"', 'Better go check it out..')
                    self.sleep(100)
                    self.setQuestStage(18)
                else:
                    self.sendNpcChat("Better check again laddy..")
        
        if npc.getID() == self.BETTY: # betty the witch
            if stage == 18:
                self.sendPlayerChat("Hey little lady, may I offer you my 'services'?")
                self.sendNpcChat("*giggles* You may!")
                self.sendPlayerChat("What did you have in mind?")
                self.sendNpcChat("Well, you see... Ineedsomeonetogosabotagehettyscauldron.")
                self.sendPlayerChat("Excuse me?")
                self.sendNpcChat("One of each element should do the trick! Chop Chop!")
                self.setQuestStage(19)
            if stage == 20:
                self.sendPlayerChat("Not what I had in mind, but the job is done!")
                self.sendNpcChat("Enjoy your delicious pumpkin!")
                self.sleep(300)
                self.addItem(self.PUMPKIN, 1)
                self.sendPlayerChat("It's not for eating!")
                self.sendNpcChat("Uh Huh.. Suree...")
                self.displayMessage("I think I better return to Grim now that I have all his pumpkins!")
                self.setQuestStage(21)

        self.release()
    
    def blockTalkToNpc(self, player, npc):
        self.setParticipant(player)
        
        if npc.getID() == self.GRIM or npc.getID() == self.FATHER or npc.getID() == self.CAMEL or npc.getID() == self.NED or npc.getID() == self.GHOST or npc.getID() == self.KING or npc.getID() == self.GUNTHOR or npc.getID() == self.WHITE_KNIGHT: # add more
            return True
        elif npc.getID() == self.HETTY or npc.getID() == self.BETTY and self.getQuestStage() >= 0:
            return True
    
    def onPlayerKilledNpc(self, player, npc):
        stage = self.getQuestStage()
        
        if stage == 22 and npc.getID() == self.DEMON and player.getUsernameHash() == self.getParticipant().getUsernameHash():
            self.sendNpcChat("...", "..this isn't over")
            self.setQuestStage(-1)
            self.setQuestCompleted()
                      
    def onObjectAction(self, gameObj, command, player):
        self.setParticipant(player) # must always set as first call
        stage = self.getQuestStage()
        
        if stage == 2 and gameObj.getID() == 19: # altar 
            self.sleep(100)
            self.addItem(self.PUMPKIN, 1)
            self.setQuestStage(3) 
            
    def onInvUseOnObject(self, gameObj, invItem, player):
        self.setParticipant(player) # must always set as first call
        stage = self.getQuestStage()
        
        if stage == 19 and gameObj.getID() == 147: #fix
            if invItem.getID() == 31 or invItem.getID() == 32 or invItem.getID() == 33 or invItem.getID() == 34:
                self.displayMessage("As you drop the rune into the cauldron, you see change color...")
                if self.hasItem(31) and self.hasItem(32) and self.hasItem(33) and self.hasItem(34):
                    self.displayMessage("As you sneak away you hear Betty raging")
                    self.removeItem(31, 1)
                    self.removeItem(32, 1) 
                    self.removeItem(33, 1)
                    self.removeItem(34, 1)
                    self.setQuestStage(20)
                else:
                    self.displayMessage("The cauldron looks like its missing something...")
               
