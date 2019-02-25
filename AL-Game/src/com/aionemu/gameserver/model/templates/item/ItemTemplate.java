package com.aionemu.gameserver.model.templates.item;

import java.util.List;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang.StringUtils;

import com.aionemu.gameserver.configs.main.CustomConfig;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.PlayerClass;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.items.ItemId;
import com.aionemu.gameserver.model.items.ItemMask;
import com.aionemu.gameserver.model.stats.calc.functions.StatFunction;
import com.aionemu.gameserver.model.templates.VisibleObjectTemplate;
import com.aionemu.gameserver.model.templates.item.actions.ItemActions;
import com.aionemu.gameserver.model.templates.itemset.ItemSetTemplate;
import com.aionemu.gameserver.model.templates.stats.ModifiersTemplate;
import com.aionemu.gameserver.world.zone.ZoneName;

@XmlAccessorType(XmlAccessType.NONE)
@XmlType(namespace = "", name = "ItemTemplate")
public class ItemTemplate extends VisibleObjectTemplate
{
	@XmlAttribute(name = "id", required = true)
	@XmlID
	private String id;

	private int itemId;

	@XmlElement(name = "modifiers", required = false)
	protected ModifiersTemplate modifiers;

	@XmlElement(name = "actions", required = false)
	protected ItemActions actions;
	
	@XmlAttribute(name = "name_desc")
	private String namedesc;

	@XmlAttribute(name = "mask")
	private int mask;

	@XmlAttribute(name = "category")
	private ItemCategory category = ItemCategory.NONE;

	@XmlAttribute(name = "slot")
	private int itemSlot;

	@XmlAttribute(name = "equipment_type")
	private EquipType equipmentType = EquipType.NONE;

	@XmlAttribute(name = "weapon_boost")
	private int weaponBoost;

	@XmlAttribute(name = "price")
	private int price;

	@XmlAttribute(name = "luna_price")
	private int lunaPrice;

	@XmlAttribute(name = "robot_id")
	private int robot_id;

	@XmlAttribute(name = "abyss_point")
	private int abyssPoint;

	@XmlAttribute(name = "max_stack_count")
	private int maxStackCount = 1;

	@XmlAttribute(name = "unit_sell_count")
	private int unitSellCount = 1;

	@XmlAttribute(name = "level")
	private int level;

	@XmlAttribute(name = "quality")
	private ItemQuality itemQuality;

	@XmlAttribute(name = "item_type")
	private ItemType itemType;

	@XmlAttribute(name = "weapon_type")
	private WeaponType weaponType;

	@XmlAttribute(name = "armor_type")
	private ArmorType armorType;

	@XmlAttribute(name = "attack_type")
	private ItemAttackType attackType;

	@XmlAttribute(name = "attack_gap")
	private float attackGap;

	@XmlAttribute(name = "desc")
	private String description;

	@XmlAttribute(name = "option_slot_bonus")
	private int optionSlotBonus;

	@XmlAttribute(name = "rnd_bonus")
	private int rnd_bonus = 0;

	@XmlAttribute(name = "rnd_count")
	private int rnd_count = 0;

	@XmlAttribute(name = "wrappable_count")
	private int wrappable_count = 0;

	@XmlAttribute(name = "max_authorize")
	private int maxAuthorize;

	@XmlAttribute(name = "tempering_table_id")
	private int temperingTableId;

	@XmlAttribute(name = "robot_name")
	private int robotName = 0;

	@XmlAttribute(name = "bonus_apply")
	private String bonusApply;

	@XmlAttribute(name = "no_enchant")
	private boolean noEnchant;

	@XmlAttribute(name = "dye")
	private boolean itemDyePermitted;

	@XmlAttribute(name = "race")
	private Race race = Race.PC_ALL;

	@XmlAttribute(name = "return_world")
	private int returnWorldId;

	@XmlAttribute(name = "return_alias")
	private String returnAlias;

	@XmlElement(name = "godstone")
	private GodstoneInfo godstoneInfo;

	@XmlElement(name = "stigma")
	private Stigma stigma;

	@XmlAttribute(name = "name")
	private String name;

	@XmlAttribute(name = "restrict")
	private String restrict;

	@XmlAttribute(name = "restrict_max")
	private String restrictMax;

	@XmlTransient
	private int[] restricts;

	@XmlTransient
	private byte[] restrictsMax;

	@XmlAttribute(name = "m_slots")
	private int manastoneSlots;

	@XmlAttribute(name = "s_slots")
	private int specialSlots;

	@XmlAttribute(name = "max_enchant")
	private int maxEnchant;

