package com.github.bakabbq.effects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.github.bakabbq.background.ThBackground;

/**
 * Created by LBQ on 9/21/14.
 */
public class BackgroundManager {
    int currentAlpha;
    ThBackground background;
    int timer;
    int state;

    public BackgroundManager() {
        timer = -1; // more like a state controller
        state = 0;
    }

    public void setBackground(ThBackground bg) {
        this.background = bg;
        state = 1;
    }

    public void fadeBackground() {
        state = -1;
    }

    public void render(SpriteBatch batch) {
        updateState();
        if (background == null)
            return;
        Color c = batch.getColor();
        batch.setColor(c.r, c.g, c.b, currentAlpha / 100f);
        background.update(batch);
        batch.setColor(c.r, c.g, c.b, c.a);
    }

    public void updateState() {
        if (state == 1) {
            currentAlpha++;
            if (currentAlpha >= 100)
                currentAlpha = 100;
        } else if (state == -1) {
            currentAlpha--;
            if (currentAlpha <= 0) {
                background = null;
                currentAlpha = 0;
                state = 0;
            }
        }
    }
}
