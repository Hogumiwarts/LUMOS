package com.hogumiwarts.lumos.member.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.hogumiwarts.lumos.exception.CustomException;
import com.hogumiwarts.lumos.exception.ErrorCode;
import com.hogumiwarts.lumos.member.dto.CreateMemberRequest;
import com.hogumiwarts.lumos.member.dto.CreateMemberResponse;
import com.hogumiwarts.lumos.member.dto.MemberResponse;
import com.hogumiwarts.lumos.member.entity.Member;
import com.hogumiwarts.lumos.member.repository.MemberRepository;
import com.hogumiwarts.lumos.util.AuthUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberService {

	private final MemberRepository memberRepository;

	public MemberResponse getMemberById() {
		Long memberId = AuthUtil.getMemberId();
		try {
			Member member = memberRepository.findById(memberId)
				.orElseThrow(() -> new CustomException(ErrorCode.MEMBER_ID_NOT_FOUND));
			return MemberResponse.fromEntity(member);
		} catch (Exception e) {
			throw new CustomException(ErrorCode.MEMBER_SERVER_ERROR);
		}
	}

	public boolean isEmailExists(String email) {
		return memberRepository.existsByEmail(email);
	}

	public CreateMemberResponse findByEmail(String email) {
		Optional<Member> member = memberRepository.findByEmail(email);
		return member.map(CreateMemberResponse::fromEntity).orElse(null);
	}

	public Boolean isMemberExists(Long memberId) {return memberRepository.existsById(memberId);}

	public CreateMemberResponse createMember(CreateMemberRequest request) {
		Member member = new Member();
		member.setEmail(request.getEmail());
		member.setPassword(request.getPassword()); // 비밀번호는 auth에서 해싱된 걸로 전달
		member.setName(request.getName());

		Member saved = memberRepository.save(member);
		return CreateMemberResponse.fromEntity(saved);
	}
}
