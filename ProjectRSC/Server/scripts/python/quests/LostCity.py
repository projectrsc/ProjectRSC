from com.prsc.gs.plugins import Quest
from com.prsc.gs.plugins.listeners.action import TalkToNpcListener, ObjectActionListener, InvUseOnItemListener, WallObjectActionListener, PlayerKilledNpcListener
from com.prsc.gs.plugins.listeners.executive import TalkToNpcExecutiveListener, ObjectActionExecutiveListener, InvUseOnItemExecutiveListener, WallObjectActionExecutiveListener, PlayerKilledNpcExecutiveListener

# LOST CITY - @author: GORF

class LostCity(Quest, TalkToNpcListener, ObjectActionListener, PlayerKilledNpcListener, InvUseOnItemListener,
               WallObjectActionListener, TalkToNpcExecutiveListener, InvUseOnItemExecutiveListener, 
               ObjectActionExecutiveListener, WallObjectActionExecutiveListener, PlayerKilledNpcExecutiveListener):
    
    # NPCS Used
    ADVENTURER_CLERIC = 207
    ADVENTURER_WIZARD = 208
    ADVENTURER_WARRIOR = 209
    ADVENTURER_ARCHER = 210
    LEPRECHAUN = 211
    MONK_OF_ENTRANA = 213
    ZOMBIE = 214
    TREE_SPIRIT = 216
    
    # Objects used
    LEPROCHAUN_TREE = 237
    ENTRANA_LADDER = 244
    DRAMEN_TREE = 245
    MAGIC_DOOR = 65
    ZANARIS_DOOR = 66
    
    # Items used'
    KNIFE = 13
    BONES = 20
    FISHING_BAIT = 380 
    BRONZE_ARROWS = 11
    BRONZE_AXE = 87 
    DRAMEN_BRANCH = 510
    DRAMEN_STAFF = 509
    
    # Required Levels
    REQUIRED_CRAFTING = 31
    REQUIRED_WOODCUTTING = 36
    
    def getQuestId(self):
        return 18
    
    def getQuestName(self):
        return "Lost city (members only)"
    
    def isMembers(self):
        return True
    
    def handleReward(self, player):
        script = player.getScriptHelper()
        script.setActiveQuest(self)
        script.addQuestPoints(3)
        script.displayMessage("The world starts to shimmer", "You find yourself in different surroundings",
                              "Well done you have completed the Lost City of Zanaris quest",
                              "@gre@You have gained 3 quest points!")
    
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
                            script.sendNpcChat("hehe thats what you think")
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
                            script.sendNpcChat("hehe thats what you think")
                elif option == 1:
                    script.sendNpcChat("No sorry I don't")
            elif stage == 1:
                script.sendPlayerChat("So let me get this straight", "I need to search the trees near here for a leprechaun?",
                                      "and he will tell me where Zanaris is?")
                script.sendNpcChat("That is what the legends and rumours are, yes")
            elif stage == 2:
                script.sendPlayerChat("thankyou for your information", "it has helped me a lot in my quest to find Zanaris")
                script.sendNpcChat("so what have you found out?", "Where is Zanaris?")
                script.sendPlayerChat("I think I will keep that to myself")
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
                    script.sendNpcChat("Silly person", "the city isn't in the shed", "the shed is a portal to Zanaris")
                    script.sendPlayerChat("So I just want into the shed and end up in Zanaris?")
                    script.sendNpcChat("Oh I didn't say?", "You need to be carrying around a dramenwood staff",
                                       "otherwise you do just end up in a shed")
                    script.sendPlayerChat("so where would I get a staff?")
                    script.sendNpcChat("Dramenwood branches are crafted from branches", "these staffs are cut from the Dramen tree",
                                       "located somewhere in a cave on the island of entrana", "I believe the monks of Entrana have recently",
                                       "start running a ship from port sarim to Entrana")
                script.setQuestStage(2)
                script.displayMessage("The leprechaun magically disappears")
                script.removeNpc(npc)
            if stage == 2:
                script.sendNpcChat("Ay you big elephant", "you have caught me", 
                                   "What would you be wanting with Old Shamus then")
                option = script.pickOption(["I'm not sure", "How do I get to Zanaris again?"])
                if option == 0:
                    script.sendNpcChat("I dunno what stupid people", "Who go to all the trouble to catch leprechaun's",
                                       "when they don't even know what they want")
                elif option == 1:
                    script.sendNpcChat("You need to enter the shed in the middle of the swamp", 
                                       "while holding a dramenwood staff", "made from a branch", 
                                       "cut from the dramen tree on the island of Entrana")
                script.displayMessage("The leprechaun magically disappears")
                script.removeNpc(npc)
        elif npc.getID() == self.MONK_OF_ENTRANA:
            if stage == 2:
                script.sendNpcChat("Be careful going down there", "You are unarmed, and there is much evilness lurking",
                                   "The evilness seems to block off our contact with our god", 
                                   "Our prayers seem to have less effect down there", 
                                   "Oh also you won't be able to come back this way",
                                   "This ladder only goes one way", "The only way out is a portal which leads deep into the wilderness")
                option = script.pickOption(["I don't think I'm strong enough to enter then", "Well that is a risk I will have to take"])
                if option == 1:
                    script.displayMessage("You climb down the ladder")
                    script.sleep(1000)
                    script.movePlayer(427, 3380, False)
                    if script.getCurrentLevel(player.SkillType.PRAYER) <= 3:
                        script.restoreStat(player.SkillType.PRAYER, 1)
                    else:
                        script.restoreStat(player.SkillType.PRAYER, 3)
                    script.setQuestStage(3)
        script.release()
        
    def onObjectAction(self, gameObj, command, player):
        script = player.getScriptHelper()
        script.setActiveQuest(self)
        stage = script.getQuestStage()
        script.occupy()
        
        if command == "search":
            if stage == 0:
                script.displayMessage("There is nothing in this tree")
            if stage == 1 or stage == 2 or stage == 3:
                if not script.isNpcNearby(self.LEPRECHAUN):
                    script.displayMessage("A leprechaun jumps down from the tree and runs off")
                    leprechaun = script.spawnNpc(self.LEPRECHAUN, 173, 661, 300000, False)
                    script.setActiveNpc(leprechaun)
                else:
                    script.displayMessage("There is nothing in this tree")
        elif command == "climb-down":
            if stage == 2:
                entrana_monk = script.getNearestNpc(self.MONK_OF_ENTRANA, 5)
                if entrana_monk == None:
                    return
                script.setActiveNpc(entrana_monk)
                script.faceNpc(entrana_monk)
                script.sendNpcChat("Be careful going down there", "You are unarmed, and there is much evilness lurking",
                                   "The evilness seems to block off our contact with our god", 
                                   "Our prayers seem to have less effect down there", 
                                   "Oh also you won't be able to come back this way",
                                   "This ladder only goes one way", "The only way out is a portal which leads deep into the wilderness")
                option = script.pickOption(["I don't think I'm strong enough to enter then", "Well that is a risk I will have to take"])
                if option == 1:
                    script.displayMessage("You climb down the ladder")
                    script.sleep(1000)
                    script.movePlayer(427, 3380, False)
                    if script.getCurrentLevel(player.SkillType.PRAYER) <= 3:
                        script.restoreStat(player.SkillType.PRAYER, 1)
                    else:
                        script.restoreStat(player.SkillType.PRAYER, 3)
                    script.setQuestStage(3)
        elif command == "chop":
            if stage == 3:
                if script.getCurrentLevel(player.SkillType.WOODCUT) < self.REQUIRED_WOODCUTTING: # 36
                    script.displayMessage("You are not a high enough woodcutting level to chop down this tree", "You need a woodcutting level of 36")
                    return
            
                if not script.isNpcNearby(self.TREE_SPIRIT):
                    script.displayMessage("You attempt to chop the tree")
                    script.sleep(1000)
                    ghost_spirit = script.spawnNpc(self.TREE_SPIRIT, player.getX() + 1, player.getY() + 1, 300000, False, True)
                    if ghost_spirit == None:
                        return
                    script.release()
                    script.attackPlayer(ghost_spirit)
                    script.setQuestStage(4)
            if stage == 5:
                script.displayMessage("You attempt to chop the tree", "You manage to cut off a dramen branch")
                script.sleep(1000)
                script.addItem(self.DRAMEN_BRANCH, 1)
        script.release()
    
    def onPlayerKilledNpc(self, player, npc):
        script = player.getScriptHelper()
        script.setActiveQuest(self)
        script.setActiveNpc(npc)
        stage = script.getQuestStage()
        
        no_bones = script.getRandom(0, 3) == 0
        
        if stage == 3 and npc.getID() == self.ZOMBIE:
            if no_bones:
                return
            random = script.getRandom(0, 2)
            if random == 0:
                script.spawnItem(player.getX(), player.getY(), self.BRONZE_ARROWS, 8)
            elif random == 1:
                script.spawnItem(player.getX(), player.getY(), self.FISHING_BAIT, 1)
            elif random == 2:
                script.spawnItem(player.getX(), player.getY(), self.BRONZE_AXE, 1)
            script.spawnItem(player.getX(), player.getY(), self.BONES, 1)
        
        if stage == 4 and npc.getID() == self.TREE_SPIRIT:
            script.setQuestStage(5)
        
    def onWallObjectAction(self, gameObj, click, player):
        script = player.getScriptHelper()
        script.setActiveQuest(self)
        stage = script.getQuestStage()
        script.occupy()
        
        if gameObj.getID() == self.MAGIC_DOOR:
            script.movePlayer(109, 245, False)
            script.sleep(500)
            script.displayMessage("you go through the door and find yourself somewhere else")
            
        if stage == -1 and gameObj.getID() == self.ZANARIS_DOOR:
            if script.isWielding(self.DRAMEN_STAFF):
                script.movePlayer(126, 3518, False)
            
        if stage == 6 and gameObj.getID() == self.ZANARIS_DOOR:
            if script.isWielding(self.DRAMEN_STAFF):
                script.sleep(500)
                script.movePlayer(126, 3518, False)
                script.setQuestStage(-1)
                script.setQuestCompleted()
            else:
                script.displayMessage("Nothing interesting happens")
                return
            
        script.release()
    
    def onInvUseOnItem(self, player, item, item2):
        script = player.getScriptHelper()
        script.setActiveQuest(self)
        stage = script.getQuestStage()
        script.occupy()
        
        if stage == 5 and script.hasItem(self.DRAMEN_BRANCH):
            if script.getCurrentLevel(player.SkillType.CRAFTING) < self.REQUIRED_CRAFTING:
                script.displayMessage("You are not a high enough crafting level to craft this staff", "You need a crafting level of 31")
                return
            script.sleep(1500)
            script.removeItem(self.DRAMEN_BRANCH, 1)
            script.displayMessage("You craft a dramen staff out of the branch")
            script.addItem(self.DRAMEN_STAFF, 1)
            script.setQuestStage(6)
        script.release()
        
    def blockInvUseOnItem(self, player, item, item2):
        if not player.canAccessMembers():
            return False
        return item.getID() == self.KNIFE and item2.getID() == self.DRAMEN_BRANCH
    
    def blockWallObjectAction(self, gameObj, click, player):
        if not player.canAccessMembers():
            return False
        return gameObj.getID() == self.MAGIC_DOOR or gameObj.getID() == self.ZANARIS_DOOR
    
    def blockObjectAction(self, gameObj, command, player):
        script = player.getScriptHelper()
        script.setActiveQuest(self)
        stage = script.getQuestStage()
        x = gameObj.getX()
        y = gameObj.getY()
        
        if not player.canAccessMembers():
            return False
        
        if command == "search" and gameObj.getID() == self.LEPROCHAUN_TREE and x == 172 and y == 662:
            return True
        
        if command == "climb-down" and gameObj.getID() == self.ENTRANA_LADDER and x == 426 and y == 548:
            return True
        
        if command == "chop" and gameObj.getID() == self.DRAMEN_TREE and x == 412 and y == 3402:
            return True
    
    def blockPlayerKilledNpc(self, player, npc):
        if not player.canAccessMembers():
            return False
        return npc.getID() == self.ZOMBIE or npc.getID() == self.TREE_SPIRIT
    
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
        
        if npc.getID() == self.MONK_OF_ENTRANA:
            return True
        
        
