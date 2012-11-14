from org.darkquest.gs.plugins import Quest;
from org.darkquest.gs.plugins.listeners.action import TalkToNpcListener, ObjectActionListener, PlayerKilledNpcListener
from org.darkquest.gs.plugins.listeners.executive import TalkToNpcExecutiveListener, ObjectActionExecutiveListener, PlayerKilledNpcExecutiveListener

'''
@author: Mister Hat
Witch's potion F2P quest
'''
class WitchPotion(Quest, TalkToNpcListener, TalkToNpcExecutiveListener, ObjectActionListener, ObjectActionExecutiveListener, PlayerKilledNpcListener, PlayerKilledNpcExecutiveListener):
	INGREDIENTS = [270, 271, 241, 134]
	
	def getQuestId(self):
		return 15

	def getQuestName(self):
		return "Witch's Potion"

	def isMembers(self):
		return False

	def handleReward(self, player):
		script = player.getScriptHelper()
		
		script.setActiveQuest(self)
		script.displayMessage("Well done you have completed the witches potion quest")
		script.advanceStat(player.SkillType.MAGIC, 225 + 50 * script.getMaxLevel(player.SkillType.MAGIC))
		script.displayMessage("@gre@You have gained 1 quest point!")
		script.addQuestPoints(1)

	def onTalkToNpc(self, player, npc):	
		script = player.getScriptHelper()
		
		script.setActiveNpc(npc)
		script.setActiveQuest(self)
		stage = script.getQuestStage()
		
		script.occupy()
		
		if stage == 0:
			script.sendNpcChat("Greetings Traveller", "What could you want with an old woman like me?")
			option = script.pickOption(["I'm in search of a quest", "I heard that you are a witch"])

			if option == 0:
				script.sendNpcChat("Hmm maybe I can think of something for you", "Would you like to become more proficient in the dark arts?")
				option = script.pickOption(["Yes help me become one with my darker side", "No I have my principles and honour", "What you mean improve my magic?"], False)
				
				if option == 0:
					script.sendPlayerChat("Yes help me become one with my darker side")
					self.start_quest(script)
				elif option == 1:
					script.sendPlayerChat("No, I have principles and honour")
					script.sendNpcChat("Suit yourself, but you're missing out")
				elif option == 2:
					script.sendPlayerChat("What you mean improve my magic?")
					script.sendNpcChat("Yes improve your magic", "Do you have no sense of drama?")
					
					option = script.pickOption(["Yes I'd like to improve my magic", "No I'm not interested", "Show me the mysteries of the dark arts"])
					if option == 0:
						self.start_quest(script)
					elif option == 1:
						script.sendNpcChat("Many aren't to start off with", "But I think you'll be drawn back to this place")
					elif option == 2:
						self.start_quest(script)
			elif option == 1:
				script.sendNpcChat("Yes it does seem to be getting fairly common knowledge", "I fear I may get a visit from the witch hunters of Falador before long")
		elif stage == 1:
			has_ingredients = True
			
			script.sendNpcChat("Greetings Traveller", "So have you found the things for the potion")
			
			for item in self.INGREDIENTS:
				if not script.hasItem(item):
					has_ingredients = False
					
			if has_ingredients:
				script.sendPlayerChat("Yes I have everything")
				script.sendNpcChat("Excellent, can I have them then?")

				script.displayMessage("You pass the ingredients to Hetty", "Hetty puts all the ingredients in her cauldron", 0)
				
				for item in self.INGREDIENTS:
					script.removeItem(item, 1)
				
				script.sleep(2000)
				script.displayMessage("Hetty closes her eyes and begins to chant")
				script.sendNpcChat("Ok drink from the cauldron")
				
				script.setQuestStage(2)
			else:
				script.sendPlayerChat("No not yet")
				script.sendNpcChat("Well remember you need to get", "An eye of newt, a rat's tail,some burnt meat and an onion")
		elif stage == 2:
			script.sendNpcChat("Well are you going to drink the potion or not?")
		elif stage == -1:
			script.sendNpcChat("Greetings Traveller", "How's your magic coming along?")
			script.sendPlayerChat("I'm practicing and slowly getting better")
			script.sendNpcChat("good, good")
				
		script.release()
	
	def start_quest(self, script):
		script.sendNpcChat(
			"Ok I'm going to make a potion to help bring out your drarker self",
			"So that you can perform acts of dark magic with greater ease",
			"You will need certain ingredients"
		)

		script.sendPlayerChat("What do I need")
		script.sendNpcChat("You need an eye of newt, a rat's tail, an onion and a piece of burnt meat")
		script.setQuestStage(1)
		
		script.release()
	
	def onObjectAction(self, gameObject, command, player):
		script = player.getScriptHelper()
		stage = script.setActiveQuest(self)
		script.occupy()

		if stage == 2:
			script.displayMessage("You drink from the cauldron", "You feel yourself imbued with power")
			script.setQuestStage(-1)
			script.setQuestCompleted()
		else:
			script.sendPlayerChat("I'd rather not", "It doesn't look very tasty")
			
		script.release()
	
	def onPlayerKilledNpc(self, player, npc):
		script = player.getScriptHelper()
		script.setActiveQuest(self)
		
		if npc.getID() == 29 and script.getQuestStage() == 1:
			script.spawnItem(npc.getX(), npc.getY(), 271, 1)

	def blockTalkToNpc(self, player, npc):
		return npc.getID() == 148
	
	def blockObjectAction(self, gameObject, command, player):
		return gameObject.getID() == 147
	
	def blockPlayerKilledNpc(self, player, npc):
		return npc.getID() == 29
