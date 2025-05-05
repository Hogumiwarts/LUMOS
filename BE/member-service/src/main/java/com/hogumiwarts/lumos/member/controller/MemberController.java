package com.hogumiwarts.lumos.member.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hogumiwarts.lumos.member.dto.CreateUserRequest;
import com.hogumiwarts.lumos.member.dto.MemberResponse;
import com.hogumiwarts.lumos.member.entity.Member;
import com.hogumiwarts.lumos.member.service.MemberService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/member/api")
@RequiredArgsConstructor
public class MemberController {

	private final MemberService memberService;

	@GetMapping("/email-exists")
	public ResponseEntity<Boolean> checkEmail(@RequestParam String email) {
		return ResponseEntity.ok(memberService.isEmailExists(email));
	}

	@PostMapping("/create")
	public ResponseEntity<MemberResponse> createMember(@RequestBody CreateUserRequest request) {
		MemberResponse response = memberService.createMember(request);
		return ResponseEntity.ok(response);
	}

	@GetMapping("/find-by-email")
	public ResponseEntity<MemberResponse> findByEmail(@RequestParam String email) {
		return ResponseEntity.ok(memberService.findByEmail(email));
	}

	@GetMapping("/members/{memberId}")
	public ResponseEntity<MemberResponse> getMember(@PathVariable Long memberId) {
		return ResponseEntity.ok(memberService.findById(memberId));
	}
}
