package br.com.tassisf.bank.controller.in;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public record AccountRequest(@JsonProperty("customer_name") String customerName,
                             @JsonProperty("customer_id") UUID customerId) {
}
