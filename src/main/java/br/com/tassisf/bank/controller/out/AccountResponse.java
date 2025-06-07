package br.com.tassisf.bank.controller.out;

import java.math.BigDecimal;

public record AccountResponse(String id, String accountNumber, BigDecimal balance, BigDecimal creditLimit) {
}
