/*
 * This file is part of Encom. **ENCOM FUCK OTHER SVN**
 *
 *  Encom is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Encom is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser Public License
 *  along with Encom.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.gameserver.model.templates.item.actions;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.configs.main.EnchantsConfig;
import com.aionemu.gameserver.controllers.observer.ItemUseObserver;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.TaskId;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.PersistentState;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.stats.container.StatEnum;
import com.aionemu.gameserver.model.templates.item.ItemCategory;
import com.aionemu.gameserver.network.aion.serverpackets.SM_INVENTORY_UPDATE_ITEM;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ITEM_USAGE_ANIMATION;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_UPDATE_PLAYER_APPEARANCE;
import com.aionemu.gameserver.services.dreamergames.model.TemperingRateLevelTemplates;
import com.aionemu.gameserver.services.dreamergames.model.TemperingRateTemplates;
import com.aionemu.gameserver.services.item.ItemPacketService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.utils.audit.AuditLogger;

/****/
/** Author Ranastic (Encom)
/****/

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name= "TemperingAction")
public class TemperingAction extends AbstractItemAction
{
	
	private static int destroyItem(Item item) {
		if (!EnchantsConfig.DESTROY_ITEM_WHEN_FAIL_TEMPER) {
			return 2;
		}
		return item.getItemTemplate().getAuthorizedType();
	}
	
