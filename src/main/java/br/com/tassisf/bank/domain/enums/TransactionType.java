package br.com.tassisf.bank.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TransactionType {
    CREDIT("Depósito"),
    DEBIT("Saque");

    private final String description;
}
