package com.hogumiwarts.lumos.device.util;

public class AirQualityLevelUtil {
    public static String toAirQualityLevel(String value) {
        if (value == null) return "Unknown";
        try {
            int level = Integer.parseInt(value);
            return switch (level) {
                case 0 -> "VeryLow";
                case 1 -> "Low";
                case 2 -> "Medium";
                case 3 -> "High";
                case 4 -> "VeryHigh";
                default -> "Unknown";
            };
        } catch (NumberFormatException e) {
            return "Unknown";
        }
    }
}
