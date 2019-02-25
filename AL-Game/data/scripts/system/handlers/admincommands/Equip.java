package admincommands;

import java.lang.reflect.Field;

import com.aionemu.gameserver.configs.administration.CommandsConfig;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.PersistentState;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.items.ManaStone;
import com.aionemu.gameserver.model.stats.listeners.ItemEquipmentListener;
import com.aionemu.gameserver.model.templates.item.ArmorType;
import com.aionemu.gameserver.model.templates.item.GodstoneInfo;
import com.aionemu.gameserver.model.templates.item.ItemTemplate;
import com.aionemu.gameserver.model.templates.item.ItemType;
import com.aionemu.gameserver.network.aion.serverpackets.SM_STATS_INFO;
import com.aionemu.gameserver.services.EnchantService;
import com.aionemu.gameserver.services.item.ItemPacketService;
import com.aionemu.gameserver.services.item.ItemSocketService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.RndArray;
import com.aionemu.gameserver.utils.Util;
import com.aionemu.gameserver.utils.chathandlers.AdminCommand;
import com.aionemu.gameserver.world.World;

public class Equip extends AdminCommand
{
	public Equip() {
		super("equip");
	}
	
	@Override
	public void execute(Player admin, String... params) {
		if (params.length != 0) {
			int i = 0;
			if ("help".startsWith(params[i])) {
				if (params[i + 1] == null) {
					showHelp(admin);
				} else if ("socket".startsWith(params[i + 1])) {
					showHelpSocket(admin);
				} else if ("enchant".startsWith(params[i + 1])) {
					showHelpEnchant(admin);
				} else if ("tempering".startsWith(params[i + 1])) {
					showHelpTempering(admin);
				} else if ("plume".startsWith(params[i + 1])) {
					showHelpTempering(admin);
				} else if ("godstone".startsWith(params[i + 1])) {
					showHelpGodstone(admin);
				} else if ("temperarmor".startsWith(params[i + 1])) {
					showHelpTemperingArmor(admin);
				} else if ("wings".startsWith(params[i + 1])) {
					showHelpWings(admin);
				}
				return;
			}
			Player player = null;
			player = World.getInstance().findPlayer(Util.convertName(params[i]));
			if (player == null) {
				VisibleObject target = admin.getTarget();
				if (target instanceof Player) {
					player = (Player) target;
				} else {
					player = admin;
				}
			} else
				i++;
			if ("socket".startsWith(params[i])) {
				if (admin.getAccessLevel() < CommandsConfig.EQUIP) {
					PacketSendUtility.sendMessage(admin, "You dont have enough rights to execute this command");
					return;
				}
				int manastone = 167000226;
				int quant = 0;
				try {
					manastone = params[i + 1] == null ? manastone : Integer.parseInt(params[i + 1]);
					quant = params[i + 2] == null ? quant : Integer.parseInt(params[i + 2]);
				} catch (Exception ex2) {
					showHelpSocket(admin);
					return;
				}
				socket(admin, player, manastone, quant);
				return;
			} if ("enchant".startsWith(params[i])) {
				if (admin.getAccessLevel() < CommandsConfig.EQUIP) {
					PacketSendUtility.sendMessage(admin, "You dont have enough rights to execute this command");
					return;
				}
				int enchant = 0;
				try {
					enchant = params[i + 1] == null ? enchant : Integer.parseInt(params[i + 1]);
				} catch (Exception ex) {
					showHelpEnchant(admin);
					return;
				}
				enchant(admin, player, enchant);
				return;
			} if ("wings".startsWith(params[i])) {
				if (admin.getAccessLevel() < CommandsConfig.EQUIP) {
					PacketSendUtility.sendMessage(admin, "You dont have enough rights to execute this command");
					return;
				}
				int enchant = 0;
				try {
					enchant = params[i + 1] == null ? enchant : Integer.parseInt(params[i + 1]);
				} catch (Exception ex) {
					showHelpWings(admin);
					return;
				}
				wings(admin, player, enchant);
				return;
			} if ("tempering".startsWith(params[i])) {
				if (admin.getAccessLevel() < CommandsConfig.EQUIP) {
					PacketSendUtility.sendMessage(admin, "You dont have enough rights to execute this command");
					return;
				}
				int tempering = 0;
				try {
					tempering = params[i + 1] == null ? tempering : Integer.parseInt(params[i + 1]);
				} catch (Exception ex) {
					showHelpTempering(admin);
					return;
				}
				tempering(admin, player, tempering);
				return;
			} if ("plume".startsWith(params[i])) {
				if (admin.getAccessLevel() < CommandsConfig.EQUIP) {
					PacketSendUtility.sendMessage(admin, "You dont have enough rights to execute this command");
					return;
				}
				int plume = 0;
				try {
					plume = params[i + 1] == null ? plume : Integer.parseInt(params[i + 1]);
				} catch (Exception ex) {
					showHelpPlume(admin);
					return;
				}
				plume(admin, player, plume);
				return;
			} if ("godstone".startsWith(params[i])) {
				if (admin.getAccessLevel() < CommandsConfig.EQUIP) {
					PacketSendUtility.sendMessage(admin, "You dont have enough rights to execute this command");
					return;
				}
				int godstone = 100;
				try {
					godstone = params[i + 1] == null ? godstone : Integer.parseInt(params[i + 1]);
				} catch (Exception ex) {
					showHelpGodstone(admin);
					return;
				}
				godstone(admin, player, godstone);
				return;
			} if ("temperarmor".startsWith(params[i])) {
				if (admin.getAccessLevel() < CommandsConfig.EQUIP) {
					PacketSendUtility.sendMessage(admin, "You dont have enough rights to execute this command");
					return;
				}
				int temperArmor = 0;
				try {
					temperArmor = params[i + 1] == null ? temperArmor : Integer.parseInt(params[i + 1]);
				} catch (Exception ex) {
					showHelpTemperingArmor(admin);
					return;
				}
				temperArmor(admin, player, temperArmor);
				return;
			}
		}
		showHelp(admin);
	}
	
