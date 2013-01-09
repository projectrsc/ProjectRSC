from com.prsc.gs.plugins.listeners.action import ObjectActionListener
from com.prsc.gs.plugins.listeners.executive import ObjectActionExecutiveListener
from com.prsc.config import Constants
from com.prsc.gs.external import EntityHandler, ItemDef, AgilityDef
from com.prsc.gs.plugins import PlugInterface

'''
@author: GORF
Agility skill 691,502
'''
class Agility(PlugInterface, ObjectActionListener, ObjectActionExecutiveListener):
	
	# KNOWN COMMANDS
	VALID_COMMANDS = ["balance on", "climb", "climb up", "grab hold of", "climb down", "enter"]
	
	# GNOME OBSTACLE COURSE EXP
	EXP_DISTRIBUTION = {655:7.5, 647:7.5, 653:7.5, 648:7.5, 649:7.5, 650:7.5, 654:7.5}
	
	# KNOWN OBSTACLE OBJECTS
	LOG = 655
	NET_WALL = 647
	SECOND_NET_WALL = 653
	WATCH_TOWER = 648
	LANDING_SIDE_OF_WATCH_TOWER = 649
	ROPESWING = 650
	PIPE = 654
	
	# KNOWN NPCS
	GNOME_TRAINER = 576 
	
	def onObjectAction(self, gameObj, command, player):
		script = player.getScriptHelper()
		obj_name = gameObj.getGameObjectDef().getName().lower()
		obj_id = gameObj.getID()
		
		if not player.withinRange(gameObj, 1):
			return
		
		if player.getFatigue() >= 7500:
			script.displayMessage("You are too tired to train")
			return

		script.occupy()		
		# GNOME OBSTACLE COURSE
		if command == "balance on" and obj_id == self.LOG:  #642
			script.movePlayer(692, 494, False)
			script.sleep(500)
			script.displayMessage("You stand on the slippery " + obj_name)
			script.movePlayer(692, 496, False)
			script.sleep(300)
			script.movePlayer(692, 498, False)
			script.displayMessage("and walk across")
			script.sleep(300)
			script.movePlayer(692, 499, False)
		
		elif command == "climb" and obj_id == self.NET_WALL:
			gnome_trainer = script.closestNpc(self.GNOME_TRAINER, 10)
			if gnome_trainer != None:
				script.setActiveNpc(gnome_trainer)
				script.sendNpcChat("Move it, move it, move it")
			script.displayMessage("You climb the net")
			script.sleep(2000)
			script.movePlayer(692, 1448, False)
			script.displayMessage("and pull yourself onto the platform")
		
		elif command == "climb up" and obj_id == self.WATCH_TOWER:
			gnome_trainer = script.closestNpc(self.GNOME_TRAINER, 10)
			if gnome_trainer != None:
				script.setActiveNpc(gnome_trainer)
				script.sendNpcChat("that's it, straight up, no messing around")
				script.sleep(2000)
			script.displayMessage("You pull yourself up the tree")
			script.sleep(1000)
			script.movePlayer(693, 2394, False)
			script.displayMessage("to the platform above")
		
		elif command == "grab hold of" and obj_id == self.ROPESWING:
			script.displayMessage("You reach out and grab the rope swing")
			script.sleep(1000)
			script.displayMessage("You hold on tight")
			script.sleep(2000)
			script.movePlayer(685, 2396, False)
			script.displayMessage("and swing to the opposite platform")
			
		elif command == "climb down" and obj_id == self.LANDING_SIDE_OF_WATCH_TOWER:
			script.displayMessage("You hang down from the tower")
			script.sleep(1000)
			script.movePlayer(683, 506, False)
			script.displayMessage("and drop to the floor")
			script.sendPlayerChat("ooof")
			
		elif command == "climb" and obj_id == self.SECOND_NET_WALL:
			gnome_trainer = script.closestNpc(self.GNOME_TRAINER, 8)
			if gnome_trainer != None:
				script.setActiveNpc(gnome_trainer)
				script.sendNpcChat("my granny can move faster than you")
			script.displayMessage("You take a few steps back")
			initial_y = player.getY()
			script.movePlayer(player.getX(), initial_y, False)
			initial_y = initial_y + 2
			script.movePlayer(player.getX(), initial_y, False)
			script.displayMessage("and run towards the net")
			initial_y = initial_y - 2
			script.movePlayer(player.getX(), initial_y, False)
			initial_y = initial_y - 2
			script.movePlayer(player.getX(), initial_y, False)
			
		elif command == "enter" and obj_id == self.PIPE:
			script.displayMessage("You squeeze through the pipe", "and shuffle down into it")
			script.sleep(2500)
			script.movePlayer(683, 494, False)
			gnome_trainer = script.closestNpc(self.GNOME_TRAINER, 4)
			if gnome_trainer != None:
				script.setActiveNpc(gnome_trainer)
				script.sendNpcChat("that's the way, well done")
		
		script.advanceStat(player.SkillType.AGILITY, self.EXP_DISTRIBUTION[obj_id], True)
		script.release()
         	
	def blockObjectAction(self, gameObj, command, player):
		if player.canAccessMembers():
			for commandStr in self.VALID_COMMANDS:
				if commandStr == command and self.EXP_DISTRIBUTION.has_key(gameObj.getID()):
					return True
		return False
