package com.github.bakabbq.shooters.bosses;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.utils.Array;
import com.github.bakabbq.IDanmakuWorld;
import com.github.bakabbq.effects.BossEffects;
import com.github.bakabbq.effects.SpellCardBonusEffect;
import com.github.bakabbq.screens.PracticeScreen;
import com.github.bakabbq.shooters.EnemyShooter;
import com.github.bakabbq.spellcards.SpellCard;

/**
 * Created by LBQ on 6/9/14.
 * <p/>
 * Seems like there is something called Animation in gdx.... .___.
 */
public class ThBoss extends EnemyShooter {
    public String name = "";
    public Array<SpellCard> spellCards = new Array();
    public Texture mainTexture = new Texture(Gdx.files.internal("bosses/stg5enm.png"));
    public boolean invincible;
    public int switchSpellTimer;

    public ThBoss(IDanmakuWorld ground) {
        super(ground);
        initSpellCards();
        initMainTexture();
        callUpdateSpellcardName();
        switchSpellTimer = 0;
    }

    public void onActive() {
    }

    ; // hook method to be overwritten on the jruby side

    public void onLeave() {
    }

    ; // hook method ...

    public int updateFrame() {
        return 10;
    }

    public void initMainTexture() {
        mainTexture = new Texture(Gdx.files.internal("bosses/stg5enm.png"));
    }


    public int getTextureY(int rId) {
        return rId * 64;
    }

    public int getTextureHeight(int rId) {
        if (rId <= 1)
            return 64;
        else
            return 64 + 16;
    }

    //ThBoss has a modified state:  state id: 0 => staying at the same position,  1 => moving left, 2 => moving right, 3 => casting
    /*
    ThBoss States
        0 => idling
        1 => moving left
        2 => moving right
        3 => casting
        4 => modified casting
     */
    @Override
    public void updateTexture() {
        int rId, cId;
        rId = cId = 0;
        boolean flip = false;

        //Yes, Magic Indeed
        switch (stateId) {
            case 0:
                rId = 0;
                cId = (stateTimer > 3 ? (0 + stateTimer % 4) : (stateTimer % 4));
                break;
            case 1:
                flip = true;
            case 2:
                rId = 1;
                cId = (stateTimer > 3 ? (2 + stateTimer % 2) : (stateTimer % 4));
                break;
            case 3:
                rId = 2;
                cId = (stateTimer > 3 ? (3) : (stateTimer % 4));
                break;
            case 4:
                rId = 3;
                cId = (stateTimer > 3 ? (3) : (stateTimer % 4));
                break;
        }
        this.initMainTexture();
        int textureCellWidth = this.mainTexture.getWidth() / 4;
        TextureRegion resultTexture = new TextureRegion(mainTexture, cId * textureCellWidth, getTextureY(rId), textureCellWidth, getTextureHeight(rId));
        if (flip)
            resultTexture.flip(true, false); // flip x, not y

        this.texture = resultTexture;
    }


    public void initSpellCards() {
        //use spellCards.add
    }

    public void smoothMovement(int forceX, int forceY, int damping) {
        this.enemyBody.setLinearDamping(damping);
        this.enemyBody.applyLinearImpulse(forceX, forceY, this.enemyBody.getLocalCenter().x, this.enemyBody.getLocalCenter().y, true);
    }


    @Override
    public void updateShoot() {
        if (notFinished())
            return;
        if (switchSpellTimer > 0) {
            switchSpellTimer--;
            return;
        }
        updateTimerOut();
        spellCards.get(0).update();
    }

    public void updateTimerOut() {

    }

    public void switchSpellTimerAfterSpellBreak() {
        switchSpellTimer = 180;
    }


    @Override
    public void onDeath() {
        PracticeScreen.getInstance().increaseScore(currentSpellcard().bonus);
        SpellCardBonusEffect.getInstance().startDisplay("" + currentSpellcard().bonus, currentSpellcard().spell);
        ((PracticeScreen) ground).onSpellClear();
        spellCards.removeIndex(0);
        callUpdateSpellcardName();

        switchSpellTimerAfterSpellBreak();
    }

    public SpellCard currentSpellcard() {
        if (spellCards.size == 0)
            return null;
        return spellCards.get(0);
    }

    public void callUpdateSpellcardName() {
        if (currentSpellcard() == null)
            return;
        BossEffects.getInstance().spellEffect.startSpell(currentSpellcard().name);
    }

    @Override
    public Shape getBodyShape() {
        CircleShape circle = new CircleShape();
        circle.setRadius(4f);
        return circle;
    }

    public int getScHp() {
        return currentSpellcard().hp;
    }

    public float getScHpRatio() {
        if (notFinished())
            return 0;
        return ((float) currentSpellcard().hp) / currentSpellcard().maxHp();
    }

    public boolean isInvincible() {
        if (switchSpellTimer > 0)
            return true;
        return false;
    }

    @Override
    public void receiveDamage(int dmg) {
        if (isInvincible())
            return;
        currentSpellcard().hp -= dmg;
        if (currentSpellcard().hp <= 0) {

            onDeath();
        }
    }

    public boolean notFinished() {
        return currentSpellcard() == null;
    }
}
