package com.github.bakabbq.shooters.bosses;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.github.bakabbq.IDanmakuWorld;

/**
 * Created by LBQ on 9/5/14.
 * <p/>
 * Bosses read from a json file
 */
public class JsonBoss extends ThBoss {
    public FileHandle oriFilehandle;
    public String author;

    public JsonBoss(IDanmakuWorld ground) {
        super(ground);
    }

    public JsonBoss(IDanmakuWorld ground, String spellBossname) {
        super(ground);
        loadData(spellBossname);
    }

    public void loadData(String spellBossname) {
        JsonReader jr = new JsonReader();
        oriFilehandle = Gdx.files.internal("bosses/" + spellBossname + ".json");
        JsonValue primaryV = jr.parse(oriFilehandle);
        String assetName = primaryV.getString("asset_name");
        this.name = primaryV.getString("name");
        author = primaryV.getString("author");
        mainTexture = new Texture(Gdx.files.internal("bosses/" + assetName));
        //mainTexture = new Texture(Gdx.files.internal("bosses/stg6enm.png"));
    }

    /*
    @Override
    public void initMainTexture() {
        mainTexture = new Texture(Gdx.files.internal("bosses/stg6enm.png"));
    }
     */
}
