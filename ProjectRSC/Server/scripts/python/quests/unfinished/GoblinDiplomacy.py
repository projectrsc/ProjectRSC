from com.prsc.gs.plugins.listeners.action import InvUseOnItemListener, TalkToNpcListener
from com.prsc.gs.plugins.listeners.executive import InvUseOnItemExecutiveListener, TalkToNpcExecutiveListener
from com.prsc.gs.plugins import Quest

'''
Created on Nov 25, 2012

@authour Mister Hat

Goblin Diplomacy F2P quest
'''

class GoblinDiplomacy(Quest, TalkToNpcListener, TalkToNpcExecutiveListener):
    # The two goblin generals
    WARTFACE = 151
    BENTNOZE = 152
    
    # The bartender in port sarim
    BARTENDER = 150
    
    # The armours used
    NORMAL_ARMOUR = 273
    ORANGE_ARMOUR = 274
    BLUE_ARMOUR = 275
    
    def getQuestId(self):
        return 5
        
    def getQuestName(self):
        return "Goblin diplomacy"
    
    def isMembers(self):
        return False
        
    def handleReward(self, player):
        pass
    
    def onTalkToNpc(self, player, npc):
        script = player.getScriptHelper()
        script.setActiveQuest(self)
        stage = script.getQuestStage()
        
        if npc.getID() == self.BARTENDER:
            script.setActiveNpc(npc)
            script.occupy()
            
            if stage == 0:
                option = script.pickOption(["Could i buy a beer please?", "Not very busy in here today is it?"], False)
            
                if option == 0:
                    self.buyBeer(script)
                elif option == 1:
                    script.sendPlayerChat("Not very busy in here today is it")
                    script.sendNpcChat(
                        "No it was earlier",
                        "There was a guy in here saying the goblins up by the mountains are arguing again",
                        "Of all things the colour of their armour",
                        "Knowing the goblins, it could easily turn into a full blown war",
                        "Which wouldn't be good",
                        "Goblin wars make such a mess of the countryside"
                    )
                    script.sendPlayerChat("Well if I have time I'll see if I can go and knock some sense into them")
                    script.setQuestStage(1)
            else:
                option = script.pickOption(["Could i buy a beer please?", "Have you heard of any more rumours in here?"], False)
                
                if option == 0:
                    self.buyBeer(script)
                elif option == 1:
                    script.sendPlayerChat("Have you heard any more rumours in here?")
                    script.sendNpcChat("No it hasn't been very busy lately")
                    
            script.release()
        
        elif npc.getID() == self.WARTFACE or npc.getID() == self.BENTNOZE:
            wartface = script.getNpc(self.WARTFACE)
            bentnoze = script.getNpc(self.BENTNOZE)
            
            if stage == 0:
                self.goblinIntro(script, player, wartface, bentnoze)
            elif stage == 1:
                self.goblinIntro(script, player, wartface, bentnoze)
                option = script.pickOption(["Why are you arguing about the colour of your armour?", "Wouldn't you prefer peace?", "Do you want me to pick an armour colour for you?"], False)

                if option == 0:                    
                    script.sendPlayerChat("Why are you arguing about the colour of your armour?")
                    script.sendNpcChat(
                        "We decide to celebrate goblin new century",
                        "By changing the colour of our armour", 
                        "Light blue got boring after a bit",
                        "And we want change", "Problem is they want difference change to us"
                    )
                elif option == 1:
                    script.sendPlayerChat("Wouldn't you prefer peace")
                    script.sendNpcChat("Yeah peace is good as long as it is peace wearing Green armour")
                    
                    self.switchGoblin(script, wartface, bentnoze)
                    
                    script.sendNpcChat("But green to much like skin!", "Nearly make you look naked!")
                elif option == 2:
                    script.sendPlayerChat("Do you want me to pick an armour colour for you?", "different to either green or red")
                    script.sendNpcChat("Hmm me dunno what that'd be like", "You'd have to bring me some, so us could decide")
                    
                    self.switchGoblin(script, wartface, bentnoze)
                    
                    script.sendNpcChat("Yep bring us orange armour")
                    
                    self.switchGoblin(script, wartface, bentnoze)
                    
                    script.sendNpcChat("Yep orange might be good")
                    
                    script.setQuestStage(2)
            elif stage == 2:
                self.goblinIntro(script, player, wartface, bentnoze)
                script.sendNpcChat("Oh it you")
                if script.hasItem(self.ORANGE_ARMOUR, 1):
                    script.sendPlayerChat("I have some orange armour")
                    script.displayMessage("You give some goblin armour the the goblins")
                    script.removeItem(self.ORANGE_ARMOUR, 1)
                    script.sendNpcChat("No I don't like that much")
                    
                    self.switchGoblin(script, wartface, bentnoze)
                    
                    script.sendNpcChat("It clashes with my skin colour")

                    self.switchGoblin(script, wartface, bentnoze)
                    
                    script.sendNpcChat("Try bringing us dark blue armour")
                    script.setQuestStage(3)
                else:
                    script.sendNpcChat("Have you got some orange goblin armour yet?")
                    script.sendPlayerChat("Err no")
                    script.sendNpcChat("Come back when you have some")
            elif stage == 3:
                self.goblinIntro(script, player, wartface, bentnoze)
                script.sendNpcChat("Oh it you")
                if script.hasItem(self.BLUE_ARMOUR, 1):
                    script.sendPlayerChat("I have some dark blue armour")
                    script.displayMessage("You give some goblin armour to the goblins")
                    script.removeItem(self.BLUE_ARMOUR, 1)
                    script.sendNpcChat("Doesn't seem quite right")
                    
                    self.switchGoblin(script, wartface, bentnoze)
                    
                    script.sendNpcChat("maybe if it was a bit lighter")
                    
                    self.switchGoblin(script, wartface, bentnoze)
                    
                    script.sendNpcChat("Yeah try light blue")
                    script.sendPlayerChat("I thought that was the amour you were changing from", "But never mind, anything is worth a try")
                    
                    script.setQuestStage(4)
                else:
                    script.sendNpcChat("Have you got some Dark Blue goblin armour yet?")
                    script.sendPlayerChat("Err no")
                    script.sendNpcChat("Come back when you have some")
            #elif stage == 4:
                #TESTICLES
                    
        wartface.unblock()
        bentnoze.unblock()
        player.setBusy(False)
            
    def buyBeer(self, script):
        '''
        The dialogue for buying a beer from the bartender is repeated twice
        '''

        script.sendPlayerChat("Could i buy a beer please?")
        script.sendNpcChat("Sure that will be 2 gold coins please")
    
        if script.hasItem(10, 2):
            script.sendPlayerChat("Ok here you go thanks")
            script.removeItem(10, 2)
            script.addItem(193, 1)
            script.displayMessage("you buy a pint of beer")
        else:
            script.displayMessage("You dont have enough couins for the beer")
    
    def goblinIntro(self, script, player, wartface, bentnoze):
        '''
        This dialogue occurs whenever you first talk to one of the goblin generals
        '''
        
        #TODO idk wtf is going on with npc facing
        
        player.setBusy(True)
        wartface.blockedBy(player)
        bentnoze.blockedBy(player)
        bentnoze.setBusy(False)
        
        script.setActiveNpc(wartface)
        script.faceNpc(wartface)
        script.sendNpcChat("green armour best")
        wartface.setBusy(False)
        
        bentnoze.setBusy(True)
        script.setActiveNpc(bentnoze)
        script.faceNpc(bentnoze);
        script.sendNpcChat("No no Red every time")
        bentnoze.setBusy(False)
        
        wartface.setBusy(True)
        script.setActiveNpc(wartface)
        script.faceNpc(wartface)
        script.sendNpcChat("go away human, we busy")
    
    def switchGoblin(self, script, wartface, bentnoze):
        '''
        Switch the current goblin we're talking to and allow the other one to roam
        '''
        
        if wartface.isBusy():
            wartface.setBusy(False)
            bentnoze.setBusy(True)
            script.setActiveNpc(bentnoze)
            script.faceNpc(bentnoze)
        else:
            bentnoze.setBusy(False)
            wartface.setBusy(True)
            script.setActiveNpc(wartface)
            script.faceNpc(wartface)
    
    def blockTalkToNpc(self, player, npc):
        return npc.getID() == self.WARTFACE or npc.getID() == self.BENTNOZE or npc.getID() == self.BARTENDER