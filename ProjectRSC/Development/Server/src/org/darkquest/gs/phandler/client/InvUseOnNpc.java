package org.darkquest.gs.phandler.client;

import org.darkquest.config.Constants;
import org.darkquest.gs.connection.Packet;
import org.darkquest.gs.event.ShortEvent;
import org.darkquest.gs.event.impl.WalkToMobEvent;
import org.darkquest.gs.model.Bubble;
import org.darkquest.gs.model.InvItem;
import org.darkquest.gs.model.Npc;
import org.darkquest.gs.model.Player;
import org.darkquest.gs.phandler.PacketHandler;
import org.darkquest.gs.states.Action;
import org.darkquest.gs.tools.DataConversions;
import org.darkquest.gs.world.World;
import org.jboss.netty.channel.Channel;


public class InvUseOnNpc implements PacketHandler {
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
        int sh = -125534;
        try {
            sh = p.readShort();
        } catch (NullPointerException npe) {
            return;
        }

        final Npc affectedNpc = world.getNpc(sh);
        final InvItem item = player.getInventory().get(p.readShort());
        if (affectedNpc == null || item == null) { // This shouldn't happen
            return;
        }

        //Services.lookup(DatabaseManager.class).addQuery(new GenericLog(player.getUsername() + " used item " + item + " on npc " + affectedNpc.getDef().getName() + " at " + player.getLocation()));

        player.setFollowing(affectedNpc);
        player.setStatus(Action.USING_INVITEM_ON_NPC);
        World.getWorld().getDelayedEventHandler().add(new WalkToMobEvent(player, affectedNpc, 1) {
            public void arrived() {
                owner.resetPath();
                owner.resetFollowing();
                if (!owner.getInventory().contains(item) || owner.isBusy() || owner.isRanging() || !owner.nextTo(affectedNpc) || affectedNpc.isBusy() || owner.getStatus() != Action.USING_INVITEM_ON_NPC) {
                    return;
                }
                owner.resetAll();

                if (item.getDef().isMembers() && !Constants.GameServer.MEMBER_WORLD) {
                    owner.getActionSender().sendMessage("Nothing interesting happens.");
                    return;
                }

                switch (affectedNpc.getID()) {
                    case 2: // Sheep
                        if (!itemId(new int[]{144})) {
                            owner.getActionSender().sendMessage("Nothing interesting happens.");
                            return;
                        }
                        owner.setBusy(true);
                        affectedNpc.blockedBy(owner);
                        affectedNpc.resetPath();
                        showBubble();
                        owner.getActionSender().sendMessage("You attempt to shear the sheep");
                        World.getWorld().getDelayedEventHandler().add(new ShortEvent(owner) {
                            public void action() {
                                if (DataConversions.random(0, 4) != 0) {
                                    owner.getActionSender().sendMessage("You get some wool");
                                    owner.getInventory().add(new InvItem(145, 1));
                                    owner.getActionSender().sendInventory();
                                } else {
                                    owner.getActionSender().sendMessage("The sheep manages to get away from you!");
                                }
                                owner.setBusy(false);
                                affectedNpc.unblock();
                            }
                        });
                        break;
                    case 217:// Cow
                    case 6:
                        if (item.getID() != 21) {
                            owner.getActionSender().sendMessage("Nothing interesting happens.");
                            return;
                        }
                        owner.setBusy(true);
                        affectedNpc.blockedBy(owner);
                        affectedNpc.resetPath();
                        Bubble bubble = new Bubble(owner, 21);
                        for (Player p : owner.getViewArea().getPlayersInView()) {
                            p.informOfBubble(bubble);
                        }
                        owner.getActionSender().sendMessage("You try to milk the cow.");
                        World.getWorld().getDelayedEventHandler().add(new ShortEvent(owner) {
                            public void action() {
                                if (DataConversions.random(0, 4) != 0) {
                                    if (owner.getInventory().remove(item) < 0)
                                        return;
                                    owner.getActionSender().sendMessage("You get some milk");
                                    owner.getInventory().add(new InvItem(22, 1));
                                    owner.getActionSender().sendInventory();
                                } else {
                                    owner.getActionSender().sendMessage("The cow runs away before you could get any milk");
                                }
                                owner.setBusy(false);
                                affectedNpc.unblock();
                            }
                        });
                        break;
                    case 160:// Thrander
                        int newID;
                        switch (item.getID()) {
                            case 308: // Bronze top
                                newID = 117;
                                break;
                            case 312: // Iron top
                                newID = 8;
                                break;
                            case 309: // Steel top
                                newID = 118;
                                break;
                            case 313: // Black top
                                newID = 196;
                                break;
                            case 310: // Mithril top
                                newID = 119;
                                break;
                            case 311: // Adamantite top
                                newID = 120;
                                break;
                            case 407: // Rune top
                                newID = 401;
                                break;
                            case 117: // Bronze body
                                newID = 308;
                                break;
                            case 8: // Iron body
                                newID = 312;
                                break;
                            case 118: // Steel body
                                newID = 309;
                                break;
                            case 196: // Black body
                                newID = 313;
                                break;
                            case 119: // Mithril body
                                newID = 310;
                                break;
                            case 120: // Adamantite body
                                newID = 311;
                                break;
                            case 401: // Rune body
                                newID = 407;
                                break;
                            case 214: // Bronze skirt
                                newID = 206;
                                break;
                            case 215: // Iron skirt
                                newID = 9;
                                break;
                            case 225: // Steel skirt
                                newID = 121;
                                break;
                            case 434: // Black skirt
                                newID = 248;
                                break;
                            case 226: // Mithril skirt
                                newID = 122;
                                break;
                            case 227: // Adamantite skirt
                                newID = 123;
                                break;
                            case 406: // Rune skirt
                                newID = 402;
                                break;
                            case 206: // Bronze legs
                                newID = 214;
                                break;
                            case 9: // Iron legs
                                newID = 215;
                                break;
                            case 121: // Steel legs
                                newID = 225;
                                break;
                            case 248: // Black legs
                                newID = 434;
                                break;
                            case 122: // Mithril legs
                                newID = 226;
                                break;
                            case 123: // Adamantite legs
                                newID = 227;
                                break;
                            case 402: // Rune legs
                                newID = 406;
                                break;
                            default:
                                owner.getActionSender().sendMessage("Nothing interesting happens.");
                                return;
                        }
                        final InvItem newPlate = new InvItem(newID, 1);
                        owner.getActionSender().sendMessage("Thrander hammers the armour");
                        World.getWorld().getDelayedEventHandler().add(new ShortEvent(owner) {
                            public void action() {
                                if (owner.getInventory().remove(item) > -1) {
                                    owner.getInventory().add(newPlate);
                                    owner.getActionSender().sendInventory();
                                }
                                affectedNpc.unblock();
                            }
                        });
                        affectedNpc.blockedBy(owner);
                        break;
                    default:
                        owner.getActionSender().sendMessage("Nothing interesting happens.");
                        break;
                }
            }

            private boolean itemId(int[] ids) {
                return DataConversions.inArray(ids, item.getID());
            }

            private void showBubble() {
                Bubble bubble = new Bubble(owner, item.getID());
                for (Player p : owner.getViewArea().getPlayersInView()) {
                    p.informOfBubble(bubble);
                }
            }
        });
    }

}
