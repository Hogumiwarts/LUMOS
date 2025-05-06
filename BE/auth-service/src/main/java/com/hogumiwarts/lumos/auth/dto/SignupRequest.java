package com.hogumiwarts.lumos.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignupRequest {

	@Schema(description = "사용자 이메일", example = "test@test.com")
	@NotBlank(message = "{email.required}")
	@Email(message = "{email.invalid}")
	private String email;

	@Schema(description = "비밀번호", example = "test1234")
	@NotBlank(message = "{password.required}")
	@Size(min = 6, message = "{password.min_length}")
	private String password1;

	@Schema(description = "검증 비밀번호", example = "test1234")
	@NotEmpty(message = "{password.confirm_required}")
	private String password2;

	@Schema(description = "사용자 이름", example = "test")
	@NotBlank(message = "{username.required}")
	private String name;
}
