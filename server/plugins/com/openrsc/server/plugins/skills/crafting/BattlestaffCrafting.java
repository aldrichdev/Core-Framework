package com.openrsc.server.plugins.skills.crafting;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.Skills;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.UseInvTrigger;
import com.openrsc.server.util.rsc.MessageType;

import static com.openrsc.server.plugins.Functions.*;

public class BattlestaffCrafting implements UseInvTrigger {

	private boolean canCraft(Item itemOne, Item itemTwo) {
		for (Battlestaff c : Battlestaff.values()) {
			if (c.isValid(itemOne.getCatalogId(), itemTwo.getCatalogId())) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void onUseInv(Player p, Item item1, Item item2) {
		Battlestaff combine = null;
		for (Battlestaff c : Battlestaff.values()) {
			if (c.isValid(item1.getCatalogId(), item2.getCatalogId())) {
				combine = c;
			}
		}
		if (p.getSkills().getLevel(Skills.CRAFTING) < combine.requiredLevel) {
			p.playerServerMessage(MessageType.QUEST, "You need a crafting level of " + combine.requiredLevel + " to make " + resultItemString(combine));
			return;
		}
		if (p.getCarriedItems().remove(new Item(combine.itemID)) != -1
			&& p.getCarriedItems().remove(new Item(combine.itemIDOther)) != -1) {
			if (combine.messages.length > 1)
				mes(p, combine.messages[0]);
			else
				p.message(combine.messages[0]);

			give(p, combine.resultItem, 1);
			p.incExp(Skills.CRAFTING, combine.experience, true);

			if (combine.messages.length > 1)
				p.message(combine.messages[1]);
		}
	}

	@Override
	public boolean blockUseInv(Player player, Item item1, Item item2) {
		return canCraft(item1, item2);
	}

	private String resultItemString(Battlestaff combinedItem) {
		String name;
		switch (combinedItem) {
			case WATER_BATTLESTAFF:
				name = "a water battlestaff";
				break;
			case EARTH_BATTLESTAFF:
				// kosher: didn't say "an earth"
				name = "a earth battlestaff";
				break;
			case FIRE_BATTLESTAFF:
				name = "a fire battlestaff";
				break;
			case AIR_BATTLESTAFF:
				name = "an air battlestaff";
				break;
			default:
				// unimplemented battlestaff or not known
				name = "this";
		}
		return name;
	}

	enum Battlestaff {
		WATER_BATTLESTAFF(ItemId.BATTLESTAFF.id(), ItemId.WATER_ORB.id(), ItemId.BATTLESTAFF_OF_WATER.id(), 400, 54, ""),
		EARTH_BATTLESTAFF(ItemId.BATTLESTAFF.id(), ItemId.EARTH_ORB.id(), ItemId.BATTLESTAFF_OF_EARTH.id(), 450, 58, ""),
		FIRE_BATTLESTAFF(ItemId.BATTLESTAFF.id(), ItemId.FIRE_ORB.id(), ItemId.BATTLESTAFF_OF_FIRE.id(), 500, 62, ""),
		AIR_BATTLESTAFF(ItemId.BATTLESTAFF.id(), ItemId.AIR_ORB.id(), ItemId.BATTLESTAFF_OF_AIR.id(), 550, 66, "");

		private int itemID;
		private int itemIDOther;
		private int resultItem;
		private int experience;
		private int requiredLevel;
		private String[] messages;

		Battlestaff(int itemOne, int itemTwo, int resultItem, int experience, int level, String... messages) {
			this.itemID = itemOne;
			this.itemIDOther = itemTwo;
			this.resultItem = resultItem;
			this.experience = experience;
			this.requiredLevel = level;
			this.messages = messages;
		}

		public boolean isValid(int i, int is) {
			return itemID == i && itemIDOther == is || itemIDOther == i && itemID == is;
		}
	}
}
