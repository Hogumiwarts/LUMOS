package com.hogumiwarts.lumos.device.dto.audio;

import com.fasterxml.jackson.databind.JsonNode;

public class AudioUtil {

    public static boolean parsePlaybackActivated(JsonNode main) {
        String playbackValue = main
                .path("mediaPlayback")
                .path("playbackStatus")
                .path("value")
                .asText(null);

        return switch (playbackValue != null ? playbackValue.toLowerCase() : "") {
            case "playing", "fast forwarding", "rewinding" -> true;
            default -> false;
        };
    }

    public static String parseAlbumArtUrl(JsonNode main) {
        return main
                .path("audioTrackData")
                .path("audioTrackData")
                .path("value")
                .path("albumArtUrl")
                .asText(null);
    }

    public static String parseArtist(JsonNode main) {
        return main
                .path("audioTrackData")
                .path("audioTrackData")
                .path("value")
                .path("artist")
                .asText(null);
    }

    public static String parseAlbumTitle(JsonNode main) {
        return main
                .path("audioTrackData")
                .path("audioTrackData")
                .path("value")
                .path("title")
                .asText(null);
    }

    public static Integer parseVolume(JsonNode main) {
        JsonNode groupVolNode = main.path("mediaGroup").path("groupVolume").path("value");
        if (!groupVolNode.isMissingNode() && groupVolNode.isInt()) {
            return groupVolNode.asInt();
        }

        JsonNode volNode = main.path("audioVolume").path("volume").path("value");
        if (!volNode.isMissingNode() && volNode.isInt()) {
            return volNode.asInt();
        }

        return null;
    }

}
