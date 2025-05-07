package com.hogumiwarts.lumos.device.util;

import com.hogumiwarts.lumos.device.dto.CommandRequest;

import java.util.List;

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

    // 필요한 경우 LIGHT도 추가
}