from com.prsc.gs.plugins.phandler import PacketHandler
from com.prsc.gs.model import Player
from com.prsc.config import Formulae

from org.jboss.netty.channel import Channel

'''
@author: GORF
This is a generic template for a packet handler (always document what the handler is)
NOT COMPLETED
'''

class ChangedStatsHandler(PacketHandler):
	
	# NPCs USED
	MAX_LEVEL = 99
	
	def getAssociatedIdentifiers(self):
		return [114]
	
	def handlePacket(self, packet, session):
		player = session.getAttachment()
		
		attack = packet.readShort()
		defense = packet.readShort()
		strength = packet.readShort()
		#ranged_exp = packet.readInt()
		#prayer_exp = packet.readInt()
		#magic_exp = packet.readInt()
		
		try:
			real_exp = player.getExp(0) + player.getExp(1) + player.getExp(2)
			given_exp = self.getExperience(attack) + self.getExperience(defense) + self.getExperience(strength)
			
			if attack > player.getAttack(): 
				if real_exp >= self.getExperience(attack):
					if given_exp > player.getExp(0):
						needed_exp = self.getExperience(attack) - player.getExp(0)
						real_exp = real_exp - needed_exp
						print("Total exp will now be: " + str(real_exp))
				else:
					print("Not possible")
						
			elif attack < player.getAttack():
				if real_exp >= self.getExperience(attack):
					if given_exp > player.getExp(0):
						needed_exp = self.getExperience(attack) - player.getExp(0)
						real_exp = real_exp + needed_exp
						print("Total exp will now be: " + str(real_exp))
				else:
					print("Not possible")
			
			if defense > player.getDefense():
				if real_exp >= self.getExperience(defense):
					if given_exp > player.getExp(1):
						needed_exp = self.getExperience(defense) - player.getExp(1)
						real_exp = real_exp - needed_exp
						print("Total exp will now be: " + str(real_exp))
			
			if defense > player.getStrength():
				if real_exp >= self.getExperience(strength):
					if given_exp > player.getExp(2):
						needed_exp = self.getExperience(strength) - player.getExp(2)
						real_exp = real_exp - needed_exp
						print("Total exp will now be: " + str(real_exp))
			
			print("Attack exp: " + str(self.getExperience(attack)))
			print("Defense exp: " + str(self.getExperience(defense)))
			print("Strength exp: " + str(self.getExperience(strength)))
			
			print("Given exp: " + str(given_exp))
			print("Real exp: " + str(real_exp))
		except Exception, ex:
			print('exception caught2')
		
	def getExperience(self, level):
		if level == 1:
			return 0
		offset_level = level - 1
		if offset_level == 1:
			return 83
		return Formulae.leveToRealExperience(level)
		
