package br.com.breshop.dto.jwt;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AuthResponseDTO {
    @JsonProperty("message")
    private String message;

    @JsonProperty("token")
    private String token;

    public AuthResponseDTO(String message) {
        this.message = message;
    }

    public AuthResponseDTO(String message, String token) {
        this.message = message;
        this.token = token;
    }

    public String getMessage() {
        return message;
    }

    public String getToken() {
        return token;
    }
}
