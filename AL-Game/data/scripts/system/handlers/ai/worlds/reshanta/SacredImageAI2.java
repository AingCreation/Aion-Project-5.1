package ai.worlds.reshanta;

import ai.NoActionAI2;
import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.skillengine.SkillEngine;
import com.aionemu.gameserver.utils.MathUtil;

@AIName("sacred_image") //258280, 258281, 258313, 258314
public class SacredImageAI2 extends NoActionAI2 {

    @Override
    protected void handleCreatureSee(Creature creature) {
        checkDistance(creature);
    }

    @Override
    protected void handleCreatureMoved(Creature creature) {
        checkDistance(creature);
    }

    private void checkDistance(Creature creature) {
        int spellid = getOwner().getNpcId() == 258281 ? 20373 : 20374;
        if (creature instanceof Player) {
            Player player = (Player) creature;
            if ((player.getRace().equals(Race.ASMODIANS) && getOwner().getNpcId() == 258281) || (player.getRace().equals(Race.ELYOS) && getOwner().getNpcId() == 258280)) {
                return;
            }
            if (MathUtil.isIn3dRangeLimited(getOwner(), creature, 0, 35)) {
                SkillEngine.getInstance().getSkill(getOwner(), spellid, 75, player).useNoAnimationSkill();
            }
        }
    }

    @Override
    public int modifyDamage(int damage) {
        return 1;
    }
}