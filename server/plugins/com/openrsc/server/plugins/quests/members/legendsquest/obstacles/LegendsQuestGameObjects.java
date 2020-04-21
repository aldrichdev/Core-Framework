package com.openrsc.server.plugins.quests.members.legendsquest.obstacles;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.constants.Skills;
import com.openrsc.server.event.SingleEvent;
import com.openrsc.server.model.Point;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.GroundItem;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.UseLocTrigger;
import com.openrsc.server.plugins.triggers.OpLocTrigger;
import com.openrsc.server.plugins.quests.members.legendsquest.npcs.LegendsQuestNezikchened;
import com.openrsc.server.plugins.skills.mining.Mining;
import com.openrsc.server.plugins.skills.thieving.Thieving;
import com.openrsc.server.util.rsc.DataConversions;
import com.openrsc.server.util.rsc.Formulae;

import java.util.Optional;

import static com.openrsc.server.plugins.Functions.*;

public class LegendsQuestGameObjects implements OpLocTrigger, UseLocTrigger {

	// Objects
	private static final int LEGENDS_CUPBOARD = 1149;
	private static final int GRAND_VIZIERS_DESK = 1177;
	private static final int TOTEM_POLE = 1169;
	private static final int ROCK = 1151;
	private static final int TALL_REEDS = 1163;
	private static final int SHALLOW_WATER = 582;
	private static final int CRATE = 1144;
	private static final int CRUDE_BED = 1162;
	private static final int CRUDE_DESK = 1032;
	private static final int TABLE = 1161;
	private static final int BOOKCASE = 931;
	private static final int CAVE_ENTRANCE_LEAVE_DUNGEON = 1158;
	private static final int CAVE_ENTRANCE_FROM_BOULDERS = 1159;
	private static final int CAVE_ANCIENT_WOODEN_DOORS = 1160;
	private static final int HEAVY_METAL_GATE = 1033;
	private static final int HALF_BURIED_REMAINS = 1168;
	private static final int CARVED_ROCK = 1037;
	private static final int WOODEN_BEAM = 1156;
	private static final int ROPE_UP = 1167;
	private static final int RED_EYE_ROCK = 1148;
	private static final int ANCIENT_LAVA_FURNACE = 1146;
	private static final int CAVERNOUS_OPENING = 1145;
	private static final int ECHNED_ZEKIN_ROCK = 1116;
	private static final int FERTILE_EARTH = 1113;

	private static final int[] SMASH_BOULDERS = {1117, 1184, 1185};
	private static final int BABY_YOMMI_TREE = 1112;
	private static final int YOMMI_TREE = 1107;
	private static final int DEAD_YOMMI_TREE = 1141;
	private static final int GROWN_YOMMI_TREE = 1108;
	private static final int ROTTEN_YOMMI_TREE = 1172;
	private static final int CHOPPED_YOMMI_TREE = 1109;
	private static final int TRIMMED_YOMMI_TREE = 1110;
	private static final int CRAFTED_TOTEM_POLE = 1111;
	private final int[] REFILLABLE = {1188, 1266, 21, 140, 341, 465};
	private final int[] REFILLED = {1189, 1267, 50, 141, 342, 464};

	@Override
	public boolean blockOpLoc(GameObject obj, String command, Player p) {
		return inArray(obj.getID(), GRAND_VIZIERS_DESK, LEGENDS_CUPBOARD, TOTEM_POLE, ROCK, TALL_REEDS,
				SHALLOW_WATER, CAVE_ENTRANCE_LEAVE_DUNGEON, CRATE, TABLE, BOOKCASE, CAVE_ENTRANCE_FROM_BOULDERS, CRUDE_DESK,
				CAVE_ANCIENT_WOODEN_DOORS, HEAVY_METAL_GATE, HALF_BURIED_REMAINS, CARVED_ROCK, WOODEN_BEAM, WOODEN_BEAM + 1, ROPE_UP,
				RED_EYE_ROCK, ANCIENT_LAVA_FURNACE, CAVERNOUS_OPENING, ECHNED_ZEKIN_ROCK, CRAFTED_TOTEM_POLE, TOTEM_POLE + 1)
				|| inArray(obj.getID(), SMASH_BOULDERS) || (obj.getID() == CRUDE_BED && command.equalsIgnoreCase("search"));
	}

