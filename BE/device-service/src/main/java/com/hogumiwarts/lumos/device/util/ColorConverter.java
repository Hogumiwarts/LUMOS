package com.hogumiwarts.lumos.device.util;

import java.awt.Color;

public class ColorConverter {

    public static float[] hexToHSV(String hex) {
        Color color = Color.decode(hex);
        float[] hsv = Color.RGBtoHSB(
                color.getRed(),
                color.getGreen(),
                color.getBlue(),
                null
        );
        hsv[0] *= 360; // hue: 0.0~1.0 → 0~360
        return hsv;
    }
}
