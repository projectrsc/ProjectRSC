package com.prsc.gs.model;

import java.net.InetSocketAddress;







import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

import org.jboss.netty.channel.Channel;

import com.prsc.config.Constants;
import com.prsc.config.Formulae;
import com.prsc.gs.builders.MiscPacketBuilder;
import com.prsc.gs.builders.ls.SavePacketBuilder;
import com.prsc.gs.connection.LSPacket;
import com.prsc.gs.connection.RSCPacket;
import com.prsc.gs.core.GameEngine;
import com.prsc.gs.event.DelayedEvent;
import com.prsc.gs.event.impl.RangeEvent;
import com.prsc.gs.external.AgilityCourseDef;
import com.prsc.gs.external.EntityHandler;
import com.prsc.gs.external.PrayerDef;
import com.prsc.gs.model.component.mob.Scriptable;
import com.prsc.gs.model.component.mob.player.Bank;
import com.prsc.gs.model.component.mob.player.Inventory;
import com.prsc.gs.model.component.mob.player.Appearance;
import com.prsc.gs.model.component.world.Area;
import com.prsc.gs.plugins.PluginHandler;
import com.prsc.gs.plugins.Quest;
import com.prsc.gs.plugins.QuestInterface;
import com.prsc.gs.plugins.phandler.client.WieldHandler;
import com.prsc.gs.states.Action;
import com.prsc.gs.states.CombatState;
import com.prsc.gs.tools.DataConversions;
import com.prsc.gs.util.Logger;
import com.prsc.gs.util.StatefulEntityCollection;

/**
 * A single player.
 */
public final class Player extends Mob {
	
	/**
	 * TODO: Create scriptable components out of the mess
	 */
	
	// Moved here for now to keep it simple
	public final static InvItem[] STARTER_ITEMS = 
		{new InvItem(4), new InvItem(70), new InvItem(376), new InvItem(156), new InvItem(87), 
		new InvItem(1263)};
	
	public enum SkillType { // Our skill set
		ATTACK(0), DEFENSE(1), STRENGTH(2), HITS(3), RANGED(4),
		PRAYER(5), MAGIC(6), COOKING(7), WOODCUT(8), FLETCHING(9),
		FISHING(10), FIREMAKING(11), CRAFTING(12), SMITHING(13), MINING(14), HERBLAW(15),
		AGILITY(16), THIEVING(17);

		protected int stat = 0;

		SkillType(int stat) {
			this.stat = stat;
		}

		public int getSkill() { 
			return stat; 
		}
	}
	
	public final Scriptable script = new Scriptable(this); 

	public int click = -1;
	
	public int lastOption = -2;
	
	public int lastMineTries = -1;
	
	public String[] lastOptions = null;

	public int getLastOption() {
		return lastOption;
	}

	public void setLastOption(int lastOption) {
		this.lastOption = lastOption;
	}

	public String[] getLastOptions() {
		return lastOptions;
	}

	public void setLastOptions(String[] lastOptions) {
		this.lastOptions = lastOptions;
	}

	public static final String MEMBER_MESSAGE = "This feature is only available on a members server";

	private boolean firstMajorUpdateSent = false;

	public boolean isFirstMajorUpdateSent() {
		return firstMajorUpdateSent;
	}
	
	public void sendMemberErrorMessage() {
		getActionSender().sendMessage(MEMBER_MESSAGE);
	}

	public void setFirstMajorUpdateSent(boolean firstMajorUpdateSent) {
		this.firstMajorUpdateSent = firstMajorUpdateSent;
	}

	public int getClick() {
		return click;
	}

	public void setClick(int click) {
		this.click = click;
	}

	public Npc getLastNpcChasingYou() {
		return lastNpcChasingYou;
	}

	public void setLastNpcChasingYou(Npc lastNpcChasingYou) {
		this.lastNpcChasingYou = lastNpcChasingYou;
	}

	public long getLastNPCChat() {
		return lastNPCChat;
	}

	public void setLastNPCChat(long lastNPCChat) {
		this.lastNPCChat = lastNPCChat;
	}

	public Npc lastNpcChasingYou = null;
	
	public long lastNPCChat = GameEngine.getAccurateTimestamp(); 
	
	private final AtomicReference<Area> area = new AtomicReference<Area>();
	
	private boolean muted = false;

	private long muteTime = 0L;

	public void setMuteTime(long muteTime) {
		if (muteTime == -1 || muteTime > (GameEngine.getAccurateTimestamp() / 1000)) {
			muted = true;
		}
		this.muteTime = muteTime;
	}

	public boolean isMuted() {
		return muted;
	}

	public long getMuteTime() {
		return muteTime;
	}
	
	private boolean botDetected = false;
	
	public void setBotDetected(boolean botDetected) {
		this.botDetected = botDetected;
	}
	
	public boolean botDetected() {
		return botDetected;
	}
	
	/**
	 * Methods to send packets related to actions
	 */
	private final MiscPacketBuilder actionSender = new MiscPacketBuilder(this);;
	/**
	 * The current agility course the player's doing
	 */
	private AgilityCourseDef agilityCourseDef = null;
	/**
	 * The Players appearance
	 */
	private Appearance appearance;
	/**
	 * Players we have been attacked by signed login, used to check if we should
	 * get a skull for attacking back
	 */
	private HashMap<Long, Long> attackedBy = new HashMap<Long, Long>();
	/**
	 * Bank for banked items
	 */
	private Bank bank;
	/**
	 * Bubbles needing displayed
	 */
	private ArrayList<Bubble> bubblesNeedingDisplayed = new ArrayList<Bubble>();
	/**
	 * Controls if were allowed to accept appearance updates
	 */
	private boolean changingAppearance = false;
	/**
	 * Chat messages needing displayed
	 */
	private ArrayList<ChatMessage> chatMessagesNeedingDisplayed = new ArrayList<ChatMessage>();
	/**
	 * List of chat messages to send
	 */
	private LinkedList<ChatMessage> chatQueue = new LinkedList<ChatMessage>();

	/**
	 * Combat style: 0 - all, 1 - str, 2 - att, 3 - def
	 */
	private int combatStyle = 0;
	private int Combo = 0;
	/**
	 * Added by Zerratar: Correct sleepword we are looking for! Case SenSitIvE
	 */
	public String correctSleepword = "";

	private int incorrectSleepTries = 0;

	private String currentIP = "0.0.0.0";
	/**
	 * Unix time when the player logged in
	 */
	private long currentLogin = 0;
	/**
	 * The current stat array
	 */
	private int[] curStat = new int[18];
	/**
	 * Should we destroy this player?
	 */
	private boolean destroy = false;
	/**
	 * DelayedEvent responsible for handling prayer drains
	 */
	private DelayedEvent drainer;

	int drainerDelay = Integer.MAX_VALUE;
	/**
	 * The drain rate of the prayers currently enabled
	 */
	private int drainRate = 0;
	/**
	 * If the second duel screen has been accepted
	 */
	private boolean duelConfirmAccepted = false;
	/**
	 * List of items offered in the current duel
	 */
	private ArrayList<InvItem> duelOffer = new ArrayList<InvItem>();
	/**
	 * If the first duel screen has been accepted
	 */
	private boolean duelOfferAccepted = false;
	/**
	 * Duel options
	 */
	private boolean[] duelOptions = new boolean[4];
	/**
	 * The exp level array
	 */
	private int[] exp = new int[18];
	/**
	 * Amount of fatigue - 0 to 7500
	 */
	private int fatigue = 7500, tempFatigue = 7500;

	private DelayedEvent sleepEvent;
	/**
	 * Event to handle following
	 */
	private DelayedEvent followEvent;
	/**
	 * Who we are currently following (if anyone)
	 */
	private Mob following;
	/**
	 * Map of players on players friend list
	 */
	private TreeMap<Long, Integer> friendList = new TreeMap<Long, Integer>();
	/**
	 * Users game settings, camera rotation preference etc
	 */
	private boolean[] gameSettings = new boolean[7]; // Why is 1 empty?
	/**
	 * The main accounts group is
	 */
	private int groupID = 4;
	/**
	 * List of usernameHash's of players on players ignore list
	 */
	private ArrayList<Long> ignoreList = new ArrayList<Long>();
	/**
	 * Is the player accessing their bank?
	 */
	private boolean inBank = false;
	/**
	 * Quests
	 */
	/**
	 * Has the player been registered into the world?
	 */
	private boolean initialized = false;
	/**
	 * The npc we are currently interacting with
	 */
	private Npc interactingNpc = null;
	/**
	 * Inventory to hold items
	 */
	private Inventory inventory;
	private boolean invis = false;
	private boolean blink = false;
	/**
	 * The IO channel of this player
	 */
	private Channel ioSession;
	/**
	 * If the player is currently in a duel
	 */
	private boolean isDueling = false;

	public boolean isMining = false;

	private boolean sleeping = false;

	private boolean isTrading = false;

	private HashMap<Integer, Integer> knownPlayersAppearanceIDs = new HashMap<Integer, Integer>();

	public String lastAnswer = null;
	/**
	 * Last arrow fired
	 */
	private long lastArrow = 0;
	/**
	 * The last menu reply this player gave in a quest
	 */
	public long lastCast = GameEngine.getAccurateTimestamp();
	/**
	 * Time of last charge spell
	 */
	private long lastCharge = 0;
	/**
	 * Last packet count time
	 */
	private long lastCount = 0;

	/**
	 * Stores the last IP address used
	 */
	private String lastIP = "0.0.0.0";
	/**
	 * Unix time when the player last logged in
	 */
	private long lastLogin = 0;

	/**
	 * Queue of last 100 packets, used for auto detection purposes
	 */
	private LinkedList<RSCPacket> lastPackets = new LinkedList<RSCPacket>();
	
	public long lastPacketTime = -1;
	/**
	 * Last time a 'ping' was received
	 */
	private long lastPing = GameEngine.getAccurateTimestamp();

