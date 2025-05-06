package com.hogumiwarts.lumos.member.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateUserRequest {
	private String email;
	private String password;
	private String name;
}
