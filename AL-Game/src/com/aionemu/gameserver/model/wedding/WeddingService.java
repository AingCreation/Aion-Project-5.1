package com.aionemu.gameserver.model.wedding;

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.gameserver.cache.HTMLCache;
import com.aionemu.gameserver.configs.main.CustomConfig;
import com.aionemu.gameserver.configs.main.HTMLConfig;
import com.aionemu.gameserver.configs.main.WeddingsConfig;
import com.aionemu.gameserver.controllers.observer.ItemUseObserver;
import com.aionemu.gameserver.dao.WeddingDAO;
import com.aionemu.gameserver.model.ChatType;
import com.aionemu.gameserver.model.TaskId;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.RequestResponseHandler;
import com.aionemu.gameserver.network.aion.serverpackets.SM_MESSAGE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_PLAYER_INFO;
import com.aionemu.gameserver.network.aion.serverpackets.SM_QUESTION_WINDOW;
import com.aionemu.gameserver.network.aion.serverpackets.SM_USE_OBJECT;
import com.aionemu.gameserver.services.MuiService;
//import com.aionemu.gameserver.services.dreamergames.services.RVRBattleServices;
import com.aionemu.gameserver.services.teleport.TeleportService2;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.utils.Util;
import com.aionemu.gameserver.world.World;
import org.apache.commons.lang.time.DurationFormatUtils;

import java.sql.Timestamp;
import java.util.Iterator;
import java.util.List;

/**
 * @author: Ione542
 */
public class WeddingService {

    private final long timeWedding = 24;
    private final long timeTeleport = 1;
    private final int teleportDelay = 5000;
    private final int hidingDelay = 5000;
    private final String[] SuitItems = WeddingsConfig.WEDDINGS_SUITS.split(",");//110900135

    public static WeddingService getInstance() {
        return SingletonHolder.instance;
    }

    public static String getRealWeddingsName(String name) {
        int index = name.indexOf('\ue020');
        if (index == -1) {
            return name;
        }
        String s = name.substring(index + 1).intern();
        return name.replaceAll("\ue020" + s, "");
    }

    private boolean WeddingsItem(Player player1, Player player) {
        if (WeddingsConfig.WEDDINGS_SUIT_ENABLE) {
            for (String suit : SuitItems) {
                int suitId = Integer.parseInt(suit);
                if (player.getEquipment().getEquippedItemsByItemId(suitId).isEmpty()) {
                    PacketSendUtility.sendMessage(player, MuiService.getInstance().getMessage("WEDDING_NEED_ITEM", suitId));
                    return false;
                }
                if (player1.getEquipment().getEquippedItemsByItemId(suitId).isEmpty()) {
                    PacketSendUtility.sendMessage(player, MuiService.getInstance().getMessage("WEDDING_NEED_ITEM_PATRNER1", suitId));
                    PacketSendUtility.sendMessage(player1, MuiService.getInstance().getMessage("WEDDING_NEED_ITEM_PATRNER2", suitId));
                    return false;
                }
            }
        }
        return true;
    }

    private void deleteWeddingsItem(Player player) {
        if (!WeddingsConfig.WEDDINGS_SUIT_ENABLE) {
            return;
        }
        for (String suit : SuitItems) {
            int suitId = Integer.parseInt(suit);
            List<Item> items = player.getEquipment().getEquippedItemsByItemId(suitId);
            if (!items.isEmpty()) {
                for (Item item : items) {
                    player.getEquipment().unEquipItem(item.getObjectId(), item.getEquipmentSlot());
                    player.getInventory().decreaseByObjectId(item.getObjectId(), 1);
                }
            }
        }
    }

    public void text(Player player, String text) {
        player.getWedding().setText(text);
        DAOManager.getDAO(WeddingDAO.class).update(player.getWedding());
    }

