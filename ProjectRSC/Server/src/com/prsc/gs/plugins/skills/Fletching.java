package com.prsc.gs.plugins.skills;

import com.prsc.config.Constants;
import com.prsc.gs.event.MiniEvent;
import com.prsc.gs.external.EntityHandler;
import com.prsc.gs.external.ItemArrowHeadDef;
import com.prsc.gs.external.ItemBowStringDef;
import com.prsc.gs.external.ItemDartTipDef;
import com.prsc.gs.external.ItemLogCutDef;
import com.prsc.gs.model.InvItem;
import com.prsc.gs.model.MenuHandler;
import com.prsc.gs.model.Player;
import com.prsc.gs.model.World;
import com.prsc.gs.plugins.listeners.executive.InvUseOnItemExecutiveListener;


public class Fletching implements InvUseOnItemExecutiveListener {

    @Override
    public boolean blockInvUseOnItem(Player player, InvItem item1, InvItem item2) {
        if (item1.getID() == 381 && attachFeathers(player, item1, item2)) {
            return true;
        } else if (item2.getID() == 381 && attachFeathers(player, item2, item1)) {
            return true;
        } else if (item1.getID() == 676 && doBowString(player, item1, item2)) {
            return true;
        } else if (item2.getID() == 676 && doBowString(player, item2, item1)) {
            return true;
        } else if (item1.getID() == 637 && doArrowHeads(player, item1, item2)) {
            return true;
        } else if (item2.getID() == 637 && doArrowHeads(player, item2, item1)) {
            return true;
        } else if (item1.getID() == 13 && doLogCut(player, item1, item2)) {
            return true;
        } else if (item2.getID() == 13 && doLogCut(player, item2, item1)) {
            return true;
        }
        return false;
    }

    private boolean attachFeathers(Player player, final InvItem feathers, final InvItem item) {
    	if(!Constants.GameServer.MEMBER_WORLD) {
    		player.sendMemberErrorMessage();
    		return true;
    	}
        int amount = 10;
        if (feathers.getAmount() < amount) {
            amount = feathers.getAmount();
        }
        if (item.getAmount() < amount) {
            amount = item.getAmount();
        }
        InvItem newItem;
        int exp;
        ItemDartTipDef tipDef = null;
        if (item.getID() == 280) {
            newItem = new InvItem(637, amount);
            exp = amount;
        } else if ((tipDef = EntityHandler.getItemDartTipDef(item.getID())) != null) {
            newItem = new InvItem(tipDef.getDartID(), amount);
            exp = (int) (tipDef.getExp() * (double) amount);
        } else {
            return false;
        }
        final int amt = amount;
        final int xp = exp;
        final InvItem newItm = newItem;
        World.getWorld().getDelayedEventHandler().add(new MiniEvent(player) {
            public void action() {
                if (owner.getInventory().remove(feathers.getID(), amt) > -1 && owner.getInventory().remove(item.getID(), amt) > -1) {
                    owner.getActionSender().sendMessage("You attach the feathers to the " + item.getDef().getName());
                    owner.getInventory().add(newItm);
                    owner.incExp(9, xp, true);
                    owner.getActionSender().sendStat(9);
                    owner.getActionSender().sendInventory();
                }
            }
        });
        return true;
    }

    private boolean doArrowHeads(Player player, final InvItem headlessArrows, final InvItem arrowHeads) {
    	if(!Constants.GameServer.MEMBER_WORLD) {
    		player.sendMemberErrorMessage();
    		return true;
    	}
        final ItemArrowHeadDef headDef = EntityHandler.getItemArrowHeadDef(arrowHeads.getID());
        if (headDef == null) {
            return false;
        }
        if (player.getCurStat(9) < headDef.getReqLevel()) {
            player.getActionSender().sendMessage("You need a fletching level of " + headDef.getReqLevel() + " to attach those.");
            return true;
        }
        int amount = 10;
        if (headlessArrows.getAmount() < amount) {
            amount = headlessArrows.getAmount();
        }
        if (arrowHeads.getAmount() < amount) {
            amount = arrowHeads.getAmount();
        }
        final int amt = amount;
        World.getWorld().getDelayedEventHandler().add(new MiniEvent(player) {
            public void action() {
                if (owner.getInventory().remove(headlessArrows.getID(), amt) > -1 && owner.getInventory().remove(arrowHeads.getID(), amt) > -1) {
                    owner.getActionSender().sendMessage("You attach the heads to the arrows");
                    owner.getInventory().add(new InvItem(headDef.getArrowID(), amt));
                    owner.incExp(9, (int) (headDef.getExp() * (double) amt), true);
                    owner.getActionSender().sendStat(9);
                    owner.getActionSender().sendInventory();
                }
            }
        });
        return true;
    }

