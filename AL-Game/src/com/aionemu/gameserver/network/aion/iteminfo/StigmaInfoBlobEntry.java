/*
 * This file is part of aion-lightning <js-emu.ru>.
 *
 *  aion-lightning is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  aion-lightning is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with aion-lightning.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.gameserver.network.aion.iteminfo;

import java.nio.ByteBuffer;

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.templates.item.Stigma;
import com.aionemu.gameserver.network.aion.iteminfo.ItemInfoBlob.ItemBlobType;
import com.aionemu.gameserver.skillengine.model.SkillLearnTemplate;

/**
 * This blob contains stigma info.
 * 
 * @author -Nemesiss-
 * @modified Rolandas
 */
public class StigmaInfoBlobEntry extends ItemBlobEntry {

	StigmaInfoBlobEntry() {
		super(ItemBlobType.STIGMA_INFO);
	}

	@Override
	public void writeThisBlob(ByteBuffer buf) {
		Item item = ownerItem;
		Stigma stigma = item.getItemTemplate().getStigma();
		SkillLearnTemplate[] skillTemplates = null;
		
		int stimgaSkillId1 = 0;
	    int stimgaSkillId2 = 0;
	    for (int i = 1; i <= owner.getLevel(); i++) {
	    	skillTemplates = DataManager.SKILL_TREE_DATA.getTemplatesFor(owner.getPlayerClass(), i, owner.getRace());
	    	for (SkillLearnTemplate skillTree : skillTemplates) {
	    		if (stigma.getSkillGroup1() != null) {
	    			if (skillTree.getSkillGroup() != null) {
	    				if (stigma.getSkillGroup1().toLowerCase().equals(skillTree.getSkillGroup().toLowerCase())) {
	    					stimgaSkillId1 = skillTree.getSkillId();
	    				}
	    			}
	    		} if (stigma.getSkillGroup2() != null) {
	    			if (skillTree.getSkillGroup() != null) {
	    				if (stigma.getSkillGroup2().toLowerCase().equals(skillTree.getSkillGroup().toLowerCase())) {
	    					stimgaSkillId2 = skillTree.getSkillId();
	    				}	
	    			}
	    		}
	    	}
	    }
	    
	    writeD(buf, stimgaSkillId1);
	    writeD(buf, stimgaSkillId2);
	    writeD(buf, stigma.getKinah());
	    
	    skip(buf, 192);
	    writeH(buf, 1);
	    writeH(buf, 0);
	    skip(buf, 96);
	    writeH(buf, 0);
	}

	@Override
	public int getSize() {
		return 8 + 4 + 192 + 4 + 96 + 2;
	}
}