	private void socket(Player admin, Player player, int manastone, int quant) {
		if (manastone != 0 && (manastone < 167000226 || manastone > 167010369)) {
			PacketSendUtility.sendMessage(admin, "You are suposed to give the item id for a" + " Manastone or 0 to remove all manastones.");
			return;
		} for (Item targetItem : player.getEquipment().getEquippedItemsWithoutStigma()) {
			if (isUpgradable(targetItem)) {
				if (manastone == 0) {
					ItemEquipmentListener.removeStoneStats(targetItem.getItemStones(), player.getGameStats());
					ItemSocketService.removeAllManastone(player, targetItem);
				} else {
					int counter = quant <= 0 ? getMaxSlots(targetItem) : quant;
					while (targetItem.getItemStones().size() < getMaxSlots(targetItem) && counter >= 0) {
						ManaStone manaStone = ItemSocketService.addManaStone(targetItem, manastone);
						ItemEquipmentListener.addStoneStats(targetItem, manaStone, player.getGameStats());
						counter--;
					}
				}
				PacketSendUtility.sendPacket(player, new SM_STATS_INFO(player));
				ItemPacketService.updateItemAfterInfoChange(player, targetItem);
				targetItem.setPersistentState(PersistentState.UPDATE_REQUIRED);
			}
		} if (manastone == 0) {
			if (player == admin) {
				PacketSendUtility.sendMessage(player, "All Manastones removed from all equipped Items");
			} else {
				PacketSendUtility.sendMessage(admin, "All Manastones removed from all equipped Items by the Player " + player.getName());
				PacketSendUtility.sendMessage(player, "Admin " + admin.getName() + " removed all manastones from all your equipped Items");
			}
		} else {
			if (player == admin) {
				PacketSendUtility.sendMessage(player, quant + "x [item: " + manastone + "] were added to free slots on all equipped items");
			} else {
				PacketSendUtility.sendMessage(admin, quant + "x [item: " + manastone + "] were added to free slots on all equipped items by the Player " + player.getName());
				PacketSendUtility.sendMessage(player, "Admin " + admin.getName() + " added " + quant + "x [item: " + manastone + "] to free slots on all your equipped items");
			}
		}
	}
	
