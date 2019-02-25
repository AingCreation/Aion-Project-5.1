package ai.instance.udasTemple;

import ai.AggressiveNpcAI2;
import com.aionemu.gameserver.ai2.AI2Actions;
import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.services.NpcShoutsService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Nexus BossScript
 *
 * @author Antraxx
 */
@AIName("nexus")
public class NexusAI2 extends AggressiveNpcAI2 {

    protected List<Integer> percents = new ArrayList<Integer>();
    private AtomicBoolean isHome = new AtomicBoolean(true);

    private void addPercent() {
        percents.clear();
        Collections.addAll(percents, new Integer[]{50});
    }

    private synchronized void checkPercentage(int hpPercentage) {
        for (Integer percent : percents) {
            if (hpPercentage <= percent) {
                switch (percent) {
                    case 50:
                        NpcShoutsService.getInstance().sendMsg(getOwner(), 1500026, getObjectId(), 0, 0);
                        AI2Actions.useSkill(this, 18605); // dmg buff
                        break;
                }
                percents.remove(percent);
                break;
            }
        }
    }

    @Override
    protected void handleSpawned() {
        super.handleSpawned();
        addPercent();
    }

    @Override
    protected void handleAttack(Creature creature) {
        super.handleAttack(creature);
        if (isHome.compareAndSet(true, false)) {
            NpcShoutsService.getInstance().sendMsg(getOwner(), 1500025, getObjectId(), 0, 0);
        }
        checkPercentage(getLifeStats().getHpPercentage());
    }

    @Override
    protected void handleBackHome() {
        addPercent();
        super.handleBackHome();
        isHome.set(true);
    }

    @Override
    protected void handleDied() {
        super.handleDied();
        percents.clear();
        NpcShoutsService.getInstance().sendMsg(getOwner(), 1500027, getObjectId(), 0, 0);
    }
}
