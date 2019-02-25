package playercommands;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.services.SkillLearnService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.chathandlers.PlayerCommand;

/**
 * @author ATracer
 */
public class cmd_skills_craft extends PlayerCommand {

    public cmd_skills_craft() {
        super("craft");
    }

    @Override
    public void execute(Player player, String... params) {
        //SkillLearnService.addMissingSkills(player);
        //Skills craft custom server
        player.getSkillList().addSkillCraft(player, 30001, 499); // Vita
        player.getSkillList().addSkillCraft(player, 30002, 499); // Vita
        player.getSkillList().addSkillCraft(player, 30003, 499); // Ether
        player.getSkillList().addSkillCraft(player, 40001, 550); // Cuisine
        player.getSkillList().addSkillCraft(player, 40002, 550); // Armes
        player.getSkillList().addSkillCraft(player, 40003, 550); // Armure
        player.getSkillList().addSkillCraft(player, 40004, 550); // Couture
        player.getSkillList().addSkillCraft(player, 40007, 550); // Alchimie
        player.getSkillList().addSkillCraft(player, 40008, 550); // Artisanat
        player.getSkillList().addSkillCraft(player, 40009, 550); // Morph Substances
        player.getSkillList().addSkillCraft(player, 40010, 550); // Construction
        player.getSkillList().addSkillCraft(player, 40011, 200); // Aetherforging
        player.getSkillList().addSkillCraft(player, 40012, 1); // Coalescence
    }

    @Override
    public void onFail(Player player, String message) {
        PacketSendUtility.sendMessage(player, "Syntax : .craft");
    }
}