	@XmlAttribute(name = "max_enchant_bonus")
	private int max_enchant_bonus;

	@XmlAttribute(name = "temp_exchange_time")
	protected int temExchangeTime;

	@XmlAttribute(name = "expire_time")
	protected int expireTime;

	@XmlElement(name = "weapon_stats")
	protected WeaponStats weaponStats;

	@XmlAttribute(name = "activate_count")
	private int activationCount;

	@XmlAttribute(name = "func_pet_id")
	private int funcPetId;

	@XmlElement(name = "tradein_list")
	protected TradeinList tradeinList;

	@XmlElement(name = "acquisition")
	private Acquisition acquisition;

	@XmlElement(name = "disposition")
	private Disposition disposition;

	@XmlElement(name = "improve")
	private Improvement improvement;

	@XmlElement(name = "uselimits")
	private ItemUseLimits useLimits = new ItemUseLimits();

	@XmlElement(name = "purchable")
	private ItemPurchableLimits purchableLimits = new ItemPurchableLimits();

	@XmlElement(name = "inventory")
	private ExtraInventory extraInventory;

	@XmlElement(name = "idian")
	private Idian idianAction;

	@XmlTransient
	private boolean isQuestUpdateItem;

	@XmlAttribute(name = "skill_group")
	private String skill_group;

	@XmlAttribute(name = "skin_skill")
	private int skin_skill;

	@XmlAttribute(name = "skill_enchant")
	private int skill_enchant;
	
	@XmlAttribute(name = "enchant_base")
	private int enchant_base = 0;
	
	@XmlAttribute(name="item_archdaeva")
	private boolean item_archdaeva;
	
	@XmlAttribute(name = "item_custom_set")
	private int itemCustomSet = 0;
	
	@XmlAttribute(name = "exceed_enchant")
	private boolean exceedEnchant = false;
	
	@XmlAttribute(name="enchant_type")
	private int enchant_type;
	
	@XmlAttribute(name="authorize_type")
	private int authorize_type;

	private static final WeaponStats emptyWeaponStats = new WeaponStats();

	/**
	 * @param u
	 * @param parent
	 */
	void afterUnmarshal(Unmarshaller u, Object parent) {
		setItemId(Integer.parseInt(id));
		String[] parts = restrict.split(",");
		restricts = new int[17]; //4.3/4.5 Restriction.
		for (int i = 0; i < parts.length; i++) {
			restricts[i] = Integer.parseInt(parts[i]);
		} if (restrictMax != null) {
			String[] partsMax = restrictMax.split(",");
			restrictsMax = new byte[17]; //4.3/4.5 Restriction.
			for (int i = 0; i < partsMax.length; i++) {
				restrictsMax[i] = Byte.parseByte(partsMax[i]);
			}
		} if (weaponStats == null) {
			weaponStats = emptyWeaponStats;
		}
	}
	
	public byte getMaxLevelRestrict(Player player) {
		if (restrictMax != null) {
            byte restrictId = player.getPlayerClass().getClassId();
            byte restrictLevel = restrictsMax[restrictId];
            return player.getLevel() <= restrictLevel ? 0 : restrictLevel;
        }
        return 0;
    }
	
	public String getId() {
		return id;
	}

	public int getMask() {
		return mask;
	}

	public ItemCategory getCategory() {
		return category;
	}

	public int getItemSlot() {
		return itemSlot;
	}

	/**
	 * @param playerClass
	 * @return
	 */
	public boolean isClassSpecific(PlayerClass playerClass) {
		boolean related = restricts[playerClass.ordinal()] > 0;
		if (!related && !playerClass.isStartingClass()) {
			related = restricts[PlayerClass.getStartingClassFor(playerClass).ordinal()] > 0;
		}
		return related;
	}

	/**
	 * @param playerClass
	 * @param level
	 * @return
	 */
	public int getRequiredLevel(PlayerClass playerClass) {
		int requiredLevel = restricts[playerClass.ordinal()];
		//A player can equip item between 66-83 but have not full stats apply.
		if (requiredLevel >= 66 && requiredLevel <= 83) {
			return 66;
		} if (requiredLevel == 0) {
			return -1;
		} else {
			return requiredLevel;
		}
	}

	public List<StatFunction> getModifiers() {
		if (modifiers != null) {
			return modifiers.getModifiers();
		}
		return null;
	}

	public ItemActions getActions() {
		return actions;
	}

	public EquipType getEquipmentType() {
		return equipmentType;
	}

	public int getPrice() {
		return price;
	}

	public int getLunaPrice() {
		return lunaPrice;
	}

	public int getRobotId() {
		return robot_id;
	}
	
	public String getNamedesc() {
		return namedesc;
	}