	@Override
	public void onOpLoc(GameObject obj, String command, Player p) {
		if (obj.getID() == ECHNED_ZEKIN_ROCK) {
			if (p.getQuestStage(Quests.LEGENDS_QUEST) == 8) {
				mes(p, p.getWorld().getServer().getConfig().GAME_TICK * 2, "The rock moves quite easily.");
				p.message("And the spirit of Echned Zekin seems to have disapeared.");
				changeloc(obj, 10000, SHALLOW_WATER);
				return;
			}
			p.setBusy(true);
			Npc echned = ifnearvisnpc(p, NpcId.ECHNED_ZEKIN.id(), 2);
			if (echned == null) {
				mes(p, p.getWorld().getServer().getConfig().GAME_TICK * 2, "A thick, green mist seems to emanate from the water...",
					"It slowly congeals into the shape of a body...");
				echned = addnpc(p, NpcId.ECHNED_ZEKIN.id(), p.getX(), p.getY(), 0, 60000 * 3);
				if (echned != null) {
					p.setBusyTimer(p.getWorld().getServer().getConfig().GAME_TICK * 5);
					delay(p.getWorld().getServer().getConfig().GAME_TICK * 2);
					mes(p, echned, p.getWorld().getServer().getConfig().GAME_TICK * 2, "Which slowly floats towards you.");
					echned.initializeTalkScript(p);
				}
				return;
			}
			if (echned != null) {
				echned.initializeTalkScript(p);
			}
			p.setBusy(false);
		}
		else if (obj.getID() == CAVERNOUS_OPENING) {
			if (command.equalsIgnoreCase("enter")) {
				if (p.getY() >= 3733) {
					p.message("You enter the dark cave...");
					p.teleport(395, 3725);
				} else {
					if (p.getCache().hasKey("cavernous_opening") || p.getQuestStage(Quests.LEGENDS_QUEST) == -1) {
						mes(p, p.getWorld().getServer().getConfig().GAME_TICK * 2, "You walk carefully into the darkness of the cavern..");
						p.teleport(395, 3733);
					} else {
						mes(p, p.getWorld().getServer().getConfig().GAME_TICK * 2, "You walk into an invisible barrier...");
						mes(p, p.getWorld().getServer().getConfig().GAME_TICK, "Somekind of magical force will not allow you to pass into the cavern.");
					}
				}
			} else if (command.equalsIgnoreCase("search")) {
				if (p.getCache().hasKey("cavernous_opening")) {
					mes(p, p.getWorld().getServer().getConfig().GAME_TICK * 2, "You can see a glowing crystal shape in the wall.",
						"It looks like the Crystal is magical, ",
						"it allows access to the cavern.");
				} else {
					mes(p, p.getWorld().getServer().getConfig().GAME_TICK * 2, "You see a heart shaped depression in the wall next to the cavern.",
						"And a message reads...",
						"@gre@All ye who stand 'ere the dragons teeth,");
					mes(p, p.getWorld().getServer().getConfig().GAME_TICK, "@gre@Place your full true heart and proceed...");
				}
			}
		}
		else if (obj.getID() == ANCIENT_LAVA_FURNACE) {
			if (command.equalsIgnoreCase("look")) {
				mes(p, p.getWorld().getServer().getConfig().GAME_TICK, "This is an ancient looking furnace.");
			} else if (command.equalsIgnoreCase("search")) {
				mes(p, p.getWorld().getServer().getConfig().GAME_TICK * 2, "You search the lava furnace.",
					"You find a small compartment that you may be able to use.",
					"Strangely, it looks as if it is designed for a specific purpose...");
				mes(p, p.getWorld().getServer().getConfig().GAME_TICK, "to fuse things together at very high temperatures...");
			}
		}
		else if (obj.getID() == RED_EYE_ROCK) {
			mes(p, p.getWorld().getServer().getConfig().GAME_TICK, "These rocks look somehow manufactured..");
		}
		else if (obj.getID() == ROPE_UP) {
			p.message("You climb the rope back out again.");
			p.teleport(471, 3707);
		}
		else if (obj.getID() == WOODEN_BEAM + 1) {
			if (p.getQuestStage(Quests.LEGENDS_QUEST) >= 9 || blockDescendBeamPostQuest(p)) {
				mes(p, p.getWorld().getServer().getConfig().GAME_TICK * 2, "The rope snaps as you're about to climb down it.",
					"Perhaps you need a new rope.");
				return;
			}
			mes(p, p.getWorld().getServer().getConfig().GAME_TICK * 2, "This rope climb looks pretty dangerous,",
				"Are you sure you want to go down?");
			int menu = multi(p,
				"Yes,I'll go down the rope...",
				"No way do I want to go down there.");
			if (menu == 0) {
				mes(p, p.getWorld().getServer().getConfig().GAME_TICK * 2, "You prepare to climb down the rope...");
				say(p, null, "! Gulp !");
				delay(p.getWorld().getServer().getConfig().GAME_TICK * 2);
				if ((p.getQuestStage(Quests.LEGENDS_QUEST) >= 0 && !p.getCache().hasKey("gujuo_potion")) || blockDescendBeamPostQuest(p)) {
					mes(p, p.getWorld().getServer().getConfig().GAME_TICK * 2, "...but a terrible fear grips you...");
					p.message("And you can go no further.");
				} else {
					int rnd = DataConversions.random(0, 4);
					if (rnd == 0) {
						mes(p, p.getWorld().getServer().getConfig().GAME_TICK * 2, "but fear stabs at your heart...",
								"and you lose concentration,",
							"you slip and fall....");
						p.damage(DataConversions.random(10, 15));
					}
					else {
						mes(p, p.getWorld().getServer().getConfig().GAME_TICK * 2, "And although fear stabs at your heart...",
								"You shimmey down the rope...");
					}
					p.teleport(426, 3707);
				}
			} else if (menu == 1) {
				p.message("You decide not to go down the rope.");
			}
		}
		else if (obj.getID() == WOODEN_BEAM) {
			p.message("You search the wooden beam...");
			if (p.getCache().hasKey("legends_wooden_beam")) {
				p.message("You search the wooden beam and find the rope you attached.");
				changeloc(obj, p.getWorld().getServer().getConfig().GAME_TICK * 8, WOODEN_BEAM + 1);
			} else {
				mes(p, p.getWorld().getServer().getConfig().GAME_TICK * 2, "You see nothing special about this...");
				p.message("Perhaps if you had a rope, it might be more functional.");
			}
		}
		else if (obj.getID() == CARVED_ROCK) {
			mes(p, p.getWorld().getServer().getConfig().GAME_TICK * 2, "You see a delicate inscription on the rock, it says,");
			mes(p, p.getWorld().getServer().getConfig().GAME_TICK * 3, "@gre@'Once there were crystals to make the pool shine,'");
			mes(p, 0, "@gre@'Ordered in stature to retrieve what's mine.'");
			String gem = "";
			boolean attached = false;
			// opal
			if (obj.getX() == 471 && obj.getY() == 3722) {
				gem = "Opal";
				attached = p.getCache().hasKey("legends_attach_1");
			}
			// emerald
			else if (obj.getX() == 474 && obj.getY() == 3730) {
				gem = "Emerald";
				attached = p.getCache().hasKey("legends_attach_2");
			}
			// ruby
			else if (obj.getX() == 471 && obj.getY() == 3734) {
				gem = "Ruby";
				attached = p.getCache().hasKey("legends_attach_3");
			}
			// diamond
			else if (obj.getX() == 466 && obj.getY() == 3739) {
				gem = "Diamond";
				attached = p.getCache().hasKey("legends_attach_4");
			}
			// sapphire
			else if (obj.getX() == 460 && obj.getY() == 3737) {
				gem = "Sapphire";
				attached = p.getCache().hasKey("legends_attach_5");
			}
			// red topaz
			else if (obj.getX() == 464 && obj.getY() == 3730) {
				gem = "Topaz";
				attached = p.getCache().hasKey("legends_attach_6");
			}
			// jade
			else if (obj.getX() == 469 && obj.getY() == 3728) {
				gem = "Jade";
				attached = p.getCache().hasKey("legends_attach_7");
			}

			if (!gem.equals("") && attached) {
				mes(p, p.getWorld().getServer().getConfig().GAME_TICK * 2, "A barely visible " + gem + " becomes clear again, spinning above the rock.",
						"And then fades again...");
			}
		}
		else if (obj.getID() == HALF_BURIED_REMAINS) {
			mes(p, "It looks as if some poor unfortunate soul died here.");
		}
		else if (obj.getID() == HEAVY_METAL_GATE) {
			if (command.equalsIgnoreCase("look")) {
				mes(p, p.getWorld().getServer().getConfig().GAME_TICK * 2, "This huge metal gate bars the way further...",
					"There is an intense and unpleasant feeling from this place.");
				p.message("And you can see why, shadowy flying creatures seem to hover in the still dark air.");
			} else if (command.equalsIgnoreCase("push")) {
				mes(p, p.getWorld().getServer().getConfig().GAME_TICK * 2, "You push the gates...they're very stiff...",
					"They won't budge with a normal push.",
					"Do you want to try to force them open with brute strength?");
				int menu = multi(p,
					"Yes, I'm very strong, I'll force them open.",
					"No, I'm having second thoughts.");
				if (menu == 0) {
					if (getCurrentLevel(p, Skills.STRENGTH) < 50) {
						p.message("You need a Strength of at least 50 to affect these gates.");
						return;
					}
					mes(p, p.getWorld().getServer().getConfig().GAME_TICK * 2, "You ripple your muscles...preparing too exert yourself...");
					say(p, null, "Hup!");
					mes(p, p.getWorld().getServer().getConfig().GAME_TICK * 2, "You brace yourself against the doors...");
					say(p, null, "Urghhhhh!");
					mes(p, p.getWorld().getServer().getConfig().GAME_TICK * 2, "You start to force against the gate..");
					say(p, null, "Arghhhhhhh!");
					mes(p, p.getWorld().getServer().getConfig().GAME_TICK * 2, "You push and push,");
					say(p, null, "Shhhhhhhshshehshsh");
					if (Formulae.failCalculation(p, Skills.STRENGTH, 50)) {
						mes(p, p.getWorld().getServer().getConfig().GAME_TICK * 2, "You just manage to force the gates open slightly, ",
							"just enough to force yourself through.");
						changeloc(obj, p.getWorld().getServer().getConfig().GAME_TICK * 3, 181);
						if (p.getY() <= 3717) {
							p.teleport(441, 3719);
						} else {
							p.teleport(441, 3717);
						}
					} else {
						mes(p, p.getWorld().getServer().getConfig().GAME_TICK * 2, "but run out of steam before you're able to force the gates open.");
						p.message("The effort of trying to force the gates reduces your strength temporarily");
						p.getSkills().decrementLevel(Skills.STRENGTH);
					}
				} else if (menu == 1) {
					p.message("You decide against forcing the gates.");
				}
			}
		}
		else if (inArray(obj.getID(), SMASH_BOULDERS)) {
			if (p.getCarriedItems().hasCatalogID(Mining.getAxe(p), Optional.of(false))) {
				if (getCurrentLevel(p, Skills.MINING) < 52) {
					p.message("You need a mining ability of at least 52 to affect these boulders.");
					return;
				}
				if (Formulae.failCalculation(p, Skills.MINING, 50)) {
					mes(p, p.getWorld().getServer().getConfig().GAME_TICK * 2, "You take a good swing at the rock with your pick...");
					changeloc(obj, p.getWorld().getServer().getConfig().GAME_TICK * 3, 1143);
					if (obj.getID() == SMASH_BOULDERS[0] && p.getY() <= 3704) {
						p.teleport(441, 3707);
					} else if (obj.getID() == SMASH_BOULDERS[0] && p.getY() >= 3707) {
						p.teleport(442, 3704);
					} else if (obj.getID() == SMASH_BOULDERS[1] && p.getY() <= 3708) {
						p.teleport(441, 3711);
					} else if (obj.getID() == SMASH_BOULDERS[1] && p.getY() >= 3711) {
						p.teleport(441, 3708);
					} else if (obj.getID() == SMASH_BOULDERS[2] && p.getY() <= 3712) {
						p.teleport(441, 3715);
					} else if (obj.getID() == SMASH_BOULDERS[2] && p.getY() >= 3715) {
						p.teleport(441, 3712);
					}
					mes(p, p.getWorld().getServer().getConfig().GAME_TICK * 3, "...and smash it into smaller pieces.");
					p.message("Another large rock falls down replacing the one that you smashed.");
				} else {
					p.message("You fail to make a mark on the rocks.");
					p.message("You miss hit the rock and the vibration shakes your bones.");
					p.message("Your mining ability suffers...");
					p.getSkills().decrementLevel(Skills.MINING);
				}
			} else {
				mes(p, "You'll need a pickaxe to smash your way through these boulders.");
			}
		}
		else if (obj.getID() == CAVE_ANCIENT_WOODEN_DOORS) {
			if (command.equalsIgnoreCase("open")) {
				if (p.getY() >= 3703) {
					mes(p, p.getWorld().getServer().getConfig().GAME_TICK * 2, "You push the doors open and walk through.");
					changeloc(obj, p.getWorld().getServer().getConfig().GAME_TICK * 3, 497);
					p.teleport(442, 3701);
					delay(p.getWorld().getServer().getConfig().GAME_TICK * 3);
					p.message("The doors make a satisfying 'CLICK' sound as they close.");
				} else {
					mes(p, p.getWorld().getServer().getConfig().GAME_TICK * 2, "You push on the doors...they're really shut..",
						"It looks as if they have a huge lock on it...");
					p.message("Although ancient, it looks very sophisticated...");
				}
			} else if (command.equalsIgnoreCase("pick lock")) {
				if (p.getY() >= 3703) {
					mes(p, p.getWorld().getServer().getConfig().GAME_TICK * 2, "You see a lever which you pull on to open the door.");
					changeloc(obj, p.getWorld().getServer().getConfig().GAME_TICK * 3, 497);
					p.teleport(442, 3701);
					mes(p, p.getWorld().getServer().getConfig().GAME_TICK * 2, "You walk through the door.");
					p.message("The doors make a satisfying 'CLICK' sound as they close.");
				} else {
					if (getCurrentLevel(p, Skills.THIEVING) < 50) {
						p.message("You need a thieving level of at least 50 to attempt this.");
						return;
					}
					if (p.getCarriedItems().hasCatalogID(ItemId.LOCKPICK.id(), Optional.of(false))) {
						mes(p, p.getWorld().getServer().getConfig().GAME_TICK * 2, "You attempt to pick the lock..");
						p.message("It looks very sophisticated ...");
						say(p, null, "Hmmm, interesting...");
						delay(p.getWorld().getServer().getConfig().GAME_TICK * 2);
						p.message("You carefully insert your lockpick into the lock.");
						say(p, null, "This will be a challenge...");
						delay(p.getWorld().getServer().getConfig().GAME_TICK * 2);
						p.message("You feel for the pins and levers in the mechanism.");
						say(p, null, "Easy does it....");
						delay(p.getWorld().getServer().getConfig().GAME_TICK * 2);
						if (Thieving.succeedPickLockThieving(p, 50)) {
							mes(p, p.getWorld().getServer().getConfig().GAME_TICK * 2, "@gre@'CLICK'");
							say(p, null, "Easy as pie...");
							delay(p.getWorld().getServer().getConfig().GAME_TICK * 2);
							mes(p, p.getWorld().getServer().getConfig().GAME_TICK * 2, "You tumble the lock mechanism and the door opens easily.");
							p.incExp(Skills.THIEVING, 100, true);
							changeloc(obj, p.getWorld().getServer().getConfig().GAME_TICK * 3, 497);
							p.teleport(441, 3703);
						} else {
							p.message("...but you don't manage to pick the lock.");
						}
					} else {
						mes(p, p.getWorld().getServer().getConfig().GAME_TICK * 2, "The mechanism for this lock looks very sophisticated...");
						p.message("you're unable to affect the lock without the proper tool..");
					}
				}
			}
		}
		else if (obj.getID() == CRUDE_DESK) {
			if (p.getCarriedItems().hasCatalogID(ItemId.SHAMANS_TOME.id(), Optional.empty())) {
				mes(p, p.getWorld().getServer().getConfig().GAME_TICK * 2, "You search the desk ...");
				p.message("...but find nothing.");
			} else {
				mes(p, p.getWorld().getServer().getConfig().GAME_TICK * 4, "You search the desk ...");
				give(p, ItemId.SHAMANS_TOME.id(), 1);
				p.message("You find a book...it looks like an ancient tome...");
			}
		}
		else if (obj.getID() == BOOKCASE) {
			mes(p, p.getWorld().getServer().getConfig().GAME_TICK * 2, "You search the bookcase...",
				"And find a large gaping hole at the back.");
			p.message("Would you like to climb through the hole?");
			int menu = multi(p,
				"Yes, I'll climb through the hole.",
				"No, I'll stay here.");
			if (menu == 0) {
				mes(p, p.getWorld().getServer().getConfig().GAME_TICK * 2, "You climb through the hole in the wall..",
					"It's very narrow and you have to contort your body a lot.",
					"After some time, you  manage to wriggle out of a small cavern...");
				p.teleport(444, 3699);
			} else if (menu == 1) {
				p.message("You decide to stay where you are.");
			}
		}
		else if (obj.getID() == TABLE) {
			p.message("You start searching the table...");
			if (p.getCarriedItems().hasCatalogID(ItemId.SCRAWLED_NOTES.id(), Optional.empty())) {
				p.message("You cannot find anything else in here.");
			} else {
				delay(p.getWorld().getServer().getConfig().GAME_TICK * 2);
				give(p, ItemId.SCRAWLED_NOTES.id(), 1);
				mes(p, p.getWorld().getServer().getConfig().GAME_TICK * 2, "You find a scrap of paper with nonesense written on it.");
			}
		}
		else if (obj.getID() == CRUDE_BED && command.equalsIgnoreCase("search")) {
			p.message("You search the flea infested rags..");
			if (p.getCarriedItems().hasCatalogID(ItemId.SCATCHED_NOTES.id(), Optional.empty())) {
				p.message("You cannot find anything else in here.");
			} else {
				delay(p.getWorld().getServer().getConfig().GAME_TICK * 2);
				give(p, ItemId.SCATCHED_NOTES.id(), 1);
				mes(p, p.getWorld().getServer().getConfig().GAME_TICK * 2, "You find a scrap of paper with spidery writing on it.");
			}
		}
		else if (obj.getID() == CRATE) {
			p.message("You search the crate.");
			if (p.getCarriedItems().hasCatalogID(ItemId.SCRIBBLED_NOTES.id(), Optional.empty())) {
				p.message("You cannot find anything else in here.");
			} else {
				delay(p.getWorld().getServer().getConfig().GAME_TICK * 2);
				give(p, ItemId.SCRIBBLED_NOTES.id(), 1);
				mes(p, p.getWorld().getServer().getConfig().GAME_TICK * 2, "After some time you find a scrumpled up piece of paper.");
				p.message("It looks like rubbish...");
			}
		}
		else if (obj.getID() == CAVE_ENTRANCE_FROM_BOULDERS) {
			mes(p, p.getWorld().getServer().getConfig().GAME_TICK * 2, "You see a small cave entrance.",
				"Would you like to climb into it?");
			int menu = multi(p,
				"Yes, I'll climb into it.",
				"No, I'll stay where I am.");
			if (menu == 0) {
				p.message("You clamber into the small cave...");
				p.teleport(452, 3702);
			} else if (menu == 1) {
				p.message("You decide against climbing into the small, uncomfortable looking tunnel.");
			}
		}
		else if (obj.getID() == CAVE_ENTRANCE_LEAVE_DUNGEON) {
			mes(p, p.getWorld().getServer().getConfig().GAME_TICK * 2, "You crawl back out from the cavern...");
			p.teleport(452, 874);
		}
		else if (obj.getID() == SHALLOW_WATER) {
			if (p.getQuestStage(Quests.LEGENDS_QUEST) == 8 && p.getY() >= 3723 && p.getY() <= 3740) {
				p.message("A magical looking pool.");
				return;
			}
			if (p.getQuestStage(Quests.LEGENDS_QUEST) >= 5 || p.getQuestStage(Quests.LEGENDS_QUEST) == -1) {
				p.message("A disgusting sess pit of filth and stench...");
				return;
			}
			mes(p, 0, "A bubbling brook with effervescent water...");
		}
		else if (obj.getID() == TALL_REEDS) {
			mes(p, p.getWorld().getServer().getConfig().GAME_TICK * 2, "These tall reeds look nice and long, ");
			mes(p, p.getWorld().getServer().getConfig().GAME_TICK * 2, "with a long tube for a stem.");
			mes(p, 0, "They reach all the way down to the water.");
		}
		else if (obj.getID() == ROCK) {
			if (p.getCache().hasKey("legends_cavern") || p.getQuestStage(Quests.LEGENDS_QUEST) >= 2 || p.getQuestStage(Quests.LEGENDS_QUEST) == -1) {
				if (p.getQuestStage(Quests.LEGENDS_QUEST) == 1) {
					mes(p, p.getWorld().getServer().getConfig().GAME_TICK * 2, "You see nothing significant...",
						"At first....");
				}
				mes(p, p.getWorld().getServer().getConfig().GAME_TICK * 2, "You see that there is a small crevice that you may be able to crawl though.?",
					"Would you like to try to crawl through, it looks quite an enclosed area.");
				int menu = multi(p,
					"Yes, I'll crawl through, I'm very athletic.",
					"No, I'm pretty scared of enclosed areas.");
				if (menu == 0) {
					if (getCurrentLevel(p, Skills.AGILITY) < 50) {
						p.message("You need an agility of 50 to even attempt this.");
						p.setBusy(false);
						return;
					}
					mes(p, p.getWorld().getServer().getConfig().GAME_TICK * 2, "You try to crawl through...",
						"You contort your body to fit the crevice.");
					if (Formulae.failCalculation(p, Skills.AGILITY, 50)) {
						mes(p, p.getWorld().getServer().getConfig().GAME_TICK * 2, "You adroitely squeeze serpent like into the crevice.",
							"You find a small narrow tunnel that goes for some distance.",
							"After some time, you find a small cave opening...and walk through.");
						p.teleport(461, 3700);
						if (p.getCache().hasKey("legends_cavern")) {
							p.getCache().remove("legends_cavern");
							p.updateQuestStage(Quests.LEGENDS_QUEST, 2);
						}
					} else {
						mes(p, p.getWorld().getServer().getConfig().GAME_TICK * 5, "You get cramped into a tiny space and start to suffocate.",
							"You wriggle and wriggle but you cannot get out..");
						mes(p, p.getWorld().getServer().getConfig().GAME_TICK * 2, "Eventually you manage to break free.",
							"But you scrape yourself very badly as your force your way out.",
							"And you're totally exhausted from the experience.");
						p.damage(5);
					}
				} else if (menu == 1) {
					mes(p, p.getWorld().getServer().getConfig().GAME_TICK * 2, "You decide against forcing yourself into the tiny crevice..",
						"And realise that you have much better things to do..",
						"Like visit Inn's and mine ore...");
				}
			} else {
				p.message("You see nothing significant.");
			}
		}
		else if (obj.getID() == TOTEM_POLE) { // BLACK
			if (p.getQuestStage(Quests.LEGENDS_QUEST) >= 10 || p.getQuestStage(Quests.LEGENDS_QUEST) == -1) {
				changeloc(obj, p.getWorld().getServer().getConfig().GAME_TICK * 16, 1170);
				mes(p, p.getWorld().getServer().getConfig().GAME_TICK * 2, "This totem pole is truly awe inspiring.",
					"It depicts powerful Karamja jungle animals.",
					"It is very well carved and brings a sense of power ",
					"and spiritual fullfilment to anyone who looks at it.");
				return;
			}
			if (p.getQuestStage(Quests.LEGENDS_QUEST) == 9) {
				replaceTotemPole(p, obj, false);
				return;
			}
			mes(p, p.getWorld().getServer().getConfig().GAME_TICK * 2, "This totem pole looks very corrupted,",
				"there is a darkness about it that seems quite unnatural.",
				"You don't like to look at it for too long.");

		}
		else if (obj.getID() == TOTEM_POLE + 1) { // RED
			if (p.getQuestStage(Quests.LEGENDS_QUEST) >= 10 || p.getQuestStage(Quests.LEGENDS_QUEST) == -1) {
				mes(p, p.getWorld().getServer().getConfig().GAME_TICK * 2, "This totem pole is truly awe inspiring.",
					"It depicts powerful Karamja jungle animals.",
					"It is very well carved and brings a sense of power ",
					"and spiritual fullfilment to anyone who looks at it.");
				return;
			}
			if (p.getQuestStage(Quests.LEGENDS_QUEST) == 9) {
				replaceTotemPole(p, obj, false);
				return;
			}
			changeloc(obj, new GameObject(obj.getWorld(), obj.getLocation(), TOTEM_POLE, obj.getDirection(), obj.getType()));
			mes(p, p.getWorld().getServer().getConfig().GAME_TICK * 2, "This totem pole looks very corrupted,",
				"there is a darkness about it that seems quite unnatural.",
				"You don't like to look at it for too long.");

		}
		else if (obj.getID() == GRAND_VIZIERS_DESK) {
			p.message("You rap loudly on the desk.");
			Npc radimus = ifnearvisnpc(p, NpcId.SIR_RADIMUS_ERKLE_HOUSE.id(), 6);
			if (radimus != null) {
				radimus.teleport(517, 545);
				npcWalkFromPlayer(p, radimus);
				radimus.initializeTalkScript(p);
			} else {
				p.message("Sir Radimus Erkle is currently busy at the moment.");
			}
		}
		else if (obj.getID() == LEGENDS_CUPBOARD) {
			if (p.getQuestStage(Quests.LEGENDS_QUEST) >= 1 || p.getQuestStage(Quests.LEGENDS_QUEST) == -1) {
				if (p.getCarriedItems().hasCatalogID(ItemId.MACHETTE.id(), Optional.of(false))) {
					p.message("The cupboard is empty.");
				} else {
					mes(p, p.getWorld().getServer().getConfig().GAME_TICK * 2, "You open the cupboard and find a machette.",
						"You take it out and add it to your inventory.");
					give(p, ItemId.MACHETTE.id(), 1);
				}
			} else {
				p.message("@gre@Sir Radimus Erkle: You're not authorised to open that cupboard.");
			}
		}
		else if (obj.getID() == CRAFTED_TOTEM_POLE) {
			if (obj.getOwner().equals(p.getUsername())) {
				mes(p, p.getWorld().getServer().getConfig().GAME_TICK * 2, "This totem pole looks very heavy...");
				changeloc(obj, new GameObject(obj.getWorld(), obj.getLocation(), FERTILE_EARTH, obj.getDirection(), obj.getType()));
				give(p, ItemId.TOTEM_POLE.id(), 1);
				if (!p.getCache().hasKey("crafted_totem_pole")) {
					p.getCache().store("crafted_totem_pole", true);
				}
				p.message("Carrying this totem pole saps your strength...");
				p.getSkills().setLevel(Skills.STRENGTH, (int) (p.getSkills().getLevel(Skills.STRENGTH) * 0.9));
			} else {
				p.message("This is not your totem pole to carry.");
			}
		}
	}

