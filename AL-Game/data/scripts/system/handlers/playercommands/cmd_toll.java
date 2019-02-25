/**
 * This file is part of Aion-Lightning <aion-lightning.org>.
 *
 *  Aion-Lightning is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Aion-Lightning is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details. *
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Aion-Lightning.
 *  If not, see <http://www.gnu.org/licenses/>.
 *
 *
 * Credits goes to all Open Source Core Developer Groups listed below
 * Please do not change here something, ragarding the developer credits, except the "developed by XXXX".
 * Even if you edit a lot of files in this source, you still have no rights to call it as "your Core".
 * Everybody knows that this Emulator Core was developed by Aion Lightning 
 * @-Aion-Unique-
 * @-Aion-Lightning
 * @Aion-Engine
 * @Aion-Extreme
 * @Aion-NextGen
 * @Aion-Core Dev.
 */
package playercommands;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.ingameshop.InGameShopEn;
import com.aionemu.gameserver.services.abyss.AbyssPointsService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.chathandlers.PlayerCommand;

/**
 * @author Tiger
 */
public class cmd_toll extends PlayerCommand {

	public cmd_toll() {
        super("gptoll");
    }

    @Override
    public void execute(Player player, String... params) {
        int gp = 50000;
        int toll = 1000;
        if (player.getAbyssRank().getGp() < gp) {
            PacketSendUtility.sendMessage(player, "You Dont Have Glory Point : 50.000GP / 1000 Toll" + "");
            return;
        }
        addToll(player, toll);
        AbyssPointsService.addGp(player, -gp);
		PacketSendUtility.sendMessage(player, "You Get : " + toll + " Toll");
    }
    
    private void addToll (Player player, int toll) {
    	InGameShopEn.getInstance().addToll(player, toll);
    }
}