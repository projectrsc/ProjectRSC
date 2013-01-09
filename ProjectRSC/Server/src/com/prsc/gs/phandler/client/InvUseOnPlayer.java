package com.prsc.gs.phandler.client;

import org.jboss.netty.channel.Channel;

import com.prsc.config.Constants;
import com.prsc.gs.connection.Packet;
import com.prsc.gs.event.ShortEvent;
import com.prsc.gs.event.impl.WalkToMobEvent;
import com.prsc.gs.model.Bubble;
import com.prsc.gs.model.InvItem;
import com.prsc.gs.model.Player;
import com.prsc.gs.phandler.PacketHandler;
import com.prsc.gs.states.Action;
import com.prsc.gs.tools.DataConversions;
import com.prsc.gs.world.World;


public class InvUseOnPlayer implements PacketHandler {
    /**
     * World instance
     */
    public static final World world = World.getWorld();

    public void handlePacket(Packet p, Channel session) throws Exception {
        Player player = (Player) session.getAttachment();
        if (player.isBusy()) {
            player.resetPath();
            return;
        }
        player.resetAll();
        final Player affectedPlayer = world.getPlayer(p.readShort());
        final InvItem item = player.getInventory().get(p.readShort());
        if (affectedPlayer == null || item == null) {
            return;
        }
        if (System.currentTimeMillis() - affectedPlayer.getLastRun() < 2000) {
            player.resetPath();
            return;
        }

        //Services.lookup(DatabaseManager.class).addQuery(new GenericLog(player.getUsername() + " used item " + item + " on player " + affectedPlayer.getUsername() + " at " + player.getLocation()));

        player.setFollowing(affectedPlayer);
        player.setStatus(Action.USING_INVITEM_ON_PLAYER);
        World.getWorld().getDelayedEventHandler().add(new WalkToMobEvent(player, affectedPlayer, 1) {
            public void arrived() {
                owner.resetPath();
                owner.resetFollowing();
                if (!owner.getInventory().contains(item) || !owner.nextTo(affectedPlayer) || owner.isBusy() || owner.isRanging() || owner.getStatus() != Action.USING_INVITEM_ON_PLAYER) {
                    return;
                }
                owner.resetAll();

                if (item.getDef().isMembers() && !Constants.GameServer.MEMBER_WORLD) {
                    owner.getActionSender().sendMessage("Nothing interesting happens.");
                    return;
                }

                switch (item.getID()) {
                    case 575: // Christmas cracker
                        owner.setBusy(true);
                        affectedPlayer.setBusy(true);
                        owner.resetPath();
                        affectedPlayer.resetPath();
                        Bubble crackerBubble = new Bubble(owner, 575);
                        for (Player p : owner.getViewArea().getPlayersInView()) {
                            p.informOfBubble(crackerBubble);
                        }
                        owner.getActionSender().sendMessage("You pull the cracker with " + affectedPlayer.getUsername() + "...");
                        affectedPlayer.getActionSender().sendMessage(owner.getUsername() + " is pulling a cracker with you...");
                        World.getWorld().getDelayedEventHandler().add(new ShortEvent(owner) {
                            public void action() {
                                InvItem phat = new InvItem(DataConversions.random(576, 581));
                                if (DataConversions.random(0, 1) == 1) {
                                    owner.getActionSender().sendMessage("Out comes a " + phat.getDef().getName() + "!");
                                    affectedPlayer.getActionSender().sendMessage(owner.getUsername() + " got the contents!");
                                    owner.getInventory().add(phat);
                                } else {
                                    owner.getActionSender().sendMessage(affectedPlayer.getUsername() + " got the contents!");
                                    affectedPlayer.getActionSender().sendMessage("Out comes a " + phat.getDef().getName() + "!");
                                    affectedPlayer.getInventory().add(phat);
                                }
                                owner.getInventory().remove(item);
                                owner.setBusy(false);
                                affectedPlayer.setBusy(false);
                                owner.getActionSender().sendInventory();
                                affectedPlayer.getActionSender().sendInventory();
                            }
                        });
                        break;
                    default:
                        owner.getActionSender().sendMessage("Nothing interesting happens.");
                        break;
                }
            }
        });
    }
}