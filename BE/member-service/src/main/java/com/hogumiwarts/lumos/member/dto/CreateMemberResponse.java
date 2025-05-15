package com.hogumiwarts.lumos.member.dto;

import com.hogumiwarts.lumos.member.entity.Member;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CreateMemberResponse {
	private Long memberId;
	private String email;
	private String name;
	private String password;

	public static CreateMemberResponse fromEntity(Member m) {
		return new CreateMemberResponse(m.getMemberId(), m.getEmail(), m.getName(), m.getPassword());
	}
}
