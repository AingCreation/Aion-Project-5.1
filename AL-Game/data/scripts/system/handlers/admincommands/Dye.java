package admincommands;

import com.aionemu.gameserver.configs.main.CustomConfig;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.PersistentState;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_UPDATE_PLAYER_APPEARANCE;
import com.aionemu.gameserver.services.item.ItemPacketService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.chathandlers.AdminCommand;

public class Dye extends AdminCommand
{
	public Dye() {
		super("dyes");
	}
	
	@Override
	public void execute(Player admin, String... params) {
		Player target;
		if (admin.getAccessLevel() > 0 && admin.getTarget() instanceof Player) {
			target = (Player) admin.getTarget();
		} else {
			target = admin;
		} if (target == null) {
			PacketSendUtility.sendMessage(admin, "You should select a target first!");
			return;
		} if (params.length == 0 || params.length > 2) {
			PacketSendUtility.sendMessage(admin, "syntax //dye <dye color|hex color|no>");
			return;
		}
		long price = CustomConfig.DYE_PRICE;
		if (admin.getInventory().getKinah() < price && !admin.isGM()) {
			PacketSendUtility.sendMessage(admin, "You need " + CustomConfig.DYE_PRICE + " kinah to dye yourself.");
			return;
		}
		String color = "";
		if (params.length == 2) {
			if (params[1].equalsIgnoreCase("petal")) {
				color = params[0];
			} else {
				color = params[0] + " " + params[1];
			}
		} else {
			color = params[0];
		}
		int rgb = 0;
		int bgra = 0;
		if (color.equalsIgnoreCase("blue")) {
			color = "f0f8ff";
		} if (color.equalsIgnoreCase("white")) {
			color = "faebd7";
		} if (color.equalsIgnoreCase("aqua")) {
			color = "00fff";
		} if (color.equalsIgnoreCase("turquoise")) {
			color = "198d94";
		} if (color.equalsIgnoreCase("blue2")) {
			color = "1f87f5";
		} if (color.equalsIgnoreCase("brown")) {
			color = "66250e";
		} if (color.equalsIgnoreCase("purple")) {
			color = "c38df5";
		} if (color.equalsIgnoreCase("red")) {
			color = "c22626";
		} if (color.equalsIgnoreCase("white2")) {
			color = "ffffff";
		} if (color.equalsIgnoreCase("black")) {
			color = "000000";
		} if (color.equalsIgnoreCase("orange")) {
			color = "e36b00";
		} if (color.equalsIgnoreCase("purple")) {
			color = "440b9a";
		} if (color.equalsIgnoreCase("pink")) {
			color = "d60b7e";
		} if (color.equalsIgnoreCase("mustard")) {
			color = "fcd251";
		} if (color.equalsIgnoreCase("green")) {
			color = "61bb4f";
		} if (color.equalsIgnoreCase("green2")) {
			color = "5f730e";
		} if (color.equalsIgnoreCase("blue3")) {
			color = "14398b";
		} if (color.equalsIgnoreCase("purple2")) {
			color = "80185d";
		} if (color.equalsIgnoreCase("wiki")) {
			color = "85e831";
		} if (color.equalsIgnoreCase("omblic")) {
			color = "ff5151";
		} if (color.equalsIgnoreCase("meon")) {
			color = "afaf26";
		} if (color.equalsIgnoreCase("ormea")) {
			color = "ffaa11";
		} if (color.equalsIgnoreCase("tange")) {
			color = "bd5fff";
		} if (color.equalsIgnoreCase("ervio")) {
			color = "3bb7fe";
		} if (color.equalsIgnoreCase("lunime")) {
			color = "c7af27";
		} if (color.equalsIgnoreCase("vinna")) {
			color = "052775";
		} if (color.equalsIgnoreCase("kirka")) {
			color = "ca84ff";
		} if (color.equalsIgnoreCase("brommel")) {
			color = "c7af27";
		} if (color.equalsIgnoreCase("pressa")) {
			color = "ff9d29";
		} if (color.equalsIgnoreCase("merone")) {
			color = "8df598";
		} if (color.equalsIgnoreCase("kukar")) {
			color = "ffff96";
		} if (color.equalsIgnoreCase("leopis")) {
			color = "31dfff";
		}
		try {
			rgb = Integer.parseInt(color, 16);
			bgra = 0xFF | ((rgb & 0xFF) << 24) | ((rgb & 0xFF00) << 8) | ((rgb & 0xFF0000) >>> 8);
		} catch (NumberFormatException e) {
			if (!color.equalsIgnoreCase("no")) {
				PacketSendUtility.sendMessage(admin, "[Dye] Can't understand: " + color);
				return;
			}
		} if (!admin.isGM()) {
			admin.getInventory().decreaseKinah(price);
		} for (Item targetItem : target.getEquipment().getEquippedItemsWithoutStigma()) {
			if (color.equals("no")) {
				targetItem.setItemColor(0);
			} else {
				targetItem.setItemColor(bgra);
			}
			ItemPacketService.updateItemAfterInfoChange(target, targetItem);
		}
		PacketSendUtility.broadcastPacket(target, new SM_UPDATE_PLAYER_APPEARANCE(target.getObjectId(), target.getEquipment().getEquippedForApparence()), true);
		target.getEquipment().setPersistentState(PersistentState.UPDATE_REQUIRED);
		if (target.getObjectId() != admin.getObjectId()) {
			PacketSendUtility.sendMessage(target, "You have been dyed by " + admin.getName() + "!");
		}
		PacketSendUtility.sendMessage(admin, "Dyed " + target.getName() + " successfully!");
	}
	
	@Override
	public void onFail(Player player, String message) {
	}
}