    public void marry(Player player, Player partner) {
        deleteWeddingsItem(player);
        deleteWeddingsItem(partner);
        Wedding wedding = new Wedding(player.getObjectId(), partner.getObjectId(), null, partner.getWorldId(), null,
                new Timestamp(System.currentTimeMillis() + (timeWedding * 1000 * 60 * 60)), MuiService.getInstance().getMessage("WEDDING_HELLO_DARLING"), MuiService.getInstance().getMessage("WEDDING_HELLO_DARLING"), new Timestamp(System.currentTimeMillis()), partner.getName(), false);
        wedding.setPartner(partner);
        player.setWedding(wedding);
        Wedding weddingPartner = new Wedding(partner.getObjectId(), player.getObjectId(), null, player.getWorldId(), null,
                new Timestamp(System.currentTimeMillis() + (timeWedding * 1000 * 60 * 60)), MuiService.getInstance().getMessage("WEDDING_HELLO_DARLING"), MuiService.getInstance().getMessage("WEDDING_HELLO_DARLING"), new Timestamp(System.currentTimeMillis()), player.getName(), true);
        weddingPartner.setPartner(player);
        partner.setWedding(weddingPartner);
        DAOManager.getDAO(WeddingDAO.class).insertWedding(wedding);
        player.setPlayerSearch(null);
        player.setNewName(null);
        partner.setPlayerSearch(null);
        partner.setNewName(null);

        player.getWedding().setPartner(partner);
        partner.getWedding().setPartner(player);

        respawn(player);
        respawn(partner);
        if (HTMLConfig.ENABLE_HTML_WELCOME) {
            showInfoWedding(player);
            showInfoWedding(partner);
        }
        String message = "Wedding " + player.getName() + " - " + partner.getName() + "!";
        Iterator<Player> iter = World.getInstance().getPlayersIterator();
        while (iter.hasNext()) {
            PacketSendUtility.sendYellowMessageOnCenter(iter.next(), message);
        }
    }

    private void respawn(Player player) {
        PacketSendUtility.sendPacket(player, new SM_PLAYER_INFO(player, false));
        World.getInstance().despawn(player);
        World.getInstance().spawn(player);
    }

    public void unmarry(Player player) {
        int partnerId = player.getWedding().getPartnerId();
        String name = player.getWedding().getPartnerName();
        DAOManager.getDAO(WeddingDAO.class).removeWedding(player.getObjectId(), partnerId);
        DAOManager.getDAO(WeddingDAO.class).insertToLog(player.getWedding());
        if (player.getWedding().isOnline()) {
            Player partner = player.getWedding().getPartner();
            partner.setWedding(null);
            partner.setPlayerSearch(null);
            partner.setNewName(null);
            respawn(partner);
        }

        player.setWedding(null);
        player.setPlayerSearch(null);
        player.setNewName(null);
        respawn(player);

        String message = player.getName() + MuiService.getInstance().getMessage("WEDDING_DIVORCE_MSG") + name + "!";
        Iterator<Player> iter = World.getInstance().getPlayersIterator();
        while (iter.hasNext()) {
            PacketSendUtility.sendYellowMessageOnCenter(iter.next(), message);
        }
    }

    public boolean canMarry(Player player, String partnerName) {
        Player partner = World.getInstance().findPlayer(partnerName);
        if (partner == null) {
            PacketSendUtility.sendMessage(player, (new StringBuilder()).append(MuiService.getInstance().getMessage("CHAR")).append(partnerName).append(MuiService.getInstance().getMessage("NOT_ONLINE")).toString());
            return false;
        } else if (!timeWedding(player)) {
            PacketSendUtility.sendMessage(player, MuiService.getInstance().getMessage("WEDDING_NOT_AVAILBLE_PARTNER1", timeWedding) + DurationFormatUtils.formatDuration(player.getWedding().getTimeWedding().getTime() - System.currentTimeMillis(), "HHh mm mnt") + "");
            return false;
        } else if (!timeWedding(partner)) {
            PacketSendUtility.sendMessage(player, MuiService.getInstance().getMessage("WEDDING_NOT_AVAILBLE_PARTNER2", timeWedding) + DurationFormatUtils.formatDuration(player.getWedding().getTimeWedding().getTime() - System.currentTimeMillis(), "HHh mm mnt") + "");
            return false;
        } else if (player.isMarried()) {
            PacketSendUtility.sendMessage(player, MuiService.getInstance().getMessage("WEDDING_ALREADY_ENGAGED"));
            return false;
        } else if (partner.isMarried()) {
            PacketSendUtility.sendMessage(player, (new StringBuilder()).append(partnerName).append(MuiService.getInstance().getMessage("WEDDING_ALREADY_ENGAGED2")).toString());
            return false;
        } else if (player.getRace() != partner.getRace()) {
            PacketSendUtility.sendMessage(player, MuiService.getInstance().getMessage("WEDDING_ONLY_SAME_RACE"));
            return false;
        } else if (player == partner) {
            PacketSendUtility.sendMessage(player, MuiService.getInstance().getMessage("WEDDING_CANNOT_SELF"));
            return false;
        } else if (player.getGender() == partner.getGender() && !player.getGender().isFemale()) {
            PacketSendUtility.sendMessage(player, MuiService.getInstance().getMessage("WEDDING_CANNOT_SAME_GENDER"));
            return false;
        } else {
            return WeddingsItem(partner, player);
        }
    }

