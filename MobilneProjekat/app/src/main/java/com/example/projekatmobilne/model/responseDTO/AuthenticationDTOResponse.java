package com.example.projekatmobilne.model.responseDTO;

public class AuthenticationDTOResponse {
    private String token;

    public AuthenticationDTOResponse(String token) {

        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
