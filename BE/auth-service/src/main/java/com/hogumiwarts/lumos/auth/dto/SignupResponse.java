package com.hogumiwarts.lumos.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class SignupResponse {
	private Long memberId;
	private String email;
	private String name;
	private String createdAt;
}