    public void showInfo(Player player) {
        //StringBuilder sb = new StringBuilder();
    }

    public void showInfoWedding(Player player) {
        //HTMLService.showHTML(player, getHTML(textInfo(player)));
    }

    public String getHTML(String message) {
        String context = HTMLCache.getInstance().getHTML("new_weddings.xhtml");
        context = context.replace("%message%", message == null ? " " : message);
        return context;
    }

    public boolean isMarriedTo(Player player, Player partner) {
        return (player.getWedding() != null && player.getWedding().getPartnerId() == partner.getObjectId());
    }

    public void summon(Player player) {
        if (!player.isMarried()) {
            PacketSendUtility.sendMessage(player, MuiService.getInstance().getMessage("WEDDING_NOT_WEDDING"));
            return;
        }
        /*if (player.isInInstance() && !RVRBattleServices.getInstance().isPlayerInRVR(player)) {
            PacketSendUtility.sendMessage(player, MuiService.getInstance().getMessage("WEDDING_COMMAND_SUMMON_CANNOT_USE"));
            return;
        }*/
        String name = player.getWedding().getPartnerName();
        Player partner = World.getInstance().findPlayer(name);
        if (partner == null) {
            PacketSendUtility.sendMessage(player, MuiService.getInstance().getMessage("WEDDING_PARTNER_NOT_ONLINE"));
            return;
        }
        if (partner.isInPrison()) {
            PacketSendUtility.sendMessage(player, MuiService.getInstance().getMessage("WEDDING_PARTNER_IN_PRISON"));
            return;
        }
        if (!timeTp(player)) {
            PacketSendUtility.sendMessage(player, MuiService.getInstance().getMessage("WEDDING_COMMAND_SUMMON_DELAY", timeTeleport) + DurationFormatUtils.formatDuration(player.getWedding().getTimeTp().getTime() - System.currentTimeMillis(), "HHч mmмин") + "");
            return;
        }
        PacketSendUtility.sendMessage(player, MuiService.getInstance().getMessage("WEDDING_COMMAND_WAITING_PARTNER") + name + " ...");
        RequestResponseHandler rh = new RequestResponseHandler(player) {
            @Override
            public void denyRequest(Creature player, Player partner) {
                PacketSendUtility.sendMessage((Player) player, MuiService.getInstance().getMessage("WEDDING_COMMAND_SUMMON_REFUSE", partner.getName()));
            }

            @Override
            public void acceptRequest(Creature player, Player partner) {
                teleport(partner, (Player) player);
                ((Player) player).getWedding().setTimeTp(new Timestamp(System.currentTimeMillis() + (timeTeleport * 1000 * 60 * 60)));
            }
        };
        partner.getResponseRequester().putRequest(902247, rh);
        PacketSendUtility.sendPacket(partner, new SM_QUESTION_WINDOW(902247, 0, 0, MuiService.getInstance().getMessage("WEDDING_COMMAND_SUMMON_REQUEST", player.getName())));
    }

