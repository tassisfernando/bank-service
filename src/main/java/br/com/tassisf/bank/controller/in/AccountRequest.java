package br.com.tassisf.bank.controller.in;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public record AccountRequest(@JsonProperty("account_number") String accountNumber,
                             @JsonProperty("customer_id") UUID customerId) {
}
