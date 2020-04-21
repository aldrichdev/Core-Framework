package com.openrsc.server.plugins.quests.members;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.constants.Skills;
import com.openrsc.server.external.Gauntlets;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.GroundItem;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.QuestInterface;
import com.openrsc.server.plugins.triggers.*;

import java.util.Optional;

import static com.openrsc.server.plugins.Functions.*;

public class FamilyCrest implements QuestInterface, TalkNpcTrigger,
	OpLocTrigger,
	OpBoundTrigger,
	UseNpcTrigger,
	KillNpcTrigger {

	@Override
	public int getQuestId() {
		return Quests.FAMILY_CREST;
	}

	@Override
	public String getQuestName() {
		return "Family crest (members)";
	}

	@Override
	public boolean isMembers() {
		return true;
	}

	@Override
	public void handleReward(Player player) {
		incQuestReward(player, player.getWorld().getServer().getConstants().getQuests().questData.get(Quests.FAMILY_CREST), true);
		player.message("@gre@You haved gained 1 quest point!");
		player.message("Well done you have completed the family crest quest");
	}

	/**
	 * NPCS: #309 - Dimintheis - quest starter #310 - Chef - 1st son in catherby
	 * #307 - man in alkharid #314 - wizard 3rd son.
	 */
	private static void dimintheisDialogue(Player p, Npc n, int cID) {
		if (cID == -1) {
			switch (p.getQuestStage(Quests.FAMILY_CREST)) {
				case 0:
					npcsay(p, n, "Hello, my name is Dimintheis",
						"Of the noble family of Fitzharmon");
					//do not send over
					int menu = multi(p, n, false,
						"Why would a nobleman live in a little hut like this?",
						"You're rich then?, can I have some money?",
						"Hi, I am bold adventurer");
					if (menu == 0) {
						say(p, n, "Why would a nobleman live in a little hut like this?");
						npcsay(p, n, "The king has taken my estate from me",
							"Until I can show him my family crest");
						int first = multi(p, n, "Why would he do that?",
							"So where is this crest?");
						if (first == 0) {
							dimintheisDialogue(p, n, Dimintheis.TRADITION);
						} else if (first == 1) {
							dimintheisDialogue(p, n, Dimintheis.THREE_SONS);
						}
					} else if (menu == 1) {
						say(p, n, "You're rich then?", "Can I have some money?");
						npcsay(p, n, "Lousy beggar",
							"There's to many of your sort about these days",
							"If I gave money to each of you who asked",
							"I'd be living on the streets myself");
					} else if (menu == 2) {
						say(p, n, "Hi, I am a bold adventurer");
						npcsay(p, n, "An adventurer hmm?",
							"I may have an adventure for you",
							"I desperatly need my family crest returning to me");
						int menu2 = multi(p, n,
							"Why are you so desperate for it?",
							"So where is this crest?",
							"I'm not interested in that adventure right now");
						if (menu2 == 0) {
							dimintheisDialogue(p, n, Dimintheis.TRADITION);
						} else if (menu2 == 1) {
							dimintheisDialogue(p, n, Dimintheis.THREE_SONS);
						}
					}
					break;
				case 1:
					say(p, n, "Where did you say I could find Caleb?");
					npcsay(p,
						n,
						"I heard word that my son Caleb is alive trying to earn his fortune",
						"As a great chef, far away in the lands beyond white wolf mountain");
					break;
				case 2:
				case 3:
				case 4:
				case 5:
				case 6:
				case 7:
				case 8:
					boolean gave_crest = false;
					if (p.getCarriedItems().hasCatalogID(ItemId.FAMILY_CREST.id(), Optional.of(false))) {
						say(p, n, "I have retrieved your crest");
						p.message("You give the crest to Dimintheis");
						p.getCarriedItems().remove(new Item(ItemId.FAMILY_CREST.id()));
						gave_crest = true;
					} else if (p.getCarriedItems().hasCatalogID(ItemId.CREST_FRAGMENT_ONE.id(), Optional.of(false))
						&& p.getCarriedItems().hasCatalogID(ItemId.CREST_FRAGMENT_TWO.id(), Optional.of(false))
						&& p.getCarriedItems().hasCatalogID(ItemId.CREST_FRAGMENT_THREE.id(), Optional.of(false))) {
						say(p, n, "I have retrieved your crest");
						p.message("You give the parts of the crest to Dimintheis");
						p.getCarriedItems().remove(new Item(ItemId.CREST_FRAGMENT_ONE.id()));
						p.getCarriedItems().remove(new Item(ItemId.CREST_FRAGMENT_TWO.id()));
						p.getCarriedItems().remove(new Item(ItemId.CREST_FRAGMENT_THREE.id()));
						gave_crest = true;
					}
					if (gave_crest) {
						p.getCache().remove("north_leverA");
						p.getCache().remove("south_lever");
						p.getCache().remove("north_leverB");
						npcsay(p, n, "Thankyou for your kindness",
							"I cannot express my gratitude enough",
							"You truly are a great hero");
						p.sendQuestComplete(Quests.FAMILY_CREST);
						npcsay(p,
							n,
							"How can I reward you I wonder?",
							"I suppose these gauntlets would make a good reward",
							"If you die you will always retain these gauntlets");
						p.message("Dimintheis gives you a pair of gauntlets");
						give(p, ItemId.STEEL_GAUNTLETS.id(), 1);
						p.getCache().set("famcrest_gauntlets", Gauntlets.STEEL.id());
						npcsay(p,
							n,
							"These gautlets can be granted extra powers",
							"Take them to one of my boys, they can each do something to them",
							"Though they can only receive one of the three powers");

						return;
					}
					npcsay(p, n, "How are you doing finding the crest");
					say(p, n, "I don't have it yet");
					break;
				case -1:
					npcsay(p, n, "Thankyou for saving our family honour",
						"We will never forget you");
					break;
			}
		}
		switch (cID) {
			case Dimintheis.THREE_SONS:
				npcsay(p,
					n,
					"Well my 3 sons took it with them many years ago",
					"When they rode out to fight in the war",
					"Against the undead necromancer and his army",
					"I didn't hear from them for many years and mourned them dead",
					"However recently I heard word that my son Caleb is alive",
					"trying to earn his fortune",
					"As a great chef, far away in the lands beyond white wolf mountain");
				int menu3 = multi(p, n, "Ok I will help you",
					"I'm not interested in that adventure right now");
				if (menu3 == 0) {
					npcsay(p, n, "I thank you greatly",
						"If you find Caleb send him my love");
					p.updateQuestStage(Quests.FAMILY_CREST, 1); // QUEST STARTED.
				}
				break;
			case Dimintheis.TRADITION:
				npcsay(p,
					n,
					"We have this tradition in the Varrocian arostocracy",
					"Each noble family has an ancient crest",
					"This represents the honour and lineage of the family",
					"If you are to lose this crest, the family's estate is given to the crown",
					"until the crest is returned",
					"In times past when there was much infighting between the various families",
					"Capturing a family's crest meant you captured their land");
				say(p, n, "so where is this crest?");
				dimintheisDialogue(p, n, Dimintheis.THREE_SONS);
				break;
		}
	}

	@Override
	public boolean blockTalkNpc(Player p, Npc n) {
		return n.getID() == NpcId.DIMINTHEIS.id() || n.getID() == NpcId.AVAN.id() || n.getID() == NpcId.JOHNATHON.id();
	}

	@Override
	public void onTalkNpc(Player p, Npc n) {
		if (n.getID() == NpcId.DIMINTHEIS.id()) {
			dimintheisDialogue(p, n, -1);
		}
		else if (n.getID() == NpcId.AVAN.id()) {
			switch (p.getQuestStage(this)) {
				case -1:
					npcsay(p, n, "I have heard word from my father",
						"Thankyou for helping to restore our family honour");
					if (p.getCarriedItems().hasCatalogID(ItemId.STEEL_GAUNTLETS.id(), Optional.of(false))
						&& getGauntletEnchantment(p) == Gauntlets.STEEL.id()) {
						say(p, n,
							"Your father said that you could improve these Gauntlets in some way for me");
						npcsay(p,
							n,
							"Indeed I can",
							"In my quest to find the perfect gold I learned a lot",
							"I can make it so when you're wearing these");
						npcsay(p, n, "You gain more experience when smithing gold");
						int menu = multi(p, n,
							"That sounds good, improve them for me",
							"I think I'll check my other options with your brothers");
						if (menu == 0) {
							mes(p, "Avan takes out a little hammer",
								"He starts pounding on the gauntlets",
								"Avan hands the gauntlets to you");
							p.getCarriedItems().getInventory().replace(ItemId.STEEL_GAUNTLETS.id(), ItemId.GAUNTLETS_OF_GOLDSMITHING.id());
							p.getCache().set("famcrest_gauntlets", Gauntlets.GOLDSMITHING.id());
						} else if (menu == 1) {
							npcsay(p, n,
								"Ok if you insist on getting help from the likes of them");
						}
					}
					break;
				case 0:
				case 1:
				case 2:
				case 3:
					npcsay(p, n, "Can't you see I'm busy?");
					break;
				case 4:
					int menu = multi(p, n,
						"Why are you hanging around in a scorpion pit?",
						"I'm looking for a man named Avan");
					if (menu == 0) {
						npcsay(p, n, "It's a good place to find gold");
					} else if (menu == 1) {
						npcsay(p, n, "I'm called Avan yes");
						say(p, n, "You have part of a crest",
							"I have been sent to fetch it");
						npcsay(p, n,
							"Is one of my good for nothing brothers after it again?");
						say(p, n, "no your father would like it back");
						npcsay(p,
							n,
							"Oh Dad wants it this time",
							"Well I'll tell you what I'll do",
							"I'm trying to obtain the perfect jewellry",
							"There is a lady I am trying to impress",
							"What I want is a gold ring with a red stone in",
							"And a necklace to match",
							"Not just any gold mind you",
							"The gold in these rocks doesn't seem to be of the best quality",
							"I want as good a quality as you can get");
						say(p, n, "Any ideas where I can find that?");
						npcsay(p,
							n,
							"Well I have been looking for such gold for a while",
							"My latest lead was a dwarf named Boot",
							"Though he has gone back to his home in the mountain now");
						say(p, n, "Ok I will try to get what you are after");
						p.updateQuestStage(this, 5);
					}
					break;
				case 5:
					npcsay(p, n, "So how are you doing getting the jewellry?");
					say(p, n, "I'm still after that perfect gold");
					npcsay(p, n,
						"Well I have been looking for such gold for a while",
						"My latest lead was a dwarf named Boot",
						"Though he has gone back to his home in the mountain now");
					break;
				case 6:
					npcsay(p, n, "So how are you doing getting the jewellry?");
					if (p.getCarriedItems().hasCatalogID(ItemId.RUBY_RING_FAMILYCREST.id(), Optional.of(false))
						&& p.getCarriedItems().hasCatalogID(ItemId.RUBY_NECKLACE_FAMILYCREST.id(), Optional.of(false))) {
						say(p, n, "I have it");
						npcsay(p, n, "These are brilliant");
						p.message("You exchange the jewellry for a piece of crest");
						p.getCarriedItems().remove(new Item(ItemId.RUBY_RING_FAMILYCREST.id()));
						p.getCarriedItems().remove(new Item(ItemId.RUBY_NECKLACE_FAMILYCREST.id()));
						give(p, ItemId.CREST_FRAGMENT_TWO.id(), 1);
						npcsay(p,
							n,
							"These are a fine piece of work",
							"Such marvelous gold to",
							"I suppose you will be after the last piece of crest now",
							"I heard my brother Johnathon is now a young mage",
							"He is hunting some demon in the wilderness",
							"But he's not doing a very good job of it",
							"He spends most his time recovering in an inn",
							"on the edge of the wilderness");
						p.updateQuestStage(this, 7);
					} else {
						say(p, n,
							"I have spoken to boot about the perfect gold",
							"I haven't bought you your jewellry yet though");
						npcsay(p, n,
							"Remember I want a gold ring with a red stone in",
							"And a necklace to match");
					}
					break;
				case 7:
					say(p, n,
						"Where did you say I could find Johnathon again?");
					npcsay(p, n,
						"I heard my brother Johnathon is now a young mage",
						"He is hunting some demon in the wilderness",
						"But he's not doing a very good job of it",
						"He spends most his time recovering in an inn",
						"on the edge of the wilderness");
					break;
				case 8:
					npcsay(p, n, "How are you doing getting the rest of the crest?");
					if (!p.getCarriedItems().hasCatalogID(ItemId.CREST_FRAGMENT_TWO.id(), Optional.of(false))) {
						int menu2 = multi(p, n,
							"I am still working on it",
							"I have lost the piece you gave me");
						if (menu2 == 0) {
							npcsay(p, n, "Well good luck in your quest");
						} else if (menu2 == 1) {
							npcsay(p, n, "Ah well here is another one");
							give(p, ItemId.CREST_FRAGMENT_TWO.id(), 1);
						}
					} else {
						say(p, n, "I am still working on it");
						npcsay(p, n, "Well good luck in your quest");
					}
					break;
			}
		}
		else if (n.getID() == NpcId.JOHNATHON.id()) {
			if (p.getQuestStage(this) >= 0 && p.getQuestStage(this) < 7) {
				npcsay(p, n, "I am so very tired, leave me to rest");
			} else if (p.getQuestStage(this) == 7) {
				say(p, n, "Greetings, are you Johnathon Fitzharmon?");
				npcsay(p, n, "That is I");
				say(p, n,
					"I seek your fragment of the Fitzharmon family quest");
				npcsay(p, n, "The poison it is too much",
					"arrgh my head is all of a spin");
				p.message("Sweat is pouring down Johnathon's face");
			} else if (p.getQuestStage(this) == 8) {
				npcsay(p, n,
					"I'm trying to kill the demon chronozon  that you mentioned");
				int menu = multi(p, n,
					"So is this Chronozon hard to defeat?",
					"Where can I find Chronozon?", "Wish me luck");
				if (menu == 0) {
					DEFEAT(p, n);
				} else if (menu == 1) {
					FIND(p, n);
				} else if (menu == 2) {
					npcsay(p, n, "Good luck");
				}
			} else if (p.getQuestStage(this) == -1) {
				npcsay(p, n, "Hello again");
				if (p.getCarriedItems().hasCatalogID(ItemId.STEEL_GAUNTLETS.id(), Optional.of(false)) && getGauntletEnchantment(p) == Gauntlets.STEEL.id()) {
					say(p, n,
						"Your father tells me, you can improve these gauntlets a bit");
					npcsay(p,
						n,
						"He would be right",
						"Though I didn't get good enough at the death spells to defeat chronozon",
						"I am pretty good at the chaos spells",
						"I can enchant your gauntlets so that your bolt spells are more effective");
					int menu = multi(p, n, "That sounds good to me",
						"I shall see what options your brothers can offer me first");
					if (menu == 0) {
						mes(p, "Johnathon waves his staff",
							"The gauntlets sparkle and shimmer");
						p.getCarriedItems().getInventory().replace(ItemId.STEEL_GAUNTLETS.id(), ItemId.GAUNTLETS_OF_CHAOS.id());
						p.getCache().set("famcrest_gauntlets", Gauntlets.CHAOS.id());
					} else if (menu == 0) {
						npcsay(p, n,
							"Boring crafting and cooking enhacements knowing them");
					}
				} else {
					npcsay(p, n, "My family now considers you a hero");
				}
			}
		}
	}

	public static int getGauntletEnchantment(Player p) {
		try {
			return p.getCache().getInt("famcrest_gauntlets");
		} catch (Exception e) {
			return Gauntlets.STEEL.id();
		}
	}

	/**
	 * DOORS: #91 - front door #88 - south left door #90 - south right door #92
	 * - north door
	 * <p>
	 * p.message("The door is locked"); p.message("The door swings open");
	 * p.message("You go through the door");
	 **/

	@Override
	public boolean blockOpBound(GameObject obj, Integer click, Player p) {
		return (obj.getID() == 88 && obj.getX() == 509 && obj.getY() == 3441) || (obj.getID() == 90 && obj.getX() == 512 && obj.getY() == 3441)
				|| obj.getID() == 91 || obj.getID() == 92;
	}

	@Override
	public void onOpBound(GameObject obj, Integer click, Player p) {
		switch (obj.getID()) {
			case 88:
				if (p.getCache().hasKey("north_leverA")
					&& p.getCache().hasKey("south_lever")
					&& p.getCache().getBoolean("north_leverA")
					&& p.getCache().getBoolean("south_lever")) {
					p.message("The door swings open");
					p.message("You go through the door");
					doDoor(obj, p);
				} else if (p.getCache().hasKey("north_leverA")
					&& p.getCache().hasKey("north_leverB")
					&& p.getCache().hasKey("south_lever")
					&& p.getCache().getBoolean("north_leverA")
					&& p.getCache().getBoolean("north_leverB")
					&& p.getCache().getBoolean("south_lever")) {
					p.message("The door swings open");
					p.message("You go through the door");
					doDoor(obj, p);

				} else {
					p.message("The door is locked");
				}
				break;
			case 90:
				if (p.getCache().hasKey("north_leverA")
					&& p.getCache().hasKey("south_lever")
					&& p.getCache().getBoolean("north_leverA")
					&& !p.getCache().getBoolean("south_lever")) {
					p.message("The door swings open");
					p.message("You go through the door");
					doDoor(obj, p);
				} else if (
					p.getCache().hasKey("north_leverA")
						&& p.getCache().hasKey("north_leverB")
						&& p.getCache().hasKey("south_lever")
						&& p.getCache().getBoolean("north_leverA")
						&& p.getCache().getBoolean("north_leverB")
						&& !p.getCache().getBoolean("south_lever")) {
					p.message("The door swings open");
					p.message("You go through the door");
					doDoor(obj, p);
				} else {
					p.message("The door is locked");
				}
				break;
			case 91:
				if (p.getCache().hasKey("north_leverA")
					&& p.getCache().hasKey("north_leverB")
					&& p.getCache().hasKey("south_lever")
					&& p.getCache().getBoolean("north_leverA")
					&& p.getCache().getBoolean("north_leverB")
					&& !p.getCache().getBoolean("south_lever")) {
					p.message("The door swings open");
					p.message("You go through the door");
					doDoor(obj, p);
				} else if (p.getQuestStage(this) == -1) { // FREE ACCESS TO THE
					// HELLHOUND ROOM AFTER
					// COMPLETING QUEST
					p.message("The door swings open");
					p.message("You go through the door");
					doDoor(obj, p);
				} else {
					p.message("The door is locked");
				}
				break;
			case 92:
				if (p.getCache().hasKey("north_leverA")
					&& (p.getCache().hasKey("north_leverB")
					|| p.getCache().hasKey("south_lever"))
					&& !p.getCache().getBoolean("north_leverA")
					&& (p.getCache().getBoolean("south_lever") || p.getCache()
					.getBoolean("north_leverB"))) {
					p.message("The door swings open");
					p.message("You go through the door");
					doDoor(obj, p);
				} else {
					p.message("The door is locked");
				}
				break;
		}
	}

	/**
	 * LEVERS: #316 - north lever infront of door #318 - north lever inside door
	 * #317 - south lever inside door
	 * <p>
	 * p.message("You pull the lever down"); p.message("you hear a clunk");
	 **/

	@Override
	public boolean blockOpLoc(GameObject obj, String command, Player p) {
		return obj.getID() == 316 || obj.getID() == 317 || obj.getID() == 318;
	}

	@Override
	public void onOpLoc(GameObject obj, String command, Player p) {
		if (command.equalsIgnoreCase("pull") && (obj.getID() == 316 || obj.getID() == 317 || obj.getID() == 318))
			doLever(p, obj.getID());
		else if (command.equalsIgnoreCase("inspect") && (obj.getID() == 316 || obj.getID() == 317 || obj.getID() == 318))
			inspectLever(p, obj.getID());
	}

	public void inspectLever(Player p, int objectID) {
		if (p.getQuestStage(Quests.FAMILY_CREST) == -1) {
			p.message("nothing interesting happens"); // SAID SO ON WIKI.
			return;
		}
		String leverName = null;
		if (objectID == 316) {
			leverName = "north_leverA";
		} else if (objectID == 317) {
			leverName = "south_lever";
		} else if (objectID == 318) {
			leverName = "north_leverB";
		}
		p.message("The lever is "
			+ (p.getCache().hasKey(leverName) && p.getCache().getBoolean(leverName) ? "down" : "up"));
	}

	public void doLever(Player p, int objectID) {
		if (p.getQuestStage(Quests.FAMILY_CREST) == -1) {
			p.message("nothing interesting happens"); // SAID SO ON WIKI.
			return;
		}
		if (!p.getCache().hasKey("north_leverA")) {
			p.getCache().store("north_leverA", false);
			p.getCache().store("south_lever", false);
			p.getCache().store("north_leverB", false);
		}
		String leverName = null;
		if (objectID == 316) {
			leverName = "north_leverA";
		} else if (objectID == 317) {
			leverName = "south_lever";
		} else if (objectID == 318) {
			leverName = "north_leverB";
		}
		p.getCache().store(leverName, !p.getCache().getBoolean(leverName));
		p.message("You pull the lever "
			+ (p.getCache().getBoolean(leverName) ? "down" : "up"));
		p.message("you hear a clunk");
	}

	@Override
	public boolean blockUseNpc(Player player, Npc npc, Item item) {
		return npc.getID() == NpcId.JOHNATHON.id() && item.getCatalogId() == ItemId.FULL_CURE_POISON_POTION.id();
	}

	@Override
	public void onUseNpc(Player p, Npc n, Item item) {
		if (n.getID() == NpcId.JOHNATHON.id() && item.getCatalogId() == ItemId.FULL_CURE_POISON_POTION.id()) {
			if (p.getQuestStage(this) == 7) {
				mes(p, "You feed your potion to Johnathon");
				p.getCarriedItems().remove(new Item(ItemId.FULL_CURE_POISON_POTION.id()));
				p.updateQuestStage(this, 8);
				npcsay(p, n, "Wow I'm feeling a lot better now",
					"Thankyou, what can I do for you?");
				say(p, n,
					"I'm after your part of the fitzharmon family crest");
				npcsay(p,
					n,
					"Ooh I don't think I have that anymore",
					"I have been trying to slay chronozon the blood demon",
					"and I think I dropped a lot of my things near him when he drove me away",
					"He will have it now");
				int menu = multi(p, n,
					"So is this Chronozon hard to defeat?",
					"Where can I find Chronozon?",
					"So how did you end up getting poisoned");
				if (menu == 0) {
					DEFEAT(p, n);
				} else if (menu == 1) {
					FIND(p, n);
				} else if (menu == 2) {
					POISONED(p, n);
				}
			} else {
				p.message("nothing interesting happens");
			}
		}
	}

	private void DEFEAT(Player p, Npc n) {
		npcsay(p, n, "Well you will need to be a good mage",
			"And I don't seem to be able to manage it",
			"He will need to be hit by the 4 elemental spells of death",
			"Before he can be defeated");
		int menu = multi(p, n, "Where can I find Chronozon?",
			"So how did you end up getting poisoned",
			"I will be on my way now");
		if (menu == 0) {
			FIND(p, n);
		} else if (menu == 1) {
			POISONED(p, n);
		}
	}

	private void POISONED(Player p, Npc n) {
		npcsay(p, n,
			"There are spiders towards the entrance to Chronozon's cave",
			"I must have taken a nip from one of them");
		int menu2 = multi(p, n, "So is this Chronozon hard to defeat?",
			"Where can I find Chronozon?", "I will be on my way now");
		if (menu2 == 0) {
			DEFEAT(p, n);
		} else if (menu2 == 1) {
			FIND(p, n);
		}
	}

	private void FIND(Player p, Npc n) {
		npcsay(p, n,
			"He is in the wilderness, somewhere below the obelisk of air");
		int menu = multi(p, n, "So is this Chronozon hard to defeat?",
			"So how did you end up getting poisoned",
			"I will be on my way now");
		if (menu == 0) {
			DEFEAT(p, n);
		} else if (menu == 1) {
			POISONED(p, n);
		}
	}

	@Override
	public boolean blockKillNpc(Player p, Npc n) {
		return n.getID() == NpcId.CHRONOZON.id();
	}

	/**
	 * LAST FIGHT MESSAGES p.message("chronozon weakens");
	 * p.message("Chronozon regenerates"); FINALLY IF ALL SPELLS CASTED HE DIES
	 * AND DROP LAST CREST PIECE: ID: 697 (ONLY DROPPING IF QUEST STAGE 8)
	 */

	@Override
	public void onKillNpc(Player p, Npc n) {
		if (n.getID() == NpcId.CHRONOZON.id()) {
			String[] elementals = new String[]{"wind", "water", "earth",
				"fire"};
			boolean regenerate = false;
			for (String s : elementals) {
				if (!p.getAttribute("chronoz_" + s, false)) {
					regenerate = true;
					break;
				}
			}
			if (regenerate) {
				n.getSkills().setLevel(Skills.HITS, n.getDef().hits);
				p.message("Chronozon regenerates");
			} else {
				if (p.getQuestStage(this) == 8) {
					p.getWorld().registerItem(
						new GroundItem(p.getWorld(), ItemId.CREST_FRAGMENT_THREE.id(), n.getX(), n.getY(), 1, p));
				}
				n.killedBy(p);
				n.remove();
			}
		}

	}

	class Dimintheis {
		public static final int THREE_SONS = 0;
		public static final int TRADITION = 1;
	}

}
