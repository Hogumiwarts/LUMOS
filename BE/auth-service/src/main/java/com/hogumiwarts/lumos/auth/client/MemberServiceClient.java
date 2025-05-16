package com.hogumiwarts.lumos.auth.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.hogumiwarts.lumos.auth.config.FeignAuthConfig;
import com.hogumiwarts.lumos.auth.dto.CreateMemberRequest;
import com.hogumiwarts.lumos.auth.dto.MemberResponse;

@FeignClient(name = "member-service", url = "${member.service.url}", configuration = FeignAuthConfig.class)
public interface MemberServiceClient {

	@PostMapping("/api/create")
	MemberResponse createMember(@RequestBody CreateMemberRequest request);

	@GetMapping("/api/email-exists")
	Boolean checkEmailExists(@RequestParam("email") String email);

	@GetMapping("/api/find-by-email")
	MemberResponse findByEmail(@RequestParam("email") String email);

	@GetMapping("/api/member-exists")
	Boolean checkMemberExists(@RequestParam("memberId") Long memberId);
}