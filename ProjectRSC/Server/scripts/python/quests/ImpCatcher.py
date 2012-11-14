from org.darkquest.gs.plugins import Quest;
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
		player.setActiveQuest(self)
		
		player.displayMessage("Well done. You have completed the Imp catcher quest")
		player.advanceStat(player.SkillType.MAGIC, 875)
		player.displayMessage("@gre@You have gained 1 quest point!")
		player.addQuestPoints(1)
		
	def onTalkToNpc(self, player, npc):
		player.setActiveNpc(npc)
		player.setActiveQuest(self)
		stage = player.getQuestStage()
		
		player.occupy()
		
		if stage == 0:
			player.sendNpcChat("Hello there")
			option = player.pickOption(["Give me a quest!", "Most of your friends are pretty quiet aren't they?"])
			
			if option == 0:
				player.sendNpcChat("Give me a quest what?")
				
				option = player.pickOption(["Give me a quest please", "Give me a quest or else", "Just stop messing around and give me a quest"])
				if option == 0:
					player.sendNpcChat(
						"Well seeing as you asked nicely", "I could do with some help",
						"The wizard grayzag next door decided he didn't like me",
						"So he cast of spell of summoning", "And summoned hundreds of little imps",
						"These imps stole all sorts of my things", "Most of these things I don't really care about",
						"They're just eggs and balls of string and things",	"But they stole my 4 magical beads",
						"There was a red one, a yellow one, a black one and a white one",
						"These imps have now spread out all over the kingdom",
						"Could you get my beads back for me"
					)

					player.sendPlayerChat("I'll try")
					player.setQuestStage(1)
				elif option == 1:
					player.sendNpcChat("Or else what? You'll attack me?", "Hahaha")
				elif option == 2:
					player.sendNpcChat("Ah now you're just assuming I have one to give")
			elif option == 1:
				player.sendNpcChat("Yes they've mostly got their head in the clouds", "Thinking about magic")
		elif stage == 1:
			beads_found = 0
			
			for bead in self.BEADS:
				if player.hasItem(bead):
					beads_found += 1
			
			player.sendNpcChat("So how are you doing finding my beads?")

			if beads_found <= 3:
				player.sendPlayerChat(("I've not found any yet" if beads_found == 0 else "I have found some of your beads"))
				player.sendNpcChat("Come back when you have them all", "The four colours of beads I need", "Are red,yellow,black and white", "Go chase some imps")
			else:
				player.sendPlayerChat("I've got all four beads", "It was hard work I can tell you")
				player.sendNpcChat("Give them here and I'll sort out a reward")
				player.displayMessage("You give four coloured beads to Wizard Mizgog")
				player.sleep(2000)
				player.sendNpcChat("Here's you're reward then", "An amulet of accuracy")
				player.displayMessage("The Wizard hands you an amulet")
				
				for bead in self.BEADS:
					player.removeItem(bead, 1)
					
				player.addItem(235)
				
				player.sleep(2000)				
				player.setQuestStage(-1)
				player.setQuestCompleted()
		elif stage == -1:
			option = player.pickOption(["Got any more quests?", "Most of your friends are pretty quiet aren't they?"])
			
			if option == 0:
				player.sendNpcChat("No Everything is good with the world today")
			elif option == 1:
				player.sendNpcChat("Yes they've mostly got their head in the clouds", "Thinking about magic")
		
		player.release()
		
	def blockTalkToNpc(self, player, npc):
		return npc.getID() == 117
