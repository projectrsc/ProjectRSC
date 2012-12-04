package com.prsc.config;

import com.prsc.gs.model.Mob;
import com.prsc.gs.model.Npc;
import com.prsc.gs.model.Player;

public class Ephemeral_Combat_Formula {


	private static final int[] FIGHT_MODES = {1, 3, 0, 0};

	public static int getYourNextHit(Mob atker, Mob defend) {
		if(atker instanceof Player && defend instanceof Player) { // PvP
			Player player = (Player)atker;
			Player defender = (Player)defend;
			return getNextHit(player.getCurStat(2), player.getWeaponPowerPoints(), player.getCurStat(0), player.getWeaponAimPoints(), defender.getCurStat(1), defender.getArmourPoints());
		} else if(atker instanceof Player && defend instanceof Npc){ // Player hitting NPC
			Player player = (Player)atker;
			Npc n = (Npc)defend;
			
			return getNextHit(player.getCurStat(2), player.getWeaponPowerPoints(), player.getCurStat(0), player.getWeaponAimPoints(), n.getDef().getDef(), defend.getArmourPoints());
		} else if(atker instanceof Npc && defend instanceof Player){ // NPC hitting Player
			Player player = (Player)defend;
			Npc n = (Npc)atker;
			return getNextHit(n.getDef().getStr(), atker.getWeaponPowerPoints(), n.getDef().getAtt(), atker.getWeaponAimPoints(), player.getCurStat(1), defend.getArmourPoints());
		}
		return 0;
		
	}

	private static int getNextHit(int strength, int power, int attack, int aim, int defence, int armour) {
		return isNextHitValid(attack, aim, defence, armour) ? (int)(Math.random() * getMaxHit(strength, power)) : 0;
	}

	private static int getMaxHit(int strength, int power) {
		return (int)Math.ceil(((strength) + FIGHT_MODES[0]) * ((power * 0.00175) + 0.1) + 1.05);
	}

	private static boolean isNextHitValid(int attack, int aim, int defence, int armour) {
		return (Math.random() * 100D) <= (attack + aim) / ((attack + aim + defence + armour) / 100D);
	}

}

