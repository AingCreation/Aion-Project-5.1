package com.aionemu.gameserver.services.item;

import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.commons.utils.Rnd;
import com.aionemu.gameserver.configs.main.LoggingConfig;
import com.aionemu.gameserver.dao.AdminCommandLogDAO;
import com.aionemu.gameserver.dao.ItemStoneListDAO;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.gameobjects.AionObject;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.PersistentState;
import com.aionemu.gameserver.model.gameobjects.player.Equipment;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.items.ItemId;
import com.aionemu.gameserver.model.items.ManaStone;
import com.aionemu.gameserver.model.items.storage.Storage;
import com.aionemu.gameserver.model.templates.item.ArmorType;
import com.aionemu.gameserver.model.templates.item.ItemTemplate;
import com.aionemu.gameserver.model.templates.quest.QuestItems;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.services.item.ItemPacketService.ItemAddType;
import com.aionemu.gameserver.services.item.ItemPacketService.ItemUpdateType;
import com.aionemu.gameserver.taskmanager.tasks.ExpireTimerTask;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.Util;
import com.aionemu.gameserver.utils.idfactory.IDFactory;
import com.aionemu.gameserver.world.World;
import com.google.common.base.Preconditions;
import com.google.common.collect.Collections2;

/**
 * @author KID
 */
public class ItemService {

	private static final Logger log = LoggerFactory.getLogger("ITEM_LOG");

	public static final ItemUpdatePredicate DEFAULT_UPDATE_PREDICATE = new ItemUpdatePredicate(ItemAddType.ITEM_COLLECT, ItemUpdateType.INC_ITEM_COLLECT);

	public static void loadItemStones(Collection<Item> itemList) {
		if (itemList != null && itemList.size() > 0) {
			DAOManager.getDAO(ItemStoneListDAO.class).load(itemList);
		}
	}

	public static long addItem(Player player, int itemId, long count) {
		return addItem(player, itemId, count, DEFAULT_UPDATE_PREDICATE);
	}

	public static long addItem(Player player, int itemId, long count, ItemUpdatePredicate predicate) {
		return addItem(player, itemId, count, null, predicate);
	}

    public static long addItem(Player player, int itemId, long count, String creator) {
        return addItem(player, itemId, count, DEFAULT_UPDATE_PREDICATE, creator);
    }