    public void teleport(Player player) {
        if (!player.isMarried()) {
            PacketSendUtility.sendMessage(player, MuiService.getInstance().getMessage("WEDDING_NOT_WEDDING"));
            return;
        }
        String name = player.getWedding().getPartnerName();
        Player partner = World.getInstance().findPlayer(name);
        if (partner == null) {
            PacketSendUtility.sendMessage(player, MuiService.getInstance().getMessage("WEDDING_PARTNER_NOT_ONLINE"));
            return;
        }
        /*if (partner.isInInstance() && !RVRBattleServices.getInstance().isPlayerInRVR(partner)) {
            PacketSendUtility.sendMessage(player, MuiService.getInstance().getMessage("WEDDING_COMMAND_SUMMON_CANNOT_USE"));
            return;
        }*/
        if (partner.isInPrison()) {
            PacketSendUtility.sendMessage(player, MuiService.getInstance().getMessage("WEDDING_PARTNER_IN_PRISON"));
            return;
        }
        if (timeTp(player)) {
            player.getWedding().setTimeTp(new Timestamp(System.currentTimeMillis() + (timeTeleport * 1000 * 60 * 60)));
        } else {
            PacketSendUtility.sendMessage(player, MuiService.getInstance().getMessage("WEDDING_COMMAND_SUMMON_DELAY", timeTeleport) + DurationFormatUtils.formatDuration(player.getWedding().getTimeTp().getTime() - System.currentTimeMillis(), "mmмин") + "");
            return;
        }
        teleport(player, partner);
    }

    public boolean timeTp(Player admin) {
        if (!admin.isMarried() || admin.getWedding().getTimeTp() == null) {
            return true;
        }
        return (admin.getWedding().getTimeTp().getTime() - System.currentTimeMillis()) / 1000 <= 0;
    }

    public boolean timeWedding(Player admin) {
        if (!admin.isMarried() || admin.getWedding().getTimeWedding() == null) {
            return true;
        }
        return (admin.getWedding().getTimeWedding().getTime() - System.currentTimeMillis()) / 1000 <= 0;
    }

    public void teleport(final Player fromPlayer, Player toPlayer) {
        final int worldId = toPlayer.getWorldId();
        final int instId = toPlayer.getInstanceId();
        final float x = toPlayer.getX();
        final float y = toPlayer.getY();
        final float z = toPlayer.getZ();
        final byte h = toPlayer.getHeading();

        final ItemUseObserver observer = new ItemUseObserver() {

            @Override
            public void abort() {
                fromPlayer.getController().cancelTask(TaskId.ACTION_ITEM_NPC);
                fromPlayer.sendPacket(new SM_USE_OBJECT(fromPlayer.getObjectId(), 0, 0, 2));
                fromPlayer.getObserveController().removeObserver(this);
            }

        };

        fromPlayer.getObserveController().attach(observer);
        fromPlayer.sendPacket(new SM_USE_OBJECT(fromPlayer.getObjectId(), 0, teleportDelay, 1));
        fromPlayer.getController().addTask(TaskId.ACTION_ITEM_NPC, ThreadPoolManager.getInstance().schedule(new Runnable() {

            @Override
            public void run() {
                fromPlayer.sendPacket(new SM_USE_OBJECT(fromPlayer.getObjectId(), 0, 0, 2));
                TeleportService2.teleportTo(fromPlayer, worldId, instId, x, y, z, h);
                fromPlayer.getObserveController().removeObserver(observer);
            }

        }, teleportDelay));
    }

    public void hideWedding(final Player player) {
        final ItemUseObserver observer = new ItemUseObserver() {

            @Override
            public void abort() {
                player.getController().cancelTask(TaskId.ACTION_ITEM_NPC);
                player.sendPacket(new SM_USE_OBJECT(player.getObjectId(), 0, 0, 2));
                player.getObserveController().removeObserver(this);
            }

        };

        player.getObserveController().attach(observer);
        player.sendPacket(new SM_USE_OBJECT(player.getObjectId(), 0, hidingDelay, 1));
        player.getController().addTask(TaskId.ACTION_ITEM_NPC, ThreadPoolManager.getInstance().schedule(new Runnable() {

            @Override
            public void run() {
                player.setShowMarried(!player.isShowMarried());
                player.sendPacket(new SM_PLAYER_INFO(player, false));
                player.getController().stopProtectionActiveTask();
                player.getKnownList().clear();
                player.updateKnownlist();
            }

        }, hidingDelay));
    }

    public void marry(Player player, String... params) {
        if (params.length < 2) {
            PacketSendUtility.sendMessage(player, "Syntax: .wedding marry character_name");
            return;
        }
        final String partnerName =  Util.convertName(params[1]);

        if (!canMarry(player, partnerName)) {
            return;
        }
        RequestResponseHandler rh = new RequestResponseHandler(player) {
            @Override
            public void denyRequest(Creature player, Player partner) {
            }

            @Override
            public void acceptRequest(Creature player, Player partner) {
                startMarryTask((Player) player, partnerName);
            }
        };
        player.getResponseRequester().putRequest(902247, rh);
        PacketSendUtility.sendPacket(player, new SM_QUESTION_WINDOW(902247, 0, 0, MuiService.getInstance().getMessage("WEDDING_QUESTION", partnerName) + " ?"));
    }

