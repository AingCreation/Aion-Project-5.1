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
package com.aionemu.gameserver.services;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.gameserver.configs.main.MembershipConfig;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Equipment;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.items.ItemSlot;
import com.aionemu.gameserver.model.skill.PlayerSkillEntry;
import com.aionemu.gameserver.model.templates.item.ItemQuality;
import com.aionemu.gameserver.model.templates.item.ItemTemplate;
import com.aionemu.gameserver.model.templates.item.Stigma;
import com.aionemu.gameserver.network.aion.serverpackets.SM_CUBE_UPDATE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_INVENTORY_UPDATE_ITEM;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SKILL_LIST;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.skillengine.model.SkillLearnTemplate;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.audit.AuditLogger;
/**
 * @author KorLightning (Encom)
 */
public class StigmaService {
    private static final Logger log = LoggerFactory.getLogger(StigmaService.class);
    private static HashMap<Integer, Long> delays = new HashMap<Integer, Long>();
    
    public static boolean notifyEquipAction(final Player player, Item resultItem, long slot) {
        Stigma stigmaInfo = resultItem.getItemTemplate().getStigma();
        String stigmaSkillName = "";
        int stigmaLevel = 1;
        String stigmaSkillBonusName = "";
        int stigmaLevelBonus = 1;
        int stigmaEnchantLvl = getSetStigmaLevel(player, null);
        boolean StigmaEnchantBonus = slot == ItemSlot.SPECIAL_STIGMA.getSlotIdMask();
        int delay = 10;
        
        if (resultItem.getItemTemplate().isStigma()) {
        	if (delays.containsKey(player.getObjectId())) {
        		long secondLeft = ((delays.get(player.getObjectId()) / 1000) + delay) - (System.currentTimeMillis() / 1000);
        		if (secondLeft > 0) {
        			PacketSendUtility.sendMessage(player, "You can equip stigma AFTER " + secondLeft + "secs");
        			return false;
        		}
        		delays.put(player.getObjectId(), System.currentTimeMillis());
        	} else {
        		if (ItemSlot.isRegularStigma(slot)) {
                    if (getPossibleRegulerStigmaCount(player) <= player.getEquipment().getEquippedItemsRegularStigma().size()) {
                        AuditLogger.info(player, "Possible client hack stigma count big , isRegularStigma :" + getPossibleRegulerStigmaCount(player) + " Stigma Equiped Count: " + player.getEquipment().getEquippedItemsRegularStigma().size());
                        return false;
                    }
                } else if (ItemSlot.isAdvancedStigma(slot)) {
                    if (getPossibleAdvencedStigmaCount(player) <= player.getEquipment().getEquippedItemsAdvencedStigma().size()) {
                        AuditLogger.info(player, "Possible client hack advanced stigma count big isAdvancedStigma: " + getPossibleAdvencedStigmaCount(player) + " Stigma Equiped Count: " + player.getEquipment().getEquippedItemsMajorStigma().size());
                        return false;
                    }
                } else if (ItemSlot.isMajorStigma(slot)) {
                    if (getPossibleMajorStigmaCount(player) <= player.getEquipment().getEquippedItemsMajorStigma().size()) {
                        AuditLogger.info(player, "Possible client hack advanced stigma count big :O");
                        return false;
                    }
                } else if (ItemSlot.isSpecialStigma(slot)) {
                    if (resultItem.getItemTemplate().getItemQuality() != ItemQuality.RARE) {
                        return false;
                    }
                } if (!resultItem.getItemTemplate().isClassSpecific(player.getCommonData().getPlayerClass())) {
                    AuditLogger.info(player, "Possible client hack not valid for class.");
                    return false;
                } if (!isPossibleEquippedStigma(player, resultItem)) {
                    AuditLogger.info(player, "Player tried to get Multiple Stigma's from One Stigma Stone!");
                    return false;
                } if (stigmaInfo == null) {
                    log.warn("Stigma info missing for item: " + resultItem.getItemTemplate().getTemplateId());
                    return false;
                }
                int kinahCount = stigmaInfo.getKinah();
                if (player.getInventory().getKinah() < kinahCount) {
                    AuditLogger.info(player, "Possible client hack kinah count low.");
                    return false;
                } if (!player.getInventory().tryDecreaseKinah(kinahCount)) {
                    log.warn("Kinah player not decreased.");
                    return false;
                }
                
                delays.put(player.getObjectId(), System.currentTimeMillis());
        	}
        	
        	for (int i = 1; i <= player.getLevel(); i++) {
                SkillLearnTemplate[] skillTemplates = DataManager.SKILL_TREE_DATA.getTemplatesFor(player.getPlayerClass(), i, player.getRace());
                for (SkillLearnTemplate skillTree : skillTemplates) {
                    if (stigmaInfo.getSkillGroup1() != null && skillTree.getSkillGroup() != null) {
                        if (stigmaInfo.getSkillGroup1().toLowerCase().equals(skillTree.getSkillGroup().toLowerCase())) {
                            int SkillLevel = skillTree.getSkillLevel() + resultItem.getEnchantLevel();
                            if (StigmaEnchantBonus) {
                                SkillLevel += stigmaEnchantLvl;
                            }
                            player.getSkillList().addStigmaSkill(player, skillTree.getSkillId(), SkillLevel, true);
                            stigmaSkillName = DataManager.SKILL_DATA.getSkillTemplate(skillTree.getSkillId()).getName();
                            stigmaLevel = DataManager.SKILL_DATA.getSkillTemplate(skillTree.getSkillId()).getLvl();
                        }
                    } if (stigmaInfo.getSkillGroup2() != null && skillTree.getSkillGroup() != null) {
                        if (stigmaInfo.getSkillGroup2().toLowerCase().equals(skillTree.getSkillGroup().toLowerCase())) {
                            int SkillLevel = skillTree.getSkillLevel() + resultItem.getEnchantLevel();
                            if (StigmaEnchantBonus) {
                                SkillLevel += stigmaEnchantLvl;
                            }
                            player.getSkillList().addStigmaSkill(player, skillTree.getSkillId(), SkillLevel, true);
                            stigmaSkillBonusName = DataManager.SKILL_DATA.getSkillTemplate(skillTree.getSkillId()).getName();
                            stigmaLevelBonus = DataManager.SKILL_DATA.getSkillTemplate(skillTree.getSkillId()).getLvl();
                        }
                    }
                }
            }
            if (stigmaSkillBonusName != "") {
                PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_STIGMA_YOU_CAN_USE_THIS_SKILL_BY_STIGMA_STONE(stigmaSkillBonusName, String.valueOf(stigmaLevelBonus)));
            }
            PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_STIGMA_YOU_CAN_USE_THIS_SKILL_BY_STIGMA_STONE(stigmaSkillName, String.valueOf(stigmaLevel)));
           
        	
        	if (!ItemSlot.isSpecialStigma(slot)) {
        		List<Integer> sStigma = player.getEquipment().getEquippedItemsAllStigmaIds();
        		List<Item> Stigma = player.getEquipment().getEquippedItemsAllStigma();
        		sStigma.add(Integer.valueOf(resultItem.getItemId()));
        		Stigma.add(resultItem);
        		checkForLinkStigmaAvailable(player, sStigma, Stigma, false);
        		StigmaSetBonus(player, Stigma, true);
        	}
        }
        return true;
    }
    public static boolean notifyUnequipAction(Player player, Item resultItem) {
        if (resultItem.getItemTemplate().isStigma()) {
            Stigma stigmaInfo = resultItem.getItemTemplate().getStigma();
            int itemId = resultItem.getItemId();
            String linkedSkillName = "";
            int linkedSkillLevel = 1;
            String stigmaSkillName = "";
            String stigmaSkillBonusName = "";
            Equipment equipment = player.getEquipment();
            
            if (itemId == 140000007 || itemId == 140000005) {
                if (equipment.hasDualWeaponEquipped(ItemSlot.LEFT_HAND)) {
                    PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_STIGMA_CANNT_UNEQUIP_STONE_FIRST_UNEQUIP_CURRENT_EQUIPPED_ITEM);
                    return false;
                }
            } for (Item item: player.getEquipment().getEquippedItemsAllStigma()) {
                Stigma si = item.getItemTemplate().getStigma();
                if (resultItem == item || si == null) {
                    continue;
                }
            }
            PacketSendUtility.sendPacket(player, SM_CUBE_UPDATE.stigmaSlots(player.getCommonData().getAdvencedStigmaSlotSize()));
            PacketSendUtility.sendPacket(player, new SM_INVENTORY_UPDATE_ITEM(player, resultItem));
            for (int i = 1; i <= player.getLevel(); i++) {
                SkillLearnTemplate[] skillTemplates = DataManager.SKILL_TREE_DATA.getTemplatesFor(player.getPlayerClass(), i, player.getRace());
                for (SkillLearnTemplate skillTree : skillTemplates) {
                    if (stigmaInfo.getSkillGroup1() != null && skillTree.getSkillGroup() != null) {
                        if (stigmaInfo.getSkillGroup1().toLowerCase().equals(skillTree.getSkillGroup().toLowerCase())) {
                            stigmaSkillName = DataManager.SKILL_DATA.getSkillTemplate(skillTree.getSkillId()).getName();
                            SkillLearnService.removeSkill(player, skillTree.getSkillId());
                            player.getEffectController().removeEffect(skillTree.getSkillId());
                        }
                    }
                    if (stigmaInfo.getSkillGroup2() != null && skillTree.getSkillGroup() != null) {
                        if (stigmaInfo.getSkillGroup2().toLowerCase().equals(skillTree.getSkillGroup().toLowerCase())) {
                            stigmaSkillBonusName = DataManager.SKILL_DATA.getSkillTemplate(skillTree.getSkillId()).getName();
                            SkillLearnService.removeSkill(player, skillTree.getSkillId());
                            player.getEffectController().removeEffect(skillTree.getSkillId());
                        }
                    }
                }
            } if (stigmaSkillBonusName != "") {
                PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1300403, stigmaSkillBonusName));
            }
            PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1300403, stigmaSkillName));
            List<Integer> equippedStigmaId = player.getEquipment().getEquippedItemsAllStigmaIds();
            List<Item> equippedStigma = player.getEquipment().getEquippedItemsAllStigma();
            equippedStigmaId.remove(Integer.valueOf(resultItem.getItemId()));
            equippedStigma.remove(resultItem);
            if (player.getLinkedSkill() > 0 && !ItemSlot.isSpecialStigma(resultItem.getEquipmentSlot())) {
                int linkedLevel = getLinkStigmaLevel(player);
                String skillGroupName = DataManager.SKILL_DATA.getSkillTemplate(player.getLinkedSkill()).getSkillGroup();
                for (int i = 1; i <= player.getLevel(); i++) {
                    SkillLearnTemplate[] skillTemplates = DataManager.SKILL_TREE_DATA.getTemplatesFor(player.getPlayerClass(), i, player.getRace());
                    for (SkillLearnTemplate skillTree : skillTemplates) {
                        if (skillTree.getSkillGroup() != null) {
                            if (skillTree.getSkillGroup().contains(skillGroupName)) {
                                linkedSkillName = DataManager.SKILL_DATA.getSkillTemplate(skillTree.getSkillId()).getName();
                                linkedSkillLevel = DataManager.SKILL_DATA.getSkillTemplate(skillTree.getSkillId()).getLvl();
                                SkillLearnService.removeLinkedSkill(player, skillTree.getSkillId(), linkedLevel + 1);
                                player.setLinkedSkill(0);
                            }
                        }
                    }
                    PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_STIGMA_DELETE_HIDDEN_SKILL(linkedSkillLevel, linkedSkillName));
                } if (resultItem.getEquipmentSlot() != ItemSlot.SPECIAL_STIGMA.getSlotIdMask()) {
                    if (!equipment.getEquippedItemsSpecialStigma().isEmpty()) {
                        Item item = (Item)player.getEquipment().getEquippedItemsSpecialStigma().get(0);
                        if (item != null) {
                            player.getEquipment().unEquipItem(item.getObjectId().intValue(), item.getEquipmentSlot());
                            PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1404453, new Object[0]));
                        }
                    }
                    List<Item> Stigma = player.getEquipment().getEquippedItemsAllStigma();
                    StigmaSetBonus(player, Stigma, false);
                }
            }
        }
        return true;
    }
    public static void onPlayerLogin(Player player) {
        List<Item> equippedItems = player.getEquipment().getEquippedItemsAllStigma();
        int BonusLevel = getSetStigmaLevel(player, null);
        for (Item item : equippedItems) {
            if (item.getItemTemplate().isStigma()) {
                if (!isPossibleEquippedStigma(player, item)) {
                    AuditLogger.info(player, "Possible client hack stigma count big :O");
                    player.getEquipment().unEquipItem(item.getObjectId().intValue(), 0);
                } else {
                    Stigma stigmaInfo = item.getItemTemplate().getStigma();
                    if (stigmaInfo == null) {
                        log.warn("Stigma info missing for item: " + item.getItemTemplate().getTemplateId());
                        player.getEquipment().unEquipItem(item.getObjectId().intValue(), 0);
                    } else if (!item.getItemTemplate().isClassSpecific(player.getCommonData().getPlayerClass())) {
                        AuditLogger.info(player, "Possible client hack not valid for class.");
                        player.getEquipment().unEquipItem(item.getObjectId().intValue(), 0);
                    }
                }
            }
        } for (Item item : equippedItems) {
            if (item.getItemTemplate().isStigma()) {
                Stigma stigmaInfo = item.getItemTemplate().getStigma();
                if (stigmaInfo == null) {
                    log.warn("Stigma info missing for item: " + item.getItemTemplate().getTemplateId());
                    return;
                } for (int i = 1; i <= player.getLevel(); i++) {
                    SkillLearnTemplate[] skillTemplates = DataManager.SKILL_TREE_DATA.getTemplatesFor(player.getPlayerClass(), i, player.getRace());
                    for (SkillLearnTemplate skillTree : skillTemplates) {
                        if (stigmaInfo.getSkillGroup1() != null && skillTree.getSkillGroup() != null) {
                            if (stigmaInfo.getSkillGroup1().toLowerCase().equals(skillTree.getSkillGroup().toLowerCase())) {
                                int SkillLevel = skillTree.getSkillLevel() + item.getEnchantLevel() + BonusLevel;
                                player.getSkillList().addStigmaSkill(player, skillTree.getSkillId(), SkillLevel, false);
                            }
                        } if (stigmaInfo.getSkillGroup2() != null && skillTree.getSkillGroup() != null) {
                            if (stigmaInfo.getSkillGroup2().toLowerCase().equals(skillTree.getSkillGroup().toLowerCase())) {
                                int SkillLevel = skillTree.getSkillLevel() + item.getEnchantLevel() + BonusLevel;
                                player.getSkillList().addStigmaSkill(player, skillTree.getSkillId(), SkillLevel, false);
                            }
                        }
                    }
                }
            }
        }
        /** Stigma Linked Skills **/
        List<Integer> sStigma = player.getEquipment().getEquippedItemsAllStigmaIds();
        List<Item> StigmaItems = player.getEquipment().getEquippedItemsAllStigma();
        checkForLinkStigmaAvailable(player, sStigma, StigmaItems, true);
    }
    private static int getPossibleRegulerStigmaCount(Player player) {
        if (player == null || player.getLevel() < 20) {
            return 0;
        }
        boolean isCompleteQuest = false;
        if (player.getRace() == Race.ELYOS) {
            isCompleteQuest = player.isCompleteQuest(1929) ||
            player.getQuestStateList().getQuestState(1929).getStatus() == QuestStatus.START &&
            player.getQuestStateList().getQuestState(1929).getQuestVars().getQuestVars() == 98;
        } else {
            isCompleteQuest = player.isCompleteQuest(2900) ||
            player.getQuestStateList().getQuestState(2900).getStatus() == QuestStatus.START &&
            player.getQuestStateList().getQuestState(2900).getQuestVars().getQuestVars() == 99;
        }
        int playerLevel = player.getLevel();
        if (player.havePermission(MembershipConfig.STIGMA_SLOT_QUEST)) {
            isCompleteQuest = true;
        }
        if (isCompleteQuest) {
            if (playerLevel <= 20) {
                return 1;
            } if (playerLevel <= 30) {
                return 2;
            } if (playerLevel >= 40) {
                return 3;
            }
            return 3;
        }
        return 0;
    }
    private static int getPossibleAdvencedStigmaCount(Player player) {
        if (player == null || player.getLevel() < 45) {
            return 0;
        }
        boolean isCompleteQuest = false;
        if (player.getRace() == Race.ELYOS) {
          isCompleteQuest = player.isCompleteQuest(1929) ||
          player.getQuestStateList().getQuestState(1929).getStatus() == QuestStatus.START &&
          player.getQuestStateList().getQuestState(1929).getQuestVars().getQuestVars() == 98;
        } else {
          isCompleteQuest = player.isCompleteQuest(2900) ||
          player.getQuestStateList().getQuestState(2900).getStatus() == QuestStatus.START &&
          player.getQuestStateList().getQuestState(2900).getQuestVars().getQuestVars() == 99;
        }
        int playerLevel = player.getLevel();
        if (player.havePermission(MembershipConfig.STIGMA_SLOT_QUEST)) {
            isCompleteQuest = true;
        }
        if (isCompleteQuest) {
            if (playerLevel <= 45) {
                return 1;
            } if (playerLevel >= 50) {
                return 2;
            }
            return 2;
        }
        return 0;
    }
    private static int getPossibleMajorStigmaCount(Player player) {
        if (player == null || player.getLevel() < 55) {
            return 0;
        }
        boolean isCompleteQuest = false;
        if (player.getRace() == Race.ELYOS) {
            isCompleteQuest = player.isCompleteQuest(1929) ||
            player.getQuestStateList().getQuestState(1929).getStatus() == QuestStatus.START &&
            player.getQuestStateList().getQuestState(1929).getQuestVars().getQuestVars() == 98;
        } else {
            isCompleteQuest = player.isCompleteQuest(2900) ||
            player.getQuestStateList().getQuestState(2900).getStatus() == QuestStatus.START &&
            player.getQuestStateList().getQuestState(2900).getQuestVars().getQuestVars() == 99;
        }
        int playerLevel = player.getLevel();
        if (player.havePermission(MembershipConfig.STIGMA_SLOT_QUEST)) {
            isCompleteQuest = true;
        } if (isCompleteQuest) {
            if (playerLevel >= 55) {
                 return 1;
            }
        }
        return 0;
      }
    private static boolean isPossibleEquippedStigma(Player player, Item item) {
        if (player == null || item == null || !item.getItemTemplate().isStigma()) {
            return false;
        }
        long itemSlotToEquip = item.getEquipmentSlot();
        if (ItemSlot.isRegularStigma(itemSlotToEquip)) {
            int stigmaCount = getPossibleRegulerStigmaCount(player);
            if (stigmaCount > 0) {
                if (stigmaCount == 1) {
                    if (itemSlotToEquip == ItemSlot.STIGMA1.getSlotIdMask()) {
                        return true;
                    }
                } else if (stigmaCount == 2) {
                    if (itemSlotToEquip == ItemSlot.STIGMA1.getSlotIdMask() || itemSlotToEquip == ItemSlot.STIGMA2.getSlotIdMask()) {
                        return true;
                    }
                } else if (stigmaCount == 3) {
                    if (itemSlotToEquip == ItemSlot.STIGMA1.getSlotIdMask() || itemSlotToEquip == ItemSlot.STIGMA2.getSlotIdMask() || itemSlotToEquip == ItemSlot.STIGMA3.getSlotIdMask()) {
                        return true;
                    }
                }
            }
        } else if (ItemSlot.isAdvancedStigma(itemSlotToEquip)) {
            int advStigmaCount = getPossibleAdvencedStigmaCount(player);
            if (advStigmaCount > 0) {
                if (advStigmaCount == 1) {
                    if (itemSlotToEquip == ItemSlot.ADV_STIGMA1.getSlotIdMask()) {
                        return true;
                    }
                } else if (advStigmaCount == 2) {
                    if (itemSlotToEquip == ItemSlot.ADV_STIGMA1.getSlotIdMask() || itemSlotToEquip == ItemSlot.ADV_STIGMA2.getSlotIdMask()) {
                        return true;
                    }
                }
            }
        } else if (ItemSlot.isMajorStigma(itemSlotToEquip)) {
            int majStigmaCount = getPossibleMajorStigmaCount(player);
            if (majStigmaCount == 1) {
                if (itemSlotToEquip == ItemSlot.MAJ_STIGMA.getSlotIdMask()) {
                    return true;
                }
            }
        } else if (itemSlotToEquip == ItemSlot.SPECIAL_STIGMA.getSlotIdMask()) {
            if (player.getEquipment().getEquippedItemsAllStigmaIds().size() == 6) {
                if (getLinkStigmaLevel(player) >= 9) {
                    return true;
                }
            }
        }
        return false;
    }
    private static void checkForLinkStigmaAvailable(Player player, List<Integer> list, List<Item> Stigma, boolean onlogin) {
        boolean hasInert = false;
        String linkedSkillName = "";
        int linkedSkillLevel = 1;
        for (Integer in : list) {
            ItemTemplate it = DataManager.ITEM_DATA.getItemTemplate(in);
            if (it.getName().contains("(Inert)")) {
                hasInert = true;
            }
        }
        int linkedLevel = 0;
        List<Integer> enchantStigmaList = new ArrayList<Integer>();
        for (Item item : Stigma) {
            if (item.getEquipmentSlot() != ItemSlot.SPECIAL_STIGMA.getSlotIdMask()) {
                enchantStigmaList.add(Integer.valueOf(item.getEnchantLevel()));
            }
        } if (enchantStigmaList.size() > 0) {
            Collections.sort(enchantStigmaList);
            linkedLevel = enchantStigmaList.get(0);
        }
        switch (player.getPlayerClass()) {
        case GLADIATOR:
            if (list.size() >= 6 && !hasInert) {
                if (list.contains(140001119) && list.contains(140001106) && list.contains(140001108)
                        || list.contains(140001119) && list.contains(140001106) && list.contains(140001107)
                        || list.contains(140001119) && list.contains(140001108) && list.contains(140001107)) {
                    for (int i = 1; i <= player.getLevel(); i++) {
                        SkillLearnTemplate[] skillTemplates = DataManager.SKILL_TREE_DATA.getTemplatesFor(player.getPlayerClass(), i, player.getRace());
                        for (SkillLearnTemplate skillTree : skillTemplates) {
                            if (skillTree.getSkillGroup() != null) {
                                if (skillTree.getSkillGroup().contains("FI_movewhirl")) {
                                    player.getSkillList().addLinkedSkill(player, skillTree.getSkillId(), linkedLevel + 1);
                                    linkedSkillName = DataManager.SKILL_DATA.getSkillTemplate(skillTree.getSkillId()).getName();
                                    linkedSkillLevel = DataManager.SKILL_DATA.getSkillTemplate(skillTree.getSkillId()).getLvl();
                                }
                            }
                        }
                    } if (!onlogin) {
                        PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_STIGMA_GET_HIDDEN_SKILL(linkedSkillLevel, linkedSkillName));
                    }
                } else if (list.contains(140001118) && list.contains(140001104) && list.contains(140001103)
                        || list.contains(140001118) && list.contains(140001104) && list.contains(140001105)
                        || list.contains(140001118) && list.contains(140001103) && list.contains(140001105)) {
                    for (int i = 1; i <= player.getLevel(); i++) {
                        SkillLearnTemplate[] skillTemplates = DataManager.SKILL_TREE_DATA.getTemplatesFor(player.getPlayerClass(), i, player.getRace());
                        for (SkillLearnTemplate skillTree : skillTemplates) {
                            if (skillTree.getSkillGroup() != null) {
                                if (skillTree.getSkillGroup().contains("FI_BladeShock")) {
                                    player.getSkillList().addLinkedSkill(player, skillTree.getSkillId(), linkedLevel + 1);
                                    linkedSkillName = DataManager.SKILL_DATA.getSkillTemplate(skillTree.getSkillId()).getName();
                                    linkedSkillLevel = DataManager.SKILL_DATA.getSkillTemplate(skillTree.getSkillId()).getLvl();
                                }
                            }
                        }
                    } if (!onlogin) {
                        PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_STIGMA_GET_HIDDEN_SKILL(linkedSkillLevel, linkedSkillName));
                    }
                } else {
                    for (int i = 1; i <= player.getLevel(); i++) {
                        SkillLearnTemplate[] skillTemplates = DataManager.SKILL_TREE_DATA.getTemplatesFor(player.getPlayerClass(), i, player.getRace());
                        for (SkillLearnTemplate skillTree : skillTemplates) {
                            if (skillTree.getSkillGroup() != null ) {
                                if (skillTree.getSkillGroup().contains("FI_Warflag")) {
                                    player.getSkillList().addLinkedSkill(player, skillTree.getSkillId(), linkedLevel + 1);
                                    linkedSkillName = DataManager.SKILL_DATA.getSkillTemplate(skillTree.getSkillId()).getName();
                                    linkedSkillLevel = DataManager.SKILL_DATA.getSkillTemplate(skillTree.getSkillId()).getLvl();
                                }
                            }
                        }
                    } if (!onlogin) {
                        PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_STIGMA_GET_HIDDEN_SKILL(linkedSkillLevel, linkedSkillName));
                    }
                }
            }
            return;
        case TEMPLAR:
            if (list.size() >= 6 && !hasInert) {
                if (list.contains(140001134) && list.contains(140001122) && list.contains(140001120)
                        || list.contains(140001134) && list.contains(140001122) && list.contains(140001125)
                        || list.contains(140001134) && list.contains(140001120) && list.contains(140001125)) {
                    for (int i = 1; i <= player.getLevel(); i++) {
                        SkillLearnTemplate[] skillTemplates = DataManager.SKILL_TREE_DATA.getTemplatesFor(player.getPlayerClass(), i, player.getRace());
                        for (SkillLearnTemplate skillTree : skillTemplates) {
                            if (skillTree.getSkillGroup() != null) {
                                if (skillTree.getSkillGroup().contains("KN_Stigma_GodPunishment")) {
                                    player.getSkillList().addLinkedSkill(player, skillTree.getSkillId(), linkedLevel + 1);
                                    linkedSkillName = DataManager.SKILL_DATA.getSkillTemplate(skillTree.getSkillId()).getName();
                                    linkedSkillLevel = DataManager.SKILL_DATA.getSkillTemplate(skillTree.getSkillId()).getLvl();
                                }
                            }
                        }
                    } if (!onlogin) {
                        PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_STIGMA_GET_HIDDEN_SKILL(linkedSkillLevel, linkedSkillName));
                    }
                } else if (list.contains(140001135) && list.contains(140001123) && list.contains(140001124)
                        || list.contains(140001135) && list.contains(140001123) && list.contains(140001121)
                        || list.contains(140001135) && list.contains(140001124) && list.contains(140001121)) {
                    for (int i = 1; i <= player.getLevel(); i++) {
                        SkillLearnTemplate[] skillTemplates = DataManager.SKILL_TREE_DATA.getTemplatesFor(player.getPlayerClass(), i, player.getRace());
                        for (SkillLearnTemplate skillTree : skillTemplates) {
                            if (skillTree.getSkillGroup() != null) {
                                if (skillTree.getSkillGroup().contains("KN_IcyShield")) {
                                    player.getSkillList().addLinkedSkill(player, skillTree.getSkillId(), linkedLevel + 1);
                                    linkedSkillName = DataManager.SKILL_DATA.getSkillTemplate(skillTree.getSkillId()).getName();
                                    linkedSkillLevel = DataManager.SKILL_DATA.getSkillTemplate(skillTree.getSkillId()).getLvl();
                                }
                            }
                        }
                    } if (!onlogin) {
                        PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_STIGMA_GET_HIDDEN_SKILL(linkedSkillLevel, linkedSkillName));
                    }
                } else {
                    for (int i = 1; i <= player.getLevel(); i++) {
                        SkillLearnTemplate[] skillTemplates = DataManager.SKILL_TREE_DATA.getTemplatesFor(player.getPlayerClass(), i, player.getRace());
                        for (SkillLearnTemplate skillTree : skillTemplates) {
                            if (skillTree.getSkillGroup() != null) {
                                if (skillTree.getSkillGroup().contains("KN_Hibernation")) {
                                    player.getSkillList().addLinkedSkill(player, skillTree.getSkillId(), linkedLevel + 1);
                                    linkedSkillName = DataManager.SKILL_DATA.getSkillTemplate(skillTree.getSkillId()).getName();
                                    linkedSkillLevel = DataManager.SKILL_DATA.getSkillTemplate(skillTree.getSkillId()).getLvl();
                                }
                            }
                        }
                    } if (!onlogin) {
                        PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_STIGMA_GET_HIDDEN_SKILL(linkedSkillLevel, linkedSkillName));
                    }
                }
            }
            return;
        case ASSASSIN:
            if (list.size() >= 6 && !hasInert) {
                if (list.contains(140001152) && list.contains(140001138) && list.contains(140001139)
                        || list.contains(140001152) && list.contains(140001138) && list.contains(140001141)
                        || list.contains(140001152) && list.contains(140001139) && list.contains(140001141)) {
                    for (int i = 1; i <= player.getLevel(); i++) {
                        SkillLearnTemplate[] skillTemplates = DataManager.SKILL_TREE_DATA.getTemplatesFor(player.getPlayerClass(), i, player.getRace());
                        for (SkillLearnTemplate skillTree : skillTemplates) {
                            if (skillTree.getSkillGroup() != null) {
                                if (skillTree.getSkillGroup().contains("AS_WideNewBlindingBurst")) {
                                    player.getSkillList().addLinkedSkill(player, skillTree.getSkillId(), linkedLevel + 1);
                                    linkedSkillName = DataManager.SKILL_DATA.getSkillTemplate(skillTree.getSkillId()).getName();
                                    linkedSkillLevel = DataManager.SKILL_DATA.getSkillTemplate(skillTree.getSkillId()).getLvl();
                                }
                            }
                        }
                    } if (!onlogin) {
                        PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_STIGMA_GET_HIDDEN_SKILL(linkedSkillLevel, linkedSkillName));
                    }
                } else if (list.contains(140001151) && list.contains(140001136) && list.contains(140001140)
                        || list.contains(140001151) && list.contains(140001136) && list.contains(140001137)
                        || list.contains(140001151) && list.contains(140001140) && list.contains(140001137)) {
                    for (int i = 1; i <= player.getLevel(); i++) {
                        SkillLearnTemplate[] skillTemplates = DataManager.SKILL_TREE_DATA.getTemplatesFor(player.getPlayerClass(), i, player.getRace());
                        for (SkillLearnTemplate skillTree : skillTemplates) {
                            if (skillTree.getSkillGroup() != null) {
                                if (skillTree.getSkillGroup().contains("AS_ReturnAttack")) {
                                    player.getSkillList().addLinkedSkill(player, skillTree.getSkillId(), linkedLevel + 1);
                                    linkedSkillName = DataManager.SKILL_DATA.getSkillTemplate(skillTree.getSkillId()).getName();
                                    linkedSkillLevel = DataManager.SKILL_DATA.getSkillTemplate(skillTree.getSkillId()).getLvl();
                                }
                            }
                        }
                    } if (!onlogin) {
                        PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_STIGMA_GET_HIDDEN_SKILL(linkedSkillLevel, linkedSkillName));
                    }
                } else {
                    for (int i = 1; i <= player.getLevel(); i++) {
                        SkillLearnTemplate[] skillTemplates = DataManager.SKILL_TREE_DATA.getTemplatesFor(player.getPlayerClass(), i, player.getRace());
                        for (SkillLearnTemplate skillTree : skillTemplates) {
                            if (skillTree.getSkillGroup() != null) {
                                if (skillTree.getSkillGroup().contains("AS_ReSignetAssault")) {
                                    player.getSkillList().addLinkedSkill(player, skillTree.getSkillId(), linkedLevel + 1);
                                    linkedSkillName = DataManager.SKILL_DATA.getSkillTemplate(skillTree.getSkillId()).getName();
                                    linkedSkillLevel = DataManager.SKILL_DATA.getSkillTemplate(skillTree.getSkillId()).getLvl();
                                }
                            }
                        }
                    } if (!onlogin) {
                        PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_STIGMA_GET_HIDDEN_SKILL(linkedSkillLevel, linkedSkillName));
                    }
                }
            }
            return;
        case RANGER:
            if (list.size() >= 6 && !hasInert) {
                if (list.contains(140001172) && list.contains(140001155) && list.contains(140001157)
                        || list.contains(140001172) && list.contains(140001155) && list.contains(140001153)
                        || list.contains(140001172) && list.contains(140001157) && list.contains(140001153)) {
                    for (int i = 1; i <= player.getLevel(); i++) {
                        SkillLearnTemplate[] skillTemplates = DataManager.SKILL_TREE_DATA.getTemplatesFor(player.getPlayerClass(), i, player.getRace());
                        for (SkillLearnTemplate skillTree : skillTemplates) {
                            if (skillTree.getSkillGroup() != null) {
                                if (skillTree.getSkillGroup().contains("RA_heavysnipe")) {
                                    player.getSkillList().addLinkedSkill(player, skillTree.getSkillId(), linkedLevel + 1);
                                    linkedSkillName = DataManager.SKILL_DATA.getSkillTemplate(skillTree.getSkillId()).getName();
                                    linkedSkillLevel = DataManager.SKILL_DATA.getSkillTemplate(skillTree.getSkillId()).getLvl();
                                }
                            }
                        }
                    } if (!onlogin) {
                        PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_STIGMA_GET_HIDDEN_SKILL(linkedSkillLevel, linkedSkillName));
                    }
                } else if (list.contains(140001173) && list.contains(140001154) && list.contains(140001158)
                        || list.contains(140001173) && list.contains(140001154) && list.contains(140001156)
                        || list.contains(140001173) && list.contains(140001158) && list.contains(140001156)) {
                    for (int i = 1; i <= player.getLevel(); i++) {
                        SkillLearnTemplate[] skillTemplates = DataManager.SKILL_TREE_DATA.getTemplatesFor(player.getPlayerClass(), i, player.getRace());
                        for (SkillLearnTemplate skillTree : skillTemplates) {
                            if (skillTree.getSkillGroup() != null) {
                                if (skillTree.getSkillGroup().contains("RA_Fasthide")) {
                                    player.getSkillList().addLinkedSkill(player, skillTree.getSkillId(), linkedLevel + 1);
                                    linkedSkillName = DataManager.SKILL_DATA.getSkillTemplate(skillTree.getSkillId()).getName();
                                    linkedSkillLevel = DataManager.SKILL_DATA.getSkillTemplate(skillTree.getSkillId()).getLvl();
                                }
                            }
                        }
                    } if (!onlogin) {
                        PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_STIGMA_GET_HIDDEN_SKILL(linkedSkillLevel, linkedSkillName));
                    }
                } else {
                    for (int i = 1; i <= player.getLevel(); i++) {
                        SkillLearnTemplate[] skillTemplates = DataManager.SKILL_TREE_DATA.getTemplatesFor(player.getPlayerClass(), i, player.getRace());
                        for (SkillLearnTemplate skillTree : skillTemplates) {
                            if (skillTree.getSkillGroup() != null) {
                                if (skillTree.getSkillGroup().contains("RA_ShockTrap")) {
                                    player.getSkillList().addLinkedSkill(player, skillTree.getSkillId(), linkedLevel + 1);
                                    linkedSkillName = DataManager.SKILL_DATA.getSkillTemplate(skillTree.getSkillId()).getName();
                                    linkedSkillLevel = DataManager.SKILL_DATA.getSkillTemplate(skillTree.getSkillId()).getLvl();
                                }
                            }
                        }
                    } if (!onlogin) {
                        PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_STIGMA_GET_HIDDEN_SKILL(linkedSkillLevel, linkedSkillName));
                    }
                }
            }
            return;
        case SORCERER:
            if (list.size() >= 6 && !hasInert) {
                if (list.contains(140001191) && list.contains(140001174) && list.contains(140001181)
                        || list.contains(140001191) && list.contains(140001174) && list.contains(140001178)
                        || list.contains(140001191) && list.contains(140001181) && list.contains(140001178)) {
                    for (int i = 1; i <= player.getLevel(); i++) {
                        SkillLearnTemplate[] skillTemplates = DataManager.SKILL_TREE_DATA.getTemplatesFor(player.getPlayerClass(), i, player.getRace());
                        for (SkillLearnTemplate skillTree : skillTemplates) {
                            if (skillTree.getSkillGroup() != null) {
                                if (skillTree.getSkillGroup().contains("Wi_windsleep")) {
                                    player.getSkillList().addLinkedSkill(player, skillTree.getSkillId(), linkedLevel + 1);
                                }
                            }
                        }
                    }
                } else if (list.contains(140001192) && list.contains(140001176) && list.contains(140001177)
                        || list.contains(140001192) && list.contains(140001176) && list.contains(140001184)
                        || list.contains(140001192) && list.contains(140001177) && list.contains(140001184)) {
                    for (int i = 1; i <= player.getLevel(); i++) {
                        SkillLearnTemplate[] skillTemplates = DataManager.SKILL_TREE_DATA.getTemplatesFor(player.getPlayerClass(), i, player.getRace());
                        for (SkillLearnTemplate skillTree : skillTemplates) {
                            if (skillTree.getSkillGroup() != null) {
                                if (skillTree.getSkillGroup().contains("Wi_FireShield")) {
                                    player.getSkillList().addLinkedSkill(player, skillTree.getSkillId(), linkedLevel + 1);
                                    linkedSkillName = DataManager.SKILL_DATA.getSkillTemplate(skillTree.getSkillId()).getName();
                                    linkedSkillLevel = DataManager.SKILL_DATA.getSkillTemplate(skillTree.getSkillId()).getLvl();
                                }
                            }
                        }
                    } if (!onlogin) {
                        PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_STIGMA_GET_HIDDEN_SKILL(linkedSkillLevel, linkedSkillName));
                    }
                } else {
                    for (int i = 1; i <= player.getLevel(); i++) {
                        SkillLearnTemplate[] skillTemplates = DataManager.SKILL_TREE_DATA.getTemplatesFor(player.getPlayerClass(), i, player.getRace());
                        for (SkillLearnTemplate skillTree : skillTemplates) {
                            if (skillTree.getSkillGroup() != null) {
                                if (skillTree.getSkillGroup().contains("WI_stoneBarrier")) {
                                    player.getSkillList().addLinkedSkill(player, skillTree.getSkillId(), linkedLevel + 1);
                                    linkedSkillName = DataManager.SKILL_DATA.getSkillTemplate(skillTree.getSkillId()).getName();
                                    linkedSkillLevel = DataManager.SKILL_DATA.getSkillTemplate(skillTree.getSkillId()).getLvl();
                                }
                            }
                        }
                    } if (!onlogin) {
                        PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_STIGMA_GET_HIDDEN_SKILL(linkedSkillLevel, linkedSkillName));
                    }
                }
            }
            return;
        case SPIRIT_MASTER:
            if (list.size() >= 6 && !hasInert) {
                if (list.contains(140001209) && list.contains(140001195) && list.contains(140001193)
                        || list.contains(140001209) && list.contains(140001195) && list.contains(140001194)
                        || list.contains(140001209) && list.contains(140001193) && list.contains(140001194)) {
                    for (int i = 1; i <= player.getLevel(); i++) {
                        SkillLearnTemplate[] skillTemplates = DataManager.SKILL_TREE_DATA.getTemplatesFor(player.getPlayerClass(), i, player.getRace());
                        for (SkillLearnTemplate skillTree : skillTemplates) {
                            if (skillTree.getSkillGroup() != null) {
                                if (skillTree.getSkillGroup().contains("El_SoulKing")) {
                                    player.getSkillList().addLinkedSkill(player, skillTree.getSkillId(), linkedLevel + 1);
                                    linkedSkillName = DataManager.SKILL_DATA.getSkillTemplate(skillTree.getSkillId()).getName();
                                    linkedSkillLevel = DataManager.SKILL_DATA.getSkillTemplate(skillTree.getSkillId()).getLvl();
                                }
                            }
                        }
                    } if (!onlogin) {
                        PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_STIGMA_GET_HIDDEN_SKILL(linkedSkillLevel, linkedSkillName));
                    }
                } else if (list.contains(140001210) && list.contains(140001199) && list.contains(140001158)
                        || list.contains(140001210) && list.contains(140001199) && list.contains(140001197)
                        || list.contains(140001210) && list.contains(140001158) && list.contains(140001197)
                        || list.contains(140001210) && list.contains(140001199) && list.contains(140001198)
                        || list.contains(140001210) && list.contains(140001158) && list.contains(140001198)) {
                    for (int i = 1; i <= player.getLevel(); i++) {
                        SkillLearnTemplate[] skillTemplates = DataManager.SKILL_TREE_DATA.getTemplatesFor(player.getPlayerClass(), i, player.getRace());
                        for (SkillLearnTemplate skillTree : skillTemplates) {
                            if (skillTree.getSkillGroup() != null) {
                                if (skillTree.getSkillGroup().contains("EL_SoulProtect")) {
                                    player.getSkillList().addLinkedSkill(player, skillTree.getSkillId(), linkedLevel + 1);
                                    linkedSkillName = DataManager.SKILL_DATA.getSkillTemplate(skillTree.getSkillId()).getName();
                                    linkedSkillLevel = DataManager.SKILL_DATA.getSkillTemplate(skillTree.getSkillId()).getLvl();
                                }
                            }
                        }
                    } if (!onlogin) {
                        PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_STIGMA_GET_HIDDEN_SKILL(linkedSkillLevel, linkedSkillName));
                    }
                } else {
                    for (int i = 1; i <= player.getLevel(); i++) {
                        SkillLearnTemplate[] skillTemplates = DataManager.SKILL_TREE_DATA.getTemplatesFor(player.getPlayerClass(), i, player.getRace());
                        for (SkillLearnTemplate skillTree : skillTemplates) {
                            if (skillTree.getSkillGroup() != null) {
                                if (skillTree.getSkillGroup().contains("EL_EnergySink")) {
                                    player.getSkillList().addLinkedSkill(player, skillTree.getSkillId(), linkedLevel + 1);
                                    linkedSkillName = DataManager.SKILL_DATA.getSkillTemplate(skillTree.getSkillId()).getName();
                                    linkedSkillLevel = DataManager.SKILL_DATA.getSkillTemplate(skillTree.getSkillId()).getLvl();
                                }
                            }
                        }
                    } if (!onlogin) {
                        PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_STIGMA_GET_HIDDEN_SKILL(linkedSkillLevel, linkedSkillName));
                    }
                }
            }
            return;
        case CLERIC:
            if (list.size() >= 6 && !hasInert) {
                if (list.contains(140001246) && list.contains(140001234) && list.contains(140001232)
                        || list.contains(140001246) && list.contains(140001234) && list.contains(140001233)
                        || list.contains(140001246) && list.contains(140001232) && list.contains(140001233)) {
                    for (int i = 1; i <= player.getLevel(); i++) {
                        SkillLearnTemplate[] skillTemplates = DataManager.SKILL_TREE_DATA.getTemplatesFor(player.getPlayerClass(), i, player.getRace());
                        for (SkillLearnTemplate skillTree : skillTemplates) {
                            if (skillTree.getSkillGroup() != null) {
                                if (skillTree.getSkillGroup().contains("PR_MassRestoreLife")) {
                                    player.getSkillList().addLinkedSkill(player, skillTree.getSkillId(), linkedLevel + 1);
                                    linkedSkillName = DataManager.SKILL_DATA.getSkillTemplate(skillTree.getSkillId()).getName();
                                    linkedSkillLevel = DataManager.SKILL_DATA.getSkillTemplate(skillTree.getSkillId()).getLvl();
                                }
                            }
                        }
                    } if (!onlogin) {
                        PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_STIGMA_GET_HIDDEN_SKILL(linkedSkillLevel, linkedSkillName));
                    }
                } else if (list.contains(140001245) && list.contains(140001229) && list.contains(140001228)
                        || list.contains(140001245) && list.contains(140001229) && list.contains(140001230)
                        || list.contains(140001245) && list.contains(140001228) && list.contains(140001230)
                        || list.contains(140001245) && list.contains(140001229) && list.contains(140001231)
                        || list.contains(140001245) && list.contains(140001228) && list.contains(140001230)) {
                    for (int i = 1; i <= player.getLevel(); i++) {
                        SkillLearnTemplate[] skillTemplates = DataManager.SKILL_TREE_DATA.getTemplatesFor(player.getPlayerClass(), i, player.getRace());
                        for (SkillLearnTemplate skillTree : skillTemplates) {
                            if (skillTree.getSkillGroup() != null) {
                                if (skillTree.getSkillGroup().contains("PR_PunishingLight")) {
                                    player.getSkillList().addLinkedSkill(player, skillTree.getSkillId(), linkedLevel + 1);
                                    linkedSkillName = DataManager.SKILL_DATA.getSkillTemplate(skillTree.getSkillId()).getName();
                                    linkedSkillLevel = DataManager.SKILL_DATA.getSkillTemplate(skillTree.getSkillId()).getLvl();
                                }
                            }
                        }
                    } if (!onlogin) {
                        PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_STIGMA_GET_HIDDEN_SKILL(linkedSkillLevel, linkedSkillName));
                    }
                } else {
                    for (int i = 1; i <= player.getLevel(); i++) {
                        SkillLearnTemplate[] skillTemplates = DataManager.SKILL_TREE_DATA.getTemplatesFor(player.getPlayerClass(), i, player.getRace());
                        for (SkillLearnTemplate skillTree : skillTemplates) {
                            if (skillTree.getSkillGroup() != null && skillTree.getSkillGroup().contains("PR_ProvokeServent")) {
                                player.getSkillList().addLinkedSkill(player, skillTree.getSkillId(), linkedLevel + 1);
                                linkedSkillName = DataManager.SKILL_DATA.getSkillTemplate(skillTree.getSkillId()).getName();
                                linkedSkillLevel = DataManager.SKILL_DATA.getSkillTemplate(skillTree.getSkillId()).getLvl();
                            }
                        }
                    } if (!onlogin) {
                        PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_STIGMA_GET_HIDDEN_SKILL(linkedSkillLevel, linkedSkillName));
                    }
                }
            }
            return;
        case CHANTER:
            if (list.size() >= 6 && !hasInert) {
                if (list.contains(140001226) && list.contains(140001212) && list.contains(140001213)
                        || list.contains(140001226) && list.contains(140001212) && list.contains(140001211)
                        || list.contains(140001226) && list.contains(140001213) && list.contains(140001211)) {
                    for (int i = 1; i <= player.getLevel(); i++) {
                        SkillLearnTemplate[] skillTemplates = DataManager.SKILL_TREE_DATA.getTemplatesFor(player.getPlayerClass(), i, player.getRace());
                        for (SkillLearnTemplate skillTree : skillTemplates) {
                            if (skillTree.getSkillGroup() != null) {
                                if (skillTree.getSkillGroup().contains("CH_PhysicalEnhancement")) {
                                    player.getSkillList().addLinkedSkill(player, skillTree.getSkillId(), linkedLevel + 1);
                                    linkedSkillName = DataManager.SKILL_DATA.getSkillTemplate(skillTree.getSkillId()).getName();
                                    linkedSkillLevel = DataManager.SKILL_DATA.getSkillTemplate(skillTree.getSkillId()).getLvl();
                                }
                            }
                        }
                    } if (!onlogin) {
                        PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_STIGMA_GET_HIDDEN_SKILL(linkedSkillLevel, linkedSkillName));
                    }
                } else if (list.contains(140001227) && list.contains(140001214) && list.contains(140001216)
                        || list.contains(140001227) && list.contains(140001214) && list.contains(140001215)
                        || list.contains(140001227) && list.contains(140001216) && list.contains(140001215)) {
                    for (int i = 1; i <= player.getLevel(); i++) {
                        SkillLearnTemplate[] skillTemplates = DataManager.SKILL_TREE_DATA.getTemplatesFor(player.getPlayerClass(), i, player.getRace());
                        for (SkillLearnTemplate skillTree : skillTemplates) {
                            if (skillTree.getSkillGroup() != null) {
                                if (skillTree.getSkillGroup().contains("CH_Tornado")) {
                                    player.getSkillList().addLinkedSkill(player, skillTree.getSkillId(), linkedLevel + 1);
                                    linkedSkillName = DataManager.SKILL_DATA.getSkillTemplate(skillTree.getSkillId()).getName();
                                    linkedSkillLevel = DataManager.SKILL_DATA.getSkillTemplate(skillTree.getSkillId()).getLvl();
                                }
                            }
                        }
                    } if (!onlogin) {
                        PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_STIGMA_GET_HIDDEN_SKILL(linkedSkillLevel, linkedSkillName));
                    }
                } else {
                    for (int i = 1; i <= player.getLevel(); i++) {
                        SkillLearnTemplate[] skillTemplates = DataManager.SKILL_TREE_DATA.getTemplatesFor(player.getPlayerClass(), i, player.getRace());
                        for (SkillLearnTemplate skillTree : skillTemplates) {
                            if (skillTree.getSkillGroup() != null) {
                                if (skillTree.getSkillGroup().contains("CH_Brand")) {
                                    player.getSkillList().addLinkedSkill(player, skillTree.getSkillId(), linkedLevel + 1);
                                    linkedSkillName = DataManager.SKILL_DATA.getSkillTemplate(skillTree.getSkillId()).getName();
                                    linkedSkillLevel = DataManager.SKILL_DATA.getSkillTemplate(skillTree.getSkillId()).getLvl();
                                }
                            }
                        }
                    } if (!onlogin) {
                        PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_STIGMA_GET_HIDDEN_SKILL(linkedSkillLevel, linkedSkillName));
                    }
                }
            }
            return;
        case AETHERTECH:
            if (list.size() >= 6 && !hasInert) {
                if (list.contains(140001279) && list.contains(140001264) && list.contains(140001269)
                        || list.contains(140001279) && list.contains(140001264) && list.contains(140001265)
                        || list.contains(140001279) && list.contains(140001269) && list.contains(140001265)) {
                    for (int i = 1; i <= player.getLevel(); i++) {
                        SkillLearnTemplate[] skillTemplates = DataManager.SKILL_TREE_DATA.getTemplatesFor(player.getPlayerClass(), i, player.getRace());
                        for (SkillLearnTemplate skillTree : skillTemplates) {
                            if (skillTree.getSkillGroup() != null) {
                                if (skillTree.getSkillGroup().contains("RI_OverloadExplosion")) {
                                    player.getSkillList().addLinkedSkill(player, skillTree.getSkillId(), linkedLevel + 1);
                                    linkedSkillName = DataManager.SKILL_DATA.getSkillTemplate(skillTree.getSkillId()).getName();
                                    linkedSkillLevel = DataManager.SKILL_DATA.getSkillTemplate(skillTree.getSkillId()).getLvl();
                                }
                            }
                        }
                    } if (!onlogin) {
                        PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_STIGMA_GET_HIDDEN_SKILL(linkedSkillLevel, linkedSkillName));
                    }
                } else if (list.contains(140001280) && list.contains(140001266) && list.contains(140001268)
                        || list.contains(140001280) && list.contains(140001266) && list.contains(140001267)
                        || list.contains(140001280) && list.contains(140001268) && list.contains(140001267)) {
                    for (int i = 1; i <= player.getLevel(); i++) {
                        SkillLearnTemplate[] skillTemplates = DataManager.SKILL_TREE_DATA.getTemplatesFor(player.getPlayerClass(), i, player.getRace());
                        for (SkillLearnTemplate skillTree : skillTemplates) {
                            if (skillTree.getSkillGroup() != null) {
                                if (skillTree.getSkillGroup().contains("RI_EathiumCharge")) {
                                    player.getSkillList().addLinkedSkill(player, skillTree.getSkillId(), linkedLevel + 1);
                                    linkedSkillName = DataManager.SKILL_DATA.getSkillTemplate(skillTree.getSkillId()).getName();
                                    linkedSkillLevel = DataManager.SKILL_DATA.getSkillTemplate(skillTree.getSkillId()).getLvl();
                                }
                            }
                        }
                    } if (!onlogin) {
                        PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_STIGMA_GET_HIDDEN_SKILL(linkedSkillLevel, linkedSkillName));
                    }
                } else {
                    for (int i = 1; i <= player.getLevel(); i++) {
                        SkillLearnTemplate[] skillTemplates = DataManager.SKILL_TREE_DATA.getTemplatesFor(player.getPlayerClass(), i, player.getRace());
                        for (SkillLearnTemplate skillTree : skillTemplates) {
                            if (skillTree.getSkillGroup() != null) {
                                if (skillTree.getSkillGroup().contains("RI_EathiumNet")) {
                                    player.getSkillList().addLinkedSkill(player, skillTree.getSkillId(), linkedLevel + 1);
                                    linkedSkillName = DataManager.SKILL_DATA.getSkillTemplate(skillTree.getSkillId()).getName();
                                    linkedSkillLevel = DataManager.SKILL_DATA.getSkillTemplate(skillTree.getSkillId()).getLvl();
                                }
                            }
                        }
                    } if (!onlogin) {
                        PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_STIGMA_GET_HIDDEN_SKILL(linkedSkillLevel, linkedSkillName));
                    }
                }
            }
            return;
        case GUNSLINGER:
            if (list.size() >= 6 && !hasInert) {
                if (list.contains(140001262) && list.contains(140001249) && list.contains(140001247)
                        || list.contains(140001262) && list.contains(140001249) && list.contains(140001248)
                        || list.contains(140001262) && list.contains(140001247) && list.contains(140001248)) {
                    for (int i = 1; i <= player.getLevel(); i++) {
                        SkillLearnTemplate[] skillTemplates = DataManager.SKILL_TREE_DATA.getTemplatesFor(player.getPlayerClass(), i, player.getRace());
                        for (SkillLearnTemplate skillTree : skillTemplates) {
                            if (skillTree.getSkillGroup() != null) {
                                if (skillTree.getSkillGroup().contains("GU_Pursuit")) {
                                    player.getSkillList().addLinkedSkill(player, skillTree.getSkillId(), linkedLevel + 1);
                                    linkedSkillName = DataManager.SKILL_DATA.getSkillTemplate(skillTree.getSkillId()).getName();
                                    linkedSkillLevel = DataManager.SKILL_DATA.getSkillTemplate(skillTree.getSkillId()).getLvl();
                                }
                            }
                        }
                    } if (!onlogin) {
                        PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_STIGMA_GET_HIDDEN_SKILL(linkedSkillLevel, linkedSkillName));
                    }
                } else if (list.contains(140001263) && list.contains(140001251) && list.contains(140001252)
                        || list.contains(140001263) && list.contains(140001251) && list.contains(140001250)
                        || list.contains(140001263) && list.contains(140001252) && list.contains(140001250)) {
                    for (int i = 1; i <= player.getLevel(); i++) {
                        SkillLearnTemplate[] skillTemplates = DataManager.SKILL_TREE_DATA.getTemplatesFor(player.getPlayerClass(), i, player.getRace());
                        for (SkillLearnTemplate skillTree : skillTemplates) {
                            if (skillTree.getSkillGroup() != null) {
                                if (skillTree.getSkillGroup().contains("GU_SupportFire")) {
                                    player.getSkillList().addLinkedSkill(player, skillTree.getSkillId(), linkedLevel + 1);
                                    linkedSkillName = DataManager.SKILL_DATA.getSkillTemplate(skillTree.getSkillId()).getName();
                                    linkedSkillLevel = DataManager.SKILL_DATA.getSkillTemplate(skillTree.getSkillId()).getLvl();
                                }
                            }
                        }
                    } if (!onlogin) {
                        PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_STIGMA_GET_HIDDEN_SKILL(linkedSkillLevel, linkedSkillName));
                    }
                } else {
                    for (int i = 1; i <= player.getLevel(); i++) {
                        SkillLearnTemplate[] skillTemplates = DataManager.SKILL_TREE_DATA.getTemplatesFor(player.getPlayerClass(), i, player.getRace());
                        for (SkillLearnTemplate skillTree : skillTemplates) {
                            if (skillTree.getSkillGroup() != null) {
                                if (skillTree.getSkillGroup().contains("GU_BlindlyShot")) {
                                    player.getSkillList().addLinkedSkill(player, skillTree.getSkillId(), linkedLevel + 1);
                                    linkedSkillName = DataManager.SKILL_DATA.getSkillTemplate(skillTree.getSkillId()).getName();
                                    linkedSkillLevel = DataManager.SKILL_DATA.getSkillTemplate(skillTree.getSkillId()).getLvl();
                                }
                            }
                        }
                    } if (!onlogin) {
                        PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_STIGMA_GET_HIDDEN_SKILL(linkedSkillLevel, linkedSkillName));
                    }
                }
            }
            return;
        case SONGWEAVER:
            if (list.size() >= 6 && !hasInert) {
                if (list.contains(140001297) && list.contains(140001285) && list.contains(140001283)
                        || list.contains(140001297) && list.contains(140001285) && list.contains(140001286)
                        || list.contains(140001297) && list.contains(140001283) && list.contains(140001286)) {
                    for (int i = 1; i <= player.getLevel(); i++) {
                        SkillLearnTemplate[] skillTemplates = DataManager.SKILL_TREE_DATA.getTemplatesFor(player.getPlayerClass(), i, player.getRace());
                        for (SkillLearnTemplate skillTree : skillTemplates) {
                            if (skillTree.getSkillGroup() != null) {
                                if (skillTree.getSkillGroup().contains("Ba_MassDispel")) {
                                    player.getSkillList().addLinkedSkill(player, skillTree.getSkillId(), linkedLevel + 1);
                                    linkedSkillName = DataManager.SKILL_DATA.getSkillTemplate(skillTree.getSkillId()).getName();
                                    linkedSkillLevel = DataManager.SKILL_DATA.getSkillTemplate(skillTree.getSkillId()).getLvl();
                                }
                            }
                        }
                    } if (!onlogin) {
                        PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_STIGMA_GET_HIDDEN_SKILL(linkedSkillLevel, linkedSkillName));
                    }
                } else if (list.contains(140001296) && list.contains(140001281) && list.contains(140001284)
                        || list.contains(140001296) && list.contains(140001281) && list.contains(140001282)
                        || list.contains(140001296) && list.contains(140001284) && list.contains(140001282)) {
                    for (int i = 1; i <= player.getLevel(); i++) {
                        SkillLearnTemplate[] skillTemplates = DataManager.SKILL_TREE_DATA.getTemplatesFor(player.getPlayerClass(), i, player.getRace());
                        for (SkillLearnTemplate skillTree : skillTemplates) {
                            if (skillTree.getSkillGroup() != null) {
                                if (skillTree.getSkillGroup().contains("Ba_IllusionRequiem")) {
                                    player.getSkillList().addLinkedSkill(player, skillTree.getSkillId(), linkedLevel + 1);
                                    linkedSkillName = DataManager.SKILL_DATA.getSkillTemplate(skillTree.getSkillId()).getName();
                                    linkedSkillLevel = DataManager.SKILL_DATA.getSkillTemplate(skillTree.getSkillId()).getLvl();
                                }
                            }
                        }
                    } if (!onlogin) {
                        PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_STIGMA_GET_HIDDEN_SKILL(linkedSkillLevel, linkedSkillName));
                    }
                } else {
                    for (int i = 1; i <= player.getLevel(); i++) {
                        SkillLearnTemplate[] skillTemplates = DataManager.SKILL_TREE_DATA.getTemplatesFor(player.getPlayerClass(), i, player.getRace());
                        for (SkillLearnTemplate skillTree : skillTemplates) {
                            if (skillTree.getSkillGroup() != null) {
                                if (skillTree.getSkillGroup().contains("Ba_SongofIllusion")) {
                                    player.getSkillList().addLinkedSkill(player, skillTree.getSkillId(), linkedLevel + 1);
                                    linkedSkillName = DataManager.SKILL_DATA.getSkillTemplate(skillTree.getSkillId()).getName();
                                    linkedSkillLevel = DataManager.SKILL_DATA.getSkillTemplate(skillTree.getSkillId()).getLvl();
                                }
                            }
                        }
                    } if (!onlogin) {
                        PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_STIGMA_GET_HIDDEN_SKILL(linkedSkillLevel, linkedSkillName));
                    }
                }
            }
            return;
        default:
            break;
        }
        hasInert = false;
    }
    private static int getLinkStigmaLevel(Player player) {
        List<Item> equippedItems = player.getEquipment().getEquippedItemsAllStigma();
        List<Integer> enchantStigmaList = new ArrayList<Integer>();
        int linkedStimgaLevel = 1;
        for (Item item : equippedItems) {
            enchantStigmaList.add(Integer.valueOf(item.getEnchantLevel()));
        } if (enchantStigmaList.size() > 0) {
            Collections.sort(enchantStigmaList);
            linkedStimgaLevel = enchantStigmaList.get(0);
        }
        return linkedStimgaLevel;
    }
    private static int getSetStigmaLevel(Player player, List<Item> StigmaItems) {
        if (StigmaItems != null) {
            List<Integer> enchantStigmaList = new ArrayList<Integer>();
            int linkedStimgaLevel = 1;
            for (Item item : StigmaItems) {
                if (item.getEquipmentSlot() != ItemSlot.SPECIAL_STIGMA.getSlotIdMask()) {
                    enchantStigmaList.add(item.getEnchantLevel());
                }
            } if (enchantStigmaList.size() > 0) {
                Collections.sort(enchantStigmaList);
                linkedStimgaLevel = enchantStigmaList.get(0);
            } if (linkedStimgaLevel == 6) {
                return 1;
            } if (linkedStimgaLevel == 7) {
                return 2;
            } if ((linkedStimgaLevel == 8) || (linkedStimgaLevel == 9)) {
                return 3;
            }     if (linkedStimgaLevel >= 10) {
                return 5;
            }
            return 0;
        }
        int stigmaEnchantLvl = getLinkStigmaLevel(player);
        if (stigmaEnchantLvl == 6) {
            return 1;
        } if (stigmaEnchantLvl == 7) {
            return 2;
        } if (stigmaEnchantLvl == 8 || stigmaEnchantLvl == 9) {
            return 3;
        } if (stigmaEnchantLvl >= 10) {
            return 5;
        }
        return 0;
    }
    public static void StigmaSetBonus(Player player, List<Item> StigmaItems, boolean equip) {
        if (equip) {
            if (StigmaItems.size() < 6) {
                return;
            }
            int BonusLevel = getSetStigmaLevel(player, StigmaItems);
            if (BonusLevel <= 0) {
                return;
            } for (Item item : StigmaItems) {
                Stigma st = item.getItemTemplate().getStigma();
                if (item.getEquipmentSlot() != ItemSlot.SPECIAL_STIGMA.getSlotIdMask()) {
                    for (int i = 1; i <= player.getLevel(); i++) {
                        SkillLearnTemplate[] skillTemplates = DataManager.SKILL_TREE_DATA.getTemplatesFor(player.getPlayerClass(), i, player.getRace());
                        for (SkillLearnTemplate skillTree : skillTemplates) {
                            if (st.getSkillGroup1() != null && skillTree.getSkillGroup() != null) {
                                if (st.getSkillGroup1().toLowerCase().equals(skillTree.getSkillGroup().toLowerCase())) {
                                    int SkillLevel = skillTree.getSkillLevel() + item.getEnchantLevel() + BonusLevel;
                                    int skillid = skillTree.getSkillId();
                                    PlayerSkillEntry skills = player.getSkillList().getSkillEntry(skillid);
                                    if (skills != null) {
                                        player.getEffectController().removeEffect(skillTree.getSkillId());
                                        player.getSkillList().removeSkill(skillid);
                                        player.getSkillList().addStigmaSkill(player, skillTree.getSkillId(), SkillLevel, true);
                                    }
                                }
                            } if (st.getSkillGroup2() != null && skillTree.getSkillGroup() != null) {
                                if (st.getSkillGroup2().toLowerCase().equals(skillTree.getSkillGroup().toLowerCase())) {
                                    int SkillLevel = skillTree.getSkillLevel() + item.getEnchantLevel() + BonusLevel;
                                    player.getEffectController().removeEffect(skillTree.getSkillId());
                                    player.getSkillList().removeSkill(skillTree.getSkillId());
                                    player.getSkillList().addStigmaSkill(player, skillTree.getSkillId(), SkillLevel, true);
                                }
                            }
                        }
                    }
                }
                PacketSendUtility.sendPacket(player, new SM_SKILL_LIST(player, player.getSkillList().getBasicSkills()));
                PacketSendUtility.sendPacket(player, new SM_SKILL_LIST(player, player.getSkillList().getStigmaSkills()));
            }
            if (BonusLevel > 0) {
                PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1404451, new Object[] { Integer.valueOf(BonusLevel) }));
            } if (getLinkStigmaLevel(player) >= 9) {
                PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1404452, new Object[0]));
            }
        } else {
            for (Item item : StigmaItems) {
                Stigma st = item.getItemTemplate().getStigma();
                if (item.getEquipmentSlot() != ItemSlot.SPECIAL_STIGMA.getSlotIdMask()) {
                    for (int i = 1; i <= player.getLevel(); i++) {
                        SkillLearnTemplate[] skillTemplates = DataManager.SKILL_TREE_DATA.getTemplatesFor(player.getPlayerClass(), i, player.getRace());
                        for (SkillLearnTemplate skillTree : skillTemplates) {
                            if (st.getSkillGroup1() != null && skillTree.getSkillGroup() != null) {
                                if (st.getSkillGroup1().toLowerCase().equals(skillTree.getSkillGroup().toLowerCase())) {
                                    int SkillLevel = skillTree.getSkillLevel() + item.getEnchantLevel();
                                    int skillid = skillTree.getSkillId();
                                    PlayerSkillEntry skills = player.getSkillList().getSkillEntry(skillid);
                                    if (skills != null) {
                                        player.getEffectController().removeEffect(skillTree.getSkillId());
                                        player.getSkillList().removeSkill(skillid);
                                        player.getSkillList().addStigmaSkill(player, skillTree.getSkillId(), SkillLevel, true);
                                    }
                                }
                            } if (st.getSkillGroup2() != null && skillTree.getSkillGroup() != null) {
                                if (st.getSkillGroup2().toLowerCase().equals(skillTree.getSkillGroup().toLowerCase())) {
                                    int SkillLevel = skillTree.getSkillLevel() + item.getEnchantLevel();
                                    player.getEffectController().removeEffect(skillTree.getSkillId());
                                    player.getSkillList().removeSkill(skillTree.getSkillId());
                                    player.getSkillList().addStigmaSkill(player, skillTree.getSkillId(), SkillLevel, true);
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}