package com.phamthanhlong.identity_service.dto.request;

import jakarta.validation.constraints.*;

import java.time.LocalDate;

public class UserCreationRequest {
    @NotNull(message = "Username must not be null")
    @NotBlank(message = "Username must not be blank")
    @Pattern(regexp = "^\\S+$", message = "Username must not contain spaces")
    private String username;

    @Size(min = 8,message = "password don't allow lest 8 characters")
    private String password;

    private String firstname;
    private String lastname;

    private LocalDate dob;

//    public UserCreationRequest() {}

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public LocalDate getDob() {
        return dob;
    }

    public void setDob(LocalDate dob) {
        this.dob = dob;
    }
}
