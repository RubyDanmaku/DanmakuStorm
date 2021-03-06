package com.github.bakabbq.bullets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;

/**
 * Created by LBQ on 8/4/14.
 */
public class BulletKnife extends BigBullet{

    public BulletKnife(int colorId) {
        super(colorId);
    }

    @Override
    public void setTextureIndex() {
        bulletSheet = new Texture(Gdx.files.internal("bullets/bullet2.png"));
        textureX = colorId * 32;
        textureY = 32 * 3;
    }

    @Override
    public Shape getShape() {
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(0.2f, 0.8f);
        return shape;
    }
}
