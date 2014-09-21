package com.github.bakabbq.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.ModelLoader;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.loader.ObjLoader;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.github.bakabbq.*;
import com.github.bakabbq.audio.MusicBox;
import com.github.bakabbq.background.DecalBackground;
import com.github.bakabbq.background.ThBackground;
import com.github.bakabbq.bullets.Bullet;
import com.github.bakabbq.bullets.BulletDef;
import com.github.bakabbq.bullets.Laser;
import com.github.bakabbq.bullets.PlayerBullet;
import com.github.bakabbq.datas.BackgroundRegistry;
import com.github.bakabbq.datas.FontBank;
import com.github.bakabbq.datas.ScoreBoard;
import com.github.bakabbq.datas.ScreenshotTaker;
import com.github.bakabbq.effects.*;
import com.github.bakabbq.screens.dayselection.StageData;
import com.github.bakabbq.shooters.BulletShooter;
import com.github.bakabbq.shooters.EnemyShooter;
import com.github.bakabbq.shooters.bosses.JsonBoss;
import com.github.bakabbq.shooters.bosses.ThBoss;
import com.github.bakabbq.shooters.bosses.testsanae.TestSanae;
import com.github.bakabbq.shooters.bosses.testsanae.TestSpellCard;
import com.github.bakabbq.shooters.players.DanmakuOption;
import com.github.bakabbq.shooters.players.DanmakuPlayer;
import com.github.bakabbq.spellcards.JRubySpellCard;

import java.util.Date;

/**
 * Created by LBQ on 7/14/14.
 *
 * PracticeScreen - The Main Screen of The Game
 * initialized with a scene to practice
 *
 *
 * Now uses a singleton pattern to make it far more simple =-=
 */
public class PracticeScreen implements Screen, IDanmakuWorld {

    private static PracticeScreen instance;
    public Array<Bullet> bullets; // array containing all bullets
    //Box2d stuffs
    public World world;
    public BulletCollisionListener collisionListener;
    public Environment environment;
    public PerspectiveCamera cam;
    public CameraInputController camController;
    public ModelBatch modelBatch;
    public Model model;
    public int memoryTime; // used to determine

    public BackgroundManager bgManager;
    public BackgroundRegistry backgroundRegistry;
    /**
     * Called when the screen should render itself.
     *
     * @param delta The time in seconds since the last render.
     */

    public int timer;
    //the code definitely looks ugly down there
    DanmakuGame game; // game object, essential for rendering
    DanmakuScene scene; // the scene, tool class
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
    //Background SpriteBatch
    SpriteBatch backgroundBatch;
    AssetManager manager;
    //Background stuff
    DecalBackground background;
    //Particle Effect Stuffs
    ParticleEffectPool particleEffectPool;
    Array<ParticleEffectPool.PooledEffect> pooledEffects = new Array();
    //BossEffect - yeah.. stupid ThEffect
    BossEffects bossEffects;
    Date gameTimer;
    MainUiRenderer uiRenderer;
    DebugValues debugValues;
    ThBoss boss;
    Array<SlaveParticleEffect> slaveParticles = new Array<SlaveParticleEffect>();
    int currentSpellIndex;
    ScoreBoard sb = new ScoreBoard();
    Array<Body> toDispose = new Array<Body>();
    private boolean paused;

    private PracticeScreen() {
        manager = new AssetManager();
        this.game = DanmakuGame.getInstance();
        this.scene = new DanmakuScene(TestSanae.class, TestSpellCard.class, ThBackground.class);
        JRubyClassLoader.init();
        JRubyClassLoader.loadLibrary("BaseScript");


        backgroundBatch = new SpriteBatch();
        bgManager = new BackgroundManager();
        backgroundRegistry = new BackgroundRegistry();
        backgroundRegistry.register("default", new ThBackground(this));
        initUiContainer();
        initObjectContainers();
        loadUiComponents();
        initAudioComponents();
        initScene();
        initParticle();
        setupZoom();
        onSpellClear();


        cam = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        cam.position.set(60f, -60f, 240f);
        cam.lookAt(60, 0, 0);
        cam.near = 10f;
        cam.far = 600f;
        cam.update();

        ModelLoader loader;
        loader = new ObjLoader();

        camController = new CameraInputController(cam);
        Gdx.input.setInputProcessor(camController);

    }