	private void godstone(Player admin, Player player, int godstone) {
		Item targetItem = player.getEquipment().getMainHandWeapon();
		if (godstone > 168000028) {
			ItemTemplate itemTemplate = DataManager.ITEM_DATA.getItemTemplate(godstone);
			GodstoneInfo godstoneInfo = itemTemplate.getGodstoneInfo();
			if (godstoneInfo == null) {
				PacketSendUtility.sendMessage(admin, "Wrong GodStone ItemID");
				return;
			}
			targetItem.addGodStone(godstone);
			PacketSendUtility.sendPacket(player, new SM_STATS_INFO(player));
			ItemPacketService.updateItemAfterInfoChange(player, targetItem);
			targetItem.setPersistentState(PersistentState.UPDATE_REQUIRED);
			if (player == admin) {
				PacketSendUtility.sendMessage(player, "[Item: " + godstone + "] socketed to your equipped MainHandWeapon [Item: " + targetItem.getItemId() + "]");
			} else {
				PacketSendUtility.sendMessage(admin, "[Item: " + godstone + "] socketed to the Player " + player.getName() + "equipped MainHandWeapon [Item: " + targetItem.getItemId() + "]");
				PacketSendUtility.sendMessage(player, "Admin " + admin.getName() + " socketed [Item: " + godstone + "]  to you equipped MainHandWeapon [Item: " + targetItem.getItemId() + "]");
			}
			targetItem.getGodStone().onEquip(player);
		} else if (targetItem.getGodStone() != null) {
			targetItem.getGodStone().onUnEquip(player);
			try {
				if (godstone <= 100) {
					godstone *= 10;
				} if (godstone > 1000) {
					godstone = 1000;
				}
				Class<?> gs = targetItem.getGodStone().getClass();
				Field probability = gs.getDeclaredField("probability");
				Field probabilityLeft = gs.getDeclaredField("probability");
				probability.setAccessible(true);
				probabilityLeft.setAccessible(true);
				probability.setInt(targetItem.getGodStone(), godstone);
				probabilityLeft.setInt(targetItem.getGodStone(), godstone);
			} catch (Exception ex2) {
				PacketSendUtility.sendMessage(admin, "Occurs an error.");
				return;
			}
			targetItem.getGodStone().onEquip(player);
			if (player == admin) {
				PacketSendUtility.sendMessage(player, "Your godstone on your MainHandWeapon will now activate around " + (godstone/10) + " percent of the time.");
			} else {
				PacketSendUtility.sendMessage(admin, "Player " + player.getName() + " godstone on MainHandWeapon will now activate around " + godstone + " percent of the time.");
				PacketSendUtility.sendMessage(player, "Admin " + admin.getName() + " blessed your godstone on your MainHandWeapon to now activate around " + godstone + " percent of the time.");
			}
		}
	}
	
