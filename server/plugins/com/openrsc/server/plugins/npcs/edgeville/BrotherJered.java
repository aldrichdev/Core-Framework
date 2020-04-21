package com.openrsc.server.plugins.npcs.edgeville;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;

import java.util.Optional;

import static com.openrsc.server.plugins.Functions.*;

public class BrotherJered implements
	TalkNpcTrigger {

	@Override
	public void onTalkNpc(Player p, Npc n) {
		int option = multi(p, n, "What can you do to help a bold adventurer like myself?", "Praise be to Saradomin");
		if (option == 0) {
			if (!p.getCarriedItems().hasCatalogID(ItemId.UNBLESSED_HOLY_SYMBOL.id(), Optional.of(false))
				&& !p.getCarriedItems().hasCatalogID(ItemId.UNSTRUNG_HOLY_SYMBOL_OF_SARADOMIN.id(), Optional.of(false))) {
				npcsay(p, n, "If you have a silver star",
						"Which is the holy symbol of Saradomin",
						"Then I can bless it",
						"Then if you are wearing it",
						"It will help you when you are praying");
			} else if (p.getCarriedItems().hasCatalogID(ItemId.UNBLESSED_HOLY_SYMBOL.id(), Optional.of(false))) {
				npcsay(p, n, "Well I can bless that star of Saradomin you have");
				int sub_option = multi(p, n, false, //do not send over
						"Yes Please", "No thankyou");
				if (sub_option == 0) {
					p.getCarriedItems().remove(new Item(ItemId.UNBLESSED_HOLY_SYMBOL.id()));
					say(p, n, "Yes Please");
					mes(p, "You give Jered the symbol",
							"Jered closes his eyes and places his hand on the symbol",
							"He softly chants",
							"Jered passes you the holy symbol");
					give(p, ItemId.HOLY_SYMBOL_OF_SARADOMIN.id(), 1);
				} else if (sub_option == 1) {
					say(p, n, "No Thankyou");
				}
			} else if (p.getCarriedItems().hasCatalogID(ItemId.UNSTRUNG_HOLY_SYMBOL_OF_SARADOMIN.id(), Optional.of(false))) {
				npcsay(p, n, "Well if you put a string on that holy symbol",
						"I can bless it for you\"");
			}
		} else if (option == 1) {
			npcsay(p, n, "Yes praise he who brings life to this world");
		}
	}

	@Override
	public boolean blockTalkNpc(Player p, Npc n) {
		return n.getID() == NpcId.BROTHER_JERED.id();
	}

}