    private boolean doBowString(Player player, final InvItem bowString, final InvItem bow) {
    	if(!Constants.GameServer.MEMBER_WORLD) {
    		player.sendMemberErrorMessage();
    		return true;
    	}
        final ItemBowStringDef stringDef = EntityHandler.getItemBowStringDef(bow.getID());
        if (stringDef == null) {
            return false;
        }
        if (player.getCurStat(9) < stringDef.getReqLevel()) {
            player.getActionSender().sendMessage("You need a fletching level of " + stringDef.getReqLevel() + " to do that.");
            return true;
        }
        World.getWorld().getDelayedEventHandler().add(new MiniEvent(player) {
            public void action() {
                if (owner.getInventory().remove(bowString) > -1 && owner.getInventory().remove(bow) > -1) {
                    owner.getActionSender().sendMessage("You add the bow string to the bow");
                    owner.getInventory().add(new InvItem(stringDef.getBowID(), 1));
                    owner.incExp(9, stringDef.getExp(), true);
                    owner.getActionSender().sendStat(9);
                    owner.getActionSender().sendInventory();
                }
            }
        });
        return true;
    }

    private boolean doLogCut(final Player player, final InvItem knife, final InvItem log) {
    	if(!Constants.GameServer.MEMBER_WORLD) {
    		player.sendMemberErrorMessage();
    		return true;
    	}
        final ItemLogCutDef cutDef = EntityHandler.getItemLogCutDef(log.getID());
        if (cutDef == null) {
            return false;
        }
        World.getWorld().getDelayedEventHandler().add(new MiniEvent(player) {
            public void action() {
                String[] options = new String[]{"Arrow shafts", "Shortbow", "Longbow", "Cancel"};
                owner.setMenuHandler(new MenuHandler(options) {
                    public void handleReply(final int option, final String reply) {
                        InvItem result;
                        int reqLvl, exp;
                        switch (option) {
                            case 0:
                                result = new InvItem(280, cutDef.getShaftAmount());
                                reqLvl = cutDef.getShaftLvl();
                                exp = cutDef.getShaftExp();
                                break;
                            case 1:
                                result = new InvItem(cutDef.getShortbowID(), 1);
                                reqLvl = cutDef.getShortbowLvl();
                                exp = cutDef.getShortbowExp();
                                break;
                            case 2:
                                result = new InvItem(cutDef.getLongbowID(), 1);
                                reqLvl = cutDef.getLongbowLvl();
                                exp = cutDef.getLongbowExp();
                                break;
                            default:
                                return;
                        }
                        if (owner.getCurStat(9) < reqLvl) {
                            owner.getActionSender().sendMessage("You need a fletching level of " + reqLvl + " to cut that.");
                            return;
                        }
                        if (owner.getInventory().remove(log) > -1) {
                            owner.getActionSender().sendMessage("You make a " + result.getDef().getName());
                            owner.getInventory().add(result);
                            owner.incExp(9, exp, true);
                            owner.getActionSender().sendStat(9);
                            owner.getActionSender().sendInventory();
                        }
                        if (owner.getInventory().hasItemId(log.getID())) {
                        	if(Constants.GameServer.BATCH_EVENTS)
                        		doLogCut(player, knife, log);
                        }
                    }
                });
                owner.getActionSender().sendMenu(options);
            }
        });
        return true;
    }

}
