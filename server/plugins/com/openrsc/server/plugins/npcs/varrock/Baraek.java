package com.openrsc.server.plugins.npcs.varrock;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;

import static com.openrsc.server.plugins.Functions.*;

public final class Baraek implements
	TalkNpcTrigger {

	@Override
	public void onTalkNpc(final Player p, final Npc n) {
		int menu;
		boolean bargained = false;
		boolean hasFur = p.getCarriedItems().hasCatalogID(ItemId.FUR.id());
		if (canGetInfoGang(p) && hasFur) {
			menu = multi(p, n, false, //do not send over
				"Can you tell me where I can find the phoenix gang?",
				"Can you sell me some furs?",
				"Hello. I am in search of a quest",
				"Would you like to buy my fur?");
		} else if (canGetInfoGang(p) && !hasFur) {
			menu = multi(p, n, false, //do not send over
				"Can you tell me where I can find the phoenix gang?",
				"Can you sell me some furs?",
				"Hello. I am in search of a quest");
		} else if (hasFur) {
			menu = multi(p, n, false, //do not send over
				"Can you sell me some furs?",
				"Hello. I am in search of a quest",
				"Would you like to buy my fur?");
			if (menu >= 0) {
				menu += 1;
			}
		} else {
			menu = multi(p, n, false, //do not send over
				"Can you sell me some furs?",
				"Hello. I am in search of a quest");
			if (menu >= 0) {
				menu += 1;
			}
		}
		if (menu == 0) {
			say(p, n, "Can you tell me where I can find the phoenix gang?");
			npcsay(p, n, "Sh Sh, not so loud",
				"You don't want to get me in trouble");
			say(p, n, "So do you know where they are?");
			npcsay(p, n, "I may do",
				"Though I don't want to get into trouble for revealing their hideout",
				"Now if I was say 20 gold coins richer",
				"I may happen to be more inclined to take that sort of risk");
			int sub_menu = multi(p, n, "Okay have 20 gold coins",
				"No I don't like things like bribery",
				"Yes I'd like to be 20 gold coins richer too");
			if (sub_menu == 0) {
				if (!ifheld(p, ItemId.COINS.id(), 20)) {
					say(p, n, "Oops. I don't have 20 coins. Silly me.");
				} else {
					p.getCarriedItems().remove(new Item(ItemId.COINS.id(), 20));
					npcsay(p, n,
						"Cheers",
						"Ok to get to the gang hideout",
						"After entering Varrock through the south gate",
						"If you take the first turning east",
						"Somewhere along there is an alleyway to the south",
						"The door at the end of there is the entrance to the phoenix gang",
						"They're operating there under the name of the VTAM corporation",
						"Be careful",
						"The phoenix gang ain't the types to be messed with");
					say(p, n, "Thanks");
					if (p.getQuestStage(Quests.SHIELD_OF_ARRAV) == 2) {
						p.updateQuestStage(Quests.SHIELD_OF_ARRAV, 3);
					}
				}
			} else if (sub_menu == 1) {
				npcsay(p, n, "Heh, if you wanna deal with the phoenix gang",
					"They're involved in much worse than a bit of bribery");
			} else if (sub_menu == 2) {
				//nothing
			}
		} else if (menu == 1) {
			say(p, n, "Can you sell me some furs?");
			npcsay(p, n, "Yeah sure they're 20 gold coins a piece");
			int opts = multi(p, n, false, //do not send over
				"Yeah, okay here you go",
				"20 gold coins thats an outrage");
			if (opts == 0) {
				if (!ifheld(p, ItemId.COINS.id(), 20)) {
					say(p, n, "Oh dear I don't seem to have enough money");
					npcsay(p, n, "Well, okay I'll go down to 18 coins");
					bargained = true;
				} else {
					say(p, n, "Yeah okay here you go");
					p.getCarriedItems().remove(new Item(ItemId.COINS.id(), 20));
					p.message("You buy a fur from Baraek");
					p.getCarriedItems().getInventory().add(new Item(ItemId.FUR.id()));
				}
			} else if (opts == 1) {
				say(p, n, "20 gold coins that's an outrage");
				npcsay(p, n, "Well, okay I'll go down to 18");
				bargained = true;
			}
		} else if (menu == 2) {
			say(p, n, "Hello I am in search of a quest");
			npcsay(p, n,
				"Sorry kiddo, I'm a fur trader not a damsel in distress");
		} else if (menu == 3) {
			say(p, n, "Would you like to buy my fur?");
			npcsay(p, n, "Lets have a look at it");
			p.message("Baraek examines a fur");
			npcsay(p, n, "It's not in the best of condition",
				"I guess I could give 12 coins to take it off your hands");
			int opts = multi(p, n, "Yeah that'll do", "I think I'll keep hold of it actually");
			if (opts == 0) {
				mes(p, "You give Baraek a fur",
					"And he gives you twelve coins");
				p.getCarriedItems().remove(new Item(ItemId.FUR.id()));
				give(p, ItemId.COINS.id(), 12);
			} else if (opts == 1) {
				npcsay(p, n, "Oh ok", "Didn't want it anyway");
			}
		}

		if (bargained) {
			int sub_opts = multi(p, n, false, //do not send over
				"Okay here you go", "No thanks I'll leave it");
			if (sub_opts == 0) {
				if (!ifheld(p, ItemId.COINS.id(), 18)) {
					say(p, n, "Oh dear I don't seem to have enough money");
					npcsay(p, n, "Well I can't go any cheaper than that mate",
						"I have a family to feed");
				} else {
					say(p, n, "Okay here you go");
					p.getCarriedItems().remove(new Item(ItemId.COINS.id(), 18));
					p.message("You buy a fur from Baraek");
					p.getCarriedItems().getInventory().add(new Item(ItemId.FUR.id()));
				}
			} else if (sub_opts == 1) {
				say(p, n, "No thanks, I'll leave it");
				npcsay(p, n, "It's your loss mate");
			}
		}
	}

	private boolean canGetInfoGang(Player p) {
		return p.getQuestStage(Quests.SHIELD_OF_ARRAV) == 2
				|| (p.getQuestStage(Quests.SHIELD_OF_ARRAV) == 3 && !p.getCache().hasKey("arrav_mission"));
	}

	@Override
	public boolean blockTalkNpc(Player p, Npc n) {
		return n.getID() == NpcId.BARAEK.id();
	}

}
