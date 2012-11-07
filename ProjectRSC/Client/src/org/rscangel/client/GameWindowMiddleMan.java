package org.rscangel.client;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.io.IOException;
import java.math.BigInteger;

import org.rscangel.client.stream.StreamClass;
import org.rscangel.client.util.Config;
import org.rscangel.client.util.DataConversions;

public abstract class GameWindowMiddleMan extends GameWindow {

	private static final long serialVersionUID = 3365932367768513685L;
	
	public final void login(String user, String pass, boolean reconnecting) {
		if (socketTimeout > 0) {
			loginScreenPrint("Please wait...", "Connecting to server");
			try {
				Thread.sleep(2000L);
			}
			catch(Exception _ex) { }
			loginScreenPrint("Sorry! The server is currently full.", "Please try again later");
			return;
		}
		
		try {
			username = user;
			user = DataOperations.formatString(user, 20);
			password = pass;
			pass = DataOperations.formatString(pass, 20);
			
			if (user.trim().length() == 0) {
				loginScreenPrint("You must enter both a username", "and a password - Please try again");
				return;
			}
			
			if(reconnecting)
				gameBoxPrint("Connection lost! Please wait...", "Attempting to re-establish");
			else
				loginScreenPrint("Please wait...", "Connecting to server");
			
			streamClass = new StreamClass(makeSocket(Config.SERVER_IP, Config.SERVER_PORT), this);
			streamClass.maxPacketReadCount = maxPacketReadCount;
			long l = DataOperations.nameToHash(user);
			streamClass.createPacket(32);
			streamClass.addByte((int)(l >> 16 & 31L));
			streamClass.flush();
			long sessionId = streamClass.readLong();

			if(sessionId == 0L) {
				loginScreenPrint("Login server offline.", "Please try again in a few mins");
				return;
			}
			System.out.println("Session id: " + sessionId);
			int sessionKeys[] = new int[4];
			sessionKeys[0] = (int)(Math.random() * 99999999D);
			sessionKeys[1] = (int)(Math.random() * 99999999D);
			sessionKeys[2] = (int)(sessionId >> 32);
			sessionKeys[3] = (int)sessionId;
			LoginDataEncryption dataEnc = new LoginDataEncryption(new byte[117]);
			dataEnc.addByte(reconnecting ? 1 : 0);
			dataEnc.addInt(Config.CLIENT_VERSION);
			dataEnc.addInt(sessionKeys[0]);
			dataEnc.addInt(sessionKeys[1]);
			dataEnc.addInt(sessionKeys[2]);
			dataEnc.addInt(sessionKeys[3]);
			dataEnc.addString(user);
			dataEnc.addString(pass);
			byte data[] = dataEnc.encrypt(dataEnc.packet);
			streamClass.createPacket(77);
			streamClass.addBytes(data, 0, data.length);
			streamClass.flush();
			int loginCode = streamClass.read();
			System.out.println("login response:" + loginCode);
			if(loginCode == 99) {
				reconnectTries = 0;
				resetVars();
				return;
			}
			if(loginCode == 0) {
				reconnectTries = 0;
				resetVars();
				return;
			}
			if(loginCode == 1) {
				reconnectTries = 0;
				return;
			}
			if(reconnecting) {
				user = "";
				pass = "";
				resetIntVars();
				return;
			}
			if(loginCode == -1) {
				loginScreenPrint("Error unable to login.", "Server timed out");
				return;
			}
			if(loginCode == 2) {
				loginScreenPrint("Invalid username or password.", "Try again, or create a new account");
				return;
			}
			if(loginCode == 3) {
				loginScreenPrint("That username is already logged in.", "Wait 60 seconds then retry");
				return;
			}
			if(loginCode == 4) {
				loginScreenPrint("The client has been updated.", "Please restart the client");
				return;
			}
			if(loginCode == 5) {
				loginScreenPrint("Error unable to login.", "Please retry");
				return;
			}
			if(loginCode == 6) {
				loginScreenPrint("Account banned.", "Appeal on the forums, ASAP");
				return;
			}
			if(loginCode == 7) {
				loginScreenPrint("Error - failed to decode profile.", "Please contact the staff");
				return;
			}
			if(loginCode == 8) {
				loginScreenPrint("Too many connections from your IP.", "Please try again later");
				return;
			}
			if(loginCode == 9) {
				loginScreenPrint("Account already in use.", "You may only login to one character at a time");
				return;
			}
			if (loginCode == 10){
				loginScreenPrint("You need a members account", "to login to this world");
				return;
			} else {
				loginScreenPrint("Error unable to login.", "Unrecognised response code");
				return;
			}
		}
		catch(Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		if(reconnectTries > 0) {
			try {
				Thread.sleep(2500L);
			}
			catch(Exception _ex) { }
			reconnectTries--;
			login(username, password, reconnecting);
		}
		if(reconnecting) {
			username = "";
			password = "";
			resetIntVars();
		} else {
			loginScreenPrint("Sorry! Unable to connect.", "Check internet settings or try another world");
		}
	}

	protected final void requestLogout() {
		if(streamClass != null)
			try {
				streamClass.createPacket(39);
				streamClass.flush();
			}
		catch(IOException _ex) { }
		username = "";
		password = "";
		resetIntVars();
		loginScreenPrint("Please enter your usename and password", "");
	}

	protected void lostConnection() {
		System.out.println("Lost connection");
		login(username, password, true);
		loginScreenPrint("Please enter your usename and password", "");
	}

	protected final void gameBoxPrint(String s1, String s2) {
		Graphics g = getGraphics();
		Font font = new Font("Helvetica", 1, 15);
		char c = '\u0200';
		char c1 = '\u0158';
		g.setColor(Color.black);
		g.fillRect(c / 2 - 140, c1 / 2 - 25, 280, 50);
		g.setColor(Color.white);
		g.drawRect(c / 2 - 140, c1 / 2 - 25, 280, 50);
		drawString(g, s1, font, c / 2, c1 / 2 - 10);
		drawString(g, s2, font, c / 2, c1 / 2 + 10);
	}

	protected final void sendPingPacket() {
		long l = System.currentTimeMillis();
		if(streamClass.hasData())
			lastPing = l;
		if(l - lastPing > 5000L) {
			lastPing = l;
			streamClass.createPacket(5);
			streamClass.formatPacket();
		}
		try {
			streamClass.writePacket(20);
		}
		catch(IOException _ex) {
			lostConnection();
			return;
		}
		int packetLength = streamClass.readPacket(packetData);
		if(packetLength > 0)
			checkIncomingPacket(packetData[0] & 0xff, packetLength);
	}


	protected final void sendLogoutPacket() {
		if (streamClass != null) {
			try {
				streamClass.createPacket(39);
				streamClass.finalisePacket();
			} catch (IOException ioe) {
			}
		}

		username = "";
		password = "";

		resetIntVars();
		loginScreenPrint("Please enter your username and password", "");


	}

	protected final void checkIncomingPacket(int command, int length) {
		try {
			if (command == 48) {
				String s = new String(packetData, 1, length - 1);
				handleServerMessage(s);
				return;
			}
			if (command == 222) {
				sendLogoutPacket();

			}
			if (command == 136) {
				cantLogout();
				return;
			}
			if (command == 249) {
				friendsCount = DataOperations.getUnsignedByte(packetData[1]);
				for (int k = 0; k < friendsCount; k++) {
					friendsListLongs[k] = DataOperations.getUnsigned8Bytes(
							packetData, 2 + k * 9);
					friendsListOnlineStatus[k] = DataOperations
							.getUnsignedByte(packetData[10 + k * 9]);
				}

				reOrderFriendsListByOnlineStatus();
				return;
			}
			if (command == 25) {
				long friend = DataOperations.getUnsigned8Bytes(packetData, 1);
				int status = packetData[9] & 0xff;
				for (int i2 = 0; i2 < friendsCount; i2++)
					if (friendsListLongs[i2] == friend) {
						if (friendsListOnlineStatus[i2] == 0 && status != 0)
							handleServerMessage("@pri@"
									+ DataOperations.longToString(friend)
									+ " has logged in");
						if (friendsListOnlineStatus[i2] != 0 && status == 0)
							handleServerMessage("@pri@"
									+ DataOperations.longToString(friend)
									+ " has logged out");
						friendsListOnlineStatus[i2] = status;
						length = 0;
						reOrderFriendsListByOnlineStatus();
						return;
					}

				friendsListLongs[friendsCount] = friend;
				friendsListOnlineStatus[friendsCount] = status;
				friendsCount++;
				reOrderFriendsListByOnlineStatus();
				return;
			}
			if (command == 2) {
				ignoreListCount = DataOperations.getUnsignedByte(packetData[1]);
				for (int i1 = 0; i1 < ignoreListCount; i1++) {
					ignoreListLongs[i1] = DataOperations.getUnsigned8Bytes(
							packetData, 2 + i1 * 8);
				}
				return;
			}
			if (command == 158) {
				blockChatMessages = packetData[1];
				blockPrivateMessages = packetData[2];
				blockTradeRequests = packetData[3];
				blockDuelRequests = packetData[4];
				return;
			}
			if (command == 170) {
				long user = DataOperations.getUnsigned8Bytes(packetData, 1);
				String s1 = DataConversions.byteToString(packetData, 9,
						length - 9);
				handleServerMessage("@pri@" + DataOperations.longToString(user)
						+ " tells you: " + s1);
				return;
			} else {
				handleIncomingPacket(command, length, packetData);
				return;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private final void reOrderFriendsListByOnlineStatus() {
		boolean flag = true;
		while (flag) {
			flag = false;
			for (int i = 0; i < friendsCount - 1; i++)
				if (friendsListOnlineStatus[i] < friendsListOnlineStatus[i + 1]) {
					int j = friendsListOnlineStatus[i];
					friendsListOnlineStatus[i] = friendsListOnlineStatus[i + 1];
					friendsListOnlineStatus[i + 1] = j;
					long l = friendsListLongs[i];
					friendsListLongs[i] = friendsListLongs[i + 1];
					friendsListLongs[i + 1] = l;
					flag = true;
				}

		}
	}

	protected final void sendUpdatedPrivacyInfo(int chatMessages,
			int privateMessages, int tradeRequests, int duelRequests) {
		streamClass.createPacket(176);
		streamClass.addByte(chatMessages);
		streamClass.addByte(privateMessages);
		streamClass.addByte(tradeRequests);
		streamClass.addByte(duelRequests);
		streamClass.formatPacket();
	}

	protected final void addToIgnoreList(String s) {
		long l = DataOperations.stringLength12ToLong(s);
		streamClass.createPacket(25);
		streamClass.addTwo4ByteInts(l);
		streamClass.formatPacket();
		for (int i = 0; i < ignoreListCount; i++)
			if (ignoreListLongs[i] == l)
				return;

		if (ignoreListCount >= ignoreListLongs.length - 1) {
			return;
		} else {
			ignoreListLongs[ignoreListCount++] = l;
			return;
		}
	}

	protected final void removeFromIgnoreList(long l) {
		streamClass.createPacket(108);
		streamClass.addTwo4ByteInts(l);
		streamClass.formatPacket();
		for (int i = 0; i < ignoreListCount; i++)
			if (ignoreListLongs[i] == l) {
				ignoreListCount--;
				for (int j = i; j < ignoreListCount; j++)
					ignoreListLongs[j] = ignoreListLongs[j + 1];

				return;
			}

	}

	protected final void addToFriendsList(String s) {
		streamClass.createPacket(168);
		streamClass.addTwo4ByteInts(DataOperations.stringLength12ToLong(s));
		streamClass.formatPacket();
		long l = DataOperations.stringLength12ToLong(s);
		for (int i = 0; i < friendsCount; i++)
			if (friendsListLongs[i] == l)
				return;

		if (friendsCount >= friendsListLongs.length - 1) {
			return;
		} else {
			friendsListLongs[friendsCount] = l;
			friendsListOnlineStatus[friendsCount] = 0;
			friendsCount++;
			return;
		}
	}

	protected final void removeFromFriends(long l) {
		streamClass.createPacket(52);
		streamClass.addTwo4ByteInts(l);
		streamClass.formatPacket();
		for (int i = 0; i < friendsCount; i++) {
			if (friendsListLongs[i] != l)
				continue;
			friendsCount--;
			for (int j = i; j < friendsCount; j++) {
				friendsListLongs[j] = friendsListLongs[j + 1];
				friendsListOnlineStatus[j] = friendsListOnlineStatus[j + 1];
			}

			break;
		}

		handleServerMessage("@pri@" + DataOperations.longToString(l)
				+ " has been removed from your friends list");
	}

	protected final void sendPrivateMessage(long user, byte message[],
			int messageLength) {
		streamClass.createPacket(254);
		streamClass.addTwo4ByteInts(user);
		streamClass.addBytes(message, 0, messageLength);
		streamClass.formatPacket();
	}

	protected final void sendChatMessage(byte abyte0[], int i) {
		streamClass.createPacket(145);
		streamClass.addBytes(abyte0, 0, i);
		streamClass.formatPacket();
	}

	protected final void sendChatString(String s) {
		streamClass.createPacket(90);
		streamClass.addString(s);
		streamClass.formatPacket();
	}

	protected abstract void loginScreenPrint(String s, String s1);

	protected abstract void resetVars();

	protected abstract void resetIntVars();

	protected abstract void cantLogout();

	protected abstract void handleIncomingPacket(int command, int length,
			byte[] abyte0);

	protected abstract void handleServerMessage(String s);

	public GameWindowMiddleMan() {
		username = "";
		password = "";
		packetData = new byte[10000];
		friendsListLongs = new long[400];
		friendsListOnlineStatus = new int[400];
		ignoreListLongs = new long[200];
	}

	public static int maxPacketReadCount;
	String username;
	String password;
	public StreamClass streamClass;
	protected byte[] packetData;
	int reconnectTries;
	long lastPing;
	public int friendsCount;
	public long[] friendsListLongs;
	public int[] friendsListOnlineStatus;
	public int ignoreListCount;
	public long[] ignoreListLongs;
	public int blockChatMessages;
	public int blockPrivateMessages;
	public int blockTradeRequests;
	public int blockDuelRequests;
	private static BigInteger key = new BigInteger(
			"1370158896620336158431733257575682136836100155721926632321599369132092701295540721504104229217666225601026879393318399391095704223500673696914052239029335");
	private static BigInteger modulus = new BigInteger(
			"1549611057746979844352781944553705273443228154042066840514290174539588436243191882510185738846985723357723362764835928526260868977814405651690121789896823");
	public int socketTimeout;

}
