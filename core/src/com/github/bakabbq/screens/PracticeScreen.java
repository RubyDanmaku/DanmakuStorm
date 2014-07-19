package com.github.bakabbq.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.github.bakabbq.BulletCollisionListener;
import com.github.bakabbq.DanmakuGame;
import com.github.bakabbq.DanmakuScene;
import com.github.bakabbq.IDanmakuWorld;
import com.github.bakabbq.audio.AudioBank;
import com.github.bakabbq.audio.MusicBox;
import com.github.bakabbq.audio.ThSe;
import com.github.bakabbq.bullets.Bullet;
import com.github.bakabbq.bullets.BulletDef;
import com.github.bakabbq.bullets.Laser;
import com.github.bakabbq.bullets.PlayerBullet;
import com.github.bakabbq.effects.ExplosionEffect;
import com.github.bakabbq.effects.ThEffect;
import com.github.bakabbq.items.ThItem;
import com.github.bakabbq.shooters.BulletShooter;
import com.github.bakabbq.shooters.EnemyShooter;
import com.github.bakabbq.shooters.bosses.ThBoss;
import com.github.bakabbq.shooters.bosses.testsanae.TestSanae;
import com.github.bakabbq.shooters.players.DanmakuOption;
import com.github.bakabbq.shooters.players.DanmakuPlayer;

/**
 * Created by LBQ on 7/14/14.
 *
 * PracticeScreen - The Main Screen of The Game
 *  initialized with a scene to practice
 */
public class PracticeScreen implements Screen, IDanmakuWorld{

    //the code definitely looks ugly down there
    DanmakuGame game; // game object, essential for rendering
    DanmakuScene scene; // the scene, tool class


    Array<Bullet> bullets; // array containing all bullets
    Array<Laser> lasers; // lasers

    Array<ThBoss> bosses; // boss array
    Array<EnemyShooter> enemies; //enemies, for possible slave spawns
    Array<BulletShooter> shooters;

    Array<ThEffect> effects; //effects array

    Array<DanmakuPlayer> players;


    //Ui Components
    TextureRegion menuBackground;

    //Audio Components
    MusicBox musicBox;



    //Box2d stuffs
    public World world;
    public BulletCollisionListener collisionListener;

    public PracticeScreen(DanmakuGame game, DanmakuScene scene){
        this.game = game;
        this.scene = scene;

        initObjectContainers();
        loadUiComponents();
        initAudioComponents();
        initScene();
        setupZoom();
    }

    void initObjectContainers(){
        bullets = new Array<Bullet>();
        lasers = new Array<Laser>();

        bosses = new Array<ThBoss>();
        enemies = new Array<EnemyShooter>();

        effects = new Array<ThEffect>();

        players = new Array<DanmakuPlayer>();

        shooters = new Array<BulletShooter>();
    }

    void loadUiComponents(){
        menuBackground = new TextureRegion(new Texture(Gdx.files.internal("menus/front.png")));
    }

    void initScene(){
        world = new World(new Vector2(0,0), true);
        collisionListener = new BulletCollisionListener();
        world.setContactListener(collisionListener);

        ThBoss boss = new TestSanae(this);
        boss.setX(237/10);
        boss.setY(62);
        bosses.add(boss);

        DanmakuPlayer player = new DanmakuPlayer(this);
        player.setPos(237/10,30/5);
        players.add(player);
    }

    void initAudioComponents(){
        musicBox = new MusicBox(this);
    }

    /*
        Box2d can never have quick objects, so i HAVE TO USE A STUPID WAY - use the camera to zoom, i really hate this...
     */
    void setupZoom(){
        game.camera.zoom = 0.2f;

        // Magic
        game.camera.position.x -= 320 - 320 / 5 + 128 / 5 - 10;
        game.camera.position.y -= 240 - 240 / 5 + (480 - 462) / 5;

        game.camera.update();
    }

