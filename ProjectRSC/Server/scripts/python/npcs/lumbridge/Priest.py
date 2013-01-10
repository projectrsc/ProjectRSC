from com.prsc.gs.plugins.listeners.action import TalkToNpcListener
from com.prsc.gs.plugins.listeners.executive import TalkToNpcExecutiveListener
from com.prsc.gs.plugins import PlugInterface

'''
@author: GORF
This is a generic template for the priest at Lumbridge
'''

class Priest(PlugInterface, TalkToNpcListener, TalkToNpcExecutiveListener):
	
	# NPCs USED
	PRIEST = 9
	
	def onTalkToNpc(self, player, npc):
		script = player.getScriptHelper()
		
		script.setActiveNpc(npc)
		script.occupy()
		
		# START YOUR CODE HERE
		script.sendNpcChat("Welcome to the church of holy saradomin")		
		option = script.pickOption(["Who's Saradomin?", "Nice place you've got here", "I'm looking for a quest"])
		if option == 0:
			script.sendNpcChat("Surely you have heard of the God, Saradomin?",
							"He who creates the forces of goodness and purity in this world?",
							"I cannot believe your ignorance", "This is the god with more followers than any other!",
							"At least in these parts!", "He who along with his brothers Guthix and Zamorak created this")
			sub_option = script.pickOption(["Oh that Saradomin", "Oh sorry i'm not from this world"])
			if sub_option == 0:
				script.sendNpcChat("There is only one saradomin")
			if sub_option == 1:
				script.sendNpcChat("That's strange", "I thought things from this world were all slime and tenticles")
				more_options = script.pickOption(["You don't understand. This is a computer game", "I am - do you like my disguise?"])
				if more_options == 0:
					script.sendNpcChat("I beg your pardon?")
					script.sendPlayerChat("Never mind")
				elif more_options == 1:
					script.sendNpcChat("Aargh begone foul creature from another dimension")
					script.sendPlayerChat("Ok, ok, it was a joke")
		elif option == 1:
			script.sendNpcChat("It is, isn't it?", "It was built 230 years ago")
		elif option == 2:
			script.sendNpcChat("Sorry, no quest here") #temporary
		#END YOUR CODE HERE
		script.release()
	
	def blockTalkToNpc(self, player, npc):
		return npc.getID() == self.PRIEST # THE NPC WE ARE GOING TO HANDLE