    public void unMarry(Player player) {
        if (!player.isMarried()) {
            PacketSendUtility.sendMessage(player, MuiService.getInstance().getMessage("WEDDING_NOT_WEDDING"));
            return;
        }
        RequestResponseHandler rh = new RequestResponseHandler(player) {
            @Override
            public void denyRequest(Creature player, Player partner) {
            }

            @Override
            public void acceptRequest(Creature player, Player partner) {
                unmarry(partner);
            }
        };
        player.getResponseRequester().putRequest(902247, rh);
        PacketSendUtility.sendPacket(player, new SM_QUESTION_WINDOW(902247, 0, 0, MuiService.getInstance().getMessage("WEDDING_DIVORCE_QUESTION", player.getWedding().getPartnerName()) + "  ?"));
    }

    public void startMarryTask(Player player, String partnerName) {
        Player partner = World.getInstance().findPlayer(partnerName);
        PacketSendUtility.sendMessage(player, MuiService.getInstance().getMessage("WEDDING_COMMAND_WAITING_PARTNER") + partnerName + " ...");

        RequestResponseHandler rh = new RequestResponseHandler(player) {
            @Override
            public void denyRequest(Creature player, Player partner) {
                PacketSendUtility.sendMessage((Player) player, partner.getName() + MuiService.getInstance().getMessage("WEDDING_DENY_REQUEST"));
            }

            @Override
            public void acceptRequest(Creature player, Player partner) {
                Player p = (Player) player;
                marry(p, partner);
            }
        };
        partner.getResponseRequester().putRequest(902247, rh);
        PacketSendUtility.sendPacket(partner, new SM_QUESTION_WINDOW(902247, 0, 0, player.getName() + MuiService.getInstance().getMessage("WEDDING_ACCEPT_REQUEST")));
    }

    public boolean WeddingCommand(Player player, String text) {
        if (player.getWedding().isOnline()) {
            Player partner = player.getWedding().getPartner();
            PacketSendUtility.sendPacket(partner, new SM_MESSAGE(player, text, ChatType.WHISPER));
            PacketSendUtility.sendMessage(player, "Message sent your spouse");
        } else {
            PacketSendUtility.sendMessage(player, "Your partner is not online!");
        }
        return true;
    }

    public void getRenamePartner(Player player) {
        if (player.isMarried()) {
            if (player.getWedding().isOnline()) {
                Player partner = player.getWedding().getPartner();
                partner.getWedding().setPartnerName(player.getName());
                PacketSendUtility.sendPacket(partner, new SM_PLAYER_INFO(partner, false));
            }
        }
    }

    public void onEnterWorld(Player player) {
        if (player.isMarried()) {
            if (player.getWedding().isOnline()) {
                Player partner = player.getWedding().getPartner();
                PacketSendUtility.sendMessage(partner, "\ue020" + player.getName() + MuiService.getInstance().getMessage("WEDDING_PARTNER_HAS_LOGIN") + "\ue020");
                PacketSendUtility.sendMessage(player, "\ue020" + partner.getName() + MuiService.getInstance().getMessage("WEDDING_PARTNER_HAS_LOGIN") + "\ue020");
            }
            String text = player.getWedding().getPartnerText();
            if (text != null && !text.isEmpty()) {
                PacketSendUtility.sendYellowMessageOnCenter(player, "\ue020" + player.getWedding().getPartnerName() + ": " + text);
            }
        }
    }

    public void dofinal(Player player) {
        if (player.isMarried()) {
            if (player.getWedding().isOnline()) {
                Player partner = player.getWedding().getPartner();
                partner.getWedding().setPartner(null);
                PacketSendUtility.sendMessage(partner, "\ue020" + player.getName() + MuiService.getInstance().getMessage("WEDDING_PARTNER_HAS_LOGOUT"));
            }
            if (player.getWedding().isUpdate()) {
                DAOManager.getDAO(WeddingDAO.class).update(player.getWedding());
            }
        }
    }

    private static class SingletonHolder {
        protected static final WeddingService instance = new WeddingService();
    }

}