	public long lastRange = GameEngine.getAccurateTimestamp();
	/**
	 * Time last report was sent, used to throttle reports
	 */
	private long lastReport = 0L;

	private long lastRun = GameEngine.getAccurateTimestamp();

	private long lastSaveTime = GameEngine.getAccurateTimestamp() + DataConversions.random(600000, 1800000);
	/**
	 * The time of the last spell cast, used as a throttle
	 */
	private long lastSpellCast = 0L;
	/**
	 * Time of last trade/duel request
	 */
	private long lastTradeDuelRequest = 0L;
	/**
	 * Whether the player is currently logged in
	 */
	private boolean loggedIn = false;

	/**
	 * Is the character male?
	 */
	private boolean maleGender;
	/**
	 * The max stat array
	 */
	private int[] maxStat = new int[18];
	/**
	 * A handler for any menu we are currently in
	 */
	private MenuHandler menuHandler = null;
	/**
	 * Added by Konijn
	 */
	private boolean noclip = false;

	private boolean nonaggro = false;

	private boolean nopk = false;
	/**
	 * NPC messages needing displayed
	 */
	private ArrayList<ChatMessage> npcMessagesNeedingDisplayed = new ArrayList<ChatMessage>();
	/**
	 * List of players who have been hit
	 */
	private ArrayList<Npc> npcsNeedingHitsUpdate = new ArrayList<Npc>();
	/**
	 * The ID of the owning account
	 */
	private int owner = 1;
	/**
	 * Amount of packets since last count
	 */
	private int packetCount = 0;
	private boolean packetSpam = false;
	/**
	 * The player's password
	 */
	private String password;
	/**
	 * List of players who have been hit
	 */
	private ArrayList<Player> playersNeedingHitsUpdate = new ArrayList<Player>();
	/**
	 * Users privacy settings, chat block etc.
	 */
	private boolean[] privacySettings = new boolean[4];
	/**
	 * List of all projectiles needing displayed
	 */
	private ArrayList<Projectile> projectilesNeedingDisplayed = new ArrayList<Projectile>();
	/**
	 * Ranging event
	 */
	private RangeEvent rangeEvent;
	/**
	 * If the player is reconnecting after connection loss
	 */
	private boolean reconnecting = false;
	/**
	 * Is a trade/duel update required?
	 */
	private boolean requiresOfferUpdate = false;
	public int sessionFlags = 0;
	/**
	 * Session keys for the players session
	 */
	private int[] sessionKeys = new int[4];

	/**
	 * The shop (if any) the player is currently accessing
	 */
	private Shop shop = null;
	/**
	 * DelayedEvent used for removing players skull after 20mins
	 */
	private DelayedEvent skullEvent = null;
	/**
	 * The current status of the player
	 */
	private Action status = Action.IDLE;
	/**
	 * When the users subscription expires (or 0 if they don't have one)
	 */
	private long subscriptionExpires = 0;
	/**
	 * If the player has been sending suscicious packets
	 */
	private boolean suspicious = false;
	/**
	 * If the second trade screen has been accepted
	 */
	private boolean tradeConfirmAccepted = false;
	/**
	 * List of items offered in the current trade
	 */
	private ArrayList<InvItem> tradeOffer = new ArrayList<InvItem>();
	/**
	 * If the first trade screen has been accepted
	 */
	private boolean tradeOfferAccepted = false;
	/**
	 * The player's username
	 */
	private String username;
	/**
	 * The player's username hash
	 */
	private long usernameHash;
	/**
	 * Nearby items that we should be aware of
	 */
	private StatefulEntityCollection<Item> watchedItems = new StatefulEntityCollection<Item>();
	/**
	 * Nearby npcs that we should be aware of
	 */
	private StatefulEntityCollection<Npc> watchedNpcs = new StatefulEntityCollection<Npc>();
	/**
	 * Nearby game objects that we should be aware of
	 */
	private StatefulEntityCollection<GameObject> watchedObjects = new StatefulEntityCollection<GameObject>();
	/**
	 * Nearby players that we should be aware of
	 */
	private StatefulEntityCollection<Player> watchedPlayers = new StatefulEntityCollection<Player>();
	/**
	 * The player we last requested to duel with, or null for none
	 */
	private Player wishToDuel = null;
	/**
	 * The player we last requested to trade with, or null for none
	 */
	private Player wishToTrade = null;
	/**
	 * The items being worn by the player
	 */
	private int[] wornItems = new int[12];

	public Player(Channel ios) {
		ioSession = ios;
		//String hostname = ((InetSocketAddress) ios.getRemoteAddress()).getHostName();
		//if(hostname == null || hostname.isEmpty())
		currentIP = ((InetSocketAddress) ios.getRemoteAddress()).getAddress().getHostAddress();
		//else
		//	currentIP = hostname;
		currentLogin = GameEngine.getAccurateTimestamp();
		this.setBusy(true);
	}

	public boolean accessingBank() {
		return inBank;
	}

	public boolean accessingShop() {
		return shop != null;
	}

	public void addAttackedBy(Player p) {
		attackedBy.put(p.getUsernameHash(), GameEngine.getAccurateTimestamp());
	}

	public void addFriend(long id, int world) {
		friendList.put(id, world);
	}

	public void addIgnore(long id) {
		ignoreList.add(id);
	}

	public int getIncorrectSleepTimes() {
		return incorrectSleepTries;
	}

	public void incrementSleepTries() {
		incorrectSleepTries++;
	}

	public void resetSleepTries() {
		incorrectSleepTries = 0;
	}

	public void addMessageToChatQueue(byte[] messageData) {
		chatQueue.add(new ChatMessage(this, messageData));
	}
	
	/*
	public void addPacket(RSCPacket p) {
		long now = System.currentTimeMillis();
		if (now - lastCount > 3000) {
			lastCount = now;
			packetCount = 0;
		}
		if (!DataConversions.inArray(Formulae.safePacketIDs, p.getID()) && ++packetCount >= 60) {
			destroy(false);
		}

		if (lastPackets.size() >= 60) {
			lastPackets.remove();
		}
		lastPackets.addLast(p);
	} */

	public void addPlayersAppearanceIDs(int[] indicies, int[] appearanceIDs) {
		for (int x = 0; x < indicies.length; x++) {
			knownPlayersAppearanceIDs.put(indicies[x], appearanceIDs[x]);
		}
	}
	
	public void resetPlayerAppearanceIDs(int[] indicies, int[] appearanceIDs) {
		knownPlayersAppearanceIDs.clear();
		for (int x = 0; x < indicies.length; x++) {
			knownPlayersAppearanceIDs.put(indicies[x], appearanceIDs[x]);
		}
	}

	public void addPrayerDrain(int prayerID) {
		drainRate = 0;
		PrayerDef prayer = EntityHandler.getPrayerDef(prayerID);
		for (int x = 0; x <= 13; x++) {
			prayer = EntityHandler.getPrayerDef(x);
			if (super.isPrayerActivated(x)) {
				drainRate += prayer.getDrainRate() / 2;
			}
		}
		drainRate = drainRate - getPrayerPoints();
		if (drainRate > 0) {
			drainer.setDelay((int) (240000 / drainRate));
		} else if (drainRate <= 0) {
			drainRate = 0;
			drainer.setDelay(240000);
		}
	}

	public void addSkull(long timeLeft) {
		if (!isSkulled()) {
			skullEvent = new DelayedEvent(this, 1200000) {

				public void run() {
					removeSkull();
				}
			};
			World.getWorld().getDelayedEventHandler().add(skullEvent);
			super.setAppearnceChanged(true);
		}
		skullEvent.setLastRun(GameEngine.getAccurateTimestamp() - (1200000 - timeLeft));
	}

	public void addToDuelOffer(InvItem item) {
		duelOffer.add(item);
	}

	public void addToTradeOffer(InvItem item) {
		tradeOffer.add(item);
	}

	public boolean canLogout() {
		/* Just do a null check, we don't need to try/catch everything.
		if (location != null && location.inWilderness() && !isMod()) {
			if (System.currentTimeMillis() - getLastMoved() < 10000) {
				getActionSender().sendMessage("You must stand peacefully in one place for 10 seconds!");
				return false;
			}
		} */
		return !isBusy() && GameEngine.getAccurateTimestamp() - getCombatTimer() > 10000;
	}

	public boolean canReport() {
		return GameEngine.getAccurateTimestamp() - lastReport > 60000;
	}

	public boolean castTimer() {
		return GameEngine.getAccurateTimestamp() - lastSpellCast > 1600;
	}

	public boolean checkAttack(Mob mob, boolean missile) {
		if (mob instanceof Player) {
			Player victim = (Player) mob;
			if (victim.isNoPK()) {
				return false;
			}

			if ((inCombat() && isDueling()) && (victim.inCombat() && victim.isDueling())) {
				Player opponent = (Player) getOpponent();
				if (opponent != null && victim.equals(opponent)) {
					return true;
				}
			}
			if (GameEngine.getAccurateTimestamp() - mob.getCombatTimer() < (mob.getCombatState() == CombatState.RUNNING
					|| mob.getCombatState() == CombatState.WAITING ? 3000 : 500)
					&& !mob.inCombat()) {
				return false;
			} 
			int myWildLvl = getLocation().wildernessLevel();
			int victimWildLvl = victim.getLocation().wildernessLevel();
			if (myWildLvl < 1 || victimWildLvl < 1) {
				actionSender
				.sendMessage("You cannot attack other players outside of the wilderness!");
				return false;
			}
			int combDiff = Math.abs(getCombatLevel() - victim.getCombatLevel());
			if (combDiff > myWildLvl) {
				actionSender.sendMessage("You must move to at least level "
						+ combDiff + " wilderness to attack "
						+ victim.getUsername() + "!");
				return false;
			}
			if (combDiff > victimWildLvl) {
				actionSender
				.sendMessage(victim.getUsername()
						+ " is not in high enough wilderness for you to attack!");
				return false;
			}
			return true;
		} else if (mob instanceof Npc) {
			Npc victim = (Npc) mob;
			if (!victim.getDef().isAttackable()) {
				setSuspiciousPlayer(true);
				return false;
			}
			return true;
		}
		return true;
	}

