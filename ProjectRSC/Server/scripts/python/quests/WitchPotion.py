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

	def handleReward(self):
		self.displayMessage("Well done you have completed the witches potion quest")
		self.advanceStat(self.SkillType.MAGIC, 225 + 50 * self.getMaxLevel(self.SkillType.MAGIC))
		self.displayMessage("@gre@You have gained 1 quest point!")
		self.addQuestPoints(1)

	def onTalkToNpc(self, player, npc):	
		self.setParticipants(player, npc)
		stage = self.getQuestStage()
		
		self.occupy()
		
		if stage == 0:
			self.sendNpcChat("Greetings Traveller", "What could you want with an old woman like me?")
			option = self.pickOption(["I'm in search of a quest", "I heard that you are a witch"])

			if option == 0:
				self.sendNpcChat("Hmm maybe I can think of something for you", "Would you like to become more proficient in the dark arts?")
				option = self.pickOption(["Yes help me become one with my darker side", "No I have my principles and honour", "What you mean improve my magic?"], False)
				
				if option == 0:
					self.sendPlayerChat("Yes help me become one with my darker side")
					self.start_quest()
				elif option == 1:
					self.sendPlayerChat("No, I have principles and honour")
					self.sendNpcChat("Suit yourself, but you're missing out")
				elif option == 2:
					self.sendPlayerChat("What you mean improve my magic?")
					self.sendNpcChat("Yes improve your magic", "Do you have no sense of drama?")
					
					option = self.pickOption(["Yes I'd like to improve my magic", "No I'm not interested", "Show me the mysteries of the dark arts"])
					if option == 0:
						self.start_quest()
					elif option == 1:
						self.sendNpcChat("Many aren't to start off with", "But I think you'll be drawn back to this place")
					elif option == 2:
						self.start_quest()
			elif option == 1:
				self.sendNpcChat("Yes it does seem to be getting fairly common knowledge", "I fear I may get a visit from the witch hunters of Falador before long")
		elif stage == 1:
			has_ingredients = True
			
			self.sendNpcChat("Greetings Traveller", "So have you found the things for the potion")
			
			for item in self.INGREDIENTS:
				if not self.hasItem(item):
					has_ingredients = False
					
			if has_ingredients:
				self.sendPlayerChat("Yes I have everything")
				self.sendNpcChat("Excellent, can I have them then?")

				self.displayMessage("You pass the ingredients to Hetty", "Hetty puts all the ingredients in her cauldron", 0)
				
				for item in self.INGREDIENTS:
					self.removeItem(item, 1)
				
				self.sleep(2000)
				self.displayMessage("Hetty closes her eyes and begins to chant")
				self.sendNpcChat("Ok drink from the cauldron")
				
				self.setQuestStage(2)
			else:
				self.sendPlayerChat("No not yet")
				self.sendNpcChat("Well remember you need to get", "An eye of newt, a rat's tail,some burnt meat and an onion")
		elif stage == 2:
			self.sendNpcChat("Well are you going to drink the potion or not?")
		elif stage == -1:
			self.sendNpcChat("Greetings Traveller", "How's your magic coming along?")
			self.sendPlayerChat("I'm practicing and slowly getting better")
			self.sendNpcChat("good, good")
				
		self.release()
	
	def start_quest(self):
		self.sendNpcChat(
			"Ok I'm going to make a potion to help bring out your drarker self",
			"So that you can perform acts of dark magic with greater ease",
			"You will need certain ingredients"
		)

		self.sendPlayerChat("What do I need")
		self.sendNpcChat("You need an eye of newt, a rat's tail, an onion and a piece of burnt meat")
		self.setQuestStage(1)
		
		self.release()
	
	def onObjectAction(self, gameObject, command, player):
		self.setParticipant(player)
		self.occupy()
		
		if self.getQuestStage() == 2:
			self.displayMessage("You drink from the cauldron", "You feel yourself imbued with power")
			self.setQuestStage(-1)
			self.setQuestCompleted()
		else:
			self.sendPlayerChat("I'd rather not", "It doesn't look very tasty")
			
		self.release()
	
	def onPlayerKilledNpc(self, player, npc):
		self.setParticipant(player)
		
		if npc.getID() == 29 and self.getQuestStage() == 1:
			self.spawnItem(npc.getX(), npc.getY(), 271, 1)

	def blockTalkToNpc(self, player, npc):
		return npc.getID() == 148
	
	def blockObjectAction(self, gameObject, command, player):
		return gameObject.getID() == 147
	
	def blockPlayerKilledNpc(self, player, npc):
		return npc.getID() == 29
