package com.aionemu.gameserver.services.craft;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.utils.Rnd;
import com.aionemu.gameserver.configs.main.LoggingConfig;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.DescriptionId;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.StaticObject;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.RewardType;
import com.aionemu.gameserver.model.templates.item.ItemTemplate;
import com.aionemu.gameserver.model.templates.recipe.Component;
import com.aionemu.gameserver.model.templates.recipe.RecipeTemplate;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.services.item.ItemService;
import com.aionemu.gameserver.services.item.ItemService.ItemUpdatePredicate;
import com.aionemu.gameserver.skillengine.task.AetherForgingTask;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.audit.AuditLogger;

public class AetherForging {
	private static final Logger log = LoggerFactory.getLogger("MAGIC_CRAFT_LOG");
	private static int firstItemUniqueId;

	public static void startForgingCraft(Player player, int recipeId, int craftType) {

		RecipeTemplate recipeTemplate = DataManager.RECIPE_DATA.getRecipeTemplateById(recipeId);
		VisibleObject target = player.getKnownList().getObject(player.getObjectId());
		ItemTemplate itemTemplate = DataManager.ITEM_DATA.getItemTemplate(recipeTemplate.getProductid());

		if (!checkForgingCraft(player, recipeTemplate, itemTemplate)) {
			sendCancelForgingCraft(player);
			return;
		}
		player.setCraftingTask(new AetherForgingTask(player, (StaticObject) target, recipeTemplate));
		player.getCraftingTask().start();
	}

	private static boolean checkForgingCraft(Player player, RecipeTemplate recipeTemplate, ItemTemplate itemTemplate) {

		if (recipeTemplate == null) {
			return false;
		}
		if (itemTemplate == null) {
			return false;
		}
		if (player.getCraftingTask() != null && player.getCraftingTask().isInProgress()) {
			return false;
		}
		return true;
	}

	public static void finishForgingCrafting(final Player player, RecipeTemplate recipetemplate, int critCount, int bonus) {
		int xpReward = ((2 * (recipetemplate.getSkillpoint() + 100) * (recipetemplate.getSkillpoint() + 100) + 60)); // should be more or less Exp ?
		xpReward = xpReward + (xpReward * bonus / 100); // bonus

		if (player.getInventory().getFreeSlots() == 0) {
			sendCancelForgingCraft(player);
			return;
		}

		for (Component component : recipetemplate.getComponent()) {
			Item firstItem = player.getInventory().getItemByObjId(firstItemUniqueId);
	        if (firstItem == null)
	            firstItem = player.getEquipment().getEquippedItemByObjId(firstItemUniqueId);
	            //PacketSendUtility.sendMessage(player, "Item craft jangan di equip gblk, pengen ngebug aja!!");
			if (!player.getInventory().decreaseByItemId(component.getItemid(), component.getQuantity())) {
				AuditLogger.info(player, " tried craft without required items.");
				return;
			}
		}
		int critVal = (Rnd.get(10000));

		int productItemId = (recipetemplate.getComboProductSize() > 0 && critVal > 9800) ? recipetemplate.getComboProduct(1) : recipetemplate.getProductid();

		ItemService.addItem(player, productItemId, recipetemplate.getQuantity(), new ItemUpdatePredicate() {

			@Override
			public boolean changeItem(Item item) {
				if (item.getItemTemplate().isWeapon() || item.getItemTemplate().isArmor()) {
					item.setItemCreator(player.getName());
				}
				return true;
			}
		});

		ItemTemplate itemTemplate = DataManager.ITEM_DATA.getItemTemplate(productItemId);
		if (LoggingConfig.LOG_CRAFT) {
			log.info(((recipetemplate.getComboProductSize() != null && critVal > 9800) ? "[CRAFT][Critical] ID/Count" : "[MAGIC_CRAFT][Normal] Added ID/Count") + (LoggingConfig.ENABLE_ADVANCED_LOGGING ? "/Item Name - " + productItemId + "/" + recipetemplate.getQuantity() + "/" + itemTemplate.getName() : " - " + productItemId + "/" + recipetemplate.getQuantity()) + " to player: " + player.getName());
		}

		int gainedCraftExp = (int) RewardType.CRAFTING.calcReward(player, xpReward);

		if (player.getSkillList().addSkillXp(player, recipetemplate.getSkillid(), gainedCraftExp, recipetemplate.getSkillpoint())) {
			player.getCommonData().addExp(xpReward, RewardType.CRAFTING);
		}
		else {
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_DONT_GET_PRODUCTION_EXP(new DescriptionId(DataManager.SKILL_DATA.getSkillTemplate(recipetemplate.getSkillid()).getNameId())));
		}
	}

	public static void sendCancelForgingCraft(Player player) {
		if (player.getCraftingTask().isInProgress()) {
			player.getCraftingTask().abort();
		}
	}
}