	public void clearBubblesNeedingDisplayed() {
		bubblesNeedingDisplayed.clear();
	}

	public void clearChatMessagesNeedingDisplayed() {
		chatMessagesNeedingDisplayed.clear();
	}

	public void clearDuelOptions() {
		for (int i = 0; i < 4; i++) {
			duelOptions[i] = false;
		}
	}

	public void clearNpcMessagesNeedingDisplayed() {
		npcMessagesNeedingDisplayed.clear();
	}

	public void clearNpcsNeedingHitsUpdate() {
		npcsNeedingHitsUpdate.clear();
	}

	public void clearPlayersNeedingHitsUpdate() {
		playersNeedingHitsUpdate.clear();
	}

	public void clearProjectilesNeedingDisplayed() {
		projectilesNeedingDisplayed.clear();
	}


	/**
	 * 	public void destroy(boolean force) {
		if(destroy) {
			return;
		}
		String user = this.getUsername();
		if(force || canLogout()) {
			if(user == null) {
				destroy = true;
				actionSender.sendLogout();
				return;
			}
			destroy = true;
			actionSender.sendLogout();
			GUI.writeValue(user, "loggedin", "false");
			if(this.isAdmin())
				GameVars.adminsOnline--;
			else if(this.rank == 3 || this.rank == 2)
				GameVars.modsOnline--;
		}
		else {
			final long startDestroy = System.currentTimeMillis();
			world.getDelayedEventHandler().add(new DelayedEvent(this, 3000) {
				public void run() {
					if(owner.canLogout() || (!(owner.inCombat() && owner.isDueling()) && System.currentTimeMillis() - startDestroy > 60000)) {
						owner.destroy(true);
						running = false;
					}
				}
			});
		}
	}
	 * @param force
	 */
	public void destroy(boolean force) {
		try {
			if (destroy) {
				return;
			}

			if (force || canLogout()) {
				if(getUsername() != null) {
					System.out.println("Logging out " + getUsername());
				}
				destroy = true;
				actionSender.sendLogout();
				
				PluginHandler.getPluginHandler().handleAction("PlayerLogout", new Object[]{this});
			} else {
				final long startDestroy = GameEngine.getAccurateTimestamp();
				World.getWorld().getDelayedEventHandler().add(new DelayedEvent(this, 3000) {

					public void run() {
						if (owner.canLogout()
								|| (!(owner.inCombat() && owner.isDueling()) && System
										.currentTimeMillis()
										- startDestroy > 60000)) {
							owner.destroy(true);
							matchRunning = false;

						}
					}
				});
			}
			ioSession.getParent().disconnect();
		} catch(Exception e) {
			if(this.getUsername() != null) {
				System.out.println("EXCEPTION AT PLAYER LOGOUT FOR: " + this.getUsername());
			}
		
			e.printStackTrace();
		}
	}

	public boolean destroyed() {
		return destroy;
	}

	public boolean equals(Object o) {
		if (o instanceof Player) {
			Player p = (Player) o;
			return usernameHash == p.getUsernameHash();
		}
		return false;
	}

	public int friendCount() {
		return friendList.size();
	}


	public MiscPacketBuilder getActionSender() {
		return actionSender;
	}

	/**
	 * @return this player's current agility course
	 */
	public AgilityCourseDef getAgilityCourseDef() {
		return agilityCourseDef;
	}

	public int getArmourPoints() {
		int points = 1;
		for (InvItem item : inventory.getItems()) {
			if (item.isWielded()) {
				points += item.getWieldableDef().getArmourPoints();
			}
		}
		return points < 1 ? 1 : points;
	}

	public int getAttack() {
		return getCurStat(0);
	}

	public Bank getBank() {
		return bank;
	}

	public List<Bubble> getBubblesNeedingDisplayed() {
		return bubblesNeedingDisplayed;
	}

	public long getCastTimer() {
		return lastSpellCast;
	}

	public List<ChatMessage> getChatMessagesNeedingDisplayed() {
		return chatMessagesNeedingDisplayed;
	}

	public int getCombatStyle() {
		return combatStyle;
	}

	public int getCombo() {
		return this.Combo;
	}

	public String getCurrentIP() {
		return currentIP;
	}

	public long getCurrentLogin() {
		return currentLogin;
	}

	public int getCurStat(int id) {
		return curStat[id];
	}

	public int[] getCurStats() {
		return curStat;
	}

	public int getDaysSinceLastLogin() {
		long now = Calendar.getInstance().getTimeInMillis() / 1000;
		return (int) ((now - lastLogin) / 86400);
	}

	public int getDaysSubscriptionLeft() {
		long now = (GameEngine.getAccurateTimestamp() / 1000);
		if (subscriptionExpires == 0 || now >= subscriptionExpires) {
			return 0;
		}
		return (int) ((subscriptionExpires - now) / 86400);
	}

	public int getDefense() {
		return getCurStat(1);
	}

	public int getDrainRate() {
		return drainRate;
	}

	public ArrayList<InvItem> getDuelOffer() {
		return duelOffer;
	}

	public boolean getDuelSetting(int i) {
		try {
			for (InvItem item : duelOffer) {
				if (DataConversions.inArray(Formulae.runeIDs, item.getID())) {
					setDuelSetting(1, true);
					break;
				}
			}
			for (InvItem item : wishToDuel.getDuelOffer()) {
				if (DataConversions.inArray(Formulae.runeIDs, item.getID())) {
					setDuelSetting(1, true);
					break;
				}
			}
		} catch (Exception e) {
		}
		return duelOptions[i];
	}

	public int getExp(int id) {
		return exp[id];
	}

	public int[] getExps() {
		return exp;
	}

	public int getFatiguePercentage() {
		return fatigue / 10;
	}

	public int getFatigue() {
		return fatigue;
	}

	public Collection<Entry<Long, Integer>> getFriendList() {
		return friendList.entrySet();
	}

	public boolean getGameSetting(int i) {
		return gameSettings[i];
	}

	public int getGroupID() {
		return groupID;
	}

	public int getHits() {
		return getCurStat(3);
	}

	public ArrayList<Long> getIgnoreList() {
		return ignoreList;
	}

	public Inventory getInventory() {
		return inventory;
	}

	public String getLastIP() {
		return lastIP; 
	}

	public long getLastLogin() {
		return lastLogin;
	}

	public long getLastPing() {
		return lastPing;
	}

	public long getLastSaveTime() {
		return lastSaveTime;
	}

	public int getMagicPoints() {
		int points = 1;
		for (InvItem item : inventory.getItems()) {
			if (item.isWielded()) {
				points += item.getWieldableDef().getMagicPoints();
			}
		}
		return points < 1 ? 1 : points;
	}

	public int getMaxStat(int id) {
		return maxStat[id];
	}

	public int[] getMaxStats() {
		return maxStat;
	}

	public MenuHandler getMenuHandler() {
		return menuHandler;
	}

	public ChatMessage getNextChatMessage() {
		return chatQueue.poll();
	}

	public Npc getNpc() {
		return interactingNpc;
	}

	public List<ChatMessage> getNpcMessagesNeedingDisplayed() {
		return npcMessagesNeedingDisplayed;
	}

	public List<Npc> getNpcsRequiringHitsUpdate() {
		return npcsNeedingHitsUpdate;
	}

	public int getOwner() {
		return owner;
	}

	public List<RSCPacket> getPackets() {
		return lastPackets;
	}

	public String getPassword() {
		return password;
	}

	public Appearance getPlayerAppearance() {
		return appearance;
	}

	public List<Player> getPlayersRequiringAppearanceUpdate() {
		List<Player> needingUpdates = new ArrayList<Player>();
		needingUpdates.addAll(watchedPlayers.getNewEntities());
		if (super.ourAppearanceChanged) {
			needingUpdates.add(this);
		}
		for (Player p : watchedPlayers.getKnownEntities()) {
			if (needsAppearanceUpdateFor(p)) {
				needingUpdates.add(p);
			}
		}
		return needingUpdates;
	}

	public List<Player> getPlayersRequiringHitsUpdate() {
		return playersNeedingHitsUpdate;
	}

	public int getPrayerPoints() {
		int points = 1;
		for (InvItem item : inventory.getItems()) {
			if (item.isWielded()) {
				points += item.getWieldableDef().getPrayerPoints();
			}
		}
		return points < 1 ? 1 : points;
	}

	public boolean getPrivacySetting(int i) {
		return privacySettings[i];
	}

	public List<Projectile> getProjectilesNeedingDisplayed() {
		return projectilesNeedingDisplayed;
	}

	public int getRangeEquip() {
		for (InvItem item : inventory.getItems()) {
			if (item.isWielded()
					&& (DataConversions.inArray(Formulae.bowIDs, item.getID()) || DataConversions
							.inArray(Formulae.xbowIDs, item.getID()))) {
				return item.getID();
			}
		}
		return -1;
	}

	public int getRangePoints() {
		int points = 1;
		for (InvItem item : inventory.getItems()) {
			if (item.isWielded()) {
				points += item.getWieldableDef().getRangePoints();
			}
		}
		return points < 1 ? 1 : points;
	}

	public Channel getSession() {
		return ioSession;
	}

	public Shop getShop() {
		return shop;
	}

	public int getSkillTotal() {
		int total = 0;
		for (int stat : maxStat) {
			total += stat;
		}
		return total;
	}

	public int getSkullTime() {
		if (isSkulled()) {
			return skullEvent.timeTillNextRun();
		}
		return 0;
	}

	public boolean getSpam() {
		return packetSpam;
	}