	private void enchant(Player admin, Player player, int enchant) {
		for (Item targetItem : player.getEquipment().getEquippedItemsWithoutStigma()) {
			if (isUpgradable(targetItem)) {
				if (targetItem.getEnchantLevel() == enchant) {
					continue;
				} if (enchant > 25) {
					enchant = 25;
				} if (enchant < 0) {
					enchant = 0;
				} if (enchant > targetItem.getItemTemplate().getMaxEnchantLevel()) {
					targetItem.setAmplification(true);
				} if (enchant < 15) {
					targetItem.setAmplification(false);
				}
				if (enchant > 20) {
					int skillId = getRndSkills(targetItem);
					targetItem.setAmplificationSkill(skillId);
				}
				targetItem.setEnchantLevel(enchant);
				if (targetItem.isEquipped()) {
					player.getGameStats().updateStatsVisually();
				}
				ItemPacketService.updateItemAfterInfoChange(player, targetItem);
			}
		} if (player == admin) {
			PacketSendUtility.sendMessage(player, "All equipped items were enchanted to level " + enchant);
		} else {
			PacketSendUtility.sendMessage(admin, "All equipped items by the Player " + player.getName() + " were enchanted to " + enchant);
			PacketSendUtility.sendMessage(player, "Admin " + admin.getName() + " enchanted all your equipped items to level " + enchant);
		}
	}
	
	private void wings(Player admin, Player player, int enchant) {
		for (Item targetItem : player.getEquipment().getEquippedItemsWithoutStigma()) {
			if (isWingsUpgrade(targetItem)) {
				if (targetItem.getEnchantLevel() == enchant) {
					continue;
				} if (enchant > 19) {
					enchant = 19;
				} if (enchant < 0) {
					enchant = 0;
				} if (enchant > targetItem.getItemTemplate().getMaxEnchantLevel()) {
					targetItem.setAmplification(true);
				} if (enchant < 15) {
					targetItem.setAmplification(false);
				}
				if (enchant > 20) {
					int skillId = getRndSkills(targetItem);
					targetItem.setAmplificationSkill(skillId);
				}
				targetItem.setEnchantLevel(enchant);
				if (targetItem.isEquipped()) {
					player.getGameStats().updateStatsVisually();
				}
				ItemPacketService.updateItemAfterInfoChange(player, targetItem);
			}
		} if (player == admin) {
			PacketSendUtility.sendMessage(player, "All equipped items were enchanted to level " + enchant);
		} else {
			PacketSendUtility.sendMessage(admin, "All equipped items by the Player " + player.getName() + " were enchanted to " + enchant);
			PacketSendUtility.sendMessage(player, "Admin " + admin.getName() + " enchanted all your equipped items to level " + enchant);
		}
	}
	
	public int getRndSkills(Item item) {
		if (item.getItemTemplate().getArmorType() == ArmorType.WING) {
			return RndArray.get(skills4Wing);
		} switch (item.getItemTemplate().getCategory()) {
			case SWORD:
			case DAGGER:
			case MACE:
			case ORB:
			case SPELLBOOK:
			case GREATSWORD:
			case POLEARM:
			case STAFF:
			case BOW:
			case GUN:
			case CANNON:
			case HARP:
			case KEYBLADE:
			case SHIELD:
				return RndArray.get(skills4WeaponShield);
			case JACKET:
				return RndArray.get(skills4Jacket);
			case PANTS:
				return RndArray.get(skills4Pant);
			case SHOULDERS:
				return RndArray.get(skills4Shoulder);
			case GLOVES:
				return RndArray.get(skills4Glove);
			case SHOES:
				return RndArray.get(skills4Shoes);
			default:
				return 0;
		}
	}
	
