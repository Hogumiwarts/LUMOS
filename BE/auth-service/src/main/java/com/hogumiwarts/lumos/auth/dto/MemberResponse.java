package com.hogumiwarts.lumos.auth.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberResponse {
	private Long memberId;
	private String email;
	private String password;
	private String name;
	private String createdAt;
}
