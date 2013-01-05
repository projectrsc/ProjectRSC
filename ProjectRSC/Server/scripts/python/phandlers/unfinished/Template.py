from com.prsc.gs.plugins.phandler import PacketHandler
from org.jboss.netty.channel import Channel

'''
@author: GORF
This is a generic template for an NPC handler (always document what the handler is)
'''

class Template(PacketHandler):
	
	# NPCs USED
	MY_NPC = -1
	
	def getAssociatedIdentifiers(self):
		return [114]
	
	def handlePacket(self, packet, session):
		id = packet.getID()