    /**
     * Called when the screen should render itself.
     *
     * @param delta The time in seconds since the last render.
     */
    @Override
    public void render(float delta) {
        game.batch.begin();
        renderShooters();
        renderEffects();
        renderBullets();
        game.batch.end();
        game.uiBatch.begin();
        renderUI();
        game.uiBatch.end();
        update();
        world.step(1 / 60f, 6, 2);
    }


    void renderShooters(){

        //there should really be some kind of method called getBatch, shouldn't there=-=
        for (EnemyShooter singleEnemy : enemies) {
            game.batch.draw(
                    singleEnemy.texture,
                    singleEnemy.getX() + 5,
                    singleEnemy.getY() + 6,
                    0,
                    0,
                    singleEnemy.texture.getRegionWidth(),
                    singleEnemy.texture.getRegionHeight(),
                    0.2f,
                    0.2f,
                    0
            );
            singleEnemy.update();
        }

        for (DanmakuPlayer singlePlayer : players){
            game.batch.draw(
                    singlePlayer.getTexture(),
                    singlePlayer.getX() - 8,
                    singlePlayer.getY() - 16,
                    singlePlayer.getTexture().getRegionWidth() / 2,
                    singlePlayer.getTexture().getRegionHeight() / 2,
                    singlePlayer.getTexture().getRegionWidth(),
                    singlePlayer.getTexture().getRegionHeight(),
                    0.2f,
                    0.2f,
                    0

            );

            for (DanmakuOption singleOption : singlePlayer.options) {
                game.batch.draw(
                        singleOption.texture,
                        singleOption.x,
                        singleOption.y,
                        singleOption.texture.getRegionWidth() / 2,
                        singleOption.texture.getRegionHeight() / 2,
                        singleOption.texture.getRegionWidth(),
                        singleOption.texture.getRegionHeight(),
                        0.2f,
                        0.2f,
                        singleOption.angle
                );
            }
        }



        for (ThBoss singleBoss : bosses) {
            game.batch.draw(
                    singleBoss.texture,
                    singleBoss.getX() + 2,
                    singleBoss.getY() + 3,
                    0,
                    0,
                    singleBoss.getTexture().getRegionWidth(),
                    singleBoss.getTexture().getRegionHeight(),
                    0.2f,
                    0.2f,
                    0
            );
        }
    }

    void renderEffects(){
        Color c;
        c = game.batch.getColor();
        for (ThEffect singleEffect : effects) {
            //Gdx.app.log("Effect", "RegionWidth " + singleEffect.texture.getRegionWidth() + " RegionHeight " + singleEffect.texture.getRegionHeight());
            game.batch.setColor(c.r, c.g, c.b, singleEffect.opacity);
            game.batch.draw(
                    singleEffect.texture,
                    singleEffect.x + singleEffect.getXOffset() ,
                    singleEffect.y + singleEffect.getYOffset(),
                    singleEffect.texture.getRegionWidth() / 2 * singleEffect.zoomX,
                    singleEffect.texture.getRegionHeight() / 2 * singleEffect.zoomY,
                    singleEffect.texture.getRegionWidth() * singleEffect.zoomX,
                    singleEffect.texture.getRegionHeight() * singleEffect.zoomY,
                    0.2f,
                    0.2f,
                    singleEffect.angle
            );
        }
        game.batch.setColor(c.r, c.g, c.b, 1);
    }

    void renderBullets(){
        Color c = game.batch.getColor();
        for (Bullet singleBullet : bullets) {

            game.batch.setColor(c.r, c.g, c.b, ((float) singleBullet.getAlpha()) / 255f);
            game.batch.draw(
                    singleBullet.getTexture(),
                    singleBullet.getX() + singleBullet.getXOffset(),
                    singleBullet.getY() + singleBullet.getYOffset(),
                    singleBullet.getOriginX(),
                    singleBullet.getOriginY(),
                    singleBullet.getTexture().getRegionWidth(),
                    singleBullet.getTexture().getRegionHeight(),
                    0.2f,
                    0.2f,
                    singleBullet.body.getAngle() - 180 + singleBullet.getAngleFix()
            );


        }
        game.batch.setColor(c.r, c.g, c.b, 1);
    }