	@Override
	public boolean blockUseLoc(GameObject obj, Item item, Player p) {
		return (item.getCatalogId() == ItemId.MACHETTE.id() && obj.getID() == TALL_REEDS)
				|| (item.getCatalogId() == ItemId.CUT_REED_PLANT.id() && obj.getID() == SHALLOW_WATER)
				|| (item.getCatalogId() == ItemId.BLESSED_GOLDEN_BOWL.id() && obj.getID() == SHALLOW_WATER)
				|| obj.getID() == CARVED_ROCK || (obj.getID() == WOODEN_BEAM && item.getCatalogId() == ItemId.ROPE.id())
				|| obj.getID() == ANCIENT_LAVA_FURNACE || (obj.getID() == RED_EYE_ROCK && item.getCatalogId() == ItemId.A_RED_CRYSTAL.id())
				|| (obj.getID() == CAVERNOUS_OPENING && item.getCatalogId() == ItemId.A_GLOWING_RED_CRYSTAL.id())
				|| (obj.getID() == FERTILE_EARTH && item.getCatalogId() == ItemId.YOMMI_TREE_SEED.id())
				|| (obj.getID() == FERTILE_EARTH && item.getCatalogId() == ItemId.GERMINATED_YOMMI_TREE_SEED.id())
				|| (obj.getID() == YOMMI_TREE && item.getCatalogId() == ItemId.BLESSED_GOLDEN_BOWL_WITH_PURE_WATER.id())
				|| (inArray(obj.getID(), DEAD_YOMMI_TREE, ROTTEN_YOMMI_TREE, GROWN_YOMMI_TREE, CHOPPED_YOMMI_TREE, TRIMMED_YOMMI_TREE) && item.getCatalogId() == ItemId.RUNE_AXE.id())
				|| (obj.getID() == TOTEM_POLE && item.getCatalogId() == ItemId.TOTEM_POLE.id());
	}