	@Override
	public boolean canAct(Player player, Item parentItem, Item targetItem) {
		if (targetItem.getItemTemplate().getMaxAuthorize() == 0) {
			PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1402157, targetItem.getName()));
			return false;
		}
		if (targetItem.getAuthorize() >= targetItem.getItemTemplate().getMaxAuthorize()) {
			PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1402153, targetItem.getName()));
			return false;
		}
		
		if (targetItem.getItemTemplate().getMaxEnchantLevel() != 0) {
			PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1402156, targetItem.getName()));
			AuditLogger.info(player, "Player trying temper non termperable equipment.");
			return false;
		}
		
		if (targetItem.getAuthorize() >= EnchantsConfig.MAX_CAP_AUTHORIZED_PLUME && targetItem.getItemTemplate().isPlume() && !targetItem.getItemTemplate().isAccessory()) {
			PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1402155, targetItem.getName()));
			return false;
	    }
		if (targetItem.getAuthorize() >= EnchantsConfig.MAX_CAP_AUTHORIZED_ARMOR && targetItem.getItemTemplate().isArmor() && !targetItem.getItemTemplate().isPlume() && !targetItem.getItemTemplate().isWing() && !targetItem.getItemTemplate().isAccessory() && !targetItem.getItemTemplate().isBracelet()) {
			PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1402155, targetItem.getName()));
			return false;
		}
		if (targetItem.getAuthorize() >= EnchantsConfig.MAX_CAP_AUTHORIZED_WEAPON && targetItem.getItemTemplate().isWeapon()) {
			PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1402155, targetItem.getName()));
			return false;
		}
		if (targetItem.getAuthorize() >= EnchantsConfig.MAX_CAP_AUTHORIZED_ACCESORIES && targetItem.getItemTemplate().isAccessory() && !targetItem.getItemTemplate().isPlume()) {
			PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1402155, targetItem.getName()));
			return false;
		}
		if (targetItem.getAuthorize() >= EnchantsConfig.MAX_CAP_AUTHORIZED_WING && targetItem.getItemTemplate().isWing()) {
			PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1402155, targetItem.getName()));
			return false;
		}
		
		return true;
	}
	
	@Override
	public void act(final Player player, final Item parentItem, final Item targetItem) {
		if (targetItem.getAuthorize() >= EnchantsConfig.MAX_CAP_AUTHORIZED_PLUME && targetItem.getItemTemplate().isPlume() && !targetItem.getItemTemplate().isAccessory()) {
			PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1402155, targetItem.getName()));
			return;
	    }
		if (targetItem.getAuthorize() >= EnchantsConfig.MAX_CAP_AUTHORIZED_ARMOR && targetItem.getItemTemplate().isArmor() && !targetItem.getItemTemplate().isPlume() && !targetItem.getItemTemplate().isWing() && !targetItem.getItemTemplate().isAccessory() && !targetItem.getItemTemplate().isBracelet()) {
			PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1402155, targetItem.getName()));
			return;
		}
		if (targetItem.getAuthorize() >= EnchantsConfig.MAX_CAP_AUTHORIZED_WEAPON && targetItem.getItemTemplate().isWeapon()) {
			PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1402155, targetItem.getName()));
			return;
		}
		if (targetItem.getAuthorize() >= EnchantsConfig.MAX_CAP_AUTHORIZED_ACCESORIES && targetItem.getItemTemplate().isAccessory() && !targetItem.getItemTemplate().isPlume()) {
			PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1402155, targetItem.getName()));
			return;
		}
		if (targetItem.getAuthorize() >= EnchantsConfig.MAX_CAP_AUTHORIZED_WING && targetItem.getItemTemplate().isWing()) {
			PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1402155, targetItem.getName()));
			return;
		}
		
		PacketSendUtility.broadcastPacketAndReceive(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(), targetItem.getObjectId(), parentItem.getObjectId(), parentItem.getItemId(), 5000, 0, 0));

		final ItemUseObserver observer = new ItemUseObserver() {

			@Override
			public void abort() {
				player.getController().cancelTask(TaskId.ITEM_USE);
				player.getObserveController().removeObserver(this);
				PacketSendUtility.sendPacket(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(), parentItem.getObjectId(), parentItem.getItemTemplate().getTemplateId(), 0, 3, 0));
				ItemPacketService.updateItemAfterInfoChange(player, targetItem);
				PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_ITEM_AUTHORIZE_CANCEL(targetItem.getNameId()));
			}
		};
		player.getObserveController().attach(observer);
		player.getController().addTask(TaskId.ITEM_USE, ThreadPoolManager.getInstance().schedule(new Runnable() {

			@Override
			public void run() {
				if (player.getInventory().decreaseByItemId(parentItem.getItemId(), 1)) {
					if (!isSuccess(player, targetItem, targetItem.getAuthorize())) {		
						PacketSendUtility.broadcastPacketAndReceive(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(), player.getObjectId(), parentItem.getObjectId(), parentItem.getItemId(), 0, 2, 0));
						switch (destroyItem(targetItem)) {
						case 1: 
							if (targetItem.isEquipped()) {
								player.getEquipment().unEquipItem(targetItem.getObjectId(), player.getEquipment().getEquippedItemByObjId(targetItem.getObjectId()).getEquipmentSlot());
								player.getInventory().decreaseByObjectId(targetItem.getObjectId(), targetItem.getItemCount());
							} else {
								player.getInventory().decreaseByObjectId(targetItem.getObjectId(), targetItem.getItemCount());
							}
							PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1402447, targetItem.getName()));
							break;
						case 2: 
							targetItem.setAuthorize(0);
							PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1402149, targetItem.getName()));
							break;
						case 3: 
							PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1403386, targetItem.getName()));
						}
					} else {
						targetItem.setAuthorize(targetItem.getAuthorize() + 1);
			            PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_ITEM_AUTHORIZE_SUCCEEDED(targetItem.getNameId(), targetItem.getAuthorize()));
			            PacketSendUtility.broadcastPacketAndReceive(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(), player.getObjectId(), parentItem.getObjectId(), parentItem.getItemId(), 0, 1, 0));
					}
					
					PacketSendUtility.broadcastPacket(player, new SM_UPDATE_PLAYER_APPEARANCE(player.getObjectId(), player.getEquipment().getEquippedItemsWithoutStigma()), false);
					PacketSendUtility.sendPacket(player, new SM_INVENTORY_UPDATE_ITEM(player, targetItem));
			          
					player.getObserveController().removeObserver(observer);
					
					if (targetItem.isEquipped()) {
						player.getGameStats().updateStatsVisually();
					}
					
					ItemPacketService.updateItemAfterInfoChange(player, targetItem);
					
					if (targetItem.isEquipped()) {
						player.getEquipment().setPersistentState(PersistentState.UPDATE_REQUIRED);
						PacketSendUtility.broadcastPacket(player, new SM_UPDATE_PLAYER_APPEARANCE(player.getObjectId(), player.getEquipment().getEquippedForApparence(), false), true);
					} else {
						player.getInventory().setPersistentState(PersistentState.UPDATE_REQUIRED);
					}
				}
			}
		}, 5000));
	}
	
	public boolean isSuccess(Player player, Item targetItem, int authLevel) {
		float temperingBoost = player.getGameStats().getStat(StatEnum.AUTHORIZE_BOOST, 0).getCurrent();
		
		int chanceTempalte = 0;
	    
		boolean success = false;
		TemperingRateTemplates trt = DataManager.TEMPERING_RATE.getCategory(targetItem.getItemTemplate().getCategory().getId());
		if (trt == null) {
			trt = DataManager.TEMPERING_RATE.getCategory(ItemCategory.ALL.getId());
		}
		
		for (TemperingRateLevelTemplates trlt : trt.getTemperingLevel()) {
			if (authLevel >= trlt.getMin() && authLevel <= trlt.getMax()) {
				chanceTempalte = trlt.getChance();
				break;
			}
		}
	  
		float chance = chanceTempalte + temperingBoost;
		
		if (chance > 100) {
			chance = 100;
		}
		
		if (EnchantsConfig.TEMPERANCE_ALWAYS_SUCCESS) {
			return true;
		}
		
		float unlucky = (float)(Math.random() * 100 + 1);
		
		if (unlucky <= chance) {
	    	success = true;
	    }
		
		if (player.getAccessLevel() > 2) {
			PacketSendUtility.sendMessage(player, "Chance: " + chanceTempalte + "% Tempering Boost: " + temperingBoost + "% Total Chance: " + chance + "% Luck: " + unlucky + " Result: " + success);
		}
		
	    return success;
	}
	
	@SuppressWarnings("unused")
	private float calcTemperingRate(Player player, Item targetItem, int authLevel) {
		float temperingBoost = player.getGameStats().getStat(StatEnum.AUTHORIZE_BOOST, 0).getCurrent();
		int chanceTempalte = 0;
	    
		TemperingRateTemplates trt = DataManager.TEMPERING_RATE.getCategory(targetItem.getItemTemplate().getCategory().getId());
		if (trt == null) {
			trt = DataManager.TEMPERING_RATE.getCategory(ItemCategory.ALL.getId());
		}
		for (TemperingRateLevelTemplates trlt : trt.getTemperingLevel()) {
	    	if (authLevel >= trlt.getMin() && authLevel <= trlt.getMax()) {
	    		chanceTempalte = trlt.getChance();
	    		break;
	    	}
		}
		
		float chance = chanceTempalte + temperingBoost;
	  
		if (chance > 100.0F) {
			chance = 100.0F;
		}
		return chance;
	}
}