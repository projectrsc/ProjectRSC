from org.darkquest.gs.plugins import Quest
from org.darkquest.gs.plugins.listeners.action import TalkToNpcListener, ObjectActionListener, PlayerKilledNpcListener
from org.darkquest.gs.plugins.listeners.executive import TalkToNpcExecutiveListener, ObjectActionExecutiveListener, PlayerKilledNpcExecutiveListener

'''
@authour: Mister Hat
Imp Catcher F2P quest
'''

class ImpCatcher(Quest, TalkToNpcListener, TalkToNpcExecutiveListener):
	BEADS = [231, 232, 233, 234]
	
	def getQuestId(self):
		return 7
		
	def getQuestName(self):
		return "Imp catcher"
	
	def isMembers(self):
		return False
		
	def handleReward(self, player):
		script = player.getScriptHelper()
		script.setActiveQuest(self)
		
		script.displayMessage("Well done. You have completed the Imp catcher quest")
		script.advanceStat(player.SkillType.MAGIC, 875)
		script.displayMessage("@gre@You have gained 1 quest point!")
		script.addQuestPoints(1)
		
	def onTalkToNpc(self, player, npc):
		script = player.getScriptHelper()
		script.setActiveNpc(npc)
		script.setActiveQuest(self)
		stage = script.getQuestStage()
		
		script.occupy()
		
		if stage == 0:
			script.sendNpcChat("Hello there")
			option = script.pickOption(["Give me a quest!", "Most of your friends are pretty quiet aren't they?"])
			
			if option == 0:
				script.sendNpcChat("Give me a quest what?")
				
				option = script.pickOption(["Give me a quest please", "Give me a quest or else", "Just stop messing around and give me a quest"])
				if option == 0:
					script.sendNpcChat(
						"Well seeing as you asked nicely", "I could do with some help",
						"The wizard grayzag next door decided he didn't like me",
						"So he cast of spell of summoning", "And summoned hundreds of little imps",
						"These imps stole all sorts of my things", "Most of these things I don't really care about",
						"They're just eggs and balls of string and things",	"But they stole my 4 magical beads",
						"There was a red one, a yellow one, a black one and a white one",
						"These imps have now spread out all over the kingdom",
						"Could you get my beads back for me"
					)

					script.sendPlayerChat("I'll try")
					script.setQuestStage(1)
				elif option == 1:
					script.sendNpcChat("Or else what? You'll attack me?", "Hahaha")
				elif option == 2:
					script.sendNpcChat("Ah now you're just assuming I have one to give")
			elif option == 1:
				script.sendNpcChat("Yes they've mostly got their head in the clouds", "Thinking about magic")
		elif stage == 1:
			beads_found = 0
			
			for bead in self.BEADS:
				if script.hasItem(bead):
					beads_found += 1
			
			script.sendNpcChat("So how are you doing finding my beads?")

			if beads_found <= 3:
				script.sendPlayerChat(("I've not found any yet" if beads_found == 0 else "I have found some of your beads"))
				script.sendNpcChat("Come back when you have them all", "The four colours of beads I need", "Are red,yellow,black and white", "Go chase some imps")
			else:
				script.sendPlayerChat("I've got all four beads", "It was hard work I can tell you")
				script.sendNpcChat("Give them here and I'll sort out a reward")
				script.displayMessage("You give four coloured beads to Wizard Mizgog")
				script.sleep(2000)
				script.sendNpcChat("Here's you're reward then", "An amulet of accuracy")
				script.displayMessage("The Wizard hands you an amulet")
				
				for bead in self.BEADS:
					script.removeItem(bead, 1)
					
				script.addItem(235)
				
				script.sleep(2000)				
				script.setQuestStage(-1)
				script.setQuestCompleted()
		elif stage == -1:
			option = script.pickOption(["Got any more quests?", "Most of your friends are pretty quiet aren't they?"])
			
			if option == 0:
				script.sendNpcChat("No Everything is good with the world today")
			elif option == 1:
				script.sendNpcChat("Yes they've mostly got their head in the clouds", "Thinking about magic")
		
		script.release()
		
	def blockTalkToNpc(self, player, npc):
		return npc.getID() == 117
