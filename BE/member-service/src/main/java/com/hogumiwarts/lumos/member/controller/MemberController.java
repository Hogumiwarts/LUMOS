package com.hogumiwarts.lumos.member.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hogumiwarts.lumos.dto.CommonResponse;
import com.hogumiwarts.lumos.member.docs.MemberApiSpec;
import com.hogumiwarts.lumos.member.dto.CreateMemberRequest;
import com.hogumiwarts.lumos.member.dto.CreateMemberResponse;
import com.hogumiwarts.lumos.member.dto.MemberResponse;
import com.hogumiwarts.lumos.member.service.MemberService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class MemberController implements MemberApiSpec {

	private final MemberService memberService;

	@GetMapping("/member")
	public ResponseEntity<CommonResponse<MemberResponse>> getMemberById() {
		return ResponseEntity.ok(CommonResponse.ok("회원 정보가 성공적으로 조회되었습니다.", (memberService.getMemberById())));
	}

	@GetMapping("/email-exists")
	public ResponseEntity<Boolean> checkEmail(@RequestParam String email) {
		return ResponseEntity.ok(memberService.isEmailExists(email));
	}

	@PostMapping("/create")
	public ResponseEntity<CreateMemberResponse> createMember(@RequestBody CreateMemberRequest request) {
		CreateMemberResponse response = memberService.createMember(request);
		return ResponseEntity.ok(response);
	}

	@GetMapping("/find-by-email")
	public ResponseEntity<CreateMemberResponse> findByEmail(@RequestParam String email) {
		return ResponseEntity.ok(memberService.findByEmail(email));
	}

	@GetMapping("/members/{memberId}")
	public ResponseEntity<CreateMemberResponse> getMember(@PathVariable Long memberId) {
		return ResponseEntity.ok(memberService.findById(memberId));
	}
}