	private static final int[] skills4WeaponShield = {
		13001, 13002, 13003, 13004, 13005, 13006,
		13007, 13008, 13009, 13010, 13011, 13012,
		13013, 13014, 13015, 13016, 13017, 13018,
		13019, 13020, 13021, 13022, 13023, 13024,
		13025, 13026, 13027, 13028, 13029, 13030,
		13031, 13032, 13033, 13034, 13035, 13036,
		13037, 13228, 13229, 13230, 13231, 13232,
		13233, 13234
	};
	private static final int[] skills4Glove = {
		13038, 13039, 13040, 13041, 13042, 13043,
		13044, 13045, 13046, 13047, 13048, 13049,
		13050, 13051, 13052, 13053, 13054, 13055,
		13056, 13057, 13058, 13059, 13060, 13247,
		13248, 13249, 13250, 13251, 13252, 13253,
		13254
	};
	private static final int[] skills4Pant = {
		13061, 13062, 13063, 13064, 13065, 13066,
		13067, 13068, 13069, 13070, 13071, 13072,
		13073, 13074, 13075, 13076, 13077, 13078,
		13079, 13080, 13081
	};
	private static final int[] skills4Shoulder = {
		13082, 13083, 13084, 13085, 13086, 13087,
		13088, 13089, 13090, 13091, 13092, 13093,
		13094, 13095, 13096, 13097, 13098, 13099,
		13100, 13101, 13102, 13103, 13104, 13105,
		13106, 13107, 13266, 13267, 13268, 13269,
		13270, 13271
	};
	private static final int[] skills4Shoes = {
		13108, 13109, 13110, 13111, 13112, 13113,
		13114, 13115, 13116, 13117, 13118, 13119,
		13120, 13121, 13122, 13123, 13124, 13125,
		13126, 13127
	};
	private static final int[] skills4Jacket = {
		13128, 13129, 13130, 13131, 13132, 13133,
		13134, 13135, 13136, 13137, 13138, 13139,
		13140, 13141, 13142, 13143, 13144, 13145,
		13146, 13147, 13235, 13236, 13237, 13238,
		13239, 13240, 13241, 13242, 13243, 13244,
		13245, 13246
	};
	private static final int[] skills4Wing = {
		13001, 13002, 13003, 13004, 13005, 13006,
		13007, 13008, 13009, 13010, 13011, 13012,
		13013, 13014, 13015, 13016, 13017, 13018,
		13019, 13020, 13021, 13022, 13023, 13024,
		13025, 13026, 13027, 13028, 13029, 13030,
		13031, 13032, 13033, 13034, 13035, 13036,
		13037, 13228, 13229, 13230, 13231, 13232,
		13233, 13234
	};
	
	private void tempering(Player admin, Player player, int tempering) {
		for (Item targetItem: player.getEquipment().getEquippedItemsWithoutStigma()) {
			if (isTempering(targetItem)) {
				if (targetItem.getAuthorize() == tempering) {
					continue;
				} if (tempering > 8) {
					tempering = 8;
				} if (tempering < 0) {
					tempering = 0;
				}
				targetItem.setAuthorize(tempering);
				if (targetItem.isEquipped()) {
					player.getGameStats().updateStatsVisually();
				}
				ItemPacketService.updateItemAfterInfoChange(player, targetItem);
			}
		} if (player == admin) {
			PacketSendUtility.sendMessage(player, "All equipped items were tempering to level " + tempering);
		} else {
			PacketSendUtility.sendMessage(admin, "All equipped items by the Player " + player.getName() + " were tempering to " + tempering);
			PacketSendUtility.sendMessage(player, "Admin " + admin.getName() + " tempering all your equipped items to level " + tempering);
		}
	}
	
	private void temperArmor(Player admin, Player player, int tempering) {
		for (Item targetItem: player.getEquipment().getEquippedItemsWithoutStigma()) {
			if (isTemperingArmor(targetItem)) {
				if (targetItem.getAuthorize() == tempering) {
					continue;
				} if (tempering > 9) {
					tempering = 9;
				} if (tempering < 0) {
					tempering = 0;
				}
				targetItem.setAuthorize(tempering);
				if (targetItem.isEquipped()) {
					player.getGameStats().updateStatsVisually();
				}
				ItemPacketService.updateItemAfterInfoChange(player, targetItem);
			}
		} if (player == admin) {
			PacketSendUtility.sendMessage(player, "All equipped items were tempering to level " + tempering);
		} else {
			PacketSendUtility.sendMessage(admin, "All equipped items by the Player " + player.getName() + " were tempering to " + tempering);
			PacketSendUtility.sendMessage(player, "Admin " + admin.getName() + " tempering all your equipped items to level " + tempering);
		}
	}
	
