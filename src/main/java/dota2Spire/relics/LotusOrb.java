package dota2Spire.relics;

import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;
import com.evacipated.cardcrawl.mod.stslib.relics.OnReceivePowerRelic;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.VulnerablePower;
import com.megacrit.cardcrawl.powers.WeakPower;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import dota2Spire.Dota2Spire;
import dota2Spire.util.StringUtil;
import dota2Spire.util.TextureLoader;

import static dota2Spire.Dota2Spire.makeRelicPath;

/**
 * 莲花
 * 本回合获得虚弱或易伤时，也给敌人同样的虚弱或易伤
 * cd 4回合
 */
public class LotusOrb extends CustomRelic implements OnReceivePowerRelic {
    // ID, images, text.
    public static final String ID = Dota2Spire.makeID("LotusOrb");
    private static final Texture IMG = TextureLoader.getTexture(makeRelicPath("LotusOrb.png"));
    private boolean onEffect = false;
    private static final int _CD = 3;

    public LotusOrb() {
        super(ID, IMG, RelicTier.COMMON, LandingSound.MAGICAL);
    }

    @Override
    public void onEquip() {
        setCount(0);
    }

    @Override
    public boolean onReceivePower(AbstractPower power, AbstractCreature source) {
        if (onEffect && source != null && source != AbstractDungeon.player) {
            if (power.ID.equals("Weakened")) {
                flash();
                addToBot(new RelicAboveCreatureAction(AbstractDungeon.player, this));
                addToTop(new ApplyPowerAction(source, AbstractDungeon.player, new WeakPower(source, power.amount, true)));
            }
            if (power.ID.equals("Vulnerable")) {
                flash();
                addToBot(new RelicAboveCreatureAction(AbstractDungeon.player, this));
                addToTop(new ApplyPowerAction(source, AbstractDungeon.player, new VulnerablePower(source, power.amount, true)));
            }
        }
        return true;
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
    public void atTurnStart() {
        if (onEffect) {
            onEffect = false;
            setCount(_CD);
        } else {
            if (this.counter > 0) {
                setCount(this.counter - 1);
            } else {
                onEffect = true;
            }
        }
    }

    @Override
    public void onPlayerEndTurn() {
        if (this.counter <= 0) {
            onEffect = true;
        }
    }

    @Override
    public String getUpdatedDescription() {
        return StringUtil.format(DESCRIPTIONS[0], _CD);
    }

    @Override
    public AbstractRelic makeCopy() {
        return new LotusOrb();
    }

}
