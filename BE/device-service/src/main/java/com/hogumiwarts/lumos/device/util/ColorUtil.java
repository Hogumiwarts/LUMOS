package com.hogumiwarts.lumos.device.util;

public class ColorUtil {

    public static String hslToHex(double hue, double saturation) {
        // SmartThings hue/saturation → HSL 변환 (0~100 → 0~1)
        float h = (float)(hue / 100.0);           // 범위: 0.0 ~ 1.0
        float s = (float)(saturation / 100.0);    // 범위: 0.0 ~ 1.0
        float l = 0.5f; // 밝기 고정 (기본값, 필요 시 level 도 받아서 조절)

        float r, g, b;

        if (s == 0) {
            r = g = b = l; // 무채색 (회색)
        } else {
            float q = l < 0.5 ? l * (1 + s) : (l + s - l * s);
            float p = 2 * l - q;
            r = hueToRGB(p, q, h + 1f/3f);
            g = hueToRGB(p, q, h);
            b = hueToRGB(p, q, h - 1f/3f);
        }

        return String.format("#%02X%02X%02X",
                Math.round(r * 255),
                Math.round(g * 255),
                Math.round(b * 255));
    }

    private static float hueToRGB(float p, float q, float t) {
        if (t < 0) t += 1;
        if (t > 1) t -= 1;
        if (t < 1f/6f) return p + (q - p) * 6 * t;
        if (t < 1f/2f) return q;
        if (t < 2f/3f) return p + (q - p) * (2f/3f - t) * 6;
        return p;
    }
}