	@Override
	public void onUseLoc(GameObject obj, Item item, Player p) {
		if (obj.getID() == TOTEM_POLE && item.getCatalogId() == ItemId.TOTEM_POLE.id()) {
			if (p.getQuestStage(Quests.LEGENDS_QUEST) >= 10 || p.getQuestStage(Quests.LEGENDS_QUEST) == -1) {
				mes(p, "You have already replaced the evil totem pole with your own.",
						"You feel a great sense of accomplishment");
				return;
			}
			if (p.getQuestStage(Quests.LEGENDS_QUEST) == 9) {
				replaceTotemPole(p, obj, true);
				return;
			}
			if (p.getQuestStage(Quests.LEGENDS_QUEST) == 8) {
				if (p.getCache().hasKey("killed_viyeldi") && !p.getCache().hasKey("viyeldi_companions")) {
					p.getCache().set("viyeldi_companions", 1);
				}
				mes(p, "You attempt to replace the evil totem pole.",
					"A black cloud emanates from the evil totem pole.");
				p.message("It slowly forms into the dread demon Nezikchened...");
				LegendsQuestNezikchened.demonFight(p);
			}
		}
		else if (obj.getID() == TRIMMED_YOMMI_TREE && item.getCatalogId() == ItemId.RUNE_AXE.id()) {
			if (obj.getOwner().equals(p.getUsername())) {
				int objectX = obj.getX();
				int objectY = obj.getY();
				mes(p, p.getWorld().getServer().getConfig().GAME_TICK * 2, "You craft a totem pole out of the Yommi tree.");
				changeloc(obj, new GameObject(obj.getWorld(), obj.getLocation(), CRAFTED_TOTEM_POLE, obj.getDirection(), obj.getType(), p.getUsername()));
				obj.getWorld().getServer().getGameEventHandler().add(new SingleEvent(obj.getWorld(), null, 60000, "Legends Quest Craft Totem Pole") {
					public void action() {
						GameObject whatObject = p.getWorld().getRegionManager().getRegion(Point.location(objectX, objectY)).getGameObject(Point.location(objectX, objectY));
						if (whatObject != null && whatObject.getID() == CRAFTED_TOTEM_POLE) {
							obj.getWorld().registerGameObject(new GameObject(obj.getWorld(), obj.getLocation(), FERTILE_EARTH, obj.getDirection(), obj.getType()));
						}
					}
				});
			} else {
				p.message("This is not your Yommi Tree.");
			}
		}
		else if (obj.getID() == CHOPPED_YOMMI_TREE && item.getCatalogId() == ItemId.RUNE_AXE.id()) {
			if (obj.getOwner().equals(p.getUsername())) {
				int objectX = obj.getX();
				int objectY = obj.getY();
				mes(p, p.getWorld().getServer().getConfig().GAME_TICK * 2, "You professionally wield your Rune Axe...",
					"As you trim the branches from the Yommi tree.");
				changeloc(obj, new GameObject(obj.getWorld(), obj.getLocation(), TRIMMED_YOMMI_TREE, obj.getDirection(), obj.getType(), p.getUsername()));
				obj.getWorld().getServer().getGameEventHandler().add(new SingleEvent(obj.getWorld(), null, 60000, "Legend Quest Trim Yommi Tree") {
					public void action() {
						GameObject whatObject = p.getWorld().getRegionManager().getRegion(Point.location(objectX, objectY)).getGameObject(Point.location(objectX, objectY));
						if (whatObject != null && whatObject.getID() == TRIMMED_YOMMI_TREE) {
							obj.getWorld().registerGameObject(new GameObject(obj.getWorld(), obj.getLocation(), FERTILE_EARTH, obj.getDirection(), obj.getType()));
						}
					}
				});
			} else {
				p.message("This is not your Yommi Tree.");
			}
		}
		else if (obj.getID() == GROWN_YOMMI_TREE && item.getCatalogId() == ItemId.RUNE_AXE.id()) {
			if (obj.getOwner().equals(p.getUsername())) {
				int objectX = obj.getX();
				int objectY = obj.getY();
				mes(p, p.getWorld().getServer().getConfig().GAME_TICK * 2, "You wield the Rune Axe and prepare to chop the Yommi tree.");
				changeloc(obj, new GameObject(obj.getWorld(), obj.getLocation(), CHOPPED_YOMMI_TREE, obj.getDirection(), obj.getType(), p.getUsername()));
				obj.getWorld().getServer().getGameEventHandler().add(new SingleEvent(obj.getWorld(), null, 60000, "Legend Quest Chop Yommi Tree") {
					public void action() {
						GameObject whatObject = p.getWorld().getRegionManager().getRegion(Point.location(objectX, objectY)).getGameObject(Point.location(objectX, objectY));
						if (whatObject != null && whatObject.getID() == CHOPPED_YOMMI_TREE) {
							obj.getWorld().registerGameObject(new GameObject(obj.getWorld(), obj.getLocation(), FERTILE_EARTH, obj.getDirection(), obj.getType()));
						}
					}
				});
				mes(p, p.getWorld().getServer().getConfig().GAME_TICK * 2, "You chop the Yommi tree down.",
					"Perhaps you should trim those branches ?");
			} else {
				p.message("This is not your Yommi Tree.");
			}
		}
		else if ((obj.getID() == DEAD_YOMMI_TREE || obj.getID() == ROTTEN_YOMMI_TREE) && item.getCatalogId() == ItemId.RUNE_AXE.id()) {
			mes(p, 0, "You chop the dead Yommi Tree down.");
			mes(p, p.getWorld().getServer().getConfig().GAME_TICK * 2, "You gain some logs..");
			changeloc(obj, new GameObject(obj.getWorld(), obj.getLocation(), FERTILE_EARTH, obj.getDirection(), obj.getType()));
			give(p, ItemId.LOGS.id(), 1);
		}
		else if (obj.getID() == YOMMI_TREE && item.getCatalogId() == ItemId.BLESSED_GOLDEN_BOWL_WITH_PURE_WATER.id()) {
			int objectX = obj.getX();
			int objectY = obj.getY();
			p.getCarriedItems().getInventory().replace(ItemId.BLESSED_GOLDEN_BOWL_WITH_PURE_WATER.id(), ItemId.BLESSED_GOLDEN_BOWL.id());
			displayTeleportBubble(p, obj.getX(), obj.getY(), true);
			mes(p, p.getWorld().getServer().getConfig().GAME_TICK * 2, "You water the Yommi tree from the golden bowl...",
				"It grows at a remarkable rate.");
			changeloc(obj, new GameObject(obj.getWorld(), obj.getLocation(), GROWN_YOMMI_TREE, obj.getDirection(), obj.getType(), p.getUsername()));
			obj.getWorld().getServer().getGameEventHandler().add(new SingleEvent(obj.getWorld(), null, 15000, "Legend Quest Water Yommi Tree") {
				public void action() {
					GameObject whatObject = p.getWorld().getRegionManager().getRegion(Point.location(objectX, objectY)).getGameObject(Point.location(objectX, objectY));
					if (whatObject != null && whatObject.getID() == GROWN_YOMMI_TREE) {
						obj.getWorld().registerGameObject(new GameObject(obj.getWorld(), obj.getLocation(), ROTTEN_YOMMI_TREE, obj.getDirection(), obj.getType()));
						if (p.isLoggedIn()) {
							p.message("The Yommi tree is past it's prime and dies .");
						}
						addloc(obj.getWorld(), obj.getLoc(), 60000);
					}
				}
			});
			mes(p, p.getWorld().getServer().getConfig().GAME_TICK * 2, "Soon the tree stops growing...",
				"It looks tall enough now to make a good totem pole.");
		}
		else if (obj.getID() == FERTILE_EARTH && item.getCatalogId() == ItemId.YOMMI_TREE_SEED.id()) {
			p.message("These seeds need to be germinated in pure water before they");
			p.message("can be planted in the fertile soil.");
		}
		else if (obj.getID() == FERTILE_EARTH && item.getCatalogId() == ItemId.GERMINATED_YOMMI_TREE_SEED.id()) {
			if (p.getQuestStage(Quests.LEGENDS_QUEST) != 8 || !p.getCarriedItems().hasCatalogID(ItemId.BLESSED_GOLDEN_BOWL_WITH_PURE_WATER.id(), Optional.of(false))) {
				p.message("You'll need some sacred water to feed ");
				p.message("the tree when it starts growing.");
				return;
			}
			if (!p.getCarriedItems().hasCatalogID(ItemId.RUNE_AXE.id(), Optional.of(false))) {
				p.message("You'll need a very tough, very sharp axe to");
				p.message("fell the tree once it is grown.");
				return;
			}
			if (getCurrentLevel(p, Skills.WOODCUT) < 50) {
				p.message("You need an woodcut level of 50 to");
				p.message("fell the tree once it is grown.");
				return;
			}
			if (getCurrentLevel(p, Skills.HERBLAW) < 45) {
				p.message("You need a herblaw skill of at least 45 to complete this task.");
				return;
			}
			// 1112, 1107
			// 1172
			p.getCarriedItems().remove(new Item(ItemId.GERMINATED_YOMMI_TREE_SEED.id()));
			if (DataConversions.random(0, 1) != 1) {
				int objectX = obj.getX();
				int objectY = obj.getY();
				changeloc(obj, new GameObject(obj.getWorld(), obj.getLocation(), BABY_YOMMI_TREE, obj.getDirection(), obj.getType()));
				mes(p, p.getWorld().getServer().getConfig().GAME_TICK * 2, "You bury the Germinated Yommi tree seed in the fertile earth...",
					"You start to see something growing.");
				changeloc(obj, new GameObject(obj.getWorld(), obj.getLocation(), YOMMI_TREE, obj.getDirection(), obj.getType()));
				obj.getWorld().getServer().getGameEventHandler().add(new SingleEvent(obj.getWorld(), null, 15000, "Legends Quest Grow Yommi Tree") {
					public void action() {
						GameObject whatObject = p.getWorld().getRegionManager().getRegion(Point.location(objectX, objectY)).getGameObject(Point.location(objectX, objectY));
						if (whatObject != null && whatObject.getID() == YOMMI_TREE) {
							obj.getWorld().registerGameObject(new GameObject(obj.getWorld(), obj.getLocation(), DEAD_YOMMI_TREE, obj.getDirection(), obj.getType(), p.getUsername()));
							if (p.isLoggedIn()) {
								p.message("The Sapling dies.");
							}
							addloc(obj.getWorld(), obj.getLoc(), 60000);
						}
					}
				});
				p.message("The plant grows at a remarkable rate.");
				p.message("It looks as if the tree needs to be watered...");
			} else {
				p.message("You planted the seed incorrectly, it withers and dies.");
			}
		}
		else if (obj.getID() == CAVERNOUS_OPENING && item.getCatalogId() == ItemId.A_GLOWING_RED_CRYSTAL.id()) {
			mes(p, p.getWorld().getServer().getConfig().GAME_TICK * 2, "You carefully place the glowing heart shaped crystal into ",
				"the depression, it slots in perfectly and glows even brighter.",
				"You hear a snapping sound coming from in front of the cave.");
			p.getCarriedItems().remove(new Item(item.getCatalogId()));
			if (!p.getCache().hasKey("cavernous_opening")) {
				p.getCache().store("cavernous_opening", true);
			}
		}
		else if (obj.getID() == RED_EYE_ROCK && item.getCatalogId() == ItemId.A_RED_CRYSTAL.id()) {
			mes(p, p.getWorld().getServer().getConfig().GAME_TICK * 2, "You carefully place the Dragon Crystal on the rock.",
				"The rocks seem to vibrate and hum and the crystal starts to glow.");
			p.message("The vibration in the area diminishes, but the crystal continues to glow.");
			p.getCarriedItems().getInventory().replace(ItemId.A_RED_CRYSTAL.id(), ItemId.A_GLOWING_RED_CRYSTAL.id());
		}
		else if (obj.getID() == ANCIENT_LAVA_FURNACE) {
			switch (ItemId.getById(item.getCatalogId())) {
				case A_CHUNK_OF_CRYSTAL:
				case A_LUMP_OF_CRYSTAL:
				case A_HUNK_OF_CRYSTAL:
					if (getCurrentLevel(p, Skills.CRAFTING) < 50) {
						//message possibly non kosher
						p.message("You need a crafting ability of at least 50 to perform this task.");
						return;
					}
					if (!p.getCache().hasKey(item.getDef(p.getWorld()).getName().toLowerCase().replace(" ", "_"))) {
						p.getCache().store(item.getDef(p.getWorld()).getName().toLowerCase().replace(" ", "_"), true);
						p.getCarriedItems().remove(new Item(item.getCatalogId()));
						mes(p, p.getWorld().getServer().getConfig().GAME_TICK * 2, "You carefully place the piece of crystal into ",
							"a specially shaped compartment in the furnace.");
					}
					if (p.getCache().hasKey("a_chunk_of_crystal") && p.getCache().hasKey("a_lump_of_crystal") && p.getCache().hasKey("a_hunk_of_crystal")) {
						mes(p, p.getWorld().getServer().getConfig().GAME_TICK * 2, "You place the final segment of the crystal together into the ",
							"strangely shaped compartment, all the pieces seem to fit...",
							"You use your crafting skill to control the furnace.",
							"The heat in the furnace slowly rises and soon fuses the parts together...",
							"As soon as the item cools, you pick it up...",
							"As the crystal touches your hands a voice inside of your head says..",
							"@gre@Voice in head: Bring life to the dragons eye.");
						p.getCache().remove("a_chunk_of_crystal");
						p.getCache().remove("a_lump_of_crystal");
						p.getCache().remove("a_hunk_of_crystal");
						give(p, ItemId.A_RED_CRYSTAL.id(), 1);
					} else {
						mes(p, p.getWorld().getServer().getConfig().GAME_TICK * 2, "The compartment in the furnace isn't full yet.");
						mes(p, p.getWorld().getServer().getConfig().GAME_TICK, "It looks like you need more pieces of crystal.");
					}
					break;
				default:
					p.message("Nothing interesting happens");
					break;
			}
		}
		else if (obj.getID() == WOODEN_BEAM && item.getCatalogId() == ItemId.ROPE.id()) {
			p.message("You throw one end of the rope around the beam.");
			p.getCarriedItems().remove(new Item(ItemId.ROPE.id()));
			changeloc(obj, p.getWorld().getServer().getConfig().GAME_TICK * 8, WOODEN_BEAM + 1);
			if (!p.getCache().hasKey("legends_wooden_beam")) {
				p.getCache().store("legends_wooden_beam", true);
			}
		}
		else if (obj.getID() == CARVED_ROCK) {
			switch (ItemId.getById(item.getCatalogId())) {
				case SAPPHIRE:
				case EMERALD:
				case RUBY:
				case DIAMOND:
				case OPAL:
				case JADE:
				case RED_TOPAZ:
					int attachmentMode = -1;
					boolean alreadyAttached = false;
					if (item.getCatalogId() == ItemId.OPAL.id() && obj.getX() == 471 && obj.getY() == 3722) { // OPAL ROCK
						attachmentMode = 1;
					} else if (item.getCatalogId() == ItemId.EMERALD.id() && obj.getX() == 474 && obj.getY() == 3730) { // EMERALD ROCK
						attachmentMode = 2;
					} else if (item.getCatalogId() == ItemId.RUBY.id() && obj.getX() == 471 && obj.getY() == 3734) { // RUBY ROCK
						attachmentMode = 3;
					} else if (item.getCatalogId() == ItemId.DIAMOND.id() && obj.getX() == 466 && obj.getY() == 3739) { // DIAMOND ROCK
						attachmentMode = 4;
					} else if (item.getCatalogId() == ItemId.SAPPHIRE.id() && obj.getX() == 460 && obj.getY() == 3737) { // SAPPHIRE ROCK
						attachmentMode = 5;
					} else if (item.getCatalogId() == ItemId.RED_TOPAZ.id() && obj.getX() == 464 && obj.getY() == 3730) { // RED TOPAZ ROCK
						attachmentMode = 6;
					} else if (item.getCatalogId() == ItemId.JADE.id() && obj.getX() == 469 && obj.getY() == 3728) { // JADE ROCK
						attachmentMode = 7;
					}
					if (p.getCache().hasKey("legends_attach_" + attachmentMode)) {
						alreadyAttached = true;
						attachmentMode = -1;
					}
					if (alreadyAttached) {
						p.message("You have already placed an " + item.getDef(p.getWorld()).getName() + " above this rock.");
						createGroundItemDelayedRemove(new GroundItem(p.getWorld(), item.getCatalogId(), obj.getX(), obj.getY(), 1, p), p.getWorld().getServer().getConfig().GAME_TICK * 8);
						mes(p, p.getWorld().getServer().getConfig().GAME_TICK * 2, "A barely visible " + item.getDef(p.getWorld()).getName() + " becomes clear again, spinning above the rock.");
						p.message("And then fades again...");
					} else {
						if (attachmentMode != -1 && !p.getCarriedItems().hasCatalogID(ItemId.BOOKING_OF_BINDING.id(), Optional.empty())) {
							p.getCarriedItems().remove(new Item(item.getCatalogId()));
							p.message("You carefully move the gem closer to the rock.");
							p.message("The " + item.getDef(p.getWorld()).getName() + " glows and starts spinning as it hovers above the rock.");
							createGroundItemDelayedRemove(new GroundItem(p.getWorld(), item.getCatalogId(), obj.getX(), obj.getY(), 1, p), p.getWorld().getServer().getConfig().GAME_TICK * 8);
							if (!p.getCache().hasKey("legends_attach_" + attachmentMode)) {
								p.getCache().store("legends_attach_" + attachmentMode, true);
							}
							if (p.getCache().hasKey("legends_attach_1")
								&& p.getCache().hasKey("legends_attach_2")
								&& p.getCache().hasKey("legends_attach_3")
								&& p.getCache().hasKey("legends_attach_4")
								&& p.getCache().hasKey("legends_attach_5")
								&& p.getCache().hasKey("legends_attach_6")
								&& p.getCache().hasKey("legends_attach_7")) {
								mes(p, p.getWorld().getServer().getConfig().GAME_TICK * 2, "Suddenly all the crystals begin to glow very brightly.",
									"The room is lit up with the bright light...",
									"Soon, the light from all the crystals converges into a point.",
									"And you see a strange book appear where the light is focused.",
									"You pick the book up and place it in your inventory.",
									"All the crystals disapear...and the light fades...");
								give(p, ItemId.BOOKING_OF_BINDING.id(), 1);
								for (int i = 0; i < 8; i++) {
									if (p.getCache().hasKey("legends_attach_" + i)) {
										p.getCache().remove("legends_attach_" + i);
									}
								}
							}
						} else {
							p.message("You carefully move the gem closer to the rock.");
							p.message("but nothing happens...");
						}
					}
					break;
				default:
					p.message("Nothing interesting happens");
					break;
			}
		}
		else if (item.getCatalogId() == ItemId.MACHETTE.id() && obj.getID() == TALL_REEDS) {
			give(p, ItemId.CUT_REED_PLANT.id(), 1);
			mes(p, p.getWorld().getServer().getConfig().GAME_TICK * 2, "You use your machette to cut down a tall reed.",
				"You cut it into a length of pipe.");
		}
		else if (item.getCatalogId() == ItemId.BLESSED_GOLDEN_BOWL.id()) {
			if (p.getQuestStage(Quests.LEGENDS_QUEST) == 8 && p.getY() >= 3723 && p.getY() <= 3740) {
				p.message("You fill the bowl up with water..");
				p.getCarriedItems().getInventory().replace(ItemId.BLESSED_GOLDEN_BOWL.id(), ItemId.BLESSED_GOLDEN_BOWL_WITH_PURE_WATER.id());
				return;
			}
			mes(p, p.getWorld().getServer().getConfig().GAME_TICK * 2, "The water is awkward to get to...",
				"The gap to the water is too narrow.");
		}
		else if (item.getCatalogId() == ItemId.CUT_REED_PLANT.id() && obj.getID() == SHALLOW_WATER) {
			if (atQuestStages(p, Quests.LEGENDS_QUEST, 5, 6, 7)) {
				mes(p, p.getWorld().getServer().getConfig().GAME_TICK * 2, "It looks as if this pool has dried up...",
					"A thick black sludge has replaced the sparkling pure water...",
					"There is a disgusting stench of death that emanates from this area...",
					"Maybe Gujuo knows what's happened...");
				if (p.getQuestStage(Quests.LEGENDS_QUEST) == 5) {
					p.updateQuestStage(Quests.LEGENDS_QUEST, 6);
				}
				return;
			}
			if((p.getQuestStage(Quests.LEGENDS_QUEST) >= 9 || p.getQuestStage(Quests.LEGENDS_QUEST) == -1)
					&& !p.getWorld().getServer().getConfig().LOOSE_SHALLOW_WATER_CHECK) {
				mes(p, p.getWorld().getServer().getConfig().GAME_TICK * 2, "You use the cut reed plant to syphon some water from the pool.",
						"You take a refreshing drink from the pool.",
						"The cut reed is soaked through with water and is now all soggy.");
				return;
			}

			int emptyID = -1;
			int refilledID = -1;
			for (int i = 0; i < REFILLABLE.length; i++) {
				if (p.getCarriedItems().hasCatalogID(REFILLABLE[i], Optional.of(false))) {
					emptyID = REFILLABLE[i];
					refilledID = REFILLED[i];
					break;
				}
			}
			if (emptyID != ItemId.NOTHING.id()) {
				mes(p, p.getWorld().getServer().getConfig().GAME_TICK * 2, "You use the cut reed plant to syphon some water from the pool.");
				if (emptyID == ItemId.GOLDEN_BOWL.id()) {
					mes(p, p.getWorld().getServer().getConfig().GAME_TICK * 2, "into your gold bowl.");
					p.getCarriedItems().getInventory().replace(ItemId.GOLDEN_BOWL.id(), ItemId.GOLDEN_BOWL_WITH_PURE_WATER.id());
					mes(p, p.getWorld().getServer().getConfig().GAME_TICK * 2, "The water doesn't seem to sparkle as much as it did in the pool.");
				} else if (emptyID == ItemId.BLESSED_GOLDEN_BOWL.id()) {
					mes(p, p.getWorld().getServer().getConfig().GAME_TICK * 2, "into your blessed gold bowl.");
					p.getCarriedItems().getInventory().replace(ItemId.BLESSED_GOLDEN_BOWL.id(), ItemId.BLESSED_GOLDEN_BOWL_WITH_PURE_WATER.id());
					mes(p, p.getWorld().getServer().getConfig().GAME_TICK * 2, "The water seems to bubble and sparkle as if alive.");
				} else {
					mes(p, p.getWorld().getServer().getConfig().GAME_TICK * 2, "You put some water in the " + p.getWorld().getServer().getEntityHandler().getItemDef(emptyID).getName().toLowerCase() + ".");
					p.getCarriedItems().getInventory().replace(emptyID, refilledID);
				}
				p.getCarriedItems().remove(new Item(ItemId.CUT_REED_PLANT.id()));
				mes(p, 0, "The cut reed is soaked through with water and is now all soggy.");
			} else {
				mes(p, p.getWorld().getServer().getConfig().GAME_TICK * 2, "You start to syphon some water up the tube...");
				mes(p, 0, "But you have nothing to put the water in.");
			}
		}
	}

