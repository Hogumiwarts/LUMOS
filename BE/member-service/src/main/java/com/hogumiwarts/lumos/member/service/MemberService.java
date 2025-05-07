package com.hogumiwarts.lumos.member.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.hogumiwarts.lumos.exception.CustomException;
import com.hogumiwarts.lumos.exception.ErrorCode;
import com.hogumiwarts.lumos.member.dto.CreateUserRequest;
import com.hogumiwarts.lumos.member.dto.MemberResponse;
import com.hogumiwarts.lumos.member.entity.Member;
import com.hogumiwarts.lumos.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberService {

	private final MemberRepository memberRepository;

	public boolean isEmailExists(String email) {
		return memberRepository.existsByEmail(email);
	}

	public MemberResponse createMember(CreateUserRequest request) {
		Member member = new Member();
		member.setEmail(request.getEmail());
		member.setPassword(request.getPassword()); // 비밀번호는 auth에서 해싱된 걸로 전달
		member.setName(request.getName());

		Member saved = memberRepository.save(member);
		return MemberResponse.fromEntity(saved);
	}

	public MemberResponse findByEmail(String email) {
		Optional<Member> member = memberRepository.findByEmail(email);
		return member.map(MemberResponse::fromEntity).orElse(null);
	}

	public MemberResponse findById(Long memberId) {
		Optional<Member> member = memberRepository.findById(memberId);
		return member.map(MemberResponse::fromEntity).orElse(null);
	}
}