    public static PracticeScreen getInstance() {
        if (instance == null)
            instance = new PracticeScreen();
        return instance;
    }

    void initUiContainer() {
        uiRenderer = new MainUiRenderer();
    }

    public PracticeData getPracticeData() {
        return PracticeData.getInstance();
    }

    public StageData getStageData() {
        return getPracticeData().stageData;
    }

    void initParticle() {
        ParticleEffect slaveEffect = new ParticleEffect();
        slaveEffect.load(Gdx.files.internal("particles/bullet_slave"), Gdx.files.internal("particles"));
        particleEffectPool = new ParticleEffectPool(slaveEffect, 1, 2);
    }

    void initObjectContainers() {
        bullets = new Array<Bullet>();
        lasers = new Array<Laser>();

        debugValues = new DebugValues();

        bosses = new Array<ThBoss>();
        enemies = new Array<EnemyShooter>();

        effects = new Array<ThEffect>();

        players = new Array<DanmakuPlayer>();

        shooters = new Array<BulletShooter>();
    }

    void loadUiComponents() {
        menuBackground = new TextureRegion(new Texture(Gdx.files.internal("menus/front.png")));
    }

    void initScene() {
        world = new World(new Vector2(0, 0), true);
        collisionListener = new BulletCollisionListener();
        world.setContactListener(collisionListener);

        background = new DecalBackground(this);
        bossEffects = BossEffects.getInstance();
        DanmakuPlayer player = new DanmakuPlayer(this);
        player.setPos(237 / 10, 30 / 5);
        players.add(player);

    }

    public ScoreBoard getStageScoreboard() {
        return sb;
    }

    public void increaseScore(int x) {
        getStageScoreboard().increaseScore(x);
    }

    public String getScoreText() {
        return getStageScoreboard().scoreText;
    }

    public String getHiScoreText() {
        return getStageScoreboard().hiScoreText;
    }

    void initAudioComponents() {
        musicBox = new MusicBox(this);
    }

    /*
        Box2d can never have quick objects, so i HAVE TO USE A STUPID WAY - use the camera to zoom, i really hate this...
     */
    void setupZoom() {
        game.camera.zoom = 0.2f;
        // Magic
        game.camera.position.x -= 271;
        game.camera.position.y -= 195;

        game.camera.update();
    }

    @Override
    public void render(float delta) {
        long start;
        camController.update();
        background.setCam(cam);
        background.decalLoop();

        start = System.currentTimeMillis();

        backgroundBatch.begin();

        //background.update(backgroundBatch);
        //bgManager.render(backgroundBatch);
        for (ThBoss singleBoss : bosses) {
            bossEffects.update(singleBoss, backgroundBatch);
        }
        renderParticles(delta);

        backgroundBatch.end();

        game.batch.begin();

        renderShooters();
        renderEffects();
        renderBullets();


        game.batch.end();

        backgroundBatch.begin();
        renderSlaveParticles();
        backgroundBatch.end();

        for (ThBoss singleBoss : bosses) {
            bossEffects.drawHpBar(singleBoss, game.uiBatch);
        }
        game.uiBatch.begin();
        renderUI();
        game.uiBatch.end();

        debugValues.renderInterval = System.currentTimeMillis() - start;
        start = System.currentTimeMillis();
        update();
        debugValues.updateInterval = System.currentTimeMillis() - start;


        timer++;
        if (timer % 3 == 0)
            increaseScore(10);
    }

    public void onPlayerBeingHit() {
        timer += 3600 / 3; // 20 minutes punishment
    }

    void renderSlaveParticles(){
        for (SlaveParticleEffect singleParticle : slaveParticles) {
            singleParticle.particle.draw(backgroundBatch, 0.9f);
        }
    }

