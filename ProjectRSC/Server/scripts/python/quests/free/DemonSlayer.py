from com.prsc.gs.plugins import Quest
from com.prsc.gs.plugins.listeners.action import TalkToNpcListener, ObjectActionListener, InvUseOnObjectListener, PlayerAttackNpcListener, PlayerKilledNpcListener
from com.prsc.gs.plugins.listeners.executive import TalkToNpcExecutiveListener, ObjectActionExecutiveListener, InvUseOnObjectExecutiveListener, PlayerAttackNpcExecutiveListener, PlayerKilledNpcExecutiveListener

class DemonSlayer(Quest, TalkToNpcListener, ObjectActionListener, InvUseOnObjectListener, PlayerAttackNpcListener,
                  PlayerKilledNpcListener, TalkToNpcExecutiveListener, ObjectActionExecutiveListener, 
                  InvUseOnObjectExecutiveListener, PlayerAttackNpcExecutiveListener, PlayerKilledNpcExecutiveListener):
    
    # NPCs used
    GYPSY = 14
    SIR_PRYSIN = 16
    WIZARD_TRAIBORN = 17 
    CAPTAIN_ROVIN = 18
    DELRITH = 35
    
    # Objects used
    KITCHEN_DRAIN = 77
    
    # items used
    COINS = 10
    BUCKET = 21
    BUCKET_OF_WATER = 50
    BONES = 20
    SILVERLIGHT_KEY_1 = 25
    SILVERLIGHT_KEY_2 = 26
    SILVERLIGHT_KEY_3 = 51
    SPINACH_ROLL = 179
    SILVERLIGHT = 52
    
    # Required
    NEEDED_BONES = 25
    
    def getQuestId(self):
        return 2
    
    def getQuestName(self):
        return "Demon Slayer"
    
    def isMembers(self):
        return False
    
    def handleReward(self, player):
        script = player.getScriptHelper()
        script.setActiveQuest(self)
        script.displayMessage("Well done. You have completed the demon slayer quest")
        script.displayMessage("@gre@You just advanced 3 quest points!")
        script.addQuestPoints(3)
    
    def onTalkToNpc(self, player, npc):        
        script = player.getScriptHelper()
        script.setActiveNpc(npc)
        script.setActiveQuest(self)
        stage = script.getQuestStage()
        npc_id = npc.getID()
        script.occupy()
        
        if npc_id == self.GYPSY:
            if stage == -1:
                script.sendNpcChat("Greetings young one", "You're a hero now", "That was a good bit of demonslaying")
                option = script.pickOption(["How do you know I killed it?", "Thanks", "Stop calling me that"])
                if option == 0:
                    script.sendNpcChat("You forget", "I'm good at knowing things")
                elif option == 2:
                    script.sendNpcChat("In the scheme of things you are very young")
                    sub_option = script.pickOption(["Ok but how old are you", "Oh if its in the scheme of things that's ok"])
                    if sub_option == 0:
                        self.howOld(script)
                    elif sub_option == 1:
                        script.sendNpcChat("You show wisdom for one so young")
            elif stage == 0:
                script.sendNpcChat("Hello, young one", "cross my palm with silver and the future will be revealed to you")
                option = script.pickOption(["Ok, here you go", "Who are you calling young one!?", "No I don't believe in that stuff"])
                if option == 0:
                    self.whoisDelrith(script)
                elif option == 1:
                    script.sendNpcChat("You have been on this world", "A relatively short time", "at least compared to me")
                    script.sendNpcChat("so do you want you fortune told or not/")
                    sub_option = script.pickOption(["Yes please", "No I don't believe in that stuff", "ooh how old are you then?"])
                    if sub_option == 0:
                        script.sendNpcChat("Cross my palm with silver and I'll tell you")
                        after_option = script.pickOption(["Ok here you go", "Oh you want me to pay. No thanks"])
                        if after_option == 0:
                            self.whoisDelrith(script)
                        elif after_option == 1:
                            script.sendNpcChat("Go away then")
                    elif sub_option == 1:
                        script.sendNpcChat("Ok suit yourself")
                    elif sub_option == 2:
                        script.sendNpcChat("Older than you imagine")
                        more_option = script.pickOption(["Believe me, I have a good imagination", "How do you know how old I think you are?",
                                                         "Oh pretty old then"])
                        if more_option == 0:
                            script.sendNpcChat("You seem like just the sort of person", "who would want their fortune told")
                            extra_option = script.pickOption(["No I don't believe in that stuff", "Yes please"])
                            if extra_option == 0:
                                script.sendNpcChat("Ok suit yourself")
                            elif extra_option == 1:
                                script.sendNpcChat("Cross my palm with silver and I'll tell you")
                                after_option = script.pickOption(["Ok here you go", "Oh you want me to pay. No thanks"])
                                if after_option == 0:
                                    self.whoisDelrith(script)
                                elif after_option == 1:
                                    script.sendNpcChat("Go away then")
                        if more_option == 1:
                            script.sendNpcChat("I have the power to know", "just as I have the power to foresee the future")
                            extra_option = script.pickOption(["ok what am I thinking now?", "ok but how old are you?", "Go on then, what's my future?"])
                            if extra_option == 0:
                                script.sendNpcChat("You are thinking that I'll never guess what you are thinking")
                            elif extra_option == 1:
                                self.howOld(script)
                            elif extra_option == 2:
                                script.sendNpcChat("Cross my palm with silver and I'll tell you")
                                after_option = script.pickOption(["Ok here you go", "Oh you want me to pay. No thanks"])
                                if after_option == 0:
                                    self.whoisDelrith(script)
                                elif after_option == 1:
                                    script.sendNpcChat("Go away then")
                        if more_option == 2:
                            script.sendNpcChat("Yes i'm old", "don't rub it in")
                elif option == 2:
                    script.sendNpcChat("Ok suit yourself")
            elif stage == 1:
                script.sendNpcChat("How goes thy quest?")
                script.sendPlayerChat("I'm still working on it")
                script.sendNpcChat("Well if you need any advice I'm always here young one")
                option = script.pickOption(["What is the magical incantation?", "Where can I find silverlight?",
                                            "Well i'd better press on with it", "Stop calling me that"])
                if option == 0:
                    self.getIncantationInfo(script) # TODO re ask again
                elif option == 1:
                    self.getSilverlightInfo(script) # TODO re ask again
                elif option == 2:
                    script.sendNpcChat("See you anon")
                elif option == 3:
                    script.sendNpcChat("In the scheme of things you are very young")
                    sub_option = script.pickOption(["Ok but how old are you", "Oh if its in the scheme of things that's ok"])
                    if sub_option == 0:
                        self.howOld(script)
                    elif sub_option == 1:
                        script.sendNpcChat("You show wisdom for one so young")
        elif npc_id == self.SIR_PRYSIN:
            if stage == -1:
                script.sendNpcChat("Hello, I've heard you stopped the demon well done")
                script.sendPlayerChat("Yes, that's right")
                script.sendNpcChat("A good job well done then")
                script.sendPlayerChat("Thank you")
            elif stage == 0:
                script.sendNpcChat("hello, who are you")
                option = script.pickOption(["I am a mighty adventurer. Who are you?", "I'm not sure, I was hoping you could tell me"])
                if option == 0:
                    script.sendNpcChat("I am Sir Prysin", "a bold and famous knight of the realm")
                elif option == 1:
                    script.sendNpcChat("Well I've never met you before")
            elif stage == 1:
                script.sendNpcChat("hello, who are you")
                option = script.pickOption(["I am a mighty adventurer. Who are you?", "I'm not sure, I was hoping you could tell me",
                                            "Gypsy Aris said I should come and talk to you"])
                if option == 0:
                    script.sendNpcChat("I am Sir Prysin", "a bold and famous knight of the realm")
                elif option == 1:
                    script.sendNpcChat("Well I've never met you before")
                elif option == 2:
                    script.sendNpcChat("Gypsy Aris? Is she still alive?", "I remember her from when I was pretty young",
                                       "Well what do you need to talk to me about?")
                    sub_option = script.pickOption(["I need to find Silverlight", "Yes, she is still alive"])
                    if sub_option == 0:
                        self.getSilverlightKeyInfo(script)
                    elif sub_option == 1:
                        script.sendNpcChat("I would have thought she had died by now", "She was pretty old, when I was a lad",
                                           "Anyway, what can I do for you?")
                        script.sendPlayerChat("I need to find Silverlight")
                        self.getSilverlightKeyInfo(script)
            elif stage == 2 or stage == 3:
                script.sendNpcChat("So how are you doing with getting the keys?")
                if script.hasItem(self.SILVERLIGHT_KEY_1, 1) and script.hasItem(self.SILVERLIGHT_KEY_2, 1) and script.hasItem(self.SILVERLIGHT_KEY_3, 1):
                    script.sendPlayerChat("I've got them all")
                    script.sendNpcChat("Excellent, Now I can give you the Silverlight")
                    script.displayMessage("You give all three keys to Sir Prysin")
                    script.removeItem(self.SILVERLIGHT_KEY_1, 1)
                    script.removeItem(self.SILVERLIGHT_KEY_2, 1)
                    script.removeItem(self.SILVERLIGHT_KEY_3, 1)
                    script.sleep(500)
                    script.displayMessage("He unlocks a long thin box")
                    script.sleep(500)
                    script.displayMessage("He hands you an impressive looking sword")
                    script.addItem(self.SILVERLIGHT, 1)
                    script.setQuestStage(4)
                    script.release()
                    return
                elif script.hasItem(self.SILVERLIGHT_KEY_2, 1) and script.hasItem(self.SILVERLIGHT_KEY_3, 1):
                    script.sendPlayerChat("I've made a start", "I've got the key off Captain Rovin", "I've got the key from the drain")
                elif script.hasItem(self.SILVERLIGHT_KEY_2, 1) and script.hasItem(self.SILVERLIGHT_KEY_1, 1):
                    script.sendPlayerChat("I've made a start", "I've got the key off Wizard Traiborn", "I've got the key off Captain Rovin")
                elif script.hasItem(self.SILVERLIGHT_KEY_1):
                    script.sendPlayerChat("I've made a start", "I've got the key off Wizard Traiborn")
                elif script.hasItem(self.SILVERLIGHT_KEY_2):
                    script.sendPlayerChat("I've made a start", "I've got the key off Captain Rovin")
                elif script.hasItem(self.SILVERLIGHT_KEY_3):
                    script.sendPlayerChat("I've made a start", "I've got the key from the drain")
                else:
                    script.sendPlayerChat("I've not found any of them yet")
                option = script.pickOption(["Can you remind me where all the keys were again?", "I'm still looking"])
                if option == 0:
                    self.remindOfKeyInfo(script)
                elif option == 1:
                    script.sendNpcChat("Ok, tell me when you've got them all")
            elif stage == 4:
                script.sendNpcChat("You sorted that demon yet?")
                script.sendPlayerChat("No, not yet")
                script.sendNpcChat("Well get on with it", "He'll be pretty powerful when gets to full strength")
        elif npc_id == self.CAPTAIN_ROVIN: # TODO get stage 1 dialogue
            if stage < 2:
                script.sendNpcChat("What are you doing up here?", "Only the palace guards are allowed up here")
                option = script.pickOption(["I am one of the palace guards", "What about the king?"])
                if option == 0:
                    script.sendNpcChat("No you're not. I know all the palace guard")
                    sub_option = script.pickOption(["I'm a new recruit", "I've had extensive plastic surgery"])
                    if sub_option == 0:
                        script.sendNpcChat("I interview all the new recruits", "I'd know if you were one of them")
                        script.sendPlayerChat("That blows that story out of the window then")
                        script.sendNpcChat("Get out of my sight")
                    elif sub_option == 1:
                        script.sendNpcChat("What sort of surgery is that?", "Never heard of it", 
                                           "Besides, you look reasonably healthy", "Why is this relevant anyway?",
                                           "You still shouldn't be here")
                elif option == 1:
                    script.sendPlayerChat("Sure you'd let him up here?")
                    script.sendNpcChat("Well, yes, I suppose we'd let him up here", "He doesn't generally want to come up here",
                                       "But if he did want to", "He could come up", "Anyway, you're not the king either",
                                       "so get out of my sight")
            if stage == 2 or stage == 3:
                script.sendNpcChat("What are you doing up here?", "Only the palace guards are allowed up here")
                option = script.pickOption(["I am one of the palace guards", "What about the king?", "Yes I know but this is important"])
                if option == 0:
                    script.sendNpcChat("No you're not. I know all the palace guard")
                    sub_option = script.pickOption(["I'm a new recruit", "I've had extensive plastic surgery"])
                    if sub_option == 0:
                        script.sendNpcChat("I interview all the new recruits", "I'd know if you were one of them")
                        script.sendPlayerChat("That blows that story out of the window then")
                        script.sendNpcChat("Get out of my sight")
                    elif sub_option == 1:
                        script.sendNpcChat("What sort of surgery is that?", "Never heard of it", 
                                           "Besides, you look reasonably healthy", "Why is this relevant anyway?",
                                           "You still shouldn't be here")
                elif option == 1:
                    script.sendPlayerChat("Sure you'd let him up here?")
                    script.sendNpcChat("Well, yes, I suppose we'd let him up here", "He doesn't generally want to come up here",
                                       "But if he did want to", "He could come up", "Anyway, you're not the king either",
                                       "so get out of my sight")
                elif option == 2:
                    script.sendNpcChat("OK, I'm listening", "Tell me what's so important")
                    sub_option = script.pickOption(["There's a demon who wants to invade this city", "Erm I forgot",
                                                    "The castle has just received it's ale delivery"])
                    if sub_option == 0:
                        script.sendNpcChat("Is it a powerful demon?")
                        script.sendPlayerChat("Yes, very")
                        script.sendNpcChat("Well as good as the palace guards are", "I don't think they're up to taking on a very powerful demon")
                        script.sendPlayerChat("No no, it's not them who's going to fight the demon", "It's me")
                        script.sendNpcChat("What all by yourself?")
                        script.sendPlayerChat("Well I am going to use the powerful sword of silverlight",
                                              "Which I believe you have one of the keys for")
                        script.sendNpcChat("Yes you're right", "Here you go")
                        script.sleep(500)
                        script.addItem(self.SILVERLIGHT_KEY_2, 1)
                        script.displayMessage("Captain Rovin hands you a key")
                    elif sub_option == 1:
                        script.sendNpcChat("Well it can't be that important then")
                        script.sendPlayerChat("How do you know?")
                        script.sendNpcChat("Just go away")
                    elif sub_option == 2:
                        script.sendNpcChat("Now that is important", "However, I'm the wrong person to speak about it",
                                           "Go talk to the kitchen staff")
        elif npc_id == self.WIZARD_TRAIBORN:
            if stage < 2:
                script.sendNpcChat("Ello young thingummywut")
                option = script.pickOption(["Whats a thingummywut?", "Teach me to be a mighty and powerful wizard"])
                if option == 0:
                    script.sendNpcChat("A thingummywut?", "Where?, where?", "Those psesky thingummywuts", "They get everywhere",
                                       "They leave a terrible mess too")
                    sub_option = script.pickOption(["Err you just called me thingummywut", "Tell me what they look like and I'll mash 'em"])
                    if sub_option == 0:
                        script.sendNpcChat("You're a thingummywut?", "I've never seen one up close before", "They said I was mad",
                                           "Now you are my proof", "There ARE thingummywuts in this tower", "Now where can I find a cage big enough to keep you")
                        extra_option = script.pickOption(["Err I'd be better off really", "They're right, you are mad"])
                        if extra_option == 0:
                            script.sendNpcChat("Oh ok have a good time", "and watch out for sheep!", "They're more cunning than they look")
                        elif extra_option == 1:
                            script.sendNpcChat("That's a pity", "I thought maybe they were winding me up")
                    elif sub_option == 1:
                        script.sendNpcChat("Don't be ridiculous", "No-one has ever seen one", "They're invisible", "or a myth",
                                           "or a figment of my imagination", "can't remember which right now")
                elif option == 1:
                    script.sendNpcChat("Wizard, eh?", "You don't want any truck with that sort", "They're not to be trusted",
                                       "That's what I've heard anyways")
                    sub_option = script.pickOption(["So aren't you a wizard?", "Oh I'd better stop talking to you then"])
                    if sub_option == 0:
                        script.sendNpcChat("How dare you?", "Of course I'm a wizard", "Now don't be so cheeky or I'll turn you into a frog")
                    elif sub_option == 1:
                        script.sendNpcChat("Cheerio then", "was nice chatting to you")
            elif stage == 2:
                script.sendNpcChat("Ello young thingummywut")
                option = script.pickOption(["Whats a thingummywut?", "Teach me to be a mighty and powerful wizard",
                                            "I need to get a key given to you Sir Prysin"])
                if option == 0:
                    script.sendNpcChat("A thingummywut?", "Where?, where?", "Those psesky thingummywuts", "They get everywhere",
                                       "They leave a terrible mess too")
                    sub_option = script.pickOption(["Err you just called me thingummywut", "Tell me what they look like and I'll mash 'em"])
                    if sub_option == 0:
                        script.sendNpcChat("You're a thingummywut?", "I've never seen one up close before", "They said I was mad",
                                           "Now you are my proof", "There ARE thingummywuts in this tower", "Now where can I find a cage big enough to keep you")
                        extra_option = script.pickOption(["Err I'd be better off really", "They're right, you are mad"])
                        if extra_option == 0:
                            script.sendNpcChat("Oh ok have a good time", "and watch out for sheep!", "They're more cunning than they look")
                        elif extra_option == 1:
                            script.sendNpcChat("That's a pity", "I thought maybe they were winding me up")
                    elif sub_option == 1:
                        script.sendNpcChat("Don't be ridiculous", "No-one has ever seen one", "They're invisible", "or a myth",
                                           "or a figment of my imagination", "can't remember which right now")
                elif option == 1:
                    script.sendNpcChat("Wizard, eh?", "You don't want any truck with that sort", "They're not to be trusted",
                                       "That's what I've heard anyways")
                    sub_option = script.pickOption(["So aren't you a wizard?", "Oh I'd better stop talking to you then"])
                    if sub_option == 0:
                        script.sendNpcChat("How dare you?", "Of course I'm a wizard", "Now don't be so cheeky or I'll turn you into a frog")
                    elif sub_option == 1:
                        script.sendNpcChat("Cheerio then", "was nice chatting to you")
                elif option == 2:
                    script.sendNpcChat("Sir Prysin? Who's that?", "What would I want his key for?")
                    sub_option = script.pickOption(["he told you were looking after it for him", "He's one of the king's knights",
                                                    "Well, have you got any keys knocking around?"])
                    if sub_option == 0:
                        script.sendNpcChat("That wasn't very clever of him", "I'd lose my head if it wasn't screwed on properly",
                                           "Go tell him to find someone else", "to look after his valuables in the future")
                        next_option = script.pickOption(["Ok I'll go tell him that", "Well, have you any keys knocking around?"])
                        if next_option == 0:
                            script.sendNpcChat("Oh that's great", "ff it wouldn't be too much trouble")
                            more_option = script.pickOption(["Err I'd be better off really", "Well, have you any keys knocking around?"])
                            if more_option == 0:
                                script.sendNpcChat("Oh ok have a good time", "and watch for the sheep!", "they're more cunning than they look")
                            elif more_option == 1:
                                self.getWizardKey(script)
                        elif next_option == 1:
                            self.getWizardKey(script)
                    elif sub_option == 1:
                        script.sendNpcChat("Say, I remember a knight with a key", "He had nice shoes", "and didn't like my homemade spinach rolls",
                                           "Would you like a spinach roll?")
                        script.displayMessage("Wizard Traiborn digs around in his pockets", "He hands you a spinach roll")
                        script.addItem(self.SPINACH_ROLL, 1)
                        script.sendPlayerChat("Thank you very much")
                        more_option = script.pickOption(["Err I'd be better off really", "Well, have you any keys knocking around?"])
                        if more_option == 0:
                            script.sendNpcChat("Oh ok have a good time", "and watch for the sheep!", "they're more cunning than they look")
                        elif more_option == 1:
                            self.getWizardKey(script)
                    elif sub_option == 2:
                        self.getWizardKey(script)
            elif stage == 3:
                script.sendNpcChat("How are you doing finding bones?")
                if script.hasItem(self.BONES, 1):
                    script.sendPlayerChat("I have some bones")
                    script.sendNpcChat("Give em here then")
                    finished = False
                    while script.hasItem(self.BONES, 1):
                        if script.sizeOfCollection() == self.NEEDED_BONES:
                            finished = True
                            break
                        script.displayMessage("You give Traiborn a set of bones")
                        script.removeItem(self.BONES, 1)
                        script.store(1)
                        script.sleep(1000)
                    if finished:
                        script.erase()
                        script.sendNpcChat("Hurrah! That's all 25 sets of bones")
                        script.displayMessage("Traiborn starts waving his arms")
                        script.sendNpcChat("Wings of dark and colour too", "spreading in the mountain dew")
                        script.displayMessage("He starts waving his arms some more")
                        script.sendNpcChat("Locked away I have a key", "Return it now until me")
                        script.displayMessage("Traiborn smiles", "Traiborn hands you a key")
                        script.sleep(500)
                        script.addItem(self.SILVERLIGHT_KEY_1, 1)
                        script.sendPlayerChat("Thank you very much")
                        script.sendNpcChat("Not a problem for a friend of sir what's-his-face")
                    else:
                        script.sendPlayerChat("That's all of them")
                        script.sendNpcChat("I still need more")
                        script.sendPlayerChat("Ok I'll look for more")
                else:
                    script.sendPlayerChat("I haven't got any at the moment")
                    script.sendNpcChat("Never mind. Keep working on it")
                    script.sendPlayerChat("Ok I'll look for more")
        script.release()
    
    def onObjectAction(self, gameObj, command, player):
        script = player.getScriptHelper()
        script.setActiveQuest(self)
        stage = script.getQuestStage()
        
        if stage == 2 or stage == 3:
            script.displayMessage("This is the drain pipe")
            script.sleep(1000)
            script.displayMessage("Running from the kitchen sink to the sewer")
            script.sleep(1000)
            script.displayMessage("I can see a key just inside the drain")
            script.sleep(1000)
            script.displayMessage("That must be the key Sir Prysin dropped")
            script.sleep(1000)
            script.displayMessage("I don't seem able to quite reach it")
            script.sleep(1000)
            script.displayMessage("It's stuck part way down")
            script.sleep(1000)
            script.displayMessage("I wonder if I can dislodge it somehow")
            script.sleep(1000)
            script.displayMessage("And knock it down into the sewers")
    
    def onInvUseOnObject(self, gameObj, item, player):
        script = player.getScriptHelper()
        script.setActiveQuest(self)
        stage = script.getQuestStage()
        
        if stage == 2 and script.hasItem(self.BUCKET_OF_WATER, 1) or stage == 3 and script.hasItem(self.BUCKET_OF_WATER, 1):
            script.removeItem(self.BUCKET_OF_WATER, 1)
            script.addItem(self.BUCKET, 1)
            script.displayMessage("You pour the liquid down the drain")
            script.sleep(1000)
            script.displayMessage("Ok I think I've washed the key down into the sewer")
            script.sleep(1000)
            script.displayMessage("I'd better go down and get it before someone else finds it")
            script.spawnItem(117, 3294, self.SILVERLIGHT_KEY_3, 1)
    
    def onPlayerAttackNpc(self, player, npc):
        script = player.getScriptHelper()
        script.setActiveNpc(npc)
        script.setActiveQuest(self)
        stage = script.getQuestStage()
        
        if stage == -1:
            return
        if stage == 4:
            if script.isWielding(self.SILVERLIGHT):
                fightEvent = script.fightNpc(npc, True)
                script.displayMessage("As you strike the demon with the silverlight he appears to weaken a lot",
                                      "As you strike Delrith a vortex opens up")
                self.incantationTime(script, fightEvent)
            else:
                script.sendPlayerChat("Maybe I'd better wield silverlight first")
                return
        else:
            script.displayMessage("You cannot attack Delrith without the silverlight sword")
            return
    
    def onPlayerKilledNpc(self, player, npc):
        script = player.getScriptHelper()
        script.setActiveNpc(npc)
        script.setActiveQuest(self)
        stage = script.getQuestStage()
        
        if stage == 5:
            script.setQuestStage(-1)
            script.setQuestCompleted()
        
    def howOld(self, script):
        script.sendNpcChat("Count the number of legs of the chairs in the blue moon inn", "and multiply that number by seven")
        script.sendPlayerChat("Errr yeah whatever")
        
    def whoisDelrith(self, script):
        if script.hasItem(self.COINS, 1):
            script.removeItem(10, 1)
            script.sendNpcChat("Come closer", "and listen carefully to what the future holds for you",
                                               "As I peer into the swirling mists of the crystal ball", "I can see images forming",
                                               "I can see you", "You are holding a very impressive looking sword",
                                               "I'm sure I can recognize that sword", "There is a big dark shadow appearing now",
                                               "Aaargh")
            new_option = script.pickOption(["Very interesting what does the Aaargh bit mean?", "Are you alright?", "Aaargh?"])
            if new_option == 0:
                script.sendNpcChat("Aaargh it's Delrith", "Delrith is coming")
                continue_option = script.pickOption(["Who's Delrith?", "Get a grip!"])
                if continue_option == 0:
                    self.nextStage(script)
                elif continue_option == 1:
                    script.sendNpcChat("Sorry I didn't expect to see Delrith", "I had to break away quickly in case he detected me")
                    self.nextStage(script)
            elif new_option == 1:
                script.sendNpcChat("Aaargh it's Delrith", "Delrith is coming")
                continue_option = script.pickOption(["Who's Delrith?", "Get a grip!"])
                if continue_option == 0:
                    self.nextStage(script)
                elif continue_option == 1:
                    script.sendNpcChat("Sorry I didn't expect to see Delrith", "I had to break away quickly in case he detected me")
                    self.nextStage(script)
            elif new_option == 2:
                script.sendNpcChat("Aaargh it's Delrith", "Delrith is coming")
                continue_option = script.pickOption(["Who's Delrith?", "Get a grip!"])
                if continue_option == 0:
                    self.nextStage(script)
                elif continue_option == 1:
                    script.sendNpcChat("Sorry I didn't expect to see Delrith", "I had to break away quickly in case he detected me")
                    self.nextStage(script)
        else:
            script.sendPlayerChat("Oh dear I don't have any money")
    
    def nextStage(self, script):
        script.sendNpcChat("Delrith", "Delrith is a powerful demon", "Oh I really hope he didn't see me", 
                                   "Looking at him through my crystal ball", "He tried to destroy this city 150 years ago",
                                   "He was stopped just in time, by the great hero Wally", "Wally managed to trap the demon",
                                   "In the stone circle just south of this city", "Using his magic sword silverlight", "Ye Gods",
                                   "Silverlight was the sword you were holding in the ball vision", 
                                   "You are the one destined to try and stop the demon this time")
        self.decreasingOptions(script, 3)
    
    def decreasingOptions(self, script, amount):
        options = 0
        if amount == 3:
            options = ["How am I meant to fight a demon who can destroy cities?", 
                    "Ok where is he? I'll kill him for you", "Wally doesn't sound like a very heroic name"]
        elif amount == 2:
            options = ["How am I meant to fight a demon who can destroy cities?", 
                    "Ok where is he? I'll kill him for you"]
        after_option = script.pickOption(options)
        if after_option == 0:
            script.sendNpcChat("I admit it won't be easy", "Wally managed to arrive at the stone circle",
                               "Just as Delrith was summoned by a cult of chaos druids", 
                               "By reciting the correct magical incantation", 
                               "and thrusting Silverlight into Delrith, while he was newly summoned", 
                               "Wally was able to imprison Delrith", "In the stone block in the centre of the circle",
                               "Delrith will come forth from the centre of the stone circle again",
                               "I would imagine an evil sorcerer is already starting on the ritual", "to summon Delrith as we speak")
            self.whichOne(script, 3)
        elif after_option == 1:
            script.sendNpcChat("Well you can't just go and fight him", "He can't be harmed by ordinary weapons", 
                               "Wally managed to arrive at the stone circle", "Just as Delrith was summoned by a cult of chaos druids", 
                               "By reciting the correct magical incantation", 
                               "and thrusting Silverlight into Delrith, while he was newly summoned", 
                               "Wally was able to imprison Delrith", "In the stone block in the centre of the circle",
                               "Delrith will come forth from the centre of the stone circle again",
                               "I would imagine an evil sorcerer is already starting on the ritual", "to summon Delrith as we speak")
            self.whichOne(script, 3)
        elif after_option == 2:
            script.sendNpcChat("Yes I know. Maybe that is why history doesn't remember him", "However he was a very great hero",
                               "Who knows how much pain and suffering", "Delrith would have brought forth without Wally to stop him",
                               "It looks like you are going to need to perform similar heroics")
            self.decreasingOptions(script, 2)
    
    def whichOne(self, script, choices):
        options = 0
        if choices == 3:
            options = ["What is the magical incantation?", "Where can I find silverlight?"]
        elif choices == 2:
            options = ["Ok thanks. I'll do my best to stop the Demon", "Where can I find silverlight?"]
        elif choices == 1:
            options = ["Ok thanks. I'll do my best to stop the Demon", "What is the magical incantation?"]
        next_option = script.pickOption(options)
        if next_option == 0 and choices == 3:
            self.getIncantationInfo(script)
            self.whichOne(script, 2)
        elif next_option == 0 and choices == 2 or next_option == 0 and choices == 1:
            script.sendNpcChat("Good luck, may guthix be with you")
            script.setQuestStage(1)
        elif next_option == 1 and choices == 3:
            self.getSilverlightInfo(script)
            self.whichOne(script, 1)
        elif next_option == 1 and choices == 2:
            self.getSilverlightInfo(script)
            self.whichOne(script, 1)
        elif next_option == 1 and choices == 1:
            self.getIncantationInfo(script)
            self.whichOne(script, 2)
    
    def getIncantationInfo(self, script):
        script.sendNpcChat("Oh yes let me think a second")
        script.displayMessage("The gypsy starts thinking")
        script.sleep(10000)
        script.sendNpcChat("Alright I think I've got it now I think", "It goes", "Carlem", "Aber", "Camerinthum",
                                   "Purchai", "Gabindo", "Have you got that?")
        script.sendPlayerChat("I think so, yes")
    
    def getSilverlightInfo(self, script):
        script.sendNpcChat("Silverlight has been passed down through Wally's descendants", 
                           "I believe it is currently in the care of one of the King's knights", "called Sir Prysin",
                           "He shouldn't be too hard to find he lives in the royal palace in this city", 
                           "Tell him gypsy Aris sent you")
    
    def getSilverlightKeyInfo(self, script):
        script.sendNpcChat("What do you need to find that for?")
        script.sendPlayerChat("I need it to fight Delrith")
        script.sendNpcChat("Delrith?", "I thought the world was rid of him")
        script.setQuestStage(2)
        option = script.pickOption(["Well, the gypsy's crystal ball seems to think otherwise", 
                                    "He's back and unfortunately I've got to deal with him"])
        if option == 0:
            script.sendNpcChat("Well if the ball says so, I'd better help you", "The problem is getting silverlight")
            self.continueKeyInfo(script)
        elif option == 1:
            script.sendNpcChat("You don't look up to much", "I suppose Silverlight may be good enough to carry you through",
                                  "The problem is getting silverlight")
            self.continueKeyInfo(script)       
    
    def continueKeyInfo(self, script):
        script.sendPlayerChat("You mean you don't have it?")
        script.sendNpcChat("Oh I do have it", "but it is so powerful", "That I have put it in a special box",
                                  "Which needs three different keys to open it", "That way, it won't fall into the wrong hands")
        sub_option = script.pickOption(["So give me the keys", "And why is this a problem?"])
        if sub_option == 0:
            script.sendNpcChat("Um", "ah", "well there's a problem")
            self.remindOfKeyInfo(script)
        elif sub_option == 1:
            self.remindOfKeyInfo(script)
    
    def remindOfKeyInfo(self, script):
        script.sendNpcChat("I kept one of the keys", "I gave the other two", "To other people for safe keeping",
                                  "One I gave to Rovin", "who is captain of the palace guard", 
                                  "I gave the other to the wizard Traiborn")
        self.moreKeyOptions(script, 3)
    
    def moreKeyOptions(self, script, choices):
        options = 0
        if choices == 4:
            options = ["Can you give me your key?", "Where does the wizard live?", "Well I'd better go key hunting"]
        elif choices == 3:
            options = ["Can you give me your key?", "Where can I find Captain Rovin?", "Where does the wizard live?"]
        elif choices == 2:
            options = ["So what does the drain lead to?", "Where can I find Captain Rovin?", "Where does the wizard live?"]
        elif choices == 1:
            options = ["Where can I find Captain Rovin?", "Where does the wizard live?", "Well I'd better go key hunting"]
        elif choices == 0:
            options = ["Can you give me your key?", "Where can I find Captain Rovin?", "Well I'd better go key hunting"]
        
        next_option = script.pickOption(options)
        if next_option == 0 and choices == 3 or next_option == 0 and choices == 4 or next_option == 0 and choices == 0:
            script.sendNpcChat("Um", "Ah", "Well there's a problem there as well", "I managed to drop the key in the drain", "just outside the palace kitchen",
                                  "it is just inside and I can't reach it")
            self.moreKeyOptions(script, 2)
        elif next_option == 0 and choices == 2:
            script.sendNpcChat("It is the drain", "For the drainpipe running from the sink in the kitchen",
                                  "Down to the palace sewers")
            self.moreKeyOptions(script, 1)
        elif next_option == 0 and choices == 1:
            script.sendNpcChat("Captain Rovin lives at the top of the guards quarters", "in the northwest wing of this place")
            self.moreKeyOptions(script, 4)
        elif next_option == 1 and choices == 3 or next_option == 1 and choices == 2 or next_option == 1 and choices == 0 or next_option == 1 and choices == 4:
            script.sendNpcChat("Captain Rovin lives at the top of the guards quarters", "in the northwest wing of this place")
            self.moreKeyOptions(script, 4)
        elif next_option == 1 and choices == 1:
            script.sendNpcChat("Wizard Traiborn?", "He is one of the wizards who lives in the tower",
                                  "On the little island just off the south coast", 
                                  "I believe his quarters are on the first floor of the tower")
            self.moreKeyOptions(script, 0)
        elif next_option == 2 and choices == 3 or next_option == 2 and choices == 2:
            script.sendNpcChat("Wizard Traiborn?", "He is one of the wizards who lives in the tower",
                                  "On the little island just off the south coast", 
                                  "I believe his quarters are on the first floor of the tower")
            self.moreKeyOptions(script, 0)
        elif next_option == 2 and choices == 0 or next_option == 2 and choices == 1 or next_option == 2 and choices == 4:
            script.sendNpcChat("Ok goodbye")
    
    def getWizardKey(self, script):
        script.sendNpcChat("Now you come to mention it - yes I do have a key", 
                            "It's in my special closet of valuable stuff", "Now how do I get into that?")
        script.displayMessage("The wizard scratches his head")
        script.sendNpcChat("I sealed it using one of my magical rituals", "so it would make sense that another ritual",
                            "Would open it again")
        script.displayMessage("The wizard beams")
        script.sendPlayerChat("So do you know which ritual to use?")
        script.sendNpcChat("Let me think a second")
        script.sleep(1000)
        script.sendNpcChat("Yes a simple drazier style ritual should suffice",
                            "Hmm", "Main problem with that is I'll need 25 set of bones",
                            "Now where am I going to get a hold of something like that")
        more_option = script.pickOption(["Hmm, that's too bad. I really need that key", "I'll get the bones for you"])
        if more_option == 0:
            script.sendNpcChat("Ah well sorry I couldn't be any more help")
        elif more_option == 1:
            script.sendNpcChat("Ooh that would be very good of you")
            script.sendPlayerChat("Ok I'll speak to you when I've got some bones")
            script.setQuestStage(3)
    
    def incantationTime(self, script, fightEvent):
        script.sendNpcChat("Now what was that incantation again?")
        option = script.pickOption(["Carlem Gabindo Purchai Zaree Camerinthum",
                                            "Purchai Aber Gabindo Carlem Camerinthum",
                                            "Purchai Camerinthum Aber Gabindo Carlem",
                                            "Carlem Aber Camerinthum Purchai Gabindo"])
        if option == 0:
            script.displayMessage("As you chant, Delrith is sucked towards the vortex", "Suddenly the vortex closes",
                                    "And Delrith is still here", "That was the wrong incantation")
            self.incantationTime(script)
        elif option == 1:
            script.displayMessage("As you chant, Delrith is sucked towards the vortex", "Suddenly the vortex closes",
                                    "And Delrith is still here", "That was the wrong incantation")
            self.incantationTime(script)
        elif option == 2:
            script.displayMessage("As you chant, Delrith is sucked towards the vortex", "Suddenly the vortex closes",
                                    "And Delrith is still here", "That was the wrong incantation")
            self.incantationTime(script)
        elif option == 3:
            fightEvent.setOpponentInvincible(False)
            script.setQuestStage(5)
    
    def blockPlayerAttackNpc(self, player, npc):
        return npc.getID() == self.DELRITH
    
    def blockPlayerKilledNpc(self, player, npc):
        return npc.getID() == self.DELRITH
    
    def blockInvUseOnObject(self, gameObj, item, player):
        x = gameObj.getX()
        y = gameObj.getY()
        return gameObj.getID() == self.KITCHEN_DRAIN and x == 117 and y == 461 and item.getID() == self.BUCKET_OF_WATER
    
    def blockObjectAction(self, gameObj, command, player):
        x = gameObj.getX()
        y = gameObj.getY()
        return command == "search" and gameObj.getID() == self.KITCHEN_DRAIN and x == 117 and y == 461
    
    def blockTalkToNpc(self, player, npc):
        return npc.getID() == self.GYPSY or npc.getID() == self.SIR_PRYSIN or npc.getID() == self.CAPTAIN_ROVIN or npc.getID() == self.WIZARD_TRAIBORN
