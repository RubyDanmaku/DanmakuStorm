package com.github.bakabbq;

import com.badlogic.gdx.physics.box2d.*;
import com.github.bakabbq.bullets.Bullet;
import com.github.bakabbq.bullets.PlayerBullet;
import com.github.bakabbq.shooters.EnemyShooter;
import com.github.bakabbq.shooters.players.PlayerGrazeCounter;

/**
 * Created by LBQ on 5/26/14.
 * <p/>
 * Veeeeeeeeeeery Ugly, needs to be rewritten
 */
public class BulletCollisionListener implements ContactListener {
    public static short PLAYER = 0x002;
    public static short PLAYER_BULLET = 0x004;
    public static short ENEMY = 0x008;
    public static short ENEMY_BULLET = 0x010;
    public static short ITEMS = 0x012;
    public static short GRAZE = 0x014;
    public boolean goBack = false;

    @Override
    public void beginContact(Contact contact) {

        Body bodyA = contact.getFixtureA().getBody();
        Body bodyB = contact.getFixtureB().getBody();

        if (bodyA.getUserData() instanceof PlayerGrazeCounter && bodyB.getUserData() instanceof Bullet) {
            increasePlayerGraze((Bullet) bodyB.getUserData());
        } else if (bodyB.getUserData() instanceof PlayerGrazeCounter && bodyA.getUserData() instanceof Bullet) {
            increasePlayerGraze((Bullet) bodyA.getUserData());
        }

    }

    public void increasePlayerGraze(Bullet bullet) {
        //((PracticeScreen) DanmakuGame.getInstance().currentScreen).getPlayer().grazeCnt++;
        //Gdx.app.log("PlayerGraze", "" + ((PracticeScreen) DanmakuGame.getInstance().currentScreen).getPlayer().grazeCnt++);
        if (bullet.canGraze())
            bullet.onGraze();
    }

    @Override
    public void endContact(Contact contact) {
        // has no use
    }


    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {
        goBack = false;
        Body bodyA = contact.getFixtureA().getBody();
        Body bodyB = contact.getFixtureB().getBody();


        if (bodyA.getLinearDamping() >= 5) {
            //goBack = true;
        }
        if (bodyB.getLinearDamping() >= 5) {
            //goBack = true;
        }

        if (bodyA.getUserData() instanceof PlayerBullet && bodyB.getUserData() instanceof EnemyShooter) {
            //((PlayerBullet) bodyA.getUserData()).destroyFlag = true;
            bodyA.getFixtureList().first().setSensor(true);
            contact.setEnabled(false);
        }

        if (bodyB.getUserData() instanceof PlayerBullet && bodyA.getUserData() instanceof EnemyShooter) {
            //((PlayerBullet) bodyA.getUserData()).destroyFlag = true;
            bodyB.getFixtureList().first().setSensor(true);
            contact.setEnabled(false);
        }


        // Enemy - PlayerBullet Listener
        if (bodyA.getUserData() instanceof EnemyShooter && bodyB.getUserData() instanceof PlayerBullet && ((PlayerBullet) bodyB.getUserData()).stillCollide()) {
            ((PlayerBullet) bodyB.getUserData()).collided = true;
            int dmg = ((PlayerBullet) bodyB.getUserData()).damage;
            ((EnemyShooter) bodyA.getUserData()).receiveDamage(dmg);

            contact.setEnabled(false);
        }

        if (bodyB.getUserData() instanceof EnemyShooter && bodyA.getUserData() instanceof PlayerBullet && ((PlayerBullet) bodyA.getUserData()).stillCollide()) {
            ((PlayerBullet) bodyA.getUserData()).collided = true;
            int dmg = ((PlayerBullet) bodyA.getUserData()).damage;
            ((EnemyShooter) bodyB.getUserData()).receiveDamage(dmg);
            contact.setEnabled(false);
        }


    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
}
