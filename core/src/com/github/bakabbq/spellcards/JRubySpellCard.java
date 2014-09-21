package com.github.bakabbq.spellcards;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.github.bakabbq.BulletScript;
import com.github.bakabbq.JRubyClassLoader;
import com.github.bakabbq.screens.PracticeScreen;
import com.github.bakabbq.shooters.bosses.ThBoss;

/**
 * Created by LBQ on 8/20/14.
 */
public class JRubySpellCard extends SpellCard {
    BulletScript script;

    public JRubySpellCard(ThBoss owner, BulletScript script) {
        super(owner);
        this.script = script;
        this.script.setOwner(this.owner);
    }

    @Override
    public void mainLoop() {
        script.update();
        updateReload();
    }

    public void reload() {
        JRubyClassLoader.loadLibrary("BaseScript");
        PracticeScreen.getInstance().timer = PracticeScreen.getInstance().memoryTime;
        String filename = script.oriFilename;
        PracticeScreen.getInstance().clearBullets();
        this.script = BulletScript.load(filename);
        this.script.setOwner(this.owner);
    }

    void updateReload() {
        if (Gdx.input.isKeyPressed(Input.Keys.R)) {
            try {
                reload();
            } catch (Exception e) {
                Gdx.app.log("JRuby", "Error when reloading script", e);
            }
        }
    }
}
