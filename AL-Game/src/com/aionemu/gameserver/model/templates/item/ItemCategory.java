package com.aionemu.gameserver.model.templates.item;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "item_category")
@XmlEnum
public enum ItemCategory
{
	MANASTONE (0x00),
	ANCIENT_MANASTONE (0x01),
	GODSTONE (0x02),
	ENCHANTMENT (0x03),
	FLUX (0x04),
	BALIC_EMOTION (0x05),
	BALIC_MATERIAL (0x06),
	RAWHIDE (0x07),
	SOULSTONE (0x08),
	RECIPE (0x09),
	GATHERABLE (0x0A),
	GATHERABLE_BONUS (0x0B),
	DROP_MATERIAL (0x0C),
	SWORD (0x0D),
	DAGGER (0x0E),
	MACE (0x0F),
	ORB (0x10),
	SPELLBOOK (0x11),
	GREATSWORD (0x12),
	POLEARM (0x13),
	STAFF (0x14),
	BOW (0x15),
	SHIELD (0x16),
	HARP (0x17),
	GUN (0x18),
	CANNON (0x19),
	KEYBLADE (0x1A),
	JACKET (0x1B),
	PANTS (0x1C),
	SHARD (0x1D),
	SHOES (0x1E),
	GLOVES (0x1F),
	SHOULDERS (0x20),
	NECKLACE (0x21),
	EARRINGS (0x22),
	RINGS (0x23),
	HELMET (0x24),
	BELT (0x25),
	SKILLBOOK (0x26),
	STIGMA (0x27),
	COINS (0x28),
	MEDALS (0x29),
	QUEST (0x2A),
	KEY (0x2B),
	TEMPERING (0x2C),
	CRAFT_BOOST (0x2D),
	COMBINATION (0x2E),
	PLUME (0x2F),
	STENCHANTMENT (0x30),
	STIGMABUNDLE (0x31),
	BRACELET (0x32),
	ESTIMA (0x33),
	ALL (0x63),
	NONE (0x64);
	
	private int id;
	
	private ItemCategory(int id) {
		this.id = id;
	}
	
	public static ItemCategory fromValue(String v) {
		return valueOf(v);
	}
	  
	public int getId() {
		return id;
	}
	  
	public String value() {
		return name();
	}
}