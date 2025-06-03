package br.com.tassisf.bank.controller.out;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public record AuthResponse(String token, String name, @JsonProperty("user_id") UUID customerId) {
}
