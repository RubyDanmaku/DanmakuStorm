package com.github.bakabbq.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.github.bakabbq.DanmakuGame;

/**
 * Created by LBQ on 7/12/14.
 */
public class TitleScreen implements Screen{
    DanmakuGame game;

    Texture background;
    Texture praying;
    Texture particle;

    int timer;


    public TitleScreen(DanmakuGame game){
        this.game = game;

        timer = 0;
        loadBasicAssets();
        loadAllAssets();
    }

    //Only load the "girls are praying" stuffs
    public void loadBasicAssets(){
        background = new Texture(Gdx.files.internal("title/background.png"));
        praying = new Texture(Gdx.files.internal("title/loading.png"));
    }

    public void loadAllAssets(){

    }

    @Override
    public void render(float delta) {
        timer ++;
        getBatch().begin();
        getBatch().draw(background,0,0);

        Color c = getBatch().getColor();
        getBatch().setColor(c.r, c.g, c.b, (float) (180 + Math.abs((Math.sin((float) timer/30f) * 75)) / 255f));
        //Deal with girls are praying
        getBatch().draw(praying,0,0);
        getBatch().setColor(c);

        getBatch().end();
    }

    public SpriteBatch getBatch(){
        return game.batch;
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void show() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {

    }
}