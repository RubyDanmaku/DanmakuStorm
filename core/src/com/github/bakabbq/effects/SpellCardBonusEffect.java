package com.github.bakabbq.effects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.github.bakabbq.DanmakuGame;

/**
 * Created by LBQ on 9/21/14.
 */
public class SpellCardBonusEffect {
    static SpellCardBonusEffect instance;
    public int timer;
    public String numbers;
    public boolean isSpellCard;

    private SpellCardBonusEffect() {
        timer = -1;
    }

    public static SpellCardBonusEffect getInstance() {
        if (instance == null)
            instance = new SpellCardBonusEffect();
        return instance;
    }

    public void startDisplay(String numbers, boolean isSpellCard) {
        timer = 0;
        this.isSpellCard = isSpellCard;
        this.numbers = numbers;
    }

    public void render(SpriteBatch batch) {
        if (timer == -1)
            return;
        timer++;
        String start_str = isSpellCard ? "Spell Card Bonus: " : "Bonus: ";
        String final_str = start_str + numbers;
        Color c = batch.getColor();
        int alpha;
        if (timer <= 30)
            alpha = timer;
        else if (timer <= 90)
            alpha = 30;
        else if (timer <= 120)
            alpha = 30 - (timer - 90);
        else {
            alpha = 0;
            timer = -1;
        }
        Gdx.app.log("Alpha", "" + alpha / 30f);
        float a = getFont().getColor().a;
        Color ori_c = getFont().getColor();
        batch.setColor(c.r, c.g, c.b, alpha / 30f);
        getFont().setColor(ori_c.r, ori_c.g, ori_c.b, alpha / 30f);
        getFont().draw(batch, final_str, 40, 480 - 30);
        getFont().setColor(ori_c.r, ori_c.g, ori_c.b, a);
        batch.setColor(c.r, c.g, c.b, c.a);
    }

    public BitmapFont getFont() {
        return DanmakuGame.getInstance().fontBank.calisto;
    }
}