	public int getSpellWait() {
		return DataConversions.roundUp((double) (1200 - (GameEngine.getAccurateTimestamp() - lastSpellCast)) / 1000D);
	}

	public Action getStatus() {
		return status;
	}

	public int getStrength() {
		return getCurStat(2);
	}

	public ArrayList<InvItem> getTradeOffer() {
		return tradeOffer;
	}

	public String getUsername() {
		return username;
	}

	public long getUsernameHash() {
		return usernameHash;
	}

	public StatefulEntityCollection<Item> getWatchedItems() {
		return watchedItems;
	}

	public StatefulEntityCollection<Npc> getWatchedNpcs() {
		return watchedNpcs;
	}

	public StatefulEntityCollection<GameObject> getWatchedObjects() {
		return watchedObjects;
	}

	public StatefulEntityCollection<Player> getWatchedPlayers() {
		return watchedPlayers;
	}

	public int getWeaponAimPoints() {
		int points = 1;
		for (InvItem item : inventory.getItems()) {
			if (item.isWielded()) {
				points += item.getWieldableDef().getWeaponAimPoints();
			}
		}
		points -= 1;
		return points < 1 ? 1 : points;
	}

	public int getWeaponPowerPoints() {
		int points = 1;
		for (InvItem item : inventory.getItems()) {
			if (item.isWielded()) {
				points += item.getWieldableDef().getWeaponPowerPoints();
			}
		}
		points -= 1;
		return points < 1 ? 1 : points;
	}

	public Player getWishToDuel() {
		return wishToDuel;
	}

	public Player getWishToTrade() {
		return wishToTrade;
	}

	public int[] getWornItems() {
		return wornItems;
	}

	public int ignoreCount() {
		return ignoreList.size();
	}

	public void incCurStat(int i, int amount) {
		curStat[i] += amount;
		if (curStat[i] < 0) {
			curStat[i] = 0;
		}
	}

	public int combatStyleToIndex() {
		if (getCombatStyle() == 1) {
			return 2;
		}
		if (getCombatStyle() == 2) {
			return 0;
		}
		if (getCombatStyle() == 3) {
			return 1;
		}
		return -1;
	}

	public void incExp(int i, int amount) {
		exp[i] += amount;
		if (exp[i] < 0) {
			exp[i] = 0;
		}

		int level = Formulae.experienceToLevel(exp[i]);
		if (level != maxStat[i]) {

			for (InvItem item : this.getInventory().getItems()) {
				if (!item.isWielded()) {
					continue;
				}
				String youNeed = "";
				for (Entry<Integer, Integer> e : item.getWieldableDef().getStatsRequired()) {
					if (this.getMaxStat(e.getKey()) < e.getValue()) {
						youNeed += e.getValue() + " " + Formulae.statArray[e.getKey()] + ", ";
					}
				}
				if (!youNeed.equals("")) {
					this.getActionSender().sendMessage("You must have at least " + youNeed.substring(0, youNeed.length() - 2) + " to use this item.");
					WieldHandler.unWieldItem(this, item, true);
				}
				if (EntityHandler.getItemWieldableDef(item.getID()).femaleOnly() && this.isMale()) {
					this.getActionSender().sendMessage("This piece of armor is for a female only.");
					WieldHandler.unWieldItem(this, item, true);
				}
			}
			this.getActionSender().sendInventory();

			int advanced = level - maxStat[i];
			incCurStat(i, advanced);
			incMaxStat(i, advanced);
			actionSender.sendStat(i);

			int comb = Formulae.getCombatlevel(maxStat);
			if (comb != getCombatLevel()) {
				setCombatLevel(comb);
			}
		}
	}

	public void incExp(int i, int amount, boolean useFatigue, boolean combat, boolean useMultiplier) {
		if (useFatigue) {
			if (fatigue >= 7500) {
				actionSender.sendMessage("@gre@You are too tired to gain experience, get some rest!");
				return;
			}
			//if (fatigue >= 7200) {
			//	actionSender.sendMessage("@gre@You start to feel tired, maybe you should rest soon.");
			//}
			if (i >= 3 && useFatigue) {
				int famt = 8 * amount / 5;
				fatigue += famt;
				actionSender.sendFatigue(fatigue / 10);
			}
		}
		if (combat && i < 3	&& (combatStyleToIndex() != i && getCombatStyle() != 0)) {
			return;
		}
		if (location.onTutorialIsland()) {
			if (exp[i] + amount > 200) {
				if (i != 3) {
					exp[i] = 200;
				} else {
					exp[i] = 1200;
				}
			}
		}
		double expRate = Constants.GameServer.NORMAL_EXP_RATE;
		double fightRate = Constants.GameServer.COMBAT_ONLY_EXP_RATE;

		if(useMultiplier && isSubscriber()) {
			expRate = Constants.GameServer.MEMBERS_EXP_RATE;
		}
		
		if(i <= 3) {
			exp[i] += amount * fightRate;
		} else {
			exp[i] += amount * expRate;
		}
		
		if (exp[i] < 0) {
			exp[i] = 0;
		}

		int level = Formulae.experienceToLevel(exp[i]);
		if (level != maxStat[i]) {
			int advanced = level - maxStat[i];
			incCurStat(i, advanced);
			incMaxStat(i, advanced);
			actionSender.sendStat(i);
			actionSender.sendMessage("@gre@You just advanced " + advanced + " " + Formulae.statArray[i] + " level" + (advanced > 1 ? "s" : "") + "!");
			actionSender.sendSound("advance");

			int comb = Formulae.getCombatlevel(maxStat);
			if (comb != getCombatLevel()) {
				setCombatLevel(comb);
			}
		}
		actionSender.sendStat(i);
	}

	public void incExp(int i, double d, boolean useFatigue) {
		incExp(i, (int) d, useFatigue, false, true);
	}

	public void incExp(int i, double d, boolean useFatigue, boolean combat, boolean multiplier) {
		incExp(i, (int) d, useFatigue, combat, multiplier);
	}

	public void incMaxStat(int i, int amount) {
		maxStat[i] += amount;
		if (maxStat[i] < 0) {
			maxStat[i] = 0;
		}
	}

	public void informOfBubble(Bubble b) {
		bubblesNeedingDisplayed.add(b);
	}

	public void informGroupOfBubble(Bubble b) {
		for (Player p : viewArea.getPlayersInView()) {
			p.informOfBubble(b);
		}
	}

	public void informOfChatMessage(ChatMessage cm) {
		chatMessagesNeedingDisplayed.add(cm);
	}

	public void informGroupOfChatMessage(ChatMessage cm) {
		for (Player p : viewArea.getPlayersInView()) {
			p.informOfChatMessage(cm);
		}
	}

	public void informOfModifiedHits(Mob mob) {
		if (mob instanceof Player) {
			playersNeedingHitsUpdate.add((Player) mob);
		} else if (mob instanceof Npc) {
			npcsNeedingHitsUpdate.add((Npc) mob);
		}
	}

	public void informGroupOfModifiedHits(Mob mob) {
		for (Player p : viewArea.getPlayersInView()) {
			p.informOfModifiedHits(mob);
		}
	}

	public void informOfNpcMessage(ChatMessage cm) {
		npcMessagesNeedingDisplayed.add(cm);
	}

	public void informGroupOfNpcMessage(ChatMessage cm) {
		for (Player p : viewArea.getPlayersInView()) {
			p.informOfNpcMessage(cm);
		}
	}

	/**
	 * This is a 'another player has tapped us on the shoulder' method.
	 * <p/>
	 * If we are in another players viewArea, they should in theory be in ours.
	 * So they will call this method to let the player know they should probably
	 * be informed of them.
	 */
	public void informOfPlayer(Player p) {
		if ((!watchedPlayers.contains(p) || watchedPlayers.isRemoving(p))
				&& withinRange(p)) {
			watchedPlayers.add(p);
		}
	}

	public void informOfProjectile(Projectile p) {
		projectilesNeedingDisplayed.add(p);
	}

	public boolean initialized() {
		return initialized;
	}

	public boolean isAdmin() {
		return groupID == 1;
	}
	
	public boolean isMod() {
		return groupID == 2 || isAdmin();
	}

	public boolean isChangingAppearance() {
		return changingAppearance;
	}

	public boolean isCharged() {
		return GameEngine.getAccurateTimestamp() - lastCharge < 600000;
	}

	public boolean isDuelConfirmAccepted() {
		return duelConfirmAccepted;
	}

	public boolean isDueling() {
		return isDueling;
	}

	public boolean isDuelOfferAccepted() {
		return duelOfferAccepted;
	}

	public boolean isFollowing() {
		return followEvent != null && following != null;
	}

	public boolean isFriendsWith(long usernameHash) {
		return friendList.containsKey(usernameHash);
	}

	public boolean isIgnoring(long usernameHash) {
		return ignoreList.contains(usernameHash);
	}

	public boolean isInvis() {
		return invis;
	}

	public boolean blink() {
		return blink;
	}

	public boolean isMale() {
		return maleGender;
	}

	public boolean isMining() {
		return isMining;
	}

	public void isMining(boolean arg) {
		isMining = arg;
	}
	
	public boolean isNonaggro() {
		return nonaggro;
	}

	public boolean isNoPK() {
		return nopk;
	}

	public String getRankHeader() {
		if (isAdmin()) {
			return "#adm#@yel@";
		} else if (groupID == 2) {
			return "#mod#@mod@";
		}
		return "";
	}

	public boolean isRanging() {
		return rangeEvent != null;
	}

	public boolean isReconnecting() {
		return reconnecting;
	}

	public boolean isSkulled() {
		return skullEvent != null;
	}

	public boolean isSubscriber() {
		return groupID == 6;
	}

	public boolean isSuspicious() {
		return suspicious;
	}

	public boolean isTradeConfirmAccepted() {
		return tradeConfirmAccepted;
	}

