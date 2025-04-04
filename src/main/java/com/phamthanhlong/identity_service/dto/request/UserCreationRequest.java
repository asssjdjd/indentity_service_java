package com.phamthanhlong.identity_service.dto.request;

import java.time.LocalDate;

import jakarta.validation.constraints.*;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserCreationRequest {
    @NotNull(message = "Username must not be null")
    @NotBlank(message = "Username must not be blank")
    @Pattern(regexp = "^\\S+$", message = "Username must not contain spaces")
    String username;

    @Size(min = 8, message = "password don't allow lest 8 characters")
    String password;

    String firstname;
    String lastname;

    LocalDate dob;

    //    public UserCreationRequest() {}

}
