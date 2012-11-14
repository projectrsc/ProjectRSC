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
		player.setActiveQuest(self)
		
		player.displayMessage("Well done you have completed the witches potion quest")
		player.advanceStat(player.SkillType.MAGIC, 225 + 50 * player.getMaxLevel(player.SkillType.MAGIC))
		player.displayMessage("@gre@You have gained 1 quest point!")
		player.addQuestPoints(1)

	def onTalkToNpc(self, player, npc):	
		player.setActiveNpc(npc)
		player.setActiveQuest(self)
		stage = player.getQuestStage()
		
		player.occupy()
		
		if stage == 0:
			player.sendNpcChat("Greetings Traveller", "What could you want with an old woman like me?")
			option = player.pickOption(["I'm in search of a quest", "I heard that you are a witch"])

			if option == 0:
				player.sendNpcChat("Hmm maybe I can think of something for you", "Would you like to become more proficient in the dark arts?")
				option = player.pickOption(["Yes help me become one with my darker side", "No I have my principles and honour", "What you mean improve my magic?"], False)
				
				if option == 0:
					player.sendPlayerChat("Yes help me become one with my darker side")
					self.start_quest(player)
				elif option == 1:
					player.sendPlayerChat("No, I have principles and honour")
					player.sendNpcChat("Suit yourself, but you're missing out")
				elif option == 2:
					player.sendPlayerChat("What you mean improve my magic?")
					player.sendNpcChat("Yes improve your magic", "Do you have no sense of drama?")
					
					option = player.pickOption(["Yes I'd like to improve my magic", "No I'm not interested", "Show me the mysteries of the dark arts"])
					if option == 0:
						self.start_quest(player)
					elif option == 1:
						player.sendNpcChat("Many aren't to start off with", "But I think you'll be drawn back to this place")
					elif option == 2:
						self.start_quest(player)
			elif option == 1:
				player.sendNpcChat("Yes it does seem to be getting fairly common knowledge", "I fear I may get a visit from the witch hunters of Falador before long")
		elif stage == 1:
			has_ingredients = True
			
			player.sendNpcChat("Greetings Traveller", "So have you found the things for the potion")
			
			for item in self.INGREDIENTS:
				if not player.hasItem(item):
					has_ingredients = False
					
			if has_ingredients:
				player.sendPlayerChat("Yes I have everything")
				player.sendNpcChat("Excellent, can I have them then?")

				player.displayMessage("You pass the ingredients to Hetty", "Hetty puts all the ingredients in her cauldron", 0)
				
				for item in self.INGREDIENTS:
					player.removeItem(item, 1)
				
				player.sleep(2000)
				player.displayMessage("Hetty closes her eyes and begins to chant")
				player.sendNpcChat("Ok drink from the cauldron")
				
				player.setQuestStage(2)
			else:
				player.sendPlayerChat("No not yet")
				player.sendNpcChat("Well remember you need to get", "An eye of newt, a rat's tail,some burnt meat and an onion")
		elif stage == 2:
			player.sendNpcChat("Well are you going to drink the potion or not?")
		elif stage == -1:
			player.sendNpcChat("Greetings Traveller", "How's your magic coming along?")
			player.sendPlayerChat("I'm practicing and slowly getting better")
			player.sendNpcChat("good, good")
				
		player.release()
	
	def start_quest(self, player):
		player.sendNpcChat(
			"Ok I'm going to make a potion to help bring out your drarker self",
			"So that you can perform acts of dark magic with greater ease",
			"You will need certain ingredients"
		)

		player.sendPlayerChat("What do I need")
		player.sendNpcChat("You need an eye of newt, a rat's tail, an onion and a piece of burnt meat")
		player.setQuestStage(1)
		
		player.release()
	
	def onObjectAction(self, gameObject, command, player):
		stage = player.setActiveQuest(self)
		player.occupy()

		if stage == 2:
			player.displayMessage("You drink from the cauldron", "You feel yourself imbued with power")
			player.setQuestStage(-1)
			player.setQuestCompleted()
		else:
			player.sendPlayerChat("I'd rather not", "It doesn't look very tasty")
			
		player.release()
	
	def onPlayerKilledNpc(self, player, npc):
		player.setActiveQuest(self)
		
		if npc.getID() == 29 and player.getQuestStage() == 1:
			player.spawnItem(npc.getX(), npc.getY(), 271, 1)

	def blockTalkToNpc(self, player, npc):
		return npc.getID() == 148
	
	def blockObjectAction(self, gameObject, command, player):
		return gameObject.getID() == 147
	
	def blockPlayerKilledNpc(self, player, npc):
		return npc.getID() == 29
