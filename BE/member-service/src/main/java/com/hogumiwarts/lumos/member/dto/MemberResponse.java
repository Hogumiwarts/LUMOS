package com.hogumiwarts.lumos.member.dto;

import com.hogumiwarts.lumos.member.entity.Member;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MemberResponse {
	private String name;

	public static MemberResponse fromEntity(Member member) {
		return new MemberResponse(member.getName());
	}
}
