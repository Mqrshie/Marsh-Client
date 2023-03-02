package com.mqrshie.marsh.util;

import com.google.gson.JsonParser;
import me.alpha432.oyvey.util.Timer;
import me.alpha432.oyvey.util.Util;

public class PlayerUtil2 implements Util {
    public static Timer timer;
    private static JsonParser PARSER;

    static {
        PlayerUtil2.timer = new Timer();
        PlayerUtil2.PARSER = new JsonParser();
    }
}