	public int getAbyssPoint() {
		return abyssPoint;
	}

	public int getLevel() {
		return level;
	}

	public ItemQuality getItemQuality() {
		return itemQuality;
	}

	public ItemType getItemType() {
		return itemType;
	}

	public WeaponType getWeaponType() {
		return weaponType;
	}

	public ArmorType getArmorType() {
        if (isPlume()) {
            return ArmorType.PLUME;
		} if (isBracelet()) {
            return ArmorType.BRACELET;
		}
        return armorType;
    }

	@Override
	public int getNameId() {
		try {
			int val = Integer.parseInt(description);
			return val;
		}
		catch (NumberFormatException nfe) {
			return 0;
		}
	}

	public long getMaxStackCount() {
		if (isKinah()) {
			if (CustomConfig.ENABLE_KINAH_CAP) {
				return CustomConfig.KINAH_CAP_VALUE;
			} else {
				return Long.MAX_VALUE;
			}
		} if (isLuna()) {
			if (CustomConfig.ENABLE_LUNA_CAP) {
				return CustomConfig.LUNA_CAP_VALUE;
			} else {
				return Long.MAX_VALUE;
			}
		}
		return maxStackCount;
	}

	public ItemAttackType getAttackType() {
		return attackType;
	}

	public float getAttackGap() {
		return attackGap;
	}

	public int getOptionSlotBonus() {
		return optionSlotBonus;
	}

	public String getBonusApply() {
		return bonusApply;
	}

	public boolean isNoEnchant() {
		return noEnchant;
	}

	public boolean isItemDyePermitted() {
		return itemDyePermitted;
	}

	public Race getRace() {
		return race;
	}

	public int getWeaponBoost() {
		return weaponBoost;
	}

	public boolean isWeapon() {
		return equipmentType == EquipType.WEAPON;
	}

	public boolean isArmor() {
		return equipmentType == EquipType.ARMOR;
	}

	public boolean isKinah() {
		return itemId == ItemId.KINAH.value();
	}

	public boolean isLuna() {
		return itemId == ItemId.LUNA.value();
	}

	public boolean isStigma() {
		return itemId > 140001102 && itemId < 140001493 || itemId > 140000000 && itemId < 140000005; // checked 5.1
	}
	
	public boolean isPlume() {
		return category == ItemCategory.PLUME;
	}
	
	public boolean isBracelet() {
		return category == ItemCategory.BRACELET;
	}
	
	public boolean isManaStone() {
		return category == ItemCategory.MANASTONE ||
		category == ItemCategory.ANCIENT_MANASTONE;
	}
	
	public boolean isEstima() {
		return category == ItemCategory.ESTIMA;
	}
	
	public boolean isTemperingSolution(){
		return category == ItemCategory.TEMPERING;
	}
	
	public boolean isInertStigma() {
		return name.endsWith("(Inert)");
	}
	
	public void setItemId(int itemId) {
		this.itemId = itemId;
	}

	/**
	 * @return id of the associated ItemSetTemplate or null if none
	 */
	public ItemSetTemplate getItemSet() {
		return DataManager.ITEM_SET_DATA.getItemSetTemplateByItemId(itemId);
	}

	/**
	 * Checks if the ItemTemplate belongs to an item set
	 */
	public boolean isItemSet() {
		return getItemSet() != null;
	}

	public GodstoneInfo getGodstoneInfo() {
		return godstoneInfo;
	}

	@Override
	public String getName() {
		return name != null ? name : StringUtils.EMPTY;
	}

	@Override
	public int getTemplateId() {
		return itemId;
	}

	public int getReturnWorldId() {
		return returnWorldId;
	}

	public String getReturnAlias() {
		return returnAlias;
	}

	public Stigma getStigma() {
		return stigma;
	}

	public int getManastoneSlots() {
		return manastoneSlots;
	}

	public int getSpecialSlots() {
		return specialSlots;
	}
	
	public int getMaxEnchantLevel() {
		return maxEnchant;
	}
	
	public int getMaxEnchantBonus() {
		return max_enchant_bonus;
	}

	public boolean hasLimitOne() {
		return (getMask() & ItemMask.LIMIT_ONE) == ItemMask.LIMIT_ONE;
	}

	public boolean isTradeable() {
		return (getMask() & ItemMask.TRADEABLE) == ItemMask.TRADEABLE;
	}

	public boolean isCanFuse() {
		return (getMask() & ItemMask.CAN_COMPOSITE_WEAPON) == ItemMask.CAN_COMPOSITE_WEAPON;
	}

	public boolean canExtract() {
		return (getMask() & ItemMask.CAN_SPLIT) == ItemMask.CAN_SPLIT;
	}

