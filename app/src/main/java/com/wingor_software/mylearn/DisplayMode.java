package com.wingor_software.mylearn;

import java.util.HashMap;
import java.util.Map;

public enum DisplayMode
{
    LIGHT(0),
    DARK(1);

    private int value;
    private static Map map = new HashMap();

    private DisplayMode(int value)
    {
        this.value = value;
    }

    static{
        for (DisplayMode mode : DisplayMode.values())
        {
            map.put(mode.value, mode);
        }
    }

    public static DisplayMode valueOf(int value) {
        return (DisplayMode) map.get(value);
    }

    public int getValue(){
        return value;
    }
}