	private void replaceTotemPole(Player p, GameObject obj, boolean calledGujuo) {
		if (p.getCarriedItems().hasCatalogID(ItemId.TOTEM_POLE.id(), Optional.of(false))) {
			if (p.getQuestStage(Quests.LEGENDS_QUEST) == 9) {
				p.updateQuestStage(Quests.LEGENDS_QUEST, 10);
			}
			changeloc(obj, p.getWorld().getServer().getConfig().GAME_TICK * 16, 1170);
			p.getCarriedItems().remove(new Item(ItemId.TOTEM_POLE.id()));
			mes(p, "You remove the evil totem pole.",
				"And replace it with the one you carved yourself.",
				"As you do so, you feel a lightness in the air,");
			p.message("almost as if the Kharazi jungle were sighing.");
			p.message("Perhaps Gujuo would like to see the totem pole.");
			if (calledGujuo) {
				Npc gujuo = addnpc(obj.getWorld(), NpcId.GUJUO.id(), p.getX(), p.getY(), 60000 * 3);
				if (gujuo != null) {
					gujuo.initializeTalkScript(p);
				}
			}
		} else {
			p.message("I shall replace it with the Totem pole");
		}
	}

	private boolean blockDescendBeamPostQuest(Player p) {
		return p.getQuestStage(Quests.LEGENDS_QUEST) == -1 &&
			!p.getWorld().getServer().getConfig().LOCKED_POST_QUEST_REGIONS_ACCESSIBLE;
	}
}