    void renderUI(){
        game.uiBatch.draw(menuBackground,0,0);
    }



    // Called Once Per Frame
    public void update(){
        for (Bullet singleBullet : bullets) {
            singleBullet.update();
            if (singleBullet.destroyFlag) {
                singleBullet.dispose();
                bullets.removeValue(singleBullet, true);
            }
        }

        for (EnemyShooter singleEnemy : enemies) {
            if (singleEnemy.dead) {
                //addExplosion(singleEnemy.getX(), singleEnemy.getY());
                singleEnemy.dispose();
                enemies.removeValue(singleEnemy, true);

            }
        }

        for (ThBoss singleBoss : bosses) {
            singleBoss.update();
        }

        for (DanmakuPlayer singlePlayer : players){
            singlePlayer.update();
            for (DanmakuOption singleOption : singlePlayer.options) {
                singleOption.update();
            }
        }

        for (ThEffect singleEffect : effects) {
            singleEffect.update();
            if (singleEffect.disposeFlag)
                effects.removeValue(singleEffect, true);
        }

    }

    /**
     * @param width
     * @param height
     */
    @Override
    public void resize(int width, int height) {

    }

    /**
     */
    @Override
    public void show() {

    }

    /**
     */
    @Override
    public void hide() {

    }

    /**
     */
    @Override
    public void pause() {

    }

    /**
     */
    @Override
    public void resume() {

    }

    /**
     * Called when this screen should release all resources.
     */
    @Override
    public void dispose() {

    }

    public void addItem(float x, float y) {
        return;
    }

    public BulletShooter addShooter(BulletShooter bs, float x, float y) {
        bs.x = x;
        bs.y = y;
        shooters.add(bs);
        return bs;
    }

    public EnemyShooter addEnemy(EnemyShooter es) {
        enemies.add(es);
        return es;
    }

    public EnemyShooter addEnemy(EnemyShooter es, float x, float y) {
        es.setX(x);
        es.setY(y);
        enemies.add(es);
        return es;
    }

    public ThBoss spawnBoss(ThBoss boss, float x, float y) {
        boss.setX(x);
        boss.setY(y);
        bosses.add(boss);
        return boss;
    }

    public void destroyBullet(Bullet b) {
        world.destroyBody(b.body);
        bullets.removeValue(b, false);
    }

    public void addEffect(ThEffect effect, float x, float y) {
        effect.x = x;
        effect.y = y;
        effects.add(effect);
    }

    public void clearEffect(ThEffect effect) {
        for (ThEffect singleEffect : effects) {
            if (singleEffect.getClass() == effect.getClass()) {
                singleEffect.enterDispose();
            }
        }
    }

    public void addLaser(Laser laser){
        lasers.add(laser);
    }

    public Bullet addBullet(BulletDef bd, float x, float y, float angle) {
        Bullet bullet;
        bullet = new Bullet(bd, world, x, y, angle);
        bullet.danmakuWorld = this;
        //addEffect(new BulletCreationEffect(), x, y);
        bullets.add(bullet);
        return bullet;
    }

    public PlayerBullet addPlayerBullet(BulletDef bd, float x, float y, float angle) {
        PlayerBullet bullet;
        bullet = new PlayerBullet(bd, world, x, y, angle);
        musicBox.playSe(AudioBank.bulletAppear);
        bullets.add(bullet);
        return bullet;
    }

    public BulletShooter addShooter(BulletShooter bs) {
        shooters.add(bs);
        return bs;
    }



    @Override
    public World getWorld() {
        return world;
    }

    public DanmakuPlayer getPlayer(){
        //TODO
        return players.first();
    }


}