	public boolean isTradeOfferAccepted() {
		return tradeOfferAccepted;
	}

	public boolean isTrading() {
		return isTrading;
	}

	public void killedBy(Mob mob) {
		killedBy(mob, false);
	}

	public void killedByNothing() {
		if (!loggedIn) {
			Logger.error(username + " not logged in, but killed!");
			return;
		}

		actionSender.sendSound("death");
		this.setLastDeath();
		actionSender.sendDied();
		for (int i = 0; i < 18; i++) {
			curStat[i] = maxStat[i];
			actionSender.sendStat(i);
		}

		inventory.sort();
		ListIterator<InvItem> iterator = inventory.iterator();

		if (!isSkulled()) {
			for (int i = 0; i < 3 && iterator.hasNext(); i++) {
				if ((iterator.next()).getDef().isStackable()) {
					iterator.previous();
					break;
				}
			}
		}

		if (activatedPrayers[8] && iterator.hasNext()) {
			if (((InvItem) iterator.next()).getDef().isStackable()) {
				iterator.previous();
			}
		}
		
		for (int slot = 0; iterator.hasNext(); slot++) {
			InvItem item = (InvItem) iterator.next();

			if (item.isWielded()) {
				item.setWield(false);
				updateWornItems(item.getWieldableDef().getWieldPos(), appearance.getSprite(item.getWieldableDef().getWieldPos()));
			}
			iterator.remove();
			world.registerItem(new Item(item.getID(), getX(), getY(), item.getAmount(), null));
			
		}
		removeSkull();

		world.registerItem(new Item(20, getX(), getY(), 1, null));

		for (int x = 0; x < activatedPrayers.length; x++) {
			if (activatedPrayers[x]) {
				activatedPrayers[x] = false;
			}
			removePrayerDrain(x);
		}
		actionSender.sendPrayers();

		setLocation(Point.location(122, 647), true);

		Collection<Player> allWatched = watchedPlayers.getAllEntities();

		for (Player p : allWatched) {
			p.removeWatchedPlayer(this);
		}

		resetPath();
		resetCombat(CombatState.LOST);
		actionSender.sendWorldInfo();
		actionSender.sendEquipmentStats();
		actionSender.sendInventory();
	}

	public void killedBy(Mob mob, boolean stake) {
		if (!loggedIn) {
			Logger.error(username + " not logged in, but killed!");
			return;
		}
		if (mob instanceof Player) {
			Player player = (Player) mob;
			player.getActionSender().sendMessage("You have defeated " + getUsername() + "!");
			player.getActionSender().sendSound("victory");
			//Services.lookup(DatabaseManager.class).addQuery(new GenericLog(player.getUsername() + " killed " + username + " at " + location));
		}
		Mob opponent = super.getOpponent();
		if (opponent != null) {
			opponent.resetCombat(CombatState.WON);
		}
		actionSender.sendSound("death");
		this.setLastDeath();
		actionSender.sendDied();
		for (int i = 0; i < 18; i++) {
			curStat[i] = maxStat[i];
			actionSender.sendStat(i);
		}

		Player player = mob instanceof Player ? (Player) mob : null;
		//if (PluginHandler.getPluginHandler().blockDefaultAction("PlayerDeath", new Object[]{this})) {
		//	return;
		//}
		if (stake) {
			if (player == null) {
				Logger.println("Player is null (not dropping item): " + this.getUsername());
			}
			for (InvItem item : duelOffer) {
				InvItem affectedItem = getInventory().get(item);
				if (affectedItem == null) {
					setSuspiciousPlayer(true);
					Logger.error("Missing staked item [" + item.getID() + ", "
							+ item.getAmount() + "] from = " + usernameHash
							+ "; to = " + player.getUsernameHash() + ";");
					continue;
				}
				if (affectedItem.isWielded()) {
					affectedItem.setWield(false);
					updateWornItems(affectedItem.getWieldableDef()
							.getWieldPos(), getPlayerAppearance().getSprite(
									affectedItem.getWieldableDef().getWieldPos()));
				}
				getInventory().remove(item);
				world.registerItem(new Item(item.getID(), getX(), getY(), item.getAmount(), player));
			}
		} else {
			inventory.sort();
			ListIterator<InvItem> iterator = inventory.iterator();
			if (!isSkulled()) {
				for(int i = 0; i < 3 && iterator.hasNext(); i++) {
					if ((iterator.next()).getDef().isStackable()) {
						iterator.previous();
						break;
					}
				}
			}
			if (activatedPrayers[8] && iterator.hasNext()) {
				if (((InvItem) iterator.next()).getDef().isStackable()) {
					iterator.previous();
				}
			}
			for (int slot = 0; iterator.hasNext(); slot++) {
				InvItem item = (InvItem) iterator.next();
				if (item.isWielded()) {
					item.setWield(false);
					updateWornItems(item.getWieldableDef().getWieldPos(), appearance.getSprite(item.getWieldableDef().getWieldPos()));
				}
				iterator.remove();
				world.registerItem(new Item(item.getID(), getX(), getY(), item.getAmount(), player));
			}
			removeSkull(); // destroy
		}
		world.registerItem(new Item(20, getX(), getY(), 1, player));

		for (int x = 0; x < activatedPrayers.length; x++) {
			if (activatedPrayers[x]) {
				activatedPrayers[x] = false;
			}
			removePrayerDrain(x);
		}
		actionSender.sendPrayers();

		this.setLocation(Point.location(122, 647), true);

		Collection<Player> allWatched = watchedPlayers.getAllEntities();

		for (Player p : allWatched) {
			p.removeWatchedPlayer(this);
		}

		resetPath();
		resetCombat(CombatState.LOST);
		actionSender.sendWorldInfo();
		actionSender.sendEquipmentStats();
		actionSender.sendInventory();
	}

	public long lastAttackedBy(Player p) {
		Long time = attackedBy.get(p.getUsernameHash());
		if (time != null) {
			return time;
		}
		return 0;
	}
	
	private DelayedEvent healUpdate = null;

