package br.com.tassisf.bank.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TransactionType {
    CREDIT("Crédito"),
    DEBIT("Débito"),
    DEPENDS("Depende da operação");

    private final String description;
}
