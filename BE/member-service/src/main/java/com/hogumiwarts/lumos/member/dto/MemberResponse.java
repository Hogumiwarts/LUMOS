package com.hogumiwarts.lumos.member.dto;

import com.hogumiwarts.lumos.member.entity.Member;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MemberResponse {
	private Long memberId;
	private String email;
	private String name;
	private String password;

	public static MemberResponse fromEntity(Member m) {
		return new MemberResponse(m.getMemberId(), m.getEmail(), m.getName(), m.getPassword());
	}
}
