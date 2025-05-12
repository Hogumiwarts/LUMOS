package com.hogumiwarts.lumos.device.docs;

import com.hogumiwarts.lumos.device.dto.DeviceStatusResponse;
import com.hogumiwarts.lumos.device.dto.DevicesCreateResponse;
import com.hogumiwarts.lumos.dto.CommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

public interface DeviceApiSpec {

    @Operation(
        summary = "회원의 디바이스 목록 조회",
        description = """
                    💡 서버 DB에 저장된 회원의 디바이스 목록을 조회합니다.
                    - 이미 등록된 디바이스 정보만 조회 됩니다.
                    - 'installedAppId' 값은 디버깅 용으로 출력한 값 입니다. (추후 제거 예정. Client 쪽에서 값을 받지 않아도 됩니다)
                    """,
        tags = {"기기정보 조회"}
    )
    @ApiResponses({@ApiResponse(responseCode = "200", description = "디바이스 목록 조회 성공"),})
    @GetMapping
    ResponseEntity<CommonResponse<List<DeviceStatusResponse>>> getAllDeviceByMember();

    @Operation(
        summary = "SmartThings 기기 탐색(동기화)",
        description = """
                    💡 SmartThings 계정에 등록된 최신 디바이스 정보를 동기화합니다.
                    - Node.js 기반 디바이스 제어 서비스에 API 요청을 보내 SmartThings API를 호출합니다.
                    - 연결된 SmartThings 계정의 전체 디바이스 목록을 받아옵니다.
                    - 내부 DB에 없는 신규 디바이스는 자동으로 저장되며,
                    - 이렇게 추가된 신규 디바이스 목록만 응답으로 반환됩니다.
                    
                    ---
                    
                    1. SmartThings 인증 요청
                    - https://api.smartthings.com/oauth/authorize?client_id=8ec4baf1-5c20-425d-9bb4-c015a47534aa&scope=r:locations:*%20r:devices:*%20x:devices:*&response_type=code&redirect_uri=https://k12d103.p.ssafy.io/smart/oauth/callback 주소로 인증 요청
                    - 안드로이드 딥링크로 redirect
                    
                    2. API 요청
                    - 위 과정에서 받은 installedAppId로 현재 API 요청
                    - SmartThings 연동 기기 정보를 DB와 동기화
                    """,
        tags = {"기기정보 조회"}
    )
    @ApiResponses({@ApiResponse(responseCode = "200", description = "기기 탐색 성공"),})
    @GetMapping("/discover")
    ResponseEntity<CommonResponse<List<DeviceStatusResponse>>> getSmartThingsDevices(
        @Parameter(description = "SmartThings 제어용 installedAppId",
            required = true,
            example = "5f810cf2-432c-4c4c-bc72-c5af5abf1ef5")
        @RequestParam String installedAppId);

    @Operation(
        summary = "스마트 태그로 디바이스 상태 조회",
        description = """
                    💡 스마트 태그 번호(tagNumber)와 회원 ID(memberId)를 기반으로 연결된 디바이스의 상태 정보를 조회합니다.
                    - 내부 DB를 통해 tagNumber에 대응하는 deviceId를 조회한 뒤, 해당 deviceId로 별도의 디바이스 제어 서비스(Node.js)에 상태 요청을 보냅니다.
                    - 따라서 반환값은 실제 디바이스의 실시간 상태를 기반으로 합니다.
                    - 디바이스 종류에 따라 반환값의 형태가 달라질 수 있습니다. (ex: 스피커의 경우 볼륨 값 정보가 포함되어있음)
                    """,
        tags = {"기기정보 조회"},
        responses = {
            @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(schema = @Schema(implementation = DeviceStatusResponse.class))),
            @ApiResponse(responseCode = "404", description = "해당 태그의 디바이스를 찾을 수 없음")
        }
    )
    @GetMapping("/{tagNumber}/status")
    ResponseEntity<CommonResponse<Object>> getDeviceStatusByTagNumber(
        @Parameter(description = "스마트 태그 번호", required = true)
        @PathVariable("tagNumber") int tagNumber
    );


    @Operation(summary = "X", description = """
            💡 deviceId 리스트를 통해 디바이스 조회 후 각 디바이스의 정보를 리스트로 반환
            """,
        tags = {"기기정보 조회"}
    )
    List<DevicesCreateResponse> getDeviceDetailsByIds(@RequestParam("deviceIds") List<Long> deviceIds);
}