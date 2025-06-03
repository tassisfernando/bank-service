package br.com.tassisf.bank.controller.out;

import br.com.tassisf.bank.domain.enums.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record StatementResponse(LocalDateTime dateTime, BigDecimal amount, TransactionType type) {
}
