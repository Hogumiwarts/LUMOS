package com.hogumiwarts.lumos.device.dto.airpurifier;

import com.fasterxml.jackson.databind.JsonNode;
import com.hogumiwarts.lumos.device.util.AirQualityLevelUtil;

public class AirPurifierUtil {

    private AirPurifierUtil() {
    }

    public static boolean parsePower(JsonNode main) {
        String switchValue = main.path("switch").path("switch").path("value").asText(null);
        return "on".equalsIgnoreCase(switchValue);
    }

    public static String parseCaqi(JsonNode main) {
        String rawAirQuality = main.path("airQualitySensor").path("airQuality").path("value").asText(null);
        return AirQualityLevelUtil.toAirQualityLevel(rawAirQuality);
    }

    public static Integer parseOdorLevel(JsonNode main) {
        JsonNode node = main.path("odorSensor").path("odorLevel").path("value");
        return node.isInt() ? node.asInt() : null;
    }

    public static Integer parseDustLevel(JsonNode main) {
        JsonNode node = main.path("dustSensor").path("dustLevel").path("value");
        return node.isInt() ? node.asInt() : null;
    }

    public static Integer parseFineDustLevel(JsonNode main) {
        JsonNode node = main.path("dustSensor").path("fineDustLevel").path("value");
        return node.isInt() ? node.asInt() : null;
    }

    public static String parseFanMode(JsonNode main) {
        return main.path("airConditionerFanMode").path("fanMode").path("value").asText(null);
    }

    public static Integer parseFilterUsageTime(JsonNode main) {
        JsonNode node = main.path("custom.filterUsageTime").path("usageTime").path("value");
        return node.isInt() ? node.asInt() : null;
    }
}
