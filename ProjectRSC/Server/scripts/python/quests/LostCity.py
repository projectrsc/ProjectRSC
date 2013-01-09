from com.prsc.gs.plugins import Quest
from com.prsc.gs.plugins.listeners.action import TalkToNpcListener, ObjectActionListener
from com.prsc.gs.plugins.listeners.executive import TalkToNpcExecutiveListener, ObjectActionExecutiveListener

class LostCity(Quest, TalkToNpcListener, ObjectActionListener, TalkToNpcExecutiveListener, ObjectActionExecutiveListener):
    
    # NPCS Used
    ADVENTURER_CLERIC = 207
    ADVENTURER_WIZARD = 208
    ADVENTURER_WARRIOR = 209
    ADVENTURER_ARCHER = 210
    LEPRECHAUN = 211
    
    # Objects used
    LEPROCHAUN_TREE = 237
    
    def getQuestId(self):
        return 18
    
    def getQuestName(self):
        return "Lost city (members only)"
    
    def isMembers(self):
        return True
    
    def handleReward(self, player):
        script = player.getScriptHelper()
        script.setActiveQuest(self)
    
    def onTalkToNpc(self, player, npc):        
        script = player.getScriptHelper()
        script.setActiveNpc(npc)
        script.setActiveQuest(self)
        stage = script.getQuestStage()
        script.occupy()
        
        if npc.getID() == self.ADVENTURER_WIZARD or npc.getID() == self.ADVENTURER_ARCHER or npc.getID() == self.ADVENTURER_CLERIC or npc.getID() == self.ADVENTURER_WARRIOR:
            if stage == 0:
                script.sendNpcChat("hello traveler")
                option = script.pickOption(["what are you camped out here for?", "Do you know any good adventures I can go on?"])
                if option == 0:
                    script.sendNpcChat("we're looking for Zanaris")
                    sub_option = script.pickOption(["Who's Zanaris", "What's Zanaris", "what makes you think its out here"])
                    if sub_option == 0:
                        script.sendNpcChat("here Zanaris isn't a person", "It's a magical hidden city")
                        next_option = script.pickOption(["If it's hidden how are planning to find it", "There's no such thing"])
                        if next_option == 0:
                            script.sendNpcChat("well we dont wan't to tell others that", "we all want the glory to find it ourselves")
                            after_option = script.pickOption(["please tell me", "looks like you don't know either if you're sitting around here"])
                            if after_option == 0:
                                script.sendNpcChat("No")
                            elif after_option == 1:
                                script.sendNpcChat("of course we know", "We haven't worked out which tree the stupid leprechaun is hiding in", 
                                               "oops didn't mean to tell you that")
                                script.sendPlayerChat("So a leprechaun knows where Zanaris is?")
                                script.sendNpcChat("eerm", "yes")
                                script.sendPlayerChat("and he's in a tree somewhere around here", "thankyou very much")
                                script.setQuestStage(1)
                        elif next_option == 1:
                            script.sendNpcChat("")
                    elif sub_option == 1:
                        script.sendNpcChat("I don't think we want other people competing with us to finish")
                        next_option = script.pickOption(["Please tell me", "Oh well never mind"])
                        if next_option == 0:
                            script.sendNpcChat("No")
                    elif sub_option == 2:
                        script.sendNpcChat("Don't you know of the legends?", "of the magical city, hidden in the swamp")
                        next_option = script.pickOption(["If it's hidden how are planning to find it", "There's no such thing"])
                        if next_option == 0:
                            script.sendNpcChat("well we dont wan't to tell others that", "we all want the glory to find it ourselves")
                            after_option = script.pickOption(["please tell me", "looks like you don't know either if you're sitting around here"])
                            if after_option == 0:
                                script.sendNpcChat("No")
                            elif after_option == 1:
                                script.sendNpcChat("of course we know", "We haven't worked out which tree the stupid leprechaun is hiding in", 
                                               "oops didn't mean to tell you that")
                                script.sendPlayerChat("So a leprechaun knows where Zanaris is?")
                                script.sendNpcChat("eerm", "yes")
                                script.sendPlayerChat("and he's in a tree somewhere around here", "thankyou very much")
                                script.setQuestStage(1)
                        elif next_option == 1:
                            script.sendNpcChat("")
                elif option == 1:
                    script.sendNpcChat("")
            elif stage == 1:
                script.sendPlayerChat("So let me get this straight", "I need to search the trees near here for a leprechaun?",
                                      "and he will tell me where Zanaris is?")
                script.sendNpcChat("That is what the legends and rumours are, yes")
        elif npc.getID() == self.LEPRECHAUN:
            if stage == 1:
                script.sendNpcChat("Ay you big elephant", "you have caught me", 
                                   "What would you be wanting with Old Shamus then")
                script.sendPlayerChat("I want to find zanaris")
                script.sendNpcChat("Zanaris?", "You need to go in the funny little shed", "in the middle of the swamp")
                script.sendPlayerChat("Oh I thought zanaris was a city")
                script.sendNpcChat("it is")
                option = script.pickOption(["How does it fit in a shed then?", "I've been in that shed, I didn't see a city"])
                if option == 0:
                    script.sendNpcChat("Silly person", "the city isn't in the shed", "the shed is a portal to Zanaris")
                    script.sendPlayerChat("So I just want into the shed and end up in Zanaris?")
                    script.sendNpcChat("Oh I didn't say?", "You need to be carrying around a dramenwood staff",
                                       "otherwise you do just end up in a shed")
                    script.sendPlayerChat("so where would I get a staff?")
                    script.sendNpcChat("Dramenwood branches are crafted from branches", "these staffs are cut from the Dramen tree",
                                       "located somewhere in a cave on the island of entrana", "I believe the monks of Entrana have recently",
                                       "start running a ship from port sarim to Entrana")
                elif option == 1:
                    script.sendNpcChat("")
                script.displayMessage("The leprechaun magically disappears")
        script.release()
        
    def onObjectAction(self, gameObj, command, player):
        script = player.getScriptHelper()
        script.setActiveQuest(self)
        stage = script.getQuestStage()
        script.occupy()
        if stage == 0:
            script.displayMessage("There is nothing in this tree")
        if stage == 1:
            if not script.isNpcNearby(self.LEPRECHAUN):
                script.displayMessage("A leprechaun jumps down from the tree and runs off")
                leprechaun = script.spawnNpc(self.LEPRECHAUN, 173, 661, 300000, False)
                script.setActiveNpc(leprechaun)
            else:
                script.displayMessage("There is nothing in this tree")
        script.release()
    
    def blockObjectAction(self, gameObj, command, player):
        script = player.getScriptHelper()
        script.setActiveQuest(self)
        stage = script.getQuestStage()
        x = gameObj.getX()
        y = gameObj.getY()
        
        if not player.canAccessMembers():
            return False
        
        return command == "search" and gameObj.getID() == self.LEPROCHAUN_TREE and x == 172 and y == 662 and stage >= 0
    
    def blockTalkToNpc(self, player, npc):
        script = player.getScriptHelper()
        script.setActiveQuest(self)
        
        if not player.canAccessMembers():
            return False
        
        if npc.getID() ==  self.ADVENTURER_CLERIC:
            return True
        
        if npc.getID() ==  self.ADVENTURER_WIZARD:
            return True
        
        if npc.getID() ==  self.ADVENTURER_ARCHER:
            return True
        
        if npc.getID() ==  self.ADVENTURER_WARRIOR:
            return True
        
        if npc.getID() == self.LEPRECHAUN:
            return True
        
