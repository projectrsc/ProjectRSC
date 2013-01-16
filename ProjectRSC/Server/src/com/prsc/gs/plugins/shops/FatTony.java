package com.prsc.gs.plugins.shops;

import com.prsc.gs.model.InvItem;
import com.prsc.gs.model.Npc;
import com.prsc.gs.model.Player;
import com.prsc.gs.model.Shop;
import com.prsc.gs.model.component.mob.Scriptable;
import com.prsc.gs.plugins.ShopInterface;
import com.prsc.gs.plugins.listeners.action.TalkToNpcListener;
import com.prsc.gs.plugins.listeners.executive.TalkToNpcExecutiveListener;

public class FatTony implements ShopInterface, TalkToNpcListener, TalkToNpcExecutiveListener {
	
	private final int FAT_TONY = 235;
	private final Shop shop = new Shop(false, 5000, 4, 2, new InvItem(321, 30));
	
	@Override
	public Shop[] getShops() {
		return new Shop[]{shop};
	}

	@Override
	public boolean isMembers() {
		return false;
	}
	
	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		return n.getID() == FAT_TONY;
	}

	@Override
	public void onTalkToNpc(Player p, Npc n) {
		Scriptable script = p.getScriptHelper();
		script.setActiveNpc(n);
		script.occupy();
				
		script.sendNpcChat("Go away I'm very busy");	
		int option = script.pickOption(new String[]{"Sorry to disturb you", "What are you busy doing?", "Have you anything to sell?"});
		if(option == 1) {
			script.sendNpcChat("I'm cooking pizzas for the people in this camp", "Not that these louts appreciate my gourmet cooking");
			int sub_option = script.pickOption(new String[]{"So what is a gourmet chef doing cooking for bandits?", "Can I have some pizza too?", "OK I'll leave you to it"});
			if(sub_option == 0) {
				script.sendNpcChat("Well I'm an outlaw", "I was accused of giving the king food poisoning", "The thought of it - I think he just drank too much wine that night", 
										"I had to flee the kingdom of Misthalin");
				int remaining_option = script.pickOption(new String[]{"Can I have some pizza too?", "OK I'll leave you to it"});
				if(remaining_option == 0) {
					wantsPizza(script, p);
				}
				else if(sub_option == 1) {
					wantsPizza(script, p);
				}
			} else if(sub_option == 1) {
				wantsPizza(script, p);
			}
		}
		else if(option == 2) {
			script.sendNpcChat("Well I guess I can sell you some half made pizzas");
			p.setAccessingShop(shop);
			p.getActionSender().showShop(shop);
		}
		script.release();
	}
	
	private void wantsPizza(Scriptable script, Player player) {
		script.sendNpcChat("Well this pizza is really meant for the bandits");
		int next_option = script.pickOption(new String[]{"Yes Okay", "Oh if I have to pay I don't want any"});
		if(next_option == 0) {
			script.sendNpcChat("I guess I could sell you some pizza bases though");
			player.setAccessingShop(shop);
			player.getActionSender().showShop(shop);
		}
		script.release();
	}
}
