package br.com.tassisf.bank.controller.in;

import java.math.BigDecimal;

public record TransactionRequest(String accountNumber, BigDecimal amount) {
}
