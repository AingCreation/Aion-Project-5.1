package com.aionemu.gameserver.services.reward;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.gameserver.configs.main.MembershipConfig;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.ingameshop.InGameShopEn;
import com.aionemu.gameserver.services.abyss.AbyssPointsService;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.World;
import com.aionemu.gameserver.world.knownlist.Visitor;

/**
 * @ author Ione542
 */

public class TollOnlineBonus {

    private static final Logger log = LoggerFactory.getLogger(TollOnlineBonus.class);

    private TollOnlineBonus() {
        ThreadPoolManager.getInstance().scheduleAtFixedRate(new Runnable() {

            @Override
            public void run() {
                World.getInstance().doOnAllPlayers(new Visitor<Player>() {

                    @Override
                    public void visit(Player object) {
					int qt = MembershipConfig.TOLL_ONLINE_BONUS_COUNT;
					int time = MembershipConfig.TOLL_ONLINE_BONUS_TIME;
					                InGameShopEn.getInstance().addToll(object, qt);
									PacketSendUtility.sendBrightYellowMessageOnCenter(object, "[Online Bonus Services]: You have Play "+time+" Minutes. You earn "+qt+" Toll");
                    }
                });
            }
        }, MembershipConfig.TOLL_ONLINE_BONUS_TIME * 60000, MembershipConfig.TOLL_ONLINE_BONUS_TIME * 60000);
    }

    public void playerLoggedIn(Player player) {
        if (MembershipConfig.TOLL_ONLINE_BONUS_ENABLE) {
            player.setOnlineBonusTime(System.currentTimeMillis());
        }
    }

    public void playerLoggedOut(Player player) {
    }

    public static TollOnlineBonus getInstance() {
        return SingletonHolder.instance;
    }

    @SuppressWarnings("synthetic-access")
    private static class SingletonHolder {

        protected static final TollOnlineBonus instance = new TollOnlineBonus();
    }
}