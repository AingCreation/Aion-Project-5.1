package playercommands;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.services.SkillLearnService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.chathandlers.PlayerCommand;

public class cmd_skills extends PlayerCommand
{
	public cmd_skills() {
		super("skills");
	}
	
	@Override
	public void execute(Player player, String... params) {
		SkillLearnService.addMissingSkills(player);
	}
	
	@Override
	public void onFail(Player player, String message) {
	    PacketSendUtility.sendMessage(player, "Syntax: .skills");
	}
}