    void renderShooters() {

        //there should really be some kind of method called getBatch, shouldn't there=-=
        for (EnemyShooter singleEnemy : enemies) {
            if (singleEnemy.isSlave()) {
                slaveParticles.add(new SlaveParticleEffect(singleEnemy.getX() + 5, singleEnemy.getY() + 6));
            } else {
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

            }
            singleEnemy.update();
        }

        for (DanmakuPlayer singlePlayer : players) {
            if (singlePlayer.isInvincible() && singlePlayer.invincibleTimer % 20 <= 10) {

            } else {
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

    void renderEffects() {
        Color c;
        c = game.batch.getColor();
        for (ThEffect singleEffect : effects) {
            //Gdx.app.log("Effect", "RegionWidth " + singleEffect.texture.getRegionWidth() + " RegionHeight " + singleEffect.texture.getRegionHeight());
            game.batch.setColor(c.r, c.g, c.b, singleEffect.opacity);
            game.batch.draw(
                    singleEffect.texture,
                    singleEffect.x + singleEffect.getXOffset(),
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

    void renderBullets() {
        Color c = game.batch.getColor();
        for (Bullet singleBullet : bullets) {

            game.batch.setColor(c.r, c.g, c.b, ((float) singleBullet.getAlpha()) / 255f);
            renderSingleBullet(singleBullet);
        }
        game.batch.setColor(c.r, c.g, c.b, 1);
    }

    public void renderSingleBullet(Bullet singleBullet) {
        if (singleBullet.hasCreationEffect()) {
            game.batch.draw(
                    singleBullet.getCreationTexture(),
                    singleBullet.getX() + singleBullet.getXOffset() - 8,
                    singleBullet.getY() + singleBullet.getYOffset() - 8,
                    singleBullet.getOriginX() * 2,
                    singleBullet.getOriginY() * 2,
                    32,
                    32,
                    0.2f * ((5 - singleBullet.timer / 5) * 0.1f + 0.5f),
                    0.2f * ((5 - singleBullet.timer / 5) * 0.1f + 0.5f),
                    0
            );
        } else {
            //game.batch.setColor(c.r, c.g, c.b, ((float) singleBullet.getAlpha()) / 255f);
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
    }

    void renderParticles(float delta) {
        for (ParticleEffectPool.PooledEffect singleEffect : pooledEffects) {
            singleEffect.draw(backgroundBatch, delta);
            if (singleEffect.isComplete()) {
                singleEffect.free();
                pooledEffects.removeValue(singleEffect, true);
            }
        }
    }

    void renderUI() {
        uiRenderer.render(game.uiBatch);
        SpellCardBonusEffect.getInstance().render(game.uiBatch);
        bossEffects.spellEffect.update();
        int graze = getPlayer().grazeCnt;
        getFontBank().calisto.draw(game.uiBatch, "" + graze, 515, 480 - 205);
        getFontBank().calisto.draw(game.uiBatch, getScoreText(), 515, 480 - 170);
        getFontBank().calisto.draw(game.uiBatch, getHiScoreText(), 515, 480 - 150);
        if (bosses.get(0).currentSpellcard() != null)
            getFontBank().calisto.draw(game.uiBatch, "Bonus: " + bosses.get(0).currentSpellcard().bonus, 460, 480 - 125);
        renderTime();
        renderFps();
    }

    void renderTime() {
        int minutes = (timer / 60);
        int trueMinutes = (timer / 60) % 60;
        int hours = minutes / 60;
        int trueHours = hours + 7;
        String timeText = String.format("%02d", trueHours) + ":" + String.format("%02d", trueMinutes);
        getFontBank().calistoBig.draw(game.uiBatch, timeText, 465, 480 - 60);
    }

    void renderFps() {
        int fps = Gdx.graphics.getFramesPerSecond();
        String debug = "Fps: " + fps + "\n" +
                "Bullets: " + bullets.size + "\n" +
                "RenderInterval: " + debugValues.renderInterval + "\n" +
                "UpdateInterval: " + debugValues.updateInterval + "\n" +
                "OverFlow?: " + (debugValues.renderInterval + debugValues.updateInterval > 16);
        //getFontBank().arial.draw(game.uiBatch,debug, 450 , 80);
        getFontBank().arial.drawMultiLine(game.uiBatch, debug, 450, 160);
    }

    public FontBank getFontBank() {
        return game.fontBank;
    }

    // Called Once Per Frame
    public void update() {
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

        for (DanmakuPlayer singlePlayer : players) {
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

        for (SlaveParticleEffect singleParticle : slaveParticles) {
            singleParticle.update();
            if (singleParticle.canDispose())
                slaveParticles.removeValue(singleParticle, true);
        }
        updateSpellTimeOut();
        removeGarbageBullets();


        for (Body body : toDispose)
            body.setTransform(100, 100, 0);
        world.step(1 / 60f, 6, 2);


        //updateScreenshot();

    }

    public void switchToNextSpellcard() {
        Gdx.app.log("Test", "2");
        // when the next spell has the same owner
        //bosses.get(0).spellCards.clear();
        bosses.get(0).spellCards.add(new JRubySpellCard(bosses.get(0), BulletScript.load(getStageData().scripts.get(currentSpellIndex).scriptFilename())));
        Gdx.app.log("Test", "3");
        //bosses.get(0).spellCards.removeIndex(0);
        Gdx.app.log("Test", "4");
        bosses.get(0).onActive();
        Gdx.app.log("Test", "5");
        Gdx.app.log("", "" + bosses.get(0).spellCards.size);
        bossEffects.spellEffect.startSpell(getStageData().scripts.get(currentSpellIndex).spellName);
    }

    public void inviteNewJsonBoss() {
        JsonBoss newBoss = new JsonBoss(this, getStageData().scripts.get(currentSpellIndex).bossName);
        newBoss.enemyBody.setTransform(10, 140, 0);
        String filename = getStageData().scripts.get(currentSpellIndex).scriptFilename();
        newBoss.spellCards.add(new JRubySpellCard(newBoss, BulletScript.load(getStageData().scripts.get(currentSpellIndex).scriptFilename())));
        bosses.add(newBoss);
        newBoss.onActive();
        bossEffects.spellEffect.startSpell(getStageData().scripts.get(currentSpellIndex).spellName);
    }

    void updateSpellTimeOut() {
        if (timer - memoryTime >= 3600)
            onSpellClear();
    }

    void displaySpellCardBonus() {

    }
    public void onSpellClear() {
        memoryTime = timer;
        bgManager.setBackground(backgroundRegistry.get("default"));
        if (bosses.size == 0) {
            inviteNewJsonBoss();
        } else {
            clearBullets();
            currentSpellIndex++;
            Gdx.app.log("", getStageData().scripts.get(currentSpellIndex).bossName);
            Gdx.app.log("", bosses.get(0).name);

            if (getStageData().scripts.get(currentSpellIndex).bossName.contains(bosses.get(0).name)) {
                switchToNextSpellcard();
            } else {
                Gdx.app.log("Test", "6");
                // when not
                bosses.get(0).spellCards.clear();
                Gdx.app.log("Test", "7");
                bosses.get(0).onLeave();
                Gdx.app.log("Test", "8");
                JsonBoss newBoss = new JsonBoss(this, getStageData().scripts.get(currentSpellIndex).bossName);
                bosses.add(newBoss);
            }

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
        //Gdx.app.log("Test", "Show Called");
    }

    /**
     */
    @Override
    public void hide() {
        //Gdx.app.log("Test", "Hide Called");
    }

    @Override
    public void pause() {
        //Gdx.app.log("Test", "Pause Called");
        game.paused = true;
    }

    public boolean isPaused() {
        return paused;
    }

    /**
     */
    @Override
    public void resume() {
        //Gdx.app.log("Test", "Resume Called");
        game.paused = false;
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

    public void clearBullets(){

        for (Bullet singleBullet : bullets) {
            toDispose.add(singleBullet.body);
        }

        bullets.clear();
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

    public void addLaser(Laser laser) {
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
//        musicBox.playSe(AudioBank.bulletAppear);
        bullets.add(bullet);
        return bullet;
    }

    public BulletShooter addShooter(BulletShooter bs) {
        shooters.add(bs);
        return bs;
    }

    public void updateScreenshot() {
        if (Gdx.input.isKeyPressed(Input.Keys.P))
            ScreenshotTaker.saveScreenshot();
    }

    void createEffect(float x, float y) {
        ParticleEffectPool.PooledEffect effect = particleEffectPool.obtain();
        effect.setPosition(x, y);
        pooledEffects.add(effect);
    }


    @Override
    public World getWorld() {
        return world;
    }

    public DanmakuPlayer getPlayer() {
        return players.first();
    }


    //returns the timerFlow per frame, depends on grazing... : in seconds
    public int getTimeFlowFrames() {
        return 1 + getPlayer().grazeCnt / 70;
    }

    private void removeGarbageBullets() {
        for (Bullet singleBullet : bullets) {
            singleBullet.update();
            if (singleBullet.getX() > 350 / 5 || singleBullet.getX() < -50 / 3 || singleBullet.getY() > 500 / 5 || singleBullet.getY() < -60 / 5)
                destroyBullet(singleBullet);
        }
    }

    public Array<Bullet> getBullets() {
        return bullets;
    }
}
