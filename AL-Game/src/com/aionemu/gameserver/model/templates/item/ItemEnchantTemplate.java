package com.aionemu.gameserver.model.templates.item;

import gnu.trove.map.hash.TIntObjectHashMap;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.aionemu.gameserver.model.stats.calc.functions.StatFunction;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name="ItemEnchantTemplate")
public class ItemEnchantTemplate
{
	@XmlAttribute(name = "id")
    private int id;
	
    @XmlAttribute(name = "type")
    private EnchantType type;
	
    @XmlElement(name = "item_enchant", required = false)
    private List<ItemEnchantBonus> item_enchant;
	
    @SuppressWarnings({"rawtypes", "unchecked"})
    @XmlTransient
    private TIntObjectHashMap<List<StatFunction>> enchants = new TIntObjectHashMap();
	
    public List<StatFunction> getStats(int level) {
    	if (getItemEnchant() == null)
			return null;
		for (ItemEnchantBonus ib : getItemEnchant()) {
			if (ib.getLevel() != level)
				continue;

			return ib.getModifiers();
		}
		return null;
    }
	
    public List<ItemEnchantBonus> getItemEnchant() {
        return this.item_enchant;
    }
	
    public int getId() {
        return this.id;
    }
	
    public EnchantType getEnchantType() {
        return this.type;
    }
}