    public static long addItem(Player player, int itemId, long count, ItemUpdatePredicate predicate, String creator) {
        return addItem(player, itemId, count, null, predicate, creator);
    }
    public static long addItem(Player player, int itemId, long count, Item sourceItem, ItemUpdatePredicate predicate, String creator) {
        ItemTemplate itemTemplate = DataManager.ITEM_DATA.getItemTemplate(itemId);
        if (count <= 0 || itemTemplate == null) {
            return 0;
        }
        Preconditions.checkNotNull(itemTemplate, "No item with id " + itemId);
        Preconditions.checkNotNull(predicate, "Predicate is not supplied");

        if (LoggingConfig.LOG_ITEM) {
            log.info("[ITEM] ID/Count"
                    + (LoggingConfig.ENABLE_ADVANCED_LOGGING ? "/Item Name - " + itemTemplate.getTemplateId() + "/" + count + "/"
                    + itemTemplate.getName() : " - " + itemTemplate.getTemplateId() + "/" + count) + " to player " + player.getName());
        }

        Storage inventory = player.getInventory();
        if (itemTemplate.isKinah()) {
            // quests do not add here
            inventory.increaseKinah(count);
            return 0;
        }

        if (itemTemplate.isStackable()) {
            count = addStackableItem(player, itemTemplate, count, predicate, creator);
        } else {
            count = addNonStackableItem(player, itemTemplate, count, sourceItem, predicate, creator);
        }

        if (inventory.isFull(itemTemplate.getExtraInventoryId()) && count > 0) {
            PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_DICE_INVEN_ERROR);
        }
        return count;
    }

	/**
	 * Add new item based on all sourceItem values
	 */
	public static long addItem(Player player, Item sourceItem) {
		return addItem(player, sourceItem.getItemId(), sourceItem.getItemCount(), sourceItem, DEFAULT_UPDATE_PREDICATE);
	}

	public static long addItem(Player player, Item sourceItem, ItemUpdatePredicate predicate) {
		return addItem(player, sourceItem.getItemId(), sourceItem.getItemCount(), sourceItem, predicate);
	}

	public static long addItem(Player player, int itemId, long count, Item sourceItem) {
		return addItem(player, itemId, count, sourceItem, DEFAULT_UPDATE_PREDICATE);
	}

	/**
	 * Add new item based on sourceItem values
	 */
	public static long addItem(Player player, int itemId, long count, Item sourceItem, ItemUpdatePredicate predicate) {
		ItemTemplate itemTemplate = DataManager.ITEM_DATA.getItemTemplate(itemId);
		if (count <= 0 || itemTemplate == null) {
			return 0;
		}
		Preconditions.checkNotNull(itemTemplate, "No item with id " + itemId);
		Preconditions.checkNotNull(predicate, "Predicate is not supplied");

		if (LoggingConfig.LOG_ITEM) {
			log.info("[ITEM] ID/Count" + (LoggingConfig.ENABLE_ADVANCED_LOGGING ? "/Item Name - " + itemTemplate.getTemplateId() + "/" + count + "/" + itemTemplate.getName() : " - " + itemTemplate.getTemplateId() + "/" + count) + " to player " + player.getName());
		}

		Storage inventory = player.getInventory();
		if (itemTemplate.isKinah()) {
			// quests do not add here
			inventory.increaseKinah(count);
			return 0;
		}

		if (itemTemplate.isStackable()) {
			count = addStackableItem(player, itemTemplate, count, predicate);
		}
		else {
			count = addNonStackableItem(player, itemTemplate, count, sourceItem, predicate);
		}

		if (inventory.isFull(itemTemplate.getExtraInventoryId()) && count > 0) {
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_DICE_INVEN_ERROR);
		}
		return count;
	}
	
	private static long addNonStackableItem(Player player, ItemTemplate itemTemplate, long count, Item sourceItem, ItemUpdatePredicate predicate, String creator) {
        Storage inventory = player.getInventory();
        while (!inventory.isFull(itemTemplate.getExtraInventoryId()) && count > 0) {
            Item newItem = ItemFactory.newItem(itemTemplate.getTemplateId());
            newItem.setItemCreator(creator);
            if (!itemTemplate.getExceedEnchant() && itemTemplate.isWeapon() || !itemTemplate.getExceedEnchant() && itemTemplate.isArmor()) {
                newItem.setEnchantLevel(itemTemplate.getMaxEnchantLevel());
            }
            if (newItem.getExpireTime() != 0) {
                ExpireTimerTask.getInstance().addTask(newItem, player);
            }
            if (sourceItem != null) {
                copyItemInfo(sourceItem, newItem);
            }
            predicate.changeItem(newItem);
            inventory.add(newItem, predicate.getAddType());

            if (creator.contains("Added By GM")) {
                String[] splitString = creator.split(":");
                String adminName = splitString[1].replaceAll("\\s", "");
                Player admin = World.getInstance().findPlayer(Util.convertName(adminName));
                if (admin != null) {
                	DAOManager.getDAO(AdminCommandLogDAO.class).insertCommandAdd(admin.getObjectId(), admin.getName(), player.getObjectId(), player.getName(), newItem.getObjectId(), newItem.getItemId(), newItem.getItemName(), (int)count, "");
                }
            }
            count--;
        }
        return count;
    }

	/**
	 * Add non-stackable item to inventory
	 */
	private static long addNonStackableItem(Player player, ItemTemplate itemTemplate, long count, Item sourceItem, ItemUpdatePredicate predicate) {
		Storage inventory = player.getInventory();
		while (!inventory.isFull(itemTemplate.getExtraInventoryId()) && count > 0) {
			Item newItem = ItemFactory.newItem(itemTemplate.getTemplateId());

			if (newItem.getExpireTime() != 0) {
				ExpireTimerTask.getInstance().addTask(newItem, player);
			}
			if (sourceItem != null) {
				copyItemInfo(sourceItem, newItem);
			}
			predicate.changeItem(newItem);
			inventory.add(newItem, predicate.getAddType());
			count--;
		}
		return count;
	}

	/**
	 * Copy some item values like item stones and enchange level
	 */
	private static void copyItemInfo(Item sourceItem, Item newItem) {
		newItem.setOptionalSocket(sourceItem.getOptionalSocket());
		newItem.setEnchantBonus(sourceItem.getEnchantBonus());
		if (sourceItem.hasManaStones()) {
			for (ManaStone manaStone : sourceItem.getItemStones()) {
				ItemSocketService.addManaStone(newItem, manaStone.getItemId());
			}
		} 
		if (sourceItem.getGodStone() != null) {
			newItem.addGodStone(sourceItem.getGodStone().getItemId());
		} 
		if (sourceItem.getEnchantLevel() > 0) {
			newItem.setEnchantLevel(sourceItem.getEnchantLevel());
		} 
		if (sourceItem.getEnchantBonus() > 0) {
			newItem.setEnchantBonus(sourceItem.getEnchantBonus());
		}
		if (sourceItem.isSoulBound()) {
			newItem.setSoulBound(true);
		}
		newItem.setBonusNumber(sourceItem.getBonusNumber());
		newItem.setRandomStats(sourceItem.getRandomStats());
		newItem.setIdianStone(sourceItem.getIdianStone());
		newItem.setRandomCount(sourceItem.getRandomCount());
		newItem.setItemColor(sourceItem.getItemColor());
		newItem.setItemSkinTemplate(sourceItem.getItemSkinTemplate());
	}
	
	 /**
     * Add stackable item to inventory
     */
    private static long addStackableItem(Player player, ItemTemplate itemTemplate, long count, ItemUpdatePredicate predicate, String creator) {
        Storage inventory = player.getInventory();
        Collection<Item> items = inventory.getItemsByItemId(itemTemplate.getTemplateId());
        for (Item item : items) {
            if (count == 0) {
                break;
            }
            count = inventory.increaseItemCount(item, count, predicate.getUpdateType(item, true));
        }

        // dirty & hacky check for arrows and shards...
        if (itemTemplate.getArmorType() == ArmorType.SHARD) {
            Equipment equipement = player.getEquipment();
            items = equipement.getEquippedItemsByItemId(itemTemplate.getTemplateId());
            for (Item item : items) {
                if (count == 0) {
                    break;
                }
                count = equipement.increaseEquippedItemCount(item, count);
                item.setItemCreator(item.getItemCreator());
            }
        }

        while (!inventory.isFull(itemTemplate.getExtraInventoryId()) && count > 0) {
            Item newItem = ItemFactory.newItem(itemTemplate.getTemplateId(), count);
            count -= newItem.getItemCount();
            inventory.add(newItem, predicate.getAddType());
            newItem.setItemCreator(creator);
            if (creator.contains("Added By GM")) {
                String[] splitString = creator.split(":");
                String adminName = splitString[1].replaceAll("\\s", "");
                Player admin = World.getInstance().findPlayer(Util.convertName(adminName));
                if (admin != null) {
                	DAOManager.getDAO(AdminCommandLogDAO.class).insertCommandAdd(admin.getObjectId(), admin.getName(), player.getObjectId(), player.getName(), newItem.getObjectId(), newItem.getItemId(), newItem.getItemName(), (int)count, "");
                }
            }
        }
        return count;
    }

	/**
	 * Add stackable item to inventory
	 */
	private static long addStackableItem(Player player, ItemTemplate itemTemplate, long count, ItemUpdatePredicate predicate) {
		Storage inventory = player.getInventory();
		Collection<Item> items = inventory.getItemsByItemId(itemTemplate.getTemplateId());
		for (Item item : items) {
			if (count == 0) {
				break;
			}
			count = inventory.increaseItemCount(item, count, predicate.getUpdateType(item, true));
		} if (itemTemplate.getArmorType() == ArmorType.SHARD) {
			Equipment equipement = player.getEquipment();
			items = equipement.getEquippedItemsByItemId(itemTemplate.getTemplateId());
			for (Item item : items) {
				if (count == 0) {
					break;
				}
				count = equipement.increaseEquippedItemCount(item, count);
			}
		}

		while (!inventory.isFull(itemTemplate.getExtraInventoryId()) && count > 0) {
			Item newItem = ItemFactory.newItem(itemTemplate.getTemplateId(), count);
			count -= newItem.getItemCount();
			inventory.add(newItem, predicate.getAddType());
		}
		return count;
	}
	
	public static boolean addQuestItems(Player player, List<QuestItems> questItems) {
		return addQuestItems(player, questItems, DEFAULT_UPDATE_PREDICATE);
	}

	public static boolean addQuestItems(Player player, List<QuestItems> questItems, ItemUpdatePredicate predicate) {
		int slotReq = 0, specialSlot = 0;

		for (QuestItems qi : questItems) {
			if (qi.getItemId() != ItemId.KINAH.value() && qi.getCount() != 0) {
				ItemTemplate template = DataManager.ITEM_DATA.getItemTemplate(qi.getItemId());
				long stackCount = template.getMaxStackCount();
				long count = qi.getCount() / stackCount;
				if (qi.getCount() % stackCount != 0)
					count++;
				if (template.getExtraInventoryId() > 0) {
					specialSlot += count;
				}
				else {
					slotReq += count;
				}
			}
		}
		Storage inventory = player.getInventory();
		if (slotReq > 0 && inventory.getFreeSlots() < slotReq) {
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_DECOMPRESS_INVENTORY_IS_FULL);
			return false;
		}
		if (specialSlot > 0 && inventory.getSpecialCubeFreeSlots() < specialSlot) {
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_DECOMPRESS_INVENTORY_IS_FULL);
			return false;
		}
		for (QuestItems qi : questItems) {
			addItem(player, qi.getItemId(), qi.getCount(), predicate);
		}
		return true;
	}

	public static void releaseItemId(Item item) {
		IDFactory.getInstance().releaseId(item.getObjectId());
	}

	public static void releaseItemIds(Collection<Item> items) {
		Collection<Integer> idIterator = Collections2.transform(items, AionObject.OBJECT_TO_ID_TRANSFORMER);
		IDFactory.getInstance().releaseIds(idIterator);
	}

	public static boolean dropItemToInventory(int playerObjectId, int itemId) {
		return dropItemToInventory(World.getInstance().findPlayer(playerObjectId), itemId);
	}

	public static boolean dropItemToInventory(Player player, int itemId) {
		if (player == null || !player.isOnline()) {
			return false;
		}
		Storage storage = player.getInventory();
		if (storage.getFreeSlots() < 1) {
			List<Item> items = storage.getItemsByItemId(itemId);
			boolean hasFreeStack = false;
			for (Item item : items) {
				if (item.getPersistentState() == PersistentState.DELETED || item.getItemCount() < item.getItemTemplate().getMaxStackCount()) {
					hasFreeStack = true;
					break;
				}
			} if (!hasFreeStack)
				return false;
		}
		return addItem(player, itemId, 1) == 0;
	}
	
	public static Item newItem(int itemId, long count, String crafterName, int ownerId, long tempItemTime, int tempTradeTime) {
        ItemTemplate itemTemplate = DataManager.ITEM_DATA.getItemTemplate(itemId);

        if (itemTemplate == null) {
            log.error(
                "Item was not populated correctly. Item template is missing for item id: " +
                itemId);

            return null;
        }

        // Outside expire time has higher priority
        if (tempItemTime <= 0) {
            tempItemTime = itemTemplate.getExpireTime() * 60;
        }

        if (tempTradeTime <= 0) {
            tempTradeTime = itemTemplate.getTempExchangeTime() * 60;
        }

        int maxStackCount = (int) itemTemplate.getMaxStackCount();

        if ((count > maxStackCount) && (maxStackCount != 0) &&
                !itemTemplate.isKinah()) {
            count = maxStackCount;
        }

        // TODO if Item object will contain ownerId - item can be saved to DB before return
        Item temp = new Item(IDFactory.getInstance().nextId(), itemTemplate, count, false, 0);

        if (itemTemplate.isWeapon() || itemTemplate.isArmor()) {
            temp.setOptionalSocket(Rnd.get(0, itemTemplate.getOptionSlotBonus()));
        }
        if (itemTemplate.getMaxEnchantBonus() != 0) {
			temp.setEnchantBonus(Rnd.get(0, itemTemplate.getMaxEnchantBonus()));
		}

        return temp;
    }
	
	public static boolean checkRandomTemplate(int randomItemId) {
		ItemTemplate template = DataManager.ITEM_DATA.getItemTemplate(randomItemId);
		return template != null;
	}
	
	public static class ItemUpdatePredicate {
		private final ItemUpdateType itemUpdateType;
		private final ItemAddType itemAddType;

		public ItemUpdatePredicate(ItemAddType itemAddType, ItemUpdateType itemUpdateType) {
			this.itemUpdateType = itemUpdateType;
			this.itemAddType = itemAddType;
		}

		public ItemUpdatePredicate() {
			this(ItemAddType.ITEM_COLLECT, ItemUpdateType.INC_ITEM_COLLECT);
		}

		public ItemUpdateType getUpdateType(Item item, boolean isIncrease) {
			if (item.getItemTemplate().isKinah())
				return ItemUpdateType.getKinahUpdateTypeFromAddType(itemAddType, isIncrease);
			return itemUpdateType;
		}

		public ItemAddType getAddType() {
			return itemAddType;
		}

		public boolean changeItem(Item item) {
			return true;
		}
	}
	
	public static void makeUpgradeItem(Player player, Item sourceItem, Item newItem) {
		Storage inventory = player.getInventory();
		newItem.setOptionalSocket(sourceItem.getOptionalSocket());
		if (sourceItem.getFusionedItemId() != 0) {
			newItem.setFusionedItem(sourceItem.getFusionedItemTemplate());
		}

		ItemSocketService.copyManaStones(sourceItem, newItem);

		if (sourceItem.getGodStone() != null) {
			newItem.addGodStone(sourceItem.getGodStone().getItemId());
		}

		int enchantLevel = sourceItem.getEnchantLevel() - 5;
		if (enchantLevel >= 20) {
			newItem.setEnchantLevel(enchantLevel);
			newItem.setAmplificationSkill(sourceItem.getAmplificationSkill());
			newItem.setAmplification(true);
		}
		else {
			newItem.setEnchantLevel(enchantLevel);
		}

		if (sourceItem.isSoulBound()) {
			newItem.setSoulBound(true);
		}

		if (sourceItem.getBonusNumber() > 0) {
			newItem.setBonusNumber(sourceItem.getBonusNumber());
			newItem.setRandomStats(sourceItem.getRandomStats());
			newItem.setRandomCount(sourceItem.getRandomCount());
		}
		ItemUpdatePredicate predicate = DEFAULT_UPDATE_PREDICATE;
		predicate.changeItem(newItem);
		inventory.add(newItem, predicate.getAddType());
	}
	
	private static void enchant(Player player, int enchant, Item item) {
		if (isUpgradable(item)) {
			if (item.getEnchantLevel() == enchant) {
				return;
			} if (enchant > 25) {
				enchant = 25;
			} if (enchant < 0) {
				enchant = 0;
			}
			item.setEnchantLevel(enchant);
			if (item.isEquipped()) {
				player.getGameStats().updateStatsVisually();
			}
			ItemPacketService.updateItemAfterInfoChange(player, item);
		}
	}
	
	public static boolean isUpgradable(Item item) {
		if (item.getItemTemplate().isNoEnchant()) {
			return false;
		} if (item.getItemTemplate().isWeapon()) {
			return true;
		} if (item.getItemTemplate().isArmor()) {
			int at = item.getItemTemplate().getItemSlot();
			if (at == 1 || /* Main Hand */
			    at == 2 || /* Sub Hand */
			    at == 8 || /* Jacket */
			    at == 16 || /* Gloves */
			    at == 32 || /* Boots */
			    at == 2048 || /* Shoulder */
			    at == 4096 || /* Pants */
				at == 32768 || /* Wing */
			    at == 131072 || /* Main Off Hand */
			    at == 262144) { /* Sub Off Hand */
				return true;
			}
		}
		return false;
	}
}