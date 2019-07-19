package com.wingor_software.mylearn;

import java.util.HashMap;
import java.util.Map;

public enum EnumColors
{
    red(1),
    yellow(2),
    green(3),
    blue(4),
    purple(5);

    private int value;
    private static Map map = new HashMap();

    private EnumColors(int value)
    {
        this.value = value;
    }

    static {
        for (EnumColors color : EnumColors.values())
        {
            map.put(color.value, color);
        }
    }

    public static EnumColors valueOf(int value)
    {
        return (EnumColors)map.get(value);
    }

    public int getValue()
    {
        return value;
    }
}
