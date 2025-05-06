package com.hogumiwarts.lumos.auth.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.hogumiwarts.lumos.auth.dto.CreateMemberRequest;
import com.hogumiwarts.lumos.auth.dto.MemberResponse;

@FeignClient(name = "member-service", url = "${member.service.url}")
public interface MemberClient {

	@GetMapping("/member/api/email-exists")
	Boolean checkEmailExists(@RequestParam("email") String email);

	@PostMapping("/member/api/create")
	MemberResponse createMember(@RequestBody CreateMemberRequest request);

	@GetMapping("/member/api/find-by-email")
	MemberResponse findByEmail(@RequestParam("email") String email);

	@GetMapping("/member/api/members/{memberId}")
	MemberResponse getMember(@PathVariable Long memberId);
}