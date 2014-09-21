package com.github.bakabbq;

import com.github.bakabbq.spellcards.SpellCard;

/**
 * Created by LBQ on 9/21/14.
 * <p/>
 * Simple Spell Name aimed to replace the original hard to use spellname
 */
public class SimpleSpellname {
    private static SimpleSpellname instance;
    public SpellCard renderingSpellcard;
    public int timer;

    private SimpleSpellname() {
    }

    public static SimpleSpellname getInstance() {
        if (instance == null)
            instance = new SimpleSpellname();
        return instance;
    }

    public void startEffect(SpellCard sc) {
        renderingSpellcard = sc;

    }

    public void endEffect() {

    }

    public void update() {

    }

    public void onRender() {

    }
}