	public void load(String username, String password, int uid, boolean reconnecting) {
		try {
			this.setID(uid);
			this.password = password;
			this.reconnecting = reconnecting;
			this.usernameHash = DataConversions.usernameToHash(username);
			this.username = DataConversions.hashToUsername(usernameHash);

			World.getWorld().getServer().getLoginConnector().getActionSender().playerLogin(this);

			healUpdate = new DelayedEvent(this, 60000) {

				private void checkStat(int statIndex) {
					if (statIndex != 3 && owner.getCurStat(statIndex) == owner.getMaxStat(statIndex)) {
						owner.getActionSender().sendMessage("Your " + Formulae.statArray[statIndex] + " ability has returned to normal.");
					}
				}

				public void run() {
					for (int statIndex = 0; statIndex < 18; statIndex++) {
						if (statIndex == 5) {
							continue;
						}
						int curStat = getCurStat(statIndex);
						int maxStat = getMaxStat(statIndex);

						if (curStat > maxStat) {
							setCurStat(statIndex, curStat - 1);
							getActionSender().sendStat(statIndex);
							checkStat(statIndex);
						} else if (curStat < maxStat) {
							setCurStat(statIndex, curStat + 1);
							getActionSender().sendStat(statIndex);
							checkStat(statIndex);
						}
					}
				}
			};
			
			World.getWorld().getDelayedEventHandler().add(healUpdate);

			drainer = new DelayedEvent(this, Integer.MAX_VALUE) {

				public void run() {
					int curPrayer = getCurStat(5);
					if (getDrainRate() > 0 && curPrayer > 0) {
						incCurStat(5, -1);
						getActionSender().sendStat(5);
						if (curPrayer <= 1) {
							for (int prayerID = 0; prayerID < 14; prayerID++) {
								setPrayer(prayerID, false);
							}
							setDrainRate(0);
							setDelay(Integer.MAX_VALUE);
							getActionSender().sendMessage("You have run out of prayer points. Return to a church to recharge");
							getActionSender().sendPrayers();
						}
					}
					if (drainRate != 0) {
						setDelay((int) (240000 / drainRate));
					}
				}
			};
			World.getWorld().getDelayedEventHandler().add(drainer);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Lazy implementation of rapid heal/restore
	 * TODO: add no hits mode
	 * @param quick
	 */
	
	public void activateQuickHealUpdater(boolean quick) {	
		if(World.getWorld().getDelayedEventHandler().contains(healUpdate)) {
			World.getWorld().getDelayedEventHandler().remove(healUpdate);
		}
		
		healUpdate = new DelayedEvent(this, quick ? 20000 : 60000) {

			private void checkStat(int statIndex) {
				if (statIndex != 3 && owner.getCurStat(statIndex) == owner.getMaxStat(statIndex)) {
					owner.getActionSender().sendMessage("Your " + Formulae.statArray[statIndex] + " ability has returned to normal.");
				}
			}

			public void run() {
				for (int statIndex = 0; statIndex < 18; statIndex++) {
					if (statIndex == 5) {
						continue;
					}
					int curStat = getCurStat(statIndex);
					int maxStat = getMaxStat(statIndex);

					if (curStat > maxStat) {
						setCurStat(statIndex, curStat - 1);
						getActionSender().sendStat(statIndex);
						checkStat(statIndex);
					} else if (curStat < maxStat) {
						setCurStat(statIndex, curStat + 1);
						getActionSender().sendStat(statIndex);
						checkStat(statIndex);
					}
				}
			}
		};
		World.getWorld().getDelayedEventHandler().add(healUpdate);
	}

	public boolean loggedIn() {
		return loggedIn;
	}

	private boolean needsAppearanceUpdateFor(Player p) {
		int playerServerIndex = p.getIndex();
		if (knownPlayersAppearanceIDs.containsKey(playerServerIndex)) {
			int knownPlayerAppearanceID = knownPlayersAppearanceIDs
					.get(playerServerIndex);
			if (knownPlayerAppearanceID != p.getAppearanceID()) {
				return true;
			}
		} else {
			return true;
		}
		return false;
	}

	public void ping() {
		lastPing = GameEngine.getAccurateTimestamp();
	}

	public void remove() {
		removed.set(true);
	}

	public void removeFriend(long id) {
		friendList.remove(id);
	}

	public void removeIgnore(long id) {
		ignoreList.remove(id);
	}

	public void removePrayerDrain(int prayerID) {
		addPrayerDrain(prayerID);
	}

	public void removeSkull() {
		if (!isSkulled()) {
			return;
		}
		super.setAppearnceChanged(true);
		skullEvent.stop();
		skullEvent = null;
	}

	public void removeWatchedNpc(Npc n) {
		watchedNpcs.remove(n);
	}

	public void removeWatchedPlayer(Player p) {
		watchedPlayers.remove(p);
	}

	public boolean requiresOfferUpdate() {
		return requiresOfferUpdate;
	}

	public void resetAll() {
		resetAllExceptTradeOrDuel();
		resetTrade();
		resetDuel();
	}

	public void resetAllExceptDueling() {
		resetAllExceptTradeOrDuel();
		resetTrade();
	}

	private void resetAllExceptTradeOrDuel() {
		if (getMenuHandler() != null) {
			resetMenuHandler();
		}
		if (accessingBank()) {
			resetBank();
		}
		if (accessingShop()) {
			resetShop();
		}
		if (interactingNpc != null) {
			interactingNpc.unblock();
		}
		if (isFollowing()) {
			resetFollowing();
		}
		if (isRanging()) {
			resetRange();
		}
		setStatus(Action.IDLE);
	}

	public void resetAllExceptTrading() {
		resetAllExceptTradeOrDuel();
		resetDuel();
	}

	public void resetBank() {
		setAccessingBank(false);
		actionSender.hideBank();
	}

	public void resetDuel() {
		Player opponent = getWishToDuel();
		if (opponent != null) {
			opponent.resetDueling();
		}
		resetDueling();
	}

	public void resetDueling() {
		if (isDueling()) {
			actionSender.sendDuelWindowClose();
			setStatus(Action.IDLE);
		}
		setWishToDuel(null);
		setDueling(false);
		setDuelOfferAccepted(false);
		setDuelConfirmAccepted(false);
		resetDuelOffer();
		clearDuelOptions();
	}

	public void resetDuelOffer() {
		duelOffer.clear();
	}

	public void resetFollowing() {
		following = null;
		if (followEvent != null) {
			followEvent.stop();
			followEvent = null;
		}
		resetPath();
	}

	public void resetMenuHandler() {
		menuHandler = null;
		actionSender.hideMenu();
	}

	public void resetRange() {
		if (rangeEvent != null) {
			rangeEvent.stop();
			// Instance.getDelayedEventHandler().remove(rangeEvent);
			rangeEvent = null;
		}
		setStatus(Action.IDLE);
	}

	public void resetShop() {
		if (shop != null) {
			shop.removePlayer(this);
			shop = null;
			actionSender.hideShop();
		}
	}

	/**
	 * Determines if the player is in the smithing interface or not
	 */
	private boolean smithing = false;

	public void setSmithing(boolean b) {
		smithing = b;
	}

	public boolean isSmithing() {
		return smithing;
	}

	public void resetTrade() {
		Player opponent = getWishToTrade();
		if (opponent != null) {
			opponent.resetTrading();
		}
		resetTrading();
	}

	public void resetTradeOffer() {
		tradeOffer.clear();
	}

	// drain
	public void resetTrading() {
		if (isTrading()) {
			actionSender.sendTradeWindowClose();
			setStatus(Action.IDLE);
		}
		setWishToTrade(null);
		setTrading(false);
		setTradeOfferAccepted(false);
		setTradeConfirmAccepted(false);
		resetTradeOffer();
	}

	public void revalidateWatchedItems() {
		for (Item i : watchedItems.getKnownEntities()) {
			if (!withinRange(i) || i.isRemoved() || !i.visibleTo(this)) {
				watchedItems.remove(i);
			}
		}
	}

	public void revalidateWatchedNpcs() {
		for (Npc n : watchedNpcs.getKnownEntities()) {
			if (!withinRange(n) || n.isRemoved()) {
				watchedNpcs.remove(n);
			}
		}
	}

	public void revalidateWatchedObjects() {
		for (GameObject o : watchedObjects.getKnownEntities()) {
			if (!withinRange(o) || o.isRemoved()) {
				watchedObjects.remove(o);
			}
		}
	}

	public void revalidateWatchedPlayers() {
		for (Player p : watchedPlayers.getKnownEntities()) {
			if (!withinRange(p) || !p.loggedIn()) {
				watchedPlayers.remove(p);
				knownPlayersAppearanceIDs.remove(p.getIndex());
			}
		}
	}// destroy

	public void save() {
		SavePacketBuilder builder = new SavePacketBuilder();
		builder.setPlayer(this);
		LSPacket temp = builder.getPacket();
		if (temp != null) {
			World.getWorld().getServer().getLoginConnector().getSession().write(temp);
		}

	}

	public void sayMessage(String msg, Mob to) {
		ChatMessage cm = new ChatMessage(this, msg, to);
		chatMessagesNeedingDisplayed.add(cm);
	}

	public void setAccessingBank(boolean b) {
		inBank = b;
	}

	public void setAccessingShop(Shop shop) {
		this.shop = shop;
		if (shop != null) {
			shop.addPlayer(this);
		}
	}

	public void setAgilityCourseDef(AgilityCourseDef def) {
		agilityCourseDef = def;
	}

	public void setAppearance(Appearance appearance) {
		this.appearance = appearance;
	}

	public void setArrowFired() {
		lastArrow = GameEngine.getAccurateTimestamp();
	}

	public void setBank(Bank b) {
		bank = b;
	}

	public void setCastTimer() {
		lastSpellCast = GameEngine.getAccurateTimestamp();
	}

	public void setChangingAppearance(boolean b) {
		changingAppearance = b;
	}

	public void setCharged() {
		lastCharge = GameEngine.getAccurateTimestamp();
	}

	public void setCombatStyle(int style) {
		combatStyle = style;
	}

	public void setCombo(int combo) {
		this.Combo = combo;
	}

	public void setCurStat(int id, int lvl) {
		if (lvl <= 0) {
			lvl = 0;
		}
		curStat[id] = lvl;
	}

	public void setDrainRate(int rate) {
		drainRate = rate;
	}

	public void setDuelConfirmAccepted(boolean b) {
		duelConfirmAccepted = b;
	}

	public void setDueling(boolean b) {
		isDueling = b;
	}

	public void setDuelOfferAccepted(boolean b) {
		duelOfferAccepted = b;
	}

	public void setDuelSetting(int i, boolean b) {
		duelOptions[i] = b;
	}

	public void setExp(int id, int lvl) {
		if (lvl < 0) {
			lvl = 0;
		}
		exp[id] = lvl;
	}

	public void setExp(int[] lvls) {
		exp = lvls;
	}

	public void handleWakeup() {
		fatigue = tempFatigue;
	}

	public void setFatigue(int fatigue) {
		this.fatigue = fatigue;
		getActionSender().sendFatigue(fatigue);
	}

	public void setFollowing(Mob mob) {
		setFollowing(mob, 0);
	}

	public void setFollowing(final Mob mob, final int radius) {
		if (isFollowing()) {
			resetFollowing();
		}
		following = mob;
		followEvent = new DelayedEvent(this, 500) {

			public void run() {
				if (!owner.withinRange(mob) || mob.isRemoved()
						|| (owner.isBusy() && !owner.isDueling())) {
					resetFollowing();
				} else if (!owner.finishedPath()
						&& owner.withinRange(mob, radius)) {
					owner.resetPath();
				} else if (owner.finishedPath()
						&& !owner.withinRange(mob, radius + 1)) {
					owner.setPath(new Path(owner.getX(), owner.getY(), mob
							.getX(), mob.getY()));
				}
			}
		};
		World.getWorld().getDelayedEventHandler().add(followEvent);
	}

	public void setGameSetting(int i, boolean b) {
		gameSettings[i] = b;
	}

	public void setGroupID(int id) {
		groupID = id;
	}

	public void setHits(int lvl) {
		setCurStat(3, lvl);
	}

	public void setInitialized() {
		initialized = true;
	}

	public void setInventory(Inventory i) {
		inventory = i;
	}

	public void setinvis(boolean arg) {
		invis = arg;
	}

	public void setBlink(boolean arg) {
		blink = arg;
	}

	public void setLastIP(String ip) {
		lastIP = ip;
	}

	public void setLastLogin(long l) {
		lastLogin = l;
	}

	public void setLastReport() {
		lastReport = GameEngine.getAccurateTimestamp();
	}

	public void setLastSaveTime(long save) {
		lastSaveTime = save;
	}

	public void setLoggedIn(boolean loggedIn) {
		if (loggedIn) {
			currentLogin = GameEngine.getAccurateTimestamp();
		}
		this.loggedIn = loggedIn;
	}

	public void setMale(boolean male) {
		maleGender = male;
	}

	public void setMaxStat(int id, int lvl) {
		if (lvl < 0) {
			lvl = 0;
		}
		maxStat[id] = lvl;
	}

	public void setMenuHandler(MenuHandler menuHandler) {
		menuHandler.setOwner(this);
		this.menuHandler = menuHandler;
	}

	// Added by Konijn
	public void setNoclip(boolean noclip) {
		this.noclip = noclip;
	}

	public void setnonaggro(boolean arg) {
		nonaggro = arg;
	}

	public void setnopk(boolean arg) {
		nopk = arg;
	}

	public void setNpc(Npc npc) {
		interactingNpc = npc;
	}

	public void setOwner(int owner) {
		this.owner = owner;
	}

	public void setPrivacySetting(int i, boolean b) {
		privacySettings[i] = b;
	}

	public void setQuestMenuHandler(MenuHandler menuHandler) {
		this.menuHandler = menuHandler;
		menuHandler.setOwner(this);
		actionSender.sendMenu(menuHandler.getOptions());
	}

	// 335000
	public void setRangeEvent(RangeEvent event) {
		if (isRanging()) {
			resetRange();
		}
		rangeEvent = event;
		rangeEvent.setLastRun(lastArrow);
		setStatus(Action.RANGING_MOB);
		World.getWorld().getDelayedEventHandler().add(rangeEvent);
	}

	public void setRequiresOfferUpdate(boolean b) {
		requiresOfferUpdate = b;
	}

	public void setServerKey(long key) {
		sessionKeys[2] = (int) (key >> 32);
		sessionKeys[3] = (int) key;
	}

	public boolean setSessionKeys(int[] keys) {
		boolean valid = (sessionKeys[2] == keys[2] && sessionKeys[3] == keys[3]);
		sessionKeys = keys;
		return valid;
	}


	public void setSkulledOn(Player player) {
		player.addAttackedBy(this);
		if (GameEngine.getAccurateTimestamp() - lastAttackedBy(player) > 1200000) {
			addSkull(1200000);
		}
	}

	public boolean shouldRangePass() {
		int percent = (int) ((this.getMaxStat(5) - 40) * 0.6);
		percent += 60;
		if (percent > 100) 
			percent = 100;
		if (DataConversions.random(0, 100) < percent)
			return false;
		else
			return true;
	}

	public void setSpam(boolean spam) {
		packetSpam = spam;
	}

	public void setSpellFail() {
		lastSpellCast = GameEngine.getAccurateTimestamp() + 20000;
	}

	public void setStatus(Action a) {
		status = a;
	}

	public void setSubscriptionExpires(long expires) {
		subscriptionExpires = expires;
	}

	public void setSuspiciousPlayer(boolean suspicious) {
		String stacktrace = "";
		Exception e = new Exception();
		StringWriter sw = new StringWriter();
	    	PrintWriter pw = new PrintWriter(sw, true);
	    	e.printStackTrace(pw);
	    	pw.flush();
	    	sw.flush();
	    	stacktrace = sw.toString();
		this.suspicious = suspicious;
		Logger.println(this.getUsername() + " was set suspicious! Stacktrace: \n" + stacktrace);
	}

	public void setTradeConfirmAccepted(boolean b) {
		tradeConfirmAccepted = b;
	}

	public void setTradeOfferAccepted(boolean b) {
		tradeOfferAccepted = b;
	}

	public void setTrading(boolean b) {
		isTrading = b;
	}

	public void setWishToDuel(Player p) {
		wishToDuel = p;
	}

	public void setWishToTrade(Player p) {
		wishToTrade = p;
	}

	public void setWornItems(int[] worn) {
		wornItems = worn;
		super.ourAppearanceChanged = true;
	}
	
	public boolean canAccessMembers() {
		return isSubscriber() || isMod() || isAdmin();
	}

	public void teleport(int x, int y, boolean bubble) {
		if (bubble && PluginHandler.getPluginHandler().blockDefaultAction("Teleport", new Object[]{this})) {
			return;
		}
		Mob opponent = super.getOpponent();
		if (inCombat()) {
			resetCombat(CombatState.ERROR);
		}
		int count = getInventory().countId(318);
		if (count > 0) {
			for (int i = 0; i < count; i++) {
				getActionSender().sendMessage(
						"a mysterious force steals your Karamaja rum");
				if (getInventory().remove(new InvItem(318)) > -1) {
					continue;
				} else {
					break;
				}
			}
			getActionSender().sendInventory();
		}
		if (opponent != null) {
			opponent.resetCombat(CombatState.ERROR);
		}
		for (Object o : getWatchedPlayers().getAllEntities()) {
			Player p = ((Player) o);
			if (bubble) {
				p.getActionSender().sendTeleBubble(getX(), getY(), false);
			}
			p.removeWatchedPlayer(this);
		}
		if (bubble) {
			actionSender.sendTeleBubble(getX(), getY(), false);
		}
		setLocation(Point.location(x, y), true);
		resetPath();
		actionSender.sendWorldInfo();
	}

	public boolean tradeDuelThrottling() {
		long now = GameEngine.getAccurateTimestamp();
		if (now - lastTradeDuelRequest > 1000) {
			lastTradeDuelRequest = now;
			return false;
		}
		return true;
	}
	
	public void updateViewedItems() {
		Iterable<Item> itemsInView = viewArea.getItemsInView();
		
		for (Item i : itemsInView) {
			if (!watchedItems.contains(i) && !i.isRemoved() && withinRange(i) && i.visibleTo(this)) {
				watchedItems.add(i);
			}
		} 
	}

	public void updateViewedNpcs() {
		Iterable<Npc> npcsInView = viewArea.getNpcsInView();
		
		for (Npc n : npcsInView) {
			if ((!watchedNpcs.contains(n) || watchedNpcs.isRemoving(n)) && withinRange(n)) {
				watchedNpcs.add(n);
			}
		}
	}

	// killed
	public void updateViewedObjects() {
		Iterable<GameObject> objectsInView = viewArea.getGameObjectsInView();
		
		for (GameObject o : objectsInView) {
			if (!watchedObjects.contains(o) && !o.isRemoved() && withinRange(o)) {
				watchedObjects.add(o);
			}
		}
	}

	public void updateViewedPlayers() {
		Iterable<Player> playersInView = viewArea.getPlayersInView();
		for (Player p : playersInView) {
			if(p != null) {
				if (p.getIndex() != getIndex() && p.loggedIn()) {
					if (!p.isInvis()) {
						informOfPlayer(p);
					}
					if (p.isInvis() && isAdmin()) {
						informOfPlayer(p);
					}
					if (!this.isInvis()) {
						p.informOfPlayer(this);
					}
				}
			}
		}
	}

	public void updateWornItems(int index, int id) {
		wornItems[index] = id;
		super.ourAppearanceChanged = true;
	}

	public HashMap<Long, Long> getAttackedBy() {
		return attackedBy;
	}

	public void setAttackedBy(HashMap<Long, Long> attackedBy) {
		this.attackedBy = attackedBy;
	}

	public LinkedList<ChatMessage> getChatQueue() {
		return chatQueue;
	}

	public void setChatQueue(LinkedList<ChatMessage> chatQueue) {
		this.chatQueue = chatQueue;
	}

	public String getCorrectSleepword() {
		return correctSleepword;
	}

	public void setCorrectSleepword(String correctSleepword) {
		this.correctSleepword = correctSleepword;
	}

	public int[] getCurStat() {
		return curStat;
	}

	public void setCurStat(int[] curStat) {
		this.curStat = curStat;
	}

	public boolean isDestroy() {
		return destroy;
	}

	public void setDestroy(boolean destroy) {
		this.destroy = destroy;
	}

	public DelayedEvent getDrainer() {
		return drainer;
	}

	public void setDrainer(DelayedEvent drainer) {
		this.drainer = drainer;
	}

	public int getDrainerDelay() {
		return drainerDelay;
	}

	public void setDrainerDelay(int drainerDelay) {
		this.drainerDelay = drainerDelay;
	}

	public boolean[] getDuelOptions() {
		return duelOptions;
	}

	public void setDuelOptions(boolean[] duelOptions) {
		this.duelOptions = duelOptions;
	}

	public DelayedEvent getFollowEvent() {
		return followEvent;
	}

	public void setFollowEvent(DelayedEvent followEvent) {
		this.followEvent = followEvent;
	}

	public boolean[] getGameSettings() {
		return gameSettings;
	}

	public void setGameSettings(boolean[] gameSettings) {
		this.gameSettings = gameSettings;
	}

	public boolean isInBank() {
		return inBank;
	}

	public void setInBank(boolean inBank) {
		this.inBank = inBank;
	}

	public boolean isInitialized() {
		return initialized;
	}

	public void setInitialized(boolean initialized) {
		this.initialized = initialized;
	}

	public Npc getInteractingNpc() {
		return interactingNpc;
	}

	public void setInteractingNpc(Npc interactingNpc) {
		this.interactingNpc = interactingNpc;
	}

	public Channel getIoSession() {
		return ioSession;
	}

	public void setIoSession(Channel ioSession) {
		this.ioSession = ioSession;
	}

	public boolean isSleeping() {
		return sleeping;
	}

	public void setSleeping(boolean isSleeping) {
		this.sleeping = isSleeping;
	}

	public HashMap<Integer, Integer> getKnownPlayersAppearanceIDs() {
		return knownPlayersAppearanceIDs;
	}

	public void setKnownPlayersAppearanceIDs(
			HashMap<Integer, Integer> knownPlayersAppearanceIDs) {
		this.knownPlayersAppearanceIDs = knownPlayersAppearanceIDs;
	}

	public String getLastAnswer() {
		return lastAnswer;
	}

	public void setLastAnswer(String lastAnswer) {
		this.lastAnswer = lastAnswer;
	}

	public long getLastArrow() {
		return lastArrow;
	}

	public void setLastArrow(long lastArrow) {
		this.lastArrow = lastArrow;
	}

	public long getLastCast() {
		return lastCast;
	}

	public void setLastCast(long lastCast) {
		this.lastCast = lastCast;
	}

	public long getLastCharge() {
		return lastCharge;
	}

	public void setLastCharge(long lastCharge) {
		this.lastCharge = lastCharge;
	}

	public long getLastCount() {
		return lastCount;
	}

	public void setLastCount(long lastCount) {
		this.lastCount = lastCount;
	}

	public long getLastDeath() {
		return cache.getLong("lastDeath");
	}

	public void setLastDeath() {
		if (!cache.hasKey("lastDeath")) {
			cache.store("lastDeath", System.currentTimeMillis());
			return;
		}
		cache.update("lastDeath", System.currentTimeMillis());
	}

	public LinkedList<RSCPacket> getLastPackets() {
		return lastPackets;
	}

	public long getLastPacketTime() {
		return lastPacketTime;
	}

	public void setLastPacketTime(long lastPacketTime) {
		this.lastPacketTime = lastPacketTime;
	}

	public long getLastRange() {
		return lastRange;
	}

	public void setLastRange(long lastRange) {
		this.lastRange = lastRange;
	}

	public long getLastReport() {
		return lastReport;
	}

	public void setLastReport(long lastReport) {
		this.lastReport = lastReport;
	}

	public long getLastRun() {
		return lastRun;
	}

	public void setLastRun(long lastRun) {
		this.lastRun = lastRun;
	}

	public long getLastSpellCast() {
		return lastSpellCast;
	}

	public void setLastSpellCast(long lastSpellCast) {
		this.lastSpellCast = lastSpellCast;
	}

	public long getLastTradeDuelRequest() {
		return lastTradeDuelRequest;
	}

	public void setLastTradeDuelRequest(long lastTradeDuelRequest) {
		this.lastTradeDuelRequest = lastTradeDuelRequest;
	}

	public boolean isMaleGender() {
		return maleGender;
	}

	public void setMaleGender(boolean maleGender) {
		this.maleGender = maleGender;
	}

	public int[] getMaxStat() {
		return maxStat;
	}

	public void setMaxStat(int[] maxStat) {
		this.maxStat = maxStat;
	}

	public boolean isNopk() {
		return nopk;
	}

	public void setNopk(boolean nopk) {
		this.nopk = nopk;
	}

	public ArrayList<Npc> getNpcsNeedingHitsUpdate() {
		return npcsNeedingHitsUpdate;
	}

	public void setNpcsNeedingHitsUpdate(ArrayList<Npc> npcsNeedingHitsUpdate) {
		this.npcsNeedingHitsUpdate = npcsNeedingHitsUpdate;
	}

	public int getPacketCount() {
		return packetCount;
	}

	public void setPacketCount(int packetCount) {
		this.packetCount = packetCount;
	}

	public boolean isPacketSpam() {
		return packetSpam;
	}

	public void setPacketSpam(boolean packetSpam) {
		this.packetSpam = packetSpam;
	}

	public ArrayList<Player> getPlayersNeedingHitsUpdate() {
		return playersNeedingHitsUpdate;
	}

	public void setPlayersNeedingHitsUpdate(
			ArrayList<Player> playersNeedingHitsUpdate) {
		this.playersNeedingHitsUpdate = playersNeedingHitsUpdate;
	}

	public boolean[] getPrivacySettings() {
		return privacySettings;
	}

	public void setPrivacySettings(boolean[] privacySettings) {
		this.privacySettings = privacySettings;
	}

	public int getSessionFlags() {
		return sessionFlags;
	}

	public void setSessionFlags(int sessionFlags) {
		this.sessionFlags = sessionFlags;
	}

	public DelayedEvent getSkullEvent() {
		return skullEvent;
	}

	public void setSkullEvent(DelayedEvent skullEvent) {
		this.skullEvent = skullEvent;
	}

	public Appearance getAppearance() {
		return appearance;
	}// destroy

	public int[] getExp() {
		return exp;
	}

	public Mob getFollowing() {
		return following;
	}

	public boolean isLoggedIn() {
		return loggedIn;
	}

	public boolean isNoclip() {
		return noclip;
	}

	public RangeEvent getRangeEvent() {
		return rangeEvent;
	}

	public boolean isRequiresOfferUpdate() {
		return requiresOfferUpdate;
	}

	public int[] getSessionKeys() {
		return sessionKeys;
	}

	public long getSubscriptionExpires() {
		return subscriptionExpires;
	}

	public void setBubblesNeedingDisplayed(
			ArrayList<Bubble> bubblesNeedingDisplayed) {
		this.bubblesNeedingDisplayed = bubblesNeedingDisplayed;
	}

	public void setChatMessagesNeedingDisplayed(
			ArrayList<ChatMessage> chatMessagesNeedingDisplayed) {
		this.chatMessagesNeedingDisplayed = chatMessagesNeedingDisplayed;
	}

	public void setCurrentIP(String currentIP) {
		this.currentIP = currentIP;
	}

	public void setCurrentLogin(long currentLogin) {
		this.currentLogin = currentLogin;
	}

	public void setDuelOffer(ArrayList<InvItem> duelOffer) {
		this.duelOffer = duelOffer;
	}

	public void setFriendList(TreeMap<Long, Integer> friendList) {
		this.friendList = friendList;
	}

	public void setIgnoreList(ArrayList<Long> ignoreList) {
		this.ignoreList = ignoreList;
	}

	public void setInvis(boolean invis) {
		this.invis = invis;
	}

	public void setMining(boolean isMining) {
		this.isMining = isMining;
	}

	public void setLastPing(long lastPing) {
		this.lastPing = lastPing;
	}

	public void setNonaggro(boolean nonaggro) {
		this.nonaggro = nonaggro;
	}

	public void setNpcMessagesNeedingDisplayed(
			ArrayList<ChatMessage> npcMessagesNeedingDisplayed) {
		this.npcMessagesNeedingDisplayed = npcMessagesNeedingDisplayed;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setProjectilesNeedingDisplayed(
			ArrayList<Projectile> projectilesNeedingDisplayed) {
		this.projectilesNeedingDisplayed = projectilesNeedingDisplayed;
	}


	public void setReconnecting(boolean reconnecting) {
		this.reconnecting = reconnecting;
	}

	public void setShop(Shop shop) {
		this.shop = shop;
	}

	public void setSuspicious(boolean suspicious) {
		this.suspicious = suspicious;
	}

	public void setTradeOffer(ArrayList<InvItem> tradeOffer) {
		this.tradeOffer = tradeOffer;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setUsernameHash(long usernameHash) {
		this.usernameHash = usernameHash;
	}

	public void setWatchedItems(StatefulEntityCollection<Item> watchedItems) {
		this.watchedItems = watchedItems;
	}

	public void setWatchedNpcs(StatefulEntityCollection<Npc> watchedNpcs) {
		this.watchedNpcs = watchedNpcs;
	}

	public void setWatchedObjects(StatefulEntityCollection<GameObject> watchedObjects) {
		this.watchedObjects = watchedObjects;
	}

	public void setWatchedPlayers(StatefulEntityCollection<Player> watchedPlayers) {
		this.watchedPlayers = watchedPlayers;
	}

	public boolean withinRange(Entity e) {
		if(e != null) {
			int xDiff = location.getX() - e.getLocation().getX();
			int yDiff = location.getY() - e.getLocation().getY();
			return xDiff <= 16 && xDiff >= -15 && yDiff <= 16 && yDiff >= -15;	
		}
		return false;
	}

	@Override
	public String toString() {
		return "[Player:" + username + "]";
	}

	private int smithingbar = -1;

	public void setSmithingBar(int id) {
		this.smithingbar = id;
	}

	public int getSmithingBar() {
		return smithingbar;
	}

	/**
	 * Players sleepword
	 */
	private String sleepword;

	public String getSleepword() {
		return sleepword;
	}

	public void setSleepword(String sleepword) {
		this.sleepword = sleepword;
	}

	public int getXpTillNextLevel(int skill) {
		int currentLevelXp = this.getExp(skill);
		int nextLevel = Formulae.experienceToLevel(currentLevelXp) + 1;
		int nextLevelXP = Formulae.leveToRealExperience(nextLevel);
		return nextLevelXP - currentLevelXp;
	}

	private final Map<Integer, Integer> questStages = new ConcurrentHashMap<>();

	public int getQuestStage(QuestInterface q) {
		if(questStages.containsKey(q.getQuestId())) {
			return questStages.get(q.getQuestId());
		}
		return 0;
	}

	public void setQuestStage(Quest q, int stage) {
		questStages.put(q.getQuestId(), stage);
		actionSender.sendQuestInfo(q.getQuestId(), stage);
	}

	public void setQuestStage(int q, int stage) {
		questStages.put(q, stage);
		actionSender.sendQuestInfo(q, stage);
	}

	public void sendQuestComplete(int questId) { // REMEMBER THIS
		world.getQuest(questId).handleReward(this);
		actionSender.sendQuestPoints();
	}

	private int questPoints = 0;

	public void incQuestPoints(int amount) {
		questPoints += amount;
	}

	public int getQuestPoints() {
		return questPoints;
	}

	private final Cache cache = new Cache();

	public int dropTickCount = -1;

	public Cache getCache() {
		return cache;
	}

	public void startSleepEvent(final boolean bed) {
		sleepEvent = new DelayedEvent(this, 600) {
			@Override
			public void run() {
				if (owner.isDestroy() || tempFatigue == 0 || !sleeping) {
					matchRunning = false;
					return;
				}

				if (bed) {
					owner.tempFatigue -= 2100;
				} else {
					owner.tempFatigue -= 431;
				}

				if (owner.tempFatigue < 0) {
					owner.tempFatigue = 0;
				}

				owner.actionSender.sendFatigue(owner.tempFatigue / 10);
			}
		};

		tempFatigue = fatigue;
		actionSender.sendFatigue(tempFatigue / 10);
		World.getWorld().getDelayedEventHandler().add(sleepEvent);
	}
	
	public Scriptable getScriptHelper() {
		return script;
	}
	
	public Area getCurrentArea() {
		return area.get();
	}
	
	@Override
	public void setLocation(Point p) {
		this.setLocation(p, false);
	}
	
	@Override
	public void setLocation(Point p, boolean teleported) {
		Area r = Area.getArea(p);
        Area cur = area.get();

        if (cur != r) {
            if (cur != null) {
                cur.removePlayer(this);
            }

            r.addPlayer(this);
            area.getAndSet(r);
        }
        //System.out.println(r.toString());
        super.setLocation(p, teleported);
	}
}
