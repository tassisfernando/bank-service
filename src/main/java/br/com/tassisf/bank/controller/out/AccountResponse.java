package br.com.tassisf.bank.controller.out;

import java.math.BigDecimal;
import java.util.UUID;

public record AccountResponse(String id, String accountNumber, BigDecimal balance, UUID customerId) {
}
