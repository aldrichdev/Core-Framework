package com.openrsc.server.plugins.npcs.varrock;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;

import java.util.Optional;

import static com.openrsc.server.plugins.Functions.*;
import static com.openrsc.server.plugins.quests.free.ShieldOfArrav.isBlackArmGang;

public class King implements TalkNpcTrigger {

	@Override
	public boolean blockTalkNpc(Player p, Npc n) {
		return n.getID() == NpcId.KING.id();
	}

	@Override
	public void onTalkNpc(Player p, Npc n) {
		if (p.getCarriedItems().hasCatalogID(ItemId.CERTIFICATE.id(), Optional.of(false))) {
			say(p, n, "Your majesty", "I have come to claim the reward",
				"For the return of the shield of Arrav");
			if (p.getQuestStage(Quests.SHIELD_OF_ARRAV) == 5) {
				mes(p, "You show the certificate to the king");
				npcsay(p, n, "My goodness",
					"This is the claim for a reward put out by my father",
					"I never thought I'd see anyone claim this reward",
					"I see you are claiming half the reward",
					"So that would come to 600 gold coins");
				mes(p, "You hand over a certificate",
					"The king gives you 600 coins");
				p.getCarriedItems().remove(new Item(ItemId.CERTIFICATE.id()));
				p.sendQuestComplete(Quests.SHIELD_OF_ARRAV);
				if (isBlackArmGang(p))
					p.updateQuestStage(Quests.SHIELD_OF_ARRAV, -2);
				return;
			} else if (p.getQuestStage(Quests.SHIELD_OF_ARRAV) >= 0) {
				npcsay(p, n, "The name on this certificate isn't yours!",
					"I can't give you the reward",
					"Unless you do the quest yourself");
			} else {
				npcsay(p, n, "You have already claimed the reward",
					"You can't claim it twice");
				mes(p, "Why don't you give this certificate",
					"To whoever helped you get the shield");
			}
			return;
		} else if (p.getCarriedItems().hasCatalogID(ItemId.BROKEN_SHIELD_ARRAV_1.id(), Optional.of(false))
			&& p.getCarriedItems().hasCatalogID(ItemId.BROKEN_SHIELD_ARRAV_2.id(), Optional.of(false))) {
			say(p, n, "Your majesty",
				"I have recovered the shield of Arrav",
				"I would like to claim the reward");
			npcsay(p, n, "The shield of Arrav, eh?",
				"Yes, I do recall my father putting a reward out for that",
				"Very well",
				"Go get the authenticity of the shield verified",
				"By the curator at the museum",
				"And I will grant you your reward");
			return;
		}
		say(p, n, "Greetings, your majesty");
		npcsay(p, n, "Do you have anything of import to say?");
		say(p, n, "Not really");
		npcsay(p, n, "You will have to excuse me then", "I am very busy",
			"I have a kingdom to run");
	}
}
