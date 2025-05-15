package com.hogumiwarts.lumos.member.docs;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.hogumiwarts.lumos.dto.CommonResponse;
import com.hogumiwarts.lumos.member.dto.CreateMemberRequest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "회원", description = "회원 관리 API")
public interface MemberApiSpec {

	@Operation(
		summary = "회원 조회",
		description = """
        💡 현재 로그인한 사용자의 회원 정보를 조회합니다.
        """
	)
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "회원 정보가 성공적으로 조회되었습니다."),
		@ApiResponse(responseCode = "400",
			description = """
				`[MEMBER-001]` 해당 ID를 가진 사용자를 찾을 수 없습니다.
				`[MEMBER-002]` "사용자 ID가 누락되었습니다.
				""",
			content = @Content()),
		@ApiResponse(responseCode = "500",
			description = "`[MEMBER-003]` 회원 조회 중 서버 내부 오류가 발생했습니다.",
			content = @Content())
	})
	ResponseEntity<?> getMemberById();

	@Operation(
		summary = "X",
		description = """
        💡 회원 존재 여부를 확인합니다.
        """
	)
	ResponseEntity<?> checkEmail(@RequestParam String email);

	@Operation(
		summary = "X",
		description = """
        💡 회원  가입을 진행합니다.
        """
	)
	ResponseEntity<?> createMember(@RequestBody CreateMemberRequest request);

	@Operation(
		summary = "X",
		description = """
        💡 이메일로 회원 존재 여부를 확인합니다.
        """
	)
	ResponseEntity<?> findByEmail(@RequestParam String email);

	@Operation(
		summary = "X",
		description = """
        💡 memberId로 회원 정보를 조회합니다.
        """
	)
	ResponseEntity<?> getMember(@PathVariable Long memberId);
}
