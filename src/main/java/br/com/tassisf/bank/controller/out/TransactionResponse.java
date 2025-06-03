package br.com.tassisf.bank.controller.out;

import java.math.BigDecimal;
import java.util.UUID;

public record TransactionResponse(UUID transactionId, String type, BigDecimal newBalance, String message) {
}
