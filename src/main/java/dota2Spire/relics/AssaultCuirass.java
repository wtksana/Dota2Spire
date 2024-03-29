package dota2Spire.relics;

import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.VulnerablePower;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import dota2Spire.Dota2Spire;
import dota2Spire.util.StringUtil;
import dota2Spire.util.TextureLoader;

import static dota2Spire.Dota2Spire.makeRelicPath;

/**
 * 强袭
 * 每次获得护甲时额外获得2点护甲
 * 获得护甲时给与敌方1层易伤，cd 3回合
 */
public class AssaultCuirass extends CustomRelic {
    // ID, images, text.
    public static final String ID = Dota2Spire.makeID("AssaultCuirass");
    private static final Texture IMG = TextureLoader.getTexture(makeRelicPath("AssaultCuirass.png"));
    private static final int _CD = 3;
    private static final int _VulnerableStack = 1;
    private static final int _Block = 2;

    public AssaultCuirass() {
        super(ID, IMG, RelicTier.RARE, LandingSound.MAGICAL);
    }

    @Override
    public void onEquip() {
        setCount(0);
    }

    private void setCount(int count) {
        if (count <= 0) {
            this.counter = -1;
            beginLongPulse();
        } else {
            this.counter = count;
            stopPulse();
        }
    }

    @Override
    public int onPlayerGainedBlock(float blockAmount) {
        if (blockAmount > 0.0F) {
            blockAmount += _Block;
            addToBot(new RelicAboveCreatureAction(AbstractDungeon.player, this));
            if (this.counter <= 0) {
                for (AbstractMonster m : AbstractDungeon.getCurrRoom().monsters.monsters) {
                    addToBot(new ApplyPowerAction(m, AbstractDungeon.player, new VulnerablePower(m, _VulnerableStack, false), _VulnerableStack));
                }
                setCount(_CD + 1);
            }
            this.flash();
        }
        return super.onPlayerGainedBlock(blockAmount);
    }

    @Override
    public void atTurnStart() {
        setCount(this.counter - 1);
    }

    @Override
    public String getUpdatedDescription() {
        return StringUtil.format(DESCRIPTIONS[0], _Block, _VulnerableStack, _CD);
    }

    @Override
    public AbstractRelic makeCopy() {
        return new AssaultCuirass();
    }
}
