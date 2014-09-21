package com.github.bakabbq.datas;

import com.github.bakabbq.background.ThBackground;

import java.util.HashMap;

/**
 * Created by LBQ on 9/21/14.
 */
public class BackgroundRegistry {
    public HashMap<String, ThBackground> registry;

    public BackgroundRegistry() {
        registry = new HashMap<String, ThBackground>();
    }

    public void register(String str, ThBackground bg) {
        registry.put(str, bg);
    }

    public ThBackground get(String str) {
        return registry.get(str);
    }
}
