package com.aionemu.gameserver.network.aion.clientpackets;

import com.aionemu.gameserver.model.actions.PlayerMode;
import com.aionemu.gameserver.model.DescriptionId;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.trade.TradePSItem;
import com.aionemu.gameserver.services.*;
import com.aionemu.gameserver.skillengine.effect.AbnormalState;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection.State;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.utils.*;

public class CM_PRIVATE_STORE extends AionClientPacket
{
	private Player activePlayer;
	private TradePSItem[] tradePSItems;
	private int itemCount;
	private boolean cancelStore;
	
	public CM_PRIVATE_STORE(int opcode, State state, State... restStates) {
		super(opcode, state, restStates);
	}
	
	@Override
	protected void readImpl() {
		activePlayer = getConnection().getActivePlayer();
		int level = activePlayer.getLevel();
		if (activePlayer == null) {
			return;
		} if (activePlayer.isInPrison()) {
			cancelStore = true;
			PacketSendUtility.sendMessage(activePlayer, "You can't open Private Shop in prison!");
			return;
		} if (level < 10) {
			//Characters under level 10 who are using a free trial cannot open a private store.
			PacketSendUtility.sendPacket(activePlayer, SM_SYSTEM_MESSAGE.STR_FREE_EXPERIENCE_CHARACTER_CANT_OPEN_PERSONAL_SHOP("10"));
			return;
		} if (activePlayer.getController().isInCombat()) {
			//You cannot open a private store while fighting.
			PacketSendUtility.sendPacket(activePlayer, SM_SYSTEM_MESSAGE.STR_PERSONAL_SHOP_DISABLED_IN_EXCHANGE);
			//As you cannot open a private store while fighting, it will be closed automatically.
			PacketSendUtility.sendPacket(activePlayer, SM_SYSTEM_MESSAGE.STR_PERSONAL_SHOP_CLOSED_FOR_COMBAT_MODE);
			PrivateStoreService.closePrivateStore(activePlayer);
			return;
		} if ((activePlayer.isFlying()) ||
		   (activePlayer.isUsingFlyTeleport()) ||
		   (activePlayer.isInPlayerMode(PlayerMode.WINDSTREAM))) {
			//You cannot open a private store while flying.
			PacketSendUtility.sendPacket(activePlayer, SM_SYSTEM_MESSAGE.STR_PERSONAL_SHOP_DISABLED_IN_FLY_MODE);
			return;
		} if (activePlayer.isInPlayerMode(PlayerMode.RIDE)) {
			//You cannot open a private store while mounted.
			PacketSendUtility.sendPacket(activePlayer, SM_SYSTEM_MESSAGE.STR_MSG_PERSONAL_SHOP_RESTRICTION_RIDE);
			return;
		} if (activePlayer.getEffectController().isAbnormalSet(AbnormalState.HIDE)) {
			//You cannot open a private store while hiding.
			PacketSendUtility.sendPacket(activePlayer, SM_SYSTEM_MESSAGE.STR_PERSONAL_SHOP_DISABLED_IN_HIDDEN_MODE);
			//Your private store closed automatically because you are currently hiding.
			PacketSendUtility.sendPacket(activePlayer, SM_SYSTEM_MESSAGE.STR_PERSONAL_SHOP_CLOSED_FOR_HIDDEN_MODE);
			PrivateStoreService.closePrivateStore(activePlayer);
			return;
        }
		itemCount = readH();
		tradePSItems = new TradePSItem[itemCount];
		if (activePlayer.getMoveController().isInMove()) {
			//You cannot open the private store on a moving object.
			PacketSendUtility.sendPacket(activePlayer, SM_SYSTEM_MESSAGE.STR_PERSONAL_SHOP_DISABLED_IN_MOVING_OBJECT);
			cancelStore = true;
			return;
		} for (int i = 0; i < itemCount; i++) {
			int itemObjId = readD();
			int itemId = readD();
			int count = readH();
			long price = readD();
			readD(); // unk ?
			Item item = activePlayer.getInventory().getItemByObjId(itemObjId);
			if ((price < 0 || item == null || item.getItemId() != itemId || item.getItemCount() < count) && !cancelStore) {
				PacketSendUtility.sendMessage(activePlayer, "Invalid item.");
				cancelStore = true;
			} else if (!item.isTradeable(activePlayer)) {
				PacketSendUtility.sendPacket(activePlayer, new SM_SYSTEM_MESSAGE(1300344, new DescriptionId(item.getNameId())));
				cancelStore = true;
			}
			tradePSItems[i] = new TradePSItem(itemObjId, itemId, count, price);
		}
	}
	
	@Override
	protected void runImpl() {
        if (activePlayer == null) {
           return;
        } if (activePlayer.getLifeStats().isAlreadyDead()) {
		    return;
		} if (!cancelStore && itemCount > 0) {
			PrivateStoreService.addItems(activePlayer, tradePSItems);
		} else {
			PrivateStoreService.closePrivateStore(activePlayer);
		}
	}
}