	private void plume(Player admin, Player player, int plume) {
		for (Item targetItem: player.getEquipment().getEquippedItemsWithoutStigma()) {
			if (isPlume(targetItem)) {
				if (targetItem.getAuthorize() == plume) {
					continue;
				} if (plume > 15) {
					plume = 15;
				} if (plume < 0) {
					plume = 0;
				}
				targetItem.setAuthorize(plume);
				if (targetItem.isEquipped()) {
					player.getGameStats().updateStatsVisually();
				}
				ItemPacketService.updateItemAfterInfoChange(player, targetItem);
			}
		} if (player == admin) {
			PacketSendUtility.sendMessage(player, "Plume tempering to level " + plume);
		} else {
			PacketSendUtility.sendMessage(admin, "Plume Enchant by the Player " + player.getName() + " were tempering to " + plume);
			PacketSendUtility.sendMessage(player, "Admin " + admin.getName() + " tempering plume your equipped items to level " + plume);
		}
	}
	
	public static boolean isTempering(Item item) {
		if (item.getItemTemplate().isNoEnchant()) {
			return false;
		} if (item.getItemTemplate().isArmor()) {
			int pt = item.getItemTemplate().getItemSlot();
			if (pt == 4 || /* Helm */
				pt == 192 || /* Earring */
				pt == 768 || /* Rings */
				pt == 1024 || /* Necklace */
				pt == 65536 || /* Belt */
				pt == 2097152) { /* Bracelet */
				return true;
			}
		}
		return false;
	}
	
	public static boolean isTemperingArmor(Item item) {
		if (item.getItemTemplate().isNoEnchant()) {
			return false;
		
		} if(item.getItemTemplate().isWeapon()) {
			return true;
		}if (item.getItemTemplate().isArmor()) {
			int au = item.getItemTemplate().getMaxAuthorize();
			int at = item.getItemTemplate().getItemSlot();
			if (at == 1 || /* Main Hand */
				    at == 2 || /* Sub Hand */
				    at == 8 || /* Jacket */
				    at == 16 || /* Gloves */
				    at == 32 || /* Boots */
				    at == 2048 || /* Shoulder */
				    at == 4096 || /* Pants */
				    at == 131072 || /* Main Off Hand */
				    at == 262144) { /* Sub Off Hand */
					return true;
				}
		}
		return false;
	}
	
	public static boolean isPlume(Item item) {
		if (item.getItemTemplate().isNoEnchant()) {
			return false;
		} if (item.getItemTemplate().isArmor()) {
			int pt = item.getItemTemplate().getItemSlot();
			if (pt == 524288) {
				return true;
			}
		}
		return false;
	}
	
	public static boolean isUpgradable(Item item) {
		if (item.getItemTemplate().isNoEnchant()) {
			return false;
		} if (item.getItemTemplate().isWeapon()) {
			return true;
		} if (item.getItemTemplate().isArmor()) {
			int au = item.getItemTemplate().getMaxAuthorize();
			int at = item.getItemTemplate().getItemSlot();
			if (at == 1 || /* Main Hand */
			    at == 2 || /* Sub Hand */
			    at == 8 || /* Jacket */
			    at == 16 || /* Gloves */
			    at == 32 || /* Boots */
			    at == 2048 || /* Shoulder */
			    at == 4096 || /* Pants */
			    at == 131072 || /* Main Off Hand */
			    at == 262144) { /* Sub Off Hand */
				return true;
			}
		}
		return false;
	}
	
	public static boolean isWingsUpgrade(Item item) {
		if (item.getItemTemplate().isNoEnchant()) {
			return false;
		} if (item.getItemTemplate().isWeapon()) {
			return false;
		} if (item.getItemTemplate().isArmor()) {
			int at = item.getItemTemplate().getItemSlot();
			if (at == 32768) {
				return true;
			}
		}
		return false;
	}
	
