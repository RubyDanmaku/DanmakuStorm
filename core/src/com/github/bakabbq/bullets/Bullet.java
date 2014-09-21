package com.github.bakabbq.bullets;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.*;
import com.github.bakabbq.BulletCollisionListener;
import com.github.bakabbq.IDanmakuWorld;


/**
 * Created by LBQ on 5/27/14.
 */
public class Bullet {
    public static BulletDef debugBullet = new BulletDef(0);
    public static BulletAmulets amuletBullet = new BulletAmulets(0);
    public static BulletKunai kunaiBullet = new BulletKunai(0);
    public static BulletOval ovalBullet = new BulletOval(0);
    public static int COLOR_GRAY = 0;
    public static int COLOR_DARKRED = 1;
    public static int COLOR_RED = 2;
    public static int COLOR_PURPLE = 3;
    public static int COLOR_VIOLET = 4;
    public static int COLOR_DARKBLUE = 5;
    public static int COLOR_BLUE = 6;
    public static int COLOR_SKYBLUE = 7;
    public static int COLOR_LIGHTBLUE = 8;
    public static int COLOR_GRASSGREEN = 9;
    public static int COLOR_GREEN = 10;
    public static int COLOR_YELLOWGREEN = 11;
    public static int COLOR_LEMONYELLOW = 12;
    public static int COLOR_YELLOW = 13;
    public static int COLOR_ORANGE = 14;
    public static int COLOR_WHITE = 15;
    public Body body;
    public BulletDef bd;
    public Sprite sprite;
    public World world;
    public boolean grazed;
    public boolean collided;
    public boolean pause;
    public IDanmakuWorld danmakuWorld;
    //Destroy Flag - once marked, it will be garbage dumped
    public boolean destroyFlag;
    public int timer;
    Fixture fixture;
    int alpha;

    public Bullet(BulletDef bd, World world, float x, float y, float angle) {
        long start;
        start = System.currentTimeMillis();
        this.bd = bd;
        this.timer = 0;
        this.world = world;
        BodyDef bodydef = new BodyDef();
        bodydef.type = BodyDef.BodyType.DynamicBody;
        body = world.createBody(bodydef);
        body.setUserData(this);
        FixtureDef fd = bd.fixtureD;
        fd.filter.categoryBits = BulletCollisionListener.ENEMY_BULLET;
        fd.filter.maskBits = (short) (BulletCollisionListener.PLAYER | 0x001 | BulletCollisionListener.PLAYER_BULLET);
        this.fixture = body.createFixture(fd);
        body.setTransform(x, y, angle);
        MassData md = new MassData();
        md.mass = 4;
        body.setMassData(md);
        this.alpha = bd.alpha;
        grazed = false;
    }

    public int getAngleFix() {
        return bd.angleFix;
    }

    public TextureRegion getTexture() {
        return bd.texture;
    }

    public float getX() {
        return body.getPosition().x;
    }

    public float getY() {
        return body.getPosition().y;
    }

    public int getXOffset() {
        return bd.xOffset;
    }

    public int getYOffset() {
        return bd.yOffset;
    }

    public int getOriginX() {
        if (bd.origin_x == -1)
            return getTexture().getRegionWidth() / 2;
        return bd.origin_x;
    }

    public int getOriginY() {
        if (bd.origin_y == -1)
            return getTexture().getRegionHeight() / 2;
        return bd.origin_y;
    }

    public void setSpeed(float angle, float speed) {
        aimAt(angle);
        float forceAngle = (angle + 270f) / 180f * (float) Math.PI;
        //body.applyLinearImpulse(MathUtils.cos(forceAngle) * speed, MathUtils.sin(forceAngle) * speed, body.getPosition().x, body.getPosition().y, true);
        body.setLinearVelocity(speed * MathUtils.cos(forceAngle), speed * MathUtils.sin(forceAngle));
    }

    public float getSpeed() {
        return body.getLinearVelocity().len();
    }

    public void setSpeed(float speed) {
        float forceAngle = (body.getAngle() + 270f) / 180f * (float) Math.PI;
        //body.applyLinearImpulse(MathUtils.cos(forceAngle) * speed, MathUtils.sin(forceAngle) * speed, body.getPosition().x, body.getPosition().y, true);
        body.setLinearVelocity(speed * MathUtils.cos(forceAngle), speed * MathUtils.sin(forceAngle));
    }

    public float getAngle() {
        return body.getAngle();
    }

    public void setAngle(float angle) {
        setSpeed(angle, getSpeed());
    }

    public void accelerate(float increment) {
        setSpeed(getSpeed() + increment);
    }

    public void update() {
        timer++;
        if (pause)
            return;
        this.bd.modifyBullet(this);

    }

    public int getAlpha() {
        return this.alpha;
    }


    public void onGraze() {
        danmakuWorld.getPlayer().grazeCnt += 1;
        grazed = true;
    }

    public void stop() {
        body.setLinearVelocity(0, 0);
        body.setAngularVelocity(0);
    }

    public boolean canGraze() {
        return !grazed;
    }

    public TextureRegion getCreationTexture() {
        return bd.onCreationTexture;
    }

    public void dispose() {
        this.world.destroyBody(this.body);
    }

    public float getTowardPlayerAngle() {
        float xDiff = getTowardPlayerX();
        float yDiff = getTowardPlayerY();
        return ((float) Math.atan(yDiff / xDiff));
    }

    public float getTowardPlayerX() {
        float xDiff = getX() - danmakuWorld.getPlayer().getX();
        return xDiff;
    }

    public float getTowardPlayerY() {
        float yDiff = getY() - danmakuWorld.getPlayer().getY();
        return yDiff;
    }

    public boolean hasCreationEffect() {
        return this.timer <= 5;
    }

    public void aimAt(float angle) {
        this.body.setTransform(getX(), getY(), angle);
    }

    public boolean canDestroy() {
        return destroyFlag;
    }

    public void onHit() {
        destroyFlag = true;
    }

    public boolean stillCollide() {
        return this.alpha == bd.alpha;
    }
}
