package com.hogumiwarts.lumos.device.util;

public class AirQualityLevelUtil {
    public static String toAirQualityLevel(String value) {
        if (value == null) return "Unknown";
        try {
            int level = Integer.parseInt(value);
            return switch (level) {
                case 0 -> "VeryHigh";
                case 1 -> "High";
                case 2 -> "Medium";
                case 3 -> "Low";
                case 4 -> "VeryLow";
                default -> "Unknown";
            };
        } catch (NumberFormatException e) {
            return "Unknown";
        }
    }
}