	public static int getMaxSlots(Item item) {
		int slots = 0;
		switch (item.getItemTemplate().getItemQuality()) {
			case COMMON:
			case JUNK:
				slots = 1;
			break;
			case RARE:
				slots = 2;
			break;
			case LEGEND:
				slots = 3;
			break;
			case UNIQUE:
				slots = 4;
			break;
			case EPIC:
				slots = 5;
			break;
			case MYTHIC:
				slots = 6;
			break;
			default:
				slots = 0;
			break;
		}
		if (item.getItemTemplate().getItemType() == ItemType.DRACONIC)
			slots += 1;
		if (item.getItemTemplate().getItemType() == ItemType.ABYSS)
			slots += 2;
		return slots;
	}
	
	private void showHelp(Player admin) {
		PacketSendUtility.sendMessage(admin, "[Help: Equip Command]\n"
		+ "  Use //equip help <socket|enchant|tempering|godstone|plume|> for more details on the command.\n"
		+ "  Notice: This command uses smart matching. You may abbreviate most commands.\n"
		+ "  For example: (//equip so 167000226 5) will match to (//equip socket 167000226 5)");
	}
	
	private void showHelpEnchant(Player admin) {
		PacketSendUtility.sendMessage(admin, "Syntax:  //equip [playerName] enchant [EnchantLevel = 0]\n"
		+ "  This command Enchants all items equipped up to 25.\n"
		+ "  Notice: You can ommit parameters between [], especially playerName.\n"
		+ "  Target: Named player, then targeted player, only then self.\n" + "  Default Value: EnchantLevel is 0.");
	}
	
	private void showHelpTempering(Player admin) {
		PacketSendUtility.sendMessage(admin, "Syntax:  //equip [playerName] tempering [TemperanceLevel = 0]\n"
		+ "  This command Tempering all items equipped up to 8.\n"
		+ "  Notice: You can ommit parameters between [], especially playerName.\n"
		+ "  Target: Named player, then targeted player, only then self.\n" + "  Default Value: TemperanceLevel is 0.");
	}
	
	private void showHelpPlume(Player admin) {
		PacketSendUtility.sendMessage(admin, "Syntax:  //plume [playerName] tempering [TemperanceLevel = 0]\n"
		+ "  This command Tempering all items equipped up to 15.\n"
		+ "  Notice: You can ommit parameters between [], especially playerName.\n"
		+ "  Target: Named player, then targeted player, only then self.\n" + "  Default Value: TemperanceLevel is 0.");
	}
	
	private void showHelpSocket(Player admin) {
		PacketSendUtility.sendMessage(admin, "Syntax:  //equip [playerName] socket [ManastoneID = 167000226] [Quantity = 0]\n"
		+ "  This command Sockets all free slots on equipped items, with the given manastone id.\n"
		+ "  Use ManastoneID = 0 to remove all Manastones. Quantity = 0 means to fill all free slots.\n"
		+ "  Notice: You can ommit parameters between [], especially playerName.\n"
		+ "  Target: Named player, then targeted player, only then self.\n"
		+ "  Default Value: ManastoneID is 167000226, Quantity is 0 meaning fill all slots.");
	}
	
	private void showHelpGodstone(Player admin) {
		PacketSendUtility.sendMessage(admin, "Syntax:  //equip [playerName] godstone [rate = 100|GodStoneID]\n"
		+ "  This command changes the godstone activation rate to the given number(0-100).\n"
		+ "  Give a GodStone ItemID and it will be socketed on you Main Hand Weapon.\n"
		+ "  Notice: You can ommit parameters between [], especially playerName.\n"
		+ "  Target: Named player, then targeted player, only then self.\n"
		+ "  Default Value: Rate is 100 witch is the default action .");
	}
	
	private void showHelpTemperingArmor(Player admin) {
		//
	}
	
	private void showHelpWings(Player admin) {
		//
	}
	
	@Override
	public void onFail(Player player, String message) {
		showHelp(player);
	}
}