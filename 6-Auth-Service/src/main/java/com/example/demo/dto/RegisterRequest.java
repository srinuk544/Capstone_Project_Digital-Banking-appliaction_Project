package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;

public class RegisterRequest {

    @NotBlank
    public String username;

    @NotBlank
    public String password;

    @NotBlank
    public String role;
}