package com.aionemu.gameserver.model.templates.item.actions;

import java.util.concurrent.Future;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.controllers.observer.ItemUseObserver;
import com.aionemu.gameserver.model.DescriptionId;
import com.aionemu.gameserver.model.TaskId;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.Kisk;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.spawns.SpawnTemplate;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ITEM_USAGE_ANIMATION;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.services.KiskService;
import com.aionemu.gameserver.spawnengine.SpawnEngine;
import com.aionemu.gameserver.spawnengine.VisibleObjectSpawner;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ToyPetSpawnAction")
public class ToyPetSpawnAction extends AbstractItemAction
{
	@XmlAttribute
	protected int npcid;
	
	@XmlAttribute
	protected int time;
	
	public int getNpcId() {
		return npcid;
	}
	
	public int getTime() {
		return time;
	}
	
	@Override
	public boolean canAct(Player player, Item parentItem, Item targetItem) {
		if (player.getFlyState() != 0) {
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_CANNOT_USE_BINDSTONE_ITEM_WHILE_FLYING);
			return false;
		} if (player.isInInstance()) {
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_CANNOT_REGISTER_BINDSTONE_FAR_FROM_NPC);
			return false;
		} if (KiskService.getInstance().haveKisk(player.getObjectId())) {
			PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1390160, new Object[0]));
			return false;
		} switch (player.getWorldId()) {
		    //Restriction Elyos Spawn Kisk.
			case 110010000: //Sanctum.
		    case 110020000: //Cloister Of Kaisinel.
		    case 110070000: //Kaisinel Academy.
			case 130090000: //Wisplight Abbey.
			case 210010000: //Poeta.
			case 210080000: //Griffoen.
			case 210110000: //Tower Of Eternity E.
			case 700010000: //Oriel.
			//Restriction Asmodians Spawn Kisk.
			case 120010000: //Pandaemonium.
			case 120020000: //Convent Of Marchutan.
			case 120080000: //Marchutan Priory.
			case 140010000: //Fatebound Abbey.
			case 220010000: //Ishalgen.
			case 220120000: //Tower Of Eternity A.
			case 220090000: //Habrok.
			case 710010000: //Pernon.
			//Restriction Both Race Spawn Kisk.
			case 600010000: //Silentera.
				PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_CANNOT_USE_ITEM_INVALID_LOCATION);
				return false;
			default:
				break;
		}
		return true;
	}
	
	@Override
	public void act(final Player player, final Item parentItem, Item targetItem) {
		player.getController().cancelUseItem();
		PacketSendUtility.broadcastPacket(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(), parentItem.getObjectId(), parentItem.getItemId(), 10000, 0, 0), true);
		final ItemUseObserver observer = new ItemUseObserver() {
			@Override
			public void abort() {
				player.getController().cancelTask(TaskId.ITEM_USE);
				player.removeItemCoolDown(parentItem.getItemTemplate().getUseLimits().getDelayId());
				PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_ITEM_CANCELED(new DescriptionId(parentItem.getItemTemplate().getNameId())));
				PacketSendUtility.broadcastPacket(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(), parentItem.getObjectId(), parentItem.getItemTemplate().getTemplateId(), 0, 2, 0), true);
			}
		};
		player.getObserveController().attach(observer);
		player.getController().addTask(TaskId.ITEM_USE, ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				PacketSendUtility.broadcastPacket(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(), parentItem.getObjectId(), parentItem.getItemId(), 0, 1, 1), true);
				player.getObserveController().removeObserver(observer);
				if (!player.getInventory().decreaseByObjectId(parentItem.getObjectId(), 1))
					return;
				PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_USE_ITEM(new DescriptionId(parentItem.getItemTemplate().getNameId())));
				float x = player.getX();
				float y = player.getY();
				float z = player.getZ();
				byte heading = (byte) ((player.getHeading() + 60) % 120);
				int worldId = player.getWorldId();
				int instanceId = player.getInstanceId();
				SpawnTemplate spawn = SpawnEngine.addNewSingleTimeSpawn(worldId, npcid, x, y, z, heading);
				final Kisk kisk = VisibleObjectSpawner.spawnKisk(spawn, instanceId, player);
				Integer objOwnerId = player.getObjectId();
				Future<?> task = ThreadPoolManager.getInstance().schedule(new Runnable() {
					@Override
					public void run() {
						kisk.getController().onDelete();
					}
				}, 7200000);
				kisk.getController().addTask(TaskId.DESPAWN, task);
				player.getController().cancelTask(TaskId.ITEM_USE);
				KiskService.getInstance().regKisk(kisk, objOwnerId);
				if (kisk.getMaxMembers() > 1) {
					kisk.getController().onDialogRequest(player);
				} else {
					KiskService.getInstance().onBind(kisk, player);
				}
			}
		}, 10000));
	}
}