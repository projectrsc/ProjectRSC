package org.darkquest.gs.plugins.misc;

import org.darkquest.config.Constants;
import org.darkquest.gs.event.ShortEvent;
import org.darkquest.gs.model.GameObject;
import org.darkquest.gs.model.InvItem;
import org.darkquest.gs.model.Player;
import org.darkquest.gs.plugins.listeners.action.ObjectActionListener;
import org.darkquest.gs.plugins.listeners.executive.ObjectActionExecutiveListener;
import org.darkquest.gs.world.World;

public class RandomObjects implements ObjectActionExecutiveListener, ObjectActionListener {

	@Override
	public void onObjectAction(final GameObject object, String command, Player owner) {	
		if (command.equals("search") && object.getGameObjectDef().getName().equals("cupboard")) {
			owner.getActionSender().sendMessage("You search the " + object.getGameObjectDef().getName() + "...");
			World.getWorld().getDelayedEventHandler().add(new ShortEvent(owner) {
				public void action() {
					if (object.getX() == 216 && object.getY() == 1562) {
						owner.getActionSender().sendMessage("You find Garlic!");
						owner.getInventory().add(new InvItem(218));
						owner.getActionSender().sendInventory();
					} else {
						owner.getActionSender().sendMessage("You find nothing");
					}
				}
			});
			return;
		} else if (command.equals("board")) {
			owner.getActionSender().sendMessage("You must talk to the owner about this.");
			return;
		} else {
			switch (object.getID()) {
			case 613: // Shilo cart
				if (object.getX() != 384 || object.getY() != 851) {
					return;
				}
				owner.setBusy(true);
				owner.getActionSender().sendMessage("You search for a way over the cart");
				World.getWorld().getDelayedEventHandler().add(new ShortEvent(owner) {
					public void action() {
						owner.getActionSender().sendMessage("You climb across");
						if (owner.getX() <= 383) {
							owner.teleport(386, 851, false);
						} 
						else {
							owner.teleport(383, 851, false);
						}
						owner.setBusy(false);
					}
				});
				break;
			case 643: // Gnome tree stone
				if (object.getX() != 416 || object.getY() != 161) {
					return;
				}
				owner.setBusy(true);
				owner.getActionSender().sendMessage("You twist the stone tile to one side");
				World.getWorld().getDelayedEventHandler().add(new ShortEvent(owner) {
					public void action() {
						owner.getActionSender().sendMessage("It reveals a ladder, you climb down");
						owner.teleport(703, 3284, false);
						owner.setBusy(false);
					}
				});
				break;
			case 638: // First roots in gnome cave
				if (object.getX() != 701 || object.getY() != 3280) {
					return;
				}
				owner.setBusy(true);
				owner.getActionSender().sendMessage("You push the roots");
				World.getWorld().getDelayedEventHandler().add(new ShortEvent(owner) {
					public void action() {
						owner.getActionSender().sendMessage("They wrap around you and drag you forwards");
						owner.teleport(701, 3278, false);
						owner.setBusy(false);
					}
				});
			case 639: // Second roots in gnome cave
				if (object.getX() != 701 || object.getY() != 3279) {
					return;
				}
				owner.setBusy(true);
				owner.getActionSender().sendMessage("You push the roots");
				World.getWorld().getDelayedEventHandler().add(new ShortEvent(owner) {
					public void action() {
						owner.getActionSender().sendMessage("They wrap around you and drag you forwards");
						owner.teleport(701, 3281, false);
						owner.setBusy(false);
					}
				});
				break;
			}

			if (object.getX() == 94 && object.getY() == 521 && object.getID() == 60) {
				try {
					int x = owner.getX() == 94 ? 93 : 94, y = owner.getY();
					owner.teleport(x, y, false);
					//System.out.println(x + ", " + y);
					owner.getActionSender().sendMessage("@red@If this is still here by release, please contact Hikilaka to fix it!");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return;
		}
	}

	@Override
	public boolean blockObjectAction(GameObject obj, String command, Player player) {
		if (obj.getID() == 613 || obj.getID() == 638 || obj.getID() == 639 || obj.getID() == 643
				|| command.equals("board") || (command.equals("search") && obj.getGameObjectDef().getName().equals("cupboard"))) {
			return true;
		}
		//System.out.println(obj.getX() + ", " + obj.getY() + ", " + obj.getID());
		
		if (obj.getLocation().getX() == 94 && obj.getLocation().getY() == 521 && obj.getID() == 60) {
			if (Constants.GameServer.MEMBER_WORLD) {
				return true;
			}
		}
		return false;
	}

}
