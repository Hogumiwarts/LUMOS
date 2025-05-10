package com.hogumiwarts.lumos.device.util;

import com.hogumiwarts.lumos.device.dto.CommandRequest;
import com.hogumiwarts.lumos.device.dto.FanMode;

import java.util.List;
import java.util.Map;

public class DeviceCommandUtil {

    // ============================== Switch : 미니빅 ==============================
    public static CommandRequest buildSwitchPowerCommand(Boolean activated) {
        return new CommandRequest(List.of(
                new CommandRequest.Command(
                        "main",
                        "switch",
                        activated != null && activated ? "on" : "off",
                        List.of()
                )
        ));
    }


    // ============================== Audio : 쉼포니스크 ==============================
    public static CommandRequest buildAudioPlayBackCommand(Boolean activated) {
        return new CommandRequest(List.of(
                new CommandRequest.Command(
                        "main",
                        "mediaPlayback", //audioVolume
                        activated != null && activated ? "play" : "stop",
                        List.of()
                )
        ));
    }

    public static CommandRequest buildAudioVolumeCommand(int volume) {
        return new CommandRequest(List.of(
                new CommandRequest.Command(
                        "main",
                        "audioVolume",
                        "setVolume",
                        List.of(volume)
                )
        ));
    }

    // ============================== AirPurifier : SSAFY 공기청정기 ==============================
    public static CommandRequest buildAirPurifierPowerCommand(Boolean activated) {
        return new CommandRequest(List.of(
                new CommandRequest.Command(
                        "main",
                        "switch",
                        activated != null && activated ? "on" : "off",
                        List.of()
                )
        ));
    }

    public static CommandRequest buildAirPurifierFanModeCommand(FanMode mode) {
        return new CommandRequest(List.of(
                new CommandRequest.Command(
                        "main",
                        "airConditionerFanMode",
                        "setFanMode",
                        List.of(mode.getMode())
                )
        ));
    }

    // ============================== Light onOff : SSAFY 조명 ==============================
    public static CommandRequest buildLightOnOffCommand(Boolean activated) {
        return new CommandRequest(List.of(
                new CommandRequest.Command(
                        "main",
                        "switch",
                        activated != null && activated ? "on" : "off",
                        List.of()
                )
        ));
    }

    // ============================== Light Color : SSAFY 조명 ==============================
    public static CommandRequest buildLightColorCommand(String hex) {
        // todo: 색상 코드 수정 필요
        float[] hsv = ColorConverter.hexToHSV(hex);
        int hue = Math.round(hsv[0]);              // 0 ~ 7
        int saturation = Math.round(hsv[1] * 100); // 0 ~ 100

        return new CommandRequest(List.of(
                new CommandRequest.Command(
                        "main",
                        "colorControl",
                        "setColor",
                        List.of(Map.of(
                                "hue", hue,
                                "saturation", saturation
                        ))
                )
        ));
    }

    // ============================== Light Temparature : SSAFY 조명 ==============================
    public static CommandRequest buildLightColorTemperatureCommand(int kelvin) {
        // SmartThings 권장 범위: 2200K ~ 6500K
        kelvin = Math.max(2200, Math.min(kelvin, 6500));

        return new CommandRequest(List.of(
                new CommandRequest.Command(
                        "main",
                        "colorTemperature",
                        "setColorTemperature",
                        List.of(kelvin)
                )
        ));
    }

    // ============================== Light Brightness: SSAFY 조명 ==============================
    public static CommandRequest buildLightColorBrightnessCommand(int brightness) {
        brightness = Math.max(0, Math.min(brightness, 100));

        return new CommandRequest(List.of(
                new CommandRequest.Command(
                        "main",
                        "switchLevel",
                        "setLevel",
                        List.of(brightness)
                )
        ));
    }

}