	public boolean isSoulBound() {
		return (getMask() & ItemMask.SOUL_BOUND) == ItemMask.SOUL_BOUND;
	}

	public boolean isBreakable() {
		return (getMask() & ItemMask.BREAKABLE) == ItemMask.BREAKABLE;
	}

	public boolean isDeletable() {
		return (getMask() & ItemMask.DELETABLE) == ItemMask.DELETABLE;
	}

	public boolean isCanIdian() {
		return (getMask() & ItemMask.CAN_IDIAN) == ItemMask.CAN_IDIAN;
	}

	public boolean isArchdaeva() {
		return item_archdaeva;
	}

	public boolean isTwoHandWeapon() {
		if (!isWeapon())
			return false;
		return weaponType.getRequiredSlots() == 2;
	}

	public int getTempExchangeTime() {
		return temExchangeTime;
	}

	public int getExpireTime() {
		return expireTime;
	}

	public final WeaponStats getWeaponStats() {
		return weaponStats;
	}

	public int getActivationCount() {
		return activationCount;
	}

	public final int getFuncPetId() {
		return funcPetId;
	}

	public void modifyMask(boolean apply, int filter) {
		if (apply)
			mask |= filter;
		else
			mask &= ~filter;
	}

	public boolean isStackable() {
		return this.maxStackCount > 1;
	}

	public boolean hasAreaRestriction() {
		return useLimits.getUseArea() != null;
	}

	public ZoneName getUseArea() {
		return useLimits.getUseArea();
	}

	public TradeinList getTradeinList() {
		return tradeinList;
	}

	public Acquisition getAcquisition() {
		return acquisition;
	}

	public int getRandomBonusId() {
		return rnd_bonus;
	}

	public int getRandomBonusCount() {
		return rnd_count;
	}
	
	public int getWrappableCount() {
		return wrappable_count;
	}

	public int getMaxAuthorize() {
		return maxAuthorize;
	}
	
	public int getTemperingTableId() {
		return temperingTableId;
	}

	public int getRobotName() {
		return robotName;
	}

	public Improvement getImprovement() {
		return improvement;
	}

	public ItemUseLimits getUseLimits() {
		return useLimits;
	}

	public ItemPurchableLimits getPurchableLimits() {
		return purchableLimits;
	}

	public Disposition getDisposition() {
		return disposition;
	}

	public int getOwnershipWorld() {
		return useLimits.getOwnershipWorld();
	}

	public Idian getIdianAction() {
		return idianAction;
	}
	
	public boolean isCombinationItem() {
		return category == ItemCategory.COMBINATION;
	}
	
	public boolean isEnchantmentStone() {
		return category == ItemCategory.ENCHANTMENT;
	}
	
	public boolean isCloth() {
		return armorType != null && equipmentType == EquipType.ARMOR;
	}
	
	public boolean isAncientStone() {
		//Ancient Manastone: HP +105 && //[Stamp] Ancient Manastone: Healing Boost +6
		return itemId >= 167020000 && itemId <= 167020112;
	}
	
	public boolean isAccessory() {
		return category == ItemCategory.EARRINGS ||
		category == ItemCategory.RINGS ||
		category == ItemCategory.NECKLACE ||
		category == ItemCategory.PLUME ||
		category == ItemCategory.BRACELET ||
		category == ItemCategory.BELT ||
		category == ItemCategory.HELMET;
	}
	
	public boolean isQuestUpdateItem() {
		return isQuestUpdateItem;
	}
	
	public void setQuestUpdateItem(boolean value) {
		this.isQuestUpdateItem = value;
	}
	
	public int getExtraInventoryId() {
		if (extraInventory == null) {
			return -1;
		}
		return extraInventory.getId();
	}
	
	public String getSkillGroup() {
		return skill_group;
	}
	
	public int getSkinSkill() {
		return skin_skill;
	}
	
	public int getSkillEnchant() {
			return skill_enchant;
	}
	
	public int getBaseEnchant() {
		return enchant_base;
	}
	
	public int getItemCustomSet() {
		return itemCustomSet;
	}
	
	public boolean getExceedEnchant() {
		return exceedEnchant;
	}

	public boolean isWing() {
		return armorType == ArmorType.WING;
	}

	public int getAuthorizedType() {
		return authorize_type;
	}
	  
	public boolean isDestroyWhenTemperFail() {
		return authorize_type == 1;
	}
	
	public boolean isDestroyWhenEnchantFail() {
		if (enchant_type == 1) {
			return true;
		}
		if (level >= 66) {
			return true;
		}
		return false;
	}
}