package com.github.bakabbq.spellcards;

import com.github.bakabbq.shooters.bosses.ThBoss;

/**
 * Created by LBQ on 6/9/14.
 */
public class SpellCard {
    public String name = "";
    public float timeOut = 60f;
    public ThBoss owner;
    public boolean spell = false;
    public int timer;
    public int hp;
    public int clearTimer;
    public int bonus;
    public int spellcardBonus;
    public int oriBonus;

    public SpellCard(ThBoss owner) {
        this.owner = owner;
        this.timer = 0;
        this.hp = maxHp();

        updateAttributes();
        int multiplyer = spell ? 15 : 8;
        this.bonus = 80000 * multiplyer;
        this.oriBonus = bonus;
    }

    public void updateBonus() {
        if (timer <= 5 * 60)
            return;
        int decrease = (int) (0.6f * oriBonus / 3600);
        bonus -= decrease;
    }

    public int maxHp() {
        return 1600;
    }

    public float getTimeOut() {
        return timeOut - (float) timer / 60f;
    }

    public float getRoundedTimeOut() {
        return Math.round(getTimeOut() * 100) / 100;
    }

    public void updateAttributes() {
        // to be overwritten
    }

    public void update() {
        timer++;
        clearTimer++;
        mainLoop();
        updateBonus();
    }

    public void mainLoop() {
        //Needs to be overwritten
    }


    public float relX(float xPlus) {
        return owner.getX() + xPlus;
    }

    public float relY(float yPlus) {
        return owner.getY() + yPlus;
    }
}
